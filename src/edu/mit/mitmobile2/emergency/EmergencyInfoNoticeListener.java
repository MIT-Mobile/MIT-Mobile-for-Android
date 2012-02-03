package edu.mit.mitmobile2.emergency;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.alerts.C2DMReceiver.NoticeListener;

public class EmergencyInfoNoticeListener extends NoticeListener {

	@Override
	public void onReceivedNotice(Context context, JSONObject object) {
		try {
			String title = context.getString(R.string.emergency_alert_title);
			String message;
			message = object.getJSONObject("aps").getString("alert");
			notifyUser(context, title, title, message,R.drawable.alert_emergency, EmergencyActivity.class);		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("EmergencyNoticeListener", "Failed at parsing emergency notice");
		}
	}

}
