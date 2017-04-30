package com.facedetect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.facedetect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by l00385426 on 2016/11/11.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button mSearchFamily;
    private Button mBeVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("liyachao222", "123");
        try {
            String[] strings = getResources().getAssets().list("images");
            for (int i = 0; i < strings.length; i++) {
                Log.i("liyachao222", i + " :" + strings[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }






//        UmengPushHelper.getInstance().sendAndroidFilecast();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Demo demo = new Demo(Constant.UMENG_APPLICATION_KEY, Constant.UMENG_MASTER_SECRET);
//                    try {
//                        demo.sendAndroidBroadcast();
//                    } catch (Exception e) {
//                        Log.i("PushClient","PushClient3 ex: "+e.toString());
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

        // 	{"ret":"SUCCESS","data":{"task_id":"us74882147754655708701","thirdparty_id":"20161027134057"}}
        initTitle();
        titleLayout.setVisibility(View.GONE);
        mSearchFamily = getView(R.id.search_family);
        mBeVolunteer = getView(R.id.be_volunteer);
        mSearchFamily.setOnClickListener(this);
        mBeVolunteer.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.search_family:
                intent = new Intent(MainActivity.this, SearchFamilyActivity.class);
                break;
            case R.id.be_volunteer:
                intent = new Intent(MainActivity.this, VolunteerActivity.class);
                break;
        }
        startActivity(intent);
    }
}
