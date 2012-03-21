package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
	
	private int primaryItemsShowing = 0;
	
	public static final int SUB_SLIDER_TITLE_BAR = 1;
	
	/**
	 * Want to make the id can not be used.
	 */
	public static final int MENU_HOME = 1024;
	public static final int MENU_MODULE_HOME = 1025;
	
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
		
		initPopupMenu();
	}
	
	public void setTextForModuleBtn(int resId) {
		mModuleHomeBtn.setText(resId);
	}
	
	public void setTextForModuleBtn(String text) {
		mModuleHomeBtn.setText(text);
	}
	
	public void setClickableForModuleBtn(boolean isClickable) {
		if (!isClickable) {
			mModuleHomeBtn.setBackgroundResource(R.drawable.titlebar_action_module_background);
		}
		mModuleHomeBtn.setClickable(!isClickable);
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
	}
	
	/**
	 * Shows the items in primary list in the titlebar.
	 */
	private void addPrimaryItemToView(MITMenuItem item) {
        	View view = getMenuItem(item);       	
            view.setFocusable(true);
            view.setClickable(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
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
	private View getMenuItem(final MITMenuItem item) {
		ImageView img = (ImageView) mInflater.inflate(R.layout.titlebar_menu_item, null);

		Drawable icon = item.getIcon();
        
		if (item.getIconResId() != 0) {
			img.setImageResource(item.getIconResId());
		} else if (icon != null) {
			img.setImageDrawable(icon);
		}
		
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mClickListener) {
					mClickListener.onOptionItemSelected(item.getId());
				}
			}
		});
		return img;
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int availableWidth = MeasureSpec.getSize(widthMeasureSpec);
		availableWidth -= mHomeBtn.getMeasuredWidth();
		availableWidth -= mModuleHomeBtn.getMeasuredWidth();		


	    int primaryItemWidth = getContext().getResources().getDimensionPixelSize(R.dimen.titleBarPrimaryItemWidth);
	    primaryItemWidth += getContext().getResources().getDimensionPixelSize(R.dimen.titleBarPadding) * 2;
	    int overflowButtonWidth = getContext().getResources().getDimensionPixelSize(R.dimen.titleBarOverflowItemWidth);
	    overflowButtonWidth += getContext().getResources().getDimensionPixelSize(R.dimen.titleBarPadding) * 2;
	    
		if (!mSecondaryItems.isEmpty()) {
			availableWidth -= overflowButtonWidth;
		}
		
		if (availableWidth > mPrimaryItems.size() * primaryItemWidth) {
			if (primaryItemsShowing != mPrimaryItems.size()) {
				requestLayout();
				primaryItemsShowing = mPrimaryItems.size();
			} else {
				return;
			}
		    
			clearPrimaryItemViews();
			mOverflowBtn.setVisibility(View.GONE);
			for (MITMenuItem item : mPrimaryItems) {
				addPrimaryItemToView(item);
			}
		} else {
			// if we have no secondary items, then the space needed 
			// for the overflow button has not yet been accounted for
			if (mSecondaryItems.isEmpty()) {
				availableWidth -= overflowButtonWidth;
			}
			
			int itemsThatFit = availableWidth / primaryItemWidth;
			if (itemsThatFit != primaryItemsShowing) {
				requestLayout();
				primaryItemsShowing = itemsThatFit;
			} else {
				return;
			}
			
			clearPrimaryItemViews();
			
			for (int i = 0; i < primaryItemsShowing; i++) {
				addPrimaryItemToView(mPrimaryItems.get(i));
			}
		}
		
		if (primaryItemsShowing < mPrimaryItems.size() || !mSecondaryItems.isEmpty()) {
			mOverflowBtn.setVisibility(View.VISIBLE);
			mPopoverMenu.clearMenuItems();
			if (primaryItemsShowing < mPrimaryItems.size()) {
				for (int j = primaryItemsShowing; j < mPrimaryItems.size(); j++) {
					mPopoverMenu.addMenuItem(mPrimaryItems.get(j));
				}
				for (MITMenuItem item : mSecondaryItems) {
					mPopoverMenu.addMenuItem(item);
				}
			}
			mPopoverMenu.refreshMenuList();
		} else {
			mOverflowBtn.setVisibility(View.GONE);
		}
	}
	
	public static MITSubSliderTitleBar createSliderTitleBar(Context context) {
		return new MITSubSliderTitleBar(context);
	}
	
	public static MITSubCategoryTitleBar createCategoryTitleBar(Context context) {
		return new MITSubCategoryTitleBar(context);
	}
	
	protected void addSubTitleBar(View view) {
		if (null != view) {
			mContainer.addView(view);
		}
	}
	
	public static interface OnMITTitleBarListener extends OnMITMenuItemSelected {
		public void onHomeSelected();
		public void onModuleHomeSelected();
	}
}
