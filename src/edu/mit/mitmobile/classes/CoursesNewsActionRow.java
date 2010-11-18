package edu.mit.mitmobile.classes;

import edu.mit.mitmobile.ActionRow;
import edu.mit.mitmobile.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CoursesNewsActionRow extends ActionRow {
	private TextView mTitleView;
	private TextView mContentView;
	private TextView mDateView;
	
	public CoursesNewsActionRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mTitleView = (TextView) findViewById(R.id.coursesNewsActionRowTitle);
		mContentView = (TextView) findViewById(R.id.coursesNewsActionRowContent);
		mDateView = (TextView) findViewById(R.id.coursesNewsActionRowDate);	
		
	}
	
	public void setTitle(String title) {
		mTitleView.setText(title);
	}
	
	public void setContent(String content) {
		mContentView.setText(content);
	}
	
	public void setDate(String date) {
		mDateView.setText(date);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.courses_news_action_row;
	}
}
