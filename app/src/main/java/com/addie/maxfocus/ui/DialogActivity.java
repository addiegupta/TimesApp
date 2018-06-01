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
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.addie.maxfocus.R;

import timber.log.Timber;

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

    private static final String APP_COLOR_KEY = "app_color";

    private SharedPreferences preferences;
    private boolean hasUsageAccess;
    private String mPackageName;
    private int mVibrantColor,mMutedColor;
    private String mAppName;
    private Bitmap mAppIcon;
    private boolean mIsWidgetLaunch;

    private TimeDialog mTimeDialog;
    private AlertDialog mStopAppDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);
        mPackageName = getIntent().getStringExtra(TARGET_PACKAGE_KEY);
        mVibrantColor = getIntent().getIntExtra(APP_COLOR_KEY,getResources().getColor(R.color.black));


//        TODO Set to a default color in case palette doesnt work
//        mVibrantColor =
//        mMutedColor =

        fetchAppData();
//        createPaletteAsync(mAppIcon);


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

        mTimeDialog = new TimeDialog(this, mPackageName, true);
        mTimeDialog.show();

        mTimeDialog.getWindow().getDecorView().setBackgroundColor(mVibrantColor);
        mTimeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window window = ((AlertDialog)dialog).getWindow();
                window.getDecorView().setBackgroundColor(mVibrantColor);
            }
        });

    }

    /**
     * Displays the alertDialog for user notifying that time has passed
     */
    private void displayStopAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop using " + mAppName).setCancelable(false)
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
                .setIcon(new BitmapDrawable(getResources(),mAppIcon))
        ;

        if (hasUsageAccess) {
            builder.setMessage("Time's up!");
        } else {
            builder.setMessage("Time's up! No permission");
        }
        mStopAppDialog = builder.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        mStopAppDialog.getWindow().setLayout((6 * width)/7, WindowManager.LayoutParams.WRAP_CONTENT);

        mStopAppDialog.getWindow().getDecorView().setBackgroundColor(mVibrantColor);
        mStopAppDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window window = ((AlertDialog)dialog).getWindow();
                window.getDecorView().setBackgroundColor(mVibrantColor);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.e("onStop called");
    }

    @Override
    protected void onDestroy() {
Timber.e("onDestroy called");
        super.onDestroy();
    }

    // Generate palette asynchronously and use it on a different
// thread using onGenerated()
    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
                ///TODO Change default color to new value
//                mVibrantColor = p.getDarkVibrantColor(getResources().getColor(R.color.white));
                mVibrantColor = p.getVibrantColor(getResources().getColor(R.color.white));
                mMutedColor = p.getDarkMutedColor(getResources().getColor(R.color.colorAccent));


                if (!mIsWidgetLaunch && mStopAppDialog.isShowing()){
                    mStopAppDialog.getWindow().getDecorView().setBackgroundColor(mVibrantColor);
                    mStopAppDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Window window = ((AlertDialog)dialog).getWindow();
                            window.getDecorView().setBackgroundColor(mVibrantColor);
                        }
                    });
                }
                else if(mIsWidgetLaunch && mTimeDialog.isShowing()){
                    mTimeDialog.getWindow().getDecorView().setBackgroundColor(mVibrantColor);
                    mTimeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Window window = ((AlertDialog)dialog).getWindow();
                            window.getDecorView().setBackgroundColor(mVibrantColor);
                        }
                    });
                }


            }
        });
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
