package edu.mit.mitmobile2.tour;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.tour.Tour.Site;
import edu.mit.mitmobile2.tour.Tour.StartLocation;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;

public class TourStartHelpActivity extends ModuleActivity {
	
	public static final String SELECTED_SITE = "selected_site";
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle saveInstance) {
		super.onCreate(saveInstance);
		final Tour tour = TourModel.getTour(this);
		
		setContentView(R.layout.boring_activity_layout);
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringLayoutTitleBar);
		titleBar.setTitle("Suggested Points");
		
		// construct webview
		LinearLayout rootLayout = (LinearLayout) findViewById(R.id.boringLayoutRoot);
		WebView mainContent = new WebView(this);
		rootLayout.addView(mainContent);
		mainContent.getSettings().setJavaScriptEnabled(true);
		mainContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mainContent.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("selectsite://")) {
					String locationId = url.substring("selectsite://".length());
					StartLocation location = tour.getStartLocation(locationId);
					Site site = tour.getSite(location.getSiteGuid());
					
					Intent resultIntent = new Intent();
					resultIntent.putExtra(SELECTED_SITE, site.getTourMapItem(TourSiteStatus.FUTURE));
					setResult(RESULT_OK, resultIntent);
					finish();
					return true;
				}
				return false;
			}
		});
		
		mainContent.setWebChromeClient(new WebChromeClient() {
			  @Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			    Log.d("TourSuggestedLocations", message + " -- From line "
			                         + lineNumber + " of "
			                         + sourceID);
			  }
		});
		
		String html = constructHtml(tour);
		mainContent.loadDataWithBaseURL(null, StyledContentHTML.html(this, html), "text/html", "utf-8", null);
	}

	private String constructHtml(Tour tour) {
		HashMap<String, String> webViewData = new HashMap<String, String>();
		webViewData.put("INTRO", tour.getStartLocationsHeader());
		
		String itemsHtml = "";
		int photoCounter = 0;
		
		for(StartLocation location : tour.getStartLocations()) {
			
			String photoHtml;
			if(location.getPhotoUrl() != null) {
				String altText = location.getLocationId().replace('-', ' ');
				
				boolean leftOrRight = (photoCounter % 2) == 0;
				String imgClass = leftOrRight ? "floatleft" : "floatright";
				imgClass += " padtop locationImg";
				
				photoHtml = "<img src=\"" + location.getPhotoUrl() + "\" " +
					"id=\"" + location.getLocationId() + "\" " +
					"alt=\"" + altText + "\" " +
					"width=\"160\" height=\"100\" " +
					"class=\"" + imgClass + "\" />";
				
				photoCounter++;
			} else {
				photoHtml = "";
			}
				
			itemsHtml += "" +
				"<li><a href=\"selectsite://" + location.getLocationId() + "\" >" + photoHtml + 
					"<strong>" + location.getTitle() + ":</strong> " +
					location.getContent() +
				"</a></li>";
		}
		webViewData.put("ITEMS", itemsHtml);
		
		return StyledContentHTML.populateTemplate(this, "tour/suggested_stops.html", webViewData);
	}
	
	@Override
	protected Module getModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	static final int MENU_SHOW_TOUR_MAP = MENU_MODULE_HOME + 1;
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.addSubMenu(0, MENU_SHOW_TOUR_MAP, Menu.NONE, "Tour Map")
			.setIcon(R.drawable.menu_maps);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SHOW_TOUR_MAP:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
