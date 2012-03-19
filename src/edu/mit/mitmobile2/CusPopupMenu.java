package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class CusPopupMenu {

    private View mRoot;
    private View mAnchorView;
    private LinearLayout mMenuList;

    private PopupWindow mWindow;
    private WindowManager mWindowManager;
    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<CusMenuItem> mMenuItems;
    private OnCusMenuItemSelected mMenuSelecterListener;
    private static final String TAG = "CusPopupMenu";
    
    public CusPopupMenu(View anchor) {
        mAnchorView = anchor;
        mContext = anchor.getContext();
        mMenuItems = new ArrayList<CusMenuItem>();
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
                    CusPopupMenu.this.mWindow.dismiss();
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

    public void addMenuItem(CusMenuItem item) {
        mMenuItems.add(item);
    }
    
    public void setMenuItemSelectedListener(OnCusMenuItemSelected listener) {
    	mMenuSelecterListener = listener;
    }

    public void show() {
    	if (mMenuItems.isEmpty()) {
    		Log.w(TAG, "You don't have any menu items yet!");
    		return;
    	}
        initMenuList();
        int availableHeight = 0;
        int[] location = new int[2];
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        
        mAnchorView.getLocationOnScreen(location);
        int yPos = location[1];
        
        //Not sure does it work well on different device.
        //Cause the screenHeight maybe different..
        if (screenHeight / 2 > location[1]) {
        	availableHeight = screenHeight - yPos - mAnchorView.getHeight();
        	yPos += mAnchorView.getHeight();
        } else {
        	availableHeight = yPos;
        	yPos -=  mMenuList.getMeasuredHeight();
        	if (yPos < 0) {
        		yPos = 0;
        	}
        }
        
        if (mMenuList.getMeasuredHeight() > availableHeight) {
        	mWindow.setHeight(availableHeight);
        } else {
        	mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        mWindow.showAtLocation(this.mAnchorView, Gravity.NO_GRAVITY, location[0], yPos);
    }

    private void initMenuList() {
    	mMenuList.removeAllViews();
        
    	for (CusMenuItem item : mMenuItems) {
        	View view = getMenuItem(item);

            view.setFocusable(true);
        	view.setEnabled(true);
            view.setClickable(true);
            mMenuList.addView(view);
        }
        mMenuList.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
    
    private View getMenuItem(final CusMenuItem item) {
        LinearLayout container = (LinearLayout) mInflater.inflate(R.layout.popup_menu_item_layout, null);
        TextView text = (TextView) container.findViewById(R.id.title);
        
        String title = item.getTitle();
        if (title != null) {
            text.setText(title);
        } else {
            text.setVisibility(View.GONE);
        }
        
        text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != mMenuSelecterListener) {
					mMenuSelecterListener.onOptionItemSelected(item.getId());
				}
				CusPopupMenu.this.mWindow.dismiss();
			}
		});
        return container;
    }
}