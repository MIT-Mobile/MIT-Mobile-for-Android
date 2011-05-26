package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesDB.CategoryTable;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

class CategoryAdapter extends CursorAdapter {

	private Context mContext;
	private TextAppearanceSpan mContactStyle;
	private TextAppearanceSpan mPhoneStyle;
	
	public CategoryAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		mContext = context;
		//mContactStyle = new TextAppearanceSpan(mContext, R.style.ListItemPrimary);
		//mPhoneStyle = new TextAppearanceSpan(mContext, R.style.ListItemSecondary);
	}
	
	private void setupRow(Cursor cursor, View row) {
		CategoryRecord categoryRecord = new CategoryRecord();
		categoryRecord.id = cursor.getString(1);
		categoryRecord.name = cursor.getString(2);
		
		TextView categoryTV = (TextView) row.findViewById(R.id.facilitiesRowTV);
		
		categoryTV.setText(categoryRecord.name,  TextView.BufferType.SPANNABLE);
		
		//Spannable spannable = (Spannable) categoryRecord.category;
		
		int separator = categoryRecord.name.length() + 1;
		//spannable.setSpan(mContactStyle, 0,
		//		separator, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//spannable.setSpan(mPhoneStyle, separator,
		//		spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		LinearLayout row = (LinearLayout) view;
		setupRow(cursor, row);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout row = (LinearLayout) inflater.inflate(R.layout.facilities_row, null);
		setupRow(cursor, row);
		return row;
	}
}
