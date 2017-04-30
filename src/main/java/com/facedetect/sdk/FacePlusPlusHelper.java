package com.facedetect.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.facedetect.Bean.EventBean;
import com.facedetect.Constant.DataSource;
import com.facedetect.application.MyApplication;
import com.facedetect.utils.MyUtils;
import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

import de.greenrobot.event.EventBus;


/**
 * Created by Android Studio. author: liyachao Date: 15/11/7 Time: 14:32
 */
public class FacePlusPlusHelper {
    private static final String TAG = "FacePlusPlusHelper";
    /**
     * 这里最好换成自己的apikey和apiSecret
     */
    private static final String apiKey = "000feb0633099eb6167e7ad3270b5338";
    private static final String apiSecret = "HL9WV4BOrBJ-8LSgsyDAHXgJUtezgJxN";

    public static final int METHOD_ONE = 50001;
    public static final int METHOD_TWO = 50002;
    public static final int METHOD_THREE = 50003;

    private FaceDetecter detecter;
    private Context context;
    private Handler detectHandler;
    private HandlerThread detectThread;

    HttpRequests httpRequests;

    private static class FacePlusPlusHelperHolder {
        private static FacePlusPlusHelper facePlusPlusHelper = new FacePlusPlusHelper(MyApplication.getContextObject());
    }


    public static FacePlusPlusHelper getInstance() {
        return FacePlusPlusHelperHolder.facePlusPlusHelper;
    }

    private FacePlusPlusHelper(Context context) {
        this.context = context;

        detectThread = new HandlerThread("detect");
        detectThread.start();
        detectHandler = new Handler(detectThread.getLooper());

        httpRequests = new HttpRequests(apiKey, apiSecret, true, false);
        httpRequests.setHttpTimeOut(60000);
        if (!MyUtils.isCPUInfo64()) {
            detecter = new FaceDetecter();
            detecter.init(context, apiKey);
        }

    }

    public void stopThread() {
        detectHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 探测选取的图片是否有脸
     */
    public void faceDetect(final String url) {
        final EventBean bean = new EventBean();
        detectHandler.post(new Runnable() {
            @Override
            public void run() {
//                if (detecter == null) {
                try {
                    JSONObject rst;
                    PostParameters parameters = new PostParameters();
                    if (url.startsWith("http")) {
                        parameters.setUrl(url);
                    } else {
                        Bitmap bitmap = MyUtils.getScaledBitmap(url);
                        byte[] bytes = MyUtils.bitmapToBytes(bitmap);
                        parameters.setImg(bytes);
                    }
                    rst = httpRequests.detectionDetect(parameters);

                    try {
                        int count = rst.getJSONArray("face").length();
                        if (count == 0) {
                            bean.setIsCorrect(false);
                            bean.setMode(METHOD_ONE);
                            bean.setDescribe("没有探测到脸,请再次选择或者拍摄");
                            EventBus.getDefault().post(bean);
                        } else {
                            float x = (float) rst.getJSONArray("face").getJSONObject(0)
                                    .getJSONObject("position").getJSONObject("center").getDouble("x");
                            float y = (float) rst.getJSONArray("face").getJSONObject(0)
                                    .getJSONObject("position").getJSONObject("center").getDouble("y");
                            //get face size
                            float w = (float) rst.getJSONArray("face").getJSONObject(0)
                                    .getJSONObject("position").getDouble("width") * 0.5f;
                            float h = (float) rst.getJSONArray("face").getJSONObject(0)
                                    .getJSONObject("position").getDouble("height") * 0.5f;
                            String detectFaceId = rst.getJSONArray("face").getJSONObject(0)
                                    .getString("face_id");

                            Face face = new Face();
                            face.left = (x - w) / 100f;
                            face.right = (x + w) / 100f;
                            face.top = (y - h) / 100f;
                            face.bottom = (y + h) / 100f;
                            bean.setIsCorrect(true);
                            bean.setFace(face);
                            bean.setMode(METHOD_ONE);

                            bean.setFaceId(detectFaceId);

                            Log.i("AVInstallation", "url1: " + url);
                            if (url.startsWith("http")) {
                                bean.setImage(url);
                                EventBus.getDefault().post(bean);
                            } else {
                                try {
                                    final AVFile avFile = AVFile.withAbsoluteLocalPath(url, url);
                                    avFile.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            Log.i("AVInstallation", "e: " + (e != null ? e.toString() : null));
                                            bean.setImage(avFile.getUrl());

                                            Log.i("AVInstallation", "url2: " + bean.getImage());

                                            EventBus.getDefault().post(bean);
                                        }
                                    });
                                } catch (FileNotFoundException e) {
                                    Log.i("AVInstallation", "url2: fail");
                                    e.printStackTrace();
                                }
                            }


                        }
                    } catch (JSONException e) {
                        bean.setIsCorrect(false);
                        bean.setMode(METHOD_ONE);
                        bean.setDescribe("探测失败，请重新比对");
                        EventBus.getDefault().post(bean);
                        e.printStackTrace();
                    }
                } catch (FaceppParseException e) {
                    bean.setIsCorrect(false);
                    bean.setMode(METHOD_ONE);
                    bean.setDescribe("探测失败，请重新比对");
                    EventBus.getDefault().post(bean);
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 得到对比图片的faceid
     *
     * @param bitmap 需要对比的图片
     */
    public void getFaceId(final Bitmap bitmap) {
        final EventBean bean = new EventBean();
        detectHandler.post(new Runnable() {
            @Override
            public void run() {

                byte[] bytes = MyUtils.bitmapToBytes(bitmap);
                try {
                    PostParameters parameters = new PostParameters();
                    parameters.setImg(bytes);
                    JSONObject rst = httpRequests
                            .detectionDetect(new PostParameters().setImg(bytes));
                    Log.i(TAG, "getFaceId: " + rst.toString());
                    int count = rst.getJSONArray("face").length();
                    if (count == 0) {
                        bean.setIsCorrect(false);
                        bean.setMode(METHOD_TWO);
                        EventBus.getDefault().post(bean);
                    } else {
                        String detectFaceId = rst.getJSONArray("face").getJSONObject(0)
                                .getString("face_id");
//                        faceSetAdd(detectFaceId);

                        bean.setIsCorrect(true);
                        bean.setFaceId(detectFaceId);
                        bean.setMode(METHOD_TWO);
                        EventBus.getDefault().post(bean);
                    }
                } catch (Exception e) {
                    bean.setIsCorrect(false);
                    bean.setMode(METHOD_TWO);
                    bean.setDescribe(e.toString());
                    EventBus.getDefault().post(bean);
                    e.printStackTrace();
                }
            }
        });
    }

    public void getFaceId(final String url) {
        final EventBean bean = new EventBean();
        detectHandler.post(new Runnable() {
            @Override
            public void run() {
                PostParameters parameters = new PostParameters();
                try {
                    JSONObject rst;
                    if (url.startsWith("http")) {
                        parameters.setUrl(url);
                        rst = httpRequests.detectionDetect(parameters);
                    } else {
                        Bitmap bitmap = MyUtils.getScaledBitmap(url);
                        byte[] bytes = MyUtils.bitmapToBytes(bitmap);
                        parameters.setImg(bytes);
                        rst = httpRequests.detectionDetect(parameters);
                    }

                    try {
                        int count = rst.getJSONArray("face").length();
                        if (count == 0) {
                            bean.setIsCorrect(false);
                            bean.setMode(METHOD_THREE);
                            bean.setDescribe("没有探测到脸,请重新拍照");
                            EventBus.getDefault().post(bean);
                        } else {
                            String faceId2 = rst.getJSONArray("face").getJSONObject(0)
                                    .getString("face_id");
                            for (EventBean bean : DataSource.getInstance().getbeans()) {
//                                if (bean.getUrl().equals(url)) {
//                                    bean.setFaceId(faceId2);
//                                    Log.i("liyachao3", "faceId2: " + faceId2);
//                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (FaceppParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 对比两个图片中脸部的相似度
     *
     * @param faceId1 已经得到的Faceid
     */
    public void comparisonFace(final String faceId1, final String url, final String faceId2) {
        final EventBean bean = new EventBean();
        detectHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String comFaceId = faceId2;

                    long timeStart = System.currentTimeMillis();
                    JSONObject rst;
                    PostParameters parameters = new PostParameters();
                    Log.i("liyachao3", "faceId1: " + faceId1 + "\n faceId2: " + faceId2);
                    if (comFaceId == null || comFaceId.length() == 0) {
                        if (url.startsWith("http")) {
                            parameters.setUrl(url);
                            rst = httpRequests.detectionDetect(parameters);
                        } else {
                            Bitmap bitmap = MyUtils.getScaledBitmap(url);
                            byte[] bytes = MyUtils.bitmapToBytes(bitmap);
                            parameters.setImg(bytes);
                            rst = httpRequests.detectionDetect(parameters);
                        }

                        int count = rst.getJSONArray("face").length();
                        if (count == 0) {
                            bean.setIsCorrect(false);
                            bean.setMode(METHOD_THREE);
                            bean.setDescribe("没有探测到脸,请重新拍照");
                            EventBus.getDefault().post(bean);
                        } else {
                            comFaceId = rst.getJSONArray("face").getJSONObject(0)
                                    .getString("face_id");
                            Log.i(TAG, "comparisonFace: " + rst.toString());
                        }
                    }
                    parameters = new PostParameters();
                    parameters.addAttribute("face_id1", faceId1);
                    parameters.addAttribute("face_id2", comFaceId);
                    JSONObject result = httpRequests
                            .recognitionCompare(parameters);
                    if (result == null || result.toString().equals("")) {
                        bean.setIsCorrect(false);
                        bean.setMode(METHOD_THREE);
                        EventBus.getDefault().post(bean);
                    } else {
                        double similarity = result.getDouble("similarity");
                        long timeEnd = System.currentTimeMillis();
                        if (timeEnd - timeStart < 2000) {
                            Thread.sleep(2000 - (timeEnd - timeStart));
                        }
                        bean.setIsCorrect(true);
                        bean.setDescribe(similarity + "");
                        bean.setMode(METHOD_THREE);
                        EventBus.getDefault().post(bean);
                    }

                } catch (Exception e) {
                    bean.setIsCorrect(false);
                    bean.setMode(METHOD_THREE);
                    bean.setDescribe("比对失败");
                    EventBus.getDefault().post(bean);
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 释放引擎
     */
    public void release() {
        if (detecter != null)
            detecter.release(context);
    }

}
