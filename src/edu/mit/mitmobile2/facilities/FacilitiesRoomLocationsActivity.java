package edu.mit.mitmobile2.facilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.RoomRecord;


public class FacilitiesRoomLocationsActivity extends ModuleActivity {
	public static final String TAG = "FacilitiesRoomLocationsActivity";
	private static final int MENU_INFO = 0;

	Context mContext;
	ListView mListView;
	final FacilitiesDB db = FacilitiesDB.getInstance(this);
	FullScreenLoader mLoader;
	
	Handler mFacilitiesLoadedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == FacilitiesDB.STATUS_ROOMS_SUCCESSFUL) {
				Log.d(TAG,"received success message for locations, launching next activity");				
				RoomAdapter adapter = new RoomAdapter(FacilitiesRoomLocationsActivity.this, db.getRoomCursor());
				ListView listView = (ListView) findViewById(R.id.facilitiesRoomsForLocationListView);
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
				
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						Cursor cursor = (Cursor)parent.getItemAtPosition(position);

						String room = cursor.getString(3);
						Global.sharedData.getFacilitiesData().setBuildingRoomName(room);
						Intent intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);
						startActivity(intent);          
					}
				});
				
				mLoader.setVisibility(View.GONE);
			}
			else {
				mLoader.showError();
			}
		}		
	};
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		
		mContext = this;

		createViews();

	}

	public void createViews() {
        setContentView(R.layout.facilities_rooms_for_location);
		mLoader = (FullScreenLoader) findViewById(R.id.facilitiesLoader);
		
		mLoader.showLoading();
		new DatabaseUpdater().execute(""); 
		
		// Outside
		TwoLineActionRow outsideLocationActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesOutsideLocationActionRow);
		outsideLocationActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Global.sharedData.getFacilitiesData().setBuildingRoomName("outside");
				Intent intent;
				intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);					
				startActivity(intent);
			}
		});

	}

	private class DatabaseUpdater extends AsyncTask<String, Void, String> {
		
	    ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... msg) {
			// Executed in worker thread
			String result = "";
			try {
				String lastUpdated = FacilitiesDB.getLocationLastUpdated(Global.sharedData.getFacilitiesData().getLocationId());
				if (lastUpdated == null || lastUpdated.equals("")) {
					FacilitiesDB.updateRooms(mContext, mFacilitiesLoadedHandler,Global.sharedData.getFacilitiesData().getBuildingNumber() );
				}
				result = "success";
			} catch (Exception e) {
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// Executed in UI thread
		}
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
		case MENU_INFO:
			Intent intent = new Intent(mContext, FacilitiesInfoActivity.class);					
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
	}
	
	
}
	