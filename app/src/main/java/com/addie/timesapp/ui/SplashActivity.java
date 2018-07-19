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

package com.addie.timesapp.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.WindowManager;

import com.addie.timesapp.R;
import com.addie.timesapp.data.AppColumns;
import com.addie.timesapp.model.App;
import com.addie.timesapp.service.SaveAppsInDbService;
import com.addie.timesapp.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.addie.timesapp.data.AppProvider.Apps.URI_APPS;

/**
 * Splash screen displayed while loading the app
 */
public class SplashActivity extends Activity {

    private static final int SPLASH_TIMEOUT = 2000;

    private static final int APPS_LOADER_DB_ID = 486;

    private static final int APPS_LOADER_MANAGER_ID = 131;


    private boolean mTutorialSeen;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fadein, R.anim.explode);
        setContentView(R.layout.activity_splash);

        Timber.plant(new Timber.DebugTree());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        }
        // Check if permission has been granted manually
        if (!preferences.getBoolean(getString(R.string.usage_permission_pref), false)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && hasUsageStatsPermission(this)) {
                preferences.edit().putBoolean(getString(R.string.usage_permission_pref), true).apply();

            }
        }

        // IF tutorial has been seen once, start loading apps else show tutorial after timeout of SPLASH_TIMEOUT miliseconds
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mTutorialSeen = prefs.getBoolean(getString(R.string.tutorial_seen_key), false);
        if (mTutorialSeen) {

            loadAppsFromManagerOrDb();

        } else {
            Cursor cursor = getContentResolver().query(URI_APPS, null, null, null, null);
            if (cursor != null && cursor.getCount() == 0) {
                startService(new Intent(SplashActivity.this, SaveAppsInDbService.class));
            }
            if (cursor != null) {
                cursor.close();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
                    startActivity(intent);
                }


            }, SPLASH_TIMEOUT);

        }

    }

    /**
     * Checks if usage permission has been granted
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        preferences.edit().putBoolean(getString(R.string.usage_permission_pref), granted).apply();

        return granted;
    }

    /**
     * Checks if app data is present in database. If yes, then loads it otherwise fetches data from PackageManager and saves in database
     */
    private void loadAppsFromManagerOrDb() {
        Cursor cursor = getContentResolver().query(URI_APPS, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            getLoaderManager().initLoader(APPS_LOADER_DB_ID, null, fetchAppsListener);
        } else {
            getLoaderManager().initLoader(APPS_LOADER_MANAGER_ID, null, fetchAppsListener);
        }
        if (cursor != null) {
            cursor.close();
        }

    }


    private LoaderManager.LoaderCallbacks<ArrayList> fetchAppsListener
            = new LoaderManager.LoaderCallbacks<ArrayList>() {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args) {
            Timber.d("onCreateLoader");
            return new SplashActivity.AppLoader(SplashActivity.this, id);
        }

        /**
         * Initialises the RecyclerView displaying the list of apps
         */
        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data) {
            Timber.d("onLoadFinished");

            MainActivity.mAppsList = data;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            startActivity(intent);

        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public static class AppLoader extends AsyncTaskLoader<ArrayList> {

        int mId;

        PackageManager mPackageManager;

        public AppLoader(@NonNull Context context, int id) {
            super(context);
            mId = id;
            mPackageManager = context.getPackageManager();
        }

        /**
         * Loads a list of installed apps on the device using PackageManager
         */
        @Nullable
        @Override
        public ArrayList loadInBackground() {
            Timber.d("loadInBackground");
            ArrayList mAppsList = new ArrayList<>();
            if (mId == APPS_LOADER_MANAGER_ID) {

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


                    Palette p = Palette.from(Utils.getBitmapFromDrawable(app.getmIcon())).generate();
                    app.setmAppColor(p.getVibrantColor(getContext().getResources().getColor(R.color.black)));
                    app.setmTextColor(Utils.getTextColor(app.getmAppColor()));
                    Timber.e("APP:" + app.getmAppColor() + " TEXT " + app.getmTextColor());

                    mAppsList.add(app);
                    values.put(AppColumns.APP_TITLE, app.getmTitle());
                    values.put(AppColumns.PACKAGE_NAME, app.getmPackage());
                    values.put(AppColumns.PALETTE_COLOR, app.getmAppColor());
                    values.put(AppColumns.TEXT_COLOR, app.getmTextColor());
                    getContext().getContentResolver().insert(URI_APPS, values);
                }


            } else if (mId == APPS_LOADER_DB_ID) {

                Cursor cursor = getContext().getContentResolver().query(URI_APPS, null, null, null, null);
                if (cursor != null) {

                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        try {
                            App app = new App();
                            app.setmTitle(cursor.getString(cursor.getColumnIndexOrThrow(AppColumns.APP_TITLE)));
                            app.setmPackage(cursor.getString(cursor.getColumnIndexOrThrow(AppColumns.PACKAGE_NAME)));
                            app.setmIcon(mPackageManager.getApplicationIcon(app.getmPackage()));
                            app.setmAppColor(cursor.getInt(cursor.getColumnIndexOrThrow(AppColumns.PALETTE_COLOR)));
                            app.setmTextColor(cursor.getInt(cursor.getColumnIndexOrThrow(AppColumns.TEXT_COLOR)));
                            mAppsList.add(app);

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        cursor.moveToNext();

                    }
                    cursor.close();
                }
            }

            return mAppsList;
        }

        @Override
        protected void onStartLoading() {

            Timber.d("onStartLoading");
            forceLoad();
        }


    }
}
