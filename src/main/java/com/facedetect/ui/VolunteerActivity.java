package com.facedetect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facedetect.Constant.Constant;
import com.facedetect.R;

/**
 * Created by l00385426 on 2016/11/11.
 */

public class VolunteerActivity extends BaseActivity implements View.OnClickListener {

    private Button start;
    private Button mBtnCompareHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        initTitle();
        btnBack.setOnClickListener(this);
        titleName.setText("我是志愿者");
        start = getView(R.id.start_compare);
        start.setOnClickListener(this);
        mBtnCompareHistory = getView(R.id.compare_history);
        mBtnCompareHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.left_title_layout:
                finish();
                break;
            case R.id.start_compare:
                intent = new Intent(VolunteerActivity.this, CompareActivity.class);
                intent.putExtra(Constant.ACTIVITY_FROM_VOLUN,Constant.ACTIVITY_FROM_VOLUN);
                startActivity(intent);
                break;
            case R.id.compare_history:
                intent = new Intent(VolunteerActivity.this, CompareResultActivity.class);
                startActivity(intent);
                break;
        }
    }
}
