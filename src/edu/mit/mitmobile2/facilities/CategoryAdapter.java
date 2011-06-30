package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;

class CategoryAdapter extends CursorAdapter {
	
	public CategoryAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}
	
	private void setupRow(Cursor cursor, View row) {
		CategoryRecord categoryRecord = new CategoryRecord();
		categoryRecord.id = cursor.getString(1);
		categoryRecord.name = cursor.getString(2);
		
		TwoLineActionRow actionRow = (TwoLineActionRow) row;
		actionRow.setTitle(categoryRecord.name);		
		
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
