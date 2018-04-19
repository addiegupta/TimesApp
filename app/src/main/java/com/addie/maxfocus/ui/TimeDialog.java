package com.addie.maxfocus.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.addie.maxfocus.R;
import com.addie.maxfocus.receiver.AppDialogBroadcastReceiver;
import com.triggertrap.seekarc.SeekArc;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * Displayed to decide time for app launch
 */
public class TimeDialog extends Dialog implements
        android.view.View.OnClickListener {

    private final String mTargetPackage;
    private final boolean mIsWidgetLaunch;
    public Context mContext;
    public Dialog dialog;
    private AppDialogBroadcastReceiver mAppDialogBroadcastReceiver;

    private int minutes;

    //TODO Rename in all classes from service to receiver
    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";

    @BindView(R.id.btn_dialog_cancel)
    Button mCancelButton;
    @BindView(R.id.btn_dialog_start)
    Button mStartButton;
    @BindView(R.id.seekArc_dialog)
    SeekArc mSeekArc;
    @BindView(R.id.tv_seekarc_progress)
    TextView mSeekArcProgressTextView;

    //TODO Change isWidgetLaunch to callingClass to be able to use with preference
    public TimeDialog(Context context, String targetPackage, boolean isWidgetLaunch) {
        super(context);
        this.mContext = context;
        this.mTargetPackage = targetPackage;
        this.mIsWidgetLaunch = isWidgetLaunch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Timber.d("Created dialog");

        setContentView(R.layout.layout_time_dialog);

        if (mIsWidgetLaunch) {
            mAppDialogBroadcastReceiver = new AppDialogBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_APP_DIALOG);

            mContext.registerReceiver(mAppDialogBroadcastReceiver, filter);

        }
        ButterKnife.bind(this);
        mStartButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        String progressText = " " + String.valueOf(mSeekArc.getProgress());
        mSeekArcProgressTextView.setText(progressText);

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
                launchAppOrForegroundApp();
                break;
            case R.id.btn_dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    /**
     * Called when time is selected and "start" is pressed on the dialog
     */
    private void launchAppOrForegroundApp() {
        // Launches the selected app

        // Broadcast intent with selected time for app to be stopped
        int time = minutes * 60000;
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(TIME_KEY, time);
        broadcastIntent.putExtra(TARGET_PACKAGE_KEY, mTargetPackage);
        broadcastIntent.setAction(ACTION_APP_DIALOG);

        if (!mIsWidgetLaunch) {
            PackageManager packageManager = mContext.getPackageManager();
            Intent launchIntent = packageManager.getLaunchIntentForPackage(mTargetPackage);
            mContext.startActivity(launchIntent);
        }
        mContext.sendBroadcast(broadcastIntent);
        ((Activity) mContext).finish();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsWidgetLaunch) {

            mContext.unregisterReceiver(mAppDialogBroadcastReceiver);
        }
    }
}