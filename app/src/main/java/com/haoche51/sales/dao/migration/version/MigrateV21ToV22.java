package com.haoche51.sales.dao.migration.version;

import android.database.sqlite.SQLiteDatabase;

import com.haoche51.sales.dao.migration.Migration;
import com.haoche51.sales.dao.migration.MigrationImpl;

/**
 * Created by mac on 15/11/25.
 */
public class MigrateV21ToV22 extends MigrationImpl {

	@Override
	public int applyMigration(SQLiteDatabase db, int currentVersion) {
		prepareMigration(db, currentVersion);
		return getMigratedVersion();
	}

	@Override
	public int getTargetVersion() {
		return 21;
	}

	@Override
	public int getMigratedVersion() {
		return 22;
	}

	@Override
	public Migration getPreviousMigration() {
		return new MigrateV20ToV21();
	}
}
