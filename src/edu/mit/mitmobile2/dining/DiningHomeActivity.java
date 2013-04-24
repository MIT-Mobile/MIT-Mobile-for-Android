package edu.mit.mitmobile2.dining;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;

public class DiningHomeActivity extends NewModuleActivity {

	FullScreenLoader mLoader;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.dining_home);
		
		mLoader = (FullScreenLoader) findViewById(R.id.diningHomeLoader);
		mLoader.showLoading();
		
		DiningModel.fetchDiningVenus(this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == MobileWebApi.SUCCESS) {
					DiningVenues venues = DiningModel.getDiningVenues(); 
					DiningVenues venues2 = venues;
					DiningVenues venues3 = venues2;
				} else {
					mLoader.showError();
				}
			}
		});
		
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
		return true;
	}

}
