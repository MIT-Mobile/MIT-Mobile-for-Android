package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationCategoryTable;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationTable;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.RoomRecord;

import android.content.Context;
import android.database.Cursor;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

class RoomAdapter extends CursorAdapter {

	private Context mContext;
	private static String TAG = "RoomAdapter";
	
	public RoomAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		mContext = context;
	}
	
	private void setupRow(Cursor cursor, View row) {
		Log.d(TAG,"setupRow " + row.getId());
		try {
			RoomRecord roomRecord = new RoomRecord();	
			
			roomRecord.building = cursor.getString(1);	
			roomRecord.floor = cursor.getString(2);
			roomRecord.room = cursor.getString(3);
		
			TextView roomTV = (TextView) row.findViewById(R.id.facilitiesRowTV);
			
			roomTV.setText(roomRecord.room,  TextView.BufferType.SPANNABLE);		
			int separator = roomRecord.room.length() + 1;
		}
		catch (Exception e) {
			Log.d(TAG,"setupRow exception = " + e.getMessage());
		}
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
