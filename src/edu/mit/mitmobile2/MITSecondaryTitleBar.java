package edu.mit.mitmobile2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

abstract public class MITSecondaryTitleBar extends LinearLayout {

	protected LinearLayout mContainer;
	protected LinearLayout mSecondaryActionItemsLL;
	
	private List<MITMenuItem> mMenuItems;
	
	private OnMITMenuItemListener mMenuListener;
	
	public MITSecondaryTitleBar(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MITSecondaryTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContainer = (LinearLayout) inflater.inflate(R.layout.mit_secondary_title_bar, this);
		mSecondaryActionItemsLL = (LinearLayout) findViewById(R.id.secondaryTitleBarActionItems);
		
		mMenuItems = new ArrayList<MITMenuItem>();
	}
	
	public void setOnMITMenuItemListener(OnMITMenuItemListener listener) {
		mMenuListener = listener;
	}
	
	/**
	 * Used for the primary, secondary list to get the wrapper view.
	 * @param item it must not be null.
	 * @return View appears on the menu.
	 */
	public void addMenuItem(final MITMenuItem item) {
		mMenuItems.add(item);
		View view = item.getView(getContext());
		view.setTag(item.getId());
		
        view.setFocusable(true);
        view.setClickable(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        mSecondaryActionItemsLL.addView(view, layoutParams);
        
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mMenuListener) {
					mMenuListener.onOptionItemSelected(item.getId());
				}
			}
		});		
	}
	
	public void updateMenuItem(MITMenuItem item) {
		ImageView view = (ImageView) mSecondaryActionItemsLL.findViewWithTag(item.getId());
		view.setImageResource(item.getIconResId());
	}
	
	public void removeMenuItem(MITMenuItem item) {
		ImageView view = (ImageView) mSecondaryActionItemsLL.findViewWithTag(item.getId());
		mSecondaryActionItemsLL.removeView(view);
	}
	
	public void addActionView(View view) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		mSecondaryActionItemsLL.addView(view, layoutParams);
	}
}
