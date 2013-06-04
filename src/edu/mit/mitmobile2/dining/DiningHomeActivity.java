package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningLink;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class DiningHomeActivity extends NewModuleActivity {

	FullScreenLoader mLoader;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.dining_home);
		
		mLoader = (FullScreenLoader) findViewById(R.id.diningHomeLoader);
		mLoader.showLoading();
		
		DiningModel.fetchDiningData(this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == MobileWebApi.SUCCESS) {
					mLoader.setVisibility(View.GONE);

					DiningVenues venues = DiningModel.getDiningVenues(); 
					displayDiningHalls(venues);
					
					List<DiningLink> links = DiningModel.getDiningLinks();
					displayDiningLinks(links);
				} else {
					mLoader.showError();
				}
			}
		});
		
	}
	
	
	private void displayDiningHalls(DiningVenues venues) {
		TabHost tabHost = (TabHost) findViewById(R.id.diningHomeTabHost);
		tabHost.setup();
		TabConfigurator tabConfigurator = new TabConfigurator(this, tabHost);
		tabConfigurator.addTab("HOUSE DINING", R.id.diningHomeHouseTab);
		tabConfigurator.addTab("RETAIL", R.id.diningHomeRetailContent);
		tabConfigurator.configureTabs();
		
		populateDiningHallRows(R.id.diningHomeHouseContent, venues.getHouses(), "Dining Houses");
		populateDiningHallRows(R.id.diningHomeRetailContent, venues.getRetail(), "Retail");
		
		View view = findViewById(R.id.diningHomeHouseTab);
		TextView messageView = (TextView) view.findViewById(R.id.diningHomeMessage);
		messageView.setText(venues.getAnnouncementsPlainText());
	}
	
	private void populateDiningHallRows(int layoutID, List<? extends DiningHall> list, String title) {
		LinearLayout layout = (LinearLayout) findViewById(layoutID);
		SectionHeader header = new SectionHeader(this, title);
		layout.addView(header, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		

		
		long currentTime = System.currentTimeMillis();
		
		boolean first = true;
		LayoutInflater inflater = getLayoutInflater();
		for (final DiningHall diningHall : list) {

			// add separator
			if (!first) {
				layout.addView(new DividerView(this, null));
			} else {
				first = false;
			}
			
			// add dining row
			View row = inflater.inflate(R.layout.dining_hall_row, null);
			RemoteImageView iconView = (RemoteImageView) row.findViewById(R.id.diningHallRowImage);
			TextView titleView = (TextView) row.findViewById(R.id.diningHallRowTitle);
			TextView subtitleView = (TextView) row.findViewById(R.id.diningHallRowSubtitle);
			TextView statusView = (TextView) row.findViewById(R.id.diningHallRowStatus);
			
//			iconView.setURL(diningHall.getIconUrl());  // uncomment this when images are in a better size for display. Causes out of memory issue currently.
			titleView.setText(diningHall.getName());
			subtitleView.setText(diningHall.getTodaysHoursSummary(currentTime));
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
						// TODO: start retail activity
						DiningRetailInfoActivity.launch(DiningHomeActivity.this, diningHall);
					}
				}				
			});
			
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
		return list;
	}
	
	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return true;
	}

}
