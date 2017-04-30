package com.facedetect.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facedetect.Bean.EventBean;
import com.facedetect.Constant.DataSource;
import com.facedetect.R;
import com.facedetect.view.PicComparisonView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 */

public class CompareResultActivity extends BaseActivity implements View.OnClickListener {
    private PicComparisonView mComparisonView1, mComparisonView2, mComparisonView3, mComparisonView4;

    private ArrayList<PicComparisonView> mComparisonViews;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_result);
        initTitle();
        btnBack.setOnClickListener(this);
        titleName.setText("对比历史");
        textView = getView(R.id.compare_result_text);
        mComparisonViews = new ArrayList<>();
        mComparisonView1 = getView(R.id.picture_comparison_result1);
        mComparisonViews.add(mComparisonView1);
        mComparisonView2 = getView(R.id.picture_comparison_result2);
        mComparisonViews.add(mComparisonView2);
        mComparisonView3 = getView(R.id.picture_comparison_result3);
        mComparisonViews.add(mComparisonView3);
        mComparisonView4 = getView(R.id.picture_comparison_result4);
        mComparisonViews.add(mComparisonView4);

        List<EventBean> result = DataSource.getInstance().getCompareResult();


        for (int i = 0; i < result.size(); i += 5) {
            textView.setVisibility(View.GONE);
            mComparisonViews.get(i/5).setVisibility(View.VISIBLE);
            mComparisonViews.get(i/5).setDatas(result.subList(i, i + 5));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_title_layout:
                finish();
                break;
        }
    }
}
