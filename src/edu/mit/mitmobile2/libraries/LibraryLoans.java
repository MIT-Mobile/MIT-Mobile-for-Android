package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import edu.mit.mitmobile2.MobileWebApi.HttpClientType;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.facilities.FacilitiesDetailsActivity;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryLoans extends ModuleActivity  {
	
	public static final String TAG = "LibraryLoans";

    private ListView mListView;
    private FullScreenLoader mLoadingView;
    static LoanData loanData;
    
    public static LoanData getLoanData() {
		return loanData;
	}

	public static void setLoanData(LoanData loanData) {
		LibraryLoans.loanData = loanData;
	}

	private TextView loanStatusTV;
    Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_loans);
        
        loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);
        mListView = (ListView) findViewById(R.id.listLibraryLoans);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        doSearch();
    }

    private void doSearch() {
        mListView.setVisibility(View.GONE);

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.fetchLoanDetail(this, uiHandler);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
                LoanData loanData = (LoanData)msg.obj;
                LibraryLoans.setLoanData((LoanData)msg.obj);
                loanStatusTV.setText("You have " + loanData.getNumLoan() + " items on loan." + loanData.getNumOverdue() + " overdue.");
                final ArrayList<LoanListItem> results = loanData.getLoans();

                if (results.size() == 0) {
                    Toast.makeText(LibraryLoans.this, "No loans found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryLoanAdapter adapter = new LibraryLoanAdapter(results);
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

    private class LibraryLoanAdapter extends SimpleArrayAdapter<LoanListItem> {
        private List<LoanListItem> libraryLoanItems;
        public LibraryLoanAdapter(List<LoanListItem> items) {
            super(LibraryLoans.this, items, R.layout.boring_action_row);
            libraryLoanItems = items;
        }

        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<LoanListItem>() {
                @Override
                public void onItemSelected(LoanListItem item) {
                    Log.d(TAG,item.getTitle() + " clicked");
            		Intent intent = new Intent(mContext, LibraryLoanDetail.class);
            		intent.putExtra("index", item.getIndex());
    				startActivity(intent);          
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

//	public void getLoanData() {
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("module", "libraries");
//		params.put("command", "loans");
//		final ArrayList<LoanListItem> loans = new ArrayList();
//
//    	MobileWebApi api = new MobileWebApi(false, true, "Libraries", mContext, uiHandler,HttpClientType.MIT);
//    	api.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
//                new MobileWebApi.DefaultErrorListener(uiHandler),
//                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
//			@Override
//			public void onResponse(JSONObject obj) {
//
//				Log.d(TAG,"on response");
//				Log.d(TAG,"obj = " + obj);
//				try {
//					Log.d(TAG,"total = " + obj.getString("total"));
//					Log.d(TAG,"start = " + obj.getString("start"));
//					Log.d(TAG,"overdue = " + obj.getString("overdue"));
//					//data.setNumLoan( Integer.parseInt(obj.getString("total")));
//					//data.setNumOverdue( Integer.parseInt(obj.getString("overdue")));
//					
//					// convert items into lv
//					JSONArray items = obj.getJSONArray("items");
//					for (int l = 0; l < items.length(); l++) {
//						LoanListItem item = new LoanListItem();
//						JSONObject tmpItem = items.getJSONObject(l);
//						Log.d(TAG,l + "");
//						Log.d(TAG,tmpItem.getString("author"));
//
//						// Author
//						item.setAuthor(tmpItem.optString("author",""));
//						
//						// Doc-Number
//						item.setDocNumber(tmpItem.optString("doc-number",""));
//					
//						// Material
//						item.setMaterial(tmpItem.optString("material",""));
//					
//						// Sub-library
//						item.setSubLibrary(tmpItem.optString("sub-library",""));
//
//						// bar code
//						item.setBarcode(tmpItem.optString("barcode",""));
//
//						// Status
//						item.setStatus(tmpItem.optString("status",""));
//					
//						// Load Date
//						item.setLoanDate(tmpItem.optString("loan-date",""));
//					
//						// Due Date
//						item.setDueDate(tmpItem.optString("due-date",""));
//
//						// Returned Date
//						item.setReturnedDate(tmpItem.optString("returned-date",""));
//
//						// Call No
//						item.setCallNo(tmpItem.optString("call-no",""));
//
//						// Year
//						item.setYear(tmpItem.optString("year",""));
//
//						// Title
//						item.setTitle(tmpItem.optString("title",""));
//
//						// Imprint
//						item.setImprint(tmpItem.optString("imprint",""));
//
//						// ISBN ISSN Display
//						item.setIsbnIssnDisplay(tmpItem.optString("isbn-issn-display",""));
//
//						// ISBN ISSN Type
//						item.setIsbnIssnType(tmpItem.optString("isbn-issn-type",""));
//
//						// Overdue
//						item.setOverdue(tmpItem.optString("overdue","").equalsIgnoreCase("TRUE"));
//
//						// Long Overdue
//						item.setLongOverdue(tmpItem.optString("long-overdue","").equalsIgnoreCase("TRUE"));
//
//						// Display Pending Fine
//						item.setDisplayPendingFine(tmpItem.optString("display-pending-fine",""));
//						
//						// Pending Fine
//						item.setPendingFine(tmpItem.optString("pending-fine",""));
//
//						// Has Hold
//						item.setLongOverdue(tmpItem.optString("has-hold","").equalsIgnoreCase("TRUE"));
//						
//						// Due Text
//						item.setDueText(tmpItem.optString("due-text",""));
//
//
//						//Log.d(TAG,item.title);
//						loans.add(item);
//					}
//				}
//				catch (JSONException e) {
//					Log.d(TAG,"JSONException = " + e.getMessage());
//				}
//					
//				//loanStatus = (SectionHeader)findViewById(R.id.libraryLoanStatus);
//				//loanStatus.setText("You have " + data.getNumLoan() + " on loan. " + data.getNumOverdue() + " overdue.");
//			}
//
//			@Override
//			public void onError() {
//				// TODO Auto-generated method stub
//				super.onError();
//				Log.d(TAG,"on error: requestJSONObject");
//			}
//	});	
//		
//	}

}
