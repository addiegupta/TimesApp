package com.addie.maxfocus.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.addie.maxfocus.R;
import com.addie.maxfocus.ui.DialogActivity;
import com.rvalerio.fgchecker.AppChecker;

import timber.log.Timber;

public class AppTimeDialogService extends Service {

    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_IN_USE_KEY = "app_in_use";
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

                Log.i(" ", "Timer finished");

                Timber.d("Starting activity");
                Intent dialogIntent = new Intent(AppTimeDialogService.this, DialogActivity.class);

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
                    }
                    dialogIntent.putExtra(APP_IN_USE_KEY, appInUse);
                }
                startActivity(dialogIntent);


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
