package com.tuber_mobile_application.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationChannels channel = new NotificationChannels(context);
        NotificationCompat.Builder builder = channel.getSessionChannel("Tutorial Session Rating Reminder","Don't forget to rate the session");
        channel.getManager().notify(1, builder.build());

    }
}
