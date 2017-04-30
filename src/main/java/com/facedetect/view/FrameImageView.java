package com.facedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facedetect.utils.MyUtils;


/**
 * Created by Android Studio. author: liyachao Date: 16/11/7 Time: 12:22
 */
public class FrameImageView extends ImageView {
    Context context;

    public FrameImageView(Context context) {
        super(context);
        this.context = context;
    }

    public FrameImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FrameImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean b = false;

    public void isDrawFrame() {
        b = true;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (b) {
            b = false;
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#167FD8"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(MyUtils.dip2px(context, 6));
            paint.setAntiAlias(true);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
    }
}
