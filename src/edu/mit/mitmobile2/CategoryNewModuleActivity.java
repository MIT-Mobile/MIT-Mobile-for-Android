package edu.mit.mitmobile2;

import java.util.HashMap;

import android.content.res.Configuration;
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
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if (mCurrentScreenInterface != null) {
		mCurrentScreenInterface.updateView();
	    }
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (null != mSecondaryTitleBar) {
			mSecondaryTitleBar.notifyScreenRotated();
		}
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

	protected ScreenInterface getCategory(String optionId) {
		return mScreenInterfaces.get(optionId);
	}

	protected ScreenInterface getCurrentCategory() {
		return mCurrentScreenInterface;
	}
	
	@Override
	public void onOptionItemSelected(String optionId) {
		loadScreen(optionId);
		mCurrentScreenInterface.updateView();
		mCurrentScreenInterface.onSelected();
	}
	
}
