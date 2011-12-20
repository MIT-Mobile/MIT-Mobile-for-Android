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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.classes.LoanData;
import edu.mit.mitmobile2.classes.RenewBookResponse;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryLoanDetail extends Activity{
	public static final String TAG = "LibraryLoanDetail";

    Context mContext;
    private FullScreenLoader mLoadingView;
    private TitleBar loanTitleBar;
	private TextView loanTitleTV;
    private TextView loanAuthorTV;
    private TextView loanCallNoTV;
	private TextView loanLibraryTV;
	private TextView loanISBNTV;
	private ImageView loanStatusIconIV;
	private TextView loanStatusTV;
	private Button loanRenewButton;
	private LinearLayout loanDetailLayout;
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
        setContentView(R.layout.library_barton_book_detail);
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        final LoanListItem item = LibraryYourAccount.getLoanData().getLoans().get(index);
        
        loanTitleBar = (TitleBar)findViewById(R.id.libraryBartonDetailTitleBar);
        loanTitleBar.setTitle("Loan");
        
        loanDetailLayout = (LinearLayout)findViewById(R.id.libraryBartonDetailLayout);
        loanTitleTV = (TextView)findViewById(R.id.libraryBartonDetailTitleTV);
        loanTitleTV.setText(item.getTitle());

        loanAuthorTV = (TextView)findViewById(R.id.libraryBartonDetailAuthorTV);
        loanAuthorTV.setText(item.getYear() + "; " + item.getAuthor());

        loanCallNoTV = (TextView)findViewById(R.id.libraryBartonDetailCallNoTV);
        loanCallNoTV.setText(item.getCallNo());
      
        loanLibraryTV = (TextView)findViewById(R.id.libraryBartonDetailLibraryTV);
        loanLibraryTV.setText(item.getSubLibrary());

        loanISBNTV = (TextView)findViewById(R.id.libraryBartonDetailISBNTV);
        loanISBNTV.setText(item.getIsbnIssnDisplay());
       
        findViewById(R.id.libraryBartonDetailStatusRow).setVisibility(View.VISIBLE);
        loanStatusIconIV = (ImageView)findViewById(R.id.libraryBartonDetailStatusIcon);
        loanStatusTV = (TextView)findViewById(R.id.libraryBartonDetailStatusTV);
        loanStatusTV.setText(Html.fromHtml(item.getDueText())); 
    	if (item.isOverdue() || item.isLongOverdue()) {
    		loanStatusTV.setTextColor(Color.RED);
    		loanStatusIconIV.setImageDrawable(getResources().getDrawable(R.drawable.status_alert));
    	}
    	else {
    		loanStatusTV.setTextColor(Color.BLACK);
    		loanStatusIconIV.setVisibility(View.GONE);
    	}

        
        
        mLoadingView = (FullScreenLoader) findViewById(R.id.libraryBartonDetailDetailLoading);

        loanRenewButton = (Button) findViewById(R.id.libraryBartonDetailRenewButton);
        loanRenewButton.setVisibility(View.VISIBLE);
        loanRenewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	renewBook(item.getBarcode());
                Log.d(TAG,"renew ");
            }
        });
        
    }

    private void renewBook(String barcode) {
    	loanDetailLayout.setVisibility(View.GONE);
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
        		
        		finish();
            	///////////////////
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };
}
