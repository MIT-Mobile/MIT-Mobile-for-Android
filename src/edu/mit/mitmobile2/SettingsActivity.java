package edu.mit.mitmobile2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

import edu.mit.mitmobile2.alerts.NotificationsAlarmReceiver;

public class SettingsActivity extends Activity {

	Context ctx;

	SharedPreferences  pref;
	
	static String PREF_EMERGENCY_CHOICE = "emergency_choice";
	static String PREF_CLASS_CHOICE = "class_choice";
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);
		
		setTitle("Settings");
		
		ctx = this;

		pref = ctx.getSharedPreferences(Global.PREFS,MODE_PRIVATE); 
	    //pref = getPreferences(MODE_PRIVATE);
	    
		updateView();
		
	}

	/****************************************************/
	void updateView() {
		
		// Classes
		final RadioGroup rgClasses = (RadioGroup) findViewById(R.id.settingsClassesRG);
		
		int id = R.id.settingsClassesRB00;
		switch (pref.getInt(PREF_CLASS_CHOICE, 0)) {
		case 0: id = R.id.settingsClassesRB00; break;
		case 1: id = R.id.settingsClassesRB01; break;
		case 2: id = R.id.settingsClassesRB02; break;
		}
		rgClasses.check(id);
		
		// Emergency
		final RadioGroup rgEmrgcy = (RadioGroup) findViewById(R.id.settingsEmergencyRG);
		
		id = R.id.settingsEmergencyRB00;
		switch (pref.getInt(PREF_EMERGENCY_CHOICE, 0)) {
		case 0: id = R.id.settingsEmergencyRB00; break;
		case 1: id = R.id.settingsEmergencyRB01; break;
		case 2: id = R.id.settingsEmergencyRB02; break;
		}
		rgEmrgcy.check(id);
		
		// Save
		Button btnSave = (Button) findViewById(R.id.settingsSaveBtn);
		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int index = 0;
				int choice;
				String type = null;
				long period = 0;

			    pref.edit();
			    SharedPreferences.Editor editor = pref.edit();
			    
				// Classes
				
				choice = rgClasses.getCheckedRadioButtonId();
				
				type = NotificationsAlarmReceiver.ACTION_ALARM_CLASS;
				
//				switch (choice) {
//				case R.id.settingsClassesRB00:
//					period = -1; index = 0; break;
//				case R.id.settingsClassesRB01:
//					period = 15*60*1000; index = 1; break;
//				case R.id.settingsClassesRB02:
//					period = 30*60*1000; index = 2; break;
//				}
				
				// note: do NOT set alarm - that is done per Class elsewhere

			    editor.putInt(PREF_CLASS_CHOICE, index);
			    
				///////////////
				
			    // Emergency
			    
			    // note: id's change with compile - want relative
			    
				choice = rgEmrgcy.getCheckedRadioButtonId();
				

				type = NotificationsAlarmReceiver.ACTION_ALARM_EMERGENCY;
				
//				switch (choice) {
//				case R.id.settingsEmergencyRB00:
//					period = -1; index = 0;          // disabled
//					break;
//				case R.id.settingsEmergencyRB01:
//					period = 30*60*1000; index = 1;  // 30 mins
//					break;
//				case R.id.settingsEmergencyRB02:
//					period = 60*60*1000; index = 2;  // 1 hr
//					break;
//				}
				
				setAlarm(period,type,null);

			    editor.putInt(PREF_EMERGENCY_CHOICE, index);
			    
			    editor.commit();
			}
		};
		btnSave.setOnClickListener(l);
		
	}
	/****************************************************/
	
	// Emergency and Class alarms occur periodically to trigger polling but there are multiple 
	// classes which need to be stored in database or be part of the alarm intent, but former 
	// is more efficient (poll all classes once per alarm not many alarms with one class each)
	
	void setAlarm(long period, String type, String data) {
		
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);

		// Cancel or Schedule alarm?
		
		if (period<0) {
			
			// Cancel 
			
			// (use matching action/data , NOT "extra" data)
			
			Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
			i.setAction(type);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
			alarmManager.cancel(pendingIntent);
			
		} else {
			
			// Schedule 
			
			long curTime = System.currentTimeMillis();
			long wakeTime;
			
			
			// #1
			//wakeTime = busTime - 5*60*1000;  // wake up 5 mins before...
			// #2 DEBUG
			wakeTime = curTime + 10*1000; 
			period = 30*1000;
			
			
			
			
			Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
			i.setAction(type);
			
			if (data!=null) {
				i.setData(Uri.parse(data));
			}
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  wakeTime, period, pendingIntent);
			
		}

	}
	
}
