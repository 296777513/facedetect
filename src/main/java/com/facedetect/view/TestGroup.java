package com.facedetect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by l00385426 on 2016/11/26.
 */

public class TestGroup extends ViewGroup {
    public TestGroup(Context context) {
        super(context);
    }

    public TestGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }
}
