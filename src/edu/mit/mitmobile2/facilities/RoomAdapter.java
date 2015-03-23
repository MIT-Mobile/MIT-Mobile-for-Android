package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.RoomRecord;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

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
		
			TwoLineActionRow actionRow = (TwoLineActionRow) row;
			
			actionRow.setTitle(roomRecord.room);		
		}
		catch (Exception e) {
			Log.d(TAG,"setupRow exception = " + e.getMessage());
		}
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
