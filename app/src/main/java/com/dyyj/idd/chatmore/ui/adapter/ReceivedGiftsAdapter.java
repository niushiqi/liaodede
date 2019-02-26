package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.MyGiftsResult;

import java.util.List;


/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/14
 * desc   : 赠人玫瑰
 */
public class ReceivedGiftsAdapter extends BaseQuickAdapter<MyGiftsResult.Gift, BaseViewHolder> {

    public  Context mContext;
    public ReceivedGiftsAdapter(Context context, @Nullable List<MyGiftsResult.Gift> data) {
        super(R.layout.item_received_gifts,data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyGiftsResult.Gift item) {

        Glide.with(mContext).load(item.getIcon()).into((ImageView) helper.getView(R.id.received_gift_img));//收到的礼物

        helper.setText(R.id.received_gift_name,item.getName());//礼物名称

        helper.setText(R.id.received_gift_num, "拥有 "+item.getNum()+"个");//拥有数量

        helper.setText(R.id.gold_tv1,""+item.getCoin());

        helper.addOnClickListener(R.id.cl_container);

        if (null != item.getSelected()) {
            if (item.getSelected() == 1) {
                helper.setBackgroundRes(R.id.cl_container, R.drawable.bg_rect_round_stroke_checked);
            }else{
                helper.setBackgroundRes(R.id.cl_container, R.drawable.bg_rect_round_stroke_gray);
            }
        }
    }

}