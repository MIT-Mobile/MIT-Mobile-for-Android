package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
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
		
		TextView categoryTV = (TextView) row.findViewById(R.id.facilitiesRowTV);
		
		categoryTV.setText(problemTypeRecord.problem_type,  TextView.BufferType.SPANNABLE);
		
		
		int separator = problemTypeRecord.problem_type.length() + 1;
		
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
