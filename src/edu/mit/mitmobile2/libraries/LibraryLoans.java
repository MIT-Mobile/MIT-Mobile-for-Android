package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.objs.LoanListItem;
import edu.mit.mitmobile2.objs.RenewResponseItem;

public class LibraryLoans extends ModuleActivity  {
	
	public static final String TAG = "LibraryLoans";

	private View mLoanResults;
    private ListView mListView;
    private FullScreenLoader mLoadingView;
    private TextView loanTitleTV;
	private TextView loanAuthorTV;
	private TextView loanStatusTV;
	private Button loanRenewBooksButton;
	private Button loanRenewSelectedBooksButton;
	private Button loanCancelRenewBooksButton;
	private Button loanDoneButton;
	
	private CheckBox cb;
	static LoanData loanData;
    LibraryLoanAdapter libraryLoanAdapter;

	private static String REPLY_OK = "ok";
	private static int mode = 1;  // determines whether the activity is in "loan" mode or "renew" mode

	public static final int LOAN_MODE = 1;
	public static final int RENEW_MODE = 2;
		
    public static LoanData getLoanData() {
		return loanData;
	}

	public static void setLoanData(LoanData loanData) {
		LibraryLoans.loanData = loanData;
	}

	public static int getMode() {
		return mode;
	}

	public static void setMode(int mode) {
		LibraryLoans.mode = mode;
	}

	Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.library_loans);
        
    	mLoanResults = (View) findViewById(R.id.loanResults);
        loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);
        mListView = (ListView) findViewById(R.id.listLibraryLoans);
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        // Set up Renew Books buttons
        loanRenewBooksButton = (Button)findViewById(R.id.loanRenewBooksButton);
        loanRenewSelectedBooksButton = (Button)findViewById(R.id.loanRenewSelectedBooksButton);
        loanCancelRenewBooksButton = (Button)findViewById(R.id.loanCancelRenewBooksButton);
        loanDoneButton = (Button)findViewById(R.id.loanDoneButton);

        loanRenewBooksButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showHideRenewBooks();
				//Intent intent = new Intent(mContext, LibraryRenewBooks.class);
				//startActivity(intent);
			}
		});
		
        loanCancelRenewBooksButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showHideRenewBooks();
				//Intent intent = new Intent(mContext, LibraryRenewBooks.class);
				//startActivity(intent);
			}
		});

        loanRenewSelectedBooksButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				renewSelectedBooks();
			}
		});

        loanDoneButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				LibraryLoans.setMode(LibraryLoans.LOAN_MODE);
				Intent intent = new Intent(mContext, LibraryYourAccount.class);
    			startActivity(intent);			}
		});

        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d(TAG,"view clicked = " + arg1.getClass());
			}
        });
        
        doSearch();
    }


    private void renewSelectedBooks() {
    	String barcodes = "";
    	for (int b = 0; b < LibraryLoans.getLoanData().getLoans().size(); b++) {
    		LoanListItem book =  LibraryLoans.getLoanData().getLoans().get(b);
    		if (book.isRenewBook()) {
    			if (barcodes.length() > 0) {
    				barcodes = barcodes + " ";
    			}
    			barcodes = barcodes + book.getBarcode();    			  
    		}
    	}
        mLoanResults.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();
    	LibraryModel.renewBook(this, renewBooksHandler, barcodes);
    }
    
    private void showHideRenewBooks() {
    	int currentMode = LibraryLoans.getMode();
    	Log.d(TAG,"currentMode = " + currentMode);
    	
    	if (currentMode == LibraryLoans.LOAN_MODE) {
			loanRenewBooksButton.setVisibility(View.GONE);
			loanRenewSelectedBooksButton.setVisibility(View.VISIBLE);
			loanCancelRenewBooksButton.setVisibility(View.VISIBLE); 
			/*
			for(int i = 0; i < mListView.getChildCount(); i++) {
				Log.d(TAG,"index = " + i);
				View lView = (View) mListView.getChildAt(i);
				cb = (CheckBox)lView.findViewById(R.id.renewBookCheckbox);
				cb.setVisibility(View.VISIBLE);			
			}
			*/
			LibraryLoans.setMode(LibraryLoans.RENEW_MODE);
    	}
    	else {
			loanRenewBooksButton.setVisibility(View.VISIBLE);
			loanRenewSelectedBooksButton.setVisibility(View.GONE);
			loanCancelRenewBooksButton.setVisibility(View.GONE);
			/*
			for(int i = 0; i < mListView.getChildCount(); i++) {
				View lView = (View) mListView.getChildAt(i);
				cb = (CheckBox)lView.findViewById(R.id.renewBookCheckbox);
				cb.setVisibility(View.GONE);
			}
			*/
			LibraryLoans.setMode(LibraryLoans.LOAN_MODE);
    	}
    	Log.d(TAG,"mode now = " + LibraryLoans.getMode());
		libraryLoanAdapter.notifyDataSetChanged();
    }

    private void doSearch() {
        mLoanResults.setVisibility(View.GONE);
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
                
                
                libraryLoanAdapter  = new LibraryLoanAdapter(results);
                mListView.setAdapter(libraryLoanAdapter);
                libraryLoanAdapter.setLookupHandler(mListView, null);
                mLoanResults.setVisibility(View.VISIBLE);
            } 
            else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };


    private Handler renewBooksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
        	Log.d(TAG,"renew book message = " + msg.arg1);
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
            	RenewBookResponse renewBookResponse = (RenewBookResponse)msg.obj;
                final ArrayList<RenewResponseItem> results = renewBookResponse.getRenewResponse();
                LibraryRenewBookAdapter adapter = new LibraryRenewBookAdapter(results);
                mListView.setAdapter(adapter);
                adapter.setLookupHandler(mListView, null);
                mLoanResults.setVisibility(View.VISIBLE);
            	int numBooks = 0;
            	int numSuccess = 0;
            	int numErrors = 0;

            	numBooks = renewBookResponse.getRenewResponse().size();
            	Log.d(TAG,"numBooks = " + numBooks);
            	for (int b = 0; b < numBooks; b++) {
            		RenewResponseItem book = (RenewResponseItem)renewBookResponse.getRenewResponse().get(b);
            		Log.d(TAG,"reply = " + book.getReply());
            		Log.d(TAG,"error = " + book.getErrorMsg());
            		Log.d(TAG,"book = " + book.getTitle());
            		if (book.getReply().equalsIgnoreCase(LibraryLoans.REPLY_OK)) {
            			numSuccess++;
            		}
            		if (book.getErrorMsg().length() > 0) {
            			numErrors++;
            		}
            	}

            	loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);
                loanStatusTV.setText(numErrors + " items(s) could not be renewed");            		
                loanStatusTV.setTextColor(Color.RED);
                
                loanRenewBooksButton = (Button) findViewById(R.id.loanRenewBooksButton);
                loanRenewBooksButton.setVisibility(View.GONE);
                
                loanRenewSelectedBooksButton = (Button) findViewById(R.id.loanRenewSelectedBooksButton);
                loanRenewSelectedBooksButton.setVisibility(View.GONE);
                
                loanCancelRenewBooksButton = (Button) findViewById(R.id.loanCancelRenewBooksButton);
                loanCancelRenewBooksButton.setVisibility(View.GONE);

                loanDoneButton = (Button) findViewById(R.id.loanDoneButton);
                loanDoneButton.setVisibility(View.VISIBLE);

                mLoanResults.setVisibility(View.VISIBLE);
            } 
            else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {
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
            super(LibraryLoans.this, items, R.layout.library_loan_action_row);
            libraryLoanItems = items;
        }
 
        
        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<LoanListItem>() {
            	@Override
                public void onItemSelected(LoanListItem item) {
            		//
                    Log.d(TAG,item.getTitle() + " clicked");
            		if (LibraryLoans.getMode() == LibraryLoans.LOAN_MODE) {
            			Intent intent = new Intent(mContext, LibraryLoanDetail.class);
            			intent.putExtra("index", item.getIndex());
            			startActivity(intent);
            		}
            		else {
            			// renew mode, toggle checkbox
            			boolean renewBook = ((LoanListItem)loanData.getLoans().get(item.getIndex())).isRenewBook();
            			((LoanListItem)loanData.getLoans().get(item.getIndex())).setRenewBook(!renewBook);
            			//libraryLoanAdapter  = new LibraryLoanAdapter(loanData.getLoans());
            			libraryLoanAdapter.notifyDataSetChanged();
            		}
                }
            });
        }

		@Override
        public void updateView(LoanListItem item, View view) {
        	        	
        	Log.d(TAG,"update view " + item.getIndex());
        	// Title
        	loanTitleTV = (TextView)view.findViewById(R.id.loanTitleTV);

        	if (!item.getTitle().equalsIgnoreCase("")) {
        		loanTitleTV.setText(item.getTitle());
        	}
        	else {
        		loanTitleTV.setVisibility(View.GONE);
        	}
        	// Year + Author
        	loanAuthorTV = (TextView)view.findViewById(R.id.loanAuthorTV);
        	if (!item.getAuthor().equalsIgnoreCase("") || !item.getYear().equalsIgnoreCase("")) {
        		loanAuthorTV.setText(item.getYear() + "; " + item.getAuthor());
        	}
        	else {
        		loanAuthorTV.setVisibility(View.GONE);
        	}

        	// Status
        	loanStatusTV = (TextView)view.findViewById(R.id.loanStatusTV);
        	loanStatusTV.setText(Html.fromHtml(item.getDueText()));
        	if (item.isOverdue() || item.isLongOverdue()) {
        		loanStatusTV.setTextColor(Color.RED);
        	}
        	else {
        		loanStatusTV.setTextColor(R.color.contents_text);
        	}
        	
        	// Renew Book Checkbox
        	cb  = (CheckBox)view.findViewById(R.id.renewBookCheckbox);
        	cb.setTag(item.getIndex());
        	
        	// Check the box if renewBook is true in the corresponding item
        	if (((LoanListItem)loanData.getLoans().get(item.getIndex())).isRenewBook()) {
        		cb.setChecked(true);
        	}
        	else {
        		cb.setChecked(false);        		
        	}
        	
        	Log.d(TAG,"update view - loan mode = " + LibraryLoans.getMode());
        	if (LibraryLoans.getMode() == LibraryLoans.RENEW_MODE) {
        		cb.setVisibility(View.VISIBLE);
        	}
        	else {
        		cb.setVisibility(View.GONE);        		
        	}
        	
        }
    }


    // Library Renew Book Adapter
    private class LibraryRenewBookAdapter extends SimpleArrayAdapter<RenewResponseItem> {
        private List<RenewResponseItem> libraryRenewItems;
        public LibraryRenewBookAdapter(List<RenewResponseItem> items) {
            super(LibraryLoans.this, items, R.layout.library_loan_action_row);
            libraryRenewItems = items;
        }
 
        
        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<RenewResponseItem>() {
                @Override
                public void onItemSelected(RenewResponseItem item) {
                    Log.d(TAG,item.getTitle() + " clicked");
                }
            });
        }

        @Override
        public void updateView(RenewResponseItem item, View view) {
        	        	
        	// Title
        	loanTitleTV = (TextView)view.findViewById(R.id.loanTitleTV);

        	if (!item.getTitle().equalsIgnoreCase("")) {
        		loanTitleTV.setText(item.getTitle());
        	}
        	else {
        		loanTitleTV.setVisibility(View.GONE);
        	}
        	// Year + Author
        	loanAuthorTV = (TextView)view.findViewById(R.id.loanAuthorTV);
        	if (!item.getAuthor().equalsIgnoreCase("") || !item.getYear().equalsIgnoreCase("")) {
        		loanAuthorTV.setText(item.getYear() + "; " + item.getAuthor());
        	}
        	else {
        		loanAuthorTV.setVisibility(View.GONE);
        	}

        	// Status
        	loanStatusTV = (TextView)view.findViewById(R.id.loanStatusTV);
        	loanStatusTV.setText(Html.fromHtml(item.getDueText()));
        	if (item.isOverdue() || item.isLongOverdue()) {
        		loanStatusTV.setTextColor(Color.RED);
        	}
        	else {
        		loanStatusTV.setTextColor(R.color.contents_text);
        	}
        	
        }
    }

}
