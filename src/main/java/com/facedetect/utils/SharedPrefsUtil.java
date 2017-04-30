package com.facedetect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.facedetect.Bean.EventBean;
import com.facedetect.application.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by l00385426 on 2016/11/14.
 */

public class SharedPrefsUtil {
    public final static String SHARED_PREFERENCES_NAME = "hw_share_preference_name";
    SharedPreferences.Editor spEditor;
    SharedPreferences sp;


    private SharedPrefsUtil() {
        spEditor = MyApplication.getContextObject().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        sp = MyApplication.getContextObject().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private static class SharedPrefsUtilHolder {
        private static SharedPrefsUtil mSharedPrefsUtil = new SharedPrefsUtil();
    }

    public static SharedPrefsUtil getInstance() {
        return SharedPrefsUtilHolder.mSharedPrefsUtil;
    }

    public void putValue(String key, int value) {

        spEditor.putInt(key, value);
        spEditor.commit();
    }

    public void putValue(String key, boolean value) {
        spEditor.putBoolean(key, value);
        spEditor.commit();
    }

    public void putValue(String key, String value) {
        spEditor.putString(key, value);
        spEditor.commit();
    }

    public int getValue(String key, int defValue) {

        return sp.getInt(key, defValue);
    }

    public boolean getValue(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public String getValue(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /**
     * 保存List
     *
     * @param key
     * @param datalist
     */
    public <T> void putValueList(String key, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        //转换成json数据，再保存
        String strJson = JSON.toJSONString(datalist);
        putValue(key, strJson);
    }

    public  List<EventBean> getEventBeanValueList(String key, String defValue) {
        List<EventBean> datalist = new ArrayList<EventBean>();
        String strJson = getValue(key, defValue);
        if (null == strJson) {
            return datalist;
        }
        datalist =  JSON.parseArray(strJson,EventBean.class);
        return datalist;

    }

    public  List<Bitmap> getBitmapValueList(String key, String defValue) {
        List<Bitmap> datalist = new ArrayList<Bitmap>();
        String strJson = getValue(key, defValue);
        if (null == strJson) {
            return datalist;
        }
        datalist =  JSON.parseArray(strJson,Bitmap.class);
        return datalist;

    }

    public <T> List<T> getValueList(String key, String defValue) {
        List<T> datalist = new ArrayList<T>();
        String strJson = getValue(key, defValue);
        if (null == strJson) {
            return datalist;
        }
        datalist = (List<T>) JSON.parseArray(strJson);
        return datalist;

    }
}
