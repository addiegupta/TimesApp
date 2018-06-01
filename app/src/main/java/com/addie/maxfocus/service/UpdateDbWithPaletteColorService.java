package com.addie.maxfocus.service;

import android.app.IntentService;
import android.content.Intent;

import com.addie.maxfocus.model.App;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdateDbWithPaletteColorService extends IntentService {


    public UpdateDbWithPaletteColorService() {
        super("UpdateDbWithPaletteColorService");
    }


    private static final String APPS_LIST_KEY = "apps_list";
    private ArrayList<App> mAppsList;

    @Override
    protected void onHandleIntent(Intent intent) {

        mAppsList = intent.getParcelableArrayListExtra(APPS_LIST_KEY);

    }
}
