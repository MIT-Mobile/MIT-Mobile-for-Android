package edu.mit.mitmobile2.alerts;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.JSONArrayResponseListener;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.MobileWebApi.IgnoreErrorListener;
import edu.mit.mitmobile2.about.BuildSettings;
import edu.mit.mitmobile2.classes.CoursesNoticeListener;
import edu.mit.mitmobile2.emergency.EmergencyInfoNoticeListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.util.Log;

public class C2DMReceiver extends BroadcastReceiver {

	public static abstract class NoticeListener {
		abstract public void onReceivedNotice(Context context, JSONObject object, int noticeCount);
		
		protected void notifyUser(Context context, String statusBarText, String alarmTitle,
				String alarmText, int iconResID, Class<?> classname) {
		
			Intent startIntent = new Intent(context, classname);		
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startIntent, 0);
			
			notifyUser(context, statusBarText, alarmTitle, alarmText, iconResID, pendingIntent);
		}
		
		
		protected void notifyUser(Context context, String statusBarText, String alarmTitle,
				String alarmText, int iconResID, PendingIntent pendingIntent ) {

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			long curTime = System.currentTimeMillis();
			
			Notification notify = new Notification(iconResID, statusBarText, curTime);
			notify.setLatestEventInfo(context, alarmTitle, alarmText, pendingIntent);
			notify.flags = Notification.FLAG_AUTO_CANCEL;
			notify.defaults = Notification.DEFAULT_ALL;

			// Launch if selected...
			notificationManager.notify((int) curTime, notify);  // use current time as id for unique notifications
			
			
		}
	}
	
	// This only get set after the
	// server has confirmed receipt of the registration id
	private static String sRegistrationID;
	
	public static void registerForNotifications(Context context) {
		if (sRegistrationID == null) {
			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0)); // boilerplate
			registrationIntent.putExtra("sender", BuildSettings.C2DM_SENDER);
			context.startService(registrationIntent);	
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("action", intent.getAction());
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			String registrationID = intent.getStringExtra("registration_id");
			if (registrationID != null) {
				registerDevice(context, registrationID);
			}
			
		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			try {
				final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
				long deviceID = preferences.getLong(DEVICE_ID_KEY, -1);
				
				JSONObject payloadObject = new JSONObject(intent.getExtras().getString("payload"));
				String tag = payloadObject.getString("tag");
				int recipientDeviceID = payloadObject.getInt("deviceID");
				if (recipientDeviceID != deviceID) {
					// notice not intended for this recipient
					// user probably uninstalled and reinstalled the app
					return;
				}

				NoticeListener noticeListener = null;
				if (tag.startsWith("emergencyinfo:")) {
					noticeListener = new EmergencyInfoNoticeListener();
				} else if (tag.startsWith("stellar:")) {
					noticeListener = new CoursesNoticeListener();
				}
				if (noticeListener != null) {
					noticeListener.onReceivedNotice(context, payloadObject, incrementNoticeCount(context));
				} else {
					Log.d("C2DMReceiver", "Could not find a target for notification with tag=" + tag);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("C2DMReceiver", "Failed at parsing incoming notification");
			}
		}
		
	}
	
	private final static String PREFERENCES = "DeviceRegistration";
	private final static String DEVICE_ID_KEY = "device_id";
	private final static String DEVICE_PASS_KEY = "pass_key";
	private final static String NOTICE_COUNT_KEY = "notice_count"; 
	
	public static void clearDeviceRegistration(Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putLong(DEVICE_ID_KEY, -1);
		editor.commit();
		
		sRegistrationID = null;
	}
	
	private void registerDevice(final Context context, final String registrationID) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		MobileWebApi api = new MobileWebApi(false, false, null, context, null);
		long deviceID = preferences.getLong(DEVICE_ID_KEY, -1);
		String appID = BuildSettings.release_project_name;
		if (deviceID == -1) {
			// need register the device
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "push");
			params.put("command", "register");
			params.put("device_type", "android");
			params.put("app_id", appID);
			params.put("device_token", registrationID);	
			
			api.requestJSONObject(params, new JSONObjectResponseListener(
				new IgnoreErrorListener(), null)  {

				@Override
				public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
					Editor editor = preferences.edit();
					editor.putLong(DEVICE_ID_KEY, object.getLong(DEVICE_ID_KEY));
					editor.putLong(DEVICE_PASS_KEY, object.getLong(DEVICE_PASS_KEY));
					editor.commit();
					sRegistrationID = registrationID;
					
					Global.onDeviceRegisterCompleted();
				}
			});
		} else {
			// need to change our token
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "push");
			params.put("command", "newDeviceToken");
			params.put("device_type", "android");
			params.put("app_id", appID);
			params.put("device_token", registrationID);	
			params.put("device_id", Long.toString(deviceID));
			params.put("pass_key", Long.toString(preferences.getLong(DEVICE_PASS_KEY, 0)));
			api.requestJSONObject(params, new JSONObjectResponseListener(
				new IgnoreErrorListener(), null)  {

				@Override
				public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
					// nothing to do
					if (object.getBoolean("success")) {
						sRegistrationID = registrationID;	
						
						Global.onDeviceRegisterCompleted();
					} else {
						Log.d("C2DMReceiver", "Device token failed to registered");
					}
				}
			});		
		}
	}

	public static void markNotificationAsRead(Context context, String tag) {
		JSONStringer encoder = new JSONStringer();
		try {
			encoder.array();
			encoder.value(tag);
			encoder.endArray();
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("C2DMReceiver", "Failed to encode tag=" + tag);
		}

		Device device = getDevice(context);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "push");
		params.put("command", "markNotificationsAsRead");
		params.put("tags", encoder.toString());
		params.put("device_type", "android");
		params.put("app_id", BuildSettings.release_project_name);	
		params.put("device_id", Long.toString(device.mDeviceId));
		params.put("pass_key", Long.toString(device.mPassKey));
		
		MobileWebApi api = new MobileWebApi(false, false, null, context, null);
		api.requestJSONArray(params, new JSONArrayResponseListener(
				new IgnoreErrorListener(), null)  {

					@Override
					public void onResponse(JSONArray array)
							throws ServerResponseException, JSONException {						
					}
		});
		
	}
	static public class Device {
		private long mDeviceId;
		private long mPassKey;
	
		Device(long deviceId, long passKey) {
			mDeviceId = deviceId;
			mPassKey = passKey;
		}
		
		public long getDeviceId() {
			return mDeviceId;
		}
		
		public long getPassKey() {
			return mPassKey;
		}
	}
	
	public static Device getDevice(Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		
		return new Device(
			preferences.getLong(DEVICE_ID_KEY, -1),
			preferences.getLong(DEVICE_PASS_KEY, 0)
		);
	}
	
	
	private int incrementNoticeCount(Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		int count = preferences.getInt(NOTICE_COUNT_KEY, 0);
		Editor editor = preferences.edit();
		editor.putInt(NOTICE_COUNT_KEY, count+1);
		editor.commit();
		return count+1;		
	}
}
