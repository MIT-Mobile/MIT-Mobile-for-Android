package edu.mit.mitmobile2.tour;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.tour.Tour.TourHeader;

public class TourIntroductionActivity extends NewModuleActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		TourHeader header = TourModel.getTour(this).getHeader();
		
		WebView webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		
		HashMap<String, String> content = new HashMap<String, String>();
		content.put("BODY-BEFORE-BUTTON", header.getDescriptionTop());
		content.put("BODY-AFTER-BUTTON", header.getDescriptionBottom());
		
		String html = StyledContentHTML.populateTemplate(this, "tour/intro_template.html", content);
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		setContentView(webView, false);
		addSecondaryTitle(header.getTitle());
		
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
	protected NewModule getNewModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
	
	private void showMap() {
		Tour tour = TourModel.getTour();
		TourMapActivity.launch(this, tour.getDefaultTourMapItems(), tour.getPathGeoPoints(), false);
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }
}
