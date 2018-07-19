/*
 * MIT License
 *
 * Copyright (c) 2018 aSoft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.addie.timesapp.service;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.addie.timesapp.R;
import com.addie.timesapp.ui.DialogActivity;
import com.addie.timesapp.ui.ForegroundServiceActivity;
import com.addie.timesapp.utils.Utils;
import com.rvalerio.fgchecker.AppChecker;

import timber.log.Timber;

/**
 * Foreground service that handles background tasks of counting time,launching stop dialog etc
 */
public class AppTimeDialogService extends Service {

    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_COLOR_KEY = "app_color";
    private static final String TEXT_COLOR_KEY = "text_color";
    private static final int APP_STOPPED_NOTIF_ID = 77;
    private static final String CALLING_CLASS_KEY = "calling_class";
    private SharedPreferences preferences;
    private static final int FOREGROUND_NOTIF_ID = 104;
    private static final String DISPLAY_1_MIN = "display_1_min";

    private int appTime;
    private boolean hasUsageAccess;
    private String targetPackage;
    private String mAppName;
    private Bitmap mAppIcon;
    private int mAppColor;
    private int mTextColor;

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopSelf();
        } else {

            initialiseVariables(intent);

            checkIfPermissionGrantedManually();

            fetchAppData();

            runForegroundService();

            setupAndStartCDT();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void checkIfPermissionGrantedManually() {
        // Check if permission has been granted manually
        if (!preferences.getBoolean(getString(R.string.usage_permission_pref), false)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && hasUsageStatsPermission(this)) {
                preferences.edit().putBoolean(getString(R.string.usage_permission_pref), true).apply();

            }
        }
        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);
    }

    /**
     * Initialises variables to be used
     *
     * @param intent starting intent
     */
    private void initialiseVariables(Intent intent) {
        if (cdt != null) {
            cdt.cancel();
        }
        appTime = intent.getIntExtra(TIME_KEY, 0);
        targetPackage = intent.getStringExtra(TARGET_PACKAGE_KEY);
        mAppColor = intent.getIntExtra(APP_COLOR_KEY, getResources().getColor(R.color.black));
        mTextColor = intent.getIntExtra(TEXT_COLOR_KEY, getResources().getColor(R.color.white));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    /**
     * Starts countdown timer for required time as specified by the class starting this service
     */
    private void setupAndStartCDT() {
        cdt = new CountDownTimer(appTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Timber.i("Countdown seconds remaining in ATDService: %s", millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                Timber.i("Timer finished.Starting activity");

                showStopDialog();

                stopForeground(true);

                stopSelf();
            }
        };
        cdt.start();
    }

    /**
     * Displays stop dialog on top of the current activity ( has a transparent background due to DialogActivity)
     */
    private void showStopDialog() {
        Intent dialogIntent = new Intent(AppTimeDialogService.this, DialogActivity.class);
        dialogIntent.putExtra(TARGET_PACKAGE_KEY, targetPackage);
        dialogIntent.putExtra(APP_COLOR_KEY, mAppColor);
        dialogIntent.putExtra(TEXT_COLOR_KEY, mTextColor);
        dialogIntent.putExtra(CALLING_CLASS_KEY, getClass().getSimpleName());
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Duration equal to 1 minute
        if (appTime == 60000)
            dialogIntent.putExtra(DISPLAY_1_MIN, false);

        if (hasUsageAccess) {

            // Checks which app is in foreground
            AppChecker appChecker = new AppChecker();
            String packageName = appChecker.getForegroundApp(AppTimeDialogService.this);

            // Creates intent to display
            if (packageName.equals(targetPackage)) {
                Timber.d("App is in use");
                startActivity(dialogIntent);
            } else {
                issueAppStoppedNotification();
            }
        }
        // No usage permission, show dialog without checking foreground app
        else {
            startActivity(dialogIntent);
        }
    }

    /**
     * Checks if usage permission has been granted
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        preferences.edit().putBoolean(getString(R.string.usage_permission_pref), granted).apply();

        return granted;
    }

    /**
     * Fetches data of target app i.e. application name and icon
     */
    private void fetchAppData() {

        ApplicationInfo appInfo;
        PackageManager pm = getPackageManager();

        try {
            Drawable iconDrawable = pm.getApplicationIcon(targetPackage);

            mAppIcon = Utils.getBitmapFromDrawable(iconDrawable);
            appInfo = pm.getApplicationInfo(targetPackage, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            appInfo = null;
            mAppIcon = null;
        }
        mAppName = (String) (appInfo != null ? pm.getApplicationLabel(appInfo) : "(unknown)");

    }

    /**
     * App is no longer running. Display notification instead of dialog
     */
    private void issueAppStoppedNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "timesapp_app_stopped";// The id of the channel.
            String channelName = getString(R.string.notif_app_stopped_channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channelId = createNotificationChannel(CHANNEL_ID, channelName, importance);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AppTimeDialogService.this, channelId);

        String title = mAppName + " " + getString(R.string.app_closed_notification_title);
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.app_notification_icon)
                .setContentIntent(PendingIntent.getActivity(AppTimeDialogService.this,
                        0, new Intent(), 0))
                .setLargeIcon(mAppIcon)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSubText(getString(R.string.app_closed_notification_subtitle))
                .setAutoCancel(true);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.notify(APP_STOPPED_NOTIF_ID, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    private void runForegroundService() {

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("timesapp_fg_service", "Background Service Notification", NotificationManager.IMPORTANCE_NONE);
        }
        Intent notificationIntent = new Intent(this, ForegroundServiceActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        Intent appLaunchIntent = getPackageManager().getLaunchIntentForPackage(targetPackage);

        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 1, appLaunchIntent, 0);
        NotificationCompat.Action.Builder actionBuilder =
                new NotificationCompat.Action.Builder(R.drawable.ic_exit_to_app_black_24dp,
                        "Return to " + mAppName, actionPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_SERVICE);
        }
        Notification notification = builder.setOngoing(true)
                .setContentText(getString(R.string.app_running_service_notif_text))
                .setSubText(getString(R.string.tap_for_more_info_foreground_notif))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .addAction(actionBuilder.build())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.app_notification_icon)
                .setContentIntent(pendingIntent).build();

        startForeground(FOREGROUND_NOTIF_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName, int importance) {

        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, importance);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(chan);
        }
        return channelId;
    }
}
