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

//TODO Clean up all the broadcast receiver mess ( maybe by starting a service in activity that controls
    // this receiver
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

        // Counts till the specified time before launching dialog activity
        cdt = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                Timber.d("Tick tick %s", l);
            }

            @Override
            public void onFinish() {
                Timber.d("Starting activity");

                // Checks which app is in foreground
                AppChecker appChecker = new AppChecker();
                String packageName = appChecker.getForegroundApp(context);

                // Creates intent to display
                // Adds boolean which contains value if app is still in foreground
                Intent dialogIntent = new Intent(context, DialogActivity.class);
                boolean appInUse = false;
                if (packageName.equals(targetPackage)) {
                    Timber.d("APp is in use");
                    appInUse = true;
                }
                dialogIntent.putExtra(APP_IN_USE_KEY, appInUse);
                context.startActivity(dialogIntent);
            }
        };
        cdt.start();

    }
}
