package com.haoche51.sales.dao.migration.version;

import android.database.sqlite.SQLiteDatabase;

import com.haoche51.sales.dao.migration.Migration;
import com.haoche51.sales.dao.migration.MigrationImpl;

/**
 * Created by mac on 15/11/25.
 */
public class MigrateV20ToV21 extends MigrationImpl {

	@Override
	public int applyMigration(SQLiteDatabase db, int currentVersion) {
		prepareMigration(db, currentVersion);
		//之前加了一下验车表字段 现在验车表删除了
		return getMigratedVersion();
	}

	@Override
	public int getTargetVersion() {
		return 20;
	}

	@Override
	public int getMigratedVersion() {
		return 21;
	}

	@Override
	public Migration getPreviousMigration() {
		return new MigrateV19ToV20();
	}
}
