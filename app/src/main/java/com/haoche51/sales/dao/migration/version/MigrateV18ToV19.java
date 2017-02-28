package com.haoche51.sales.dao.migration.version;

import android.database.sqlite.SQLiteDatabase;

import com.haoche51.sales.dao.migration.Migration;
import com.haoche51.sales.dao.migration.MigrationImpl;

public class MigrateV18ToV19 extends MigrationImpl {

	@Override
	public int applyMigration(SQLiteDatabase db, int currentVersion) {
		prepareMigration(db, currentVersion);


		return getMigratedVersion();
	}

	@Override
	public int getTargetVersion() {
		return 18;
	}

	@Override
	public int getMigratedVersion() {
		return 19;
	}

	@Override
	public Migration getPreviousMigration() {
		return new MigrateV17ToV18();
	}
}