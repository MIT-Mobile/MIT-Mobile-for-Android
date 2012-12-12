package edu.mit.mitmobile2.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("BootReceiver","BootReceiver");
		
		GCMIntentService.registerForNotifications(context);
		
	}

}
