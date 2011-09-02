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

public class AskUsActivity extends ModuleActivity {
	
    private Spinner mTopicSpinner;
    private Spinner mStatusSpinner;

    private EditText mSubjectText;
    private EditText mDetailText;
    private EditText mDepartmentText;
    private EditText mQuestionText;
    private EditText mPhoneText;
    private Button mSubmitButton;
    
    private TextView mEmailText;
    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;

    private String[] topicsArray;
    private String[] statusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_ask_us);
        
        mScrollView = (LockingScrollView) findViewById(R.id.scrollView);
        
        mTopicSpinner = (Spinner) findViewById(R.id.topicSpinner);
        mStatusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        
        mSubjectText = (EditText) findViewById(R.id.subject);
        mDetailText = (EditText) findViewById(R.id.detailedQuestion);
        mQuestionText = (EditText) findViewById(R.id.question);

        mDepartmentText = (EditText) findViewById(R.id.department);
        mPhoneText = (EditText) findViewById(R.id.phoneNumber);
        
        mSubmitButton = (Button) findViewById(R.id.submit);
        
        mEmailText = (TextView) findViewById(R.id.emailContent);
        mLoader = (FullScreenLoader) findViewById(R.id.askUsLoading);
        
        topicsArray = getResources().getStringArray(R.array.libraryTopics);
        String topicsTitle = getResources().getString(R.string.libraryTopicsTitle);
        SpinnerAdapter topicAdapter = new SimpleSpinnerAdapter(this, topicsTitle, Arrays.asList(topicsArray));
        mTopicSpinner.setAdapter(topicAdapter);
        mTopicSpinner.setPrompt(topicsTitle);

        
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

                String question = mQuestionText.getText().toString().trim();
                if("".equals(question)) {
                	mQuestionText.requestFocus();
                    prompt("Question is required!");
                    return;
                }
                
                String description = mDetailText.getText().toString().trim();
                if("".equals(description)) {
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
                
                mScrollView.setVisibility(View.GONE);
                mLoader.setVisibility(View.VISIBLE);
                mLoader.showLoading();
                
                LibraryModel.sendAskUsInfo(AskUsActivity.this, uiHandler, topic, status, department, subject, question, description, "form");
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

}
