package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchResultsHeader extends RelativeLayout {

	TextView mTextView;
	
	public SearchResultsHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.search_results_header, this);
		
		mTextView = (TextView) findViewById(R.id.searchResultsHeaderText);
		
	}

	public void setText(String text) {
		mTextView.setText(text);
	}
}
