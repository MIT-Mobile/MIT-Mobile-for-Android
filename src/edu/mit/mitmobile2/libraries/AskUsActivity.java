package edu.mit.mitmobile2.libraries;

import java.util.Arrays;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
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
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleSpinnerAdapter;
import edu.mit.mitmobile2.libraries.LibraryModel.UserIdentity;
import edu.mit.mitmobile2.libraries.VerifyUserCredentials.VerifyUserCredentialsListener;

public class AskUsActivity extends ModuleActivity {
	
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
    
    private TextView mEmailText;
    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;

    private String[] topicsArray;
    private String[] statusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_ask_us);
        
        mScrollView = (LockingScrollView) findViewById(R.id.askUsScrollView);
        
        mTopicSpinner = (Spinner) findViewById(R.id.topicSpinner);
        mStatusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        
        mSubjectText = (EditText) findViewById(R.id.subject);
        mDetailText = (EditText) findViewById(R.id.detailedQuestion);

        mDepartmentText = (EditText) findViewById(R.id.department);
        mPhoneText = (EditText) findViewById(R.id.phoneNumber);
        
        mSubmitButton = (Button) findViewById(R.id.submit);
        
        mEmailText = (TextView) findViewById(R.id.askUsEmailContent);
        mLoader = (FullScreenLoader) findViewById(R.id.askUsLoading);
        
        mTechHelpSection = findViewById(R.id.librariesTechHelpSection);
        mOnCampusRadioGroup = (RadioGroup) findViewById(R.id.librariesOnCampusRadioGroup);        
        mVPNRadioGroup = (RadioGroup) findViewById(R.id.librariesVPNRadioGroup);
        
        
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
                String topic = null;
                if (position < 0) {
                	mScrollView.smoothScrollTo(0, mTopicSpinner.getTop());
                	mTopicSpinner.performClick();
                    prompt("Please select a topic!");
                    return;
                } else {
                    topic = topicsArray[position];
                }
                
                String subject = mSubjectText.getText().toString().trim();
                if("".equals(subject)) {
                	mSubjectText.requestFocus();
                    prompt("Subject is required!");
                    return;
                }
                
                String question = mDetailText.getText().toString().trim();
                if("".equals(question)) {
                	mDetailText.requestFocus();
                    prompt("Description is required!");
                    return;
                }
                
                String phone = mPhoneText.getText().toString().trim();
                if("".equals(phone)) {
                	mPhoneText.requestFocus();
                	prompt("Phone number is required!");
                	return;
                }
                
                position = mStatusSpinner.getSelectedItemPosition()-1;
                String status = null;
                if (position < 0) {
                	mScrollView.smoothScrollTo(0, mStatusSpinner.getTop());
                	mStatusSpinner.performClick();
                    prompt("Please select a status!");
                    return;
                } else {
                	String[] statusCodeArray = getResources().getStringArray(R.array.libraryStatusCode);
                    status = statusCodeArray[position];
                }
                
                String department = mDepartmentText.getText().toString();
                if("".equals(department)) {
                	mDepartmentText.requestFocus();
                    prompt("Department is required!");
                    return;
                }
                
                String onCampus;
                String usingVPN;
                boolean technicalHelp = topic.equals("Technical Help");
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
                
                LibraryModel.sendAskUsInfo(AskUsActivity.this, uiHandler, topic, status, department, subject, question, "form");
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
                String content = (String)msg.obj;
                mEmailText.setText(content);
                mEmailText.setVisibility(View.VISIBLE);
                
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
