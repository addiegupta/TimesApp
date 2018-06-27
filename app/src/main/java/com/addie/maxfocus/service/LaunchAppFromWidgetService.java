package com.addie.maxfocus.service;

import android.app.IntentService;
import android.content.Intent;

import com.addie.maxfocus.ui.DialogActivity;

import timber.log.Timber;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class LaunchAppFromWidgetService extends IntentService {

    private static final String TARGET_PACKAGE_KEY = "target_package";
    private static final String CALLING_CLASS_KEY = "calling_class";



    public LaunchAppFromWidgetService() {
        super("LaunchAppFromWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Timber.d("Service launched");
            final String packageName = intent.getStringExtra(TARGET_PACKAGE_KEY);

            final Intent dialogIntent = new Intent(LaunchAppFromWidgetService.this, DialogActivity.class);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dialogIntent.putExtra(TARGET_PACKAGE_KEY, packageName);
            dialogIntent.putExtra(CALLING_CLASS_KEY,getClass().getSimpleName());
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dialogIntent);

        }

    }
}
