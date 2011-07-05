package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.ProblemTypeRecord;

class ProblemTypeAdapter extends CursorAdapter {

	private Context mContext;
	
	public ProblemTypeAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		mContext = context;
	}
	
	private void setupRow(Cursor cursor, View row) {
		ProblemTypeRecord problemTypeRecord = new ProblemTypeRecord();
		problemTypeRecord.problem_type = cursor.getString(1);
		
		TwoLineActionRow actionRow = (TwoLineActionRow) row;
		actionRow.setTitle(problemTypeRecord.problem_type);
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
