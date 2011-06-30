package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.TwoLineActionRow;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

public class RoomSearchCursorAdapter extends CursorAdapter implements FilterQueryProvider {
	
	FacilitiesDB mFacilitiesDB;
	private static final String TAG = "LocationsSearchCursorAdapter";
	
	public RoomSearchCursorAdapter(Context context, FacilitiesDB facilitiesDB) {
		super(context, facilitiesDB.getRoomCursor());
		mFacilitiesDB = facilitiesDB;
		setFilterQueryProvider(this);
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		Log.d(TAG,"constraint = " + constraint);
		Cursor cursor = mFacilitiesDB.getRoomSearchCursor(constraint);
		return new RoomSearchFilteredCursor(cursor, constraint.toString());
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TwoLineActionRow actionRow = (TwoLineActionRow) view;
		RoomSearchFilteredCursor filteredCursor = (RoomSearchFilteredCursor) cursor;
		String result = rowString(filteredCursor);
		Spannable title = Spannable.Factory.getInstance().newSpannable(result);		
		
		// find substrings matching constraint
		int currentIndex = 0;
		String constraint = filteredCursor.getConstraint().toLowerCase();
		String resultLower = result.toLowerCase();
		while(resultLower.indexOf(constraint, currentIndex) >= 0) {
			int foundIndex = resultLower.indexOf(constraint, currentIndex);
			title.setSpan(new ForegroundColorSpan(Color.RED), foundIndex, foundIndex + constraint.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			currentIndex = foundIndex + constraint.length();
		}
		
		actionRow.setTitle(title, TextView.BufferType.SPANNABLE);	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return new TwoLineActionRow(context);
	}

	private String rowString(RoomSearchFilteredCursor cursor) {
		int idIndex = cursor.getColumnIndex(FacilitiesDB.RoomTable._ID);
		long id = cursor.getLong(idIndex);
		if (id == -1) {
			return "Use '" + cursor.getConstraint() + "'";
		} else {
			int titleIndex = cursor.getColumnIndex(FacilitiesDB.RoomTable.ROOM);
			return cursor.getString(titleIndex);
		}
	}
	
	@Override
	public String convertToString(Cursor cursor) {
		return "";
	}
	
	public static class RoomSearchFilteredCursor extends CursorWrapper {

		String mConstraint;
		
		public RoomSearchFilteredCursor(Cursor cursor, String constraint) {
			super(cursorWithHeader(cursor));
			mConstraint = constraint;
		}
		
		public String getConstraint() {
			return mConstraint;
		}
	}

	private static Cursor cursorWithHeader(Cursor cursor) {
		MatrixCursor header = new MatrixCursor(new String[]{"_id"}, 1);
		header.addRow(new Object[] { new Long(-1)});
		return new MergeCursor(new Cursor[] {header, cursor});
	}
}
