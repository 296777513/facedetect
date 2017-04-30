package com.facedetect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facedetect.utils.MyUtils;
import com.faceplusplus.api.FaceDetecter;
import com.lidroid.xutils.bitmap.core.AsyncDrawable;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Android Studio. author: liyachao Date: 16/11/6 Time: 10:53
 */
public class FacePlusImageView extends ImageView {
    private Paint rectPaint;
    private RectF currentRectF;
    private Context context;
    private AnimationFinishListener listener;
    private Bitmap bitmap;
    private int curX, curY;
    private boolean isFirst = true;
    private float left, top, right, bottom;
    private float scale;
    private Timer timer;
    private boolean isVisiable = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isVisiable = !isVisiable;
            rectPaint.setColor(isVisiable ? Color.TRANSPARENT : Color.parseColor("#167FD8"));
            invalidate();
        }
    };

    public FacePlusImageView(Context context) {
        super(context);
        init(context);
    }

    public FacePlusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FacePlusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    int width, height;//屏幕的宽和高的像素

    private void init(Context context) {
        this.context = context;
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(Color.parseColor("#167FD8"));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(MyUtils.dip2px(context, 2));
    }


    /**
     * 得到当前的bitmap
     */
    private void getBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) return;
        if (drawable instanceof AsyncDrawable) {
            Bitmap curBmp = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas1 = new Canvas(curBmp);
            drawable.draw(canvas1);
            bitmap = curBmp.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels - MyUtils.getStatusBarHeight(context);
        float scaleX = width * 1.0f / bitmap.getWidth();
        float scaleY = height * 1.0f / bitmap.getHeight();
        scale = Math.min(scaleX, scaleY);
        setScaleX(scale);
        setScaleY(scale);
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        if (scaleX < scaleY) {
            curX = 0;
            curY = (int) ((height - bitmap.getHeight() * scale) / 2);
        } else {
            curY = 0;
            curX = (int) ((width - bitmap.getWidth() * scale) / 2);
        }
        currentRectF = new RectF(0, 0, getWidth(), getHeight());
    }


    public void setMyRect(FaceDetecter.Face faceinfo) {
        if (timer != null)
            timer.cancel();
        rectPaint.setColor(Color.parseColor("#167FD8"));
        /**
         * 得到脸部的范围
         */
        left = getWidth() * faceinfo.left;
        top = getHeight() * faceinfo.top;
        right = getWidth() * faceinfo.right;
        bottom = getHeight() * faceinfo.bottom;

        float widthLen = right - left;
        float heightLen = bottom - top;

        left = (left - widthLen * 0.2f) >= 0 ? (left - widthLen * 0.2f) : 0;
        top = (top - heightLen * 0.2f) >= 0 ? (top - heightLen * 0.2f) : 0;
        right = (right + widthLen * 0.2f) <= getWidth() ? (right + widthLen * 0.2f) : getWidth();
        bottom = (top + heightLen * 0.2f) <= getHeight() ? (bottom + heightLen * 0.2f) : getHeight();

        int tempWidth = (int) ((right - left) > (bitmap.getWidth() - left) ?
                (bitmap.getWidth() - left) : (right - left));
        int tempHeight = (int) ((bottom - top) > (bitmap.getHeight() - top) ?
                (bitmap.getHeight() - top) : (bottom - top));
        bitmap = Bitmap.createBitmap(bitmap, (int) (left), (int) (top),
                tempWidth, tempHeight, new Matrix(), true);

        RectF startRectF = new RectF(0, 0, getWidth(), getHeight());
        RectF endRectF = new RectF(left, top, right, bottom);
        ValueAnimator ani = ValueAnimator.ofObject(new RectFEvaluator(), startRectF, endRectF);
        ani.setDuration(3000);
        ani.start();
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentRectF = (RectF) valueAnimator.getAnimatedValue();
                invalidate();
            }

        });
        ani.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (listener != null)
                    listener.onFinish(bitmap, currentRectF, scale, curX, curY);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    public void detecting() {
        if (isFirst) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            }, 0, 800);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirst) {
            isFirst = false;
            getBitmap();
        }
        canvas.drawRect(currentRectF, rectPaint);
    }

    public class RectFEvaluator implements TypeEvaluator<RectF> {

        @Override
        public RectF evaluate(float fraction, RectF startRectF, RectF endRectF) {
            float left = startRectF.left + fraction * (endRectF.left - startRectF.left);
            float top = startRectF.top + fraction * (endRectF.top - startRectF.top);
            float right = startRectF.right + fraction * (endRectF.right - startRectF.right);
            float bottom = startRectF.bottom + fraction * (endRectF.bottom - startRectF.bottom);
            return new RectF(left, top, right, bottom);
        }
    }


    public interface AnimationFinishListener {
        void onFinish(Bitmap bitmap, RectF rectF, float scale, int curX, int curY);
    }

    public void setAnimationFinishListener(AnimationFinishListener listener) {
        this.listener = listener;
    }


}
