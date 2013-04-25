package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.List;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.SpatialReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.AudioPlayer;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.ResizableImageView;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.tour.Tour.Directions;
import edu.mit.mitmobile2.tour.Tour.GeoPoint;
import edu.mit.mitmobile2.tour.Tour.HtmlContentNode;
import edu.mit.mitmobile2.tour.Tour.PhotoInfo;
import edu.mit.mitmobile2.tour.Tour.SideTrip;
import edu.mit.mitmobile2.tour.Tour.Site;
import edu.mit.mitmobile2.tour.Tour.TourItem;
import edu.mit.mitmobile2.tour.Tour.TourItemContentNode;

public class TourStopSliderInterface implements SliderInterface, OnClickListener {
	
	private Context mContext;
	private ScrollView mView;
	private TourItem mTourItem;
	private Tour mTour;
	private AudioPlayer mAudioPlayer;
	private ImageButton audioButton;
	private RemoteImageView mMainImageView;
	private boolean mIsSite;
	
	public TourStopSliderInterface(Context context, Tour tour, TourItem tourItem, AudioPlayer ap, TourProgressBar progbar, boolean isSite) {
		mContext = context;
		mTour = tour;
		mTourItem = tourItem;
		mAudioPlayer = ap;
		mIsSite = isSite;
	}
	
	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}

	@Override
	public View getView() {
		if(mView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = (ScrollView) inflater.inflate(R.layout.tour_stop, null);
			audioButton = (ImageButton) mView.findViewById(R.id.tourVoiceOverButton);
			audioButton.setFocusable(false);
			if(mTourItem.getAudioUrl() != null) {
				audioButton.setOnClickListener(this);
			} else {
				audioButton.setVisibility(View.GONE);
			}
			
			mWebView = (WebView) mView.findViewById(R.id.tourStopWebView);
			mMainImageView = (RemoteImageView) mView.findViewById(R.id.tourStopPhoto);
			
			updateImage();
			initializeMap();
		}
		return mView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelected() {
		refreshImages();
	}
	
	
	private WebView mWebView;
	protected com.esri.core.geometry.Point mCenterPoint;
	private SpatialReference mSpatialReferenceWGS84;
	private SpatialReference mSpatialReferenceWebMerc;
	private ResizableImageView mMapImageView;
	
	
	private void initializeMap() {
		if(mTourItem.getPath() != null) {
			
			mSpatialReferenceWGS84 = SpatialReference.create(WGS84_WKID);
			mSpatialReferenceWebMerc = SpatialReference.create(WEBMERC_WKID);
			
			mView.findViewById(R.id.tourDirectionsMapContainer).setVisibility(View.VISIBLE);
			mMapImageView = (ResizableImageView) mView.findViewById(R.id.tourDirectionsMapIV);
			
			final List<GeoPoint> geoPoints = mTourItem.getPath().getGeoPoints();
			GeoRect geoRect = new GeoRect(geoPoints);
			GeoPoint center = geoRect.getCenter();
			mCenterPoint = (com.esri.core.geometry.Point)GeometryEngine.project(new com.esri.core.geometry.Point(center.getLongitudeE6()/1000000., center.getLatitudeE6()/1000000.), 
					mSpatialReferenceWGS84, mSpatialReferenceWebMerc);
			
			mMapImageView.setOnSizeChangedListener(new ResizableImageView.OnSizeChangedListener() {	
				@Override
				public void onSizeChanged(final int w, final int h, int oldw, int oldh) {
					if (w == 0 || h == 0) {
						// map not ready to be drawn
						return;
					}
					
					
					// add a padding (subtract from image size)
					int padding = AttributesParser.parseDimension("1dip", mContext);
					int width = w - 2 * padding;
					int height = h - 2 * padding;
					
					mMapImageView.setURLs(getMapURLs(width, height));
							
					// add the current location dot overlay
					mMapImageView.setOverlay(new ResizableImageView.Overlay() {
						@Override
						public void draw(Canvas canvas) {
							
							// draw single segment
							drawGeoPointsPath(canvas, R.dimen.tourSingleSegmentPathWidth, geoPoints, w, h);
							
							// draw complete path
							drawGeoPointsPath(canvas, R.dimen.tourPathWidth, mTour.getPathGeoPoints(), w, h);
							
							// draw stop markers
							BitmapDrawable firstImage = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_starting_arrow);
							BitmapDrawable lastImage = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_ending_arrow);
							
							drawArrow(canvas, geoPoints.get(0), geoPoints.get(1), firstImage, true, w, h);
							drawArrow(canvas, geoPoints.get(geoPoints.size()-1), geoPoints.get(geoPoints.size()-2), lastImage, false, w, h);
							
							if(mLocation != null) {
								GeoPoint location = new GeoPoint((int) (mLocation.getLatitude() * 1000000), (int) (mLocation.getLongitude() * 1000000));
								Point center = getPoint(location, w, h);
								
								
								
								// draw error circle
								Paint errorCirclePaint = new Paint();
								errorCirclePaint.setARGB(127, 0, 0, 255);
								errorCirclePaint.setAntiAlias(true);
								float errorRadius = metersToPixels(mLocation.getAccuracy(), location);
								
								// do not show location if not on map or error radius is bigger than map
								if(
									center.x < 0 || 
									center.x > w || 
									center.y < 0 || 
									center.y > h ||
									errorRadius * 2 > Math.max(w, h) ) {
										return;
								}
								
								canvas.drawCircle(center.x, center.y, errorRadius, errorCirclePaint);
								
								// draw bullseye
								Paint centerPaint = new Paint();
								centerPaint.setAntiAlias(true);
								centerPaint.setARGB(255, 0, 0, 255);
								canvas.drawCircle(center.x, center.y, 5, centerPaint);
								
								
							}
						}
					});
							
				}
			});
			
			if(mMapImageView.getHeight() > 0) {
				mMapImageView.notifyOnSizeChangedListener();
			}
		}
		
	}

	private void drawGeoPointsPath(Canvas canvas, int widthResourceId, List<? extends GeoPoint> geoPoints, int width, int height) {
		// configure the path width/color
		Paint linePaint = new Paint();
		linePaint.setColor(mContext.getResources().getColor(R.color.tourPathColor));
		float lineWidth = mContext.getResources().getDimension(widthResourceId);
		linePaint.setStrokeWidth(lineWidth);
		linePaint.setStyle(Paint.Style.STROKE);

		Point start = getPoint(geoPoints.get(0), width, height);
		android.graphics.Path graphicsPath = new android.graphics.Path();
		graphicsPath.moveTo(start.x, start.y);
		
		for(int i=1; i < geoPoints.size(); i++) {
			Point stop = getPoint(geoPoints.get(i), width, height);
			graphicsPath.lineTo(stop.x, stop.y);
		}
		
		// draw path
		canvas.drawPath(graphicsPath, linePaint);
	}
	
	private void drawArrow(Canvas canvas, GeoPoint firstGeoPoint, GeoPoint secondGeoPoint, BitmapDrawable stopBitmap, boolean outGoing, int width, int height) {
		
		Point firstPoint = getPoint(firstGeoPoint, width, height);
		Point secondPoint = getPoint(secondGeoPoint, width, height);
		float deltaX = secondPoint.x - firstPoint.x;
		float deltaY = secondPoint.y - firstPoint.y;
		if(!outGoing) {
			deltaX = -deltaX;
			deltaY = -deltaY;
		}
		
		float sin = (float) (deltaY / Math.sqrt(deltaX*deltaX + deltaY*deltaY));
		float cos = (float) (deltaX / Math.sqrt(deltaX*deltaX + deltaY*deltaY));
		
		Matrix matrix = new Matrix();
		matrix.setSinCos(sin, cos);
		matrix.preTranslate(-stopBitmap.getIntrinsicWidth()/2, -stopBitmap.getIntrinsicHeight()/2); // move the arrow into a position so we can rotate about 0,0
		matrix.postTranslate(firstPoint.x, firstPoint.y); // move arrow to the stop position
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(stopBitmap.getBitmap(), matrix, paint);
	}
	
	private void updateImage() {
		if(mTourItem.getPath() == null) {
			View imageDividerView = mView.findViewById(R.id.tourStopPhotoDivider);
			if(mTourItem.getPhotoInfo() != null) {
				mMainImageView.setVisibility(View.VISIBLE);
				mMainImageView.setURL(mTourItem.getPhotoInfo().getPhotoUrl());
				imageDividerView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void refreshImages() {
		mWebView.loadUrl("javascript:refreshImages()");
		mMainImageView.refresh();
		if (mMapImageView != null) {
			mMapImageView.refresh();
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void updateView() {
		TextView titleView = (TextView) mView.findViewById(R.id.tourStopTitle);
		titleView.setText(mTourItem.getTitle());
		
		mWebView.setFocusable(false);
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLightTouchEnabled(true);
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("sidetrip://")) {
					String sideTripId = url.substring("sidetrip://".length());
					
					Site site = null;
					if(mTourItem.getClass() == Site.class) {
						site = (Site) mTourItem;
					} else if(mTourItem.getClass() == Directions.class) {
						Directions directions = (Directions) mTourItem;
						site = directions.getSource();
					}
				
					TourSideTripActivity.launch(mContext, site.getSiteGuid(), sideTripId, mIsSite);	
				} else {
					CommonActions.viewURL(mContext, url);
				}
				
				return true;
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
	        @Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				 Log.d("TourSiteLocation", "TourItemId= " + mTourItem.getTitle() + " message +  -- From line "
				                         + lineNumber + " of "
				                         + sourceID);
		    }
		});
		
		mWebView.loadDataWithBaseURL(null, webviewHtml(), "text/html", "utf-8", null);
	}

	public String webviewHtml() {
		String bodyHtml = "";		
		
		for(TourItemContentNode node : mTourItem.getContent().getContentNodes()) {
			if(node.getClass() == HtmlContentNode.class) {
				bodyHtml += node.getHtml();
			} else {
				SideTrip sideTrip = (SideTrip) node;
				bodyHtml += TourHtml.sideTripLinkHtmlFragment(sideTrip);
			}
		}
		
		if(mTourItem.getPath() != null && mTourItem.getPhotoInfo() != null) {
			PhotoInfo photoInfo = mTourItem.getPhotoInfo();
			return TourHtml.tourStopHtml(mContext, bodyHtml, photoInfo.getPhotoUrl(), photoInfo.getPhotoLabel());
		} else {
			// no photo so just use the plain template
			return TourHtml.tourStopHtml(mContext, bodyHtml, null, null);
		}
	}

	@Override
	public void onClick(View v) {
		if(mTourItem.getAudioUrl() != null) {
			mAudioPlayer.togglePlay(mTourItem.getAudioUrl(), audioButton); 
		}
	}
	
	Location mLocation;
	public void onLocationChanged(Location location) {
		mLocation = location;
		if (mMapImageView != null) {
			mMapImageView.invalidate();
		}
	}
	
	private static final int WEBMERC_WKID = 102113;
	private static final int WGS84_WKID = 4326;		
	private final float BASE_ZOOM = 17.1f;
	private com.esri.core.geometry.Point mTopLeft;
	private com.esri.core.geometry.Point mBottomRight; 
	
	private List<String> getMapURLs(int width, int height) {
		
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int zoom = mTourItem.getPath().getZoom();
		double widthExtent = width * Math.pow(2, BASE_ZOOM - zoom) / metrics.density;
		double heightExtent = height * Math.pow(2, BASE_ZOOM - zoom) / metrics.density;
		
		double minX = mCenterPoint.getX() - widthExtent/2;
		double minY = mCenterPoint.getY() - heightExtent/2;
		double maxX = mCenterPoint.getX() + widthExtent/2;
		double maxY = mCenterPoint.getY() + heightExtent/2;
		String bbox = minX + "," + minY + "," + maxX + "," + maxY;
		
		mTopLeft = (com.esri.core.geometry.Point)GeometryEngine.project(new com.esri.core.geometry.Point(minX, minY), 
				mSpatialReferenceWebMerc, mSpatialReferenceWGS84);
		
		mBottomRight = (com.esri.core.geometry.Point)GeometryEngine.project(new com.esri.core.geometry.Point(maxX, maxY), 
				mSpatialReferenceWebMerc, mSpatialReferenceWGS84);
		
		ArrayList<String> urls = new ArrayList<String>();
		urls.add("http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/export?format=png24&transparent=false&f=image&bbox=" + bbox + "&size=" + width + "," + height);
		urls.add("http://maps.mit.edu/pub/rest/services/basemap/WhereIs_Base_Topo/MapServer/export?format=png24&transparent=true&f=image&bbox=" + bbox + "&size=" + width + "," + height);
		return urls;
	}
	
	/*
	 * Since ArcGIS calculation are way to slow we do interpolation
	 * based on Top-Left and Bottom-Right points in WGS84
	 */
	private Point getPoint(GeoPoint point, int width, int height) {
		double fractionX = (point.getLongitudeE6()/1000000. - mTopLeft.getX()) / (mBottomRight.getX()-mTopLeft.getX());
		double fractionY = (point.getLatitudeE6()/1000000. - mTopLeft.getY()) / (mBottomRight.getY()-mTopLeft.getY());		
		int x = (int) ((fractionX) * width);
		int y = (int) ((1-fractionY) * height);
		return new Point(x, y);
	}
	
	static final double EARTH_RADIUS_METERS = 6370.0 * 1000.0;
	
	public float metersToPixels(float meters, GeoPoint geoPoint) {		
		int zoom = mTourItem.getPath().getZoom();
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		double meterPerPixel = Math.pow(2, BASE_ZOOM - zoom) / metrics.density;

		return (float) (meterPerPixel * meters);
	}
}
