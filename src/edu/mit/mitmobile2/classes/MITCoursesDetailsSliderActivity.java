package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.classes.CoursesDataModel.SubscriptionType;
import edu.mit.mitmobile2.objs.CourseItem;

public class MITCoursesDetailsSliderActivity extends SliderActivity {
	
	public static final String SUBJECT_MASTER_ID_KEY = "subject_master_id";
	public static final String KEY_COURSE_ID = "course_id";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String MY_STELLAR_KEY = "my_stellar";
	
	static final int MENU_MY_STELLAR = MENU_LAST + 1;
	
	List<CourseItem> courses;

	String courseId;

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
	
	ArrayList<CourseDetailsView> mCourseDetailsViews = new ArrayList<CourseDetailsView>();
    void createViews() {

    	CourseDetailsView cv;
    	
    	for (int x=0; x<courses.size(); x++) {

    		CourseItem ci = courses.get(x);
    		
    		cv = new CourseDetailsView(this,ci);
    		
    		addScreen(cv, ci.title, "Class Info");   
    		
    		mCourseDetailsViews.add(cv);
    		
    	}
    	
    	setPosition(mStartPosition);
    	
    }
    
    
    public static void showSubscriptionToast(Context context, SubscriptionType subscriptionType) {
		String userNotice = null;
		switch (subscriptionType) {
			case SUBSCRIBE:
				userNotice = "Subscribing for notices";
				break;
			case UNSUBSCRIBE:
				userNotice = "removing notices";
				break;
		}  
		
		Toast.makeText(context, userNotice, Toast.LENGTH_SHORT).show();
    }
    
    SubscriptionType mCurrentSubscriptionType;
    CourseItem mCurrentCourse;
	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == MENU_MY_STELLAR) {
			showSubscriptionToast(this, mCurrentSubscriptionType);
			CoursesDataModel.subscribeForCourse(this, mCurrentCourse, mCurrentSubscriptionType, new Handler());
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		
		String menuTitle;
		int menuIconId;
		mCurrentCourse = mCourseDetailsViews.get(getPosition()).getCourse();
		if(CoursesDataModel.myCourses.containsKey(mCurrentCourse.masterId)) {
			menuTitle = "Remove";
			menuIconId = R.drawable.menu_remove_bookmark;
			mCurrentSubscriptionType = SubscriptionType.UNSUBSCRIBE;
		} else {
			menuTitle = "Add to My Stellar";
			menuIconId = R.drawable.menu_add_bookmark;
			mCurrentSubscriptionType = SubscriptionType.SUBSCRIBE;
		}
		
		menu.add(0, MENU_MY_STELLAR, Menu.NONE, menuTitle)
			.setIcon(menuIconId);
		
	}
	
	@Override
	protected Module getModule() {
		return new ClassesModule();
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
	
}
