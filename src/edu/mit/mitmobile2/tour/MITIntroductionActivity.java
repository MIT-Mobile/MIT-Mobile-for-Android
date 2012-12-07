package edu.mit.mitmobile2.tour;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;

public class MITIntroductionActivity extends NewModuleActivity {

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WebView contentView = new WebView(this);
		contentView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		contentView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		contentView.loadUrl("file:///android_asset/tour/intro_to_mit.html");
		
		setContentView(contentView, true);
		addSecondaryTitle("Introduction to MIT");
	}
	
	@Override
	protected NewModule getNewModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

}
