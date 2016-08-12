package me.fanjie.testmyalarm02.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import me.fanjie.testmyalarm02.R;
import me.fanjie.testmyalarm02.core.C;
import me.fanjie.testmyalarm02.model.Alarm;
import me.fanjie.zxing.activity.CaptureActivity;

public class AlarmBellActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_TAKE_QR_CODE = 189;

    private TextView tvAlarmTime;
    private Alarm alarm;

    private MediaPlayer player;
    private Vibrator vibrator;

    public static void startMe(Context context, Alarm alarm) {
        Intent intent = new Intent(context, AlarmBellActivity.class);
        intent.putExtra(C.ALARM, alarm);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_bell);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarm = (Alarm) getIntent().getSerializableExtra(C.ALARM);

        if(!TextUtils.isEmpty(alarm.getFlag())){
            setTitle(alarm.getFlag());
        }

        tvAlarmTime = (TextView) findViewById(R.id.tv_alarm_time);
        tvAlarmTime.setText(alarm.getTimeString());

        Log.e(this.toString(), alarm.toString());

        Uri uri;
        if (alarm.getBellUrl()== null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }else {
            uri = Uri.parse(alarm.getBellUrl());
        }
//        player = MediaPlayer.create(this, uri);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            player.setDataSource(this,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
        player.setVolume(1f,1f);
        player.setLooping(true);
        player.start();

        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        if(alarm.isShack()){
            Log.e("vibrate",alarm.isShack()+"");
            long[] ls = new long[]{100,200,400,500};
            vibrator.vibrate(ls,0);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            if(alarm.hasQRCode()){
                fab.setImageResource(R.drawable.ic_camera_alt_white_24dp);
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(alarm.hasQRCode()){
                        CaptureActivity.startMe(AlarmBellActivity.this,"扫码关闭闹钟",REQUEST_CODE_TAKE_QR_CODE);
                    }else {
                        stopRing();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_TAKE_QR_CODE){
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                String str = bundle.getString("result");
                if(str!=null && str.equals(alarm.getKeyCode())){
                    stopRing();
                }
            }
        }
    }

    private void stopRing(){
        player.stop();
        if(alarm.isShack()){
            vibrator.cancel();
        }
        finish();
    }
}
