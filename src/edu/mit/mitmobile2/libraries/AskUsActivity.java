package edu.mit.mitmobile2.libraries;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

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
        
        topicsArray = getResources().getStringArray(R.array.topics);
        statusArray = getResources().getStringArray(R.array.status);
        
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (String item : topicsArray) {
            topicAdapter.add(item);
        }
        mTopicSpinner.setAdapter(topicAdapter);
        
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (String item : statusArray) {
            statusAdapter.add(item);
        }
        mStatusSpinner.setAdapter(statusAdapter);
        
        mSubmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = mTopicSpinner.getSelectedItemPosition();
                String topic = null;
                if (position <= 0) {
                    prompt("Please select a topic!");
                } else {
                    topic = topicsArray[position];
                }
                
                String subject = mSubjectText.getText().toString();
                if("".equals(subject)) {
                    prompt("Subject is required!");
                    mSubjectText.requestFocus();
                    return;
                }

                String question = mQuestionText.getText().toString();
                if("".equals(question)) {
                    prompt("Question is required!");
                    return;
                }
                
                String description = mDetailText.getText().toString();
                if("".equals(description)) {
                    prompt("Description is required!");
                    return;
                }
                
                position = mStatusSpinner.getSelectedItemPosition();
                String status = null;
                if (position <= 0) {
                    prompt("Please select a status!");
                    return;
                } else {
                    status = statusArray[position];
                }
                
                String department = mDepartmentText.getText().toString();
                if("".equals(department)) {
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
