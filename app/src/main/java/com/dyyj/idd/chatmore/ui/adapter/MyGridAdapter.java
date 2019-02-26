package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.utils.BaseViewHolder;

/**
 * Created by mlb on 2018/10/3.
 */

public class MyGridAdapter extends BaseAdapter {
    private Context mContext;

    public String[] img_text ;

    public MyGridAdapter(Context mContext, String[] img_text) {
        super();
        this.mContext = mContext;
        this.img_text = img_text;
    }
    private int clickTemp = -1;
    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }
    @Override
    public int getCount() {
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_tv, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        tv.setText(img_text[position]+"元");

        if (clickTemp == position) {
            tv.setBackgroundResource(R.drawable.bg_withdraw3);
            tv.setTextColor(Color.parseColor("#00A9F0"));
        } else {
            tv.setBackgroundResource(R.drawable.bg_withdraw2);
            tv.setTextColor(Color.parseColor("#999999"));
        }

        return convertView;
    }

}

