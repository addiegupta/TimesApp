package com.addie.maxfocus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.addie.maxfocus.ui.DialogActivity;

import timber.log.Timber;

/**
 * Receives intent to display dialog after selected time has passed.
 * Launches DialogActivity to display an alert dialog
 */
public class AppDialogBroadcastReceiver extends BroadcastReceiver {


    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";

    CountDownTimer cdt = null;


    @Override
    public void onReceive(final Context context, Intent intent) {

        Timber.d("Broadcast received");
        final int time = intent.getIntExtra(TIME_KEY, 0);

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

                context.startActivity(new Intent(context, DialogActivity.class));
            }
        };
        cdt.start();

    }
}
