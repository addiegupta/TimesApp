package com.addie.maxfocus.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.addie.maxfocus.R;
import com.addie.maxfocus.service.AppTimeDialogService;

/**
 * Displays dialog on top of the foreground running activity
 * Transparent activity so only dialog is visible
 */

//FIXME URGENT Palette mess needs to be sorted. Shortcut requiring double tap
public class DialogActivity extends Activity {


    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";

    private static final String APP_IN_USE_KEY = "app_in_use";
    private static final String IS_WIDGET_LAUNCH = "is_widget_launch";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String TIME_KEY = "time";
    private static final String DISPLAY_1_MIN = "display_1_min";

    private static final String APP_COLOR_KEY = "app_color";

    private SharedPreferences preferences;
    private boolean hasUsageAccess;
    private String mPackageName;
    private int mAppColor;
    private String mAppName;
    private Bitmap mAppIcon;
    private boolean mIsWidgetLaunch;
    private boolean mDisplay1Min;

    private TimeDialog mTimeDialog;
    private AlertDialog mStopAppDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);
        mPackageName = getIntent().getStringExtra(TARGET_PACKAGE_KEY);
        mAppColor = getIntent().getIntExtra(APP_COLOR_KEY,getResources().getColor(R.color.black));
        mDisplay1Min = getIntent().getBooleanExtra(DISPLAY_1_MIN,true);

        fetchAppData();


        mIsWidgetLaunch = getIntent().getBooleanExtra(IS_WIDGET_LAUNCH, false);

        if (mIsWidgetLaunch) {
            displayTimeDialog();
        } else {
            displayStopAppDialog();
        }
    }

    /**
     * Displays a TimeDialog to select time to be set for app usage
     */
    private void displayTimeDialog() {

        mTimeDialog = new TimeDialog(this, mPackageName, mAppColor, true);
        mTimeDialog.show();

        mTimeDialog.getWindow().getDecorView().setBackgroundColor(mAppColor);
        mTimeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window window = ((AlertDialog)dialog).getWindow();
                window.getDecorView().setBackgroundColor(mAppColor);
            }
        });

    }

    /**
     * Displays the alertDialog for user notifying that time has passed
     */
    private void displayStopAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = "Stop using " + mAppName;
        builder.setTitle(Html.fromHtml("<font color='#FFFFFF'>"+title+"</font>"))
                .setCancelable(false)
                .setPositiveButton("Stop"
                        , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Goes to home screen
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setIcon(new BitmapDrawable(getResources(),mAppIcon))
        ;

        if(mDisplay1Min){

            builder.setNegativeButton("+1 Min"
                    , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        grantOneMinuteExtra();
                    }
                });

        }

        if (hasUsageAccess) {
            builder.setMessage(Html.fromHtml("<font color='#FFFFFF'>Time's up!</font>"));
        } else {
            builder.setMessage(Html.fromHtml("<font color='#FFFFFF'>Time's up! \n (Foreground app check permission not granted)</font>"));
        }
        mStopAppDialog = builder.show();


        Button nbutton = mStopAppDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (nbutton!=null){

            nbutton.setTextColor(Color.WHITE);
        }
        Button pbutton = mStopAppDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.WHITE);


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        mStopAppDialog.getWindow().setLayout((6 * width)/7, WindowManager.LayoutParams.WRAP_CONTENT);

        mStopAppDialog.getWindow().getDecorView().setBackgroundColor(mAppColor);
        mStopAppDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window window = ((AlertDialog)dialog).getWindow();
                window.getDecorView().setBackgroundColor(mAppColor);
            }
        });

    }


    /**
     * Called when +1 Minute is selected on the dialog
     */
    private void grantOneMinuteExtra() {

        // Start service selected time for app to be stopped
        int time = 60000;
        Intent timeServiceIntent = new Intent(this, AppTimeDialogService.class);
        timeServiceIntent.putExtra(TIME_KEY, time);
        timeServiceIntent.putExtra(TARGET_PACKAGE_KEY, mPackageName);
        timeServiceIntent.putExtra(APP_COLOR_KEY, mAppColor);
        this.startService(timeServiceIntent);

        finish();

    }

    private void fetchAppData(){
        ApplicationInfo appInfo;
        PackageManager pm = getPackageManager();

        try {
            mAppIcon = ((BitmapDrawable) pm.getApplicationIcon(mPackageName)).getBitmap();
            appInfo = pm.getApplicationInfo(mPackageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            appInfo = null;
            mAppIcon = null;
        }
        mAppName = (String) (appInfo != null ? pm.getApplicationLabel(appInfo) : "(unknown)");

    }

}
