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

import com.addie.maxfocus.R;
import com.addie.maxfocus.ui.DialogActivity;
import com.rvalerio.fgchecker.AppChecker;

import timber.log.Timber;

public class AppTimeDialogService extends Service {

    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_IN_USE_KEY = "app_in_use";
    private static final int NOTIF_ID = 77;
    private SharedPreferences preferences;

    private int appTime;
    private boolean hasUsageAccess;
    private String targetPackage;


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

        if (intent == null){
            Timber.e("Service started with null intent. Stopping self");
            stopSelf();
        }
        appTime = intent.getIntExtra(TIME_KEY, 0);
        targetPackage = intent.getStringExtra(TARGET_PACKAGE_KEY);
        Timber.d("apptime is " + appTime + " targetpackage is " + targetPackage);

        Timber.i("Starting timer in ATDService...");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);
        cdt = new CountDownTimer(appTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Timber.i("Countdown seconds remaining in ATDService: " + millisUntilFinished / 1000);
//                bi.putExtra("countdown", millisUntilFinished);
//                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {

                Timber.i("Timer finished");

                Timber.d("Starting activity");


                Intent dialogIntent = new Intent(AppTimeDialogService.this, DialogActivity.class);
                dialogIntent.putExtra(TARGET_PACKAGE_KEY, targetPackage);


                if (hasUsageAccess) {

                    // Checks which app is in foreground
                    AppChecker appChecker = new AppChecker();
                    String packageName = appChecker.getForegroundApp(AppTimeDialogService.this);

                    // Creates intent to display
                    // Adds boolean which contains value if app is still in foreground
                    boolean appInUse = false;
                    if (packageName.equals(targetPackage)) {
                        Timber.d("App is in use");
                        appInUse = true;
                        startActivity(dialogIntent);
                    } else {
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                        Notification.Builder builder = new Notification.Builder(AppTimeDialogService.this);

                        //Fetches data of target app
                        ApplicationInfo ai;
                        PackageManager pm = getPackageManager();
                        Bitmap icon;
                        try {
                            icon = ((BitmapDrawable) pm.getApplicationIcon(targetPackage)).getBitmap();
                            ai = pm.getApplicationInfo(targetPackage, 0);
                        } catch (final PackageManager.NameNotFoundException e) {
                            ai = null;
                            icon = null;
                        }
                        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");



                        String title = applicationName + " " + getString(R.string.app_closed_notification_title);
                        builder.setContentTitle(title)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setContentIntent(PendingIntent.getActivity(AppTimeDialogService.this,
                                        0, new Intent(), 0))
                                .setLargeIcon(icon)
                                .setSubText(getString(R.string.app_closed_notification_subtitle))
                                .setAutoCancel(true);
                        Notification notification = builder.build();
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        if (notificationManager != null) {
                            notificationManager.notify(NOTIF_ID, notification);
                        }

                    }
                } else {
                    startActivity(dialogIntent);
                }
                stopSelf();
            }
        };

        cdt.start();


        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }
}
