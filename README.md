
# 简介

　　这个项目是在2016年华为研究所举办的第一届黑客马拉松比赛时开发的项目，这个项目是借鉴之前写[Android人脸对比](http://blog.csdn.net/a296777513/article/details/50095719 ，想出一个关于防止儿童走失的想法，然后增加浏览历史和定位失踪儿童以及推送附近人的功能。
　　其实整个项目并不难，并且也很粗糙，但是由于整个项目从开始到结束都是我一个人开发，尤其到最后决赛的极限4小时编程，那种紧张、压抑的气氛充斥着整个赛场，还好在最后一刻完成了发布的任务，在最后的决赛中获得了第二的成绩（与第一名仅差了0.3分），对于这个成绩我还是比较满意的，在这里我要感谢一下我的其他两位队友（下面的效果图就是其中一位），都是北大的才子。

　　首先看一下效果图，第一个gif是发送丢失信息的功能演示；第二个gif是接收到推送，进行比对功能的演示。

![这里写图片描述](http://img.blog.csdn.net/20170501000438447?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTI5Njc3NzUxMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![这里写图片描述](http://img.blog.csdn.net/20170501000505981?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTI5Njc3NzUxMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

# 一、基础知识
------

在这个项目中使用到了很多第三方的SDK，现在市场上有很多免费的第三方工具类SDK供大家使用，很多比较难使用的，需要配置很多参数，开发代码特别多，但是功能还不能保证。这次开发中在选择第三方工具类上就花费了很长的时间，综合评定后决定使用下面这几个第三方的SDK：

* [高德定位](http://lbs.amap.com/api/android-location-sdk/locationsummary/)
* [LeanCloud推送](https://leancloud.cn/docs/android_push_guide.html)
* [Face++人脸比对](https://console.faceplusplus.com.cn/documents/4887579)




## Face++
　　Face++人脸识别的三个核心概念：Image，Face和FaceId。
　　
　　Image指用户或应用程序给Face++API提供的图片，以供后续检测/识别使用。用户可以通过制定的url或在程序中上传（通过HTTP POST提交图片的二进制文件）提供Image。

　　Face指Image中检测出得人脸。一张Image中可能包含多个Face，但是在我写的Demo中只用到一个Face。
　　FaceId指上传到Face++服务器后，服务器返回的人脸标示符，在后面的对比中可以直接使用FaceId进行对比。

##　定位
　　这里使用的高德定位的API，Android 定位 SDK 是一套简单的LBS服务定位接口，您可以使用这套定位API获取定位结果、逆地理编码（地址文字描述）、以及地理围栏功能。
　　下面是简单的代码示例，具体的功能以及参数请参考官网。
```java

//声明mLocationOption对象
public AMapLocationClientOption mLocationOption = null;
mlocationClient = new AMapLocationClient(this);
//初始化定位参数
mLocationOption = new AMapLocationClientOption();
//设置定位监听
mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
mLocationOption.setInterval(2000);
//设置定位参数
mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
mlocationClient.startLocation();
@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
        if (amapLocation.getErrorCode() == 0) {
        //定位成功回调信息，设置相关消息
        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        amapLocation.getLatitude();//获取纬度
        amapLocation.getLongitude();//获取经度
        amapLocation.getAccuracy();//获取精度信息
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(amapLocation.getTime());
        df.format(date);//定位时间
    } else {
              //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
        Log.e("AmapError","location Error, ErrCode:"
            + amapLocation.getErrorCode() + ", errInfo:"
            + amapLocation.getErrorInfo());
        }
    }
	}
```

## 推送
　　在这里我必须吐槽一下**友盟推送**和**Bmob推送**，这两个对于开发者来说太不友善了，使用起来太麻烦，而且还有很多bug。
　　这里使用的推送是LeanCloud开发推送功能，总体而言还是比较简单的。开发文档清晰简洁，链接上面已经给出了。

## 属性动画
　　属性动画这里使用的第三方的库：NineOldAndroids。NineOldAndroids是一个乡下兼容的动画库，主要的使低于API 11的系统也能够使用View的属性动画。以下是官网的描述：

> Android library for using the Honeycomb (Android 3.0) animation API on all versions of the platform back to 1.0!Animation prior to Honeycomb was very limited in what it could accomplish so in Android 3.x a new API was written. With only a change in imports, we are able to use a large subset of the new-style animation with exactly the same API.

　　View的属性动画在Android API 11及其以后才支持，该库的作用就是让API 11以下的系统也能够正常的使用属性动画。它的雷鸣、用法与官方都一样，只是包名不一样。使用该库，你就可以在API版本很低的情况下也能够使用各种属性动画，让你的应用更加有动感、平滑。官方地址：[NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)。

## EventBus
　　[EventBus](https://github.com/greenrobot/EventBus)指的是一个发布/订阅的时间总线，EventBus包含4个成分：发布者，订阅者，事件，总线。4者之间的关系大体如下：

![这里写图片描述](http://img.blog.csdn.net/20151129133254851)

　　订阅者可以订阅多个事件，发送者可以发布任何事件，发布者同时也可以是订阅者。使用EventBus的优点是：独立出一个发布订阅模块，调用者可以通过使用这个模块，屏蔽一些线程切换的问题，简单地实现发布订阅功能。另个一优点是解耦合，是类之间的耦合降低。


　　xUtils的BitmapUtils，它替我们考虑所有图片加载的情况，大体来说就是使用了三级缓存来加载网络图片和本地图片，让我们的程序不会因为图片造成内存溢出的BUG。

# 二、项目流程
------------
　　整个项目是分为两个大的功能部分：**寻找家人**和**我是志愿者**。

## 寻找家人

　　这个模块主要是发送丢失儿童的信息，使用到推送和定位，整个发送的步骤主要是以下几步：
　　1、上传丢失儿童的照片信息。
　　2、选择是否需要将此条信息推送到周围人。
　　3、进入到推送成功的页面。

## 志愿者

　　这个模块也是整个项目的核心部分**人脸对比**，下面是对比的具体步骤：

　　ps：四张作为对比样本的图片，是从网络下载的。
　　1、首先选取一张照片（从手机相册或者拍摄得到），然后点击开始比对。
　　2、使用FaceDetecter（线上或者离线）API分析bitmap，将探测到的人脸存储到Face中。
　　3、根据Face中的数据，将人脸部分提取出来，然后逐一跟之前的四张图片进行对比。
　　4、将对比的结果显示出来。

# 三、要代码分析
------

## 主要代码分析

因为整个项目都是围绕着人脸识别展开的，那FacePlusPlusHelper肯定是最重要的，我把Face++的API中的几个我使用的方法封装的这个类中。

```
/**
 * Created by Android Studio. author: liyachao Date: 2016/11/7 Time: 14:32
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


}
```

　　首先提取一些需要用到的常量，因为整个类中主要有三个方法：探测脸部，请求FaceId，脸部对比，所以需要用三种类型代表不同的方法，方便在EventBus中做区分。
　　代码初始化了一个线程，用来请求网络（因为请求网络是耗时的操作，因此需要开启线程，不能在UI线程中直接请求，否则会造成程序崩溃），初始化了FaceDetecter（Face++的离线包），大家可以看到我这里做了一个手机CPU是否为64位的判断，因为Face++离线包中没有做64位的so，所以如果手机64位的还直接调用Face++的离线包，会有Bug。如果手机是64位，则直接请求网络来探测脸部。
　　剩下的方法都在程序中做了注释，大家应该可以看懂。
　　大家可以看到，在请求网络成功时，我这里没有使用到回调，而是使用的EventBus，和回调是一个效果。

　　剩下的代码，就不一一讲解了，项目中的代码不难，如果感兴趣的朋友可以和我讨论。

四、总结
----
　　这个项目中运用到的知识点还是比较多的，很多地方都没有讲到，不过我相信对于大家来说，都应该能够看懂，如果有什么不清楚的地方，可以留言，互相讨论。
　　如果其中有什么不对或者不足的地方，希望大家能提出来。
