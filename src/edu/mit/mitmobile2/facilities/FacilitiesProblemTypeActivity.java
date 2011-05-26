package edu.mit.mitmobile2.facilities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.R;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class FacilitiesProblemTypeActivity extends ListActivity {

	public static final String TAG = "FacilitiesProblemTypeActivity";
	String[] problemTypes;
	private Context mContext;	
	ListView mListView;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        this.mContext = this;
        setContentView(R.layout.facilities_problem_type);
        
        // get problem type list
        Resources res = getResources();
		problemTypes = res.getStringArray(R.array.facilities_problem_types);		

        setListAdapter(new ArrayAdapter<String>(this,
            R.layout.simple_row, problemTypes));
	}

    public void onListItemClick(ListView parent, View v,int position, long id) {   
    	Toast.makeText(this, "You have selected " + problemTypes[position],Toast.LENGTH_SHORT).show();
    	Object o =  problemTypes[position];
    	Log.d(TAG,o.getClass().toString());
    	selectProblemType(problemTypes[position]);
    } 
    
	public void selectProblemType(String problem) {		
    	Global.setProblemType(problem);
		Log.d(TAG, "problem = " + problem);
    	Intent i = new Intent(mContext, FacilitiesProblemLocationActivity.class);
		startActivity(i);
	}
}
	

//	private ListView lv;
//	private String problem_types[];
//	Context mContext;
//	ListView mListView;
//	
//	/****************************************************/
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		Log.d(TAG,"onCreate()");
//		super.onCreate(savedInstanceState);
//		mContext = this;
//		Resources res = getResources();
//		problem_types = res.getStringArray(R.array.facilities_problem_types);
//
//		setContentView(R.layout.facilities_problem_type);
//		ArrayAdapter adapter = new ArrayAdapter(mContext, problem_types);
//		setListAdapter(adapter);
//				
//	}
//
//	/****************************************************/
//
//	
//	public void onClick(View v) {
//		Log.d(TAG, "clicked " + v.getId());
//	}
//	
