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

package com.addie.timesapp.service;

import android.app.IntentService;
import android.content.Intent;

import com.addie.timesapp.ui.DialogActivity;

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
