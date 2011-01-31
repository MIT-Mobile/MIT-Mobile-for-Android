package edu.mit.mitmobile2.tour;

import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;

public class MITIntroductionActivity extends ModuleActivity {

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.boring_activity_layout);
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringLayoutTitleBar);
		titleBar.setTitle("Introduction to MIT");
		
		LinearLayout mRoot = (LinearLayout) findViewById(R.id.boringLayoutRoot);
		
		WebView contentView = new WebView(this);
		contentView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		contentView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mRoot.addView(contentView);
		contentView.loadUrl("file:///android_asset/tour/intro_to_mit.html");		
	}
	
	@Override
	protected Module getModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}

}
