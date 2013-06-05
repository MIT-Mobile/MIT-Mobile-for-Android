package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
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
		
		layoutDescription();
		layoutMenu();
		
		layoutInfoList();
		
	}
	
	private void layoutDescription() {
		TextView descriptionTV = (TextView)findViewById(R.id.retailDescriptionTV);
		if (mHall.getDescriptionHtml() != null && !mHall.getDescriptionHtml().isEmpty()) {
			descriptionTV.setText(Html.fromHtml(mHall.getDescriptionHtml()));
		} else {
			descriptionTV.setVisibility(View.GONE);
		}
	}
	
	private void layoutMenu() {
		View row = findViewById(R.id.menuItemRow);
		row.setBackgroundColor(Color.WHITE);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) row.getLayoutParams();
		int margin = getResources().getDimensionPixelSize(R.dimen.standardPadding);
		params.setMargins(margin, 0, margin, margin);
		TextView rowLabel = (TextView) row.findViewById(R.id.diningHallInfoLabel);
		TextView rowValue = (TextView) row.findViewById(R.id.diningHallInfoValue);
		ImageView rowAction = (ImageView) row.findViewById(R.id.diningInfoItemRowActionIcon);
		rowLabel.setText(getString(R.string.dining_menu_info_label));
		boolean tempHasMenu = false;
		if (mHall.getMenuUrl() != null && !mHall.getMenuUrl().isEmpty()) {
			tempHasMenu = true;
			rowValue.setText(mHall.getMenuUrl());
			rowValue.setEllipsize(TruncateAt.END);
			rowValue.setSingleLine(true);
			rowAction.setBackgroundResource(R.drawable.action_external);
		} else if (mHall.getMenuHtml() != null && !mHall.getMenuHtml().isEmpty()) {
			tempHasMenu = false;
			rowAction.setBackgroundResource(R.drawable.tour_notsure_arrow);
		} else {
			row.setVisibility(View.GONE);
		}
		
		final boolean isExternalMenu = tempHasMenu;
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isExternalMenu) {
					CommonActions.viewURL(DiningRetailInfoActivity.this, mHall.getMenuUrl());
				} else {
					// go to description page
					
				}
			}
		});
	}
	
	private void layoutScheduleList() {
		
	}
	
	private void layoutInfoList() {
		ListView infoLV = (ListView)findViewById(R.id.diningHallInfoLV);
		infoLV.setAdapter(new SimpleArrayAdapter<InfoItem>(this, getHallInfo(), R.layout.dining_hall_info_item_row) {
			@Override
			public void updateView(InfoItem item, View view) {
				TextView label = (TextView) view.findViewById(R.id.diningHallInfoLabel);
				TextView value = (TextView) view.findViewById(R.id.diningHallInfoValue);
				ImageView img = (ImageView) view.findViewById(R.id.diningInfoItemRowActionIcon);
				
				label.setText(item.getInfoLabel());
				value.setText(item.getInfoValue());
				
				if (item.getInfoActionId() > 0) {
					img.setVisibility(View.VISIBLE);
					img.setImageResource(item.getInfoActionId());
				} else {
					img.setVisibility(View.GONE);
				}
			}
			
			@Override
			public boolean isEnabled(int position) {
				InfoItem item = this.getItem(position);
				return (item.getInfoActionId() > 0);
			}
			
			@Override
			public boolean areAllItemsEnabled() {
				return true;	// need to be true so dividers show up
			}
		});
		
		infoLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InfoItem item = (InfoItem)parent.getAdapter().getItem(position);
				if (item.getInfoActionId() == R.drawable.action_map) {
					String locationName = mHall.getLocation().mDescription;
					String url = "mitmobile://map/search?" + locationName;
					CommonActions.doAction(DiningRetailInfoActivity.this, url);
				} else if (item.getInfoActionId() == R.drawable.action_external) {
					CommonActions.viewURL(DiningRetailInfoActivity.this, mHall.getHomePageUrl());
				}
			}
		});
		
	}
	
	private List<InfoItem> getHallInfo() {
		ArrayList<InfoItem> items = new ArrayList<InfoItem>();
		if (mHall.getCuisineString() != null && !mHall.getCuisineString().isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_cuisine_info_label), mHall.getCuisineString(), 0));
		}
		if (!mHall.getPaymentOptions().isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_payment_info_label), mHall.getPaymentOptionString(), 0));
		}
		if (mHall.getLocation() != null) {
			items.add(new InfoItem(getString(R.string.dining_location_info_label), mHall.getLocation().mDescription, R.drawable.action_map));
		}
		if (mHall.getHomePageUrl() != null && !mHall.getHomePageUrl().isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_home_page_info_label), mHall.getHomePageUrl(), R.drawable.action_external));
		}
		return items;
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
		return true;
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
