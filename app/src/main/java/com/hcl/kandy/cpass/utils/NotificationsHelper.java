package com.hcl.kandy.cpass.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.utilities.Globals;

public class NotificationsHelper {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6578;

    private NotificationsHelper() {}

    public static void showNotifications(String title, String msg, Intent intent) {
        Context applicationContext = Globals.getApplicationContext();

        NotificationManager manager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "my_channel_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            manager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentText(msg)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
