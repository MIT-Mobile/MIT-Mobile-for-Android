package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MITPopupMenuItemLayout extends LinearLayout {

	private ImageView mImageView;
    private TextView mTextView;
	
    private Paint mPaint;
    public boolean mHasTopBorder = false;
    public boolean mHasBottomBorder = false;
    
	public MITPopupMenuItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_menu_item_layout, this);
		
		mImageView = (ImageView) view.findViewById(R.id.popup_menuitem_image);
		mTextView = (TextView) view.findViewById(R.id.popup_menuitem_title);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(context.getResources().getColor(R.color.popupDividerColor));
		mPaint.setStrokeWidth(4);
		view.setBackgroundResource(R.drawable.highlight_background);
	}
	
	public void setImageDrawable(Drawable drawable) {
		mImageView.setImageDrawable(drawable);
	}

	public void setImageResource(int resId) {
		mImageView.setImageResource(resId);
	}
	
	public void setImageVisibility(int visibility) {
		mImageView.setVisibility(visibility);
	}
	
	public void setText(String text) {
		mTextView.setText(text);
	}
	
	public void setTextVisibility(int visibility) {
		mTextView.setVisibility(visibility);
	}
	
	public void setTopBorder(boolean hasTopBorder) {
		mHasTopBorder = hasTopBorder;
	}

	public void setBottomBorder(boolean hasBottomBorder) {
		mHasBottomBorder = hasBottomBorder;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (mHasTopBorder) {
			canvas.drawLine(0, 0, getMeasuredWidth(), 0, mPaint);
		}
		if (mHasBottomBorder) {
			canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), mPaint);
		}
	}
}
