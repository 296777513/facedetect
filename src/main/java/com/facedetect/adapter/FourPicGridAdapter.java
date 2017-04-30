package com.facedetect.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facedetect.Bean.EventBean;
import com.facedetect.R;
import com.facedetect.utils.BitmapUtil;
import com.facedetect.utils.MyUtils;
import com.lidroid.xutils.BitmapUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Android Studio. author: liyachao Date: 15/8/26 Time: 13:42
 */
public class FourPicGridAdapter extends CommonAdapter<EventBean> {
    private TextView textView;

    public FourPicGridAdapter(Context context, List<EventBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, EventBean bean, int position) {
        ImageView imageView = holder.getView(R.id.activity_checked_add);
        textView = holder.getView(R.id.activity_checked_text);
        BitmapUtil.getInstance().display(imageView,bean.getImage());
        if (bean.getSimilarity() != 0) {
            textView.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("###.00");
            textView.setText(df.format(bean.getSimilarity()) + "%");
        }
    }

}
