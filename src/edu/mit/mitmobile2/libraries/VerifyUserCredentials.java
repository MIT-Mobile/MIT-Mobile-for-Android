package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.libraries.LibraryModel.UserIdentity;

public class VerifyUserCredentials {
	
	public static interface VerifyUserCredentialsListener {
		public void onUserLoggedIn(UserIdentity user);
		
		//public void onUserNotLoggedIn();
	}

	public static void VerifyUserHasFormAccess(final Activity activity, final VerifyUserCredentialsListener listener) {
        LibraryModel.getUserIdentity(activity, new Handler() {
        	@Override
        	public void handleMessage(Message message) {
        		if (message.arg1 == MobileWebApi.SUCCESS) {
        			UserIdentity identity = (UserIdentity) message.obj;
        			if (!identity.isMITIdentity()) {
        				Toast.makeText(activity, "Must be logged in with an MIT account", Toast.LENGTH_LONG).show();
        				activity.finish();
        			} else {
        				listener.onUserLoggedIn(identity);
        			}
        		} else {
        			activity.finish();
        		}
        	}
        });
	}
}
