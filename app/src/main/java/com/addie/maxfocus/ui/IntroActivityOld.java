package com.addie.maxfocus.ui;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.FloatRange;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.addie.maxfocus.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
//TODO Update app name wherever applicable
//TODO Replace all strings with R.strings
public class IntroActivityOld extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableLastSlideAlphaExitTransition(true);
        setSkipButtonVisible();

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });


        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorAccent)
                        .buttonsColor(R.color.colorPrimary)
                        .image(R.drawable.example_appwidget_preview)
                        .title("Control your app usage")
                        .description("Limit the time you spend on your phone")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .buttonsColor(R.color.colorPrimaryDark)
                .title("Set a timer before starting an app")
                .description("Just add a shortcut to your home screen for the apps you love.")
                .build());


        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.default_blue_light)
                        .buttonsColor(R.color.progress_gray)
                        .image(R.drawable.hand)
                        .title(getString(R.string.usage_permission_title))
                        .description(getString(R.string.usage_permission_message))
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("Opening settings");
                        requestUsageStatsPermission();

                    }
                }, "Grant Usage Permission"));

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.colorAccent)
                .title("Control wasting time on endless social feeds")
                .description("Get Started")
                .build());
    }


    void requestUsageStatsPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
//TODO: Comment out. Done for debugging
//                && !hasUsageStatsPermission(this)
                ) {
            //TODO Change to new app name
            Toast.makeText(this, R.string.usage_permission_instruction, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(getString(R.string.usage_permission_pref), granted).apply();

        return granted;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        startActivity(new Intent(this,MainActivity.class));
    }
}
