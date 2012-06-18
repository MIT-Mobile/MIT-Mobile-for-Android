package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MITPopupMenu {

    private View mRoot;
    private View mAnchorView;
    private LinearLayout mMenuList;

    private PopupWindow mWindow;
    private WindowManager mWindowManager;
    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<MITMenuItem> mMenuItems;
    private OnMITMenuItemListener mMenuSelecterListener;
    private static final String TAG = "MITPopupMenu";
    
    public MITPopupMenu(View anchor) {
        mAnchorView = anchor;
        mContext = anchor.getContext();
        mMenuItems = new ArrayList<MITMenuItem>();
        initViews();
        initWindow();
        
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }
    
    private void initViews() {
    	mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = (ViewGroup) mInflater.inflate(R.layout.popup_menu_layout, null);
        mMenuList = (LinearLayout) mRoot.findViewById(R.id.popupMenuList);
    }
    
    private void initWindow() {
    	mWindow = new PopupWindow(mContext);
        mWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);

        mWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    MITPopupMenu.this.mWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        
        mWindow.setContentView(mRoot);
    }
    
    public void addMenuItem(MITMenuItem item) {
    	if (null != item) {
    		mMenuItems.add(item);
    	}
    }
    

    public void clearMenuItems() {
    	mMenuItems.clear();
    }
    
    public void setMenuItemSelectedListener(OnMITMenuItemListener listener) {
    	mMenuSelecterListener = listener;
    }

    public void show() {
    	if (mMenuItems.isEmpty()) {
    		Log.w(TAG, "You don't have any menu items yet!");
    		return;
    	}
        refreshMenuList();
        int availableHeight = 0;
        int[] location = new int[2];
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        
        mAnchorView.getLocationOnScreen(location);
        
        int xPos = location[0];
        int yPos = location[1];
        
        //Not sure does it work well on different device.
        //Cause the screenHeight maybe different..
        if (screenHeight / 2 > yPos) {
        	availableHeight = screenHeight - yPos - mAnchorView.getHeight();
        	yPos += mAnchorView.getHeight();
        } else {
        	availableHeight = yPos;
        	yPos -=  mMenuList.getMeasuredHeight();
        	if (yPos < 0) {
        		yPos = 0;
        	}
        }
        
        if (screenWidth / 2 >  xPos) {
        	xPos += AttributesParser.parseDimension("6dip", mContext);
        } else {
        	xPos = screenWidth - mMenuList.getMeasuredWidth() - AttributesParser.parseDimension("6dip", mContext);
        }
        
        if (mMenuList.getMeasuredHeight() > availableHeight) {
        	mWindow.setHeight(availableHeight);
        } else {
        	mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        
        // the animation has serious bugs in gingerbread
        // not sure which API level bugs are fixed (but definitely fixed by API Level 15)
        if (Build.VERSION.SDK_INT >= 11) {
        	mWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        }
        mWindow.showAtLocation(this.mAnchorView, Gravity.NO_GRAVITY, xPos, yPos);
    }

    public void refreshMenuList() {
    	mMenuList.removeAllViews();
    	
    	for (MITMenuItem item : mMenuItems) {
        	View view = getMenuItem(item, mMenuItems.indexOf(item));
        	
            view.setFocusable(true);
        	view.setEnabled(true);
            view.setClickable(true);
            mMenuList.addView(view);
        }
    }
    
    private View getMenuItem(final MITMenuItem item, int index) {
    	MITPopupMenuItemLayout itemLayout = new MITPopupMenuItemLayout(mContext, null);
        
    	itemLayout.setTopBorder(true);
    	if ((mMenuItems.size() - 1) == index) {
    		itemLayout.setBottomBorder(true);
    	}
    	
        if (item.getIcon() != null) {
        	itemLayout.setImageDrawable(item.getIcon());
        } else if (item.getIconResId() != 0) {
        	itemLayout.setImageResource(item.getIconResId());
        } else {
        	itemLayout.setImageVisibility(View.GONE);
        }
        
        String title = item.getTitle();
        if (title != null) {
        	itemLayout.setText(title);
        } else {
        	itemLayout.setTextVisibility(View.GONE);
        }
        
        itemLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mMenuSelecterListener) {
					mMenuSelecterListener.onOptionItemSelected(item.getId());
				}
				MITPopupMenu.this.mWindow.dismiss();
			}
		});
        return itemLayout;
    }
    
    public void notifyScreenRotated() {
    	if (mWindow.isShowing()) {
    		MITPopupMenu.this.mWindow.dismiss();
    	}
    }
}