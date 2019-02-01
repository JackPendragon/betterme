package com.hauff.patrick.betterme.notofications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;
import java.util.Random;

/**
 * Our Receiver to send a alter notification
 */
public class AlertReceiver extends BroadcastReceiver {

    NotificationHelper notificationHelper;                                                          //Our Notoficationhelper

    /**
     * Our onReceive override method to set our Notification
     *
     * @param context context
     * @param intent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationHelper = new NotificationHelper(context);

        String title = Objects.requireNonNull(intent.getExtras()).getString("activity");                               //Set our Msg.
        String body = intent.getExtras().getString("description");

        Notification.Builder builder = notificationHelper.getEMDTChannelNotification(title, body);
        notificationHelper.getManager().notify(new Random().nextInt(), builder.build());
    }
}
