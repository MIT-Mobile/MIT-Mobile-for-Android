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
import edu.mit.mitmobile2.events.EventsModel.EventType;
import edu.mit.mitmobile2.objs.EventCategoryItem;

public class EventsSimpleCategoryActivity extends ModuleActivity {

	private Context mContext;
	private ListView mListView;
	private FullScreenLoader mLoadingView;
	private TitleBar mTitleBar;
	
	private String mSourceId;
	private EventType mEventType;
	private String mSourceName;
	
	final static String SOURCE_ID_KEY = "source_id";	
	
	public static void launch(Context context, EventType type) {
		Intent intent = new Intent(context, EventsSimpleCategoryActivity.class);
		intent.putExtra(SOURCE_ID_KEY, type.getTypeId());
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_categories);
		
		Bundle extras = getIntent().getExtras();
		mSourceId = extras.getString(SOURCE_ID_KEY);
		mEventType = EventsModel.getEventType(mSourceId);
		if(mEventType == null) {
			// graceful exit
			finish();
			return;
		}
		
		mSourceName = mEventType.getShortName();
		
		mListView = (ListView) findViewById(R.id.eventsCategoryLV);	
		mLoadingView = (FullScreenLoader) findViewById(R.id.eventsCategoriesLoading);
		mTitleBar = (TitleBar) findViewById(R.id.eventsCategoryTitleBar);
		mContext = this;
		

		EventsModel.fetchCategories(this, mEventType, handleCategories());
			
		mTitleBar.setTitle(mSourceName);
	}
	
	private Handler handleCategories() {
		return new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					List<EventCategoryItem> categories = EventsModel.getCategories(mEventType);
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
					MITEventsDaysSliderActivity.launchCategory(mContext, item.catid, item.name, mEventType.getTypeId(), null);						
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
			actionRow.setTitle(item.name);
		}	
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);		
	}
}
