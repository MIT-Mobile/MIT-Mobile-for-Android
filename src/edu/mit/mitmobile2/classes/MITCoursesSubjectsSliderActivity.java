package edu.mit.mitmobile2.classes;

import java.util.ArrayList;

import android.os.Bundle;
import edu.mit.mitmobile2.CategoryNewModuleActivity;
import edu.mit.mitmobile2.NewModule;

public class MITCoursesSubjectsSliderActivity extends CategoryNewModuleActivity {

	private ArrayList<String> course_ids = new ArrayList<String>();  // TODO hashMap
	
	public static final String KEY_POSITION = "key_position";
	
	int position;
	MITCoursesSubjectsSliderActivity self;
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();
        if (extras!=null){
        	position = extras.getInt(KEY_POSITION, 0);
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
    		addCategory(cv, courseId, "Course " + courseId);
    	}
    	onOptionItemSelected(course_ids.get(position));
    }	 

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
	
	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new ClassesModule();
	}
	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
	
}
