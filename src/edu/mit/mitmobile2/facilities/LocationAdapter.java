package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationCategoryTable;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationTable;
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

class LocationAdapter extends CursorAdapter {

	private Context mContext;
	
	public LocationAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		mContext = context;
	}
	
	private void setupRow(Cursor cursor, View row) {
		LocationRecord locationRecord = FacilitiesDB.getLocationRecord(cursor);
		populateView(locationRecord, row);
	}
	
	public static void populateView(LocationRecord locationRecord, View row) {
		TextView locationTV = (TextView) row.findViewById(R.id.facilitiesRowTV);
		
		if (locationRecord.bldgnum.equals("")) {
			locationTV.setText(locationRecord.name,  TextView.BufferType.SPANNABLE);
		}
		else {
			locationTV.setText(locationRecord.bldgnum + " - " + locationRecord.name,  TextView.BufferType.SPANNABLE);		
		}
		int separator = locationRecord.name.length() + 1;
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
