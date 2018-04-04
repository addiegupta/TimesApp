package com.addie.maxfocus.ui;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.addie.maxfocus.R;
import com.addie.maxfocus.adapter.AppAdapter;
import com.addie.maxfocus.model.App;
import com.addie.maxfocus.receiver.AppDialogBroadcastReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Displays a list of apps from which an app is selected for launching with a timer
 */
//TODO: Correct everything for rotation
//TODO Cache packages in database
//TODO: Launch timer option for selected apps when launched from launcher
public class AppsActivity extends AppCompatActivity implements AppAdapter.AppOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList> {

    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final int APPS_LOADER_ID = 131;

    @BindView(R.id.rv_apps)
    RecyclerView mAppsRecyclerView;
    @BindView(R.id.pb_apps_loading_indicator)
    ProgressBar mLoadingIndicator;

    private AppAdapter mAdapter;
    private App mSelectedApp;
    private AppDialogBroadcastReceiver mAppDialogBroadcastReceiver;
    private static PackageManager mPackageManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        ButterKnife.bind(this);
        Timber.d("onCreate");

        requestUsageStatsPermission();

        // Start Loading Apps
        getSupportLoaderManager().initLoader(APPS_LOADER_ID, null, this);

        //Register broadcast receiver to receive "stop app" dialogs
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_APP_DIALOG);
        mAppDialogBroadcastReceiver = new AppDialogBroadcastReceiver();
        registerReceiver(mAppDialogBroadcastReceiver, filter);
    }


    void requestUsageStatsPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAppDialogBroadcastReceiver);
    }

    /**
     * Called by AppAdapter when an app is selected in the RecyclerView
     *
     * @param selectedApp the app that is selected
     */
    @Override
    public void onClick(App selectedApp) {
        mSelectedApp = selectedApp;

        showTimerDialog();
    }

    /**
     * Shows timer dialog to select the duration for which the selected app is to be run
     */
    public void showTimerDialog() {

        TimeDialog tdialog = new TimeDialog(this, mSelectedApp.getmPackage());
        tdialog.show();

    }

    /**
     * Displays either the recyclerView or the progressbar depending upon showRV
     *
     * @param showRV decides which view to be displayed
     */
    private void showRecyclerView(boolean showRV) {
        if (showRV) {

            mAppsRecyclerView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.GONE);
        } else {
            mAppsRecyclerView.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList> onCreateLoader(int id, Bundle args) {
        Timber.d("onCreateLoader");
        showRecyclerView(false);
        return new AppLoader(this);
    }

    /**
     * Initialises the RecyclerView displaying the list of apps
     */

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {

        showRecyclerView(true);
        Timber.d("onLoadFinished");
        mAdapter = new AppAdapter(this, this);
        mAppsRecyclerView.setAdapter(mAdapter);

        mAdapter.setListData(data);

        mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAppsRecyclerView.setHasFixedSize(true);

        getSupportLoaderManager().destroyLoader(APPS_LOADER_ID);

    }


    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {

    }

    public static class AppLoader extends AsyncTaskLoader<ArrayList> {

        public AppLoader(@NonNull Context context) {
            super(context);
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

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> activities = mPackageManager.queryIntentActivities(intent, 0);
            Collections.sort(activities, new ResolveInfo.DisplayNameComparator(mPackageManager));
            for (ResolveInfo ri : activities) {
                App app = new App();
                app.setmPackage(ri.activityInfo.packageName);
                app.setmTitle((String) ri.loadLabel(mPackageManager));
                app.setmIcon(ri.activityInfo.loadIcon(mPackageManager));
                mAppsList.add(app);
            }

            return mAppsList;
        }

        @Override
        protected void onStartLoading() {

            Timber.d("onStartLoading");
            forceLoad();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}