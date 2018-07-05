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

package com.addie.timesapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.RemoteViews;

import com.addie.timesapp.R;
import com.addie.timesapp.service.LaunchAppFromWidgetService;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AppIconWidgetConfigureActivity AppIconWidgetConfigureActivity}
 */
public class AppIconWidget extends AppWidgetProvider {

    private static final String TARGET_PACKAGE_KEY = "target_package";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String packageName = AppIconWidgetConfigureActivity.loadPackageNameForWidget(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_icon);
        try {
            views.setImageViewBitmap(R.id.app_widget_icon, ((BitmapDrawable) context.getPackageManager().getApplicationIcon(packageName)).getBitmap());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        TODO: FIX!! Causing crash. Fixed, still might cause crash
        if (!packageName.equals(context.getString(R.string.appwidget_text))) {
            // Launches the selected app
            PackageManager packageManager = context.getPackageManager();

//            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            Intent launchIntent = new Intent(context, LaunchAppFromWidgetService.class);
            launchIntent.putExtra(TARGET_PACKAGE_KEY,packageName);
            launchIntent.setAction(Long.toString(System.currentTimeMillis()));

//                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d("WIDGET", "Package being applied in intent is :" + packageName);
            PendingIntent appPendingIntent = PendingIntent.getService(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.app_widget_icon, appPendingIntent);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AppIconWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

