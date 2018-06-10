package com.addie.maxfocus.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.addie.maxfocus.R;
import com.triggertrap.seekarc.SeekArc;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class PrefTimeDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Context mContext;
    private SharedPreferences preferences;

    private int minutes;

    //TODO Rename in all classes from service to receiver
    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String APP_COLOR_KEY = "app_color";
    private static final String TEXT_COLOR_KEY = "text_color";


    @BindView(R.id.btn_pref_dialog_cancel)
    Button mCancelButton;
    @BindView(R.id.btn_pref_dialog_start)
    Button mStartButton;
    @BindView(R.id.seekArc_dialog_pref)
    SeekArc mSeekArc;
    @BindView(R.id.tv_seekarc_progress_pref)
    TextView mSeekArcProgressTextView;
    @BindView(R.id.tv_pref_dialog_title)
    TextView mDialogTitleTextView;

    //TODO Change isWidgetLaunch to callingClass to be able to use with preference
    public PrefTimeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Timber.d("Created pref dialog");

        setContentView(R.layout.layout_pref_time_dialog);

        ButterKnife.bind(this);

        mStartButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

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
            case R.id.btn_pref_dialog_start:
                dismiss();
                saveDefaultAppDuration();
                break;
            case R.id.btn_pref_dialog_cancel:
                dismiss();
                ((Activity)mContext).finish();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void saveDefaultAppDuration() {
        String minutesString = String.valueOf(minutes);
        preferences.edit().putString(mContext.getString(R.string.pref_app_time_key),minutesString).apply();
        ((Activity)mContext).finish();
    }


    //Finish dialog and activity on pressing back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.dismiss();
        ((Activity) mContext).finish();

    }

}
