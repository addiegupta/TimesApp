package com.addie.maxfocus.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = AppDatabase.VERSION)
public final class AppDatabase {
    public static final int VERSION = 1;

    @Table(AppColumns.class) public static final String APPS = "apps";
}


