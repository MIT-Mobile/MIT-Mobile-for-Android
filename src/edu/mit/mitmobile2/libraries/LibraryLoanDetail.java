package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class LibraryLoanDetail extends Activity{
	public static final String TAG = "LibraryLoanDetail";

    Context mContext;
    private FullScreenLoader mLoadingView;
	private TextView loanTitleTV;
    private TextView loanAuthorTV;
    private TextView loanCallNoTV;
	private TextView loanLibraryTV;
	private TextView loanISBNTV;
	private TextView loanStatusTV;
	private Button loanRenewButton;
    private int index;
    
//        Intent intent = new Intent(context, LibraryDetailActivity.class);
//        intent.putExtra(LibraryLoanDetail.KEY_POSITION, position);
//
//        context.startActivity(intent);
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_loan_detail);
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        final LoanListItem item = LibraryLoans.getLoanData().getLoans().get(index);
        
        loanTitleTV = (TextView)findViewById(R.id.loanTitleTV);
        loanTitleTV.setText(item.getTitle());

        loanAuthorTV = (TextView)findViewById(R.id.loanAuthorTV);
        loanAuthorTV.setText(item.getYear() + "; " + item.getAuthor());

        loanCallNoTV = (TextView)findViewById(R.id.loanCallNoTV);
        loanCallNoTV.setText(item.getCallNo());
      
        loanLibraryTV = (TextView)findViewById(R.id.loanLibraryTV);
        loanLibraryTV.setText(item.getSubLibrary());

        loanISBNTV = (TextView)findViewById(R.id.loanISBNTV);
        loanISBNTV.setText(item.getIsbnIssnDisplay());
       
        loanStatusTV = (TextView)findViewById(R.id.loanStatusTV);
        loanStatusTV.setText(Html.fromHtml(item.getDueText()));  
    	if (item.isOverdue() || item.isLongOverdue()) {
    		loanStatusTV.setTextColor(Color.RED);
    	}
    	else {
    		loanStatusTV.setTextColor(R.color.contents_text);
    	}

        
        
        mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

        loanRenewButton = (Button) findViewById(R.id.loanRenewButton);
        loanRenewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSearch(item.getBarcode());
                Log.d(TAG,"renew ");
            }
        });
        
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
            	///////////////////
            	Intent intent = new Intent(mContext, LibraryRenewDetail.class);
        		intent.putExtra("index", index);
        		intent.putExtra("successMsg",response.getRenewResponse().get(0).getSuccessMsg());
        		intent.putExtra("errorMsg", response.getRenewResponse().get(0).getErrorMsg());
        		startActivity(intent);          
            	///////////////////
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };
}
