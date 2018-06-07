package com.addie.maxfocus.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.addie.maxfocus.R;
import com.addie.maxfocus.service.AppTimeDialogService;
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
    private SharedPreferences preferences;

    private int minutes;
    private int mAppColor;
    private int mTextColor;

    //TODO Rename in all classes from service to receiver
    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
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

    //TODO Change isWidgetLaunch to callingClass to be able to use with preference
    public TimeDialog(Context context, String targetPackage, int appColor, boolean isWidgetLaunch,int textColor) {
        super(context);
        this.mContext = context;
        this.mTargetPackage = targetPackage;
        this.mAppColor = appColor;
        this.mIsWidgetLaunch = isWidgetLaunch;
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

        String titleText = mContext.getString(R.string.set_duration_for) + " " + applicationName;
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
                if (mIsWidgetLaunch) {
                    startAppActivity();
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    private void setViewColors(){

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
        ((Activity) mContext).finish();

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