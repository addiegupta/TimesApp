package com.addie.maxfocus.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.addie.maxfocus.receiver.AppDialogBroadcastReceiver;

/**
 * Displays dialog on top of the foreground running activity
 * Transparent activity so only dialog is visible
 */
//TODO Change behavior when app is not in use/permission not granted
public class DialogActivity extends Activity {


    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";

    private static final String APP_IN_USE_KEY = "app_in_use";
    private static final String IS_WIDGET_LAUNCH = "is_widget_launch";
    private static final String TARGET_PACKAGE_KEY = "target_package";

    private AppDialogBroadcastReceiver mAppDialogBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getIntent().getBooleanExtra(IS_WIDGET_LAUNCH,false)){
            displayTimeDialog();

            //Register broadcast receiver to receive "stop app" dialogs
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_APP_DIALOG);
            mAppDialogBroadcastReceiver = new AppDialogBroadcastReceiver();
            registerReceiver(mAppDialogBroadcastReceiver, filter);

        }
        else {

        displayStopAppDialog();
        }
    }


    //TODO: Check why am I unregistering
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getIntent().getBooleanExtra(IS_WIDGET_LAUNCH,false)) {

            unregisterReceiver(mAppDialogBroadcastReceiver);
        }
    }
    /**
     * Displays a TimeDialog to select time to be set for app usage
     */
    private void displayTimeDialog(){

        String packageName = getIntent().getStringExtra(TARGET_PACKAGE_KEY);
        TimeDialog dialog = new TimeDialog(this,packageName,true);
        dialog.show();

    }
    /**
     * Displays the alertDialog for user notifying that time has passed
     */
    private void displayStopAppDialog() {

        boolean appInUse = getIntent().getBooleanExtra(APP_IN_USE_KEY, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop using this app").setCancelable(false)
                .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Goes to home screen
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
        ;

        // Change in dialog depending on whether app is still in use or not
        if (appInUse) {
            builder.setMessage("Same app is still in use");
        } else {
            builder.setMessage("Good job!App is no longer being used");
        }
        builder.show();
    }

}
