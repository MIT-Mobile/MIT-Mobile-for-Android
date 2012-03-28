package edu.mit.mitmobile2.classes;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.objs.CourseItem;

public class MITCoursesDetailsSliderActivity extends SliderNewModuleActivity {
	
	public static final String SUBJECT_MASTER_ID_KEY = "subject_master_id";
	public static final String KEY_COURSE_ID = "course_id";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String MY_STELLAR_KEY = "my_stellar";
	
	List<CourseItem> courses;

	String courseId;

	MITMenuItem mBookmarkMenuItem;
	MITPlainSecondaryTitleBar mSecondaryTitleBar;
	
	private int mStartPosition;
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();
    	
        if (extras!=null){        	
        	if(extras.containsKey(KEY_COURSE_ID)) {
        		courseId = extras.getString(KEY_COURSE_ID);   
        		courses = CoursesDataModel.getSubjectList(courseId);  // null here 
        	}
        	if(extras.containsKey(SEARCH_TERM_KEY)) {
        		String searchTerms = extras.getString(SEARCH_TERM_KEY);
        		Log.d("CourseDetails", "searchTerms: " + searchTerms);
        		courses = CoursesDataModel.executeLocalSearch(searchTerms);
        	}
        	if(extras.containsKey(MY_STELLAR_KEY)) {
        		courses = CoursesDataModel.getFavoritesList(this);
        	}
        	//mStartPosition = CoursesDataModel.getPosition(courses, extras.getString(SUBJECT_MASTER_ID_KEY)); // FIXME courses is null so Exception
        }

        if (courses == null) {
        	finish();
        	return;
        }
        
		setTitle("MIT Course Details");
		
    	mStartPosition = CoursesDataModel.getPosition(courses, extras.getString(SUBJECT_MASTER_ID_KEY));
        createViews();
	}
	/****************************************************/
	static void launchActivity(Context context, CourseItem item, boolean searchOrCourseListMode, String extras) {
		
		// load the activity that shows all the detail search results
		Intent intent = new Intent(context, MITCoursesDetailsSliderActivity.class);
		if(searchOrCourseListMode) {
			intent.putExtra(SEARCH_TERM_KEY, extras);
		} else {
			intent.putExtra(KEY_COURSE_ID, extras);
		}
		
		intent.putExtra(SUBJECT_MASTER_ID_KEY, item.masterId);

		CoursesDataModel.markAsRecentlyViewed(item);
		context.startActivity(intent);
	}
	/****************************************************/
    void createViews() {

    	CourseDetailsView cv;
    	
    	for (int x=0; x<courses.size(); x++) {

    		CourseItem ci = courses.get(x);
    		
    		cv = new CourseDetailsView(this,ci);
    		addScreen(cv, ci.title, "" + (x+1) + " of " + courses.size());   
    	}
    	
    	setPosition(mStartPosition);
    	
    }
	/****************************************************/
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
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
