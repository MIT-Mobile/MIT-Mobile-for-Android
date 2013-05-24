package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class DiningHouseInfoActivity extends NewModuleActivity {
	
	private static String HOUSE_DINING_HALL_ID_KEY = "hall_id";
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_hall_house_info);
		
		
		DiningVenues venues = DiningModel.getDiningVenues();
		if (venues == null) {
			// fail gracefully
			finish();
			return;
		}
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		HouseDiningHall house = venues.getHouseDiningHall(houseID);
		
		long selectedTime = 1367351565000L;
		//selectedTime = System.currentTimeMillis();
		DiningHallHeaderView headerView = (DiningHallHeaderView) findViewById(R.id.diningHallHouseInfoHeader);
		headerView.setHall(house, selectedTime);
		headerView.setBackgroundColor(Color.TRANSPARENT);
		
		
		ListView infoLV = (ListView) findViewById(R.id.diningHallHouseInfoTopLV);
		infoLV.setAdapter(new SimpleArrayAdapter<InfoItem>(this, getHouseInfo(house), R.layout.dining_hall_house_info_item_row) {
			@Override
			public void updateView(InfoItem item, View view) {
				TextView label = (TextView) view.findViewById(R.id.diningMealItemInfoLabel);
				TextView value = (TextView) view.findViewById(R.id.diningMealItemInfoValue);
				ImageView img = (ImageView) view.findViewById(R.id.diningInfoItemRowActionIcon);
				
				label.setText(item.getInfoLabel());
				value.setText(item.getInfoValue());
				
				if (item.getInfoActionId() > 0) {
					img.setVisibility(View.VISIBLE);
					img.setImageResource(item.getInfoActionId());
				} else {
					img.setVisibility(View.GONE);
				}
				
				if (item.getInfoLabel() == getString(R.string.dining_location_info_label)) {
//					view.setBackgroundResource(R.drawable.highlight_background);
					final String locationName = item.getInfoValue();
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO link to map
							String url = "mitmobile://map/search?" + locationName;
							CommonActions.doAction(DiningHouseInfoActivity.this, url);
						}
					});
				}
			}
		});
		
		
		
		
		ListView scheduleLV = (ListView) findViewById(R.id.diningHallHouseInfoScheduleLV);
		scheduleLV.setAdapter(new SimpleArrayAdapter<String>(this, house.getPaymentOptions(), R.layout.dining_hall_house_info_item_row) {
			@Override
			public void updateView(String item, View view) {
				TextView tv = (TextView) view.findViewById(R.id.diningMealItemInfoValue);
				tv.setText(item);
			}
		});
		
		
		
	}
	
	private List<InfoItem> getHouseInfo(HouseDiningHall hall) {
		InfoItem locationInfo = new InfoItem(getString(R.string.dining_location_info_label), hall.getLocation().mDescription, R.drawable.action_map);
		InfoItem paymentInfo = new InfoItem(getString(R.string.dining_payment_info_label), hall.getPaymentOptionString(), 0);
		
		ArrayList<InfoItem> list = new ArrayList<InfoItem>();
		list.add(locationInfo);
		list.add(paymentInfo);
		
		return list;
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
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

	
	private static class InfoItem {
		String mInfoLabel;
		String mInfoValue;
		int mInfoActionId;
		
		public InfoItem() { }
		
		public InfoItem(String label, String value, int actionId) {
			mInfoLabel = label;
			mInfoValue = value;
			mInfoActionId = actionId;
			
		}
		
		public String getInfoLabel() {
			return mInfoLabel;
		}

		public void setInfoLabel(String mInfoLabel) {
			this.mInfoLabel = mInfoLabel;
		}

		public String getInfoValue() {
			return mInfoValue;
		}

		public void setInfoValue(String mInfoValue) {
			this.mInfoValue = mInfoValue;
		}

		public int getInfoActionId() {
			return mInfoActionId;
		}

		public void setmInfoActionId(int mInfoActionId) {
			this.mInfoActionId = mInfoActionId;
		}
	}
	
}
