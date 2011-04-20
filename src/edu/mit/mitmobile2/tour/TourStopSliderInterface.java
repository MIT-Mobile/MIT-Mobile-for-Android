package edu.mit.mitmobile2.tour;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.AudioPlayer;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.LoadingUIHelper;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.OptimizedSliderInterface;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.ResizableImageView;
import edu.mit.mitmobile2.maps.MapCanvasDrawer;
import edu.mit.mitmobile2.tour.Tour.HtmlContentNode;
import edu.mit.mitmobile2.tour.Tour.PhotoInfo;
import edu.mit.mitmobile2.tour.Tour.SideTrip;
import edu.mit.mitmobile2.tour.Tour.Site;
import edu.mit.mitmobile2.tour.Tour.TourItem;
import edu.mit.mitmobile2.tour.Tour.TourItemContentNode;

import com.google.android.maps.GeoPoint;

public class TourStopSliderInterface implements OptimizedSliderInterface, OnClickListener {
	
	private Context mContext;
	private LockingScrollView mView;
	private TourItem mTourItem;
	private Tour mTour;
	private AudioPlayer mAudioPlayer;
	private ImageButton audioButton;
	private RemoteImageView mMainImageView;
	
	public TourStopSliderInterface(Context context, Tour tour, TourItem tourItem, AudioPlayer ap, TourProgressBar progbar) {
		mContext = context;
		mTour = tour;
		mTourItem = tourItem;
		mAudioPlayer = ap;
	}
	
	@Override
	public LockingScrollView getVerticalScrollView() {
		return mView;
	}

	@Override
	public View getView() {
		if(mView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = (LockingScrollView) inflater.inflate(R.layout.tour_stop, null);
			audioButton = (ImageButton) mView.findViewById(R.id.tourVoiceOverButton);
			audioButton.setFocusable(false);
			if(mTourItem.getAudioUrl() != null) {
				audioButton.setOnClickListener(this);
			} else {
				audioButton.setVisibility(View.GONE);
			}
			
			mWebView = (WebView) mView.findViewById(R.id.tourStopWebView);
			mMainImageView = (RemoteImageView) mView.findViewById(R.id.tourStopPhoto);
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
	
	enum MapStatus {
		NotStarted,
		InProgress,
		Succeeded,
		Failed
	}
	
	MapStatus mMapStatus = MapStatus.NotStarted;
	
	MapCanvasDrawer mMapCanvasDrawer = null;
	
	private WebView mWebView;
	
	
	private void updateMap() {
		if( (mMapStatus == MapStatus.NotStarted  || mMapStatus == MapStatus.Failed) && 
			mTourItem.getPath() != null ) {
			
			
			mMapStatus = MapStatus.InProgress;
			
			mView.findViewById(R.id.tourDirectionsMapContainer).setVisibility(View.VISIBLE);
			final ResizableImageView mapImageView = (ResizableImageView) mView.findViewById(R.id.tourDirectionsMapIV);
			
			mapImageView.setOnSizeChangedListener(new ResizableImageView.OnSizeChangedListener() {	
				@Override
				public void onSizeChanged(final int w, final int h, int oldw, int oldh) {
					final List<GeoPoint> geoPoints = mTourItem.getPath().getGeoPoints();
					int zoom = mTourItem.getPath().getZoom();
					
					mapImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.busybox));
					LoadingUIHelper.startLoadingImage(new Handler(), mapImageView);
					
					// add a padding (subtract from image size)
					int padding = AttributesParser.parseDimension("1dip", mContext);
					int width = w - 2 * padding;
					int height = h - 2 * padding;
					
					mMapCanvasDrawer = new MapCanvasDrawer(width, height, geoPoints, zoom);
					
					mMapCanvasDrawer.drawMap(mContext, new MapCanvasDrawer.MapCanvasDrawerCallback() {
						@Override
						public void onMapDrawingComplete(final MapCanvasDrawer canvasDrawer, Canvas canvas, Bitmap bitmap) {
							
							// draw single segment
							drawGeoPointsPath(canvasDrawer, canvas, R.dimen.tourSingleSegmentPathWidth, geoPoints);
							
							// draw complete path
							drawGeoPointsPath(canvasDrawer, canvas, R.dimen.tourPathWidth, mTour.getPathGeoPoints());
							
							// draw stop markers
							BitmapDrawable firstImage = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_starting_arrow);
							BitmapDrawable lastImage = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_ending_arrow);
							
							drawArrow(canvasDrawer, canvas, geoPoints.get(0), geoPoints.get(1), firstImage, true);
							drawArrow(canvasDrawer, canvas, geoPoints.get(geoPoints.size()-1), geoPoints.get(geoPoints.size()-2), lastImage, false);
							
							mapImageView.setImageBitmap(bitmap);
							
							// add the current location dot overlay
							mapImageView.setOverlay(new ResizableImageView.Overlay() {
								@Override
								public void draw(Canvas canvas) {
									if(mLocation != null) {
										GeoPoint location = new GeoPoint((int) (mLocation.getLatitude() * 1000000), (int) (mLocation.getLongitude() * 1000000));
										Point center = canvasDrawer.getPoint(location);
										
										
										
										// draw error circle
										Paint errorCirclePaint = new Paint();
										errorCirclePaint.setARGB(127, 0, 0, 255);
										errorCirclePaint.setAntiAlias(true);
										float errorRadius = canvasDrawer.metersToPixels(mLocation.getAccuracy(), location);
										
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
							
							mMapCanvasDrawer = null;
							mMapStatus = MapStatus.Succeeded;
						}

						@Override
						public void onMapDrawingError(MapCanvasDrawer canvasDrawer) {
							mapImageView.setImageResource(R.drawable.photo_missing);
							mMapStatus = MapStatus.Failed;
						}
					});
				}
			});
			
			if(mapImageView.getHeight() > 0) {
				mapImageView.notifyOnSizeChangedListener();
			}
		}
		
	}

	private void drawGeoPointsPath(MapCanvasDrawer canvasDrawer, Canvas canvas, int widthResourceId, List<? extends GeoPoint> geoPoints) {
		// configure the path width/color
		Paint linePaint = new Paint();
		linePaint.setColor(mContext.getResources().getColor(R.color.tourPathColor));
		float lineWidth = mContext.getResources().getDimension(widthResourceId);
		linePaint.setStrokeWidth(lineWidth);
		linePaint.setStyle(Paint.Style.STROKE);

		Point start = canvasDrawer.getPoint(geoPoints.get(0));
		android.graphics.Path graphicsPath = new android.graphics.Path();
		graphicsPath.moveTo(start.x, start.y);
		
		for(int i=1; i < geoPoints.size(); i++) {
			Point stop = canvasDrawer.getPoint(geoPoints.get(i));
			graphicsPath.lineTo(stop.x, stop.y);
		}
		
		// draw path
		canvas.drawPath(graphicsPath, linePaint);
	}
	
	private void drawArrow(MapCanvasDrawer canvasDrawer, Canvas canvas, GeoPoint firstGeoPoint, GeoPoint secondGeoPoint, BitmapDrawable stopBitmap, boolean outGoing) {
		
		Point firstPoint = canvasDrawer.getPoint(firstGeoPoint);
		Point secondPoint = canvasDrawer.getPoint(secondGeoPoint);
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
	
	public void releaseLargeMemoryChunks() {
		mMainImageView.setURL(null);
		ResizableImageView mapImageView = (ResizableImageView) mView.findViewById(R.id.tourDirectionsMapIV);
		mapImageView.setOverlay(null);
		mapImageView.setImageDrawable(null);
		if(mMapCanvasDrawer != null) {
			mMapCanvasDrawer.cancelRequest();
			mMapCanvasDrawer = null;
		}
		mMapStatus = MapStatus.NotStarted;
	}
	
	public void completelyUpdateView() {
		getView(); //insure view has already been inflated
		updateMap();
		updateImage();
	}
	
	public void refreshImages() {
		mWebView.loadUrl("javascript:refreshImages()");
		mMainImageView.refresh();
		updateMap();
	}
	
	@Override
	public void updateView() {
		TextView titleView = (TextView) mView.findViewById(R.id.tourStopTitle);
		titleView.setText(mTourItem.getTitle());
		
		mWebView.setFocusable(false);
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("sidetrip://")) {
					String sideTripId = url.substring("sidetrip://".length());
					Site site = (Site) mTourItem;
					TourSideTripActivity.launch(mContext, site.getSiteGuid(), sideTripId);	
				} else {
					CommonActions.viewURL(mContext, url);
				}
				
				return true;
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
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
		if(mMapStatus == MapStatus.Succeeded) {
			// map image needs to be refreshed
			ImageView mapImageView = (ImageView) mView.findViewById(R.id.tourDirectionsMapIV);
			mapImageView.invalidate();
		}
	}
}
