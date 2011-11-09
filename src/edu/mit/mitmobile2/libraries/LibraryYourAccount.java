package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.classes.FineData;
import edu.mit.mitmobile2.classes.HoldData;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.objs.FineListItem;
import edu.mit.mitmobile2.objs.HoldListItem;
import edu.mit.mitmobile2.objs.LoanListItem;
import edu.mit.mitmobile2.objs.RenewResponseItem;

public class LibraryYourAccount extends Activity {

	private static final String TAG = "LibraryYourAccount"; 
	private static final int LOANS_TAB = 0;
	private static final int FINES_TAB = 1;
	private static final int HOLDS_TAB = 2;
	private int currentTab = 0;
	
	protected TabHost tabHost;	
	protected Activity mActivity;		
	protected int ADD_NEW_TAB = Menu.FIRST;
	protected TabHost.TabSpec spec;
	protected int displayWidth;
	
	
	// Loan Properties
	private View mLoanResults;
    private ListView loanListView;
    private FullScreenLoader loanLoadingView;
    private TextView loanTitleTV;
	private TextView loanAuthorTV;
	private TextView loanStatusTV;
	private Button loanRenewBooksButton;
	private Button loanRenewSelectedBooksButton;
	private Button loanCancelRenewBooksButton;
	private Button loanDoneButton;
	private static String REPLY_OK = "ok";
	private static int mode = 1;  // determines whether the activity is in "loan" mode or "renew" mode

	public static final int LOAN_MODE = 1;
	public static final int RENEW_MODE = 2;

	private CheckBox cb;

	static LoanData loanData;
	
	LibraryLoanAdapter libraryLoanAdapter;

    // Fine Properties
    static FineData fineData;
    private View mFineResults;
    private TextView fineStatusTV;
    private TextView fineDisplayAmountTV;
    private TextView fineTitleTV;
    private TextView fineAuthorTV;
    private TextView fineFineDateTV;
    private ListView fineListView;
    private FullScreenLoader fineLoadingView;

    // Hold Properties
    private ListView holdListView;
    private FullScreenLoader holdLoadingView;
    private View mHoldResults;
    private TextView holdTitleTV;
	private TextView holdAuthorTV;
	private TextView holdStatusTV;
	private TextView holdPickupLocationTV;
	static HoldData holdData;
	 
	// Getters and Setters
    public static int getMode() {
		return mode;
	}

	public static void setMode(int mode) {
		LibraryYourAccount.mode = mode;
	}

    public static LoanData getLoanData() {
		return loanData;
	}

	public static void setLoanData(LoanData loanData) {
		LibraryYourAccount.loanData = loanData;
	}

	public static HoldData getHoldData() {
		return holdData;
	}

	public static void setHoldData(HoldData holdData) {
		LibraryHolds.holdData = holdData;
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mActivity = this;
		setContentView(R.layout.your_account_tab_layout);
		
		setUpViews();
		setUpButtons();
		setUpTabs();
		
    }

	// SET UP VIEWS
	private void setUpViews() {
        mLoanResults = (View) findViewById(R.id.loanResults);
        loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);
        loanListView = (ListView) findViewById(R.id.listLibraryLoans);
        loanLoadingView = (FullScreenLoader) findViewById(R.id.libraryLoanLoading);

        fineStatusTV = (TextView) findViewById(R.id.fineStatusTV);
        mFineResults = (View) findViewById(R.id.fineResults);
        fineListView = (ListView) findViewById(R.id.listLibraryFines);
        fineLoadingView = (FullScreenLoader) findViewById(R.id.libraryFineLoading);
        
        holdStatusTV = (TextView) findViewById(R.id.holdStatusTV);
        mHoldResults = (View) findViewById(R.id.holdResults);
        holdListView = (ListView) findViewById(R.id.listLibraryHolds);
        holdLoadingView = (FullScreenLoader) findViewById(R.id.libraryHoldLoading);

        
	}
	
	// SET UP TABS
	private void setUpTabs() {
		tabHost = (TabHost)findViewById(R.id.tabHost);  
		tabHost.setup();  // NEEDED!!!
		
		TabConfigurator tabConfigurator = new TabConfigurator(mActivity, tabHost);
		tabConfigurator.addTab("Loans",R.id.tabLoans);
		tabConfigurator.addTab("Fines",R.id.tabFines);
		tabConfigurator.addTab("Holds",R.id.tabHolds);
		
		tabConfigurator.configureTabs();
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				currentTab = tabHost.getCurrentTab();
				switch (currentTab) {
					case LOANS_TAB:
						loadLoans();
					break;	

					case FINES_TAB:
						loadFines();
					break;	

					case HOLDS_TAB:
						loadHolds();
					break;	
				}
			}
		});

		// When the tabs are first created load the loans data 
		currentTab = tabHost.getCurrentTab();
		if (currentTab == LOANS_TAB) {
				loadLoans();
		}
	}
	
	// SET UP BUTTONS
	private void setUpButtons() {

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
				LibraryYourAccount.setMode(LibraryYourAccount.LOAN_MODE);
				Intent intent = new Intent(mActivity, LibraryYourAccount.class);
    			startActivity(intent);			}
		});
		
	}
	
    private void showHideRenewBooks() {
    	int currentMode = LibraryYourAccount.getMode();
	    loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);

    	Log.d(TAG,"currentMode = " + currentMode);
    	
    	if (currentMode == LibraryYourAccount.LOAN_MODE) {
    		loanStatusTV.setVisibility(View.GONE);
    		loanRenewBooksButton.setVisibility(View.GONE);
			loanRenewSelectedBooksButton.setVisibility(View.VISIBLE);
			loanCancelRenewBooksButton.setVisibility(View.VISIBLE); 
			
			// Hide tabs
			tabHost.getTabWidget().setVisibility(View.GONE);

			LibraryYourAccount.setMode(LibraryYourAccount.RENEW_MODE);
    	}
    	else {
    		loanStatusTV.setVisibility(View.VISIBLE);
    		loanRenewBooksButton.setVisibility(View.VISIBLE);
			loanRenewSelectedBooksButton.setVisibility(View.GONE);
			loanCancelRenewBooksButton.setVisibility(View.GONE);
			
			// Show Tabs
			tabHost.getTabWidget().setVisibility(View.VISIBLE);

			LibraryYourAccount.setMode(LibraryYourAccount.LOAN_MODE);
    	}
    	Log.d(TAG,"mode now = " + LibraryYourAccount.getMode());
		libraryLoanAdapter.notifyDataSetChanged();
    }

    // Renew Selected Books
    private void renewSelectedBooks() {
    	String barcodes = "";
    	for (int b = 0; b < LibraryYourAccount.getLoanData().getLoans().size(); b++) {
    		LoanListItem book =  LibraryYourAccount.getLoanData().getLoans().get(b);
    		if (book.isRenewBook()) {
    			if (barcodes.length() > 0) {
    				barcodes = barcodes + " ";
    			}
    			barcodes = barcodes + book.getBarcode();    			  
    		}
    	}
        mLoanResults.setVisibility(View.GONE);
        loanLoadingView.setVisibility(View.VISIBLE);
        loanLoadingView.showLoading();
    	LibraryModel.renewBook(this, renewBooksHandler, barcodes);
    }

	
	// TAB LOADING METHODS
	public void loadLoans() {
	        mLoanResults.setVisibility(View.GONE);
	        loanLoadingView.setVisibility(View.VISIBLE);
	        loanLoadingView.showLoading();
	        LibraryModel.fetchLoanDetail(this, loansUiHandler);
	}
	
	public void loadFines() {
    	mFineResults.setVisibility(View.GONE);
        fineLoadingView.setVisibility(View.VISIBLE);
        fineLoadingView.showLoading();

        LibraryModel.fetchFineDetail(this, finesUiHandler);		
	}

	public void loadHolds() {
        mHoldResults.setVisibility(View.GONE);
        holdLoadingView.setVisibility(View.VISIBLE);
        holdLoadingView.showLoading();

        LibraryModel.fetchHoldDetail(this, holdsUiHandler);
	}
	// END TAB LOADING METHODS

	
	// UI HANDLERS
    private Handler loansUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            loanLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
                LoanData loanData = (LoanData)msg.obj;
                LibraryYourAccount.setLoanData((LoanData)msg.obj);
                loanStatusTV.setText("You have " + loanData.getNumLoan() + " items on loan." + loanData.getNumOverdue() + " overdue.");
                final ArrayList<LoanListItem> results = loanData.getLoans();

                if (results.size() == 0) {
                    Toast.makeText(LibraryYourAccount.this, "No loans found", Toast.LENGTH_SHORT).show();
                }
                
                libraryLoanAdapter  = new LibraryLoanAdapter(results);
                loanListView.setAdapter(libraryLoanAdapter);
                libraryLoanAdapter.setLookupHandler(loanListView, null);
                mLoanResults.setVisibility(View.VISIBLE);
            } 
            else if (msg.arg1 == MobileWebApi.ERROR) {
                loanLoadingView.showError();
            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {
                loanLoadingView.showError();
            }
        }
    };
    
    private Handler renewBooksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            loanLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
            	RenewBookResponse renewBookResponse = (RenewBookResponse)msg.obj;
                final ArrayList<RenewResponseItem> results = renewBookResponse.getRenewResponse();
                LibraryRenewBookAdapter adapter = new LibraryRenewBookAdapter(results);
                loanListView.setAdapter(adapter);
                adapter.setLookupHandler(loanListView, null);
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
            		if (book.getReply().equalsIgnoreCase(LibraryYourAccount.REPLY_OK)) {
            			numSuccess++;
            		}
            		if (book.getErrorMsg().length() > 0) {
            			numErrors++;
            		}
            	}

            	loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);
                loanStatusTV.setVisibility(View.VISIBLE);
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
                loanLoadingView.showError();
            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {
                loanLoadingView.showError();
            }
        }
    };
    
    private Handler finesUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            fineLoadingView.setVisibility(View.GONE);

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
                    Toast.makeText(LibraryYourAccount.this, "No holds found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryFineAdapter adapter = new LibraryFineAdapter(results);
                fineListView.setAdapter(adapter);
                adapter.setLookupHandler(fineListView, null);
            	mFineResults.setVisibility(View.VISIBLE);

            } else if (msg.arg1 == MobileWebApi.ERROR) {
                fineLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                fineLoadingView.showError();
            }
        }
    };

    private Handler holdsUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
            holdLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
                @SuppressWarnings("unchecked")
                HoldData holdData = (HoldData)msg.obj;
                LibraryYourAccount.setHoldData((HoldData)msg.obj);
             
                holdStatusTV.setText("You have " + holdData.getNumRequest() + " hold requests." + holdData.getNumReady() + " for pickup.");
                final ArrayList<HoldListItem> results = holdData.getHolds();

                if (results.size() == 0) {
                    Toast.makeText(LibraryYourAccount.this, "No holds found", Toast.LENGTH_SHORT).show();
                }
                
                
                LibraryHoldAdapter adapter = new LibraryHoldAdapter(results);
                holdListView.setAdapter(adapter);
                adapter.setLookupHandler(holdListView, null);
                mHoldResults.setVisibility(View.VISIBLE);
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                holdLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                holdLoadingView.showError();
            }
        }
    };
	// END UI HANDLERS


    // ARRAY ADAPTERS
    
    // LIBRARY LOANS ADAPTER
    private class LibraryLoanAdapter extends SimpleArrayAdapter<LoanListItem> {
        private List<LoanListItem> libraryLoanItems;
        public LibraryLoanAdapter(List<LoanListItem> items) {
            super(LibraryYourAccount.this, items, R.layout.library_loan_action_row);
            libraryLoanItems = items;
        }
 
        
        public void setLookupHandler(ListView listView, final String extras) {
            setOnItemClickListener(listView, new SimpleArrayAdapter.OnItemClickListener<LoanListItem>() {
            	@Override
                public void onItemSelected(LoanListItem item) {
            		//
                    Log.d(TAG,item.getTitle() + " clicked");
            		if (LibraryYourAccount.getMode() == LibraryYourAccount.LOAN_MODE) {
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
        	
        	Log.d(TAG,"update view - loan mode = " + LibraryYourAccount.getMode());
        	if (LibraryYourAccount.getMode() == LibraryYourAccount.RENEW_MODE) {
        		cb.setVisibility(View.VISIBLE);
        	}
        	else {
        		cb.setVisibility(View.GONE);        		
        	}
        	
        }
    }


    // LibraryFineAdapter
    private class LibraryFineAdapter extends SimpleArrayAdapter<FineListItem> {
        private List<FineListItem> libraryFineItems;
        public LibraryFineAdapter(ArrayList<FineListItem> results) {
            super(LibraryYourAccount.this, results, R.layout.library_fine_action_row);
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

    
    // LIBRARY HOLD ADAPTER
    private class LibraryHoldAdapter extends SimpleArrayAdapter<HoldListItem> {
        private List<HoldListItem> libraryHoldItems;
        public LibraryHoldAdapter(List<HoldListItem> items) {
            super(LibraryYourAccount.this, items, R.layout.library_hold_action_row);
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
    
    
    // Library Renew Book Adapter
    private class LibraryRenewBookAdapter extends SimpleArrayAdapter<RenewResponseItem> {
        private List<RenewResponseItem> libraryRenewItems;
        public LibraryRenewBookAdapter(List<RenewResponseItem> items) {
            super(LibraryYourAccount.this, items, R.layout.library_loan_action_row);
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

    // END ARRAY ADAPTERS
}