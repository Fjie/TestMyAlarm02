package me.fanjie.testmyalarm02.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.fanjie.testmyalarm02.core.C;

/**
 * Created by fanjie on 2016/5/17.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "AlarmDB.db";

    private static final String TYPE_INT = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_BLOB = " BLOB";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + C.AlarmEntry.TABLE_NAME
            + "("
            + C.AlarmEntry._ID + " INTEGER PRIMARY KEY,"
            + C.AlarmEntry.CN_TIME + TYPE_INT + ","
            + C.AlarmEntry.CN_IS_WORK + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_IS_REPEAT + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_REPEAT_DAYS + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_BELL_NAME + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_BELL_URL + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_IS_SHACK + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_FLAG + TYPE_TEXT+ ","
            + C.AlarmEntry.CN_QR_IMAGE + TYPE_BLOB+ ","
            + C.AlarmEntry.CN_KEY_CODE + TYPE_TEXT
            +")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + C.AlarmEntry.TABLE_NAME;

    static {
        Log.e("---CreateTabel", SQL_CREATE_ENTRIES);
    }

    public AlarmDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
