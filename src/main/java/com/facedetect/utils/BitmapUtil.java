package com.facedetect.utils;

import android.graphics.Bitmap;

import com.facedetect.application.MyApplication;
import com.lidroid.xutils.BitmapUtils;

/**
 * Created by l00385426 on 2016/11/18.
 */

public class BitmapUtil {


    private static class BitmapUtilsHolder {
        static BitmapUtils bitmapUtils = new BitmapUtils(MyApplication.getContextObject());
    }

    public static BitmapUtils getInstance() {
        BitmapUtils bitmapUtils = BitmapUtilsHolder.bitmapUtils;
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        bitmapUtils.configDefaultBitmapMaxSize(300, 300);
        return bitmapUtils;
    }
}
