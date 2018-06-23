package com.addie.maxfocus.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.addie.maxfocus.R;

public class SplashActivity extends Activity {

    private static final int SPLASH_TIMEOUT = 2500;
    private boolean mTutorialSeen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }



        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mTutorialSeen = prefs.getBoolean(getString(R.string.tutorial_seen_key),false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTutorialSeen){
                    //Show apps
                    // if apps get loaded before,display apps
                    startActivity(new Intent(SplashActivity.this,AppsActivity.class));
                }
                else{
                    //show tutorial
                    startActivity(new Intent(SplashActivity.this,IntroActivity3.class));
                }


            }
        },SPLASH_TIMEOUT);



    }
}
