package edu.mit.mitmobile2.mit150;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.StyledContentHTML;

public class CorridorStorySliderActivity extends SliderActivity {
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		for(CorridorStory story : CorridorModel.sCorridorStories) {
			addScreen(new StorySliderInterface(this, story), null, "Story");
		}
		
		setPosition(getPositionValue());
	}
	
	@Override
	protected Module getModule() {
		return new MIT150Module();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { } // TODO Auto-generated method stub
	
	static final SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM d, yyyy");
	
	private class StorySliderInterface implements SliderInterface {
		Context mContext;
		WebView mWebView;
		LockingScrollView mScrollView;
		CorridorStory mStory;
		
		public StorySliderInterface(Context context, CorridorStory story) {
			mContext = context;
			mStory = story;
		}
		
		@Override
		public LockingScrollView getVerticalScrollView() {
			return mScrollView;
		}

		@Override
		public View getView() {
			mWebView = new WebView(mContext);
			mWebView.setFocusable(false);  // prevent whacky scrolling to focus on a webview
			mWebView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			mScrollView = new LockingScrollView(mContext);
			mScrollView.addView(mWebView);
			
			return mScrollView;
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSelected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateView() {
			HashMap<String, String> content = new HashMap<String, String>();
			content.put("TITLE", mStory.getTitle());
			content.put("AFFILIATION", mStory.getAffiliation());
			content.put("FIRST-NAME", mStory.getFirstname());
			content.put("LAST-NAME", mStory.getLastname());
			content.put("HTML-BODY", mStory.getBody());
			content.put("DATE", sDateFormat.format(mStory.getDatePosted()));
			
			if(mStory.getImage() != null) {
				content.put("IMG-URL", mStory.getImage().getUrl());
				content.put("IMG-WIDTH", Integer.toString(mStory.getImage().getWidth()));
				content.put("IMG-HEIGHT", Integer.toString(mStory.getImage().getHeight()));
				content.put("HAS-IMAGE", "true");
			} else {
				content.put("IMG-URL", "");
				content.put("IMG-WIDTH", "");
				content.put("IMG-HEIGHT", "");
				content.put("HAS-IMAGE", "false");
			}
			
			String html = StyledContentHTML.populateTemplate(CorridorStorySliderActivity.this, "mit150/corridor_template.html", content);
			
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
			
		}
		
	}
}
