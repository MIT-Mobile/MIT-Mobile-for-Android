package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

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

		TwoLineActionRow actionRow = (TwoLineActionRow) row;
		actionRow.setTitle(locationCategoryRecord.locationId);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		setupRow(cursor, view);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View row = new TwoLineActionRow(mContext);
		setupRow(cursor, row);
		return row;
	}
}
