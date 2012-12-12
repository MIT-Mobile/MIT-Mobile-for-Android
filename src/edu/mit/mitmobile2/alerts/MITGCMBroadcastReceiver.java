package edu.mit.mitmobile2.alerts;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class MITGCMBroadcastReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "edu.mit.mitmobile2.alerts.GCMIntentService";
	}
}
