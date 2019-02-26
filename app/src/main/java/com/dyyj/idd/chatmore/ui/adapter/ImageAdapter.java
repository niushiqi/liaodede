package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * desc 广场图片展示
 *
 * @author zhangcx
 * 2018/12/27 1:20 PM
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mDatas;

    public ImageAdapter(Context context, List<String> datas) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();
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
            convertView = View.inflate(mContext, R.layout.item_square_pic, null);
        }
        ImageView imageView = convertView.findViewById(R.id.iv_item_image);
        View tv = convertView.findViewById(R.id.tv);
        String imgUrl = mDatas.get(position);
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.toLowerCase().endsWith(".gif")) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
        }

        Glide.with(mContext).load(imgUrl)
                .asBitmap().crossFade()
                .transform(new CenterCrop(mContext), new RoundedCornersTransformation(mContext, 10, 0))
                .error(R.drawable.ic_default_img).placeholder(R.drawable.ic_default_img)
                .into(imageView);
        return convertView;
    }
}
