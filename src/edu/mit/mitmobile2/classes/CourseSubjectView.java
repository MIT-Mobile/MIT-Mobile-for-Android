package edu.mit.mitmobile2.classes;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.objs.CourseItem;

public class CourseSubjectView implements SliderInterface, OnItemClickListener, OnLongClickListener  {

	Activity mContext;

	String courseId = "";

	public ArrayList<CourseItem> subjects = new ArrayList<CourseItem>();

	ListView lv_subjects;
	
	int longClickPos;
	
	View mView;
	
	/****************************************************/
	public CourseSubjectView(Context context, String courseId) {
		
		//super(context);

		this.courseId = courseId;
		
		mContext = (Activity) context;
	}
	
	
	public View getView() {
		LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mView = (LinearLayout) vi.inflate(R.layout.courses_subject, null);
		
		// 2.10, 2.20, 2.30 etc
		
		lv_subjects = (ListView) mView.findViewById(R.id.coursesSubjectsLV);
		lv_subjects.setOnItemClickListener(this);

		return mView;
	}
	
	/****************************************************/
	protected void getData() {

		final Runnable updateResultsUI = new Runnable() {
			public void run() {
				
				subjects = CoursesDataModel.getSubjectList(courseId);
				
				FullScreenLoader loader = (FullScreenLoader) mView.findViewById(R.id.coursesSubjectsLoader);
				
				if(subjects == null) {
					loader.showError();
					Toast.makeText(mContext, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_LONG).show();
					return;
				}
				
				CoursesArrayAdapter caa = new CoursesArrayAdapter(mContext, R.layout.courses_row, subjects);
				caa.setUseLongFormat(true);

				// we need to tell the slider activity to freeze because
				// because setting the adapter causes it scroll in unpredictable ways
				loader.setVisibility(View.GONE);
				lv_subjects.setVisibility(View.VISIBLE);
				
				SliderActivity sliderActivity = (SliderActivity) mContext;
				sliderActivity.freezeScroll();
				lv_subjects.setAdapter(caa);
				sliderActivity.unfreezeScroll();

				lv_subjects.setOnItemClickListener(CourseSubjectView.this);
				//lv_subjects.setOnLongClickListener(CourseSubjectView.this);  // TODO
				
			}
		};
		
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				post(updateResultsUI);
			}
		};
	
	

		CoursesDataModel.fetchSubjectList(myHandler,courseId);
		
		
	}
	/****************************************************/
	@Override
	public void updateView() {
		
	}
	
	/****************************************************/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
		MITCoursesDetailsSliderActivity.launchActivity(mContext, subjects.get(position), false, courseId);		
	}
	
	private boolean mHasBeenSelected = false;
	@Override
	public void onSelected() {
		if(!mHasBeenSelected) {
			mHasBeenSelected = true;
			getData();
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		
		longClickPos = lv_subjects.getPositionForView(v);
		
		if (longClickPos<0) return false;
		
		mContext.showDialog(0);
		
		return false;
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
