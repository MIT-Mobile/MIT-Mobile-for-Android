package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MITSubCategoryTitleBar extends LinearLayout {

	private LinearLayout mContainer;
	private FrameLayout mAnchorView;
	private MITPopupMenu mPopUpMenu;
	
	public MITSubCategoryTitleBar(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MITSubCategoryTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContainer = (LinearLayout) inflater.inflate(R.layout.mit_sub_category_title_bar, this);
		mAnchorView = (FrameLayout) mContainer.findViewById(R.id.subCategoryTitleBarAnchorView);
		
		mPopUpMenu = new MITPopupMenu(mAnchorView);
		
		mAnchorView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopUpMenu.show();
			}
		});
	}
	
	public void setOnMenuItemClicked(OnMITMenuItemSelected listener) {
		mPopUpMenu.setMenuItemSelectedListener(listener);
	}
	
	public void addPopUpMenuItems(MITMenuItem item) {
		mPopUpMenu.addMenuItem(item);
	}
}
