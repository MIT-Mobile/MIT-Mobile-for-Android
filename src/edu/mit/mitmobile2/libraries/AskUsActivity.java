package edu.mit.mitmobile2.libraries;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleSpinnerAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.libraries.LibraryModel.FormResult;
import edu.mit.mitmobile2.libraries.LibraryModel.UserIdentity;
import edu.mit.mitmobile2.libraries.VerifyUserCredentials.VerifyUserCredentialsListener;

public class AskUsActivity extends NewModuleActivity {
	
    private Spinner mTopicSpinner;
    private Spinner mStatusSpinner;

    private EditText mSubjectText;
    private EditText mDetailText;
    private EditText mDepartmentText;
    private EditText mPhoneText;
    private Button mSubmitButton;

    private View mTechHelpSection;
    private RadioGroup mOnCampusRadioGroup;
    private RadioGroup mVPNRadioGroup;
    
	private View mThankYouView;
	private TwoLineActionRow mContentResult;
	private TwoLineActionRow mGoHomeButton;

    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;

    private String[] topicsArray;
    private String[] statusArray;
	private Context mContext;
	public static final String TAG = "AskUsActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_ask_us);
        addSecondaryTitle("Ask Us");
        
        mContext = this;
        
        mScrollView = (LockingScrollView) findViewById(R.id.askUsScrollView);
        
        mTopicSpinner = (Spinner) findViewById(R.id.topicSpinner);
        mTopicSpinner.setOnItemSelectedListener(mUpdateSubmitSpinnerListener);
        mStatusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        mStatusSpinner.setOnItemSelectedListener(mUpdateSubmitSpinnerListener);
        
        mSubjectText = (EditText) findViewById(R.id.subject);
        mSubjectText.addTextChangedListener(mUpdateSubmitButtonTextWatcher);
        mDetailText = (EditText) findViewById(R.id.detailedQuestion);
        mDetailText.addTextChangedListener(mUpdateSubmitButtonTextWatcher);

        mDepartmentText = (EditText) findViewById(R.id.department);
        mDepartmentText.addTextChangedListener(mUpdateSubmitButtonTextWatcher);
        mPhoneText = (EditText) findViewById(R.id.phoneNumber);
        mPhoneText.addTextChangedListener(mUpdateSubmitButtonTextWatcher);
        
        mSubmitButton = (Button) findViewById(R.id.submit);
        mSubmitButton.setEnabled(false);
        
        mThankYouView = findViewById(R.id.libraryAskUsThankYou);
        mContentResult = (TwoLineActionRow) findViewById(R.id.librariesThankYouContentActionRow);
        mGoHomeButton = (TwoLineActionRow) findViewById(R.id.librariesThankYouReturnHome);
        mGoHomeButton.setOnClickListener(new View.OnClickListener() {
			@Override
		    public void onClick(View v) {
				Intent intent = new Intent(mContext, getNewModule().getModuleHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
		    }
		});
        
        mLoader = (FullScreenLoader) findViewById(R.id.askUsLoading);
        
        mTechHelpSection = findViewById(R.id.librariesTechHelpSection);
        mOnCampusRadioGroup = (RadioGroup) findViewById(R.id.librariesOnCampusRadioGroup); 
        mOnCampusRadioGroup.setOnCheckedChangeListener(mUpdateSubmitCheckedChangeListener);
        mVPNRadioGroup = (RadioGroup) findViewById(R.id.librariesVPNRadioGroup);
        mVPNRadioGroup.setOnCheckedChangeListener(mUpdateSubmitCheckedChangeListener);
        
        
        topicsArray = getResources().getStringArray(R.array.libraryTopics);
        String topicsTitle = getResources().getString(R.string.libraryTopicsTitle);
        SpinnerAdapter topicAdapter = new SimpleSpinnerAdapter(this, topicsTitle, Arrays.asList(topicsArray));
        mTopicSpinner.setAdapter(topicAdapter);
        mTopicSpinner.setPrompt(topicsTitle);
        mTopicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == topicsArray.length) {  // this corresponds to the technical help topic
					mTechHelpSection.setVisibility(View.VISIBLE);
				} else {
					mTechHelpSection.setVisibility(View.GONE);
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mTechHelpSection.setVisibility(View.GONE);
			}
        });

        
        statusArray = getResources().getStringArray(R.array.libraryStatus);
        String statusTitle = getResources().getString(R.string.libraryStatusTitle);
        SpinnerAdapter statusAdapter = new SimpleSpinnerAdapter(this, statusTitle, Arrays.asList(statusArray));
        mStatusSpinner.setAdapter(statusAdapter);
        mStatusSpinner.setPrompt(statusTitle);
        
        mSubmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            	imm.hideSoftInputFromWindow(mSubmitButton.getWindowToken(), 0);
            	
                int position = mTopicSpinner.getSelectedItemPosition()-1;
                String topic = topicsArray[position];
                
                String subject = mSubjectText.getText().toString().trim();                
                String question = mDetailText.getText().toString().trim();                
                String phone = mPhoneText.getText().toString().trim();
                
                position = mStatusSpinner.getSelectedItemPosition()-1;
                String[] statusCodeArray = getResources().getStringArray(R.array.libraryStatusCode);
                String status = statusCodeArray[position];
                
                String department = mDepartmentText.getText().toString().trim();
                
                boolean technicalHelp = topic.equals("Technical Help");
                String onCampus = null;
                String usingVPN = null;
                if(technicalHelp) {
                	if(mOnCampusRadioGroup.getCheckedRadioButtonId() == -1) {
                		// nothing selected.
                		mOnCampusRadioGroup.requestFocus();
                		prompt("Must select on or off campus");
                		return;
                	}
                	onCampus = ((RadioButton) findViewById(mOnCampusRadioGroup.getCheckedRadioButtonId()))
                		.getText().toString().toLowerCase();
                	
                	if(mVPNRadioGroup.getCheckedRadioButtonId() == -1) {
                		mVPNRadioGroup.requestFocus();
                		prompt("Must specify if your using VPN");
                		return;
                	}
                	usingVPN = ((RadioButton) findViewById(mVPNRadioGroup.getCheckedRadioButtonId()))
    					.getText().toString().toLowerCase();
                }
                
                
                showLoading();
                
                LibraryModel.sendAskUsInfo(AskUsActivity.this, uiHandler, topic, status, department, subject, question, phone, usingVPN, onCampus, "form");
            }
        });
        
        showLoading();
        VerifyUserCredentials.VerifyUserHasFormAccess(this, new VerifyUserCredentialsListener() {
			@Override
			public void onUserLoggedIn(UserIdentity user) {
				showForm();
			}
        });
        
    }
    
    TextWatcher mUpdateSubmitButtonTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			mSubmitButton.setEnabled(formValidates());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) { }
    	
    };
    
    OnItemSelectedListener mUpdateSubmitSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View row, int position, long id) {
			mSubmitButton.setEnabled(formValidates());
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) { }
		
    };
    
    OnCheckedChangeListener mUpdateSubmitCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int position) {
			mSubmitButton.setEnabled(formValidates());
		}	
    };
    
    private boolean formValidates() {
    	Spinner[] spinners = new Spinner[] {mTopicSpinner, mStatusSpinner};
    	for (int i=0; i < spinners.length; i++) {
    		if (spinners[i].getSelectedItemPosition() < 1) {
    			return false;
    		}
    	}
    	
    	EditText[] editTexts = new EditText[] {mSubjectText, mDetailText, mDepartmentText};
    	for (int i=0; i < editTexts.length; i++) {
    		if (editTexts[i].getText().toString().trim().length() == 0) {
    			return false;
    		}
    	}
    	
    	
    	int position = mTopicSpinner.getSelectedItemPosition()-1;
    	String topic = topicsArray[position];    	
        boolean technicalHelp = topic.equals("Technical Help");
        if(technicalHelp) {
        	if(mOnCampusRadioGroup.getCheckedRadioButtonId() == -1) {
        		return false;
        	}        	
        	if(mVPNRadioGroup.getCheckedRadioButtonId() == -1) {
        		return false;
        	}
        }
        
        return true;
    }
    
    private void showForm() {
   	 	mScrollView.setVisibility(View.VISIBLE);
   	 	mLoader.setVisibility(View.GONE);
   	 	mLoader.stopLoading();
    }
    
    private void showLoading() {
    	 mScrollView.setVisibility(View.GONE);
         mLoader.setVisibility(View.VISIBLE);
         mLoader.showLoading();
    }

    private void prompt(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLoader.setVisibility(View.GONE);
            
            if (msg.arg1 == MobileWebApi.SUCCESS) {
                FormResult result = (FormResult)msg.obj;
                mThankYouView.setVisibility(View.VISIBLE);
                mContentResult.setTitle(result.getFeedbackString());
                
            } else {
            	mScrollView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected NewModule getNewModule() {
        return new LibrariesModule();
    }

    @Override
    public boolean isModuleHomeActivity() {
        return false;
    }

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }
}
