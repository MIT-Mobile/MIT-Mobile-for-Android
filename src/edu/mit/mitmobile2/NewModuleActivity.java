package edu.mit.mitmobile2;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


public abstract class NewModuleActivity extends ModuleActivity {

	private MitTitleBar mTitleBar;
	
	protected abstract NewModule getNewModule();
	protected abstract boolean isScrollable();
	protected abstract void onOptionSelected(int id);
	
	/**
	 * Use it to add TitleBar items by use {@link NewModuleActivity#addPrimaryMenuItem(List)
	 * , NewModuleActivity#addSecondaryMenuItem(List)}}
	 */
	protected abstract void initTitleBar();
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(R.layout.new_module_main);
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.newModuleMain);
		mTitleBar = (MitTitleBar) findViewById(R.id.mitTitleBar);
		initViews();
		initTitleBar();
		mTitleBar.configureTitleBar();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(layoutResID, null);
		mainLayout.addView(view);
	}
	
	private void initViews() {
		mTitleBar.setOnTitleBarClick(new OnCusMenuItemSelected() {
			@Override
			public void onOptionItemSelected(int optionId) {
				// TODO Auto-generated method stub
				switch (optionId) {
				case MitTitleBar.MENU_HOME:
					MITNewsWidgetActivity.goHome(NewModuleActivity.this);
					break;
				case MitTitleBar.MENU_MODULE_HOME:
					Intent intent = new Intent(NewModuleActivity.this, getModule().getModuleHomeActivity());
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					break;
				default:
					onOptionSelected(optionId);
					break;
				}
			}
		});
		mTitleBar.setTextForModuleBtn(getNewModule().getSecondaryOptions());
	}
	
	protected void addPrimaryMenuItem(List<CusMenuItem> menuItems) {
		if (isItemListNull(menuItems)) {
			return;
		}
		for (CusMenuItem item : menuItems) {
			mTitleBar.addPrimaryItem(item);
		}
	}
	
	protected void addSecondaryMenuItem(List<CusMenuItem> menuItems) {
		if (isItemListNull(menuItems)) {
			return;
		}
		for (CusMenuItem item : menuItems) {
			mTitleBar.addSecondaryItem(item);
		}
	}
	
	private boolean isItemListNull(List<CusMenuItem> menuItems) {
		if (null == menuItems || menuItems.isEmpty()) {
			return true;
		}
		return false;
	}
}
