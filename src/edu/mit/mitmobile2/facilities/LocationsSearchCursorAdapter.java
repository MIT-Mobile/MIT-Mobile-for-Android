package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.TwoLineActionRow;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

public class LocationsSearchCursorAdapter extends CursorAdapter implements FilterQueryProvider {
	
	FacilitiesDB mFacilitiesDB;
	private static final String TAG = "LocationsSearchCursorAdapter";
	
	public LocationsSearchCursorAdapter(Context context, FacilitiesDB facilitiesDB) {
		super(context, facilitiesDB.getLocationCursor());
		mFacilitiesDB = facilitiesDB;
		setFilterQueryProvider(this);
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		Log.d(TAG,"constraint = " + constraint);
		Cursor c = mFacilitiesDB.getLocationSearchCursor(constraint);
		return new FilteredCursor(c, constraint.toString());
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TwoLineActionRow actionRow = (TwoLineActionRow) view;
		String result = convertToString(cursor);
		Spannable title = Spannable.Factory.getInstance().newSpannable(result);		
		
		// find substrings matching constraint
		int currentIndex = 0;
		FilteredCursor filteredCursor = (FilteredCursor) cursor;
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
		//int titleIndex = cursor.getColumnIndex(FacilitiesDB.LocationTable.NAME);
		return cursor.getString(5); // display_name index
	}
	
	private static class FilteredCursor extends CursorWrapper {

		String mConstraint;
		
		public FilteredCursor(Cursor cursor, String constraint) {
			super(cursor);
			mConstraint = constraint;
		}
		
		public String getConstraint() {
			return mConstraint;
		}
	}

}
