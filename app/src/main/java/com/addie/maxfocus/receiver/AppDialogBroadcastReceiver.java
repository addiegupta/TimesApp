package com.addie.maxfocus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.addie.maxfocus.ui.DialogActivity;
import com.rvalerio.fgchecker.AppChecker;

import timber.log.Timber;

/**
 * Receives intent to display dialog after selected time has passed.
 * Launches DialogActivity to display an alert dialog
 */
public class AppDialogBroadcastReceiver extends BroadcastReceiver {


    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_IN_USE_KEY = "app_in_use";

    CountDownTimer cdt = null;


    @Override
    public void onReceive(final Context context, Intent intent) {

        Timber.d("Broadcast received");
        final int time = intent.getIntExtra(TIME_KEY, 0);
        final String targetPackage = intent.getStringExtra(TARGET_PACKAGE_KEY);

        //TODO Change first argument to time
        // Counts till the specified time before launching dialog activity
        cdt = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long l) {
                Timber.d("Tick tick " + l);
            }

            @Override
            public void onFinish() {
                Timber.d("Starting activity");

                AppChecker appChecker = new AppChecker();
                String packageName = appChecker.getForegroundApp(context);
                Timber.d(packageName);
                Intent dialogIntent = new Intent(context,DialogActivity.class);
                boolean appInUse = false;
                Timber.d("Package name of app is : "+targetPackage + "foreground is :"+packageName);
                if (packageName.equals(targetPackage)){
                    Timber.d("APp is in use");
                    appInUse = true;
                }
                dialogIntent.putExtra(APP_IN_USE_KEY,appInUse);
                context.startActivity(dialogIntent);
            }
        };
        cdt.start();

    }
}
