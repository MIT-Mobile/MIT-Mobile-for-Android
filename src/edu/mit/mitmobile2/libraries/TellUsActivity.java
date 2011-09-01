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

public class TellUsActivity extends ModuleActivity {
    
    private Spinner mStatusSpinner;

    private EditText mFeedbackText;
    private Button mSubmitButton;
    private TextView mEmailText;
    
    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;

    private String[] statusArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_tell_us);
        
        mScrollView = (LockingScrollView) findViewById(R.id.scrollView);
        mFeedbackText = (EditText) findViewById(R.id.feebackText);
        mStatusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mEmailText = (TextView) findViewById(R.id.emailContent);
        mLoader = (FullScreenLoader) findViewById(R.id.askUsLoading);
        
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
            	
                int position = mStatusSpinner.getSelectedItemPosition()-1;
                String status = null;
                if(position >= 0) {
                    status = statusArray[position];
                }
                
                String feedback = mFeedbackText.getText().toString().trim();
                if("".equals(feedback)) {
                    Toast.makeText(TellUsActivity.this, "Department is required!", Toast.LENGTH_LONG).show();
                    return;
                }
                
                mScrollView.setVisibility(View.GONE);
                mLoader.setVisibility(View.VISIBLE);
                mLoader.showLoading();
                
                LibraryModel.sendTellUsInfo(TellUsActivity.this, uiHandler, status, feedback);
            }
        });
        
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
