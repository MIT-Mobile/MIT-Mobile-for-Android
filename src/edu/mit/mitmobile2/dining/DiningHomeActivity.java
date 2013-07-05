package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningLink;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;
import edu.mit.mitmobile2.dining.DiningModel.RetailDiningHall;
import edu.mit.mitmobile2.facilities.FacilitiesDB;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationTable;

public class DiningHomeActivity extends NewModuleActivity {
	public static final String SELECTED_TAB = "dining.selected_tab";
	private static int MAP_ACTIVITY_REQUEST_CODE = 1;
	
	FullScreenLoader mLoader;
	DiningVenues mVenues;
	
	TabHost mTabHost;
	private DiningHomeActivity mContext;
	private FacilitiesDB mFacilitiesDB;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.dining_home);
		
		mContext = this;
		
		mLoader = (FullScreenLoader) findViewById(R.id.diningHomeLoader);
		mLoader.showLoading();
		
		mFacilitiesDB = FacilitiesDB.getInstance(this);
		mFacilitiesDB.updateDatabase(this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == MobileWebApi.SUCCESS) {
					DiningModel.fetchDiningData(mContext, false, new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if (msg.arg1 == MobileWebApi.SUCCESS) {
								mLoader.setVisibility(View.GONE);

								mVenues = DiningModel.getDiningVenues(); 
								displayDiningHalls();
								
								List<DiningLink> links = DiningModel.getDiningLinks();
								displayDiningLinks(links);
								
							} else {
								mLoader.showError();
							}
						}						
					});
				} else {
					mLoader.showError();
				}
			}			
		});		
	}
	
	private HashMap<String, String> mBuildingName = new HashMap<String, String>();
	
	private String getBuildingName(String buildingNumber) {
		if (!mBuildingName.containsKey(buildingNumber)) {
			
			Cursor cursor = mFacilitiesDB.getLocationByBuildingNumber(buildingNumber);
			while (cursor.moveToNext()) {
				int columnIndex = cursor.getColumnIndexOrThrow(LocationTable.NAME);
				String name = cursor.getString(columnIndex);
				if (name != null && name.length() > 0) {
					cursor.close();
					mBuildingName.put(buildingNumber, name);
					break;
				}				
			}
			cursor.close();
		}
		
		return mBuildingName.get(buildingNumber);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mVenues != null) {
			// need to refresh retail dining because bookmarks may have changed
			// TODO:: could use SharedPreferenceChangedListener to only update when bookmark has changed
			LinearLayout layout = (LinearLayout) findViewById(R.id.diningHomeRetailContent);
			layout.removeAllViews();
			displayRetailDiningHalls();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MAP_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String tabIndex = data.getStringExtra(SELECTED_TAB);
				mTabHost.setCurrentTabByTag(tabIndex);
			}
		}
	}
	
	private void displayDiningHalls() {
		mTabHost = (TabHost) findViewById(R.id.diningHomeTabHost);
		mTabHost.setup();
		TabConfigurator tabConfigurator = new TabConfigurator(this, mTabHost);
		tabConfigurator.addTab("HOUSE DINING", R.id.diningHomeHouseTab);
		tabConfigurator.addTab("RETAIL", R.id.diningHomeRetailContent);
		tabConfigurator.configureTabs();
		
		populateDiningHallRows(R.id.diningHomeHouseContent, mVenues.getHouses(), "Dining Houses");
		
		displayRetailDiningHalls();		
		
		View view = findViewById(R.id.diningHomeHouseTab);
		TextView messageView = (TextView) view.findViewById(R.id.diningHomeMessage);
		messageView.setText(mVenues.getAnnouncementsPlainText());
		messageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SimpleSingleWebViewActivity.launch(DiningHomeActivity.this, mVenues.getAnnouncementsHtml());
			}
		});
	}
	
	private void displayRetailDiningHalls() {
		addBookmarkedRetailVenuesToLayout(R.id.diningHomeRetailContent, DiningModel.currentTimeMillis());
		Map<String, List<? extends DiningHall>> retailHallsBySection = mVenues.getRetail();
		for (String buildingNumber: DiningModel.getDiningVenues().getRetailBuildingNumbers()) {
			List<? extends DiningHall> halls = retailHallsBySection.get(buildingNumber);
			
			String title;
			if (buildingNumber == "other") {
				title = "Other";
			} else {
				String buildingName = getBuildingName(buildingNumber);
				title = buildingNumber + " - " + buildingName;
			}
			
			populateDiningHallRows(R.id.diningHomeRetailContent, halls, title);
		}		
	}
	
	private void populateDiningHallRows(int layoutID, List<? extends DiningHall> list, String title) {
		LinearLayout layout = (LinearLayout) findViewById(layoutID);
		long currentTime = DiningModel.currentTimeMillis();
		
		SectionHeader header = new SectionHeader(this, title);
		layout.addView(header, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		boolean first = true;
		LayoutInflater inflater = getLayoutInflater();
		for (final DiningHall diningHall : list) {

			// add separator
			if (!first) {
				layout.addView(new DividerView(this, null));
			} else {
				first = false;
			}
			
			View row = viewForDiningHall(inflater, diningHall, currentTime);
			layout.addView(row, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		}		
	}
	
	private View viewForDiningHall(LayoutInflater inflater, final DiningHall diningHall, long currentTime) {
		// add dining row
		View row = inflater.inflate(R.layout.dining_hall_row, null);
		RemoteImageView iconView = (RemoteImageView) row.findViewById(R.id.diningHallRowImage);
		TextView titleView = (TextView) row.findViewById(R.id.diningHallRowTitle);
		TextView subtitleView = (TextView) row.findViewById(R.id.diningHallRowSubtitle);
		TextView statusView = (TextView) row.findViewById(R.id.diningHallRowStatus);
		
		if (diningHall.getIconUrl() != null) {
			iconView.setURL(diningHall.getIconUrl());
		} else {
			iconView.setVisibility(View.GONE);
		}
		titleView.setText(diningHall.getName());
		
		String subtitle = diningHall.getTodaysHoursSummary(currentTime);
		if (subtitle != null && subtitle.length() > 0) {
			subtitleView.setText(subtitle);
		} else {
			subtitleView.setVisibility(View.GONE);
		}
		
		switch (diningHall.getCurrentStatus(currentTime)) {
			case OPEN:
				statusView.setText("Open");
				statusView.setTextColor(getResources().getColor(R.color.dining_open));
				break;
			case CLOSED:
				statusView.setText("Closed");
				statusView.setTextColor(getResources().getColor(R.color.dining_closed));
				break;
		}
		
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (diningHall instanceof HouseDiningHall) {
					DiningScheduleActivity.launch(DiningHomeActivity.this, diningHall);
				} else {
					DiningRetailInfoActivity.launch(DiningHomeActivity.this, diningHall);
				}
			}				
		});
		return row;
	}
	
	private void addBookmarkedRetailVenuesToLayout(int layoutID, long currentTime) {
		LinearLayout layout = (LinearLayout) findViewById(layoutID);
		List<RetailDiningHall> halls = RetailDiningHall.getBookmarks(this);
		if (!halls.isEmpty()) {
			// add favorites sectionheader
			SectionHeader header = new SectionHeader(this, "");
			ImageSpan favIcon = new ImageSpan(this, R.drawable.dining_bookmark_section_header, ImageSpan.ALIGN_BASELINE);
			SpannableString text = new SpannableString("  Favorites");
			text.setSpan(favIcon, 0, 1, SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			header.getTextView().setText(text);
			
			layout.addView(header, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		LayoutInflater inflater = getLayoutInflater();
		boolean first = true;
		for (RetailDiningHall hall : halls) {
			// add separator
			if (!first) {
				layout.addView(new DividerView(this, null));
			} else {
				first = false;
			}
			View row = viewForDiningHall(inflater, hall, currentTime);
			layout.addView(row, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}
	
	private void displayDiningLinks(List<DiningLink> links) {
		LinearLayout resourcesLayout = (LinearLayout) findViewById(R.id.diningHomeResources);
		for (final DiningLink link : links) {
			resourcesLayout.addView(new DividerView(this, null));
			TwoLineActionRow row = new TwoLineActionRow(this);
			row.setTitle(link.getTitle());
			row.setActionIconResource(R.drawable.action_external);
			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.viewURL(DiningHomeActivity.this, link.getUrl());
				}				
			});
			resourcesLayout.addView(row);
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
		list.add(DiningModule.LISTVIEW_ITEM_ID);
		return list;
	}
	
	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals(DiningModule.MAPVIEW_ITEM_ID)) {
			Intent i = new Intent(this, DiningMapActivity.class);
			i.putExtra(SELECTED_TAB, mTabHost.getCurrentTabTag()); 
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(i, MAP_ACTIVITY_REQUEST_CODE);
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return true;
	}

}
