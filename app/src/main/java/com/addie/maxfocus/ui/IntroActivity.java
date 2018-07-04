package com.addie.maxfocus.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.addie.maxfocus.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage page1 = new SliderPage();
        page1.setTitle(getString(R.string.app_name));
        page1.setDescription("Control your phone usage");
        page1.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page1.setImageDrawable(R.drawable.app_icon_large);
        addSlide(AppIntroFragment.newInstance(page1));

        SliderPage pagei = new SliderPage();
        pagei.setTitle("Add app shortcut");
        pagei.setDescription("Add new app shorcuts to the home screen using " + getString(R.string.app_name));
        pagei.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        pagei.setImageDrawable(R.drawable.replace_icon);

        SliderPage page2 = new SliderPage();
        page2.setTitle("Set a timer");
        page2.setDescription("Before starting an app,specify the time you want to spend on it");
        page2.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page2.setImageDrawable(R.drawable.time_dialog_phone);

        SliderPage page3 = new SliderPage();
        page3.setTitle("Get notified");
        page3.setDescription("A dialog will pop up when the specified time has passed");
        page3.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page3.setImageDrawable(R.drawable.stop_dialog_new);

        SliderPage page4 = new SliderPage();
        page4.setTitle("All Set!");
        page4.setDescription("Press Done to begin");
        page4.setImageDrawable(R.drawable.ic_check_circle_big_green_128dp);
        page4.setBgColor(getResources().getColor(R.color.colorPrimaryDark));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(pagei));
        addSlide(AppIntroFragment.newInstance(page2));
        addSlide(AppIntroFragment.newInstance(page3));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addSlide(PermissionSlide.newInstance(R.layout.fragment_permission_slide));
        }
        addSlide(AppIntroFragment.newInstance(page4));

        // OPTIONAL METHODS
        // Override bar/separator color
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));
        // Hide Skip/Done button
        showSkipButton(true);
        setProgressButtonEnabled(true);

        showSeparator(false);

        // SHOW or HIDE the statusbar

        showStatusBar(false);
        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation(); // OR
//        setZoomAnimation(); // OR
//        setFlowAnimation(); // OR
//        setSlideOverAnimation(); // OR
//        setDepthAnimation(); // OR
//        setCustomTransformer(yourCustomTransformer);

        // Permissions -- takes a permission and slide number
//        askForPermissions(new String[]{
//                Manifest.permission.CAMERA}, 1);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        //TODO : Change to last slide
        getPager().setCurrentItem(5);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this, MainActivity.class));
        finish();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(getString(R.string.tutorial_seen_key),true).apply();

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    public void onNextPressed() {
        super.onNextPressed();
    }

}

