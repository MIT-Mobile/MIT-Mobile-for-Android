package edu.mit.mitmobile2.libraries;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.HoldListItem;

public class LibraryHoldDetail extends NewModuleActivity{
	public static final String TAG = "LibraryHoldDetail";

    Context mContext;
    private FullScreenLoader mLoadingView;
    private TextView holdTitleTV;
    private TextView holdAuthorTV;
    private TextView holdCallNoTV;
	private TextView holdLibraryTV;
	private TextView holdISBNTV;
	private ImageView holdStatusIconIV;
	private TextView holdStatusTV;
    private int index;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.library_barton_book_detail);
        
        Bundle extras = getIntent().getExtras();
        index = extras.getInt("index");
        Log.d(TAG,"index = " + index);
        final HoldListItem hold = LibraryHolds.getHoldData().getHolds().get(index);
        
        addSecondaryTitle("Hold");
        
        holdTitleTV = (TextView)findViewById(R.id.libraryBartonDetailTitleTV);
        holdTitleTV.setText(hold.getTitle());

        holdAuthorTV = (TextView)findViewById(R.id.libraryBartonDetailAuthorTV);
        holdAuthorTV.setText(hold.getYear() + "; " + hold.getAuthor());

        holdCallNoTV = (TextView)findViewById(R.id.libraryBartonDetailCallNoTV);
        holdCallNoTV.setText(hold.getCallNo());
      
        holdLibraryTV = (TextView)findViewById(R.id.libraryBartonDetailLibraryTV);
        holdLibraryTV.setText(hold.getSubLibrary());

        holdISBNTV = (TextView)findViewById(R.id.libraryBartonDetailISBNTV);
        holdISBNTV.setText(hold.getIsbnIssnDisplay());

        findViewById(R.id.libraryBartonDetailStatusRow).setVisibility(View.VISIBLE);
        holdStatusIconIV = (ImageView) findViewById(R.id.libraryBartonDetailStatusIcon);
        holdStatusTV = (TextView)findViewById(R.id.libraryBartonDetailStatusTV);
        
        if (hold.getReady().equalsIgnoreCase("TRUE")) {        	
        	holdStatusIconIV.setImageDrawable(getResources().getDrawable(R.drawable.status_ready));
        	holdStatusTV.setText(hold.getStatus() + "\nPick up at " + hold.getPickupLocation());
        	holdStatusTV.setTextColor(getResources().getColor(R.color.hold_ready_text));
        } else {
        	holdStatusIconIV.setVisibility(View.GONE);
        	holdStatusTV.setText(hold.getStatus());
        	holdStatusTV.setTextColor(getResources().getColor(R.color.libraries_gray));
        }

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
