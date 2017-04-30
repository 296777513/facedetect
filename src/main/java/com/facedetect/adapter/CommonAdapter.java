package com.facedetect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Android Studio. author: liyachao Date: 15-4-21 Time: 16:33
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> datas;
    protected LayoutInflater inflater;
    protected int layoutId;

    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, layoutId, position);
        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t, int position);

}
