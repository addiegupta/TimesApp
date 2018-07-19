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
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.graphics.Palette;

import com.addie.timesapp.R;
import com.addie.timesapp.data.AppColumns;
import com.addie.timesapp.model.App;
import com.addie.timesapp.utils.Utils;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.addie.timesapp.data.AppProvider.Apps.URI_APPS;

/**
 * Fetches installed apps using PackageManager and saves them in the database for faster loading
 * Launched on first app launch from splash screen
 */
public class SaveAppsInDbService extends IntentService {

    public SaveAppsInDbService() {
        super("SaveAppsInDbService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
           saveAppsInDb();
        }
    }

    /**
     * Loads a list of installed apps on the device using PackageManager
     * Saves the apps in a database
     */
    private void saveAppsInDb() {

        PackageManager mPackageManager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = mPackageManager.queryIntentActivities(intent, 0);
        Collections.sort(activities, new ResolveInfo.DisplayNameComparator(mPackageManager));
        for (ResolveInfo ri : activities) {
            ContentValues values = new ContentValues();
            App app = new App();
            app.setmPackage(ri.activityInfo.packageName);
            app.setmTitle((String) ri.loadLabel(mPackageManager));
            app.setmIcon(ri.activityInfo.loadIcon(mPackageManager));

            //Fetches vibrant colour of app icon using Palette library for better design
            Palette p = Palette.from(Utils.getBitmapFromDrawable(app.getmIcon())).generate();
            app.setmAppColor(p.getVibrantColor(getResources().getColor(R.color.black)));
            app.setmTextColor(Utils.getTextColor(app.getmAppColor()));

            values.put(AppColumns.APP_TITLE, app.getmTitle());
            values.put(AppColumns.PACKAGE_NAME, app.getmPackage());
            values.put(AppColumns.PALETTE_COLOR, app.getmAppColor());
            values.put(AppColumns.TEXT_COLOR, app.getmTextColor());
            try{

            getContentResolver().insert(URI_APPS, values);
            }catch (Exception e){
                Timber.d("COULD NOT INSERT FROM SERVICE");
            }
        }


    }

}
