package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.ChatGiftsResult;

import java.util.List;

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/12
 * desc   : 礼物
 */

public class GiftGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    public List<ChatGiftsResult.Data.Gift> mGifts;

    /**
     * 页数下标,从0开始(当前是第几哪条线页)
     */
    private int mCurIndex;
    /**
     * 每一页显示的个数
     */
    private int pageSize = 8;

    public GiftGridAdapter(Context mContext, List<ChatGiftsResult.Data.Gift> gift, int curIndex) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mGifts = gift;
        this.mCurIndex = curIndex;
    }

    //private int clickTemp = -1;

    //标识选择的Item
    /*public void setSeclection(int position) {
        clickTemp = position;
    }*/

    /**
     * 先判断数据集的大小是否足够显示满本页？mDatas.size() > (curIndex+1)*pageSize,
     * 如果够，则直接返回每一页显示的最大条目个数pageSize,
     * 如果不够，则有几项返回几,(mDatas.size() - curIndex * pageSize);(也就是最后一页的时候就显示剩余item)
     */
    @Override
    public int getCount() {
        return mGifts.size() > (mCurIndex + 1) * pageSize ? pageSize : (mGifts.size() - mCurIndex * pageSize);
    }

    @Override
    public ChatGiftsResult.Data.Gift getItem(int position) {
        return mGifts.get(mCurIndex * pageSize + position);
    }

    @Override
    public long getItemId(int position) {
        return position + mCurIndex * pageSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.gift_item_layout, parent, false);
            holder.gift_img = convertView.findViewById(R.id.img_gift);
            holder.giftName_tv = convertView.findViewById(R.id.tv_gift_name);
            holder.giftPrice_tv = convertView.findViewById(R.id.tv_pea_nums);
            holder.giftXianMian_tv = convertView.findViewById(R.id.tv_pea_xianmian);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).
                load(mGifts.get(mCurIndex * pageSize+position).getIcon())
                .asBitmap().crossFade()
                .placeholder(R.drawable.ic_gifts_placeholder)
                .error(R.drawable.ic_gifts_placeholder)
                .into(holder.gift_img);
        holder.giftName_tv.setText(mGifts.get(mCurIndex * pageSize+position).getName());
        if (mGifts.get(mCurIndex * pageSize+position).getDedouNum() == 0) {
            holder.giftPrice_tv.setVisibility(View.GONE);//价格消失
            holder.giftXianMian_tv.setVisibility(View.VISIBLE);//显示限免
        }else{
            holder.giftXianMian_tv.setVisibility(View.GONE);//限免消失
            holder.giftPrice_tv.setVisibility(View.VISIBLE);
            holder.giftPrice_tv.setText(mGifts.get(mCurIndex * pageSize+position).getDedouNum()+"得豆");
        }
        return convertView;
    }

    class ViewHolder{
        ImageView gift_img;
        TextView giftName_tv;
        TextView giftPrice_tv;
        TextView giftXianMian_tv;
    }

}