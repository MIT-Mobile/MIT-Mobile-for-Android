package edu.mit.mitmobile2.shuttles;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.PowerManager;
import android.os.Vibrator;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.activities.ShuttleStopActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String stopId = intent.getStringExtra(Constants.STOP_ID_KEY);
        String routeId = intent.getStringExtra(Constants.ROUTE_ID_KEY);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent moduleIntent = new Intent(context, ShuttleStopActivity.class);
        moduleIntent.putExtra(Constants.ROUTE_ID_KEY, routeId);
        moduleIntent.putExtra(Constants.STOP_ID_KEY, stopId);
        moduleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, moduleIntent, PendingIntent.FLAG_ONE_SHOT);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(1000);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.alert_icon)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.alert_icon_outline))
                        .setContentTitle("MIT")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText("MIT"))
                        .setLights(Color.argb(255, 226, 70, 47), 1000, 1000)
                        .setContentText("Shuttle Arriving")
                        .setContentIntent(pendingIntent);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
