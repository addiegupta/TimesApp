package com.addie.maxfocus.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.addie.maxfocus.R;
import com.addie.maxfocus.service.SetAlarmService;

/**
 * Implementation of App Widget functionality.
 */
public class AlarmWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.alarm_widget);

        Intent alarmIntent = new Intent(context, SetAlarmService.class);
        alarmIntent.setAction(Long.toString(System.currentTimeMillis()));

        PendingIntent alarmPendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
        views.setOnClickPendingIntent(R.id.alarm_widget_icon, alarmPendingIntent);

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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

