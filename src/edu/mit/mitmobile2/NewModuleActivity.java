package edu.mit.mitmobile2;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import edu.mit.mitmobile2.MITTitleBar.OnMITTitleBarListener;

public abstract class NewModuleActivity extends Activity {

	private MITTitleBar mTitleBar;
	private LinearLayout mMainLayout;
	private View mContentView;
	private LayoutInflater mInflater;
	
	protected Global app;
	
	protected abstract NewModule getNewModule();
	protected abstract boolean isScrollable();
	protected abstract void onOptionSelected(String optionId);
	protected abstract boolean isModuleHomeActivity();
	
	protected MITTitleBar getTitleBar() {
		if (mTitleBar == null) {
			throw new RuntimeException("getTitleBar() can not be called before setContentView()");
		}
		return mTitleBar;
	}
	
	protected void addSecondaryTitle(String title) {
	    MITPlainSecondaryTitleBar secondaryTitleBar = new MITPlainSecondaryTitleBar(this);
	    secondaryTitleBar.setTitle(title);
	    getTitleBar().addSecondaryBar(secondaryTitleBar);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		app = (Global)getApplication();
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	/**
	 * Use it to add TitleBar items by use {@link NewModuleActivity#addPrimaryMenuItem(List)
	 * , NewModuleActivity#addSecondaryMenuItem(List)}}
	 */
	protected void refreshTitleBarOptions() {
		mTitleBar.clearMenuItems();
		List<MITMenuItem> primaryItems = getPrimaryMenuItems();		
		List<String> blackList = getMenuItemBlackList();
		if (primaryItems != null) {
			for (MITMenuItem item : primaryItems) {
				if (!blackList.contains(item.getId())) {
					mTitleBar.addPrimaryItem(item);
				}
			}
		}
		List<MITMenuItem> secondaryItems = getSecondaryMenuItems();
		if (secondaryItems != null) {
			for (MITMenuItem item : secondaryItems) {
				if (!blackList.contains(item.getId())) {
					mTitleBar.addSecondaryItem(item);
				}
			}
		}
		mTitleBar.requestLayout();
	}

	// default implementation for primary, and secondary menu items.
	protected List<MITMenuItem> getPrimaryMenuItems() {
		return getNewModule().getPrimaryOptions();
	}
	
	protected List<MITMenuItem> getSecondaryMenuItems() {
		return getNewModule().getSecondaryOptions();
	}

	protected List<String> getMenuItemBlackList() {
		return Collections.emptyList();
	}
	
	protected void initContentView() {
		super.setContentView(R.layout.new_module_main);
		mMainLayout = (LinearLayout) findViewById(R.id.newModuleMain);
		mTitleBar = (MITTitleBar) findViewById(R.id.mitTitleBar);
		initViews();
		refreshTitleBarOptions();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mTitleBar != null) {
			mTitleBar.notifiyScreenChanged();
		}
	}
	
	@Override
	public void setContentView(int layoutResID) {
		initContentView();
		
		View view = mInflater.inflate(layoutResID, null);
		wrapContentView(view);
		mMainLayout.addView(mContentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	protected void setContentView(View view, boolean fullRefresh) {
		if (fullRefresh || (mMainLayout == null)) {
			initContentView();			
		} 
		if (mContentView != null) {
			mMainLayout.removeViewAt(mMainLayout.getChildCount()-1);
		}
		wrapContentView(view);
		mMainLayout.addView(mContentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	private void wrapContentView(View view) {
		if (isScrollable()) {
			ScrollView scrollView = new ScrollView(this);
			scrollView.addView(view, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mContentView = scrollView;
		} else {
			mContentView = view;
		}
	}
	
	private void initViews() {
		mTitleBar.setOnTitleBarListener(new OnMITTitleBarListener() {
			@Override
			public void onOptionItemSelected(String optionId) {
				if (!getNewModule().onItemSelected(NewModuleActivity.this, optionId)) {
					onOptionSelected(optionId);
				}
			}

			@Override
			public void onHomeSelected() {
				HomeScreenActivity.goHome(NewModuleActivity.this);				
			}

			@Override
			public void onModuleHomeSelected() {
				Intent intent = new Intent(NewModuleActivity.this, getNewModule().getModuleHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mTitleBar.setTextForModuleBtn(getNewModule().getShortName());
		mTitleBar.setModuleButtonEnabled(!isModuleHomeActivity());
	}
	
	public interface OnBackPressedListener {
		public boolean onBackPressed();
	}
	
	OnBackPressedListener mBackPressedListener;
	
	public void setOnBackPressedListener(OnBackPressedListener backPressedListener) {
		mBackPressedListener = backPressedListener;
	}
	
	@Override
	public void onBackPressed() {
		if (mBackPressedListener != null) {
			if (mBackPressedListener.onBackPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	public interface OnPausedListener {
		public void onPaused();
	}
	
	OnPausedListener mPausedListener;
	
	public void setOnPausedListener(OnPausedListener pausedListener) {
		mPausedListener = pausedListener;
	}
	
	@Override
	protected void onPause() {
		if (mPausedListener != null) {
			mPausedListener.onPaused();
		}
		super.onPause();
	}
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        		mTitleBar.showOverflowMenu();
        		return true;
        } 
        return super.onKeyUp(keyCode, event);
    }
	
	public void showFullScreen(View view) {
		FrameLayout fullScreenFrameLayout = (FrameLayout) findViewById(R.id.newModuleMainFullScreenFrameLayout);
		fullScreenFrameLayout.removeAllViews();
		fullScreenFrameLayout.addView(view, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		fullScreenFrameLayout.setVisibility(View.VISIBLE);
	}
	
	public void hideFullScreen() {
		FrameLayout fullScreenFrameLayout = (FrameLayout) findViewById(R.id.newModuleMainFullScreenFrameLayout);
		fullScreenFrameLayout.removeAllViews();
		fullScreenFrameLayout.setVisibility(View.GONE);
	}
}
