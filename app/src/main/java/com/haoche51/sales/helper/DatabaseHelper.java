package com.haoche51.sales.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haoche51.record.dao.PhoneRecordDAO;
import com.haoche51.sales.dao.TransactionReadyInfoDAO;
import com.haoche51.sales.dao.VehicleBrandDAO;
import com.haoche51.sales.dao.VehicleModelDAO;
import com.haoche51.sales.dao.VehicleSeriesDAO;
import com.haoche51.sales.dao.migration.version.MigrateV17ToV18;
import com.haoche51.sales.dao.migration.version.MigrateV18ToV19;
import com.haoche51.sales.dao.migration.version.MigrateV19ToV20;
import com.haoche51.sales.dao.migration.version.MigrateV20ToV21;
import com.haoche51.sales.dao.migration.version.MigrateV21ToV22;
import com.haoche51.sales.dao.migration.version.MigrateV22ToV23;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "checker.db";
    public static int DB_VERSION = 23;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    protected void finalize() throws Throwable {
        getWritableDatabase().close();
        super.finalize();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VehicleModelDAO.CREATE_TABLE);
        db.execSQL(VehicleBrandDAO.CREATE_TABLE);
        db.execSQL(VehicleSeriesDAO.CREATE_TABLE);
        db.execSQL(TransactionReadyInfoDAO.CREATE_TABLE);//创建地销准备监控数据表
        db.execSQL(PhoneRecordDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 23:
                new MigrateV22ToV23().applyMigration(db, oldVersion);
                break;
            case 22:
                new MigrateV21ToV22().applyMigration(db, oldVersion);
                break;
            case 21:
                new MigrateV20ToV21().applyMigration(db, oldVersion);
                break;
            case 20:
                new MigrateV19ToV20().applyMigration(db, oldVersion);
                break;
            case 19:
                new MigrateV18ToV19().applyMigration(db, oldVersion);
                break;
            case 18:
                new MigrateV17ToV18().applyMigration(db, oldVersion);
                break;
            //TODO ...
        }
    }
}
