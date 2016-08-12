package me.fanjie.testmyalarm02.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import me.fanjie.testmyalarm02.R;

public class QRCodeActivity extends AppCompatActivity {

    public static final String QR_CODE = "qrcode";

    private ImageView ivQRCode;

    public static void startMyActivity(AppCompatActivity activity, Bitmap bitmap, int requestCode){
        Intent i = new Intent(activity,QRCodeActivity.class);
        i.putExtra(QR_CODE,bitmap);
        activity.startActivityForResult(i,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ivQRCode = (ImageView) findViewById(R.id.iv_qrcode);

        Bitmap bitmap = getIntent().getParcelableExtra(QR_CODE);
        ivQRCode.setImageBitmap(bitmap);
    }

}
