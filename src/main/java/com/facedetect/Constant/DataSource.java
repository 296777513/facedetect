package com.facedetect.Constant;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.facedetect.Bean.EventBean;
import com.facedetect.application.MyApplication;
import com.facedetect.utils.MyUtils;
import com.facedetect.utils.SharedPrefsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by l00385426 on 2016/11/10.
 */
public class DataSource {

    public static final String DATA_SOURCE_IMAGES = "dataSourceImages";
    public static final String DATA_SOURCE_EVENT_BEANS = "dataSourceEventBeans";
    public static final String DATA_SOURCE_COMPARE_RESULT = "dataSourceCompareResult";
    public static final String DATA_SOURCE_UPLOAD_PIC_HISTORY = "dataSourceUploadPicHistory";
    public static final String DATA_SOURCE_FACE_ID = "dataSourceFaceId";

    private List<String> images;
    private List<EventBean> eventBeans;
    private List<EventBean> compareResult;
    private List<EventBean> fromOtherDevices;
    private List<String> uploadPicHistory;

    private static class SingletonHolder {
        private static DataSource singleton = new DataSource();
    }

    public static DataSource getInstance() {
        return SingletonHolder.singleton;
    }

    /**
     * http://ac-e1JzCyDc.clouddn.com/wFdcXlu903AzzM4bH35GDnxvWHUDl6I9YLFnTr0d
     * http://ac-e1JzCyDc.clouddn.com/HCPQu5UqVaK8hk6iXYhlTvYMuKOgIk3T0SWND2RU
     * http://ac-e1JzCyDc.clouddn.com/l7XnOYsEnwHvW3V6JWAULEcjBvh5vMJraNqnbamh
     * http://ac-e1JzCyDc.clouddn.com/iqZ76VZF3zkP556A1a4dvn39itYOWYzgXJLQEkGF
     */
    private DataSource() {
        compareResult = new ArrayList<>();
        fromOtherDevices = new ArrayList<>();

        uploadPicHistory = SharedPrefsUtil.getInstance().getValueList(DATA_SOURCE_UPLOAD_PIC_HISTORY, null);
        images = new ArrayList<>();
        if (uploadPicHistory == null || uploadPicHistory.size() == 0) {
            uploadPicHistory = new ArrayList<>();
        }

        images = SharedPrefsUtil.getInstance().getValueList(DATA_SOURCE_IMAGES, null);
        if (images == null || images.size() == 0) {
            images = new ArrayList<>();
            images.add("http://ac-e1JzCyDc.clouddn.com/wFdcXlu903AzzM4bH35GDnxvWHUDl6I9YLFnTr0d");
            images.add("http://ac-e1JzCyDc.clouddn.com/HCPQu5UqVaK8hk6iXYhlTvYMuKOgIk3T0SWND2RU");
            images.add("http://ac-e1JzCyDc.clouddn.com/l7XnOYsEnwHvW3V6JWAULEcjBvh5vMJraNqnbamh");
            images.add("http://ac-e1JzCyDc.clouddn.com/iqZ76VZF3zkP556A1a4dvn39itYOWYzgXJLQEkGF");
        }

        eventBeans = new ArrayList<>();
        for (String url : images) {
            EventBean bean = new EventBean();
            bean.setImage(url);
            eventBeans.add(bean);
        }
    }

    public List<EventBean> getbeans() {
        return eventBeans;
    }


    public List<EventBean> getBeansFromUrl() {
        List<EventBean> beans = new ArrayList<>();
        for (String url : images) {
            EventBean bean = new EventBean();
            bean.setImage(url);
            beans.add(bean);
        }
        return beans;
    }

    public List<String> getImages() {
        return images;
    }

    public void addBeans(EventBean bean) {
        eventBeans.add(0, bean);
        eventBeans.remove(eventBeans.size() - 1);
    }

    public void addUlr(String url) {
        images.add(0, url);
        images.remove(images.size() - 1);

        SharedPrefsUtil.getInstance().putValueList(DATA_SOURCE_IMAGES, images);
    }


    public void addCompareResult(List<EventBean> result) {
        compareResult.addAll(0, result);

        if (compareResult.size() > 20) {
            for (int i = 0; i < 5; i++) {
                compareResult.remove(compareResult.size() - 1 - i);
            }
        }
    }

    public void addUploadHistory(String url) {
        uploadPicHistory.add(0, url);
        images.add(0, url);
        images.remove(images.size() - 1);
        SharedPrefsUtil.getInstance().putValueList(DATA_SOURCE_UPLOAD_PIC_HISTORY, uploadPicHistory);
        SharedPrefsUtil.getInstance().putValueList(DATA_SOURCE_IMAGES, images);
    }

    public void addFromOtherDevice(List<EventBean> result) {
        Log.i("liyachao55", "addFromOtherDevice");
        fromOtherDevices.addAll(result);
    }

    public List<EventBean> getFromOtherDevices() {
        Log.i("liyachao55", "getFromOtherDevices");
        return fromOtherDevices;
    }


    public List<EventBean> getCompareResult() {
        return compareResult;
    }


    public List<String> getUploadPicHistory() {
        return uploadPicHistory;
    }

}
