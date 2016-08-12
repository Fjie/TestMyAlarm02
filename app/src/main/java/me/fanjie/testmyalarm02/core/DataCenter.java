package me.fanjie.testmyalarm02.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.fanjie.testmyalarm02.AlarmReceiver;
import me.fanjie.testmyalarm02.db.AlarmDBHelper;
import me.fanjie.testmyalarm02.db.DBManager;
import me.fanjie.testmyalarm02.model.Alarm;

/**
 * Created by fanjie on 2016/5/17.
 * 数据中心，处理数据请求、数据库存取等
 */
public class DataCenter {

    private Context context;
    private DBManager dm;
    private AlarmManager am;

    public DataCenter(Context context) {
        this.context = context;
        dm = new DBManager(context);
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


    }

    public void openAlarm(Alarm alarm){
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(C.ALARM,alarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,10086,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP,alarm.getCalendar(),pendingIntent);
        changeWork(alarm);
    }

    public void closeAlarm(Alarm alarm){
        changeWork(alarm);
    }

    public void addAlarm(Alarm alarm){
        dm.addAlarm(alarm);
    }

    public void delectAlarm(Alarm alarm){
        dm.delectAlarm(alarm);
    }

    public List<Alarm> getAlarms(){

        return dm.selectAlarms();
    }

    public void addBell(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_BELL_NAME,alarm.getBellName());
        values.put(C.AlarmEntry.CN_BELL_URL,alarm.getBellUrlString());
        dm.updateAlarm(alarm.getID(),values);
    }

    public void addQRCode(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_KEY_CODE,alarm.getKeyCode());
        values.put(C.AlarmEntry.CN_QR_IMAGE,alarm.getQrImageToBD());
        dm.updateAlarm(alarm.getID(),values);
    }

    public void changeShack(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_IS_SHACK,alarm.isShack()+"");
        dm.updateAlarm(alarm.getID(),values);
    }

    public void changeWork(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_IS_WORK,alarm.isWork()+"");
        dm.updateAlarm(alarm.getID(),values);
    }
    public void addFlag(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_FLAG,alarm.getFlag());
        dm.updateAlarm(alarm.getID(),values);
    }

    public void changeRepeat(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_IS_REPEAT,alarm.isRepeat()+"");
        values.put(C.AlarmEntry.CN_REPEAT_DAYS,alarm.getRepeatDaysToDB());
        dm.updateAlarm(alarm.getID(),values);
    }

    public void changeTime(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(C.AlarmEntry.CN_TIME,alarm.getCalendar());
        dm.updateAlarm(alarm.getID(),values);
    }
}
