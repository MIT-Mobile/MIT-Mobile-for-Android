package edu.mit.mitmobile2.classes;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchBar;
import edu.mit.mitmobile2.SectionHeader;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.CourseItem;

public class CoursesTopActivity extends ModuleActivity {

	CoursesArrayAdapter caa;
	
	Context ctx;

	SearchBar mSearchBar;
	
	LinearLayout mMyStellarLL;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		ctx = this;

		CoursesDataModel.getMyStellar(this);
		
		createView();
		
		prefetchAllData();  
		
	}
	
	/****************************************************/
	@Override
	protected void onResume() {
		super.onResume();
		refreshMyStellarList();
	}
	
	/****************************************************/
	void createView() {
	
		setContentView(R.layout.courses);

		mSearchBar = (SearchBar) findViewById(R.id.coursesSearchBar);
		mSearchBar.setSearchHint(getString(R.string.courses_search_hint));  
		mSearchBar.setSystemSearchInvoker(this);
		
		View view;
		OnClickListener l;
		
		
		//LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//mBrowseCourses = inflater.inflate(R.layout.courses_main_footer, null);	
		
		mMyStellarLL = (LinearLayout) findViewById(R.id.coursesMyStellar);
		
		view = findViewById(R.id.courses01TV);
		l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx,MITCoursesListsSliderActivity.class);
				i.putExtra(MITCoursesListsSliderActivity.KEY_GROUP, MITCoursesListsSliderActivity.GROUP_01);
				ctx.startActivity(i);
			}
		};
		view.setOnClickListener(l);

		view = findViewById(R.id.courses11TV);
		l = new OnClickListener() {
			@Override
			public void onClick(View v) {;
				Intent i = new Intent(ctx,MITCoursesListsSliderActivity.class);
				i.putExtra(MITCoursesListsSliderActivity.KEY_GROUP, MITCoursesListsSliderActivity.GROUP_11);
				ctx.startActivity(i);
			}
		};
		view.setOnClickListener(l);

		view = findViewById(R.id.courses21TV);
		l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx,MITCoursesListsSliderActivity.class);
				i.putExtra(MITCoursesListsSliderActivity.KEY_GROUP, MITCoursesListsSliderActivity.GROUP_21);
				ctx.startActivity(i);
			}
		};		
		view.setOnClickListener(l);
		
		view = findViewById(R.id.coursesOtherTV);
		l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx,MITCoursesListsSliderActivity.class);
				i.putExtra(MITCoursesListsSliderActivity.KEY_GROUP, MITCoursesListsSliderActivity.GROUP_OTHER);
				ctx.startActivity(i);
			}
		};
		view.setOnClickListener(l);
		
		refreshMyStellarList();	
	}
	
	/****************************************************/
	protected void prefetchAllData() {			
		final FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.coursesBrowseLoader);
		loader.showLoading();
		
		CoursesDataModel.fetchList(this, new Handler() {
			@Override
			public void handleMessage(Message message) {
				
				if(message.arg1 == MobileWebApi.SUCCESS) {	
					loader.setVisibility(View.GONE);	
					View browseList = findViewById(R.id.coursesBrowseList);
					browseList.setVisibility(View.VISIBLE);
				} else {
					loader.showError();
				}
			}
		});
		
	}
	
	protected void refreshMyStellarList() {
		final ArrayList<CourseItem> myStellar = CoursesDataModel.getFavoritesList();
		mMyStellarLL.removeAllViews();
		if(myStellar.size() > 0) {
			mMyStellarLL.addView(new SectionHeader(this, "My Stellar"));
			for(final CourseItem courseItem : myStellar) {
				TwoLineActionRow row = new TwoLineActionRow(ctx);
				if (courseItem.read) row.setTitle(courseItem.masterId,0xFF000000);
				else row.setTitle(courseItem.masterId,0xFFFF0000);
				mMyStellarLL.addView(row);
				mMyStellarLL.addView(new DividerView(this, null));
				row.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						courseItem.read = true;
						CoursesDataModel.saveMyStellar(ctx);
						Intent intent = new Intent(ctx, MITCoursesDetailsSliderActivity.class);
						intent.putExtra(MITCoursesDetailsSliderActivity.MY_STELLAR_KEY, true);
						intent.putExtra(MITCoursesDetailsSliderActivity.SUBJECT_MASTER_ID_KEY, courseItem.masterId);  
						startActivity(intent);
					}
				});
			}
		}
	}
	
	@Override
	protected Module getModule() {
		return new ClassesModule();
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);
	}
	
}
