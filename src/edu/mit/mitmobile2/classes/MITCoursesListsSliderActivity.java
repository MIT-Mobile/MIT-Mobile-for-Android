package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;
import edu.mit.mitmobile2.CategoryNewModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.objs.CourseListItem;

public class MITCoursesListsSliderActivity extends CategoryNewModuleActivity {

	int group;
	
	public static final String KEY_GROUP = "group";
	
	public static final int GROUP_01 = 0;
	public static final int GROUP_11 = 1;
	public static final int GROUP_21 = 2;
	public static final int GROUP_OTHER = 3;
	
	// #0
	//public ArrayList<CourseListItem> unsorted_courses;
	// #1
	public ArrayList<CourseListItem>[] courses;
	// #2
	/*
	public ArrayList<CourseListItem> coursesOther;
	public ArrayList<CourseListItem> courses01;
	public ArrayList<CourseListItem> courses11;
	public ArrayList<CourseListItem> courses21;
	*/
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();
        if (extras!=null){
        	group = extras.getInt(KEY_GROUP,0);
        }

    	setTitle("MIT Courses");

    	sortAllData();
    	
    	createViews();
    	
	}
	/****************************************************/
    void sortAllData() {
    	
    	courses = new ArrayList[4];
    	for (int x=0; x<4; x++) {
    		courses[x] = new ArrayList<CourseListItem>();
    	}
   
    	List<CourseListItem> unsorted_courses = CoursesDataModel.getList();
        
    	if (unsorted_courses==null) {
        	Toast.makeText(MITCoursesListsSliderActivity.this, "Sorry, network error, try again later.", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	// Divide data
    	for (CourseListItem c : unsorted_courses) {
    		
    		try {
    			
    			String test = c.shortStr.replaceAll("[a-zA-Z]", "");
    			Integer i = Integer.valueOf(test);
    			int iii = i.intValue();
    			
    			if (iii<11) {
    				courses[0].add(c);
    			} else if (iii<21) {
    				courses[1].add(c);
    			} else if (iii<31) {
    				courses[2].add(c);
    			} else {
    				courses[3].add(c);
    			}
    			
    		}
    		catch (NumberFormatException ex) {
    			courses[3].add(c);
    		}
    		
    	}
    	
    }
	/****************************************************/
    void createViews() {

    	CourseListView cv;
    	
    	String[] titles = new String[] {"Courses 1-10","Courses 11-20","Courses 21-25","Other Courses"};
    	
    	for (int x=0; x<4; x++) {
    		cv = new CourseListView(this,x,courses[x],titles[x]);
    		addCategory(cv, titles[x], titles[x]);
    	}
    	onOptionItemSelected(titles[group]);
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
