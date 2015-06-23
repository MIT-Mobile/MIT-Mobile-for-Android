package edu.mit.mitmobile2.shuttles;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shared.MITContentProvider;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int alarmId = intent.getIntExtra(Constants.ALARM_ID_KEY, -1);
        String description = intent.getStringExtra(Constants.ALARM_DESCRIPTION);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent moduleIntent = new Intent(context, MITMainActivity.class);
        moduleIntent.setData(Uri.parse("mitmobile2://shuttles"));
        moduleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, moduleIntent, PendingIntent.FLAG_ONE_SHOT);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(1000);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.alarm_icon)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.alarm_checked_icon))
                        .setContentTitle("MIT")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(description))
                        .setLights(Color.argb(255, 226, 70, 47), 1000, 1000)
                        .setContentText(description)
                        .setContentIntent(pendingIntent);

        mNotificationManager.notify(1, mBuilder.build());
        context.getContentResolver().delete(MITContentProvider.ALERTS_URI, Schema.Alerts.ID_COL + "=" + alarmId, null);
    }
}
