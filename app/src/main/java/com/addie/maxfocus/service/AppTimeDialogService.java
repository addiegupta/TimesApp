package com.addie.maxfocus.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.addie.maxfocus.R;
import com.addie.maxfocus.ui.DialogActivity;
import com.addie.maxfocus.ui.ForegroundServiceActivity;
import com.rvalerio.fgchecker.AppChecker;

import timber.log.Timber;

public class AppTimeDialogService extends Service {

    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_COLOR_KEY = "app_color";
    private static final String APP_IN_USE_KEY = "app_in_use";
    private static final int APP_STOPPED_NOTIF_ID = 77;
    private SharedPreferences preferences;
    private static final int FOREGROUND_NOTIF_ID = 104;
    private static final String DISPLAY_1_MIN = "display_1_min";

    private int appTime;
    private boolean hasUsageAccess;
    private String targetPackage;
    private String mAppName;
    private Bitmap mAppIcon;
    private int mAppColor;

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        Timber.i("Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.plant(new Timber.DebugTree());

        if (intent == null) {
            Timber.e("Service started with null intent. Stopping self");
            stopSelf();
        } else {
            Timber.i("Starting timer in ATDService...");

            initialiseVariables(intent);

            fetchAppData();

            runForegroundService();

            setupAndStartCDT();


        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initialiseVariables(Intent intent) {
        if (cdt!=null){
            cdt.cancel();
        }
        appTime = intent.getIntExtra(TIME_KEY, 0);
        targetPackage = intent.getStringExtra(TARGET_PACKAGE_KEY);
        mAppColor = intent.getIntExtra(APP_COLOR_KEY,getResources().getColor(R.color.black));
        Timber.d("apptime is " + appTime + " targetpackage is " + targetPackage);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);

    }

    private void setupAndStartCDT() {
        cdt = new CountDownTimer(appTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Timber.i("Countdown seconds remaining in ATDService: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                Timber.i("Timer finished");

                Timber.d("Starting activity");

                showStopDialog();

                stopForeground(true);

                stopSelf();

            }
        };

        cdt.start();

    }

    private void showStopDialog() {
        Intent dialogIntent = new Intent(AppTimeDialogService.this, DialogActivity.class);
        dialogIntent.putExtra(TARGET_PACKAGE_KEY, targetPackage);
        dialogIntent.putExtra(APP_COLOR_KEY, mAppColor);
        if (appTime==60000)
            dialogIntent.putExtra(DISPLAY_1_MIN,false);

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
     * Fetches data of target app i.e. application name and icon
     */
    private void fetchAppData() {

        ApplicationInfo appInfo;
        PackageManager pm = getPackageManager();

        try {
            mAppIcon = ((BitmapDrawable) pm.getApplicationIcon(targetPackage)).getBitmap();
            appInfo = pm.getApplicationInfo(targetPackage, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            appInfo = null;
            mAppIcon = null;
        }
        mAppName = (String) (appInfo != null ? pm.getApplicationLabel(appInfo) : "(unknown)");

    }

    private void issueAppStoppedNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(AppTimeDialogService.this);

        String title = mAppName + " " + getString(R.string.app_closed_notification_title);
        builder.setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(PendingIntent.getActivity(AppTimeDialogService.this,
                        0, new Intent(), 0))
                .setLargeIcon(mAppIcon)
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
        Intent notificationIntent = new Intent(this, ForegroundServiceActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent appLaunchIntent = getPackageManager().getLaunchIntentForPackage(targetPackage);

        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 1, appLaunchIntent, 0);
        NotificationCompat.Action.Builder actionBuilder =
                new NotificationCompat.Action.Builder(R.drawable.ic_exit_to_app_black_24dp,
                        "Return to " + mAppName, actionPendingIntent);

        Notification notification = builder
                .setContentText(getString(R.string.app_running_service_notif_text))
                .setSubText(getString(R.string.tap_for_more_info_foreground_notif))
                .addAction(actionBuilder.build())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        startForeground(FOREGROUND_NOTIF_ID, notification);
    }
}
