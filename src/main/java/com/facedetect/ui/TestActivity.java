package com.facedetect.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.facedetect.R;
import com.facedetect.view.AnimationCircleView;

/**
 * Created by l00385426 on 2016/11/18.
 */

public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ViewGroup group = (ViewGroup) findViewById(android.R.id.content);
        View view = group.getChildAt(0);
    }


}
