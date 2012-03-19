package edu.mit.mitmobile2.classes;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.Menu;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;

public class MITCoursesSubjectsSliderActivity extends SliderActivity {

	private ArrayList<String> course_ids = new ArrayList<String>();  // TODO hashMap
	
	MITCoursesSubjectsSliderActivity self;
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();
        if (extras!=null){
        	//curCourseId = extras.getString(KEY_COURSE_ID);
        }

        course_ids = CoursesDataModel.getCourseIds();
        if (course_ids==null) {
        	finish();
        	return;
        }
        
        
    	setTitle("MIT Subjects");
    	
    	createViews();

	}
	/****************************************************/
    void createViews() {

    	CourseSubjectView cv;
    	
    	for (int x=0; x<course_ids.size(); x++) {

    		String courseId = course_ids.get(x);
    		
    		cv = new CourseSubjectView(this, courseId);

    		addScreen(cv, courseId, "Course " + courseId);    
    		
    	}
    	
		setPosition(getPositionValue());
    	
    }	 

	@Override
	protected Module getModule() {
		return new ClassesModule();
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, Menu.NONE, "Search")
		  .setIcon(R.drawable.menu_search);
	}
	
}
