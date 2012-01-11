package edu.mit.mitmobile2.libraries;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleSpinnerAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.libraries.LibraryModel.FormResult;
import edu.mit.mitmobile2.libraries.LibraryModel.UserIdentity;
import edu.mit.mitmobile2.libraries.VerifyUserCredentials.VerifyUserCredentialsListener;

public class AppointmentActivity extends ModuleActivity {
    
    private Spinner mPurposeSpinner;
    private Spinner mTopicSpinner;
    private Spinner mStatusSpinner;

    private EditText mResearchTopic;
    private EditText mResearchTimeframe;
    private EditText mResearchInfo;
    private EditText mResearchCourse;
    private EditText mDepartment;
    private EditText mPhoneNumber;
    private Button mSubmitButton;
    
    private TwoLineActionRow mContentResult;
    private TwoLineActionRow mGoHomeButton;
    private View mThankYouView;
    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;
    
    private String[] topicsArray;
    private String[] statusArray;
    private String[] purposeArray;
    
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_appointment);
        
        mContext = this;
        
        mScrollView = (LockingScrollView) findViewById(R.id.appointmentScrollView);
        
        mPurposeSpinner = (Spinner) findViewById(R.id.purposeSpinner);
        
        mTopicSpinner = (Spinner) findViewById(R.id.discussTopic);
        mTopicSpinner.setOnItemSelectedListener(mUpdateSubmitSpinnerListener);
        
        mStatusSpinner = (Spinner) findViewById(R.id.appointmentStatusSpinner);
        mStatusSpinner.setOnItemSelectedListener(mUpdateSubmitSpinnerListener);
        
        mResearchTopic = (EditText) findViewById(R.id.researchTopic);
        mResearchTopic.addTextChangedListener(mUpdateSubmitButtonTextWatcher);
  
        mResearchTimeframe = (EditText) findViewById(R.id.researchTimeframe);
        
        mResearchInfo = (EditText) findViewById(R.id.researchInfo);
        mResearchInfo.addTextChangedListener(mUpdateSubmitButtonTextWatcher);
        
        mResearchCourse = (EditText) findViewById(R.id.whichCourse);

        mDepartment = (EditText) findViewById(R.id.appointmentDepartment);
        mDepartment.addTextChangedListener(mUpdateSubmitButtonTextWatcher);
        
        mPhoneNumber = (EditText) findViewById(R.id.appointmentPhoneNumber);
        
        mSubmitButton = (Button) findViewById(R.id.submitAppointment);
        mSubmitButton.setEnabled(false);
        
        mThankYouView = findViewById(R.id.libraryAppointmentThankYou);
        mContentResult = (TwoLineActionRow) findViewById(R.id.librariesThankYouContentActionRow);
        mGoHomeButton = (TwoLineActionRow) findViewById(R.id.librariesThankYouReturnHome);
        mGoHomeButton.setOnClickListener(new View.OnClickListener() {
			@Override
		    public void onClick(View v) {
				Intent intent = new Intent(mContext, getModule().getModuleHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
		    }
		});
        
        mLoader = (FullScreenLoader) findViewById(R.id.appointmentLoading);
        
        topicsArray = getResources().getStringArray(R.array.libraryResearchTopics);
        String topicTitle = getResources().getString(R.string.libraryResearchTopicTitle);
        SpinnerAdapter topicsAdapter = new SimpleSpinnerAdapter(this, topicTitle, Arrays.asList(topicsArray));
        mTopicSpinner.setAdapter(topicsAdapter);
        mTopicSpinner.setPrompt(topicTitle);
        
        
        statusArray = getResources().getStringArray(R.array.libraryStatus);
        String statusTitle = getResources().getString(R.string.libraryStatusTitle);
        SpinnerAdapter statusAdapter = new SimpleSpinnerAdapter(this, statusTitle, Arrays.asList(statusArray));
        mStatusSpinner.setAdapter(statusAdapter);
        mStatusSpinner.setPrompt(statusTitle);
        
        purposeArray = getResources().getStringArray(R.array.libraryResearchPurpose);
        SpinnerAdapter purposeAdapter = new SimpleSpinnerAdapter(this, "", Arrays.asList(purposeArray));
        mPurposeSpinner.setAdapter(purposeAdapter);
        
        mSubmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {                
                String topic = mResearchTopic.getText().toString().trim();
                String timeframe = mResearchTimeframe.getText().toString().trim();                
                String information = mResearchInfo.getText().toString().trim();
                
                int position = mPurposeSpinner.getSelectedItemPosition()-1;
                String purpose = "";
                if(position >= 0) {
                	purpose = purposeArray[position];
                }                
                String course = mResearchCourse.getText().toString().trim();
                
                
                
                position = mTopicSpinner.getSelectedItemPosition()-1;
                String researchTopic = null;
                if (position >= 0) {
                	researchTopic = topicsArray[position];
                }
                
                position = mStatusSpinner.getSelectedItemPosition()-1;
                String status = null;
                if (position >= 0) {
                	String[] statusCodeArray = getResources().getStringArray(R.array.libraryStatusCode);
                    status = statusCodeArray[position];
                }
                
                String department = mDepartment.getText().toString();                
                
                String phoneNumber = mPhoneNumber.getText().toString().trim();
                
                showLoading();
                
                LibraryModel.sendAppointmentEmail(AppointmentActivity.this, uiHandler, topic, timeframe, information, purpose, course, researchTopic, status, department, phoneNumber);
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
    
    private boolean formValidates() {
    	Spinner[] spinners = new Spinner[] {mTopicSpinner, mStatusSpinner};
    	for (int i=0; i < spinners.length; i++) {
    		if (spinners[i].getSelectedItemPosition() < 1) {
    			return false;
    		}
    	}
    	
    	EditText[] editTexts = new EditText[] {mResearchTopic, mResearchInfo, mDepartment};
    	for (int i=0; i < editTexts.length; i++) {
    		if (editTexts[i].getText().toString().trim().length() == 0) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    private void showLoading() {
        mScrollView.setVisibility(View.GONE);
        mLoader.setVisibility(View.VISIBLE);
        mLoader.showLoading();
    }
    
    private void showForm() {
        mScrollView.setVisibility(View.VISIBLE);
        mLoader.setVisibility(View.GONE);
        mLoader.stopLoading();
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

}
