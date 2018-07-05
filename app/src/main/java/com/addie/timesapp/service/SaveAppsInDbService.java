package com.addie.timesapp.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;

import com.addie.timesapp.R;
import com.addie.timesapp.data.AppColumns;
import com.addie.timesapp.extra.Utils;
import com.addie.timesapp.model.App;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.addie.timesapp.data.AppProvider.Apps.URI_APPS;

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

            Palette p = Palette.from(((BitmapDrawable) app.getmIcon()).getBitmap()).generate();
            app.setmAppColor(p.getVibrantColor(getResources().getColor(R.color.black)));
            app.setmTextColor(Utils.getTextColor(app.getmAppColor()));
            Timber.e("APP:" + app.getmAppColor() + " TEXT " + app.getmTextColor());

            values.put(AppColumns.APP_TITLE, app.getmTitle());
            values.put(AppColumns.PACKAGE_NAME, app.getmPackage());
            values.put(AppColumns.PALETTE_COLOR, app.getmAppColor());
            values.put(AppColumns.TEXT_COLOR, app.getmTextColor());
            try{

            getContentResolver().insert(URI_APPS, values);
            }catch (Exception e){
                Timber.e("COULD NOT INSERT FROM SERVICE");
            }
        }


    }

}
