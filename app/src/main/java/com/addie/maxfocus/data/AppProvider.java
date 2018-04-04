package com.addie.maxfocus.data;

import android.net.Uri;

import com.addie.maxfocus.BuildConfig;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = AppProvider.AUTHORITY, database = AppDatabase.class)
public class AppProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    @TableEndpoint(table = AppDatabase.APPS)
    public static class Apps {

        @ContentUri(
                path = "apps",
                type = "vnd.android.cursor.dir/app",
                defaultSort = AppColumns.APP_TITLE + " ASC")
        public static final Uri URI_APPS = Uri.parse("content://" + AUTHORITY + "/apps");

    }


}


