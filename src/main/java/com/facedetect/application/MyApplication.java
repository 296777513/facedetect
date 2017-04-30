package com.facedetect.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.facedetect.Constant.Constant;
import com.facedetect.Constant.DataSource;
import com.facedetect.sdk.FacePlusPlusHelper;
import com.facedetect.ui.CompareActivity;
//import com.umeng.message.IUmengRegisterCallback;
//import com.umeng.message.MsgConstant;
//import com.umeng.message.PushAgent;
//import com.umeng.message.UTrack;
//import com.umeng.message.UmengMessageHandler;
//import com.umeng.message.UmengNotificationClickHandler;
//import com.umeng.message.common.UmLog;
//import com.umeng.message.entity.UMessage;


/**
 * Created by l00385426 on 2016/11/10.
 */

public class MyApplication extends Application {
    private static Context mContext;
    private static final String TAG = MyApplication.class.getName();
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";

    @Override
    public void onCreate() {
        super.onCreate();


        AVOSCloud.initialize(this, Constant.CLOUD_APPLICATION_ID, Constant.CLOUD_APPLICATION_KEY);
        AVInstallation.getCurrentInstallation().saveInBackground();
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    Log.i("AVInstallation", "installationId: " + installationId);
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    Log.i("AVInstallation", "失败:" + e.toString());
                }
            }
        });
// 设置默认打开的 Activity
        PushService.setDefaultPushCallback(this, CompareActivity.class);


//        initUmeng();
        mContext = getApplicationContext();

//        DataSource.getInstance();
    }


//    public void initUmeng() {
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        //注册推送服务 每次调用register都会回调该接口
//
//        mPushAgent.setDebugMode(true);
//
//        //sdk开启通知声音
//        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
//        // sdk关闭通知声音
////		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//        // 通知声音由服务端控制
////		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
//
////		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
////		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//        Log.i(TAG, "dealWithCustomMessage: ");
//
//        UmengMessageHandler messageHandler = new UmengMessageHandler() {
//            /**
//             * 自定义消息的回调方法
//             * */
//            @Override
//            public void dealWithCustomMessage(final Context context, final UMessage msg) {
//                new Handler().post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        // 对自定义消息的处理方式，点击或者忽略
//                        boolean isClickOrDismissed = true;
//                        Log.i(TAG, "dealWithCustomMessage: " + msg.custom);
//                        if (isClickOrDismissed) {
//                            //自定义消息的点击统计
//                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//                        } else {
//                            //自定义消息的忽略统计
//                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
//                        }
//
//                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            /**
//             * 自定义通知栏样式的回调方法
//             * */
//            @Override
//            public Notification getNotification(Context context, UMessage msg) {
//                ImageId.imageIdStr = msg.extra.get(Constant.CLOUD_IMAGES);
//                Log.i(TAG, "getNotification: " + ImageId.imageIdStr);
//                switch (msg.builder_id) {
//                    case 1:
//                        Notification.Builder builder
//                                = new Notification.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewResource(R.id.notification_large_icon, getSmallIconId(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView)
//                                .setSmallIcon(getSmallIconId(context, msg))
//                                .setTicker(msg.ticker)
//                                .setAutoCancel(true);
//
//                        return builder.getNotification();
//                    default:
//                        //默认为0，若填写的builder_id并不存在，也使用默认。
//                        return super.getNotification(context, msg);
//                }
//            }
//        };
//        mPushAgent.setMessageHandler(messageHandler);
//
//        /**
//         * 自定义行为的回调处理
//         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
//         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
//         * */
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//            }
//        };
//        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
//        //参考http://bbs.umeng.com/thread-11112-1-1.html
//        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
//
//
//        mPushAgent.register(new IUmengRegisterCallback() {
//            @Override
//            public void onSuccess(String deviceToken) {
//                UmLog.i(TAG, "device token: " + deviceToken);
//
//
//                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//                UmLog.i(TAG, "register failed: " + s + " " + s1);
//                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
//
//            }
//        });
//
//        //此处是完全自定义处理设置
////        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
//    }

    public static Context getContextObject() {
        return mContext;
    }
}
