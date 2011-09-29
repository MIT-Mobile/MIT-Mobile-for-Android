package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
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
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryFines extends ModuleActivity  {
	
	public static final String TAG = "LibraryFines";

    private ListView mListView;
    private FullScreenLoader mLoadingView;
    private TextView fineStatusTV;
    Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_fines);
        
        fineStatusTV = (TextView) findViewById(R.id.fineStatusTV);
        mListView = (ListView) findViewById(R.id.listLibraryFines);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        doSearch();
    }

    private void doSearch() {
        mListView.setVisibility(View.GONE);

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.fetchFineDetail(this, uiHandler);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
                FineData fineData = (FineData)msg.obj;
                
                Date date = new Date();
                fineStatusTV.setText("Balance as of " + date.toLocaleString() + ": " + fineData.getBalance() 
                		+ " Payable at any MIT library service desk. TechCASH accepted only at Hayden Library. ");
                final ArrayList<LoanListItem> results = fineData.getHolds();

                if (results.size() == 0) {
                    Toast.makeText(LibraryFines.this, "No holds found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryFineAdapter adapter = new LibraryFineAdapter(results);
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
        return new LibraryModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

    @Override
    protected void prepareActivityOptionsMenu(Menu menu) {

    }

    private class LibraryFineAdapter extends SimpleArrayAdapter<LoanListItem> {
        private List<LoanListItem> libraryFineItems;
        public LibraryFineAdapter(List<LoanListItem> items) {
            super(LibraryFines.this, items, R.layout.boring_action_row);
            libraryFineItems = items;
        }

        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<LoanListItem>() {
                @Override
                public void onItemSelected(LoanListItem item) {
                    Log.d(TAG,item.getTitle() + " clicked");
                	//LibraryDetailActivity.launchActivity(getContext(), libraryItems, libraryItems.indexOf(item));
                }
            });
        }

        @Override
        public void updateView(LoanListItem item, View view) {
            TwoLineActionRow twoLineActionRow = (TwoLineActionRow) view;
            twoLineActionRow.setTitle(item.getTitle());
            twoLineActionRow.setSubtitle(item.getAuthor());
        }

    }

}
