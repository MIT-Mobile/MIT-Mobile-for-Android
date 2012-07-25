package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class FacilitiesProblemTypeActivity extends ModuleActivity {

	public static final String TAG = "FacilitiesProblemTypeActivity";

	private Context mContext;	
	final FacilitiesDB db = FacilitiesDB.getInstance(this);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        this.mContext = this;        
        createViews();
	}

	public void createViews() {
	
        setContentView(R.layout.facilities_problem_type);

		ProblemTypeAdapter adapter = new ProblemTypeAdapter(FacilitiesProblemTypeActivity.this, db.getProblemTypeCursor());
		ListView listView = (ListView) findViewById(R.id.facilitiesProblemTypeListView);
		listView.setAdapter(adapter);
		listView.setVisibility(View.VISIBLE);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Cursor cursor = (Cursor)parent.getItemAtPosition(position);
				String problemType = cursor.getString(1);
				Global.sharedData.getFacilitiesData().setProblemType(problemType);
				Intent intent = new Intent(mContext, FacilitiesDetailsActivity.class);
				startActivity(intent);          
			}
		});

		
	}	

	@Override
	protected Module getModule() {
		return new FacilitiesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
	}
}
	
