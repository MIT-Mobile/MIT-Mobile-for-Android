package edu.mit.mitmobile2;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class MITPlainSecondaryTitleBar extends MITSecondaryTitleBar {

	private TextView mTitleView;
	
	public MITPlainSecondaryTitleBar(Context context) {
		super(context);
		mTitleView = (TextView) mContainer.findViewById(R.id.secondaryTitleBarPlainTitle);
		mTitleView.setVisibility(View.VISIBLE);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}
}
