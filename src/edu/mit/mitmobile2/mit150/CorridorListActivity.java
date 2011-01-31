package edu.mit.mitmobile2.mit150;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TitleBar;

public class CorridorListActivity extends ModuleActivity {

	ListView mStoryListView;
	ArrayAdapter<CorridorStory> mStoryListAdapter;
	FullScreenLoader mLoader;
	View mFooterView;
	
	private static final String LOAD_MORE = "Load more...";
	private static final String LOADING = "Loading...";
	private static final String NO_MORE_TO_LOAD = "End of Corridor";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boring_list_layout);
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringListTitleBar);
		titleBar.setTitle("The Corridor");		
	
		mStoryListView = (ListView) findViewById(R.id.boringListLV);
		mLoader = (FullScreenLoader) findViewById(R.id.boringListLoader);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mFooterView = inflater.inflate(R.layout.mit150_corridor_list_footer, null);
		
		CorridorModel.fetchInitialStories(this, mLoadStoriesHandler);
		
		mStoryListView.setOnItemClickListener(mOnItemClickListener);
	}
	
	Handler mLoadStoriesHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == MobileWebApi.SUCCESS) {
				mLoader.setVisibility(View.GONE);
				mStoryListView.setVisibility(View.VISIBLE);
				if(msg.arg2 > 0) {
					setFooterText(LOAD_MORE);
				} else {
					setFooterText(NO_MORE_TO_LOAD);
				}
				mStoryListView.addFooterView(mFooterView);
				
				mStoryListAdapter = new StoryAdapter(CorridorListActivity.this , CorridorModel.sCorridorStories);
				mStoryListView.setAdapter(mStoryListAdapter);
			} else if(msg.arg1 == MobileWebApi.ERROR) {
				mLoader.showError();
			}
		}
	};
	
	AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
			if(view == mFooterView) {
				setFooterText(LOADING);
				CorridorModel.fetchMoreStories(CorridorListActivity.this, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if(msg.arg1 == MobileWebApi.SUCCESS) {
							if(msg.arg2 > 0) {
								setFooterText(LOAD_MORE);
							} else {
								setFooterText(NO_MORE_TO_LOAD);
							}
							
							mStoryListAdapter.notifyDataSetChanged();
						} else {
							setFooterText(LOAD_MORE);
						}
					}
				});
			} else {
				
				// open the story the user click on
				Intent intent = new Intent(CorridorListActivity.this, CorridorStorySliderActivity.class);
				intent.putExtra(CorridorStorySliderActivity.KEY_POSITION, position);
				startActivity(intent);				
			}
		}
	};
	
	private void setFooterText(String footerText) {
		TextView textView = (TextView) mFooterView.findViewById(R.id.corridorListFootText);
		textView.setText(footerText);
	}
	
	private class StoryAdapter extends SimpleArrayAdapter<CorridorStory> {

		public StoryAdapter(Context context, List<CorridorStory> items) {
			super(context, items, R.layout.mit150_corridor_story_row);
		}

		@Override
		public void updateView(CorridorStory item, View view) {
			TextView title = (TextView) view.findViewById(R.id.corridorListTitle);
			title.setText(item.getTitle());
			
			TextView summary = (TextView) view.findViewById(R.id.corridorListSummary);
			summary.setText(item.getPlainText());			
		}
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
	protected void prepareActivityOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}

	
}
