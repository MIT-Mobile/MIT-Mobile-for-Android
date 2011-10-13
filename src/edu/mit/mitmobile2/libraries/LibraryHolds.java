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
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.classes.FineData;
import edu.mit.mitmobile2.classes.HoldData;
import edu.mit.mitmobile2.objs.HoldListItem;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryHolds extends ModuleActivity  {
	
	public static final String TAG = "LibraryHolds";

    private ListView mListView;
    private FullScreenLoader mLoadingView;
    private TextView holdStatusTV;
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
        mListView = (ListView) findViewById(R.id.listLibraryHolds);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        doSearch();
    }

    private void doSearch() {
        mListView.setVisibility(View.GONE);

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
                @SuppressWarnings("unchecked")
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
                mListView.setVisibility(View.VISIBLE);

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
        private List<HoldListItem> libraryHoldItems;
        public LibraryHoldAdapter(List<HoldListItem> items) {
            super(LibraryHolds.this, items, R.layout.boring_action_row);
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
            TwoLineActionRow twoLineActionRow = (TwoLineActionRow) view;
            twoLineActionRow.setTitle(item.getTitle());
            twoLineActionRow.setSubtitle(item.getAuthor());
        }

    }

}
