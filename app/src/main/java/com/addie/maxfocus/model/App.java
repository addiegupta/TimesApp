package com.addie.maxfocus.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class to store data of apps i.e. title,package name and icon drawable
 */

public class App implements Parcelable {
    private String mTitle, mPackage;
    private Drawable mIcon;
    private int mTextColor;

    public int getmTextColor() {
        return mTextColor;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public static Creator<App> getCREATOR() {
        return CREATOR;
    }

    public App(String mTitle, String mPackage, Drawable mIcon, int mTextColor, int mAppColor) {
        this.mTitle = mTitle;
        this.mPackage = mPackage;
        this.mIcon = mIcon;
        this.mTextColor = mTextColor;
        this.mAppColor = mAppColor;
    }

    public App(String mTitle, String mPackage, Drawable mIcon, int mAppColor) {
        this.mTitle = mTitle;
        this.mPackage = mPackage;
        this.mIcon = mIcon;
        this.mAppColor = mAppColor;
    }

    public int getmAppColor() {
        return mAppColor;
    }

    public void setmAppColor(int mAppColor) {
        this.mAppColor = mAppColor;
    }

    private int mAppColor;

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

        Bitmap bitmap = (Bitmap)((BitmapDrawable)this.mIcon).getBitmap();
        dest.writeParcelable(bitmap,flags);
        dest.writeInt(this.mTextColor);
        dest.writeInt(this.mAppColor);
    }

    protected App(Parcel in) {
        this.mTitle = in.readString();
        this.mPackage = in.readString();
        this.mIcon = in.readParcelable(Drawable.class.getClassLoader());
        this.mTextColor = in.readInt();
        this.mAppColor = in.readInt();
    }

    public static final Creator<App> CREATOR = new Creator<App>() {
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
