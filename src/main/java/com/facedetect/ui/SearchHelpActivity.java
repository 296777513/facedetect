package com.facedetect.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.SendCallback;
import com.facedetect.Bean.EventBean;
import com.facedetect.Constant.Constant;
import com.facedetect.Constant.DataSource;
import com.facedetect.R;
import com.facedetect.utils.MyUtils;
import com.facedetect.view.AnimationCircleView;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by l00385426 on 2016/11/18.
 */

public class SearchHelpActivity extends BaseActivity implements View.OnClickListener, LocationSource,
        AMapLocationListener {

    private AnimationCircleView animationCircleView;
    private ImageView imageView;
    private AMap aMap;
    private MapView mapView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    //声明mLocationOption对象
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_help);
        initTitle();
        btnBack.setOnClickListener(this);
        titleName.setText("发送推送");
        mapView = getView(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        animationCircleView = getView(R.id.search_help_view1);
        animationCircleView.startMyAnimation();
        imageView = getView(R.id.search_help_light);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0, 360);
        imageView.setPivotX(MyUtils.dip2px(this, 60));
        imageView.setPivotY(MyUtils.dip2px(this, 60));
        animator.setDuration(2000);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(Constant.ACCESS_COARSE_LOCATION_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            writeSDCardPermission();
        }
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        toast = Toast.makeText(this, "您的求助信息已经发送给周围2公里范围内人群，如果有线索，发现者会和您联系,您可以返回上一个页面查看历史", Toast.LENGTH_LONG);
        showMyToast(toast, 10000);


    }

    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        etupLocationStyle();
    }

    private void etupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(null);
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
        toast.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_title_layout:
                finish();
                break;
        }
    }

    private void sendPush() {
        if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            requestPermission(Constant.READ_PHONE_CODE,
                    Manifest.permission.READ_PHONE_STATE);
        } else {
            readPhoneNumber();
        }
    }

    @Override
    public void readPhoneNumber() {
        super.readPhoneNumber();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = tm.getLine1Number();
        AVPush push = new AVPush();
        JSONObject object = new JSONObject();
        try {
            object.put("alert", "您附近有孩子或者老人走失，请帮助他");
            object.put(Constant.CLOUD_PHONE_NUMBER, phoneNum);
            object.put(Constant.CLOUD_ADDRESS, mAddrress);
//            object.put("action","com.facedetect.UPDATE_STATUS");

            List<EventBean> beans = DataSource.getInstance().getBeansFromUrl();
            for (int i = 0; i < beans.size(); i++) {
                object.put(Constant.CLOUD_IMAGES_STRING + i,
                        beans.get(i).getImage());
                Log.i("AVInstallation", i + ": " + beans.get(i).getImage());
            }
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

    boolean isFirst = true;
    String mAddrress = null;

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                if (isFirst) {
                    mAddrress = amapLocation.getAddress();
                    isFirst = false;
                    sendPush();
                }
                amapLocation.getAddress();
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

}
