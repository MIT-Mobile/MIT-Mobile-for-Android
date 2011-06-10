package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.TwoLineActionRow;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;

public class LocationsSearchCursorAdapter extends CursorAdapter implements FilterQueryProvider {
	
	CharSequence mLastConstraint = null;
	FacilitiesDB mFacilitiesDB;
	
	public LocationsSearchCursorAdapter(Context context, FacilitiesDB facilitiesDB) {
		super(context, facilitiesDB.getLocationCursor());
		mFacilitiesDB = facilitiesDB;
		setFilterQueryProvider(this);
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		Cursor c = mFacilitiesDB.getLocationSearchCursor(constraint);
		return new FilteredCursor(c, constraint);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TwoLineActionRow actionRow = (TwoLineActionRow) view;
		actionRow.setTitle(convertToString(cursor));	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return new TwoLineActionRow(context);
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		int titleIndex = cursor.getColumnIndex(FacilitiesDB.LocationTable.NAME);
		return cursor.getString(titleIndex);
	}
	
	private static class FilteredCursor extends CursorWrapper {

		CharSequence mConstraint;
		
		public FilteredCursor(Cursor cursor, CharSequence constraint) {
			super(cursor);
			mConstraint = constraint;
		}
		
		public CharSequence getConstraint() {
			return mConstraint;
		}
	}

}
