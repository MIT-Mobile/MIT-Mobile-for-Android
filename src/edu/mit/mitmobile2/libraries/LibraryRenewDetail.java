package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryRenewDetail extends Activity{
	public static final String TAG = "LibraryRenewDetail";

    Context mContext;
    private FullScreenLoader mLoadingView;
	private TextView renewTitleTV;
    private TextView renewAuthorTV;
	private TextView renewOverdueTV;
	private TextView renewMessageTV;
	private Button renewDoneButton;
	
    private int index;
    private String errorMsg = "";
    private String successMsg = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_renew_detail);
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        successMsg = extras.getString("successMsg");
        errorMsg = extras.getString("errorMsg");
        
        LoanListItem item = LibraryYourAccount.getLoanData().getLoans().get(index);
        
        renewTitleTV = (TextView)findViewById(R.id.renewTitleTV);
        renewTitleTV.setText(item.getTitle());

        renewAuthorTV = (TextView)findViewById(R.id.renewAuthorTV);
        renewAuthorTV.setText(item.getYear() + "; " + item.getAuthor());

        renewOverdueTV = (TextView)findViewById(R.id.renewOverdueTV);
        renewOverdueTV.setText(Html.fromHtml(item.getDueText()));
        
        renewMessageTV = (TextView)findViewById(R.id.renewMessageTV);
        if (errorMsg.length() > 0) {
        	renewMessageTV.setText("1 item could not be renewed.");
        }
        else {
        	renewMessageTV.setText(successMsg);
        	renewMessageTV.setTextColor(R.color.result_text);
        }
        
        renewDoneButton  = (Button)findViewById(R.id.renewDoneButton);
		renewDoneButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LibraryYourAccount.class);
				startActivity(intent);
			}
		});

        //mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        
    }

    private void doSearch(String barcode) {

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();

        LibraryModel.renewBook(this, uiHandler,barcode);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
        	Log.d(TAG,"arg1 = " + msg.arg1);
            
            mLoadingView.setVisibility(View.GONE);

            RenewBookResponse response = (RenewBookResponse)msg.obj;
            Log.d(TAG,"error = " + response.getRenewResponse().get(0).getErrorMsg());
            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };
}
