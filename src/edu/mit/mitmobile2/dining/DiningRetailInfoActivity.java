package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.RetailDiningHall;

public class DiningRetailInfoActivity extends NewModuleActivity {
	
	private static String HOUSE_DINING_HALL_ID_KEY = "hall_id";
	
	private ArrayList<RetailDiningHall> mBookmarkedHalls;
	private RetailDiningHall mHall;
	
	private DiningHallHeaderView mHeaderView;
	
	public static void launch(Context context, DiningHall diningHall) {
		if (diningHall instanceof RetailDiningHall) {
			Intent intent = new Intent(context, DiningRetailInfoActivity.class);
			intent.putExtra(HOUSE_DINING_HALL_ID_KEY, diningHall.getID());
			context.startActivity(intent);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_hall_retail_info);
		mBookmarkedHalls = new ArrayList<RetailDiningHall>(RetailDiningHall.getBookmarks(this));
		
		DiningVenues venues = DiningModel.getDiningVenues();
		if (venues == null) {
			// fail gracefully
			finish();
			return;
		}
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		mHall = venues.getRetailDiningHall(houseID);
		
		long selectedTime = 1367351565000L;
		//selectedTime = System.currentTimeMillis();
		mHeaderView = (DiningHallHeaderView) findViewById(R.id.diningHallHouseInfoHeader);
		mHeaderView.setHall(mHall, selectedTime);
		mHeaderView.setBackgroundColor(Color.TRANSPARENT);
		mHeaderView.setActionClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBookmarkedHalls.contains(mHall)) {
					mBookmarkedHalls.remove(mHall);
					RetailDiningHall.saveBookmarks(DiningRetailInfoActivity.this, mBookmarkedHalls);
					mHeaderView.setActionImageResourceId(R.drawable.dining_bookmark);
				} else {
					mBookmarkedHalls.add(mHall);
					RetailDiningHall.saveBookmarks(DiningRetailInfoActivity.this, mBookmarkedHalls);
					mHeaderView.setActionImageResourceId(R.drawable.dining_bookmark_selected);
				}
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mBookmarkedHalls.contains(mHall)) {
			mHeaderView.setActionImageResourceId(R.drawable.dining_bookmark_selected);
		} else {
			mHeaderView.setActionImageResourceId(R.drawable.dining_bookmark);
		}
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
		return list;
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
