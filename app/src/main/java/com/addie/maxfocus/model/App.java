package com.addie.maxfocus.model;

import android.graphics.drawable.Drawable;

/**
 * Model class to store data of apps i.e. title,package name and icon drawable
 */

public class App {
    private String mTitle, mPackage;
    private Drawable mIcon;

public App(){}
    public App(String mTitle, String mPackage, Drawable mIcon) {
        this.mTitle = mTitle;
        this.mPackage = mPackage;
        this.mIcon = mIcon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPackage() {
        return mPackage;
    }

    public void setmPackage(String mPackage) {
        this.mPackage = mPackage;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }
}
