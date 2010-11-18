package edu.mit.mitmobile.classes;

import java.util.List;

import edu.mit.mitmobile.R;
import edu.mit.mitmobile.TwoLineActionRow;
import edu.mit.mitmobile.objs.CourseListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CoursesListArrayAdapter extends ArrayAdapter<CourseListItem> {

	Context ctx;
	
	public CoursesListArrayAdapter(Context context, int textViewResourceId, List<CourseListItem> courses) {
		
		super(context, textViewResourceId,courses);
		ctx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.courses_row, null);
		}

		CourseListItem ci = (CourseListItem) this.getItem(position);
		
		if (ci != null) {
			TwoLineActionRow row = (TwoLineActionRow) v;
			row.setTitle("Course " + ci.shortStr);	
			row.setSubtitle(ci.name);			
		}
		
		
		return v;
	}
	
}
