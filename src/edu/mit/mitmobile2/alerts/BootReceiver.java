package edu.mit.mitmobile2.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("BootReceiver","BootReceiver");
		
		NotificationsHelper.setupAlarmData(context);
		
		/*

		//if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			

			// TODO remove
			//if (true) return;
			
			// Clear Shuttle alarms
			SharedPreferences pref = context.getSharedPreferences(Global.PREFS,Context.MODE_PRIVATE); 
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(ShuttleModel.KEY_STOP_ID, null);  
			editor.putString(ShuttleModel.KEY_ROUTE_ID, null);  
			editor.putString(ShuttleModel.KEY_TIME, null);  
			editor.commit();
			

			// Restart Emergency
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
			Intent i = new Intent(context, NotificationsAlarmReceiver.class);
			i.setAction(NotificationsAlarmReceiver.ACTION_ALARM_EMERGENCY);
			long curTime  = System.currentTimeMillis();
			long wakeTime = curTime + 60*1000; 
			long period = 60*1000;//30*60*1000;  // 30 mins
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  wakeTime, period, pendingIntent);
			Log.d("BootReceiver", pendingIntent.toString());
			
			// Restart Stellar 
			CoursesDataModel cdm = new CoursesDataModel();
			cdm.getMyStellar(context);
			cdm.startAlarms(context);
			
		//}
		 */
	}

}
