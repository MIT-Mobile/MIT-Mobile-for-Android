package edu.mit.mitmobile2;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import edu.mit.mitmobile2.OnMITMenuItemListener;

public class MITPopupSecondaryTitleBar extends MITSecondaryTitleBar implements OnMITMenuItemListener {

	private View mAnchorView;
	private MITPopupMenu mPopUpMenu;
	private TextView mTitleView;
	private HashMap<String, String> mTitles = new HashMap<String, String>();
	private OnMITMenuItemListener mMenuItemListener;
	
	public MITPopupSecondaryTitleBar(Context context, AttributeSet attrs)  {
		super(context, attrs);
		
		mTitleView = (TextView) mContainer.findViewById(R.id.secondaryTitleBarPopupTitle);
		mAnchorView = mContainer.findViewById(R.id.secondaryTitleBarAnchorView);
		mAnchorView.setVisibility(View.VISIBLE);
		
		mPopUpMenu = new MITPopupMenu(mAnchorView);
		
		mPopUpMenu.setMenuItemSelectedListener(this);
		
		mAnchorView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopUpMenu.show();
			}
		});		
	}
	
	public MITPopupSecondaryTitleBar(Context context) {
		this(context, null);
	}
	
	public void setOnPopupMenuItemListener(OnMITMenuItemListener listener) {
		mMenuItemListener = listener;
	}
	
	public void addPopupMenuItem(MITMenuItem item) {
		mPopUpMenu.addMenuItem(item);
		if (mTitles.size() == 0) {
			setTitle(item.getTitle());
		}
		mTitles.put(item.getId(), item.getTitle());
	}

	public void setTitle(String title) {
		mTitleView.setText(title.toUpperCase());
	}

	@Override
	public void onOptionItemSelected(String optionId) {
		setTitle(mTitles.get(optionId));
		if (mMenuItemListener != null) {
			mMenuItemListener.onOptionItemSelected(optionId);
		}
	}
}
