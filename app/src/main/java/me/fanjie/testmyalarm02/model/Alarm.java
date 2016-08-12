package me.fanjie.testmyalarm02.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import me.fanjie.testmyalarm02.core.C;

/**
 * Created by fanjie on 2016/5/5.
 * 闹钟模型
 */
public class Alarm implements Serializable {

    public static final String[] DAYS_OF_WEEK = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private Calendar calendar;//日期时间
    private boolean isWork;//闹钟是否打开
    private boolean isRepeat;//是否重复
    private boolean[] repeatDays = new boolean[7];//重复日
    private String bellName;//铃声名
    private String bellUrl;//铃声Url
    private boolean isShack;//是否震动
    private String flag;//标签

    private byte[] qrImage;//二维码图片
    private String keyCode;//二维码字符

    private boolean isExpand;//是否展开

    private String ID;


    public Alarm() {
    }

    public Alarm(Calendar calendar) {
        this.calendar = calendar;
    }

    public Alarm(long milliseconds) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(milliseconds);
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Alarm(int hourOfDay, int minute) {
        setCalendar(hourOfDay, minute);
    }

    public long getCalendar() {
        return calendar.getTimeInMillis();
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setCalendar(long milliseconds) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(milliseconds);
    }

    public void setCalendar(int hourOfDay, int minute) {
        if (this.calendar == null) {
            this.calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
    }

    public String getTimeString() {
//        TODO 暂时只做24小时制的，之后补充12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(calendar.getTime());
    }

    public String getRepeatDaysToDB() {
        StringBuilder builder = new StringBuilder(Arrays.toString(repeatDays));
        for (boolean b :
                repeatDays) {
            builder.append(b);
            builder.append(C.REPEAT_DAY_SPLIT_CHAR);
        }
        Log.e("repeatDayToDB", builder.toString());
        return builder.toString();
    }

    public String getRepeatDaysString() {
        StringBuilder sb = new StringBuilder();
        if (getFlag() != null && !getFlag().isEmpty()) {
            sb.append(getFlag() + "  ");
        }
        if (isRepeat()) {
            if (isEveryDayRepeat()) {
                sb.append("每天");
            } else {
                for (int i = 0; i < repeatDays.length; i++) {
                    if (repeatDays[i]) {
                        sb.append(DAYS_OF_WEEK[i] + ",");
                    }
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                } else {
                    setRepeat(false);
                    return getRepeatDaysString();
                }
            }
        } else {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());

            if (calendar.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY)) {
                sb.append("明天");
            } else if (calendar.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.MINUTE) <= now.get(Calendar.MINUTE)) {
                sb.append("明天");
            } else {
                sb.append("今天");
            }

        }
        return sb.toString();
    }

    public boolean hasQRCode() {
        return getKeyCode() != null && !getKeyCode().isEmpty() && getQrImage() != null;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    public boolean isRepeat() {
        return isRepeat && repeatDays != null;
    }

    public boolean isEveryDayRepeat() {
        if (isRepeat()) {
            for (boolean b : getRepeatDays()) {
                if (!b) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setRepeat(String repeat) {
        setRepeat(Boolean.parseBoolean(repeat));
    }

    public void setRepeat(boolean repeat) {
        if (!repeat) {
            for (int i = 0; i < getRepeatDays().length; i++) {
                getRepeatDays()[i] = false;
            }
        }
        isRepeat = repeat;
    }

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public Bitmap getQrImage() {
        if (qrImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(qrImage, 0, qrImage.length);
            return bitmap;
        } else {
            return null;
        }
    }

    public byte[] getQrImageToBD() {

        return qrImage;
    }

    public void setQrImage(Bitmap qrImage) {
        if (qrImage != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            qrImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
            this.qrImage = bos.toByteArray();
        }
    }

    public String getBellUrl() {
        return bellUrl;
    }

    public String getBellUrlString() {
        if (bellUrl == null) {
            return null;
        }
        return bellUrl.toString();
    }


    public boolean isShack() {
        return isShack;
    }

    public void setShack(boolean shack) {
        isShack = shack;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setRepeatDays(String buildString) {
        if (buildString != null) {
            String[] strs = buildString.split(C.REPEAT_DAY_SPLIT_CHAR);
            if (strs.length == 7) {
                for (int i = 0; i < 7; i++) {
                    setRepeatDays(i, Boolean.parseBoolean(strs[i]));
                }
            }
            Log.e("setRepeatDays", strs.toString());
        }
    }

    public void setRepeatDays(int index, boolean repeat) {
        this.repeatDays[index] = repeat;
    }


    public String getBellName() {
        return bellName;
    }

    public void setBellName(String bellName) {
        this.bellName = bellName;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "calendar=" + getCalendar() +
                ", isWork=" + isWork +
                ", isRepeat=" + isRepeat +
                ", repeatDays=" + Arrays.toString(repeatDays) +
                ", bellName='" + bellName + '\'' +
                ", bellUrl=" + bellUrl +
                ", isShack=" + isShack +
                ", flag='" + flag + '\'' +
                ", qrImage=" + qrImage +
                ", keyCode='" + keyCode + '\'' +
                ", isExpand=" + isExpand +
                ", ID='" + ID + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public void setBellUrl(String string) {
        this.bellUrl = string;
    }

    public void setWork(String string) {
        setWork(Boolean.parseBoolean(string));
    }

    public void setShack(String string) {
        setShack(Boolean.parseBoolean(string));
    }

    public void setQrImage(byte[] blob) {
        this.qrImage = blob;

    }

}
