package com.addie.maxfocus.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import timber.log.Timber;

/**
 * Displays dialog on top of the foreground running activity
 * Transparent activity so only dialog is visible
 */
public class DialogActivity extends Activity {


    private static final String APP_IN_USE_KEY = "app_in_use";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayStopAppDialog();
    }

    /**
     * Displays the alertDialog for user notifying that time has passed
     */
    private void displayStopAppDialog() {

        boolean appInUse = getIntent().getBooleanExtra(APP_IN_USE_KEY, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop using this app bro").setCancelable(false)
                .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
        ;
        Timber.d("Value of bool appInUse is :" + appInUse);
        if (appInUse) {
            builder.setMessage("Same app is still in use");
        } else {
            builder.setMessage("Good job!App is no longer being used");
        }
        builder.show();
    }

}
