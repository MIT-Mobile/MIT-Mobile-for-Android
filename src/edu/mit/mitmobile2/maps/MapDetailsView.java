package edu.mit.mitmobile2.maps;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import edu.mit.mitmobile2.LoadingUIHelper;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.about.BuildSettings;
import edu.mit.mitmobile2.objs.MapItem;

public class MapDetailsView implements SliderInterface {

	Activity mActivity;
	MapItem mi;
	//String query;

	LockingScrollView topView;
	
	TabHost tabHost;
	TabHost.TabSpec specHere;
	TabHost.TabSpec specPhotos;
	private ImageView mThumbnailView;
	
	/****************************************************/
	
	public MapDetailsView(Context context, MapItem mapItem) {
		this.mi = mapItem;
		mActivity = (Activity) context;
	}
	
	@Override
	public View getView() {
		LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		topView = (LockingScrollView) vi.inflate(R.layout.map_details, null);
		
		return topView;
	}

	/****************************************************/

	@Override
	public void updateView() {
		createView();
		topView.requestLayout();
	}
	
	public void createView() {
		
		tabHost = (TabHost) topView.findViewById(R.id.mapDetailsTH);  
		tabHost.setup();  // NEEDED!!!

		
		TabConfigurator tabConfigurator = new TabConfigurator(mActivity, tabHost);
		tabConfigurator.addTab("What's Here", R.id.mapDetailsHereLL);
		tabConfigurator.addTab("Photo", R.id.mapDetailsPhotosLL);
		tabConfigurator.configureTabs();
		
		WebView wv;
		TextView tv;

		tv = (TextView) tabHost.findViewById(R.id.mapDetailsPhotosTV);		
		if ("".equals(mi.bldgimg)) {
			//Typeface tf = tv.getTypeface();
			//tv.setTypeface(tf, R.style.BodyText);  // no effect - must set manually
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			tv.setTextColor(0xFF505050);
			tv.setText("No Photo Available"); 
		} else {
			wv = (WebView) tabHost.findViewById(R.id.mapDetailsPhotosWV);
			wv.setFocusable(false); // prevent photo tab from being focused on when tab pressed
			
			final ImageView loadingView = (ImageView) topView.findViewById(R.id.mapDetailsImageLoadingView);
			loadingView.setVisibility(View.VISIBLE);
			
			// turn off loading view after picture completes loading
			wv.setPictureListener(new WebView.PictureListener() {
				@Override
				public void onNewPicture(WebView view, Picture picture) {
					LoadingUIHelper.stopLoadingImage(new Handler(), loadingView);
					loadingView.setVisibility(View.GONE);
				}
			});
			
			wv.loadDataWithBaseURL(null, StyledContentHTML.imageHtml(mActivity, mi.bldgimg), "text/html", "utf-8", null);
			if(!mi.viewangle.equals(""))  {
				tv.setText("view from " + mi.viewangle);  
			}
		}
		
		///////////////////////
		
		String bullet = new String(new int[] {0x2022}, 0 ,1);
		String text = "";
		for (String s : mi.contents) {
			text += " "  + bullet + " " + s + "\n";
		}
		
		if ("".equals(text)) text = "No Information Available";
		
		tv = (TextView) tabHost.findViewById(R.id.mapDetailsHereTV);
		tv.setText(text);
		//tv.setText(mi.snippets);
		
		///////////////////////

		tv = (TextView) topView.findViewById(R.id.mapDetailsQueryTV);
		if ("".equals(mi.query)) {
			tv.setVisibility(View.GONE);
		} else {
			text = "\'"+ mi.query + "\' was found in:";
			tv.setText(text);
		} 
		
		///////////////////////

		
		TextView titleView = (TextView) topView.findViewById(R.id.mapDetailsTitleTV);
			
		String buildingName = "";
		
		if (mi.bldgnum.equals("")) {
			if ("".equals(mi.name)) buildingName = mi.displayName;
			else buildingName = mi.name;
		} else {
			buildingName = "Building " + mi.bldgnum;
			if(!mi.bldgnum.equals(mi.name)) {
				if(!(mi.name.equals("") || mi.name.equals(buildingName))) {
					buildingName += " (" + mi.name + ")";
				}
			} 
		}

		//if (!"".equals(mi.query)) buildingName = "\'"+ mi.query + "\' was found in:\n\n"+ buildingName;
		titleView.setText(buildingName);
		
		
		TextView subtitleView = (TextView) topView.findViewById(R.id.mapDetailsSubtitleTV);
		
		if (mi.street.contains("Access Via ")) {
			mi.street = mi.street.replace("Access Via ", "");
		}
		
		subtitleView.setText(mi.street);
		
		mThumbnailView = (ImageView) topView.findViewById(R.id.mapDetailsThumbnailIV);
		mThumbnailView.setScaleType(ScaleType.CENTER);
		mThumbnailView.setImageResource(R.drawable.busybox);
		
		mThumbnailView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MITMapActivity.viewMapItem(mActivity, mi);
			}
		});

		tabHost.setFocusable(false);		
	}

	
	/****************************************************/

	boolean mHasBeenSelected = false;
	@Override
	public void onSelected() {
		if(!mHasBeenSelected) {
			LoadingUIHelper.startLoadingImage(new Handler(), mThumbnailView);
			
			// Note we assume the thumbnail is smaller than a single map tile
			final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.mapThumbnailInner);
			
			final int zoomLevel = 17;
			
			// google map tile coordinates of the actual center long/lat
			double xCenter = MITMapView.computeGoogleX((int)Math.round(mi.long_wgs84*1000000), zoomLevel);
			double yCenter = MITMapView.computeGoogleY((int)Math.round(mi.lat_wgs84*1000000), zoomLevel);
			
			// google map tile coordinates of the top left corner of thumbnail image
			double xLeft = xCenter - (double) (size/2) / (double) MITMapView.IMAGE_TILE_SIZE;
			double yTop = yCenter - (double) (size/2) / (double) MITMapView.IMAGE_TILE_SIZE;
			
			final int tileX = (int) Math.floor(xLeft);
			final int tileY = (int) Math.floor(yTop);
			
			final int topOffset = (int) (MITMapView.IMAGE_TILE_SIZE * (yTop - tileY));
			final int leftOffset = (int) (MITMapView.IMAGE_TILE_SIZE * (xLeft - tileX));
			
			final String baseURL = "http://" + BuildSettings.MOBILE_WEB_DOMAIN + "/api/map/tile2/";
			
			final Handler uiHandler = new Handler();
			
			final Bitmap thumbnailBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
			final Canvas canvas = new Canvas(thumbnailBitmap);
			
			new Thread() {
				boolean result = true;
				
				@Override 
				public void run() {

					result = drawBitmap(0, 0) && result;
					result = drawBitmap(0, 1) && result;
					result = drawBitmap(1, 0) && result;
					result = drawBitmap(1, 1) && result;
					BitmapDrawable mapPin = (BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.map_red_pin);
					Bitmap mapPinBitmap = mapPin.getBitmap();
					
					// center the bottom left point of the map pin
					int left = (size - mapPinBitmap.getWidth())/2;
					int top = (size/2 - mapPinBitmap.getHeight());
					canvas.drawBitmap(mapPinBitmap, left, top, null);
					
			     	uiHandler.post(new Runnable() {
						@Override
						public void run() {
							if(result) {
								mThumbnailView.setImageBitmap(thumbnailBitmap);
							} else {
								mThumbnailView.setImageResource(R.drawable.news_placeholder);
							}
						}
			     	});
				}
				
				private boolean drawBitmap(int X, int Y) {
					final String url = baseURL + zoomLevel + "/" + (tileY+Y) + "/" + (tileX+X);

			    	DefaultHttpClient httpClient = new DefaultHttpClient();
			    	HttpGet request = new HttpGet(url);
			    	HttpResponse response;
					try {
						response = httpClient.execute(request);
				     	if(response.getStatusLine().getStatusCode() == 200) {
				     		final Bitmap imageTile = BitmapFactory.decodeStream(response.getEntity().getContent());
				     		canvas.drawBitmap(imageTile, -leftOffset + X * MITMapView.IMAGE_TILE_SIZE, -topOffset + Y * MITMapView.IMAGE_TILE_SIZE, null);
				     		return true;
				     	}	
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}	
					
					return false;
				}
			}.start();
			
		}
		
		mHasBeenSelected = true;
	}

	@Override
	public LockingScrollView getVerticalScrollView() {
		return topView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}	
}
