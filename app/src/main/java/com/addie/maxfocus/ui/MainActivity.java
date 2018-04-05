package com.addie.maxfocus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.addie.maxfocus.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_apps)
    Button mAppsButton;
    @BindView(R.id.btn_study_break)
    Button mStudyButton;
    @BindView(R.id.btn_instant_alarm)
    Button mAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        mAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAppsActivity();
            }
        });

        mAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlarmActivity();
            }
        });

        mStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStudyTimerActivity();
            }
        });
    }

    private void startAppsActivity() {
        finish();
        startActivity(new Intent(MainActivity.this, AppsActivity.class));
    }

    //    TODO: Improve
    private void startAlarmActivity() {

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 8); // adds one hour
        Timber.d(cal.getTime().toString()); // returns new date object, one hour in the future
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);


        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        startActivity(intent);

//    TODO:Issue notification that alarm is set and phone should be kept aside

    }

    //    TODO:Implement
    private void startStudyTimerActivity() {

        Intent breakTimerintent = new Intent(AlarmClock.ACTION_SET_TIMER);

        int breakTimerLength = 20;//This is a value in seconds;Change 20 to value decided in sharedpreferences

        breakTimerintent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        breakTimerintent.putExtra(AlarmClock.EXTRA_LENGTH, breakTimerLength);
        //TODO Change message
        breakTimerintent.putExtra(AlarmClock.EXTRA_MESSAGE, "Time for a break!");

        //TODO: Issue a notification as well?
        startActivity(breakTimerintent);
        Toast.makeText(this, "Timer set for " + String.valueOf(breakTimerLength) + " seconds from now", Toast.LENGTH_SHORT).show();

        //TODO: Figure out how to set study timer after decided time

        // This is not working; only 1 timer is supported at least in Samsung
        /*
        Intent studyTimerIntent = new Intent(AlarmClock.ACTION_SET_TIMER);

        int studyTimerLength = breakTimerLength + 20;// CHange 20 to a value chosen from shared preferences

        studyTimerIntent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);
        studyTimerIntent.putExtra(AlarmClock.EXTRA_LENGTH,studyTimerLength);
        //TODO Change message
        studyTimerIntent.putExtra(AlarmClock.EXTRA_MESSAGE,"Break is over, time to study");

        //TODO: Issue a notification as well? Something like" your timer is set
        //TODO: check notificattion for details" which will contain clock intent
        startActivity(studyTimerIntent);

        //TODO: Prompt user to decide whether to continue timer cycle or not
*/
//        startActivity(new Intent(MainActivity.this,StudyTimerActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
