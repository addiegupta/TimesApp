package com.addie.timesapp.extra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by addie on 25-06-2018.
 */

public final class Utils {

    public static int getTextColor(int color) {

        int redColorValue = (color >> 16) & 0xFF;
        int greenColorValue = (color >> 8) & 0xFF;
        int blueColorValue = (color) & 0xFF;

        if ((redColorValue * 0.299
                + greenColorValue * 0.587
                + blueColorValue * 0.114) > 186)
            //black
            return 0;
        else
            //white
            return 16777215;

    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
