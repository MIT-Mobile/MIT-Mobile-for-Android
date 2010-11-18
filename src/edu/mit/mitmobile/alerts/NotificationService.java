package edu.mit.mitmobile.alerts;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import edu.mit.mitmobile.Global;
import edu.mit.mitmobile.JSONParser;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.classes.CourseSubjectParser;
import edu.mit.mitmobile.classes.CoursesDataModel;
import edu.mit.mitmobile.classes.CoursesTopActivity;
import edu.mit.mitmobile.emergency.EmergencyActivity;
import edu.mit.mitmobile.emergency.EmergencyParser;
import edu.mit.mitmobile.objs.CourseItem;
import edu.mit.mitmobile.objs.EmergencyItem;
import edu.mit.mitmobile.objs.RouteItem;
import edu.mit.mitmobile.objs.CourseItem.Announcement;
import edu.mit.mitmobile.objs.RouteItem.Stops;
import edu.mit.mitmobile.shuttles.RoutesParser;
import edu.mit.mitmobile.shuttles.ShuttleModel;
import edu.mit.mitmobile.shuttles.ShuttlesActivity;
import edu.mit.mitmobile.shuttles.StopsParser;


public class NotificationService extends Service {
	
	private static final String TAG = "NotificationService";
	
	private static final int EMERGENCY_VERSION_NOT_FOUND = -1;

	static long SHUTTLE_THRESHOLD = 8*60*1000;
	
	Context ctx;
	SharedPreferences pref;
	Intent i;
	int icon;
	Thread thr;
	
	/******************************************************************/
    Runnable mTask = new Runnable() {
        public void run() {
        	
    		if (i==null) return;
    		
    		String action = i.getAction();
    		
    		if (action==null) return;
    		
    		if (action.equals(NotificationsAlarmReceiver.ACTION_ALARM_EMERGENCY)) {
    			icon = R.drawable.alert_emergency;
    			checkEmergency();
    		} else if (action.equals(NotificationsAlarmReceiver.ACTION_ALARM_CLASS)) {
    			icon = R.drawable.alert_stellar;
    			checkClass();
    		} else if (action.equals(NotificationsAlarmReceiver.ACTION_ALARM_SHUTTLE)) {
    			icon = R.drawable.alert_shuttles;
    			checkStop();
    		}

        }
    };
    /**
     * @return ****************************************************************/
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "starting notificationService thread");
		
		super.onStart(intent, startId);
		
		i = intent;
		
		ctx = getApplicationContext();

		pref = ctx.getSharedPreferences(Global.PREFS,MODE_PRIVATE);  
		
		thr = new Thread(null, mTask, "NotificationService_Service");
		thr.start();
		
		return START_FLAG_REDELIVERY;
	
	}
	/******************************************************************/
	/*
	@Override
	public void onDestroy() {
		//thr.stop();
		super.onDestroy();
	}*/
	/******************************************************************/
    @Override
    public void onCreate() {
    	Log.d(TAG, "created notification service");
    }
	/******************************************************************/
	protected void checkEmergency() {
		Log.d(TAG, "checking emergency version tag");
		
		Looper.prepare();
		
		try {
		
		// Get status...
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				super.handleMessage(msg);
				EmergencyItem ei = EmergencyParser.getStatus();
				// Compare with last version...
			    int lastVersion = pref.getInt(Global.PREF_KEY_EMERGENCY_VERSION, EMERGENCY_VERSION_NOT_FOUND);  
			    int newVersion = -1;
			    try {
				    if (ei.version!=null) newVersion = Integer.valueOf(ei.version);
			    } catch (NumberFormatException ne) {
			    	return;
			    }
			    Log.d(TAG, "Emergency version used to be " + String.valueOf(lastVersion));
			    Log.d(TAG, "Now it is " + String.valueOf(newVersion));
			    
			    if (newVersion != lastVersion) {
					SharedPreferences.Editor editor = pref.edit();
					editor.putInt(Global.PREF_KEY_EMERGENCY_VERSION,newVersion);  
					editor.commit();
				    
				    if (lastVersion != EMERGENCY_VERSION_NOT_FOUND
				    		&& newVersion >= 0)
				    {
				    	String title = getString(R.string.emergency_alert_title);
				    	Spanned htmlText = Html.fromHtml(ei.text);
						notifyUser(title, title, htmlText.toString(),
								EmergencyActivity.class, 0);
					}
			    }
			    Looper l = Looper.myLooper();
			    if (l!=null) {
			    	l.quit();
			    }
				stopSelf();

			}
		};
		
		EmergencyParser.refreshStatus(this, handler);

		Looper.loop();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/******************************************************************/
	protected void checkClass() {

		pref = getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE); 
		
		// Get list of watched classes...
	    String alarms  = pref.getString(CoursesDataModel.PREF_KEY_STELLAR_IDS, null);
	    String updates = pref.getString(CoursesDataModel.PREF_KEY_STELLAR_LAST_UPDATES, null);
	    String reads = pref.getString(CoursesDataModel.PREF_KEY_STELLAR_READ, null);

		Log.d("NotificationService","stellar-alert: checkClass-> enter");
	    
		if (alarms!=null) {

			Log.d("NotificationService","stellar-alert: checkClass-> alarms exist");
		    
			String[] class_alarms = alarms.split("###");
			final String[] last_updates = updates.split("###");
			final String[] class_reads = reads.split("###");
			
			for (int x=0; x<class_alarms.length; x++) {
				
				String s = class_alarms[x];
				final String lu = last_updates[x];
				final int index = x;
				
				// Check each class ...
				final JSONParser cp = new CourseSubjectParser(false,true);
				String u = cp.getBaseUrl()+"&command=subjectInfo&id="+s;
				cp.getJSON(u, true);
				
				if (cp.items.size()<1) {
					Log.e("NotificationService","stellar-alert: checkClass-> no data");
					continue;
				}
				CourseItem ci = (CourseItem) cp.items.get(0);
				
				// For each Announcement...
				long last_changed = Long.valueOf(lu);
				boolean updated = false;
				for (Announcement a : ci.announcements) {
					if (a.unixtime>last_changed) {
						updated = true;
						last_changed = a.unixtime;
						last_updates[index] = String.valueOf(last_changed);
						class_reads[index] = String.valueOf(false);
					}
				}
				if (updated) {
					String title = ci.masterId + " Announcement";
					String text = ci.announcements.get(0).text;
					Log.d("NotificationService","stellar-alert: class-> " + ci.masterId + " : " + ci.title);
					notifyUser(title, title, text, CoursesTopActivity.class, x);
				}
				
				
			}  // for each class...

			SharedPreferences.Editor editor = pref.edit();
			
			// Save updated announcement times
			String concat = "";
			for (int x=0; x<last_updates.length; x++) {
				concat += last_updates[x] + "###";
			}
			editor.putString(CoursesDataModel.PREF_KEY_STELLAR_LAST_UPDATES, concat);  
			
			// Save read flag
			concat = "";
			for (int x=0; x<class_reads.length; x++) {
				concat += class_reads[x] + "###";
			}
			editor.putString(CoursesDataModel.PREF_KEY_STELLAR_READ, concat);  
			 
			boolean success = editor.commit();
			if (!success) {
				Log.e("NotificationService","stellar-alert: save-> failed commit");
			}
			
			stopSelf();
			
		}
		
		
	}
	/******************************************************************/
	protected void checkStop() {


		String stop_title = i.getStringExtra(ShuttleModel.KEY_STOP_TITLE);
		String route_id = i.getStringExtra(ShuttleModel.KEY_ROUTE_ID);
		String stop_id = i.getStringExtra(ShuttleModel.KEY_STOP_ID);
		long alarmTime = i.getLongExtra(ShuttleModel.KEY_TIME,-1);
	
		
		Stops match = null;
		
		long curTime = System.currentTimeMillis();
		int timeToArrival = (int)(alarmTime - curTime);
		
		if (timeToArrival > SHUTTLE_THRESHOLD) {

			long next = alarmTime;
			
			// Query for more accurate time...
			StopsParser sp = new StopsParser();
			sp.getJSON(RoutesParser.ROUTES_BASE_URL+"?command=stopInfo&id="+stop_id,true);
			ArrayList<Stops> sss = (ArrayList<Stops>) sp.items;
			
			if (sss.isEmpty()) {

				// stick with same alarm time...
				
			} else {
			
				// find matching route_id
				for (Stops s : sss) {
					Log.d("NotificationService","matching: " + s.route_id);
					if (route_id.equals(s.route_id)) {
						match = s;
						break;
					}
				}
		
				if (match==null) {
					Log.e("NotificationService","alert: checkStop-> no data");
					return;
				}
				
				// given route, now find closest time 
				next = match.next*1000;
				for (int p : match.predictions) {
					Log.d("NotificationService","alerts: closest: " + String.valueOf(next));
					if (alarmTime<next) {
						break;
					}
					next =+ p;
				}

				alarmTime = next;
			}

			///////////////////////////////
			
			// Reschedule...
			Log.d("NotificationService","alerts: reschedule: " + String.valueOf(alarmTime) + ", curTime=" + String.valueOf(curTime));
			rescheduleShuttleAlarm(stop_title,stop_id,route_id,alarmTime);
			
		} else {
			
			pref = getSharedPreferences(Global.PREFS_SHUTTLES,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE); 
			
			// Modify saved alerts (in case UI is running) 
			HashMap<String,HashMap <String,Long>>  alertIdx = ShuttleModel.getAlerts(pref);
			HashMap <String,Long> routes_times = alertIdx.get(stop_id);
			if (routes_times!=null) {
				routes_times.remove(route_id);
				ShuttleModel.saveAlerts(pref, alertIdx);
			}

			RouteItem routeItem = ShuttleModel.getRoute(route_id);
			String title;
			
			if (routeItem==null) title = stop_title;
			else title = routeItem.title;
			String alertText = String.format("%s in %d minutes",
					stop_title, timeToArrival / (60 * 1000));
				
			// Arrives soon... notify!
			notifyUser(title, title, alertText, ShuttlesActivity.class,0);
		}

		stopSelf();
		
	}

	/******************************************************************/
	protected void rescheduleShuttleAlarm(String title, String stop, String route, long alarmTime) {

		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);
		
		Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
		i.putExtra(ShuttleModel.KEY_STOP_TITLE, title);
		i.putExtra(ShuttleModel.KEY_STOP_ID, stop);
		i.putExtra(ShuttleModel.KEY_ROUTE_ID, route);
		i.putExtra(ShuttleModel.KEY_TIME, alarmTime);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP,  alarmTime-4*60*1000, pendingIntent);
		
	}
	/******************************************************************/
	protected void notifyUser(String statusBarText, String alarmTitle,
			String alarmText, Class<?> classname, int offset) {

		NotificationManager mgr = (NotificationManager)  ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		
		/*
		A PendingIntent itself is simply a reference to a token maintained by the system describing the original data used to retrieve it. 
		This means that, even if its owning application's process is killed, the PendingIntent itself will remain usable from other processes 
		that have been given it. If the creating application later re-retrieves the same kind of PendingIntent 
		
		(same operation, same Intent action, data, categories, and components, and same flags), >>>>> used to CANCEL
		
		it will receive a PendingIntent representing the same token if that is still valid, and can thus call cancel() to remove it.
		*/
		
		//ActivityManager am;
		//am = (ActivityManager) ctx.getSystemService( Context.ACTIVITY_SERVICE );

		/*
		//List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(100);
		List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
		boolean running = false;
		for (ActivityManager.RunningAppProcessInfo p : apps) {
			if (p.processName.equalsIgnoreCase("edu.mit.mitmobile")) {
				if (p.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) running = true;
			}
		}
		*/
		
		Intent startIntent = new Intent(ctx, classname);
		
		PendingIntent contentIntent = null;
		//if (!running)  contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, classname), 0);
		contentIntent = PendingIntent.getActivity(ctx, 0, startIntent, 0);
		
		long curTime = System.currentTimeMillis();
		
		Notification notify = new Notification(icon, statusBarText, curTime);
		notify.setLatestEventInfo(ctx, alarmTitle, alarmText, contentIntent);
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		notify.defaults = Notification.DEFAULT_ALL;
		//notify.sound = (Uri) intent.getParcelableExtra("Ringtone");
		//notify.vibrate = (long[]) intent.getExtras().get("vibrationPatern");

		// Launch if selected...
		mgr.notify((int) curTime+offset, notify);  // use current time as id for unique notifications
		
		
	}
	/******************************************************************/
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
