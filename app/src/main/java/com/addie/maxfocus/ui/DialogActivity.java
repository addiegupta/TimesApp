package com.addie.maxfocus.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.addie.maxfocus.R;

/**
 * Displays dialog on top of the foreground running activity
 * Transparent activity so only dialog is visible
 */
public class DialogActivity extends Activity {


    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";

    private static final String APP_IN_USE_KEY = "app_in_use";
    private static final String IS_WIDGET_LAUNCH = "is_widget_launch";
    private static final String TARGET_PACKAGE_KEY = "target_package";

    private SharedPreferences preferences;
    private boolean hasUsageAccess;
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);

        mPackageName = getIntent().getStringExtra(TARGET_PACKAGE_KEY);

        if (getIntent().getBooleanExtra(IS_WIDGET_LAUNCH, false)) {
            displayTimeDialog();
        } else {
            displayStopAppDialog();
        }
    }

    /**
     * Displays a TimeDialog to select time to be set for app usage
     */
    private void displayTimeDialog() {

        TimeDialog dialog = new TimeDialog(this, mPackageName, true);
        dialog.show();

    }

    /**
     * Displays the alertDialog for user notifying that time has passed
     */
    private void displayStopAppDialog() {

        //Fetches data of target app
        ApplicationInfo ai;
        PackageManager pm = getPackageManager();
        Bitmap icon;
        try {
            icon = ((BitmapDrawable) pm.getApplicationIcon(mPackageName)).getBitmap();
            ai = pm.getApplicationInfo(mPackageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
            icon = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop using " + applicationName).setCancelable(false)
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
                .setIcon(new BitmapDrawable(getResources(),icon))
        ;

        if (hasUsageAccess) {
//            boolean appInUse = getIntent().getBooleanExtra(APP_IN_USE_KEY, false);

            // Change in dialog depending on whether app is still in use or not
//            if (appInUse) {
            builder.setMessage("Time's up!");
//            } else {
//                builder.setMessage("Good job!App is no longer being used");
//            }
        } else {
            builder.setMessage("Time's up! No permission");
        }
        builder.show();
    }

}
