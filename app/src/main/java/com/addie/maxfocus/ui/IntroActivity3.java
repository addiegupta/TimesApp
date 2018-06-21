package com.addie.maxfocus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.addie.maxfocus.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity3 extends AppIntro {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SliderPage page1= new SliderPage();
        page1.setTitle("Title 1");
        page1.setDescription("Description 1");
        page1.setBgColor(getResources().getColor(R.color.colorAccent));
        page1.setImageDrawable(R.drawable.thumb);

        SliderPage page2= new SliderPage();
        page2.setTitle("Title 2");
        page2.setDescription("Description 2");
        page2.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        page2.setImageDrawable(R.drawable.time_dialog_graphic);

        SliderPage page3= new SliderPage();
        page3.setTitle("Title 3");
        page3.setDescription("Description 3");
        page3.setBgColor(getResources().getColor(R.color.colorAccentLight));
        page3.setImageDrawable(R.drawable.stop_dialog_graphic);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(page1));
        addSlide(SampleSlide.newInstance(R.layout.fragment_sample_slide));
        addSlide(AppIntroFragment.newInstance(page2));
        addSlide(AppIntroFragment.newInstance(page3));

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
        getPager().setCurrentItem(3);

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this,MainActivity.class));
        finish();
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

