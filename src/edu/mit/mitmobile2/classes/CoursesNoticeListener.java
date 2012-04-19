package edu.mit.mitmobile2.classes;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.alerts.C2DMReceiver.NoticeListener;

public class CoursesNoticeListener extends NoticeListener {

	@Override
	public void onReceivedNotice(Context context, JSONObject object) {
		try {
			String tag = object.getString("tag");
			String[] tagParts = tag.split(":");
			String courseID = tagParts[1];
			String title = "Stellar " + courseID;
			String message = object.getJSONObject("aps").getString("title");
			
			Intent intent = new Intent(context, MITCoursesDetailsSliderActivity.class);
			intent.putExtra(MITCoursesDetailsSliderActivity.MY_STELLAR_KEY, true);
			intent.putExtra(MITCoursesDetailsSliderActivity.SUBJECT_MASTER_ID_KEY, courseID); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			notifyUser(context, title, title, message, R.drawable.alert_stellar, pendingIntent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("CourseNoticeListener", "Failed at parsing emergency notice");
		}
	}

}
