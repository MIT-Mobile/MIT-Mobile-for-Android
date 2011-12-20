package edu.mit.mitmobile2.libraries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.classes.FineData;
import edu.mit.mitmobile2.classes.HoldData;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.objs.FineListItem;
import edu.mit.mitmobile2.objs.HoldListItem;
import edu.mit.mitmobile2.objs.LoanListItem;
import edu.mit.mitmobile2.objs.RenewResponseItem;

public class LibraryYourAccount extends ModuleActivity {

	private static final String TAG = "LibraryYourAccount"; 
	private static final int LOANS_TAB = 0;
	private static final int FINES_TAB = 1;
	private static final int HOLDS_TAB = 2;
	private int currentTab = 0;
	
	protected TitleBar mTitleBar;
	protected TabHost tabHost;	
	protected Activity mActivity;		
	protected int ADD_NEW_TAB = Menu.FIRST;
	protected TabHost.TabSpec spec;
	protected int displayWidth;
	
	
	// Loan Properties
	private View mLoanResults;
    private ListView loanListView;
    private FullScreenLoader loanLoadingView;
    private RelativeLayout loansButtonRow;
    private RelativeLayout renewButtonRow;
    private RelativeLayout doneButtonRow;
    private TextView loanTitleTV;
	private TextView loanAuthorTV;
	private TextView loanStatusTV;
	private ImageView loanStatusIcon;
	private LinearLayout renewStatusError;
	private LinearLayout renewStatusSuccess;
	private TextView renewStatusErrorTV;
	private TextView renewStatusSuccessTV;
	private Button loanRenewBooksButton;
	private Button loanRenewSelectedBooksButton;
	private Button loanCancelRenewBooksButton;
	private Button loanDoneButton;
	private static String REPLY_OK = "ok";
	private static int mode = 1;  // determines whether the activity is in "loan" mode or "renew" mode

	public static final int LOAN_MODE = 1;
	public static final int RENEW_MODE = 2;
	public static final int RENEW_RESULTS_MODE = 3;

	private CheckBox cb;

	static LoanData loanData;
	
	LibraryLoanAdapter libraryLoanAdapter;

    // Fine Properties
    static FineData fineData;
    private View mFineResults;
    private TextView fineBalanceTV;
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
	private ImageView holdStatusIcon;

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
		setMode(LibraryYourAccount.LOAN_MODE);
		
		setUpViews();
		setUpButtons();
		setUpTabs();
		
    }

	// SET UP VIEWS
	private void setUpViews() {
		mTitleBar = (TitleBar) findViewById(R.id.librariesYourAccountTitleBar);
		mTitleBar.setTitle("Your Account");
		
        mLoanResults = (View) findViewById(R.id.loanResults);
        loanStatusTV = (TextView) findViewById(R.id.loanStatusTV);
        renewStatusError = (LinearLayout) findViewById(R.id.renewStatusError);
        renewStatusSuccess = (LinearLayout) findViewById(R.id.renewStatusSuccess);
        renewStatusErrorTV = (TextView) findViewById(R.id.renewStatusErrorTV);
        renewStatusSuccessTV = (TextView) findViewById(R.id.renewStatusSuccessTV);
        loanListView = (ListView) findViewById(R.id.listLibraryLoans);
        loanLoadingView = (FullScreenLoader) findViewById(R.id.libraryLoanLoading);
        loansButtonRow = (RelativeLayout) findViewById(R.id.loansButtonRow);
        renewButtonRow = (RelativeLayout) findViewById(R.id.renewButtonRow);
        doneButtonRow = (RelativeLayout) findViewById(R.id.doneButtonRow);
        
        fineBalanceTV = (TextView) findViewById(R.id.fineBalanceTV);
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
				for (int l = 0; l < LibraryYourAccount.getLoanData().getLoans().size(); l++) {
					
				}
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
				showHideRenewBooks();			
    		}
		});
		
	}
	
    private void showHideRenewBooks() {
    	int currentMode = LibraryYourAccount.getMode();

    	Log.d(TAG,"currentMode = " + currentMode);
    	
    	if (currentMode == LibraryYourAccount.LOAN_MODE) {
    		loansButtonRow.setVisibility(View.GONE);
    		renewButtonRow.setVisibility(View.VISIBLE);
			// Hide tabs
			tabHost.getTabWidget().setVisibility(View.GONE);
			mTitleBar.setTitle("Renew");
			
			LibraryYourAccount.setMode(LibraryYourAccount.RENEW_MODE);
    	}
    	else {
    		doneButtonRow.setVisibility(View.GONE);
    		renewButtonRow.setVisibility(View.GONE);
    		loansButtonRow.setVisibility(View.VISIBLE);
			
			// Show Tabs
			tabHost.getTabWidget().setVisibility(View.VISIBLE);
			mTitleBar.setTitle("Your Account");
			
			if (currentMode == LibraryYourAccount.RENEW_RESULTS_MODE) {
				loanListView.setAdapter(libraryLoanAdapter);
				libraryLoanAdapter.setLookupHandler(loanListView, null);
			}
			
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
			//if (!loggedIn) {
			//	Log.d(TAG,"before log in");
			//	LibraryModel.getUserIdentity(mActivity, loginUiHandler);
			//	Log.d(TAG,"after log in");
			//}
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

                LoanData loanData = (LoanData)msg.obj;
                LibraryYourAccount.setLoanData((LoanData)msg.obj);
                String loanOverdueText = "";
                if (loanData.getNumOverdue() == 1) {
                	loanOverdueText = "\n" + loanData.getNumOverdue() + " is overdue.";
                } else {
                	loanOverdueText = "\n" + loanData.getNumOverdue() + " are overdue.";
                }
                loanStatusTV.setText("You have " + loanData.getNumLoan() + " items on loan." + loanOverdueText);
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
            setMode(RENEW_RESULTS_MODE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");

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
            	
            	// Set renew errors messages
            	if (numErrors > 0) {
            		renewStatusError.setVisibility(View.VISIBLE);
                	renewStatusErrorTV.setText(numErrors + " could not be renewed");            		            		
            	}
            	else {
            		renewStatusError.setVisibility(View.GONE);            		
            	}

            	// Set renew success messages
               	if (numSuccess > 0) {
            		renewStatusSuccess.setVisibility(View.VISIBLE);
                	if (numSuccess > 1) {
                    	renewStatusSuccessTV.setText(numSuccess + " items were renewed");            		            		
                	}
                	else {
                    	renewStatusSuccessTV.setText(numSuccess + " item was renewed");            		            		            		
                	}
            	}
               	else {
            		renewStatusSuccess.setVisibility(View.GONE);               		
               	}
               	
            	renewButtonRow.setVisibility(View.GONE);
            	doneButtonRow.setVisibility(View.VISIBLE);
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

                FineData fineData = (FineData)msg.obj;
                LibraryFines.setFineData((FineData)msg.obj);
                
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                fineBalanceTV.setText("Balance as of " + dateFormat.format(date) + ": " + fineData.getBalance()); 
                fineStatusTV.setText("Payable at any MIT library service desk.\nTechCASH accepted only at Hayden Library.");
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

                HoldData holdData = (HoldData)msg.obj;
                LibraryYourAccount.setHoldData((HoldData)msg.obj);
             
                String holdReadyString = "";
                if (holdData.getNumReady() == 1) {
                	holdReadyString = "\n" + holdData.getNumReady() + " is ready for pickup.";
                } else {
                	holdReadyString = "\n" + holdData.getNumReady() + " are ready for pickup.";
                }
                holdStatusTV.setText("You have " + holdData.getNumRequest() + " hold requests." + holdReadyString);
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

        public LibraryLoanAdapter(List<LoanListItem> items) {
            super(LibraryYourAccount.this, items, R.layout.library_loan_action_row);
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

        	// Status + Icon
        	loanStatusTV = (TextView)view.findViewById(R.id.loanStatusTV);
        	loanStatusIcon = (ImageView)view.findViewById(R.id.loanStatusIcon);
        	String statusText = "";
        	if (item.isHasHold()) {
        		statusText = "Item has holds\n"; 
        	}
        	statusText += Html.fromHtml(item.getDueText());
        	loanStatusTV.setText(statusText);
        	if (item.isOverdue() || item.isLongOverdue()) {
        		loanStatusTV.setTextColor(Color.RED);
        		loanStatusIcon.setImageResource(R.drawable.status_alert);
        		loanStatusIcon.setVisibility(View.VISIBLE);
        	}
        	else {
        		loanStatusTV.setTextColor(getResources().getColor(R.color.libraries_gray));
        		loanStatusIcon.setVisibility(View.GONE);
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

        public LibraryFineAdapter(ArrayList<FineListItem> results) {
            super(LibraryYourAccount.this, results, R.layout.library_fine_action_row);
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

        public LibraryHoldAdapter(List<HoldListItem> items) {
            super(LibraryYourAccount.this, items, R.layout.library_hold_action_row);
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

        	// Status Icon
    		holdStatusIcon = (ImageView)view.findViewById(R.id.holdStatusIcon);
    	
        	// Status
    		holdStatusTV = (TextView)view.findViewById(R.id.holdStatusTV);
        	if (!item.getStatus().equalsIgnoreCase("")) {
        		holdStatusTV.setText(item.getStatus());
        		holdStatusTV.setVisibility(View.VISIBLE);
        	}
        	else {
        		holdStatusTV.setVisibility(View.GONE);
        	}

        	// Pickup Location
        	holdPickupLocationTV = (TextView)view.findViewById(R.id.holdPickupLocationTV);
        	if (!item.getPickupLocation().equalsIgnoreCase("")) {
        		holdPickupLocationTV.setText("Pick up at " + item.getPickupLocation());
        		holdPickupLocationTV.setVisibility(View.VISIBLE);
        	}
        	else {
        		holdPickupLocationTV.setVisibility(View.GONE);
        	}
        	
        	// Set color and icon based on Status value
        	if (item.getReady().equalsIgnoreCase("TRUE")) {
        		Log.d(TAG,"ready for " + item.getTitle() + " = " + item.getReady());
        		holdStatusIcon.setVisibility(View.VISIBLE);
        		int holdReadyTextColor = getResources().getColor(R.color.hold_ready_text);
        		holdStatusTV.setTextColor(holdReadyTextColor);
        		holdPickupLocationTV.setTextColor(holdReadyTextColor);
        		holdPickupLocationTV.setVisibility(View.VISIBLE);
        		Log.d(TAG,"setting status to green, " + R.color.hold_ready_text);
        		Log.d(TAG,"holdStatusTV = " + holdStatusTV.getCurrentTextColor());
        	}
        	else {
        		holdStatusIcon.setVisibility(View.GONE);        		
        		holdStatusTV.setTextColor(getResources().getColor(R.color.libraries_gray));
        		holdPickupLocationTV.setTextColor(getResources().getColor(R.color.libraries_gray));
        		holdPickupLocationTV.setVisibility(View.GONE);
        	}
        }

    }   
    
    
    // Library Renew Book Adapter
    private class LibraryRenewBookAdapter extends SimpleArrayAdapter<RenewResponseItem> {
        public LibraryRenewBookAdapter(List<RenewResponseItem> items) {
            super(LibraryYourAccount.this, items, R.layout.library_loan_action_row);
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
        	loanStatusIcon = (ImageView)view.findViewById(R.id.loanStatusIcon);
        	loanStatusIcon.setImageResource(R.drawable.status_alert);
        	loanStatusTV = (TextView)view.findViewById(R.id.loanStatusTV);
        	loanStatusTV.setText(Html.fromHtml(item.getDueText()));
        	if (item.isOverdue() || item.isLongOverdue()) {
        		loanStatusTV.setTextColor(Color.RED);
        		loanStatusIcon.setVisibility(View.VISIBLE);
        	}
        	else {
        		loanStatusTV.setTextColor(R.color.contents_text);
        		loanStatusIcon.setVisibility(View.GONE);
        	}
        	
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (getMode() == RENEW_MODE || getMode() == RENEW_RESULTS_MODE) {
    		if (loanLoadingView.getVisibility() == View.GONE) {
    			showHideRenewBooks();
    		}
    		return true;
    	}
    	
    	return super.onKeyDown(keyCode, event);
    }
    
	@Override
	protected Module getModule() {
		// TODO Auto-generated method stub
		return new LibrariesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}

    // END ARRAY ADAPTERS
}