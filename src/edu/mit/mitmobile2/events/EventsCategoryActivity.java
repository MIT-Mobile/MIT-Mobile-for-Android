package edu.mit.mitmobile2.events;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.EventCategoryItem;

public class EventsCategoryActivity extends NewModuleActivity {

	Context mContext;
	ListView mListView;
	FullScreenLoader mLoadingView;
	
	int mCategoryId = -1;
	
	final static String CATEGORY_ID_KEY = "category_id";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_categories);
		
		Bundle extras = getIntent().getExtras();
		mCategoryId = extras.getInt(CATEGORY_ID_KEY);
		
		mListView = (ListView) findViewById(R.id.eventsCategoryLV);	
		mLoadingView = (FullScreenLoader) findViewById(R.id.eventsCategoriesLoading);
		mContext = this;
		
		
		EventCategoryItem categoryItem = EventsModel.getCategory(mCategoryId);
		if(categoryItem == null) {
		    finish();
		    return;
		}
			
		addSecondaryTitle(categoryItem.name);
			
		ArrayList<EventCategoryItem> categories = new ArrayList<EventCategoryItem>();
		categories.add(categoryItem);
		categories.addAll(categoryItem.subcats);
			
		// load categories into listView
		loadCategories(categories);
	}
	
	private Handler handleCategories() {
		return new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					List<EventCategoryItem> categories = EventsModel.getCategories();
					loadCategories(categories);
				} else {
					mLoadingView.showError();
				}
			}
		};
	}
	
	private void loadCategories(List<EventCategoryItem> categories) {
		SimpleArrayAdapter<EventCategoryItem> adapter = new EventCategoryArrayAdapter(mContext, categories, mCategoryId);
		adapter.setOnItemClickListener(mListView, 
			new SimpleArrayAdapter.OnItemClickListener<EventCategoryItem>() {
				@Override
				public void onItemSelected(EventCategoryItem item) {
					
					String title = item.name;
					if (item.subcats != null) {
						title = "All " + title;
					} 
					
					MITEventsDaysSliderActivity.launchCategory(mContext, item.catid, title, "Events", null);						
				}
			}
		);
		mListView.setAdapter(adapter);
		
		
		mLoadingView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected NewModule getNewModule() {
		return new EventsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
