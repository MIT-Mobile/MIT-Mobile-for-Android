package edu.mit.mitmobile2;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;

public abstract class CategoryNewModuleActivity extends NewModuleActivity implements OnMITMenuItemListener {

	MITPopupSecondaryTitleBar mSecondaryTitleBar;
	HashMap<String, ScreenInterface> mScreenInterfaces = new HashMap<String, ScreenInterface>();
	HashMap<String, View> mViews = new HashMap<String, View>();
	HashMap<String, String> mTitles = new HashMap<String, String>();
	ScreenInterface mCurrentScreenInterface = null;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mSecondaryTitleBar = new MITPopupSecondaryTitleBar(this);
		mSecondaryTitleBar.setOnPopupMenuItemListener(this);
		initContentView();
		getTitleBar().addSecondaryBar(mSecondaryTitleBar);
	}
	
	
	protected MITPopupSecondaryTitleBar getSecondaryBar() {
		return mSecondaryTitleBar;
	}
	
	protected void addCategory(ScreenInterface screenInterface, String menuId, String title) {
		mSecondaryTitleBar.addPopupMenuItem(new MITMenuItem(menuId, title));
		mScreenInterfaces.put(menuId, screenInterface);
		mTitles.put(menuId, title);
		
		if (mCurrentScreenInterface == null) {
			onOptionItemSelected(menuId);
		}
	}
	
	private void loadScreen(String menuId) {
		View view;
		String title = mTitles.get(menuId);
		ScreenInterface screenInterface = mScreenInterfaces.get(menuId);
		if (mViews.containsKey(menuId)) {
			view = mViews.get(menuId);
		} else {
			view = screenInterface.getView();
			mViews.put(menuId, view);
		}
		
		mSecondaryTitleBar.setTitle(title);
		mCurrentScreenInterface = screenInterface;
		setContentView(view, false);
	}


	@Override
	public void onOptionItemSelected(String optionId) {
		loadScreen(optionId);
		mCurrentScreenInterface.updateView();
		mCurrentScreenInterface.onSelected();
	}
}
