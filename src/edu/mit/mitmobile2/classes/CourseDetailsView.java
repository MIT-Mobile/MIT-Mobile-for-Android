package edu.mit.mitmobile2.classes;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.CourseItem.CourseTime;
import edu.mit.mitmobile2.people.PeopleSearchActivity;

public class CourseDetailsView implements SliderInterface {

	protected SliderActivity mActivity;
	protected CourseItem mCourseItem;
	
	protected LockingScrollView mView;
	
	protected boolean noStaff = false;
	
	protected TabHost tabHost;
	
	protected FullScreenLoader mLoader;
	
	/***************************************************/
	public CourseDetailsView(Context context, CourseItem course) {
		
		mCourseItem = course;
		
		mActivity = (SliderActivity) context;
		
	}
	
	@Override
	public View getView() {
		LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mView = (LockingScrollView) vi.inflate(R.layout.courses_details, null);
		
		mLoader = (FullScreenLoader) mView.findViewById(R.id.coursesDetailsLoader);
		
		updateHeader(mCourseItem);		

		tabHost = (TabHost) mView.findViewById(R.id.coursesDetailsTH);  
		tabHost.setup();  // NEEDED!!!
		
		TabConfigurator tabConfigurator = new TabConfigurator(mActivity, tabHost);
		tabConfigurator.addTab("News", R.id.tabNews);
		tabConfigurator.addTab("Info", R.id.tabInfo);
		tabConfigurator.addTab("Staff", R.id.tabStaff);
		
		tabConfigurator.configureTabs();
		
		return mView;
	}

	private void updateHeader(CourseItem courseItem) {
		TextView tv;
		
		tv = (TextView) mView.findViewById(R.id.coursesDetailsTitleTV);
		tv.setText(courseItem.masterId + " " + courseItem.title);
		
		tv = (TextView) mView.findViewById(R.id.coursesDetailsTimeTV);
		tv.setText(courseItem.term);
	}
	
	private boolean mHasBeenSelected = false;
	
	/****************************************************/
	@Override
	public void onSelected() {
		if(mHasBeenSelected) {
			// only load up data for the class once
			return;
		} else {
			mHasBeenSelected = true;
		}
		
		final Runnable updateResultsUI = new Runnable() {
			public void run() {
				CourseItem c = CoursesDataModel.getDetails(mCourseItem.masterId);
				if(c != null) {
					updateUI(c);
				} else {
					mLoader.showError();
				}
			}
		};
	
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				post(updateResultsUI);
			}
		};
	
		CoursesDataModel.fetchDetailsList(mActivity, myHandler, mCourseItem.masterId);
		
	}
	/****************************************************/
	void updateUI(final CourseItem courseItem) {
		
		CoursesNewsActionRow newsRow;
	
		updateHeader(courseItem);
		
		LinearLayout ll;
	
		mLoader.setVisibility(View.GONE);
		tabHost.setVisibility(View.VISIBLE);
		
		////////////////
		// News (Announcements)
		
		ll = (LinearLayout) tabHost.findViewById(R.id.tabNews);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("(M/d HH:mm)");  
	
		for (int i=0; i < courseItem.announcements.size(); i++) {
			CourseItem.Announcement an = courseItem.announcements.get(i);
			final int position = i;
			
			newsRow = new CoursesNewsActionRow(mActivity);
			newsRow.setTitle(an.title);
			newsRow.setContent(an.text);
			
			// format the date
			Date date = new Date();
			date.setTime(an.unixtime*1000);
			String dateText = dateFormat.format(date);
			newsRow.setDate(dateText);
			
			newsRow.setOnClickListener(new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, MITCoursesAnnouncementsSliderActivity.class);
					intent.putExtra(MITCoursesAnnouncementsSliderActivity.KEY_SUBJECT_MASTER_ID, courseItem.masterId);
					intent.putExtra(MITCoursesAnnouncementsSliderActivity.KEY_POSITION, position);
					mActivity.startActivity(intent);
				}
			});
			
			ll.addView(newsRow);
			ll.addView(new DividerView(mActivity, null));
		}

		if (courseItem.announcements.size()==0) {
			View noAnnouncements = mView.findViewById(R.id.coursesDetailsNoAnnouncements);
			noAnnouncements.setVisibility(View.VISIBLE);
		}
		
		////////////////
		// Info
		
		ll = (LinearLayout) tabHost.findViewById(R.id.tabInfo);
		
		// course times and Map link
		String location;
		TwoLineActionRow classTimeRow;
		for (CourseTime t : courseItem.times) {

			classTimeRow = new TwoLineActionRow(mActivity);	
			
			location = t.title + ": ";
			location += t.time + " ";
			if(!t.location.equals("")) {
				location +=  "(" + t.location + ")";
				classTimeRow.setActionIconResource(R.drawable.action_map);
				final CourseTime ct = t;
				classTimeRow.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommonActions.searchMap(mActivity, ct.location);
					}
				});
			}

			classTimeRow.setTitle(location);
			
			ll.addView(classTimeRow);
			ll.addView(new DividerView(mActivity, null));
		}
		
		
		// description
		if(!courseItem.description.equals("")) {
			TwoLineActionRow descriptionRow = new TwoLineActionRow(mActivity);
			descriptionRow.setTitle("Description:");
			descriptionRow.setSubtitle(courseItem.description);
			ll.addView(descriptionRow);
		}	
	
		
		////////////////
		// Staff
		
		if (courseItem.staff.instructors.size() + courseItem.staff.tas.size() == 0) {
			mView.findViewById(R.id.courseDetailsNoStaff).setVisibility(View.VISIBLE);
		} else {
			mView.findViewById(R.id.courseDetailsNoStaff).setVisibility(View.GONE);
		
			ll = (LinearLayout) tabHost.findViewById(R.id.tabStaff);
		
			LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
			// instructors
			if(courseItem.staff.instructors.size() > 0) {
				TextView header = (TextView) inflator.inflate(R.layout.courses_details_staff_header, null);
				header.setText("Instructors");
				ll.addView(header);
				ll.addView(new DividerView(mActivity, null));
			}
		
			for (final String staffName : courseItem.staff.instructors) {
				TwoLineActionRow personRow = new TwoLineActionRow(mActivity);
				personRow.setTitle(staffName);
				personRow.setActionIconResource(R.drawable.action_people);
				personRow.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PeopleSearchActivity.peopleSearch(mActivity, staffName);
					}
				});
			
				ll.addView(personRow);
				ll.addView(new DividerView(mActivity, null));			
			}
		
			// tas
			if(courseItem.staff.tas.size() > 0) {
				TextView header = (TextView) inflator.inflate(R.layout.courses_details_staff_header, null);
				header.setText("TAs");
				ll.addView(header);
				ll.addView(new DividerView(mActivity, null));
			}
		
			for (final String staffName : courseItem.staff.tas) {
				TwoLineActionRow personRow = new TwoLineActionRow(mActivity);
				personRow.setTitle(staffName);
				personRow.setActionIconResource(R.drawable.action_people);
				personRow.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						PeopleSearchActivity.peopleSearch(mActivity, staffName);
					}
				});
			
				ll.addView(personRow);
				ll.addView(new DividerView(mActivity, null));
			}
		}
		
		tabHost.setCurrentTab(0);
		
	}
	
	/****************************************************/
	

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LockingScrollView getVerticalScrollView() {
		return mView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}	
}
