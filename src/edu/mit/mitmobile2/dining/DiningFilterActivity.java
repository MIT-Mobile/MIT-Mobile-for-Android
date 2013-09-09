package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.dining.DiningModel.DiningDietaryFlag;

public class DiningFilterActivity extends NewModuleActivity {
	
	public static final String SELECTED_FILTERS = "filter.extra";
	ListView mListView;
	ArrayList<DiningDietaryFlag> mSelectedFlags = new ArrayList<DiningDietaryFlag>();
	List<DiningDietaryFlag> mAllFlags;
	SimpleArrayAdapter<DiningDietaryFlag> mFilterAdapter;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		mSelectedFlags = getIntent().getExtras().getParcelableArrayList(SELECTED_FILTERS);
		
		setContentView(R.layout.boring_list_layout);
		
		findViewById(R.id.boringListTitleBar).setVisibility(View.GONE);
		findViewById(R.id.boringListLoader).setVisibility(View.GONE);
		
		mAllFlags = new ArrayList<DiningDietaryFlag>(DiningDietaryFlag.allFlags());
		Collections.sort(mAllFlags, DiningDietaryFlag.NameDescendingComparator);	// sorted name descending
		
		mFilterAdapter = new SimpleArrayAdapter<DiningDietaryFlag>(this, mAllFlags, R.layout.dining_filter_row) {
			@Override
			public void updateView(DiningDietaryFlag item, View view) {
				ImageView iconView = (ImageView) view.findViewById(R.id.flagIconIV);
				TextView textView = (TextView) view.findViewById(R.id.flagDisplayNameTV);
				ImageView selectedView = (ImageView) view.findViewById(R.id.flagSelectedIV);
				ImageView unSelectedView = (ImageView) view.findViewById(R.id.flagUnselectedIV);
				
				iconView.setBackgroundResource(item.getIconId());
				textView.setText(item.getDisplayName());
				
				if (mSelectedFlags.contains(item)) {
					selectedView.setVisibility(View.VISIBLE);
				} else {
					selectedView.setVisibility(View.INVISIBLE);
				}
			}
		};
		
		mListView = (ListView)findViewById(R.id.boringListLV);
		mListView.setVisibility(View.VISIBLE);
		mListView.setAdapter(mFilterAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				DiningDietaryFlag item = mAllFlags.get(position);
				if (mSelectedFlags.contains(item)) {
					mSelectedFlags.remove(item);
				} else {
					mSelectedFlags.add(item);
				}
				mFilterAdapter.notifyDataSetChanged();
			}
		});
	}
	

	@Override
	protected List<MITMenuItem> getSecondaryMenuItems() {
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("done", "Done"));
		return items;
	}
	
	@Override
	public void onBackPressed() {
		finishWithFiltersResult();
	}
	
	private void finishWithFiltersResult() {
		Intent returnIntent = new Intent();
		returnIntent.putParcelableArrayListExtra(SELECTED_FILTERS, mSelectedFlags);
		setResult(RESULT_OK, returnIntent);        
		finish();
	}
	
	@Override
	protected NewModule getNewModule() {
		return new DiningModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}
	
	@Override
	protected List<String> getMenuItemBlackList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(DiningModule.FILTER_ITEM_ID);
		list.add(DiningModule.LISTVIEW_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}

	@Override
	protected void onOptionSelected(String optionId) { 
		if(optionId.equals("done")) {
			finishWithFiltersResult();
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
