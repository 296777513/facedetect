package com.facedetect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;

/**
 * Created by l00385426 on 2016/11/18.
 */

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "AVInstallation1";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Get Broadcat");
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
            //获取消息内容
            JSONObject json = new JSONObject(Boolean.parseBoolean(intent.getExtras().getString("com.avos.avoscloud.Data")));

            Log.d(TAG, "got action " + action + " on channel " + channel + " with:"
                    + " \n json:" + json.toString());
//            Iterator itr = json.keys();
//            while (itr.hasNext()) {
//                String key = (String) itr.next();
//                Log.d(TAG, "..." + key + " => " + json.getString(key));
//            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}