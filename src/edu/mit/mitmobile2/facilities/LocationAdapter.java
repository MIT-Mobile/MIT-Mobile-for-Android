package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

class LocationAdapter extends CursorAdapter {
	
	public LocationAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}
	
	private void setupRow(Cursor cursor, View row) {
		LocationRecord locationRecord = FacilitiesDB.getLocationRecord(cursor);
		populateView(locationRecord, row);
	}
	
	public static void populateView(LocationRecord locationRecord, View row) {
		TwoLineActionRow twoLineActionRow = (TwoLineActionRow) row;
		
		if (locationRecord.bldgnum.equals("")) {
			twoLineActionRow.setTitle(locationRecord.name);
		}
		else {
			twoLineActionRow.setTitle(locationRecord.bldgnum + " - " + locationRecord.name,  TextView.BufferType.SPANNABLE);		
		}
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		setupRow(cursor, view);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		TwoLineActionRow row = new TwoLineActionRow(context);
		setupRow(cursor, row);
		return row;
	}
}
