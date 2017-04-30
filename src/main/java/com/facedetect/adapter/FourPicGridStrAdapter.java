package com.facedetect.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.facedetect.R;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by Android Studio. author: liyachao Date: 15/8/26 Time: 13:42
 */
public class FourPicGridStrAdapter extends CommonAdapter<String> {
    private BitmapUtils bitmapUtils;

    public FourPicGridStrAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public void convert(ViewHolder holder, String str, int position) {
        ImageView imageView = holder.getView(R.id.activity_checked_add);
        bitmapUtils.display(imageView, str);

    }
}
