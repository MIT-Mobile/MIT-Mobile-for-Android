package edu.mit.mitmobile2.classes;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.objs.CourseListItem;

public class CourseListView extends LinearLayout implements SliderInterface, OnItemClickListener   {

	Activity a;
	
	ArrayList<String> course_ids = new ArrayList<String>();
	
	ListView lv_courses;  
	
	String mCourseListTitle;
	
	/**
	 * @param courses **************************************************/
	public CourseListView(Context context, int groupId, ArrayList<CourseListItem> courses, String title) {
		
		super(context);

		mCourseListTitle = title;
		
		a = (Activity) context;
		
		LayoutInflater vi = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout topView = (LinearLayout) vi.inflate(R.layout.courses_list, null);		
		
		// Prep data
		for (int i=0; i<courses.size(); i++) {
			CourseListItem c = courses.get(i);
			course_ids.add(c.shortStr);  // TODO hashMap
		}
		
		
		lv_courses = (ListView) topView.findViewById(R.id.coursesListTopLV);
		lv_courses.setOnItemClickListener(this);
		
		CoursesListArrayAdapter caa = new CoursesListArrayAdapter(a, R.layout.courses_row, courses);
		lv_courses.setAdapter(caa);

		addView(topView);

	}
	/****************************************************/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		//CourseListItem si = (CourseListItem) lv_courses.getItemAtPosition(position);

		 // FIXME drop
		CoursesDataModel.cur_course_ids = course_ids;
		
		Intent i = new Intent(a, MITCoursesSubjectsSliderActivity.class);
		//i.putExtra(MITCoursesSubjectsSliderActivity.KEY_COURSE_ID, position);
		i.putExtra(MITCoursesSubjectsSliderActivity.KEY_POSITION, position);
		a.startActivity(i);
		
	}
	
	/****************************************************/
	@Override
	public void updateView() {
		
		// Leave blank... we preloaded all data...
		
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void onSelected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}	
}
