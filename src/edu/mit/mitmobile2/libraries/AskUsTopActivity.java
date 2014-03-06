package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView.BufferType;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.libraries.LibraryModel.UserIdentity;

public class AskUsTopActivity extends NewModuleActivity {

    private TwoLineActionRow askUsRow;
    private TwoLineActionRow makeAppontmentRow;
    private TwoLineActionRow generalHelpRow;
    private Activity mActivity;
    public static final String TAG = "AskUsTopActivity";
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        mActivity = this;
        
        setContentView(R.layout.library_askus_top);
        
        askUsRow = (TwoLineActionRow) findViewById(R.id.askUsItem);
        makeAppontmentRow = (TwoLineActionRow) findViewById(R.id.makeAppointmentItem);
        
        generalHelpRow = (TwoLineActionRow) findViewById(R.id.generalHelpItem);
        String generalHelpTitle = "General help (617.324.2275)";
        Spannable titleSpan = Spannable.Factory.getInstance().newSpannable(generalHelpTitle);
        TextAppearanceSpan secondaryStyle = new TextAppearanceSpan(this, R.style.ListItemSecondary);  
        titleSpan.setSpan(secondaryStyle, generalHelpTitle.indexOf("("), generalHelpTitle.indexOf(")")+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);     
        generalHelpRow.setTitle(titleSpan, BufferType.SPANNABLE);
        generalHelpRow.setActionIconResource(R.drawable.action_phone);
        
        askUsRow.setOnClickListener(new OnClickListener() {
        	
        	@Override
            public void onClick(View v) {
        		
        		LibraryModel.getUserIdentity(mActivity,  getTouchStoneHandler(mActivity, "edu.mit.mitmobile2.libraries.AskUsActivity"));
            }
        	
        });
        makeAppontmentRow.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		
        		LibraryModel.getUserIdentity(mActivity,  getTouchStoneHandler(mActivity, "edu.mit.mitmobile2.libraries.AppointmentActivity"));
            }
        	
        });
        generalHelpRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonActions.callPhone(AskUsTopActivity.this, generalHelpRow.getTitle().toString());
            }
        });
        

    }
    
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
   
    //---------------
    protected Handler createTouchStoneHandler(final String target) {
  
    	Handler TouchStoneHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {     	
            	Log.d(TAG,"touchstone messsage = " + msg.arg1);
            	if (msg.arg1 == MobileWebApi.SUCCESS) {
            		UserIdentity userIdentity = (UserIdentity)msg.obj;
            		Log.d(TAG,"shbidentity = " + userIdentity.getShibIdentity());
            		Log.d(TAG,"username = " + userIdentity.getUsername());
            		Log.d(TAG,"mit identity = " + userIdentity.isMITIdentity() + "");
            		if (userIdentity.getShibIdentity() != null && userIdentity.getShibIdentity().length() > 1) {
            			//startActivity(new Intent(AskUsTopActivity.this, AppointmentActivity.class));           			
            			try {
	        				  Class<?> c = Class.forName(target);
	        				  Intent i = new Intent(mActivity, c);
	        				  startActivity(i);
	        				  Log.d(TAG,"start intent = " + i.toString());
	        			  } catch (Throwable e) {
	        		            System.err.println(e);
	        		      }
            		}
            	}
            }
        };
        return TouchStoneHandler;
    }
    
/*
  	private Handler AskUsHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
          	
          	Log.d(TAG,"touchstone messsage = " + msg.arg1);
          	if (msg.arg1 == MobileWebApi.SUCCESS) {
          		UserIdentity userIdentity = (UserIdentity)msg.obj;
          		Log.d(TAG,"shbidentity = " + userIdentity.getShibIdentity());
          		Log.d(TAG,"username = " + userIdentity.getUsername());
          		Log.d(TAG,"mit identity = " + userIdentity.isMITIdentity() + "");
          		if (userIdentity.getShibIdentity() != null && userIdentity.getShibIdentity().length() > 1) {
          			  startActivity(new Intent(AskUsTopActivity.this, AskUsActivity.class));      			  
          		}
          	}
          }
      };
      
      private Handler AppointmentHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
          	
          	Log.d(TAG,"touchstone messsage = " + msg.arg1);
          	if (msg.arg1 == MobileWebApi.SUCCESS) {
          		UserIdentity userIdentity = (UserIdentity)msg.obj;
          		Log.d(TAG,"shbidentity = " + userIdentity.getShibIdentity());
          		Log.d(TAG,"username = " + userIdentity.getUsername());
          		Log.d(TAG,"mit identity = " + userIdentity.isMITIdentity() + "");
          		if (userIdentity.getShibIdentity() != null && userIdentity.getShibIdentity().length() > 1) {
          			  startActivity(new Intent(AskUsTopActivity.this, AppointmentActivity.class));
          		}
          	}
          }
      };
*/
}
