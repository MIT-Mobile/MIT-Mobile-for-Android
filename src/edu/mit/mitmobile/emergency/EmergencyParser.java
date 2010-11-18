package edu.mit.mitmobile.emergency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import edu.mit.mitmobile.Global;
import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.alerts.NotificationService;
import edu.mit.mitmobile.objs.EmergencyItem;
import edu.mit.mitmobile.objs.EmergencyItem.Contact;

public class EmergencyParser {
	
	public static final int RECEIVED_STATUS = 10;
	static final String PREFS_CONTACTS_UPDATED = "emergency_contacts_updated";

	private static final int CONTACTS_CACHE_HOURS = 72;
	
	private static EmergencyItem sEmergencyItem = null;

	public static EmergencyItem getStatus() {
		return sEmergencyItem;
	}
	
	public static void fetchContacts(Context context, final Handler uiHandler) {
		SharedPreferences prefs = context.getSharedPreferences(Global.PREFS, Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();
		int cacheAge = (int)(System.currentTimeMillis() / 1000 / 60 / 60)
			- prefs.getInt(PREFS_CONTACTS_UPDATED, 0);
		
		if (cacheAge < CONTACTS_CACHE_HOURS) {
			MobileWebApi.sendSuccessMessage(uiHandler);
		} else {
			MobileWebApi api = new MobileWebApi(false, true, "Emergency", context, uiHandler);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "emergency");
			params.put("command", "contacts");
			final EmergencyDB db = EmergencyDB.getInstance(context);
			api.requestJSONArray(params, new MobileWebApi.JSONArrayResponseListener(
	                new MobileWebApi.DefaultErrorListener(uiHandler),
	                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
				@Override
				public void onResponse(JSONArray array) {
					List<Contact> contacts = parseContacts(array);
					if (contacts != null) {
						db.clearAll();
						for (Contact c : contacts) {
							db.addContact(c);
						}
					}
					int time = (int)(System.currentTimeMillis() / (1000 * 60 * 60));
					editor.putInt(PREFS_CONTACTS_UPDATED, time);
					editor.commit();
					MobileWebApi.sendSuccessMessage(uiHandler);
				}
			});
		}
	}
	
	public static void refreshStatus(Context context, final Handler handler) {
		MobileWebApi api = null;
		if (context.getClass().equals(NotificationService.class)) {
			api = new MobileWebApi(false, false, "Emergency", context, handler);
		} else {
			api = new MobileWebApi(false, true, "Emergency", context, handler);
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "emergency");
		api.requestJSONArray(params, new MobileWebApi.JSONArrayResponseListener(
                new MobileWebApi.DefaultErrorListener(handler),
                new MobileWebApi.DefaultCancelRequestListener(handler)) {
			@Override
			public void onResponse(JSONArray array) {
				sEmergencyItem = parseStatus(array);
				MobileWebApi.sendSuccessMessage(handler);
			}
		});
	}
	
	public static void fetchStatus(Context context, final Handler handler) {
		if (sEmergencyItem == null) {
			refreshStatus(context, handler);
		} else {
			MobileWebApi.sendSuccessMessage(handler);
		}
	}
	
	static EmergencyItem parseStatus(JSONArray array) {
		EmergencyItem item = new EmergencyItem();
    	
		try {
			JSONObject jsonObj = (JSONObject) array.get(0);
			item.text = jsonObj.getString("text");
			item.title = jsonObj.getString("title");
			
			item.version = jsonObj.getString("version");
			if (item.version==null) {
				Log.d("EmergencyParser","EmergencyParser: null version");
			}
			item.version = jsonObj.optString("version", "0");
			
			item.unixtime = jsonObj.getLong("unixtime");

			////////////////
			
			JSONObject jDate = jsonObj.optJSONObject("date");
			
			item.date.year = jDate.getInt("year");
			item.date.month = jDate.getInt("month");
			item.date.day = jDate.getInt("day");
			item.date.hour = jDate.getInt("hour");
			item.date.second = jDate.getInt("second");
			item.date.fraction = jDate.getInt("fraction");

			item.date.warning_count = jDate.getInt("warning_count");
			item.date.error_count = jDate.getInt("error_count");

			JSONArray jMsgs = jDate.optJSONArray("warnings");

			if (jMsgs!=null) {
				item.date.warnings = new String[jMsgs.length()];
				for(int s=0; s<jMsgs.length(); s++)
				{
					item.date.warnings[s]=jMsgs.getString(s);
				}
			}

			jMsgs = jDate.optJSONArray("errors");

			if (jMsgs!=null) {
				item.date.errors = new String[jMsgs.length()];
				for(int s=0; s<jMsgs.length(); s++)
				{
					item.date.errors[s]=jMsgs.getString(s);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			
		}
		
		return item;
	}
	
	
	static List<Contact> parseContacts(JSONArray array) {

		ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject jsonObj = array.getJSONObject(i);

				Contact contact = new EmergencyItem.Contact();

				contact.phone = jsonObj.getString("phone");
				contact.contact = jsonObj.getString("contact");
				if (jsonObj.has("description")) {
					contact.description = jsonObj.getString("description");
				}

				contacts.add(contact);
				
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return contacts;
	}
	
	
}
