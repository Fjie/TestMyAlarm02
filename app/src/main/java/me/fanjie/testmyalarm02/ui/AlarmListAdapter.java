package me.fanjie.testmyalarm02.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.fanjie.testmyalarm02.R;
import me.fanjie.testmyalarm02.model.Alarm;

/**
 * Created by fanjie on 2016/5/7.
 */
public class AlarmListAdapter extends BaseAdapter {

    private static final int NOT_CURRENT_POSITION = -999;

    private Context context;
    private List<Alarm> alarms;
    private AlarmItemClickCallback callback;
    private int currentPosition = NOT_CURRENT_POSITION;

    public AlarmListAdapter(Context context, List<Alarm> alarms) {
        this.context = context;
        this.alarms = alarms;
    }

    public void addAlarm(Alarm alarm) {
        if (alarms == null) {
            alarms = new ArrayList<>();
        }
        alarms.add(alarm);
        expandItem(alarms.size() - 1);
        notifyDataSetChanged();
    }

    public void deleteAlarm(int position) {
        if(currentPosition == position){
            currentPosition = NOT_CURRENT_POSITION;
        }
        alarms.remove(position);
        notifyDataSetChanged();
    }


    public void expandItem(int position) {
        Log.e(this.toString(), "expandItem");
        if (currentPosition != NOT_CURRENT_POSITION && currentPosition != position) {
            getItem(currentPosition).setExpand(false);
        }
        Alarm a = getItem(position);
        if (a.isExpand()) {
            a.setExpand(false);
            currentPosition = NOT_CURRENT_POSITION;
        } else {
            a.setExpand(true);
            currentPosition = position;
        }
        notifyDataSetChanged();
    }

    public void setCallback(AlarmItemClickCallback callback) {
        this.callback = callback;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public int getCount() {
        if (alarms != null) {
            return alarms.size();
        }
        return 0;
    }

    @Override
    public Alarm getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View v, ViewGroup parent) {

//        Log.e(this.toString(), "getView");
        final Alarm alarm = getItem(position);
        ViewHolder h;
        v = LayoutInflater.from(context).inflate(R.layout.layout_alarm_list_item, null);
        h = new ViewHolder();
        h.tvTime = (TextView) v.findViewById(R.id.tv_time);
        h.swAlarm = (Switch) v.findViewById(R.id.sw_alarm);
        h.llExpandContent = (LinearLayout) v.findViewById(R.id.ll_expand_content);
        h.cbIsRepeat = (CheckBox) v.findViewById(R.id.cb_is_repeat);
        h.llRepeatDays = (LinearLayout) v.findViewById(R.id.ll_repeat_days);
        int childs = h.llRepeatDays.getChildCount();
//        Log.e(this.toString(), childs + "");
        h.cbsRepeatDays = new CheckBox[childs];
        for (int i = 0; i < childs; i++) {
//            Log.e(this.toString(), "for" + i);
            final int day = i;
            h.cbsRepeatDays[day] = (CheckBox) h.llRepeatDays.getChildAt(day);
            h.cbsRepeatDays[day].setChecked(alarm.getRepeatDays()[day]);
            h.cbsRepeatDays[day].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    alarm.setRepeatDays(day,isChecked);
                    callback.isRepeatDayChecked(position,alarm);
                    Log.e(this.toString(), alarm.toString());
                }
            });
        }
        h.tvSelectBell = (TextView) v.findViewById(R.id.tv_select_bell);
        h.cbIsShack = (CheckBox) v.findViewById(R.id.cb_is_shack);
        h.tvInputFlag = (TextView) v.findViewById(R.id.tv_input_flag);
        h.llTakeQRCode = (LinearLayout) v.findViewById(R.id.ll_take_qr_code);
        h.tvQRCode = (TextView) v.findViewById(R.id.tv_qr_code);
        h.tvReqeatDays = (TextView) v.findViewById(R.id.tv_repeat_days);
        h.ivDelete = (ImageView) v.findViewById(R.id.iv_delete);
        h.ivExpandBtn = (ImageView) v.findViewById(R.id.ivExpandBtn);

        v.setTag(h);


        h.tvTime.setText(alarm.getTimeString());
        h.swAlarm.setChecked(alarm.isWork());

        if (alarm.isExpand()) {
            h.tvReqeatDays.setVisibility(View.GONE);
            h.cbIsRepeat.setChecked(alarm.isRepeat());
            h.tvSelectBell.setText(alarm.getBellName());
            h.cbIsShack.setChecked(alarm.isShack());
            h.tvInputFlag.setText(alarm.getFlag());
            h.ivExpandBtn.setImageResource(R.drawable.ic_expand_less_black_24dp);
            if (alarm.isRepeat()) {
                h.llRepeatDays.setVisibility(View.VISIBLE);
            } else {
                h.llRepeatDays.setVisibility(View.GONE);
            }

            if (alarm.hasQRCode()) {
                h.tvQRCode.setBackgroundResource(R.drawable.ic_insert_photo_black_24dp);

            } else {
                h.tvQRCode.setBackground(null);
            }
            h.llExpandContent.setVisibility(View.VISIBLE);
            h.ivDelete.setVisibility(View.VISIBLE);

        } else {
            h.llExpandContent.setVisibility(View.GONE);
            h.ivDelete.setVisibility(View.GONE);
            h.tvReqeatDays.setText(alarm.getRepeatDaysString());
            h.ivExpandBtn.setImageResource(R.drawable.ic_expand_more_black_24dp);
            h.tvReqeatDays.setVisibility(View.VISIBLE);
        }
        
        h.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null){
                    callback.timeClick(position, alarm);
                }
            }
        });

        h.swAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (callback != null) {
                    callback.isWorkChanged(position, alarm, isChecked);
                }
            }
        });

        h.cbIsRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (callback != null) {
                    callback.isRepeatChecked(position, alarm, isChecked);
                }
            }
        });

        h.tvSelectBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.selectBillClick(position, alarm);
                }
            }
        });

        h.cbIsShack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (callback != null) {
                    callback.isShackChecked(position, alarm, isChecked);
                }
            }
        });

        h.tvInputFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.inputFlagClick(position, alarm);
            }
        });

        h.llTakeQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.takeQRCodeClick(position, alarm);
            }
        });

        h.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deleteClick(position, alarm);
            }
        });

        return v;
    }

    private class ViewHolder {

        TextView tvTime;
        Switch swAlarm;
        LinearLayout llExpandContent;
        CheckBox cbIsRepeat;
        LinearLayout llRepeatDays;
        CheckBox[] cbsRepeatDays;
        TextView tvSelectBell;
        CheckBox cbIsShack;
        TextView tvInputFlag;
        LinearLayout llTakeQRCode;
        TextView tvQRCode;
        TextView tvReqeatDays;
        ImageView ivDelete;
        ImageView ivExpandBtn;
    }

    public interface AlarmItemClickCallback {

        void isRepeatChecked(int position, Alarm alarm, boolean isChecked);

        void isRepeatDayChecked(int position, Alarm alarm);

        void isShackChecked(int position, Alarm alarm, boolean isChecked);

        void selectBillClick(int position, Alarm alarm);

        void isWorkChanged(int position, Alarm alarm, boolean isChecked);

        void inputFlagClick(int position, Alarm alarm);

        void takeQRCodeClick(int position, Alarm alarm);

        void deleteClick(int position, Alarm alarm);

        void timeClick(int position, Alarm alarm);
    }
}
