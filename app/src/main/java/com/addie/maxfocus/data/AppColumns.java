package com.addie.maxfocus.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by addie on 04-04-2018.
 */

public interface AppColumns {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    String APP_TITLE="app_title";

    @DataType(TEXT)
    String PACKAGE_NAME="package_name";

    @DataType(INTEGER)
    String PALETTE_COLOR ="palette_color";


}
