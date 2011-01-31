package edu.mit.mitmobile2.maps;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MITMapView extends MapView  {
	
	boolean touched = false;
	boolean mPinchZoom = false;
	int mLastZoomDrawn = -1;;

	private static final int ZOOM_LEVEL_CORRECTION = -1;
	static final int IMAGE_TILE_SIZE = 256;
	
	private static final int MAX_ZOOM_LEVEL = 19;
	private static final int MIN_ZOOM_LEVEL = 13;
	
	private static final int WEST_LONGITUDE_E6  = -71132032;
	private static final int EAST_LONGITUDE_E6  = -71004543;
	private static final int NORTH_LATITUDE_E6  =  42385049;
	private static final int SOUTH_LATITUDE_E6  =  42339688;
	
	boolean tapped_overlay = false;
	
	int mTopY = -1;
	int mLeftX = -1;
	
	// Memory related
	ActivityManager.MemoryInfo mInfo;
	ActivityManager am;
	long lastMem = 0;
	
	SimpleOnGestureListener mTapDetector;
	
	static double kRadiusOfEarthInMeters	=      6378100.0d;
	
	int mNumRows = 3;
	int mNumColumns = 3;
	
	int orientation ;
	
	Context ctx;

	
	class LatLon {
		double lat;
		double lon;
	}
	
	
	MapTilesManager mtm;

	public boolean lowMemory = false;  // TODO drop? tried to catch low memory event from MapActivity
	
	class TileOverlay extends Overlay {
		private int mTileX;
		private int mTileY;
		
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
			super.draw(canvas, mapView, shadow);
			
			boolean drawCompleted;
			
			if (!shadow) {

            	//Log.d("MITMapView","draw");
            	
            	// we subtract 1 from the zoom level because
            	// google map tiles are 128 pixels but the mit
            	// map tiles are 256 pixels
            	int zLevel = mapView.getZoomLevel() + ZOOM_LEVEL_CORRECTION;
            	
              	if (( zLevel > MAX_ZOOM_LEVEL ) || (zLevel < MIN_ZOOM_LEVEL)) {
              		return;
            	} 
            	
              	int tileSize = computeTileSize(mapView, zLevel);
              	
            	if(mPinchZoom) {            		
            		int longitudeE6 = computeLongitudeE6(mTileX, zLevel);
            		int latitudeE6 = computeLatitudeE6(mTileY, zLevel);
            		GeoPoint geoPoint = new GeoPoint(latitudeE6, longitudeE6);
            		Point offsetPoint = mapView.getProjection().toPixels(geoPoint, null);
            		drawCompleted = drawTiles(canvas, mTileX, mTileY, offsetPoint.x, offsetPoint.y, zLevel, tileSize, false);
            		
            		
            	} else {
            		// it is more important to be precise when at integer zoom level
            		// than when zooming in and out, so if its close to the actual
            		// tile size assume we are at integer zoom
            		if((tileSize >= IMAGE_TILE_SIZE - 20) && (tileSize <= IMAGE_TILE_SIZE + 20)) {
            			tileSize = IMAGE_TILE_SIZE;
            		}
            		
            		GeoPoint topLeftGeoPoint = mapView.getProjection().fromPixels(0, 0);
            		
            		double googleX = computeGoogleX(topLeftGeoPoint.getLongitudeE6(), zLevel);
            		double googleY = computeGoogleY(topLeftGeoPoint.getLatitudeE6(), zLevel);
            		
            		mTileX = (int) Math.floor(googleX);
            		int offsetX = -(int) Math.round((googleX - mTileX) * tileSize);
            		
            		mTileY = (int) Math.floor(googleY);
            		int offsetY = -(int) Math.round((googleY - mTileY) * tileSize);            			
            		
            		drawCompleted = drawTiles(canvas, mTileX, mTileY, offsetX, offsetY, zLevel, tileSize, true);
            		
            	}
            	
  
            	if(drawCompleted && tileSize != IMAGE_TILE_SIZE) {
            		if(!mPinchZoom && drawCompleted && tileSize < 10) {
            			mapView.getController().zoomIn();
            			mapView.getController().zoomOut();
            			Log.d("MapView", "resetting tile size");
            		}
            	}
			}
            
		}	

		/************************************************/
		
		
		boolean drawTiles(Canvas canvas,int tileX, int tileY, int offsetX, int offsetY, int zoomLevel, int tileSize, boolean fillScreen) {	

	        Rect src = new Rect();
			Rect dest = new Rect();
			
			int tileRow, tileCol;
			Bitmap bm;

			
			int numRows;
			int numColumns;
			if(fillScreen && tileSize < IMAGE_TILE_SIZE - 2) {
				// this a crude way to do the calculation
				// but anything else seems to be noticably slow
				numRows = mNumRows+1;
				numColumns = mNumColumns+2;
			} else {
				numRows = mNumRows;
				numColumns = mNumColumns;
			}
			
			// this flag is used to see 
			// it the view was drawn with zero cache misses
			boolean noCacheMisses = true;
			
			for (int row=0; row < numRows + 1; row++) {
				for (int col=0; col< numColumns + 1; col++) {

					tileRow = row + tileY;
					tileCol = col + tileX;
					
					if(!isTileOnMap(tileCol, tileRow, zoomLevel)) {
						continue;
					}
					
					if(mtm.TileOutOfBounds(tileCol, numRows, zoomLevel)) {
						continue;
					}
					
					int tileOriginX = col * tileSize + offsetX;
					int tileOriginY = row * tileSize + offsetY;

					bm = mtm.getBitmap(tileCol, tileRow, zoomLevel, true);
					
					if (bm!=null) {
	                	
						src.bottom = IMAGE_TILE_SIZE; 
						src.left   = 0; 
						src.right  = IMAGE_TILE_SIZE; 
						src.top    = 0; 
						
						dest.bottom = tileOriginY + tileSize; 
						dest.left   = tileOriginX; 
						dest.right  = tileOriginX + tileSize; 
						dest.top    = tileOriginY; 

						canvas.drawBitmap (bm,  src,  dest, null);
						
					} else {
						
						noCacheMisses = false;
						
						if(mLastZoomDrawn > -1 && zoomLevel > mLastZoomDrawn) {
							int factor = integerPowerOfTwo(zoomLevel - mLastZoomDrawn);
							int lastX = tileCol / factor;
							int lastY = tileRow / factor;
							bm = mtm.getBitmap(lastX, lastY, mLastZoomDrawn, false);
							
							if(bm != null) {
								// now need to calculate which portion of
								// the low res tile to crop
								src.left = (tileCol % factor) * IMAGE_TILE_SIZE / factor;
								src.right = src.left + IMAGE_TILE_SIZE / factor;
								src.top = (tileRow % factor) * IMAGE_TILE_SIZE / factor;
								src.bottom = src.top + IMAGE_TILE_SIZE / factor;
								
								
								dest.bottom = tileOriginY + tileSize; 
								dest.left   = tileOriginX; 
								dest.right  = tileOriginX + tileSize; 
								dest.top    = tileOriginY; 

								canvas.drawBitmap (bm,  src,  dest, null);	
								Log.d("MITMapView", "pixelating tile col: " + lastX + "row: " + lastY + "zoom-level: " + zoomLevel);
							}
						}
													
						boolean block = (tileSize != IMAGE_TILE_SIZE) && (bm == null);
							
						bm = mtm.fetchBitmapOnThread(tileCol, tileRow, zoomLevel, block);
							
						if(bm != null) {
							src.bottom = IMAGE_TILE_SIZE; 
							src.left   = 0; 
							src.right  = IMAGE_TILE_SIZE; 
							src.top    = 0; 
							
							dest.bottom = tileOriginY + tileSize; 
							dest.left   = tileOriginX; 
							dest.right  = tileOriginX + tileSize; 
							dest.top    = tileOriginY; 

							canvas.drawBitmap (bm,  src,  dest, null);
						}
					}
				}
				
				if(noCacheMisses) {
					mLastZoomDrawn = zoomLevel;
				}
			}

			lowMemory = false;
			
			return noCacheMisses;
			
		}  // drawTile()
		
	
	}
	
	/****************************************************/
	
	public MITMapView(Context context, String key) {
		super(context, key);
		ctx = context;
		setup();
	}
	
	public MITMapView(Context context,AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		setup();
	}
	
	public MITMapView(Context context, AttributeSet attrs, int defStyle) {
		super( context,  attrs,  defStyle);
		ctx = context;
		setup();
	}

	//////////////////////////////////////////////
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
            	touched = true;
            	//postInvalidate();
            	invalidate();
            }
            case MotionEvent.ACTION_UP:
            {	
            	touched = false;  // draw!
            }
            case MotionEvent.ACTION_MOVE:
            {	
            	int count = ev.getPointerCount();
            	if (count>1) {
            		mPinchZoom = true;
            		touched = true;
            	}
            	else {
            		mPinchZoom = false;
            		touched = false;
            	}
            }
        }

		
        try {
        	boolean consumedTouch = super.onTouchEvent(ev);
        	// FIXME something is always consuming event even when overlay not tapped
        	/*
        	if (!consumedTouch) {
        		if (mTapDetector.onSingleTapUp(ev)) removeAllViews();
        	}
        	*/
        	if (tapped_overlay) {
        		tapped_overlay = false;  // ignore and reset flag
        	} else {
        		if (mTapDetector.onSingleTapUp(ev)) {
        			removeAllViews();  // remove bubble bcos non-overlay was tapped
        		}	
        	}
        	return consumedTouch;
        } catch (OutOfMemoryError memoryError) {
        	Log.d("MapView", "Memory error in onTouch handler");
        	memoryError.printStackTrace();
        	System.gc();
        	return false;
        }
	}

	//////////////////////////////////////////////
	
	void setup() {

		mTapDetector = new SimpleOnGestureListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// TODO Auto-generated method stub
				return super.onSingleTapConfirmed(e);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
				//return super.onSingleTapUp(e);
			}
			
		};
		
		mtm = new MapTilesManager(this,ctx);
		
		mtm.initFirstTime();
		
		// Initialize...
		List<Overlay>  ovrlys = getOverlays();
		
		TileOverlay mTileOverlay = new TileOverlay();
		
		if(isFroyo()) {
			// Add overlays...
			ovrlys.add(mTileOverlay);
		}
		
		setRowsCols();		

		// Memory...
		am = (ActivityManager) ctx.getSystemService( Context.ACTIVITY_SERVICE );
		mInfo = new ActivityManager.MemoryInfo ();
		
	}
	
	public void clearItemOverlays() {
		List<Overlay> overlays = getOverlays();
		
		// if froyou dont clear the first overlay (since that overlay is just
		// the overlay that draws the custom map tiles
		int minimumNumberOfOverlays = isFroyo() ? 1 : 0;
		while(overlays.size() > minimumNumberOfOverlays) {
			overlays.remove(overlays.size()-1);
		}		
	}
	
	
	int mDisplayWidth;
	
	void setRowsCols() {

		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		int w = display.getWidth();
		int h = display.getHeight();
		
		mNumRows = h / IMAGE_TILE_SIZE;
		mNumColumns = w / IMAGE_TILE_SIZE;
		
		if(h % IMAGE_TILE_SIZE != 0) {
			mNumRows++;
		}
		
		if(w % IMAGE_TILE_SIZE != 0) {
			mNumColumns++;
		}
		
		
	    
	}
	
	
	private int computeTileSize(MapView mapView, int zoomLevel) {
		Projection projection = mapView.getProjection();
		
		// use the top corner and the tile one next to it
		// to calculate tile size, we use X because in the mercator projection
		// X is linear, so its easier to work with than Y
		
		GeoPoint topLeftPoint = projection.fromPixels(0, 0);
		double googleX = computeGoogleX(topLeftPoint.getLongitudeE6(), zoomLevel);
		double nextTileGoogleX = googleX + 1;
		int nextTileLongitudeE6 = computeLongitudeE6(nextTileGoogleX, zoomLevel);
		GeoPoint nextPoint = new GeoPoint(topLeftPoint.getLatitudeE6(), nextTileLongitudeE6);
		Point nextTilePoint = projection.toPixels(nextPoint, null);
		return nextTilePoint.x;
	}
	
	private static int computeLongitudeE6(double googleX, int zoomLevel) {
		double longitude = -180. + (360. * googleX) / Math.pow(2.0, zoomLevel);
		return (int) Math.round(longitude * 1000000.);
	}
	
	private static int computeLatitudeE6(double googleY, int zoomLevel) {
		double mercatorY =  Math.PI * ( 1 - 2 * (googleY / Math.pow(2.0, zoomLevel) ) );
		double phi = Math.atan( Math.sinh(mercatorY));
		
		// convert from radians to  microdegrees
		return (int) Math.round(phi * 180. / Math.PI * 1000000.);
	}
	
	static double computeGoogleX(int longitudeE6, int zoomLevel) {		
		return (180. + ((double)longitudeE6/1000000.))/(360.) * Math.pow(2.0, zoomLevel);
	}
	
	static double computeGoogleY(int latitudeE6, int zoomLevel) {
		// convert to radians
		double phi = (double)latitudeE6/1000000. * Math.PI / 180.;
		
		// calculate mercator coordinate
		double mercatorY = Math.log(Math.tan(phi) + 1./Math.cos(phi));
		
		// rescale to google coordinate
		return (Math.PI - mercatorY) / ( 2. * Math.PI)  * Math.pow(2.0, zoomLevel);
	}
	
	
	/*
	 *  tile boundary cache to minimize the number of times
	 *  we need to do complicated mercator type calculations
	 */
	private int[] mWestX = new int[21];
	private int[] mEastX = new int[21];
	private int[] mNorthY = new int[21];
	private int[] mSouthY = new int[21];	
		
	private boolean isTileOnMap(int tileX, int tileY, int zoomLevel) {
			initZoomLevel(zoomLevel);
			
			if(tileX < mWestX[zoomLevel]) {
				return false;
			}
		
			if(tileX > mEastX[zoomLevel]) {
				return false;
			}
		
			if(tileY < mNorthY[zoomLevel]) {
				return false;
			}
		
			if(tileY > mSouthY[zoomLevel]) {
				return false;
			}
		
			return true;
	}
			
	private void initZoomLevel(int zoomLevel) {
			if(mWestX[zoomLevel] == 0) {
				mWestX[zoomLevel] = (int) Math.floor(computeGoogleX(WEST_LONGITUDE_E6, zoomLevel));
			}
		
			if(mEastX[zoomLevel] == 0) {
				mEastX[zoomLevel] = (int) Math.ceil(computeGoogleX(EAST_LONGITUDE_E6, zoomLevel));
			}
		
			if(mNorthY[zoomLevel] == 0) {
				mNorthY[zoomLevel] = (int) Math.floor(computeGoogleY(NORTH_LATITUDE_E6, zoomLevel));
			}
		
			if(mSouthY[zoomLevel] == 0) {
				mSouthY[zoomLevel] = (int) Math.ceil(computeGoogleY(SOUTH_LATITUDE_E6, zoomLevel));
			}
	}	
	
	private static int integerPowerOfTwo(int exponent) {
		int factor = 1;
		for(int i = 0; i < exponent; i++) {
			factor = factor * 2;
		}
		return factor;
	}
	
	/****************************************************/
	private static boolean isFroyo () {		
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1;	    
	}
	
	public void stop() {
		mtm.stop();
	}
}

