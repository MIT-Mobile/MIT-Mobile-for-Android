package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MITTitleBar extends LinearLayout {

	private LinearLayout mContainer;
	private ImageView mHomeBtn;
	private TextView mModuleHomeBtn;
	private ImageView mOverflowBtn;
	private LinearLayout mPrimaryList;
	
	private ArrayList<MITMenuItem> mPrimaryItems;
	private ArrayList<MITMenuItem> mSecondaryItems;
	
	private LayoutInflater mInflater;
	private OnMITTitleBarListener mClickListener;
	private MITPopupMenu mPopoverMenu;
	
	private int mPrimaryItemsShowing = 0;
	private int mPrimaryItemWidth;
	private int mOverflowButtonWidth;
	
	public MITTitleBar(Context context) {
		this(context, null);
	}
	
	public MITTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPrimaryItems = new ArrayList<MITMenuItem>();
		mSecondaryItems = new ArrayList<MITMenuItem>();
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContainer = (LinearLayout) mInflater.inflate(R.layout.mit_title_bar, this);
		mContainer.setOrientation(LinearLayout.VERTICAL);
		mHomeBtn = (ImageView) mContainer.findViewById(R.id.titleHomeBtn);
		mModuleHomeBtn = (TextView) mContainer.findViewById(R.id.titleModuleHomeBtn);
		mPrimaryList = (LinearLayout) mContainer.findViewById(R.id.titlePrimaryList);
		
		mOverflowBtn = (ImageView) mContainer.findViewById(R.id.titleOverflowBtn);
		mOverflowBtn.setVisibility(View.GONE);
		
		mHomeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mClickListener.onHomeSelected();
			}
		});
		mModuleHomeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mClickListener.onModuleHomeSelected();
			}
		});
		
		mPrimaryItemWidth = getContext().getResources().getDimensionPixelSize(R.dimen.titleBarPrimaryItemWidth);
		mPrimaryItemWidth += getContext().getResources().getDimensionPixelSize(R.dimen.titleBarPadding) * 2;
		mOverflowButtonWidth = getContext().getResources().getDimensionPixelSize(R.dimen.titleBarOverflowItemWidth);
		mOverflowButtonWidth += getContext().getResources().getDimensionPixelSize(R.dimen.titleBarPadding) * 2;
		
		initPopupMenu();
	}
	
	public void setTextForModuleBtn(int resId) {
		mModuleHomeBtn.setText(resId);
	}
	
	public void setTextForModuleBtn(String text) {
		mModuleHomeBtn.setText(text);
	}
	
	public void setModuleButtonEnabled(boolean enabled) {
		mModuleHomeBtn.setClickable(enabled);
	}
	
	/**
	 * Add the item appears on the titlebar. {@link CusPopupMenu}}
	 * @param item it must have the background picture
	 */
	public void addPrimaryItem(MITMenuItem item) {
		mPrimaryItems.add(item);
	}
	
	/**
	 * Add the item appears on the {@link CusPopupMenu}}
	 * @param item it should not have the background
	 */
	public void addSecondaryItem(MITMenuItem item) {
		mSecondaryItems.add(item);
	}

	/**
	 * clear menu options
	 */
	public void clearMenuItems() {
		mPrimaryItems.clear();
		mSecondaryItems.clear();
		mPrimaryItemsShowing = 0;
	}
	
	/**
	 * Call it before the {@link MitTitleBar#configureTitleBar()}}}
	 ** @param listenter
	 */
	public void setOnTitleBarListener(OnMITTitleBarListener listener) {
		mClickListener = listener;
	}
	
	private void initPopupMenu() {
		mPopoverMenu = new MITPopupMenu(mOverflowBtn);
		mOverflowBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPopoverMenu.show();
			}
		});
		mPopoverMenu.setMenuItemSelectedListener(new OnMITMenuItemListener() {

			@Override
			public void onOptionItemSelected(String optionId) {
				mClickListener.onOptionItemSelected(optionId);
			}
		});	
	}
	
	public void showOverflowMenu() {
		if (mOverflowBtn.getVisibility() == View.VISIBLE) {
			mPopoverMenu.show();
		}
	}
	
	/**
	 * Shows the items in primary list in the titlebar.
	 */
	private void addPrimaryItemToView(MITMenuItem item) {
        	View view = getMenuItem(item);       	
            view.setFocusable(true);
            view.setClickable(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mPrimaryItemWidth, LayoutParams.FILL_PARENT);
            mPrimaryList.addView(view, mPrimaryList.getChildCount()-1, layoutParams);
    }
    
	private void clearPrimaryItemViews() {
		// remove everything but the overflow button
		while (mPrimaryList.getChildCount() > 1) {
			mPrimaryList.removeViewAt(0);
		}
	}
	
	/**
	 * Used for the primary, secondary list to get the wrapper view.
	 * @param item it must not be null.
	 * @return View appears on the menu.
	 */
	public View getMenuItem(final MITMenuItem item) {
		View view = item.getView(getContext());
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mClickListener) {
					mClickListener.onOptionItemSelected(item.getId());
				}
			}
		});
		return view;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int availableWidth = MeasureSpec.getSize(widthMeasureSpec);
		availableWidth -= mHomeBtn.getMeasuredWidth();
		availableWidth -= mModuleHomeBtn.getMeasuredWidth();		
	    
		if (!mSecondaryItems.isEmpty()) {
			availableWidth -= mOverflowButtonWidth;
		}
		
		if (availableWidth > mPrimaryItems.size() * mPrimaryItemWidth) {
			mPrimaryItemsShowing = mPrimaryItems.size();
			
			clearPrimaryItemViews();
			mOverflowBtn.setVisibility(View.GONE);
			for (MITMenuItem item : mPrimaryItems) {
				addPrimaryItemToView(item);
			}
		} else {
			// if we have no secondary items, then the space needed 
			// for the overflow button has not yet been accounted for
			if (mSecondaryItems.isEmpty()) {
				availableWidth -= mOverflowButtonWidth;
			}
			
			int itemsThatFit = availableWidth / mPrimaryItemWidth;
			mPrimaryItemsShowing = itemsThatFit;
			
			clearPrimaryItemViews();
			
			for (int i = 0; i < mPrimaryItemsShowing; i++) {
				addPrimaryItemToView(mPrimaryItems.get(i));
			}
		}
		
		if (mPrimaryItemsShowing < mPrimaryItems.size() || !mSecondaryItems.isEmpty()) {
			mOverflowBtn.setVisibility(View.VISIBLE);
			mPopoverMenu.clearMenuItems();
			if (mPrimaryItemsShowing < mPrimaryItems.size()) {
				for (int j = mPrimaryItemsShowing; j < mPrimaryItems.size(); j++) {
					mPopoverMenu.addMenuItem(mPrimaryItems.get(j));
				}
			}
			for (MITMenuItem item : mSecondaryItems) {
				mPopoverMenu.addMenuItem(item);
			}
			mPopoverMenu.refreshMenuList();
		} else {
			mOverflowBtn.setVisibility(View.GONE);
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	/*
	 * These add bar methods should only be called once
	 */
	View mSecondaryBar;
	public void addSecondaryBar(View view) {
		mSecondaryBar = view;
		mContainer.addView(view, 1, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	View mSliderBar;
	public void addSliderBar(View view) {
		mSliderBar = view;
		mContainer.addView(view, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void removeSliderBar() {
		if (mSliderBar != null) {
			mContainer.removeView(mSliderBar);
			mSliderBar = null;
		}
	}
	
	public void notifiyScreenChanged() {
		if (null != mPopoverMenu) {
			mPopoverMenu.notifyScreenRotated();
		}
	}
	
	public static interface OnMITTitleBarListener extends OnMITMenuItemListener {
		public void onHomeSelected();
		public void onModuleHomeSelected();
	}
}
