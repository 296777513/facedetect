package com.facedetect.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facedetect.Bean.EventBean;
import com.facedetect.Constant.Constant;
import com.facedetect.Constant.DataSource;
import com.facedetect.Constant.ImageId;
import com.facedetect.R;
import com.facedetect.adapter.FourPicGridAdapter;
import com.facedetect.sdk.FacePlusPlusHelper;
import com.facedetect.utils.BitmapUtil;
import com.facedetect.utils.MyUtils;
import com.facedetect.view.FacePlusImageView;
import com.facedetect.view.FrameImageView;
import com.lidroid.xutils.BitmapUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Android Studio. author: liyachao Date: 16/11/10 Time: 16:05
 */
public class DetectComparisonActivity extends BaseActivity implements View.OnClickListener {
    private FacePlusPlusHelper facePlusPlusHelper;
    private View light;
    private FacePlusImageView background;
    //    private BlurringView blurringView;
    private Intent intent;
    private String imagePath;
    //    private BitmapUtils bitmapUtils;
    private ObjectAnimator animation;
    private Context context;
    private FrameImageView imageView;
    private RelativeLayout compaLayout;
    private ImageView comparisonPic;
    private View blackBg;
    private List<EventBean> urlList;
    private double result = 0d;
    private String faceId;
    private int comparisonIndex = 0, count = 0;
    private TextView comparisonText;
    private Button btn1, btn2;
    private Bitmap myBitmap;
    private boolean aniIsFinished = false;
    private GridView gridView;
    private FourPicGridAdapter adapter;
    private ArrayList<EventBean> restultBeans;
    /**
     * 现实进度条
     */
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_comparison);
        EventBus.getDefault().register(this);
        Log.i("liyachao333", "0");
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facePlusPlusHelper.stopThread();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(EventBean bean) {
        if (bean.isCorrect()) {
            if (bean.getMode() == FacePlusPlusHelper.METHOD_ONE) {
                background.setMyRect(bean.getFace());
                faceId = bean.getFaceId();
                comparisonPic();
            } else if (bean.getMode() == FacePlusPlusHelper.METHOD_TWO) {
                faceId = bean.getFaceId();
                comparisonPic();
            } else if (bean.getMode() == FacePlusPlusHelper.METHOD_THREE) {
                double num = Double.parseDouble(bean.getDescribe());
                EventBean bean1 = new EventBean();
                bean1.setSimilarity(num);
                bean1.setImage(urlList.get(comparisonIndex).getImage());
                restultBeans.add(bean1);
                Collections.sort(restultBeans, new Comparator<EventBean>() {
                    @Override
                    public int compare(EventBean eventBean, EventBean t1) {
                        int temp1 = (int) eventBean.getSimilarity();
                        int temp2 = (int) t1.getSimilarity();
                        if (temp1 > temp2) {
                            return -1;
                        } else if (temp1 < temp2) {
                            return 1;
                        } else return 0;
                    }
                });

                adapter.notifyDataSetChanged();
                result = result > num ? result : num;
                comparisonIndex++;
                comparisonPic();
            }
        } else {
            Toast.makeText(this, bean.getDescribe(), Toast.LENGTH_SHORT).show();
            if (bean.getMode() == FacePlusPlusHelper.METHOD_THREE) {
                comparisonPic();
            }
            comparisonText.setText(bean.getDescribe());
            animation.cancel();
            light.setVisibility(View.GONE);
            btn2.setEnabled(true);
            btn2.setOnClickListener(this);
        }
    }

    private boolean isUrgent = false;
    private String phoneNum = null;

    private void initDatas() {

        intent = getIntent();
        imagePath = intent.getStringExtra(CompareActivity.IMAGE_PATH);
        if (imagePath == null) {
            finish();
        }
        Log.i("liyachao333", "str: " + imagePath);
        urlList = new ArrayList<>();
        String str = intent.getStringExtra(Constant.ACTIVITY_FROM_VOLUN);
        Log.i("AVInstallation", "str: " + str);
        if (str != null && str.equals(Constant.ACTIVITY_FROM_VOLUN)) {
            urlList.addAll(DataSource.getInstance().getBeansFromUrl());
        } else {
            isUrgent = true;
            btn2.setText("拨打电话");
            Log.i("AVInstallation", "AVObject: " + (ImageId.imageIdStr == null));
            String str1 = intent.getExtras().getString("com.avos.avoscloud.Data");
            Log.i("AVInstallation", "str1: " + str1);
            JSONObject json = JSONObject.parseObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
            Log.i("AVInstallation", "json: " + json.toString());
            phoneNum = json.getString(Constant.CLOUD_PHONE_NUMBER);
            for (int i = 0; i < 4; i++) {
                String url = json.getString(Constant.CLOUD_IMAGES_STRING + i);
                EventBean bean = new EventBean();
                bean.setImage(url);
                urlList.add(bean);
            }
        }
    }

    protected void initView() {
        context = this;


        /**
         * 初始化进度框
         */
        progressDialog = new ProgressDialog(this);
        btn1 = getView(R.id.activity_detect_giveup);
        btn2 = getView(R.id.activity_detect_submit);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        gridView = getView(R.id.activity_detect_comparison_grid);
        restultBeans = new ArrayList<>();

        initDatas();


        adapter = new FourPicGridAdapter(context, restultBeans, R.layout.gridview_four_pic_item);
        gridView.setAdapter(adapter);
        btn2.setEnabled(false);
        facePlusPlusHelper = FacePlusPlusHelper.getInstance();
        background = getView(R.id.activity_checked_detect_background);
        imageView = getView(R.id.imageview);
        blackBg = getView(R.id.activity_checked_black);
        compaLayout = getView(R.id.activity_detect_compar_Layout);
        comparisonPic = getView(R.id.activity_detect_compar_pic);
        comparisonText = getView(R.id.activity_detect_text);
        Log.i("liyachao333", "3");


        BitmapUtil.getInstance().display(comparisonPic, urlList.get(comparisonIndex).getImage());

        Log.i("liyachao333", "4");
        light = getView(R.id.activity_detect_light);
        animation = ObjectAnimator.ofFloat(light, "translationY", light.getTranslationY(), MyUtils.dip2px(context, 115));
        animation.setDuration(2000);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(-1);
        facePlusPlusHelper.faceDetect(imagePath);
        background.setImageBitmap(MyUtils.getScaledBitmap(imagePath));
        background.detecting();
        Log.i("liyachao333", "5");
        background.setAnimationFinishListener(new FacePlusImageView.AnimationFinishListener() {
            @Override
            public void onFinish(Bitmap bitmap, RectF rectF, float scale, int curX, int curY) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//                facePlusPlusHelper.getFaceId(bitmap);
                imageView.setImageBitmap(bitmap);
                background.setBackgroundColor(Color.BLACK);
                lp.width = (int) ((rectF.right - rectF.left) * scale);
                lp.height = (int) ((rectF.bottom - rectF.top) * scale);
                imageView.setX(rectF.left * scale + curX);
                imageView.setY(rectF.top * scale + curY);
                imageView.setLayoutParams(lp);
                blackBg.setVisibility(View.VISIBLE);

                float y = MyUtils.dip2px(context, 120) * 1.0f / lp.height;
                float x = MyUtils.dip2px(context, 120) * 1.0f / lp.width;
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(imageView, "lycX",
                        1f, x);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(imageView, "lycY",
                        1f, y);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageView, "transX",
                        imageView.getX(), MyUtils.dip2px(context, 40));
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageView, "transY",
                        imageView.getY(), MyUtils.dip2px(context, 138));

                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float val = (float) valueAnimator.getAnimatedValue();
                        imageView.setPivotX(0);
                        imageView.setScaleX(val);
                    }
                });
                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float val = (float) valueAnimator.getAnimatedValue();
                        imageView.setPivotY(0);
                        imageView.setScaleY(val);
                    }
                });
                animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float val = (float) valueAnimator.getAnimatedValue();
                        imageView.setX(val);
                    }
                });
                animator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float val = (float) valueAnimator.getAnimatedValue();
                        imageView.setY(val);
                    }
                });

                AnimatorSet animSet = new AnimatorSet();
                animSet.play(animator1).with(animator2).with(animator3).with(animator4);
                animSet.setDuration(2000);
                animSet.start();
                animSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        imageView.isDrawFrame();
                        compaLayout.setVisibility(View.VISIBLE);

                        animation.start();
                        aniIsFinished = true;
                        comparisonPic();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });


    }


    @Override
    public void callPhoneNumber() {
        super.callPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ((phoneNum == null || phoneNum.length() == 0) ? "110" : phoneNum)));
        this.startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_detect_giveup:
                finish();
                break;
            case R.id.activity_detect_submit:

                if (isUrgent) {
                    if (!hasPermission(Manifest.permission.CALL_PHONE)) {
                        requestPermission(Constant.CALL_PHONE_CODE,
                                Manifest.permission.CALL_PHONE);
                    } else {
                        callPhoneNumber();
                    }
                } else {
                    EventBean bean = new EventBean();
                    bean.setImage(imagePath);
                    restultBeans.add(0, bean);
                    if (restultBeans.size() == 5) {
                        DataSource.getInstance().addCompareResult(restultBeans);
                        Toast.makeText(this, "上传完成", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DetectComparisonActivity.this, VolunteerActivity.class));
                    } else {
                        Toast.makeText(this, "数据有误，请再次比对", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    public void comparisonPic() {
        if (faceId != null && aniIsFinished) {
            count++;
            if (count <= urlList.size()) {
                if (comparisonIndex < urlList.size()) {
//                    bitmapUtils.display(comparisonPic, urlList.get(comparisonIndex));
                    BitmapUtil.getInstance().display(comparisonPic, urlList.get(comparisonIndex).getImage());
                    facePlusPlusHelper.comparisonFace(faceId, urlList.get(comparisonIndex).getImage()
                            , null);
                }
            } else {
                comparisonText.setText("比较完成");
                animation.cancel();
                light.setVisibility(View.GONE);
                if (!isUrgent) {
                    btn2.setEnabled(true);
                    btn2.setOnClickListener(this);
                }
                if (isUrgent && restultBeans.get(0).getSimilarity() > 70) {
                    btn2.setEnabled(true);
                    btn2.setOnClickListener(this);
                }
            }
        }
    }


}
