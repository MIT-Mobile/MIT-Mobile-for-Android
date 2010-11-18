package edu.mit.mitmobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TitleBar extends LinearLayout {
	protected TextView mTextView;
	protected View mLeftArrow;
	protected View mRightArrow;
	
	protected View mSubtitleLeftArrow;
	protected View mSubtitleRightArrow;
	protected TextView mSubtitleTextView;
	protected View mSubtitleTitleBar;
	
	protected boolean mHasSubtitle = false;
	
	public TitleBar(final Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.title_bar, this);
		
		mTextView = (TextView) findViewById(R.id.titleBarTV);
		mLeftArrow = findViewById(R.id.titleBarLeftArrow);
		mRightArrow = findViewById(R.id.titleBarRightArrow);
		
		mSubtitleLeftArrow = findViewById(R.id.titleBarSubtitleLeftArrow);
		mSubtitleRightArrow = findViewById(R.id.titleBarSubtitleRightArrow);
		mSubtitleTextView = (TextView) findViewById(R.id.titleBarSubtitleTV);
		mSubtitleTitleBar = findViewById(R.id.titleBarSubtitleBar);
		
		ImageView backgroundView = (ImageView) findViewById(R.id.titleBarBackgrooundIV);
		int height = backgroundView.getDrawable().getIntrinsicHeight();
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
		RelativeLayout titleWrapper = (RelativeLayout) findViewById(R.id.titleWrapper);
		titleWrapper.setLayoutParams(params);
		
		String initialText = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
		if(initialText != null) {
			setTitle(initialText);
		}
		
		findViewById(R.id.titleBarHomeButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MITNewsWidgetActivity.goHome(context);
			}
		});
	}
	
	public void setTitle(String text) {
		mTextView.setText(text);
	}
	
	public void useSubtitleBar() {
		mHasSubtitle = true;
		mSubtitleTitleBar.setVisibility(VISIBLE);
	}
	
	public boolean getHasSubtitle() {
		return mHasSubtitle;
	}
	
	public void setSubtitle(String subtitle) {
		mSubtitleTextView.setText(subtitle);
	}
	
	public void showArrows() {
		if(!mHasSubtitle) {
			mLeftArrow.setVisibility(VISIBLE);
			mRightArrow.setVisibility(VISIBLE);
		} else {
			mSubtitleLeftArrow.setVisibility(VISIBLE);
			mSubtitleRightArrow.setVisibility(VISIBLE);
		}
	}
	
	public void setLeftArrowEnabled(boolean enabled) {
		if(!mHasSubtitle) {
			mLeftArrow.setEnabled(enabled);
		} else {
			mSubtitleLeftArrow.setEnabled(enabled);
		}
	}
	
	public void setRightArrowEnable(boolean enabled) {
		if(!mHasSubtitle) {
			mRightArrow.setEnabled(enabled);
		} else {
			mSubtitleRightArrow.setEnabled(enabled);
		}
	}
	
	public void setOnArrowListener(final OnArrowListener arrowListener) {
		mLeftArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				arrowListener.onLeftArrow();
			}
		});
		
		mSubtitleLeftArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				arrowListener.onLeftArrow();
			}
		});
		
		mRightArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				arrowListener.onRightArrow();
			}
		});	
		
		mSubtitleRightArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				arrowListener.onRightArrow();
			}
		});
	}
	
	public static interface OnArrowListener {
		public void onRightArrow();
		
		public void onLeftArrow();
	}	
}
