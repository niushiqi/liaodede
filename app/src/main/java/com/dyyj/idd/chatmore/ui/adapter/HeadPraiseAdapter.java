package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;

import java.util.List;

/**
 * desc 点赞头像图片展示
 *
 * @author zhangcx
 * 2018/12/27 1:20 PM
 */
public class HeadPraiseAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mDatas;

    public HeadPraiseAdapter(Context context, List<String> datas) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mDatas.size() > 30) {
            return 30;
        } else {
            return mDatas.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_head_comment_praise, null);
        }
        ImageView imageView = convertView.findViewById(R.id.iv_item_head_praise);
        ImageLoader.loadHead(imageView, mDatas.get(position));
        return convertView;
    }
}
