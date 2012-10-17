package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.Date;
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
import edu.mit.mitmobile2.objs.FineListItem;

public class LibraryFines extends ModuleActivity  {
	
	public static final String TAG = "LibraryFines";

    private ListView mListView;
    private FullScreenLoader mLoadingView;
    static FineData fineData;
    private View mFineResults;
    private TextView fineStatusTV;
    private TextView fineDisplayAmountTV;
    private TextView fineTitleTV;
    private TextView fineAuthorTV;
    private TextView fineFineDateTV;

    public static FineData getFineData() {
		return fineData;
	}

	public static void setFineData(FineData fineData) {
		LibraryFines.fineData = fineData;
	}

    Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_fines);
        
        fineStatusTV = (TextView) findViewById(R.id.fineStatusTV);
        mFineResults = (View) findViewById(R.id.fineResults);
        mListView = (ListView) findViewById(R.id.listLibraryFines);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        doSearch();
    }

    private void doSearch() {
    	mFineResults.setVisibility(View.GONE);
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
                LibraryFines.setFineData((FineData)msg.obj);
                
                Date date = new Date();
                fineStatusTV.setText("Balance as of " + date.toLocaleString() + ": " + fineData.getBalance() 
                		+ " Payable at any MIT library service desk. TechCASH accepted only at Hayden Library. ");
                final ArrayList<FineListItem> results = fineData.getFines();

                if (results.size() == 0) {
                    Toast.makeText(LibraryFines.this, "No holds found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryFineAdapter adapter = new LibraryFineAdapter(results);
                mListView.setAdapter(adapter);
                adapter.setLookupHandler(mListView, null);
            	mFineResults.setVisibility(View.VISIBLE);

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

    private class LibraryFineAdapter extends SimpleArrayAdapter<FineListItem> {
        private List<FineListItem> libraryFineItems;
        public LibraryFineAdapter(ArrayList<FineListItem> results) {
            super(LibraryFines.this, results, R.layout.library_fine_action_row);
            libraryFineItems = results;
        }

        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<FineListItem>() {
                @Override
                public void onItemSelected(FineListItem item) {
                    Log.d(TAG,item.getTitle() + " clicked");
            		Intent intent = new Intent(mContext, LibraryFineDetail.class);
            		intent.putExtra("index", item.getIndex());
    				startActivity(intent);          
                }
            });
        }

        @Override
        public void updateView(FineListItem item, View view) {

        	// Display Amount
        	fineDisplayAmountTV = (TextView)view.findViewById(R.id.fineDisplayAmountTV);
        	fineDisplayAmountTV.setText(item.getDisplayAmount());

        	// Title
        	fineTitleTV = (TextView)view.findViewById(R.id.fineTitleTV);
        	fineTitleTV.setText(item.getTitle());
        	
        	// Year + Author
        	fineAuthorTV = (TextView)view.findViewById(R.id.fineAuthorTV);
        	if (!item.getAuthor().equalsIgnoreCase("") || !item.getYear().equalsIgnoreCase("")) {
        		fineAuthorTV.setText(item.getYear() + "; " + item.getAuthor());
        	}
        	else {
        		fineAuthorTV.setVisibility(View.GONE);
        	}

        	// Fine Date
        	fineFineDateTV = (TextView)view.findViewById(R.id.fineFineDateTV);
        	fineFineDateTV.setText("Fined " + item.getFineDate());
        }

    }

}
