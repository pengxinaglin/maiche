package com.haoche51.sales.dao.migration.version;

import android.database.sqlite.SQLiteDatabase;

import com.haoche51.sales.dao.TransactionReadyInfoDAO;
import com.haoche51.sales.dao.migration.Migration;
import com.haoche51.sales.dao.migration.MigrationImpl;

/**
 * Created by mac on 15/11/25.
 */
public class MigrateV22ToV23 extends MigrationImpl {

  @Override
  public int applyMigration(SQLiteDatabase db, int currentVersion) {
    prepareMigration(db, currentVersion);
    try {
      db.execSQL(TransactionReadyInfoDAO.CREATE_TABLE);//创建上传中任务表
    } catch (Exception e) {
      e.printStackTrace();
    }

    return getMigratedVersion();
  }

  @Override
  public int getTargetVersion() {
    return 22;
  }

  @Override
  public int getMigratedVersion() {
    return 23;
  }

  @Override
  public Migration getPreviousMigration() {
    return new MigrateV21ToV22();
  }
}
