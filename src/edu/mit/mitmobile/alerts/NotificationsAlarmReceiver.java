package edu.mit.mitmobile.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class NotificationsAlarmReceiver extends BroadcastReceiver {

	static int NOTIFICATION_ID = 0;
	static long THRESHOLD = 5*60*1000;
	
	static String ACTION_ALARM = "edu.mit.alarm";
	static String ACTION_PUSH = "edu.mit.push";

	public static String ACTION_ALARM_SHUTTLE = "edu.mit.alarm.shuttle";
	public static String ACTION_ALARM_CLASS = "edu.mit.alarm.class";
	public static String ACTION_ALARM_EMERGENCY = "edu.mit.alarm.emergency";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("NotificationsAlarmReceiver", "received intent");

	
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		
		Uri uriData = intent.getData();
		
		
		// Redirect intent to service
		
		Intent i = new Intent(context, NotificationService.class);
		
		i.setAction(action);
		i.putExtras(extras);
		if (uriData!=null) {
			Log.d("NotificationsAlarmReceiver","data= " + uriData.toString());
			i.setData(uriData); 
		}
		
		context.startService(i);
		
	}

}
