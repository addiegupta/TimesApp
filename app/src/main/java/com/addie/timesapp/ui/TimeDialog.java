/*
 * MIT License
 *
 * Copyright (c) 2018 aSoft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.addie.timesapp.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.addie.timesapp.R;
import com.addie.timesapp.service.AppTimeDialogService;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.triggertrap.seekarc.SeekArc;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * Displayed to decide time for app launch
 */
public class TimeDialog extends Dialog implements
        android.view.View.OnClickListener {

    private final String mTargetPackage;
    public Context mContext;
    private SharedPreferences preferences;

    private int minutes;
    private int mAppColor;
    private int mTextColor;

    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_COLOR_KEY = "app_color";
    private static final String TEXT_COLOR_KEY = "text_color";


    @BindView(R.id.btn_dialog_cancel)
    Button mCancelButton;
    @BindView(R.id.btn_dialog_start)
    Button mStartButton;
    @BindView(R.id.seekArc_dialog)
    SeekArc mSeekArc;
    @BindView(R.id.tv_seekarc_progress)
    TextView mSeekArcProgressTextView;
    @BindView(R.id.tv_seekarc_progress_m_label)
    TextView mSeekArcProgressTextViewmLabel;
    @BindView(R.id.tv_dialog_title)
    TextView mDialogTitleTextView;
    @BindView(R.id.iv_time_dialog_icon)
    ImageView mAppIconImageView;

    public TimeDialog(Context context, String targetPackage, int appColor,int textColor) {
        super(context);
        this.mContext = context;
        this.mTargetPackage = targetPackage;
        this.mAppColor = appColor;
        this.mTextColor = textColor;
        Timber.d(String.valueOf(mAppColor));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Timber.d("Created dialog");

        setContentView(R.layout.layout_time_dialog);

        ButterKnife.bind(this);

        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!preferences.contains(mContext.getString(R.string.pref_display_tap_target_time_dialog))|| preferences.getBoolean(mContext.getString(R.string.pref_display_tap_target_time_dialog),true)) {
            displayTapTargetView();
        }


        mStartButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setViewColors();

        ApplicationInfo ai;
        PackageManager pm = mContext.getPackageManager();
        Bitmap icon;
        try {
            icon = ((BitmapDrawable) pm.getApplicationIcon(mTargetPackage)).getBitmap();
            ai = pm.getApplicationInfo(mTargetPackage, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
            icon = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        String titleText = applicationName;
        mDialogTitleTextView.setText(titleText);
        mAppIconImageView.setImageBitmap(icon);

        String progressText = " " + String.valueOf(mSeekArc.getProgress());
        mSeekArcProgressTextView.setText(progressText);

        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int progress = Integer.parseInt(preferences.getString(mContext.getString(R.string.pref_app_time_key), "10"));

        mSeekArc.setProgress(progress);
        String progressTxt = " " + String.valueOf(progress);
        mSeekArcProgressTextView.setText(progressTxt);
        minutes = progress;


        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                if (i == 0)
                    i = 1;
                String progressText = " " + String.valueOf(i);
                mSeekArcProgressTextView.setText(progressText);
                minutes = i;
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_start:
                dismiss();
                launchTargetApp();
                break;
            case R.id.btn_dialog_cancel:
                dismiss();
                startAppActivity();
                break;
            default:
                break;
        }
        dismiss();
    }


    /**
     * Displays the tapTargetView tutorial on using the app icons
     */
    private void displayTapTargetView() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                Rect lowerScreen = new Rect(width/8, height-height/15, width-(width/8), height+height/8);

                new TapTargetSequence(TimeDialog.this)
                        .targets(
                                TapTarget.forView(mSeekArc, "Set a timer by adjusting the slider")
                                        .cancelable(false).transparentTarget(true).targetRadius(width/10).outerCircleColor(R.color.colorPrimary),

                                TapTarget.forView(mStartButton, "Launch the app with the timer set" ).cancelable(false).outerCircleColor(R.color.colorPrimary),
                                TapTarget.forView(mCancelButton, "Launch without a timer")
                                        .cancelable(false).outerCircleColor(R.color.colorPrimary),
                                TapTarget.forBounds(lowerScreen,"Press back to close dialog").targetRadius(width/12).transparentTarget(true).outerCircleColor(R.color.colorPrimary)
                        ).listener(new TapTargetSequence.Listener() {
                     @Override
                    public void onSequenceFinish() {
                        }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                       }
                }).start();


                SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
                preferences.edit().putBoolean(mContext.getString(R.string.pref_display_tap_target_time_dialog), false).apply();


            }
        }, 200);


    }


    private void setViewColors() {

        // Only this seems to work, Passing the int directly to setTextColor seems to be missing some properties
        int parsedTextColor = Color.parseColor(String.format("#%06X", (0xFFFFFF & mTextColor)));

        mStartButton.setTextColor(parsedTextColor);
        mCancelButton.setTextColor(parsedTextColor);
        mDialogTitleTextView.setTextColor(parsedTextColor);
        mSeekArcProgressTextView.setTextColor(parsedTextColor);
        mSeekArcProgressTextViewmLabel.setTextColor(parsedTextColor);
        mSeekArc.setArcColor(parsedTextColor);
        mSeekArc.setProgressColor(parsedTextColor);

    }

    /**
     * Called when time is selected and "start" is pressed on the dialog
     */
    private void launchTargetApp() {

        // Start service selected time for app to be stopped
        int time = minutes * 60000;
        Intent timeServiceIntent = new Intent(mContext, AppTimeDialogService.class);
        timeServiceIntent.putExtra(TIME_KEY, time);
        timeServiceIntent.putExtra(TARGET_PACKAGE_KEY, mTargetPackage);
        timeServiceIntent.putExtra(APP_COLOR_KEY, mAppColor);
        timeServiceIntent.putExtra(TEXT_COLOR_KEY, mTextColor);
        mContext.startService(timeServiceIntent);

        // Launches the selected app
        startAppActivity();
        ((Activity) mContext).finish();

    }

    //Finish dialog and activity on pressing back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.dismiss();

        ActivityManager am = (ActivityManager)mContext.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
        String currentActivity = taskInfo.get(0).topActivity.getClassName();
        if(currentActivity.equals(DialogActivity.class.getName())){
            ((Activity) mContext).finish();
        }
    }


    /**
     * Launches the target app using PackageManager
     */
    private void startAppActivity() {
        PackageManager packageManager = mContext.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(mTargetPackage);
        mContext.startActivity(launchIntent);
    }
}