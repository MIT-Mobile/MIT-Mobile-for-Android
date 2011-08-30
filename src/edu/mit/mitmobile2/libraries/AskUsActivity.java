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
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

public class AskUsActivity extends ModuleActivity {

    private Spinner mTopicSpinner;
    private Spinner mStatusSpinner;

    private EditText mSubjectText;
    private EditText mDetailText;
    private EditText mDepartmentText;
    private EditText mPhoneText;
    private Button mSubmitButton;

    private String[] topicsArray;
    private String[] statusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_ask_us);
        
        mTopicSpinner = (Spinner) findViewById(R.id.topicSpinner);
        mStatusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        
        mSubjectText = (EditText) findViewById(R.id.subject);
        mDetailText = (EditText) findViewById(R.id.detailedQuestion);
        mDepartmentText = (EditText) findViewById(R.id.department);
        mPhoneText = (EditText) findViewById(R.id.phoneNumber);
        
        mSubmitButton = (Button) findViewById(R.id.submit);
        
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
                    //TODO:
                } else {
                    topic = topicsArray[position];
                }

                position = mStatusSpinner.getSelectedItemPosition();
                String status = null;
                if (position <= 0) {
                    //TODO:
                } else {
                    status = statusArray[position];
                }
                
                String subject = mSubjectText.getText().toString();
                if(subject.equals("")) {
                    //TODO:
                }
                String description = mDetailText.getText().toString();
                if(description.equals("")) {
                    //TODO:
                }
                String department = mDepartmentText.getText().toString();
                if(description.equals("")) {
                    //TODO:
                }
                
                
                LibraryModel.sendAskUsInfo(AskUsActivity.this, uiHandler, topic, status, department, subject, description);
            }
        });
        
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
