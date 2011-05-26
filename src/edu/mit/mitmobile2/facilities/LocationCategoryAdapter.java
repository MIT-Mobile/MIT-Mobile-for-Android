package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;

import android.content.Context;
import android.database.Cursor;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

class LocationCategoryAdapter extends CursorAdapter {

	private Context mContext;
	
	public LocationCategoryAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		mContext = context;
	}
	
	private void setupRow(Cursor cursor, View row) {
		LocationCategoryRecord locationCategoryRecord = new LocationCategoryRecord();
		locationCategoryRecord.categoryId = cursor.getString(1);
		locationCategoryRecord.locationId = cursor.getString(2);

		
		TextView locationTV = (TextView) row.findViewById(R.id.facilitiesRowTV);
		
		locationTV.setText(locationCategoryRecord.locationId,  TextView.BufferType.SPANNABLE);
		
		int separator = locationCategoryRecord.categoryId.length() + 1;
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
