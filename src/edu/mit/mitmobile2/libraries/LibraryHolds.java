package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.objs.HoldListItem;

public class LibraryHolds extends ModuleActivity  {
	
	public static final String TAG = "LibraryHolds";

    private ListView mListView;
    private FullScreenLoader mLoadingView;
    private View mHoldResults;
    private TextView holdTitleTV;
	private TextView holdAuthorTV;
	private TextView holdStatusTV;
	private TextView holdPickupLocationTV;
    
    Context mContext;
    
    static HoldData holdData;
    
    public static HoldData getHoldData() {
		return holdData;
	}

	public static void setHoldData(HoldData holdData) {
		LibraryHolds.holdData = holdData;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_holds);
        
        holdStatusTV = (TextView) findViewById(R.id.holdStatusTV);
        mHoldResults = (View) findViewById(R.id.holdResults);
        mListView = (ListView) findViewById(R.id.listLibraryHolds);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        doSearch();
    }

	
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        doSearch();
	}

	private void doSearch() {
        mHoldResults.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.fetchHoldDetail(this, uiHandler);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                
                HoldData holdData = (HoldData)msg.obj;
                LibraryHolds.setHoldData((HoldData)msg.obj);
             
                holdStatusTV.setText("You have " + holdData.getNumRequest() + " hold requests." + holdData.getNumReady() + " for pickup.");
                final ArrayList<HoldListItem> results = holdData.getHolds();

                if (results.size() == 0) {
                    Toast.makeText(LibraryHolds.this, "No holds found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryHoldAdapter adapter = new LibraryHoldAdapter(results);
                mListView.setAdapter(adapter);
                adapter.setLookupHandler(mListView, null);
                mHoldResults.setVisibility(View.VISIBLE);
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };

    @Override
    protected Module getModule() {
        return new LibrariesModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {

    }

    private class LibraryHoldAdapter extends SimpleArrayAdapter<HoldListItem> {
        @SuppressWarnings("unused")
		private List<HoldListItem> libraryHoldItems;
        public LibraryHoldAdapter(List<HoldListItem> items) {
            super(LibraryHolds.this, items, R.layout.library_hold_action_row);
            libraryHoldItems = items;
        }

        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<HoldListItem>() {
                @Override
                public void onItemSelected(HoldListItem item) {
            		Intent intent = new Intent(mContext, LibraryHoldDetail.class);
            		intent.putExtra("index", item.getIndex());
    				startActivity(intent);          
                }
            });
        }

        @Override
        public void updateView(HoldListItem item, View view) {
        	Log.d(TAG,"title = " + item.getTitle());
        	Log.d(TAG,"author = " + item.getAuthor());
        	Log.d(TAG,"status = " + item.getStatus());
        	Log.d(TAG,"pickup location = " + item.getPickupLocation());
        	
        	// Title
        	holdTitleTV = (TextView)view.findViewById(R.id.holdTitleTV);

        	if (!item.getTitle().equalsIgnoreCase("")) {
        		holdTitleTV.setText(item.getTitle());
        	}
        	else {
        		holdTitleTV.setVisibility(View.GONE);
        	}
        	// Year + Author
        	holdAuthorTV = (TextView)view.findViewById(R.id.holdAuthorTV);
        	if (!item.getAuthor().equalsIgnoreCase("")) {
        		holdAuthorTV.setText(item.getYear() + "; " + item.getAuthor());
        	}
        	else {
        		holdAuthorTV.setVisibility(View.GONE);
        	}

        	// Status
        	holdStatusTV = (TextView)view.findViewById(R.id.holdStatusTV);
        	if (!item.getStatus().equalsIgnoreCase("")) {
        		holdStatusTV.setText(item.getStatus());
        	}
        	else {
        		holdStatusTV.setVisibility(View.GONE);
        	}

        	// Pickup Location
        	holdPickupLocationTV = (TextView)view.findViewById(R.id.holdPickupLocationTV);
        	if (!item.getPickupLocation().equalsIgnoreCase("")) {
        		holdPickupLocationTV.setText("Pick up at " + item.getPickupLocation());
        	}
        	else {
        		holdPickupLocationTV.setVisibility(View.GONE);
        	}
        }

    }

}
