package edu.mit.mitmobile2.news.view;

import java.util.Map;

import android.os.Bundle;
import android.view.MenuItem;
import edu.mit.mitmobile2.CategoryNewModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.news.NewsModule;
import edu.mit.mitmobile2.news.net.NewsDownloader;

public class NewsCategoryListActivity extends CategoryNewModuleActivity{

	private NewsDownloader nd;
	@Override
	protected NewModule getNewModule() {
		return new NewsModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		createView();
	}
	
	void createView() {
		nd = NewsDownloader.getInstance(this);
		for (Map.Entry<String, String> entry : nd.getAllCategories().entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    addCategory(new NewsListSlider(this,key),key,value);
		}
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(NewsDetailsActivity.CATEGORY_ID_KEY)){
			onOptionItemSelected(extras.getString(NewsDetailsActivity.CATEGORY_ID_KEY));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}
}
