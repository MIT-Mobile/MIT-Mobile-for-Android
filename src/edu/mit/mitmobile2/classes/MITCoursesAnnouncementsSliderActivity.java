package edu.mit.mitmobile2.classes;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.classes.CoursesDataModel.SubscriptionType;
import edu.mit.mitmobile2.objs.CourseItem;

public class MITCoursesAnnouncementsSliderActivity extends SliderActivity {
	
	final static String KEY_SUBJECT_MASTER_ID = "master_id";
	String mMasterId;
	CourseItem mCourseItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();
    	mMasterId = extras.getString(KEY_SUBJECT_MASTER_ID);
    	mCourseItem = CoursesDataModel.getDetails(mMasterId);
    	
    	createViews();

	}
    
    private void createViews() {
    	for(int i = 0; i < mCourseItem.announcements.size(); i++) {
    		CourseItem.Announcement announcement = mCourseItem.announcements.get(i);
    		SliderInterface announcementView = new CoursesAnnouncementView(this, announcement);
    		addScreen(announcementView, "Announcement: " + Integer.toString(i+1), "Announcements");
    	}
    	
    	setPosition(getPositionValue());
    }

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy '@' HH:mm");
    
    private class CoursesAnnouncementView extends LinearLayout implements SliderInterface {  	
    	
    	CourseItem.Announcement mAnnouncement;
    	
    	TextView mTitleView;
    	TextView mDateView;
    	TextView mBodyView;

		public CoursesAnnouncementView(Context context, CourseItem.Announcement announcement) {
			super(context);
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.courses_announcements, this);
			
			mTitleView = (TextView) findViewById(R.id.coursesAnnouncementTitleTV);
			mDateView = (TextView) findViewById(R.id.coursesAnnouncementDateTV);
			mBodyView = (TextView) findViewById(R.id.coursesAnnouncementBodyTV);
			mAnnouncement = announcement;
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
		public void updateView() {
			mTitleView.setText(mAnnouncement.title);
			
			String dateText = sDateFormat.format(new Date(mAnnouncement.unixtime * 1000));
			mDateView.setText(dateText);
			
			mBodyView.setText(mAnnouncement.text);
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
    
	@Override
	protected Module getModule() {
		return new ClassesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	static final int MENU_MY_STELLAR = MENU_LAST + 1;
	
	SubscriptionType mSubscriptionType;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == MENU_MY_STELLAR) {
			MITCoursesDetailsSliderActivity.showSubscriptionToast(this, mSubscriptionType);
			CoursesDataModel.subscribeForCourse(this, mCourseItem, mSubscriptionType, new Handler());
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		String menuTitle;
		int menuIconId;
		
		if(CoursesDataModel.myCourses.containsKey(mMasterId)) {
			menuTitle = "Remove from My Stellar";
			menuIconId = R.drawable.menu_remove_bookmark;
			mSubscriptionType = SubscriptionType.UNSUBSCRIBE;
		} else {
			menuTitle = "Add to My Stellar";
			menuIconId = R.drawable.menu_add_bookmark;
			mSubscriptionType = SubscriptionType.SUBSCRIBE;
		}
		
		menu.add(0, MENU_MY_STELLAR, Menu.NONE, menuTitle)
			.setIcon(menuIconId);
		
	}
	

}
