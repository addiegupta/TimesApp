package com.addie.timesapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface AppColumns {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    String APP_TITLE="app_title";

    @DataType(TEXT) @Unique
    String PACKAGE_NAME="package_name";

    @DataType(INTEGER)
    String PALETTE_COLOR ="palette_color";

    @DataType(INTEGER)
    String TEXT_COLOR = "text_color";


}
