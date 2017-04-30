package com.facedetect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facedetect.Bean.EventBean;
import com.facedetect.R;
import com.facedetect.adapter.FourPicGridAdapter;
import com.facedetect.utils.BitmapUtil;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Android Studio. author: liyachao Date: 15/11/8 Time: 10:51
 */
public class PicComparisonView extends LinearLayout implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private ImageView compareView;
    private FourPicGridAdapter adapter;
    private List<EventBean> imageUrls;
//    private BitmapUtils bitmapUtils;


    public PicComparisonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PicComparisonView(Context context) {
        super(context);
        init(context);
    }

    public PicComparisonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_picture_comparison, this);
        gridView = (GridView) findViewById(R.id.view_picture_comparison_grid);
        compareView = (ImageView) findViewById(R.id.view_picture_compare);
//        bitmapUtils = new BitmapUtils(context);
        gridView.setOnItemClickListener(this);
        imageUrls = new ArrayList<>();
        adapter = new FourPicGridAdapter(context, imageUrls, R.layout.gridview_four_pic_item_black);
        gridView.setAdapter(adapter);
    }

    public void setDatas(List<EventBean> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            EventBean url = imageUrls.get(i);
            if (i == 0) {
//                bitmapUtils.display(compareView, url.getUrl());
                BitmapUtil.getInstance().display(compareView,url.getImage());
            } else {
                this.imageUrls.add(url);
            }
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.getDefault().post(position);
    }
}
