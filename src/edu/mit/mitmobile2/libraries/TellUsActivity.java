package edu.mit.mitmobile2.libraries;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
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

public class TellUsActivity extends ModuleActivity {
    
	private Activity mContext;
	
    private Spinner mStatusSpinner;

    private EditText mFeedbackText;
    private Button mSubmitButton;
    private View mThankYouView;
    private TwoLineActionRow mContentResult;
    private TwoLineActionRow mGoHomeButton;
    
    private FullScreenLoader mLoader;
    private LockingScrollView mScrollView;

    private String[] statusArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library_tell_us);
        
        mContext = this;
        
        mScrollView = (LockingScrollView) findViewById(R.id.scrollView);
        mFeedbackText = (EditText) findViewById(R.id.feebackText);
        mStatusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mLoader = (FullScreenLoader) findViewById(R.id.tellUsLoading);
        mThankYouView = findViewById(R.id.libraryTellUsThankYou);
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
                    String[] statusCodeArray = getResources().getStringArray(R.array.libraryStatusCode);
                    status = statusCodeArray[position];
                }
                
                String feedback = mFeedbackText.getText().toString().trim();
                if("".equals(feedback)) {
                    Toast.makeText(TellUsActivity.this, "Description is required!", Toast.LENGTH_LONG).show();
                    return;
                }
                
                mScrollView.setVisibility(View.GONE);
                mLoader.setVisibility(View.VISIBLE);
                mLoader.showLoading();
                
                LibraryModel.sendTellUsInfo(TellUsActivity.this, uiHandler, status, feedback);
            }
        });
        
        showLoader();
        VerifyUserCredentials.VerifyUserHasFormAccess(mContext, new VerifyUserCredentialsListener() {
			@Override
			public void onUserLoggedIn(UserIdentity user) {
				showForm();
			}
        });
    }

    
    private void showLoader() {
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
