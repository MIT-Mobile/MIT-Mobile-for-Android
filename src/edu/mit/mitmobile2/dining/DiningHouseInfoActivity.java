package edu.mit.mitmobile2.dining;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
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
		
		
		ListView scheduleLV = (ListView) findViewById(R.id.diningHallHouseInfoScheduleLV);
		
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

}
