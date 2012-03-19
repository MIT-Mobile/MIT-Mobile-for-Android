package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MitTitleBar extends RelativeLayout {

	private RelativeLayout mContainer;
	private ImageView mHomeBtn;
	private TextView mModuleHomeBtn;
	private ImageView mOverflowBtn;
	private LinearLayout mPrimaryList;
	
	private ArrayList<CusMenuItem> mPrimaryItems;
	private ArrayList<CusMenuItem> mSecondaryItems;
	
	private LayoutInflater mInflater;
	private OnCusMenuItemSelected mClickListener;
	
	/**
	 * Want to make the id can not be used.
	 */
	public static final int MENU_HOME = 1024;
	public static final int MENU_MODULE_HOME = 1025;
	
	public MitTitleBar(Context context) {
		this(context, null);
	}
	
	public MitTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPrimaryItems = new ArrayList<CusMenuItem>();
		mSecondaryItems = new ArrayList<CusMenuItem>();
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContainer = (RelativeLayout) mInflater.inflate(R.layout.mit_title_bar, this);
		mHomeBtn = (ImageView) mContainer.findViewById(R.id.titleHomeBtn);
		mModuleHomeBtn = (TextView) mContainer.findViewById(R.id.titleModuleHomeBtn);
		mPrimaryList = (LinearLayout) mContainer.findViewById(R.id.titlePrimaryList);
		
		mOverflowBtn = (ImageView) mContainer.findViewById(R.id.titleOverflowBtn);
		mOverflowBtn.setVisibility(View.GONE);
		
		mHomeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mClickListener.onOptionItemSelected(MENU_HOME);
			}
		});
		mModuleHomeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mClickListener.onOptionItemSelected(MENU_MODULE_HOME);
			}
		});
	}
	
	public void setTextForModuleBtn(int resId) {
		mModuleHomeBtn.setText(resId);
	}
	
	public void setTextForModuleBtn(String text) {
		mModuleHomeBtn.setText(text);
	}
	
	/**
	 * Add the item appears on the titlebar. {@link CusPopupMenu}}
	 * @param item it must have the background picture
	 */
	public void addPrimaryItem(CusMenuItem item) {
		mPrimaryItems.add(item);
	}
	
	/**
	 * Add the item appears on the {@link CusPopupMenu}}
	 * @param item it should not have the background
	 */
	public void addSecondaryItem(CusMenuItem item) {
		mSecondaryItems.add(item);
	}
	
	private boolean isShowOverFlow() {
		return !mSecondaryItems.isEmpty();
	}
	
	/**
	 * Call it before the {@link MitTitleBar#configureTitleBar()}}}
	 ** @param listenter
	 */
	public void setOnTitleBarClick(OnCusMenuItemSelected listenter) {
		mClickListener = listenter;
	}
	/**
	 * Called after addPrimaryItem, addSecondaryItem.
	 */
	public void configureTitleBar(OnCusMenuItemSelected listenter) {
		mClickListener = listenter;
		initPrimaryItemList();
		initSecondaryItemList();
	}
	
	/**
	 * Call it after Called the 
	 * {@link MitTitleBar#setOnTitleBarClick(onTitleBarClickListener)}}}
	 */
	public void configureTitleBar() {
		initPrimaryItemList();
		initSecondaryItemList();
	}
	
	/**
	 * Shows the items in primary list in the titlebar.
	 */
	private void initPrimaryItemList() {
        for (CusMenuItem item : mPrimaryItems) {
        	View view = getMenuItem(item);
        	
            view.setFocusable(true);
            view.setClickable(true);
            mPrimaryList.addView(view);
        }
        mPrimaryList.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
	
	private void initSecondaryItemList() {
		if (isShowOverFlow()) {
			final CusPopupMenu menu = new CusPopupMenu(mOverflowBtn);
				
			for (CusMenuItem item : mSecondaryItems) {
				menu.addMenuItem(item);
			}
			menu.setMenuItemSelectedListener(mClickListener);
						
			mOverflowBtn.setVisibility(View.VISIBLE);
			mOverflowBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					menu.show();
				}
			});
		}
	}
    
	/**
	 * Used for the primary, secondary list to get the wrapper view.
	 * @param item it must not be null.
	 * @return View appears on the menu.
	 */
	private View getMenuItem(final CusMenuItem item) {
		LinearLayout container = (LinearLayout) mInflater.inflate(R.layout.titlebar_menu_item, null);
		ImageView img = (ImageView) container.findViewById(R.id.icon);
		TextView text = (TextView) container.findViewById(R.id.title);

		String title = item.getTitle();
		Drawable icon = item.getIcon();
        
		if (item.getIconResId() != 0) {
			img.setImageResource(item.getIconResId());
		} else {
			if (icon != null) {
				img.setImageDrawable(icon);
			} else {
				img.setVisibility(View.GONE);
			}
		}

		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}
		
		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mClickListener) {
					mClickListener.onOptionItemSelected(item.getId());
				}
			}
		});
		return container;
	}
}
