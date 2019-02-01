package com.hauff.patrick.betterme.notofications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;

import com.hauff.patrick.betterme.R;
import com.hauff.patrick.betterme.entry.Entry;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Our NotificationHelper class to manage the notifications
 */
public class NotificationHelper extends ContextWrapper {

    private static final String EDMT_CHANNEL_ID = "emdt.dev.androidnotificationchannel.EDMTDEV";    //our help variables
    private static final String EDMT_CHANNEL_NAME = "EDMTDEV Channel";
    private NotificationManager manager;

    /**
     * Notification constructor
     *
     * @param base base
     */
    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    /**
     * To create a channel for our notification
     */
    public void createChannels(){
        NotificationChannel edmtChannel = new NotificationChannel(EDMT_CHANNEL_ID, EDMT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        edmtChannel.enableLights(true);
        edmtChannel.enableVibration(true);
        edmtChannel.setLightColor(Color.GREEN);
        edmtChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(edmtChannel);
    }

    /**
     * To get our Manager
     *
     * @return manager
     */
    public NotificationManager getManager(){
        if(manager == null) manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    /**
     * To get our EMT Channel Notification
     *
     * @param title title
     * @param body body
     * @return Notification Notification
     */
    public Notification.Builder getEMDTChannelNotification(String title, String body){
        return new Notification.Builder(getApplicationContext(), EDMT_CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_betterme_logo);
    }

    /**
     * To set our Notification time
     *
     * @param notifications notifications
     * @param dayList dayList
     */
    public void setNotificationTime(ArrayList<String> notifications, ArrayList<Entry> dayList){
        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < notifications.size(); i++) {                                             //If isDone null we return it as false
            if(dayList.get(i).isDone() == null){
                dayList.get(i).setDone(false);
            }
            if(!dayList.get(i).isDone()){                                                   //Notification only when a activity is not done
                String notificationTime = notifications.get(i);
                int hour = Integer.parseInt(notificationTime.substring(0, 2));
                int minutes = Integer.parseInt(notificationTime.substring(3, 5));

                calendar.set(Calendar.HOUR_OF_DAY, hour);                                           //Set the Notification time
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, 0);

                startAlarm(calendar, i, dayList.get(i).getActivity(), dayList.get(i).getDescription());
            }
        }
    }

    /**
     * To start our alarm
     *
     * @param calendar calendar
     * @param i i
     * @param activity activity
     * @param description description
     */
    private void startAlarm(Calendar calendar, int i, String activity, String description){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("activity", activity);
        intent.putExtra("description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
