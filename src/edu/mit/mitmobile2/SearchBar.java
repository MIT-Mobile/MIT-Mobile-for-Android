package edu.mit.mitmobile2;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchBar extends LinearLayout {

	ImageButton mSearchBox;
	ImageButton mSearchButton;
	TextView mSearchHintTV;
	
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

	public SearchBar(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.search_bar, this);
		
		mSearchBox = (ImageButton) findViewById(R.id.searchBox);
		mSearchBox.setFocusableInTouchMode(true);
		mSearchButton = (ImageButton) findViewById(R.id.searchButton);
		mSearchHintTV = (TextView) findViewById(R.id.searchBoxHint);
		
		if(attributeSet != null) {
			String hintText = attributeSet.getAttributeValue(NAMESPACE, "hint");
			setSearchHint(hintText);
		}
	}
	
	public static interface OnInitiateSearchListener {
		public void onInitiateSearch(String searchQuery);	
	}
	
	
	public void setSearchHint(String searchHint) {
		mSearchHintTV.setText(searchHint);
	}
	
	public void setSystemSearchInvoker(final Activity activity) {
		
		mSearchBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSearchBox.isFocused()) {
					activity.onSearchRequested();
				} else {
					mSearchBox.requestFocus();
				}
			}
		});
		
		mSearchBox.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getUnicodeChar() != 0) {
					String initialSearch = new String(new int[] { event.getUnicodeChar() }, 0, 1);
					activity.startSearch(initialSearch, false, null, false);
					return true;
				}
				return false;
			}
		});
		
		mSearchBox.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.onSearchRequested();
				return true;
			}
		});
		
		mSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.onSearchRequested();
			}
		});
	}
}
