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

package com.addie.timesapp2.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.addie.timesapp2.R
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage

/**
 * An introductory activity that explains the method of use of the app
 */
class IntroActivity : AppIntro() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCallingClass = ""
        if (intent.hasExtra(CALLING_CLASS_KEY)) {
            mCallingClass = intent.getStringExtra(CALLING_CLASS_KEY)
            if (mCallingClass == "SettingsFragment") {
                val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this@IntroActivity)
                preferences.edit().putBoolean(getString(R.string.pref_display_tap_target_apps), true).apply()

                preferences.edit().putBoolean(getString(R.string.pref_display_tap_target_time_dialog), true).apply()
            }
        }

        val page1 = SliderPage()
        page1.title = getString(R.string.app_name)
        page1.description = getString(R.string.intro_page1_desc)
        page1.bgColor = resources.getColor(R.color.colorPrimaryDark)
        page1.imageDrawable = R.drawable.app_icon_large

        val page2 = SliderPage()
        page2.title = getString(R.string.intro_page2_title)
        page2.description = getString(R.string.intro_page2_desc)
        page2.bgColor = resources.getColor(R.color.colorPrimaryDark)
        page2.imageDrawable = R.drawable.replace_icon

        val page3 = SliderPage()
        page3.title = getString(R.string.intro_page3_title)
        page3.description = getString(R.string.intro_page3_desc)
        page3.bgColor = resources.getColor(R.color.colorPrimaryDark)
        page3.imageDrawable = R.drawable.time_dialog_phone

        val page4 = SliderPage()
        page4.title = getString(R.string.intro_page4_title)
        page4.description = getString(R.string.intro_page4_desc)
        page4.bgColor = resources.getColor(R.color.colorPrimaryDark)
        page4.imageDrawable = R.drawable.stop_dialog

        val page5 = SliderPage()
        page5.title = getString(R.string.intro_page5_title)
        page5.description = getString(R.string.intro_page5_desc)
        page5.imageDrawable = R.drawable.ic_check_circle_big_green_128dp
        page5.bgColor = resources.getColor(R.color.colorPrimaryDark)

        addSlide(AppIntroFragment.newInstance(page1))
        addSlide(AppIntroFragment.newInstance(page2))
        addSlide(AppIntroFragment.newInstance(page3))
        addSlide(AppIntroFragment.newInstance(page4))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addSlide(UsagePermissionSlide.newInstance(R.layout.fragment_permission_slide))
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.)
        addSlide(AppIntroFragment.newInstance(page5))

        showSkipButton(true)
        isProgressButtonEnabled = true
        showSeparator(false)
        showStatusBar(false)
        setFadeAnimation()

    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        getPager().currentItem = 4

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean(getString(R.string.tutorial_seen_key), true).apply()

        val mainIntent = Intent(this, MainActivity::class.java)

        // Display tutorials preference has been selected from preference screen
        if (mCallingClass == "SettingsFragment") {
            // Finish all activities
            finishAffinity()
            startActivity(mainIntent)
        } else {
            startActivity(mainIntent)
            finish()
        }//Default behavior

    }

    companion object {
        private val CALLING_CLASS_KEY = "calling_class"
        private var mCallingClass: String? = null
    }

}

