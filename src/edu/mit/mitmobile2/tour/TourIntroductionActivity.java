package edu.mit.mitmobile2.tour;

import java.util.HashMap;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.tour.Tour.TourHeader;

public class TourIntroductionActivity extends ModuleActivity {

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		setContentView(R.layout.boring_activity_layout);
		
		TourHeader header = TourModel.getTour().getHeader();
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringLayoutTitleBar);
		titleBar.setTitle(header.getTitle());
		
		LinearLayout rootView = (LinearLayout) findViewById(R.id.boringLayoutRoot);
		WebView webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("BODY-BEFORE-BUTTON", header.getDescriptionTop());
		content.put("BODY-AFTER-BUTTON", header.getDescriptionBottom());
		
		String html = StyledContentHTML.populateTemplate(this, "tour/intro_template.html", content);
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		rootView.addView(webView);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.equals("select_start")) {
					showMap();
				} else {
					CommonActions.viewURL(TourIntroductionActivity.this, url);
				}
				return true;
			}
		});
	}
	
	@Override
	protected Module getModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	private static final int MENU_SHOW_TOUR_MAP = MENU_MODULE_HOME + 1;
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SHOW_TOUR_MAP, Menu.NONE, "Tour Map")
				.setIcon(R.drawable.menu_maps);		
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SHOW_TOUR_MAP:
				showMap();
				return true;			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showMap() {
		Tour tour = TourModel.getTour();
		TourMapActivity.launch(this, tour.getDefaultTourMapItems(), tour.getPathGeoPoints(), false);
	}
}
