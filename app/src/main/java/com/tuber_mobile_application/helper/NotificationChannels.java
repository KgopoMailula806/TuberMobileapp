package com.tuber_mobile_application.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tuber_mobile_application.R;

public class NotificationChannels extends ContextWrapper
{
    private NotificationManager notificationManager;

    public NotificationChannels(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createTuberChannels();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTuberChannels()
    {
        NotificationChannel requestChannel = new NotificationChannel(App_Global_Variables.NOTIFICATION_CHANNEL_REQUEST_ID,
                "Requests", NotificationManager.IMPORTANCE_HIGH);
        requestChannel.enableLights(true);
        requestChannel.enableVibration(true);
        requestChannel.setLightColor(R.color.colorPrimary);
        requestChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(requestChannel);

        NotificationChannel sessionChannel = new NotificationChannel(App_Global_Variables.NOTIFICATION_CHANNEL_SESSION_ID,
                "Sessions", NotificationManager.IMPORTANCE_HIGH);
        requestChannel.enableLights(true);
        requestChannel.enableVibration(true);
        requestChannel.setLightColor(R.color.colorPrimary);
        requestChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(sessionChannel);

    }

    public NotificationManager getManager()
    {
        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return  notificationManager;
    }

    public NotificationCompat.Builder getRequestChannel(String title, String Message)
    {
        return  new NotificationCompat.Builder(getApplicationContext(), App_Global_Variables.NOTIFICATION_CHANNEL_REQUEST_ID)
                .setContentTitle(title)
                .setContentText(Message)
                .setSmallIcon(R.drawable.tuber);
    }

    public NotificationCompat.Builder getSessionChannel(String title, String Message)
    {
        return  new NotificationCompat.Builder(getApplicationContext(), App_Global_Variables.NOTIFICATION_CHANNEL_SESSION_ID)
                .setContentTitle(title)
                .setContentText(Message)
                .setSmallIcon(R.drawable.tuber);
    }
}
