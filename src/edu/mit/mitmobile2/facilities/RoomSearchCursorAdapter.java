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
		super(context, null);
		mFacilitiesDB = facilitiesDB;
		setFilterQueryProvider(this);
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		Log.d(TAG,"constraint = " + constraint);
		String trimmedConstraint = constraint.toString().trim();
		if(trimmedConstraint.length() > 0) {
			Cursor cursor = mFacilitiesDB.getRoomSearchCursor(trimmedConstraint);
			return new RoomSearchFilteredCursor(cursor, trimmedConstraint);
		} else {
			return null;
		}
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TwoLineActionRow actionRow = (TwoLineActionRow) view;
		int idIndex = cursor.getColumnIndex(FacilitiesDB.RoomTable._ID);
		RoomSearchFilteredCursor filteredCursor = (RoomSearchFilteredCursor) cursor;
		long id = cursor.getLong(idIndex);
		if (id == -1 ) {
			String useMyTextString = "Use '" + filteredCursor.getConstraint() + "'";
			actionRow.setTitle(useMyTextString);
			return;
		}
		
		int titleIndex = cursor.getColumnIndex(FacilitiesDB.RoomTable.ROOM);
		String result = cursor.getString(titleIndex);
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
