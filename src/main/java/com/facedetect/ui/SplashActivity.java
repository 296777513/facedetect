package com.facedetect.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.facedetect.Constant.DataSource;
import com.facedetect.R;
import com.facedetect.sdk.FacePlusPlusHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by l00385426 on 2016/11/18.
 */

public class SplashActivity extends Activity implements View.OnClickListener {

    private ImageView welcome;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        welcome = (ImageView) findViewById(R.id.welcome);
        welcome.setOnClickListener(this);
        intent = new Intent(SplashActivity.this, MainActivity.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.startActivity(intent);
                finish();
            }
        },2000);


        new Thread() {

            @Override
            public void run() {
                FacePlusPlusHelper.getInstance();
                DataSource.getInstance();
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome:
                startActivity(intent);
                finish();
                break;
        }
    }
}
