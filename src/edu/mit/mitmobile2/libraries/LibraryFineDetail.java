package edu.mit.mitmobile2.libraries;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;

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
import edu.mit.mitmobile2.objs.FineListItem;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LibraryFineDetail extends Activity{
	public static final String TAG = "LibraryFineDetail";

    Context mContext;
    private FullScreenLoader mLoadingView;
	private TextView fineTitleTV;
    private TextView fineAuthorTV;
    private TextView fineCallNoTV;
	private TextView fineLibraryTV;
	private TextView fineISBNTV;
	private TextView fineFineDateTV;
	private TextView fineAmountOwedTV;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_fine_detail);
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        Log.d(TAG,"index = " + index);
        final FineListItem fine = LibraryFines.getFineData().getFines().get(index);
        
        fineTitleTV = (TextView)findViewById(R.id.fineTitleTV);
        fineTitleTV.setText(fine.getTitle());

        fineAuthorTV = (TextView)findViewById(R.id.fineAuthorTV);
        fineAuthorTV.setText(fine.getYear() + "; " + fine.getAuthor());

        fineCallNoTV = (TextView)findViewById(R.id.fineCallNoTV);
        fineCallNoTV.setText(fine.getCallNo());
      
        fineLibraryTV = (TextView)findViewById(R.id.fineLibraryTV);
        fineLibraryTV.setText(fine.getSubLibrary());

        fineISBNTV = (TextView)findViewById(R.id.fineISBNTV);
        fineISBNTV.setText(fine.getIsbnIssnDisplay());
       
    	fineFineDateTV = (TextView)findViewById(R.id.fineFineDateTV);
    	fineFineDateTV.setText(fine.getFineDate());
        
    	fineAmountOwedTV = (TextView)findViewById(R.id.fineAmountOwedTV);
    	fineAmountOwedTV.setText(Html.fromHtml(fine.getDisplayAmount()));

    	mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

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
