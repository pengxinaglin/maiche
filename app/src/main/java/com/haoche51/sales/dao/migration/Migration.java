package com.haoche51.sales.dao.migration;

import android.database.sqlite.SQLiteDatabase;

public interface Migration {

  int applyMigration(SQLiteDatabase db, int currentVersion);

  Migration getPreviousMigration();

  int getTargetVersion();

  int getMigratedVersion();
}