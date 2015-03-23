package edu.mit.mitmobile2.alerts;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.shuttles.ShuttleModel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NotificationsHelper {

	AlarmManager alarmManager;

	int REQ_CODE = 0;  

	/******************************************/
	void scheduleAlarm(Context ctx) {
	
		Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, REQ_CODE, i, 0);

		alarmManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), pendingIntent);
		
		//Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show();
		
	}
	
	void cancelAlarm(Context ctx) {
		Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, REQ_CODE, i, 0);
		alarmManager.cancel(pendingIntent);
	}

	public static boolean setupAlarmData(Context context) {

		// Clear Shuttle alarms
		SharedPreferences pref = context.getSharedPreferences(Global.PREFS_SHUTTLES,Context.MODE_PRIVATE); 
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(ShuttleModel.KEY_STOP_ID, null);  
		editor.putString(ShuttleModel.KEY_ROUTE_ID, null);  
		editor.putString(ShuttleModel.KEY_TIME, null);  
		editor.commit();
		
		return true;
		
	}
	
}
