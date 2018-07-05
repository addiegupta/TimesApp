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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.addie.timesapp.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {
    private static final String CALLING_CLASS_KEY = "calling_class";
    private static String mCallingClass;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallingClass = "";
        if (getIntent().hasExtra(CALLING_CLASS_KEY)) {
            mCallingClass = getIntent().getStringExtra(CALLING_CLASS_KEY);
            if (mCallingClass.equals("SettingsFragment")) {
                SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(IntroActivity.this);
                preferences.edit().putBoolean(getString(R.string.pref_display_tap_target_apps), true).apply();

                preferences.edit().putBoolean(getString(R.string.pref_display_tap_target_time_dialog), true).apply();
            }
        }

        SliderPage page1 = new SliderPage();
        page1.setTitle(getString(R.string.app_name));
        page1.setDescription(getString(R.string.intro_page1_desc));
        page1.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page1.setImageDrawable(R.drawable.app_icon_large);

        SliderPage page2 = new SliderPage();
        page2.setTitle(getString(R.string.intro_page2_title));
        page2.setDescription(getString(R.string.intro_page2_desc));
        page2.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page2.setImageDrawable(R.drawable.replace_icon);

        SliderPage page3 = new SliderPage();
        page3.setTitle(getString(R.string.intro_page3_title));
        page3.setDescription(getString(R.string.intro_page3_desc));
        page3.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page3.setImageDrawable(R.drawable.time_dialog_phone);

        SliderPage page4 = new SliderPage();
        page4.setTitle(getString(R.string.intro_page4_title));
        page4.setDescription(getString(R.string.intro_page4_desc));
        page4.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page4.setImageDrawable(R.drawable.stop_dialog_new);

        SliderPage page5 = new SliderPage();
        page5.setTitle(getString(R.string.intro_page5_title));
        page5.setDescription(getString(R.string.intro_page5_desc));
        page5.setImageDrawable(R.drawable.ic_check_circle_big_green_128dp);
        page5.setBgColor(getResources().getColor(R.color.colorPrimaryDark));

        addSlide(AppIntroFragment.newInstance(page1));
        addSlide(AppIntroFragment.newInstance(page2));
        addSlide(AppIntroFragment.newInstance(page3));
        addSlide(AppIntroFragment.newInstance(page4));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addSlide(PermissionSlide.newInstance(R.layout.fragment_permission_slide));
        }
        addSlide(AppIntroFragment.newInstance(page5));

        showSkipButton(true);
        setProgressButtonEnabled(true);
        showSeparator(false);
        showStatusBar(false);
        setFadeAnimation();

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        getPager().setCurrentItem(5);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(getString(R.string.tutorial_seen_key), true).apply();

        Intent mainIntent = new Intent(this, MainActivity.class);
        if (mCallingClass.equals("SettingsFragment")) {
            // Finish all activities
            finishAffinity();
            startActivity(mainIntent);
        } else {
            startActivity(mainIntent);
            finish();
        }

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

