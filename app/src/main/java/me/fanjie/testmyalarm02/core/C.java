package me.fanjie.testmyalarm02.core;

import android.provider.BaseColumns;

/**
 * Created by fanjie on 2016/5/17.
 */
public final class C {

    public static final String ALARM = "alarm";
    public static final String BELL_URI = "bellUri";
    /** 分割重复日的字符 */
    public static final String REPEAT_DAY_SPLIT_CHAR = "-";
    public static final String TITLE = "title";


    public static class AlarmEntry implements BaseColumns{
        public static final String TABLE_NAME = "MyAlarmTable";
        public static final String CN_TIME = "time";
        public static final String CN_IS_WORK = "isWork";
        public static final String CN_IS_REPEAT = "isRepeat";
        public static final String CN_REPEAT_DAYS = "repeatDays";
        public static final String CN_BELL_NAME = "bellName";
        public static final String CN_BELL_URL = "bellUrl";
        public static final String CN_IS_SHACK = "isShack";
        public static final String CN_FLAG = "flag";
        public static final String CN_QR_IMAGE = "qrImage";
        public static final String CN_KEY_CODE = "keyCode";
    }
}
