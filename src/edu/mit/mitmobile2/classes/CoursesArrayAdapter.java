package edu.mit.mitmobile2.classes;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.CourseItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CoursesArrayAdapter extends ArrayAdapter<CourseItem> {

	Context ctx;

	private boolean mUseLongFormat = false;
	public CoursesArrayAdapter(Context context, int textViewResourceId, List<CourseItem> courses) {
		
		super(context, textViewResourceId,courses);
		ctx = context;
	}

	public void setUseLongFormat(boolean useLongFormat) {
		mUseLongFormat = useLongFormat;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.courses_row, null);
		}

		CourseItem ci = (CourseItem) this.getItem(position);
		
		if (ci != null) {
			TwoLineActionRow row = (TwoLineActionRow) v;
			row.setTitle(ci.name);	
			
			if(mUseLongFormat) {
				row.setSubtitle(ci.title);
			}
		}
		
		return v;
	}
	
}
