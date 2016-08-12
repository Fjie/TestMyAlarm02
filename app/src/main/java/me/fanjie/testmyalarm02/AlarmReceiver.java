package me.fanjie.testmyalarm02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import me.fanjie.testmyalarm02.activity.AlarmBellActivity;
import me.fanjie.testmyalarm02.core.C;
import me.fanjie.testmyalarm02.model.Alarm;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e(this.toString(),intent+"++++++++++++++++++++");
        Alarm alarm = (Alarm) intent.getSerializableExtra(C.ALARM);
        AlarmBellActivity.startMe(context,alarm);


    }
}
