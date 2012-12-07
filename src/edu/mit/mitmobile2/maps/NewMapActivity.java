package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.MITTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBarSwitch;
import edu.mit.mitmobile2.TitleBarSwitch.OnToggledListener;

public class NewMapActivity extends NewModuleActivity {

	MITPlainSecondaryTitleBar mSecondaryTitleBar;
	TitleBarSwitch mSwitchView;

	private static String LIST = "List";
	private static String MAP = "Map";
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(new View(this), true);
		
		MITTitleBar titleBar = getTitleBar();
		
		mSecondaryTitleBar = new MITPlainSecondaryTitleBar(this);
		mSwitchView = new TitleBarSwitch(this);
		mSwitchView.setLabels(MAP, LIST);
		mSwitchView.setSelected(MAP);
		mSwitchView.setOnToggledListener(new OnToggledListener() {
			@Override
			public void onToggled(String selected) {
				if (selected.equals(LIST)) {
					showList();
				} else if (selected.equals(MAP)) {
					showMap();
				}
			}
		});
		
		mSecondaryTitleBar.addActionView(mSwitchView);
		//mSecondaryTitleBar.setVisibility(View.GONE);
		showText("Results \"sample\" 15 found");
		titleBar.addSecondaryBar(mSecondaryTitleBar);
	}
	
	
	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("directions", "Directions", R.drawable.menu_directions));
		items.add(new MITMenuItem("layers", "Layers", R.drawable.menu_layers));
		
		for (MITMenuItem item : getNewModule().getPrimaryOptions()) {
			items.add(item);
		}
		return items;
	}
	
	private void showList() {
		// TODO Auto-generated method stub
	}
	
	private void showMap() {
		// TODO Auto-generated method stub
	}
	
	private void showText(String text) {
		mSecondaryTitleBar.setTitle(text);
	}
	
	@Override
	protected NewModule getNewModule() {
		return new NewMapModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return true;
	}

}
