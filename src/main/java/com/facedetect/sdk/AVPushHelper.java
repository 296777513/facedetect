package com.facedetect.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.SendCallback;
import com.facedetect.application.MyApplication;
import com.faceplusplus.api.FaceDetecter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by l00385426 on 2016/11/18.
 */

public class AVPushHelper {

    private FaceDetecter detecter;
    private Context context;
    private Handler detectHandler;
    private HandlerThread detectThread;
    private AVPush push;


    private static class AVPushHelperHolder {
        private static AVPushHelper AVPushHelper = new AVPushHelper(MyApplication.getContextObject());
    }


    public static AVPushHelper getInstance() {
        return AVPushHelperHolder.AVPushHelper;
    }

    private AVPushHelper(Context context) {
        this.context = context;
        detectThread = new HandlerThread("detect");
        detectThread.start();
        detectHandler = new Handler(detectThread.getLooper());

        push = new AVPush();
        JSONObject object = new JSONObject();
        try {
            object.put("alert", "push message to android device directly");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        push.setPushToAndroid(true);
        push.setData(object);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // push successfully.
                    Log.i("AVInstallation", "successfully");
                } else {
                    // something wrong.
                    Log.i("AVInstallation", "wrong");
                }
            }
        });
    }

}
