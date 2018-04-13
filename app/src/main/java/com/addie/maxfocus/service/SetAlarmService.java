package com.addie.maxfocus.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;

import com.addie.maxfocus.R;

import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by addie on 13-04-2018.
 */

public class SetAlarmService extends IntentService {
    private static final int ALARM_NOTIF_ID = 234;

    public SetAlarmService() {
        super("SetAlarmService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 8); // adds one hour
        Timber.d(cal.getTime().toString()); // returns new date object, one hour in the future
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);


        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        startActivity(alarmIntent);


        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm has been set!";
            String description = "Time to set the phone aside";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("a", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        } else {

            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Alarm has been set!")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText("It's time to set the phone aside");
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationManager.notify(ALARM_NOTIF_ID, mBuilder.build());
        }

    }
}
