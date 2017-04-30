package com.facedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.facedetect.application.MyApplication;
import com.facedetect.utils.MyUtils;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by Android Studio. author: liyachao Date: 15/7/14 Time: 16:52
 */
public class AnimationCircleView extends View {
    private Context context;
    private Paint defaultPaint;// 绘制地步圆圈画笔
    private Paint circlePaint2;// 绘制外部圆环画笔

    private boolean isStartAnim1, isStartAnim2;

    private boolean isFirstInit = true;
    private int halfHeight;
    private int halfWidth;
    private int defaultRadius, lineRadius, targetRadius, targetRadius1, circleRadius, circleRadius1;
    private ValueAnimator animator, animator1, animator2;


    public AnimationCircleView(Context context) {
        super(context);
        init(context);
        Looper.loop();
    }

    public AnimationCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnimationCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        defaultRadius = MyUtils.dip2px(context, 50);
        targetRadius = MyUtils.dip2px(context, 200);
        targetRadius1 = MyUtils.dip2px(context, 170);
        lineRadius = defaultRadius;
        circleRadius = defaultRadius;
        circleRadius1 = defaultRadius;

        defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);
        defaultPaint.setColor(Color.parseColor("#50e6cb85"));

        circlePaint2 = new Paint();
        circlePaint2.setAntiAlias(true);// 抗锯齿
        circlePaint2.setStyle(Paint.Style.STROKE);// 实心
        circlePaint2.setColor(Color.parseColor("#ffc123"));
        circlePaint2.setStrokeWidth(MyUtils.dip2px(context, 1));


        animator = new ValueAnimator();
        animator.setDuration(1500);
        animator.setFloatValues(0f, 1f);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new MyUpdateListener());

        animator1 = new ValueAnimator();
        animator1.setDuration(1300);
        animator1.setFloatValues(0f, 1f);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new MyUpdateListener());

        animator2 = new ValueAnimator();
        animator2.setDuration(1100);
        animator2.setFloatValues(0f, 1f);
        animator1.setInterpolator(new LinearInterpolator());
        animator2.addUpdateListener(new MyUpdateListener());

    }

    private class MyUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (animation == animator) {
                float currentValue = (float) animation.getAnimatedValue();
                int currentTime = (int) (animation.getCurrentPlayTime() / 100);
                if (currentValue < 1.0f) {
                    lineRadius = (int) ((targetRadius * currentValue) + defaultRadius);
                } else {
                    lineRadius = defaultRadius;
                    animator1.cancel();
                    animator2.cancel();
                    circleRadius = defaultRadius;
                    circleRadius1 = defaultRadius;
                }

                if (currentTime == 4 && !isStartAnim1) {
                    animator1.start();
                    isStartAnim1 = true;
                }
            }
            if (animation == animator1) {
                float currentValue = (float) animation.getAnimatedValue();
                int currentTime = (int) (animation.getCurrentPlayTime() / 100);
                if (currentValue < 1.0f) {
                    circleRadius = (int) ((targetRadius1 * currentValue) + defaultRadius);
                } else {
                    circleRadius = defaultRadius;
                    isStartAnim1 = false;
                }
                if (currentTime == 3 && !isStartAnim2) {
                    animator2.start();
                    isStartAnim2 = true;
                }
            }
            if (animation == animator2) {
                float currentValue = (float) animation.getAnimatedValue();
                if (currentValue < 1.0f) {
                    circleRadius1 = (int) ((targetRadius1 * currentValue));
                } else {
                    circleRadius1 = defaultRadius;
                    isStartAnim2 = false;
                }
            }
            postInvalidate();
        }
    }


    public void startMyAnimation() {
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (isFirstInit) {
            halfHeight = getHeight() / 2;
            halfWidth = getWidth() / 2;
            isFirstInit = false;
//        }
        /**
         * 离屏缓存
         * Layer层的宽和高要设定好，不然会出现有些部位不再层里面，你的操作是不对这些部位起作用的
         */
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.drawCircle(halfWidth, halfHeight, lineRadius,
                circlePaint2);

        if (isStartAnim2) {
            canvas.drawCircle(halfWidth, halfHeight, circleRadius,
                    defaultPaint);
            defaultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            defaultPaint.setColor(Color.BLUE);
            canvas.drawCircle(halfWidth, halfHeight, circleRadius1,
                    defaultPaint);
            defaultPaint.setColor(Color.parseColor("#50e6cb85"));

        } else {
            canvas.drawCircle(halfWidth, halfHeight, circleRadius,
                    defaultPaint);
        }

        defaultPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

}
