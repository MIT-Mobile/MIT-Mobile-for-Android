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
import android.widget.Button;
import android.widget.EditText;
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
    
    private TextView mEmailText;
    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;
    
    private String[] topicsArray;
    private String[] statusArray;
    private String[] purposeArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_appointment);
        
        mScrollView = (LockingScrollView) findViewById(R.id.appointmentScrollView);
        
        mPurposeSpinner = (Spinner) findViewById(R.id.purposeSpinner);
        mTopicSpinner = (Spinner) findViewById(R.id.discussTopic);
        mStatusSpinner = (Spinner) findViewById(R.id.appointmentStatusSpinner);
        
        mResearchTopic = (EditText) findViewById(R.id.researchTopic);
        mResearchTimeframe = (EditText) findViewById(R.id.researchTimeframe);
        mResearchInfo = (EditText) findViewById(R.id.researchInfo);
        mResearchCourse = (EditText) findViewById(R.id.whichCourse);

        mDepartment = (EditText) findViewById(R.id.appointmentDepartment);
        mPhoneNumber = (EditText) findViewById(R.id.appointmentPhoneNumber);
        
        mSubmitButton = (Button) findViewById(R.id.submitAppointment);
        
        mEmailText = (TextView) findViewById(R.id.appointmentEmailContent);
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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSubmitButton.getWindowToken(), 0);
                
                String topic = mResearchTopic.getText().toString().trim();
                if("".equals(topic)) {
                    mResearchTopic.requestFocus();
                  prompt("Topic is required!");
                  return;
              }
                
                String timeframe = mResearchTimeframe.getText().toString().trim();
                
                String information = mResearchInfo.getText().toString().trim();
                if("".equals(information)) {
                    mResearchInfo.requestFocus();
                  prompt("Research information is required!");
                  return;
              }
                
                int position = mPurposeSpinner.getSelectedItemPosition()-1;
                String purpose = "";
                if(position >= 0) {
                	purpose = purposeArray[position];
                }
                
                String course = mResearchCourse.getText().toString().trim();
                
                
                
                position = mTopicSpinner.getSelectedItemPosition()-1;
                String researchTopic = null;
                if (position < 0) {
                    mScrollView.smoothScrollTo(0, mTopicSpinner.getTop());
                    mTopicSpinner.performClick();
                    prompt("Please select a topic!");
                    return;
                } else {
                    researchTopic = topicsArray[position];
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
                
                String department = mDepartment.getText().toString();
                if("".equals(department)) {
                    mDepartment.requestFocus();
                    prompt("Department is required!");
                    return;
                }
                
                
                String phoneNumber = mPhoneNumber.getText().toString().trim();
                
                mScrollView.setVisibility(View.GONE);
                mLoader.setVisibility(View.VISIBLE);
                mLoader.showLoading();
                
                LibraryModel.sendAppointmentEmail(AppointmentActivity.this, uiHandler, topic, timeframe, information, purpose, course, researchTopic, status, department, phoneNumber);
            }
        });
        
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
