package com.addie.maxfocus.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.addie.maxfocus.R;
import com.addie.maxfocus.adapter.AppAdapter;
import com.addie.maxfocus.data.AppColumns;
import com.addie.maxfocus.extra.RecyclerViewDisabler;
import com.addie.maxfocus.model.App;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.addie.maxfocus.data.AppProvider.Apps.URI_APPS;

/**
 * Displays a list of apps from which an app is selected for launching with a timer
 */
//TODO: Correct everything for rotation
public class AppsActivity extends AppCompatActivity implements AppAdapter.AppOnClickHandler, OnShowcaseEventListener {

    private static final String ACTION_APP_DIALOG = "com.addie.maxfocus.service.action.APP_DIALOG";
    private static final int APPS_LOADER_MANAGER_ID = 131;
    private static final int APPS_LOADER_DB_ID = 486;
    private static final String APPS_LIST_KEY = "apps_list";
    private static final String APP_COLOR_KEY = "app_color";
    private static final String TEXT_COLOR_KEY = "text_color";
    private static final String CALLING_CLASS_KEY = "calling_class";


    private ShowcaseView mShowcaseView;

    @BindView(R.id.rv_apps)
    RecyclerView mAppsRecyclerView;
    @BindView(R.id.pb_apps_loading_indicator)
    ProgressBar mLoadingIndicator;


    private static final String APP_IN_USE_KEY = "app_in_use";
    //TODO: Change parameter name
    private static final String TARGET_PACKAGE_KEY = "target_package";

    private AppAdapter mAdapter;
    private App mSelectedApp;
    private static PackageManager mPackageManager;
    private RecyclerView.OnItemTouchListener mRecyclerViewDisabler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        ButterKnife.bind(this);

        // Start Loading Apps
        loadAppsFromManagerOrDb();
    }

    private void loadAppsFromManagerOrDb() {
        Cursor cursor = getContentResolver().query(URI_APPS, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            getSupportLoaderManager().initLoader(APPS_LOADER_DB_ID, null, fetchAppsListener);
        } else {
            getSupportLoaderManager().initLoader(APPS_LOADER_MANAGER_ID, null, fetchAppsListener);
        }
        if (cursor != null) {

            cursor.close();
        }

    }

    /**
     * Called by AppAdapter when an app is selected in the RecyclerView
     *
     * @param selectedApp the app that is selected
     */
    @Override
    public void onClick(App selectedApp) {
        mSelectedApp = selectedApp;
        try {
            createShortcut();
            Toast.makeText(this, R.string.shortcut_created, Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Called by AppAdapter when an app is long clickedin the RecyclerView
     *
     * @param selectedApp the app that is selected
     */
    @Override
    public void onLongClick(App selectedApp) {
        mSelectedApp = selectedApp;

        showTimerDialog();
    }

    /**
     * Shows timer dialog to select the duration for which the selected app is to be run
     */
    public void showTimerDialog() {

        TimeDialog tdialog = new TimeDialog(this, mSelectedApp.getmPackage()
                , mSelectedApp.getmAppColor(), mSelectedApp.getmTextColor());
        tdialog.show();

        tdialog.getWindow().getDecorView().setBackgroundColor(mSelectedApp.getmAppColor());
        tdialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Window window = ((AlertDialog) dialog).getWindow();
                window.getDecorView().setBackgroundColor(mSelectedApp.getmAppColor());
            }
        });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apps_action_refresh_list:
                refreshAppsList();
                break;
            default:
                break;
        }
        return true;
    }

    private void refreshAppsList() {
        if (mAdapter != null) {

            mAdapter.setListData(null);
        }
        getContentResolver().delete(URI_APPS, null, null);
        loadAppsFromManagerOrDb();

    }

    private LoaderManager.LoaderCallbacks<ArrayList> fetchAppsListener
            = new LoaderManager.LoaderCallbacks<ArrayList>() {
        @NonNull
        @Override
        public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args) {
            Timber.d("onCreateLoader");
            showRecyclerView(false);
            return new AppLoader(AppsActivity.this, id);
        }

        /**
         * Initialises the RecyclerView displaying the list of apps
         */
        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data) {
            showRecyclerView(true);
            Timber.d("onLoadFinished");
            mAdapter = new AppAdapter(AppsActivity.this, AppsActivity.this);
            mAppsRecyclerView.setAdapter(mAdapter);

            mAdapter.setListData(data);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AppsActivity.this);
            if (!preferences.contains(getString(R.string.pref_display_showcase_apps))) {

                mRecyclerViewDisabler = new RecyclerViewDisabler();

                mAppsRecyclerView.addOnItemTouchListener(mRecyclerViewDisabler);
                displayShowcaseView();
            }

            mAppsRecyclerView.setLayoutManager(new GridLayoutManager(AppsActivity.this, 4, LinearLayoutManager.VERTICAL, false));
            mAppsRecyclerView.setHasFixedSize(true);

            getSupportLoaderManager().destroyLoader(APPS_LOADER_MANAGER_ID);

        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList> loader) {

        }
    };

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }

    /**
     * Displays the showcaseView tutorial on using the app icons
     */
    private void displayShowcaseView() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                // TODO Add enter animation for the showcaseView
                RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
                lps.setMargins(margin, margin, margin, margin);


                ViewTarget target = new ViewTarget(mAppsRecyclerView.getChildAt(4).findViewById(R.id.iv_app_list_item_icon));
                mShowcaseView = new ShowcaseView.Builder(AppsActivity.this)
                        .withMaterialShowcase()
                        .setTarget(target)
                        .setContentTitle(R.string.showcase_apps_title)
                        .setContentText(R.string.showcase_apps_message)
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setShowcaseEventListener(AppsActivity.this)
                        .replaceEndButton(R.layout.view_custom_button)
                        .build();
                mShowcaseView.setButtonPosition(lps);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AppsActivity.this);
                preferences.edit().putBoolean(getString(R.string.pref_display_showcase_apps), true).apply();

                mAppsRecyclerView.removeOnItemTouchListener(mRecyclerViewDisabler);

            }
        }, 500);


    }

    private static int getTextColor(int color) {

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

    public static class AppLoader extends AsyncTaskLoader<ArrayList> {

        int mId;

        public AppLoader(@NonNull Context context, int id) {
            super(context);
            mPackageManager = context.getPackageManager();
            mId = id;
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

                    Palette p = Palette.from(((BitmapDrawable) app.getmIcon()).getBitmap()).generate();
                    app.setmAppColor(p.getVibrantColor(getContext().getResources().getColor(R.color.black)));
                    app.setmTextColor(getTextColor(app.getmAppColor()));
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

    public void createShortcut() throws PackageManager.NameNotFoundException {
        Bitmap icon = getAppIconShortcut();


        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        shortcutintent.putExtra("duplicate", true);

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mSelectedApp.getmTitle());

        Intent appIntent = new Intent(getApplicationContext(), DialogActivity.class);
        appIntent.putExtra(TARGET_PACKAGE_KEY, mSelectedApp.getmPackage());
        appIntent.putExtra(APP_COLOR_KEY, mSelectedApp.getmAppColor());
        appIntent.putExtra(TEXT_COLOR_KEY, mSelectedApp.getmTextColor());

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, appIntent);
        sendBroadcast(shortcutintent);
    }

    private Bitmap getAppIconShortcut() throws PackageManager.NameNotFoundException {

        Bitmap appIcon = ((BitmapDrawable) getPackageManager().getApplicationIcon(mSelectedApp.getmPackage())).getBitmap();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.pref_shortcut_icon_key), true)) {
            Bitmap timerIcon = getBitmapFromVectorDrawable(this, R.drawable.ic_timelapse_white_24dp);

            Bitmap bmOverlay = Bitmap.createBitmap(appIcon.getWidth(), appIcon.getHeight(), appIcon.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(appIcon, new Matrix(), null);
            canvas.drawBitmap(timerIcon, 94, 94, null);
            return bmOverlay;
        } else
            return appIcon;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
