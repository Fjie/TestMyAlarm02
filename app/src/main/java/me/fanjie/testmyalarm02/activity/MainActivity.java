package me.fanjie.testmyalarm02.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.jar.Manifest;

import me.fanjie.testmyalarm02.core.DataCenter;
import me.fanjie.testmyalarm02.ui.AlarmListAdapter;
import me.fanjie.testmyalarm02.R;
import me.fanjie.testmyalarm02.model.Alarm;
import me.fanjie.zxing.activity.CaptureActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AlarmListAdapter.AlarmItemClickCallback {

    public static final int REQUEST_CODE_SELECT_BILL = 10086;
    public static final int REQUEST_CODE_TAKE_QR_CODE = 10000;
    private static final int REQUEST_CODE_PERMISSIONS = 1010;

    private ListView lvAlarmList;
    private AlarmListAdapter adapter;

    private Alarm currentAlarm;
    private DataCenter dataCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataCenter = new DataCenter(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(true);
                }
            });
        }

        lvAlarmList = (ListView) findViewById(R.id.lv_alarm_list);
        lvAlarmList.setOnItemClickListener(this);



        adapter = new AlarmListAdapter(this, dataCenter.getAlarms());
        adapter.setCallback(this);
        lvAlarmList.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTime(final boolean isNewAlarm) {
        long l = isNewAlarm?System.currentTimeMillis():currentAlarm.getCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (isNewAlarm) {
                    Alarm alarm = new Alarm(hourOfDay, minute);
                    alarm.setBellUrl(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
                    alarm.setBellName("默认铃声");
                    adapter.addAlarm(alarm);
                    dataCenter.addAlarm(alarm);
                    currentAlarm = alarm;
                    lvAlarmList.setSelection(adapter.getCurrentPosition());
                }else {
                    currentAlarm.setCalendar(hourOfDay,minute);
                    adapter.notifyDataSetChanged();
                    dataCenter.changeTime(currentAlarm);
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setFlag(String flag) {

        View root = LayoutInflater.from(this).inflate(R.layout.dialog_input_flag, null);
        final EditText etInputFlag = (EditText) root.findViewById(R.id.et_input_flag);
        etInputFlag.setText(flag);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("标签");
        builder.setView(root);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(etInputFlag.getText())) {
                    currentAlarm.setFlag(etInputFlag.getText().toString());
                    adapter.notifyDataSetChanged();
                    dataCenter.addFlag(currentAlarm);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etInputFlag, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_BILL) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                String str;
                if (uri == null) {
                    str = "不响铃";
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                    str = ringtone.getTitle(this);
                }
                Log.e(this.toString(), str + uri);
                if (currentAlarm != null) {
                    currentAlarm.setBellUrl(uri.toString());
                    currentAlarm.setBellName(str);
                    dataCenter.addBell(currentAlarm);
                }
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == REQUEST_CODE_TAKE_QR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String str = bundle.getString("result");
                Bitmap bitmap = bundle.getParcelable("barcode");
                currentAlarm.setKeyCode(str);
                currentAlarm.setQrImage(bitmap);
                adapter.notifyDataSetChanged();
                dataCenter.addQRCode(currentAlarm);
                Log.e(this.toString(), str + bitmap);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(this.toString(), "onClick");
        adapter.expandItem(position);
        lvAlarmList.setSelection(position);
    }

    @Override
    public void isRepeatChecked(int position, Alarm alarm, boolean isChecked) {
        alarm.setRepeat(isChecked);
        adapter.notifyDataSetChanged();
        dataCenter.changeRepeat(alarm);
    }

    @Override
    public void isRepeatDayChecked(int position, Alarm alarm) {
        currentAlarm = alarm;
        dataCenter.changeRepeat(alarm);
    }

    @Override
    public void isShackChecked(int position, Alarm alarm, boolean isChecked) {
        alarm.setShack(isChecked);
        currentAlarm = alarm;
        adapter.notifyDataSetChanged();
        dataCenter.changeShack(alarm);
    }

    @Override
    public void selectBillClick(int position, Alarm alarm) {
        currentAlarm = alarm;
        Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(i, REQUEST_CODE_SELECT_BILL);
    }

    @Override
    public void isWorkChanged(int position, Alarm alarm, boolean isChecked) {
        alarm.setWork(isChecked);
        adapter.notifyDataSetChanged();
        if(isChecked){
            dataCenter.openAlarm(alarm);
        }else {
            dataCenter.closeAlarm(alarm);
        }
    }

    @Override
    public void inputFlagClick(int position, Alarm alarm) {
        currentAlarm = alarm;
        setFlag(alarm.getFlag());
    }

    @Override
    public void takeQRCodeClick(int position, Alarm alarm) {
        currentAlarm = alarm;

        if(alarm.hasQRCode()) {
            QRCodeActivity.startMyActivity(this,alarm.getQrImage(),1010);
        }else {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},REQUEST_CODE_PERMISSIONS);
            }else {
                CaptureActivity.startMe(this, "添加二维码（条形码）", REQUEST_CODE_TAKE_QR_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                CaptureActivity.startMe(this, "添加二维码（条形码）", REQUEST_CODE_TAKE_QR_CODE);
            }else {
                Toast.makeText(this,"获取二维码需要摄像头权限",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void deleteClick(int position, Alarm alarm) {
        currentAlarm = null;
        adapter.deleteAlarm(position);
        dataCenter.delectAlarm(alarm);
    }

    @Override
    public void timeClick(int position, Alarm alarm) {
        currentAlarm = alarm;
        setTime(false);
    }


}
