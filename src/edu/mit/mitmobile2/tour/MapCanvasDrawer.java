package edu.mit.mitmobile2.tour;

import java.util.List;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.ImageDiskCache;
import edu.mit.mitmobile2.tour.Tour.GeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;

public class MapCanvasDrawer {

	private Canvas mCanvas;
	private Bitmap mBitmap;
	
	private int mZoom;
	
	private int mLeftCol;
	private int mLeftOffset;
	
	private int mTopRow;
	private int mTopOffset;
	
	public static final int IMAGE_TILE_SIZE = 256;

	static final double DOUBLE_IMAGE_TILE_SIZE = IMAGE_TILE_SIZE;
	static final int MAX_ZOOM = 19;
	
	static final double EARTH_RADIUS_METERS = 6370.0 * 1000.0;
	
	private String SERVER = "http://" + Global.getMobileWebDomain() + "/api/map/tile2/";
	
	private boolean mRequestCanceled = false;
	
	public static double computeGoogleX(int longitudeE6, int zoomLevel) {		
		return (180. + ((double)longitudeE6/1000000.))/(360.) * Math.pow(2.0, zoomLevel);
	}
	
	public static double computeGoogleY(int latitudeE6, int zoomLevel) {
		// convert to radians
		double phi = (double)latitudeE6/1000000. * Math.PI / 180.;
		
		// calculate mercator coordinate
		double mercatorY = Math.log(Math.tan(phi) + 1./Math.cos(phi));
		
		// rescale to google coordinate
		return (Math.PI - mercatorY) / ( 2. * Math.PI)  * Math.pow(2.0, zoomLevel);
	}

	public MapCanvasDrawer(int width, int height, List<GeoPoint> geoPoints, Integer zoom) {
		initCanvasBitmap(width, height);
		
		GeoRect geoRect = new GeoRect(geoPoints);
		
		// note maxLat corresponds to minY likewise minLat corresponds to maxY
		double minY = MapCanvasDrawer.computeGoogleY(geoRect.getMaxLatitudeE6(), 0);
		double maxY = MapCanvasDrawer.computeGoogleY(geoRect.getMinLatitudeE6(), 0);
		double maxX = MapCanvasDrawer.computeGoogleX(geoRect.getMaxLongitudeE6(), 0);
		double minX = MapCanvasDrawer.computeGoogleX(geoRect.getMinLongitudeE6(), 0);
		
		if(zoom != null) {
			mZoom = zoom;
		} else {
			mZoom = Math.min(
				zoomLevel(height, maxY - minY),
				zoomLevel(width, maxX - minX)
			);
			mZoom = Math.min(mZoom, MAX_ZOOM);
		}
		
		double centerY = 0.5 * (maxY + minY) * Math.pow(2, mZoom);
		double centerX = 0.5 * (maxX + minX) * Math.pow(2, mZoom);
		
		initTileCoordinates(centerX, centerY, width, height);
	}
	
	public MapCanvasDrawer(int width, int height, GeoPoint geoPoint, int zoom) {
		initCanvasBitmap(width, height);
		
		mZoom = zoom;
		
		double centerY = MapCanvasDrawer.computeGoogleY(geoPoint.getLatitudeE6(), mZoom);
		double centerX = MapCanvasDrawer.computeGoogleX(geoPoint.getLongitudeE6(), mZoom);
		
		initTileCoordinates(centerX, centerY, width, height);
	}
	
	private void initCanvasBitmap(int width, int height) {
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}
	
	private void initTileCoordinates(double centerX, double centerY, int width, int height) {
		double topY = centerY - ((double) height / 2.) / DOUBLE_IMAGE_TILE_SIZE;
		mTopRow = (int) Math.round(Math.floor(topY));
		mTopOffset = (int) Math.round(DOUBLE_IMAGE_TILE_SIZE * (topY - Math.floor(topY)));
		
		double leftX = centerX - ((double) width / 2.) / DOUBLE_IMAGE_TILE_SIZE;
		mLeftCol = (int) Math.round(Math.floor(leftX));
		mLeftOffset = (int) Math.round(DOUBLE_IMAGE_TILE_SIZE * (leftX - Math.floor(leftX)));
	}
	
	public Point getPoint(GeoPoint geoPoint) {
		double x = MapCanvasDrawer.computeGoogleX(geoPoint.getLongitudeE6(), mZoom);
		double y = MapCanvasDrawer.computeGoogleY(geoPoint.getLatitudeE6(), mZoom);
		
		int pixelX = (int) Math.round((x - mLeftCol) * MapCanvasDrawer.IMAGE_TILE_SIZE) - mLeftOffset;
		int pixelY = (int) Math.round((y - mTopRow) * MapCanvasDrawer.IMAGE_TILE_SIZE) - mTopOffset;
		
		return new Point(pixelX, pixelY);
	}
	
	private static int zoomLevel(int size, double googleDelta) {
		double zoomDouble = Math.log( ((double)size) / DOUBLE_IMAGE_TILE_SIZE / googleDelta) / Math.log(2.);
		return (int) Math.round(Math.floor(zoomDouble));
	}
	
	public void drawMap(final Context context, final MapCanvasDrawerCallback callback) {
		
		final Handler handler = new Handler();
		
		new Thread() {
			@Override
			public void run() {
				ImageDiskCache imageCache = new ImageDiskCache(context);
				int columns = mCanvas.getWidth() / MapCanvasDrawer.IMAGE_TILE_SIZE + 2;
				int rows = mCanvas.getHeight() / MapCanvasDrawer.IMAGE_TILE_SIZE + 2;
				
				// loop thru the rows and columns drawing the map tiles
				for(int column = 0; column < columns; column++) {
					for(int row = 0; row < rows; row++) {
						int tileX = column + mLeftCol;
						int tileY = row + mTopRow;
						
						Log.d("MapCanvasDrawer", SERVER + mZoom + "/" + tileY + "/" + tileX);
						
						byte[] imageBytes = imageCache.getImageBytes(SERVER + mZoom + "/" + tileY + "/" + tileX ); 
						Bitmap bitmap = null;
						if(imageBytes != null) {
							synchronized(MapCanvasDrawer.this) {
								if(!mRequestCanceled) {
									bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
								} else {
									// request canceled just exit
									mCanvas = null;
									Log.d("MapCanvasDrawer", "Canceling map canvas drawing");
									return;
								}
							}
						}
						
						// check for failure to load map tile
						if(bitmap == null) {
							// need to exit early and make sure the error callback gets called
							
							handler.post(new Runnable() {
								@Override
								public void run() {
									callback.onMapDrawingError(MapCanvasDrawer.this);
								}
							});
							return;
						}
						
						mCanvas.drawBitmap(
							bitmap, 
							MapCanvasDrawer.IMAGE_TILE_SIZE * column - mLeftOffset, 
							MapCanvasDrawer.IMAGE_TILE_SIZE * row - mTopOffset,
							null
						);
						bitmap.recycle();
					}
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						callback.onMapDrawingComplete(MapCanvasDrawer.this, mCanvas, mBitmap);
					}
				});
			}
		}.start();
	}
	
	public void cancelRequest(){
		mRequestCanceled = true;
	}
	
	public void drawMarkerCentered(GeoPoint geoPoint, BitmapDrawable bitmap) {
		int width = bitmap.getIntrinsicWidth();
		int height = bitmap.getIntrinsicHeight();
		Point point = getPoint(geoPoint);
		
		mCanvas.drawBitmap(bitmap.getBitmap(), point.x - width/2, point.y - height/2, null);
	}
	
    public interface MapCanvasDrawerCallback {
		public void onMapDrawingComplete(MapCanvasDrawer canvasDrawer, Canvas canvas, Bitmap bitmap);
		
		public void onMapDrawingError(MapCanvasDrawer canvasDrawer);
	}
    
    public float metersToPixels(float meters, GeoPoint geoPoint) {
    	double latitude = ((double) geoPoint.getLatitudeE6()) / 1000000.0;
    	
    	double radians = latitude * Math.PI / 180.;
    	
    	double mercatorDistance = meters / (2. * Math.PI * Math.cos(radians) * EARTH_RADIUS_METERS);
    	
    	return (float) (mercatorDistance * Math.pow(2, mZoom) * DOUBLE_IMAGE_TILE_SIZE);
    }
}
