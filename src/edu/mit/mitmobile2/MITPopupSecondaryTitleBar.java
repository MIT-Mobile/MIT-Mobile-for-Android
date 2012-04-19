package edu.mit.mitmobile2;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import edu.mit.mitmobile2.OnMITMenuItemListener;

public class MITPopupSecondaryTitleBar extends MITSecondaryTitleBar {

	private View mAnchorView;
	private MITPopupMenu mPopUpMenu;
	private TextView mTitleView;
	
	public MITPopupSecondaryTitleBar(Context context, AttributeSet attrs)  {
		super(context, attrs);
		
		mTitleView = (TextView) mContainer.findViewById(R.id.secondaryTitleBarPopupTitle);
		mAnchorView = mContainer.findViewById(R.id.secondaryTitleBarAnchorView);
		mAnchorView.setVisibility(View.VISIBLE);
		
		mPopUpMenu = new MITPopupMenu(mAnchorView);
		
		mAnchorView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopUpMenu.show();
			}
		});		
	}
	
	public MITPopupSecondaryTitleBar(Context context) {
		this(context, null);
	}
	
	public void setOnPopupMenuItemListener(OnMITMenuItemListener listener) {
		mPopUpMenu.setMenuItemSelectedListener(listener);
	}
	
	public void addPopupMenuItem(MITMenuItem item) {
		mPopUpMenu.addMenuItem(item);
	}

	public void setTitle(String title) {
		mTitleView.setText(title.toUpperCase());
	}
}
