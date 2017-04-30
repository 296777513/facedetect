　　由于个人的兴趣，自己写了一个人脸识别、对比的demo，这个demo中用到的人脸识别不是Android SDK中自带的FaceDetector而是使用的Face++提供的人脸识别和人脸探测的API，这个demo不仅实现了人脸探测和人脸对比，而且实现的效果是比较绚丽的，先给大家看一下效果图。
　　
　　![这里写图片描述](http://img.blog.csdn.net/20151129133206277)



一、基础知识
------

　　Face++人脸识别的三个核心概念：Image，Face和FaceId。
　　
　　Image指用户或应用程序给Face++API提供的图片，以供后续检测/识别使用。用户可以通过制定的url或在程序中上传（通过HTTP POST提交图片的二进制文件）提供Image。

　　Face指Image中检测出得人脸。一张Image中可能包含多个Face，但是在我写的Demo中只用到一个Face。
　　FaceId指上传到Face++服务器后，服务器返回的人脸标示符，在后面的对比中可以直接使用FaceId进行对比。

　　属性动画这里使用的第三方的库：NineOldAndroids。NineOldAndroids是一个乡下兼容的动画库，主要的使低于API 11的系统也能够使用View的属性动画。以下是官网的描述：

> Android library for using the Honeycomb (Android 3.0) animation API on all versions of the platform back to 1.0!Animation prior to Honeycomb was very limited in what it could accomplish so in Android 3.x a new API was written. With only a change in imports, we are able to use a large subset of the new-style animation with exactly the same API.

　　View的属性动画在Android API 11及其以后才支持，该库的作用就是让API 11以下的系统也能够正常的使用属性动画。它的雷鸣、用法与官方都一样，只是包名不一样。使用该库，你就可以在API版本很低的情况下也能够使用各种属性动画，让你的应用更加有动感、平滑。官方地址：[NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)。

　　EventBus指的是一个发布/订阅的时间总线，EventBus包含4个成分：发布者，订阅者，事件，总线。4者之间的关系大体如下：

![这里写图片描述](http://img.blog.csdn.net/20151129133254851)

　　订阅者可以订阅多个事件，发送者可以发布任何事件，发布者同时也可以是订阅者。使用EventBus的优点是：独立出一个发布订阅模块，调用者可以通过使用这个模块，屏蔽一些线程切换的问题，简单地实现发布订阅功能。另个一优点是解耦合，是类之间的耦合降低。
　　xUtils的BitmapUtils，它替我们考虑所有图片加载的情况，大体来说就是使用了三级缓存来加载网络图片和本地图片，让我们的程序不会因为图片造成内存溢出的BUG。

二、人脸识别、比对的流程
------------

ps：四张作为对比样本的图片，是从网络上抓取下来的，当然也可以是从本地去拿，这里偷懒了，没有做。
1、首先选取一张照片（从手机相册或者拍摄得到），然后点击开始比对。
2、使用FaceDetecter（线上或者离线）API分析bitmap，将探测到的人脸存储到Face中。
3、根据Face中的数据，将人脸部分提取出来，然后逐一跟之前的四张图片进行对比。
4、将对比的结果显示出来。

三、项目讲解
------

1、项目的机构


虽然结构稍微有点复杂，但是很清晰，有四个包，分别是：适配器、Event数据类型、activity、使用到的一些工具以及一些自定义的view（主要是用来对图片进行动画时使用到的）。

2、主要代码分析
因为本篇主要讲解的是人脸识别，那FacePlusPlusHelper肯定是最重要的，我把Face++的API中的几个我使用的方法封装的这个类中。

```
/**
 * Created by Android Studio. author: liyachao Date: 15/11/7 Time: 14:32
 */
public class FacePlusPlusHelper {

    /**
     * 这里最好换成自己的apikey和apiSecret
     */
    private static final String apiKey = "000feb0633099eb6167e7ad3270b5338";
    private static final String apiSecret = "pB_jtq-piOg9euRbQysSP65FlfcyGMmn";

    public static final int METHOD_ONE = 50001;
    public static final int METHOD_TWO = 50002;
    public static final int METHOD_THREE = 50003;

    private FaceDetecter detecter;
    private Context context;
    private Handler detectHandler;
    private HandlerThread detectThread;

    private EventBean bean;

    public FacePlusPlusHelper(Context context) {
        this.context = context;
        detectThread = new HandlerThread("detect");
        detectThread.start();
        detectHandler = new Handler(detectThread.getLooper());
        bean = new EventBean();
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
     *
     * @param bitmap
     */
    public void faceDetect(final Bitmap bitmap) {
        detectHandler.post(new Runnable() {
            @Override
            public void run() {
                if (detecter == null) {
                    HttpRequests httpRequests = new HttpRequests(apiKey, apiSecret, true, false);
                    byte[] bytes = MyUtils.bitmapToBytes(bitmap);
                    PostParameters parameters = new PostParameters();
                    parameters.setImg(bytes);
                    try {
                        JSONObject rst = httpRequests
                                .detectionDetect(new PostParameters().setImg(bytes));
                        int count = rst.getJSONArray("face").length();
                        if (count == 0) {
                            bean.setIsCorrect(false);
                            bean.setMode(METHOD_ONE);
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

                            Face face = new Face();
                            face.left = (x - w) / 100f;
                            face.right = (x + w) / 100f;
                            face.top = (y - h) / 100f;
                            face.bottom = (y + h) / 100f;
                            bean.setIsCorrect(true);
                            bean.setFace(face);
                            bean.setMode(METHOD_ONE);
                            EventBus.getDefault().post(bean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("double", "bitmap is null11: " + (detecter.findFaces(bitmap) == null));
                    final Face[] faceinfo = detecter.findFaces(bitmap);// 进行人脸检测
                    if (faceinfo == null) {
                        bean.setIsCorrect(false);
                        bean.setMode(METHOD_ONE);
                        EventBus.getDefault().post(bean);
                    } else {
                        bean.setIsCorrect(true);
                        bean.setFace(faceinfo[0]);
                        bean.setMode(METHOD_ONE);
                        EventBus.getDefault().post(bean);
                    }
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
        detectHandler.post(new Runnable() {
            @Override
            public void run() {
                HttpRequests httpRequests = new HttpRequests(apiKey, apiSecret, true, false);
                byte[] bytes = MyUtils.bitmapToBytes(bitmap);
                try {
                    PostParameters parameters = new PostParameters();
                    parameters.setImg(bytes);
                    JSONObject rst = httpRequests
                            .detectionDetect(new PostParameters().setImg(bytes));
                    Log.i("double", "getFaceId: " + rst.toString());
                    int count = rst.getJSONArray("face").length();
                    if (count == 0) {
                        bean.setIsCorrect(false);
                        bean.setMode(METHOD_TWO);
                        EventBus.getDefault().post(bean);
                    } else {
                        String detectFaceId = rst.getJSONArray("face").getJSONObject(0)
                                .getString("face_id");
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

    /**
     * 对比两个图片中脸部的相似度
     *
     * @param faceId1 已经得到的Faceid
     * @param url     需要对比的url
     */
    public void comparisonFace(final String faceId1, final String url) {
        detectHandler.post(new Runnable() {
            @Override
            public void run() {
                HttpRequests httpRequests = new HttpRequests(apiKey, apiSecret, true, false);
                try {
                    long timeStart = System.currentTimeMillis();
                    JSONObject rst = httpRequests.detectionDetect(new PostParameters()
                            .setUrl(url));
                    int count = rst.getJSONArray("face").length();
                    if (count == 0) {
                        bean.setIsCorrect(false);
                        bean.setMode(METHOD_THREE);
                        bean.setDescribe("没有探测到脸");
                        EventBus.getDefault().post(bean);

                    } else {
                        String faceId2 = rst.getJSONArray("face").getJSONObject(0)
                                .getString("face_id");
                        PostParameters parameters = new PostParameters();
                        parameters.addAttribute("face_id1", faceId1);
                        parameters.addAttribute("face_id2", faceId2);
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
                    }
                } catch (Exception e) {
                    bean.setIsCorrect(false);
                    bean.setMode(METHOD_THREE);
                    bean.setDescribe(e.toString());
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
```

　　首先提取一些需要用到的常量，因为整个类中主要有三个方法：探测脸部，请求FaceId，脸部对比，所以需要用三种类型代表不同的方法，方便在EventBus中做区分。
　　22~23行代码初始化了一个线程，用来请求网络（因为请求网络是耗时的操作，因此需要开启线程，不能在UI线程中直接请求，否则会造成程序崩溃），初始化了FaceDetecter（Face++的离线包），大家可以看到我这里做了一个手机CPU是否为64位的判断，因为Face++离线包中没有做64位的so，所以如果手机64位的还直接调用Face++的离线包，会有Bug。如果手机是64位，则直接请求网络来探测脸部。
　　剩下的三个方法都在程序中做了注释，大家应该可以看懂。
　　大家可以看到，在请求网络成功时，我这里没有使用到回调，而是使用的EventBus，和回调是一个效果。
　　剩下的代码，就不一一讲解了，项目中的代码不难，如果感兴趣的朋友可以和我讨论。

四、总结
----
　　这个Demo中运用到的知识点还是比较多的，很多地方都没有讲到，不过我相信对于大家来说，都应该能够看懂，如果有什么不清楚的地方，可以留言，互相讨论。
　　如果其中有什么不对或者不足的地方，希望大家能提出来。