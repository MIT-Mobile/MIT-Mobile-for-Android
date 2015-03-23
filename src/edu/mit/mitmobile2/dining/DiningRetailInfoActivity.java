package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.RetailDiningHall;
import edu.mit.mitmobile2.dining.DiningModel.RetailDiningHall.DailyHours;

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
		
		long selectedTime = DiningModel.currentTimeMillis();
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
		layoutScheduleList();
		layoutInfoList();
		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void layoutDescription() {
		WebView descriptionWV = (WebView)findViewById(R.id.retailDescriptionTV);
		if (mHall.getDescriptionHtml() != null && !mHall.getDescriptionHtml().isEmpty()) {
			HashMap<String, String> content = new HashMap<String, String>();
			content.put("BODY", mHall.getDescriptionHtml());
			String html = StyledContentHTML.populateTemplate(this, "dining/announcement.html", content);
			descriptionWV.getSettings().setJavaScriptEnabled(true);
			descriptionWV.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		} else {
			descriptionWV.setVisibility(View.GONE);
		}
	}
	
	private void layoutMenu() {
		View row = findViewById(R.id.menuItemRow);
		row.setBackgroundResource(R.drawable.highlight_background);
		
		int padding = getResources().getDimensionPixelSize(R.dimen.standardPadding);
		row.setPadding(padding, padding, padding, padding);

		TextView rowLabel = (TextView) row.findViewById(R.id.diningHallInfoLabel);
		TextView rowValue = (TextView) row.findViewById(R.id.diningHallInfoValue);
		ImageView rowAction = (ImageView) row.findViewById(R.id.diningInfoItemRowActionIcon);
		rowLabel.setText(getString(R.string.dining_menu_info_label));
		boolean tempHasMenu = false;
		if (mHall.getMenuUrl() != null && !mHall.getMenuUrl().isEmpty()) {
			tempHasMenu = true;
			rowValue.setText(stripUrlScheme(mHall.getMenuUrl()));
			rowValue.setEllipsize(TruncateAt.END);
			rowValue.setSingleLine(true);
			rowAction.setImageResource(R.drawable.action_external);
		} else if (mHall.getMenuHtml() != null && !mHall.getMenuHtml().isEmpty()) {
			tempHasMenu = false;
			rowAction.setImageResource(R.drawable.action_external);
		} else {
			View rowWrapper = findViewById(R.id.menuItemRowWrapper);
			rowWrapper.setVisibility(View.GONE);
		}
		
		final boolean isExternalMenu = tempHasMenu;
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isExternalMenu) {
					// view web page
					CommonActions.viewURL(DiningRetailInfoActivity.this, mHall.getMenuUrl());
				} else {
					// go to description page
					SimpleSingleWebViewActivity.launch(DiningRetailInfoActivity.this, mHall.getMenuHtml());
				}
			}
		});
	}
	
	private void layoutScheduleList() {
		LinearLayout scheduleContainer = (LinearLayout)findViewById(R.id.diningHallInfoScheduleContainer);		
		
		List<ScheduleItem> items = getScheduleInfo();
		if (items.size() == 0) {
			scheduleContainer.setVisibility(View.GONE);
			return;
		}
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);		
		boolean first = true;
		for (ScheduleItem item : items) {
			
			// add separator
			if (!first) {
				scheduleContainer.addView(new DividerView(this, null));
			} else {
				first = false;
			}
			
			View view = inflater.inflate(R.layout.dining_hall_info_item_row, null);
			view.setBackgroundColor(Color.WHITE);
			TextView label = (TextView) view.findViewById(R.id.diningHallInfoLabel);
			TextView value = (TextView) view.findViewById(R.id.diningHallInfoValue);
			ImageView img = (ImageView) view.findViewById(R.id.diningInfoItemRowActionIcon);
			img.setVisibility(View.GONE);
			
			if (item.dayStart.getDayAbbreviation().equals(item.dayEnd.getDayAbbreviation())) {
				label.setText(item.dayStart.getDayAbbreviation());
			} else {
				label.setText(item.dayStart.getDayAbbreviation() + " - " + item.dayEnd.getDayAbbreviation());
			}
			value.setText(item.dayStart.getScheduleSpan());
			scheduleContainer.addView(view);
		}
	}
	
	private void layoutInfoList() {
		LinearLayout infoContainer = (LinearLayout)findViewById(R.id.diningHallInfoContainer);
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		int standardPadding = getResources().getDimensionPixelOffset(R.dimen.standardPadding);
		for (InfoItem item : getHallInfo()) {
			
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			itemLayout.setBackgroundColor(Color.WHITE);
			params.bottomMargin = standardPadding;
			itemLayout.setLayoutParams(params);
			
			View view = inflater.inflate(R.layout.dining_hall_info_item_row, null);
			
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
			
			if (item.getInfoActionId() == R.drawable.action_map || item.getInfoActionId() == R.drawable.action_external) {
				final InfoItem clickItem = item;
				view.setBackgroundResource(R.drawable.highlight_background);
				view.setPadding(standardPadding, standardPadding, standardPadding, standardPadding);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (clickItem.getInfoActionId() == R.drawable.action_map) {
							String locationName = mHall.getLocation().mDescription;
							String url = "mitmobile://map/search?" + locationName;
							CommonActions.doAction(DiningRetailInfoActivity.this, url);
						} else if (clickItem.getInfoActionId() == R.drawable.action_external) {
							CommonActions.viewURL(DiningRetailInfoActivity.this, mHall.getHomePageUrl());
						}
					}
				});
			}
			
			if (item.isSingleLine()) {
				value.setEllipsize(TruncateAt.END);
				value.setSingleLine(true);
			}
			itemLayout.addView(view);
			infoContainer.addView(itemLayout);
		}
	}
	
	private List<ScheduleItem> getScheduleInfo() {
		
		List<DailyHours> schedule = mHall.getDailyHours();
		ArrayList<ScheduleItem> items = new ArrayList<ScheduleItem>();
		ScheduleItem previousItem = null;
		for (DailyHours hours : schedule) {
			if (previousItem == null) {
				// first time through, add schedule item
				ScheduleItem item = new ScheduleItem();
				item.dayStart = hours;
				item.dayEnd = hours;
				items.add(item);
				previousItem = item;
			} else {
				int dayDiff = (7 + hours.getDay().get(Calendar.DAY_OF_WEEK) - previousItem.dayEnd.getDay().get(Calendar.DAY_OF_WEEK)) % 7;
				if (previousItem.dayEnd.getScheduleSpan().equals(hours.getScheduleSpan()) &&
						dayDiff == 1) {
					// schedule span is equal update previous item daySpan and days are adjacent
					items.get(items.size() - 1).dayEnd = hours;
					previousItem = items.get(items.size() - 1);
				} else {
					// schedule item is not equal, or not adjacent, add new item to list and update previous item
					ScheduleItem item = new ScheduleItem();
					item.dayStart = hours;
					item.dayEnd = hours;
					items.add(item);
					previousItem = item;
				}
			}
		}
		return items;
	}
	
	private List<InfoItem> getHallInfo() {
		ArrayList<InfoItem> items = new ArrayList<InfoItem>();
		if (mHall.getCuisineString() != null && !mHall.getCuisineString().isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_cuisine_info_label), mHall.getCuisineString(), 0));
		}
		if (mHall.getPaymentOptions() != null && !mHall.getPaymentOptions().isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_payment_info_label), mHall.getPaymentOptionString(), 0));
		}
		if (mHall.getLocation().mDescription != null && !mHall.getLocation().mDescription.isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_location_info_label), mHall.getLocation().mDescription, R.drawable.action_map));
		}
		if (mHall.getHomePageUrl() != null && !mHall.getHomePageUrl().isEmpty()) {
			items.add(new InfoItem(getString(R.string.dining_home_page_info_label), stripUrlScheme(mHall.getHomePageUrl()), R.drawable.action_external, true));
		}
		return items;
	}
	
	private String stripUrlScheme(String url) {
		if (url.startsWith("http://")) {
			url = url.substring(7);
		} else if (url.startsWith("https://")) {
			url = url.substring(8);
		}
		return url;
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
		list.add(DiningModule.LISTVIEW_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}
	
	private static class ScheduleItem {
		public DailyHours dayStart;
		public DailyHours dayEnd;
	}

}
