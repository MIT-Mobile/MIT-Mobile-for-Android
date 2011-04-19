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
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.EventCategoryItem;

public class EventsCategoryActivity extends ModuleActivity {

	Context mContext;
	ListView mListView;
	FullScreenLoader mLoadingView;
	TitleBar mTitleBar;
	
	boolean mIsSubcategory = false;
	int mCategoryId = -1;
	
	final static String IS_SUBCATEGORY_KEY = "is_subcategory";
	final static String CATEGORY_ID_KEY = "category_id";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_categories);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey(IS_SUBCATEGORY_KEY)) {
			mIsSubcategory = true;
			mCategoryId = extras.getInt(CATEGORY_ID_KEY);
		}
		
		mListView = (ListView) findViewById(R.id.eventsCategoryLV);	
		mLoadingView = (FullScreenLoader) findViewById(R.id.eventsCategoriesLoading);
		mTitleBar = (TitleBar) findViewById(R.id.eventsCategoryTitleBar);
		mContext = this;
		
		
		if(!mIsSubcategory) {
			EventsModel.fetchCategories(this, handleCategories());
			mTitleBar.setTitle("Categories");
		} else {
			EventCategoryItem categoryItem = EventsModel.getCategory(mCategoryId);
			
			mTitleBar.setTitle(categoryItem.name);
			
			ArrayList<EventCategoryItem> categories = new ArrayList<EventCategoryItem>();
			categories.add(categoryItem);
			categories.addAll(categoryItem.subcats);
			
			// load categories into listView
			loadCategories(categories);
		}
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
		SimpleArrayAdapter<EventCategoryItem> adapter = new EventCategoryArrayAdapter(mContext, categories);
		adapter.setOnItemClickListener(mListView, 
			new SimpleArrayAdapter.OnItemClickListener<EventCategoryItem>() {
				@Override
				public void onItemSelected(EventCategoryItem item) {
					if(item.subcats == null) {
						MITEventsDaysSliderActivity.launchCategory(mContext, item.catid, item.name, "Events");						
					} else {
						// the category has subcategories
						if(mIsSubcategory) {
							MITEventsDaysSliderActivity.launchCategory(mContext, item.catid, item.name, "Events");
						} else {
							// at the root level so launch subcategories
							Intent intent = new Intent(mContext, EventsCategoryActivity.class);
							intent.putExtra(IS_SUBCATEGORY_KEY, true);
							intent.putExtra(CATEGORY_ID_KEY, item.catid);
							mContext.startActivity(intent);
						}
					}
				}
			}
		);
		mListView.setAdapter(adapter);
		
		
		mLoadingView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected Module getModule() {
		return new EventsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	private class EventCategoryArrayAdapter extends SimpleArrayAdapter<EventCategoryItem> {

		public EventCategoryArrayAdapter(Context context, List<EventCategoryItem> items) {
			super(context, items, R.layout.events_category_row);
		}

		@Override
		public void updateView(EventCategoryItem item, View view) {
			TwoLineActionRow actionRow = (TwoLineActionRow) view;
			
			// for subcategories we want the root category
			// to be call "All categoryName"
			if(mIsSubcategory && mCategoryId == item.catid) {
				actionRow.setTitle("All " + item.name);
			} else {
				actionRow.setTitle(item.name);
			}
		}	
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);		
	}
}
