package com.tuber_mobile_application.helper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.tuber_mobile_application.Home;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.R;

public class NotificationMaker
{
    private String notificationTitle;
    private String notificationMessage;
    private Context context;
    private Activity activity;
    private String parcel;



    public NotificationMaker(Activity currentActivity, Context context, String notificationTitle, String notificationMessage, String parcel)
    {

        activity = currentActivity;
        this.context = context;
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.parcel = parcel; // bulk information for the notification
    }

    public void sendNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.tuber)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setAutoCancel(true);

        Intent intent = new Intent(context, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Parcel",parcel);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

    }
}
