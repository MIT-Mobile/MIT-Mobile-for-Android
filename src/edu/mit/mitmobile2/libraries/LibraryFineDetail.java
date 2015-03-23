package edu.mit.mitmobile2.libraries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.FineListItem;

public class LibraryFineDetail extends NewModuleActivity{
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
        setContentView(R.layout.library_barton_book_detail);
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        Log.d(TAG,"index = " + index);
        final FineListItem fine = LibraryFines.getFineData().getFines().get(index);
        
        addSecondaryTitle("Fine");
        
        fineTitleTV = (TextView)findViewById(R.id.libraryBartonDetailTitleTV);
        fineTitleTV.setText(fine.getTitle());

        fineAuthorTV = (TextView)findViewById(R.id.libraryBartonDetailAuthorTV);
        fineAuthorTV.setText(fine.getYear() + "; " + fine.getAuthor());

        fineCallNoTV = (TextView)findViewById(R.id.libraryBartonDetailCallNoTV);
        fineCallNoTV.setText(fine.getCallNo());
      
        fineLibraryTV = (TextView)findViewById(R.id.libraryBartonDetailLibraryTV);
        fineLibraryTV.setText(fine.getSubLibrary());

        fineISBNTV = (TextView)findViewById(R.id.libraryBartonDetailISBNTV);
        fineISBNTV.setText(fine.getIsbnIssnDisplay());
       
        findViewById(R.id.libraryBartonDetailFineDateRow).setVisibility(View.VISIBLE);
    	fineFineDateTV = (TextView)findViewById(R.id.libraryBartonDetailFineDateTV);
    	fineFineDateTV.setText(fine.getFineDate());
        
    	findViewById(R.id.libraryBartonDetailAmountOwedRow).setVisibility(View.VISIBLE);
    	fineAmountOwedTV = (TextView)findViewById(R.id.libraryBartonDetailAmountOwedTV);
    	fineAmountOwedTV.setText(Html.fromHtml(fine.getDisplayAmount()));

    	mLoadingView = (FullScreenLoader) findViewById(R.id.librarySearchLoading);

    }

    @SuppressWarnings("unused")
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

	@Override
	protected NewModule getNewModule() {
		return new LibrariesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }
}
