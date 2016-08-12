package me.fanjie.testmyalarm02.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.fanjie.testmyalarm02.core.C;
import me.fanjie.testmyalarm02.model.Alarm;

/**
 * Created by fanji on 2016/7/27.
 */
public class DBManager {

    private SQLiteDatabase db;

    public DBManager(Context context) {
        db = new AlarmDBHelper(context).getWritableDatabase();
    }

    public void addAlarm(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_TIME,alarm.getCalendar());
        values.put(C.AlarmEntry.CN_BELL_NAME,alarm.getBellName());
        values.put(C.AlarmEntry.CN_BELL_URL,alarm.getBellUrlString());
        db.insert(C.AlarmEntry.TABLE_NAME,null,values);
    }

    public void delectAlarm(Alarm alarm){
        String selection = C.AlarmEntry._ID + " = ?";
        String[] args = new String[]{alarm.getID()};
        db.delete(C.AlarmEntry.TABLE_NAME,selection,args);
    }



    public void updateAlarm(String alarmId, ContentValues values){

        String selection = C.AlarmEntry._ID + " = ?";
        String[] args = new String[]{alarmId};

        db.update(C.AlarmEntry.TABLE_NAME,values,selection,args);

    }

    public List<Alarm> selectAlarms(){
        List<Alarm> alarms = new ArrayList<>();

        Cursor c = db.rawQuery("select * from " + C.AlarmEntry.TABLE_NAME,null);
        Log.e("slelctAlarms", String.valueOf(c));
        c.moveToFirst();
        while(c.moveToNext()){
            Alarm alarm = new Alarm();
            alarm.setID(c.getString(c.getColumnIndex(C.AlarmEntry._ID)));
            alarm.setCalendar(c.getLong(c.getColumnIndex(C.AlarmEntry.CN_TIME)));
            alarm.setWork(c.getString(c.getColumnIndex(C.AlarmEntry.CN_IS_WORK)));
            alarm.setRepeat(c.getString(c.getColumnIndex(C.AlarmEntry.CN_IS_REPEAT)));
            alarm.setRepeatDays(c.getString(c.getColumnIndex(C.AlarmEntry.CN_REPEAT_DAYS)));
            alarm.setBellName(c.getString(c.getColumnIndex(C.AlarmEntry.CN_BELL_NAME)));
            alarm.setBellUrl(c.getString(c.getColumnIndex(C.AlarmEntry.CN_BELL_URL)));
            alarm.setShack(c.getString(c.getColumnIndex(C.AlarmEntry.CN_IS_SHACK)));
            alarm.setFlag(c.getString(c.getColumnIndex(C.AlarmEntry.CN_FLAG)));
            alarm.setQrImage(c.getBlob(c.getColumnIndex(C.AlarmEntry.CN_QR_IMAGE)));
            alarm.setKeyCode(c.getString(c.getColumnIndex(C.AlarmEntry.CN_KEY_CODE)));

            Log.e("alarm", alarm.toString());
            alarms.add(alarm);
        }
        return alarms;
    }
}
