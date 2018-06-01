package com.addie.maxfocus.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class to store data of apps i.e. title,package name and icon drawable
 */

public class App implements Parcelable {
    private String mTitle, mPackage;
    private Drawable mIcon;

    public App(String mTitle, String mPackage, Drawable mIcon, int mVibrantColor) {
        this.mTitle = mTitle;
        this.mPackage = mPackage;
        this.mIcon = mIcon;
        this.mVibrantColor = mVibrantColor;
    }

    public int getmVibrantColor() {
        return mVibrantColor;
    }

    public void setmVibrantColor(int mVibrantColor) {
        this.mVibrantColor = mVibrantColor;
    }

    private int mVibrantColor;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mPackage);
        dest.writeParcelable((Parcelable) this.mIcon, flags);
        dest.writeInt(this.mVibrantColor);
    }

    protected App(Parcel in) {
        this.mTitle = in.readString();
        this.mPackage = in.readString();
        this.mIcon = in.readParcelable(Drawable.class.getClassLoader());
        this.mVibrantColor = in.readInt();
    }

    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        @Override
        public App createFromParcel(Parcel source) {
            return new App(source);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };
}
