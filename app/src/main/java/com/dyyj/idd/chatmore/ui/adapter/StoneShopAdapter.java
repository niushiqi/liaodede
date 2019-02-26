package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult;

import java.util.List;

/**
 * Created by mlb on 2018/10/1.
 **/

public class StoneShopAdapter extends BaseQuickAdapter<RecycleShopResult.Data.Stone, BaseViewHolder> {

     public  Context context1;
    public StoneShopAdapter(Context context, @Nullable List<RecycleShopResult.Data.Stone> data) {
        super(R.layout.item_shop,data);
         context1 = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecycleShopResult.Data.Stone item) {

        helper.setText(R.id.atv_go,item.getGoodsPrice()+"元");//价格

        setTextFont(helper.getView(R.id.tv_num));
        helper.setText(R.id.tv_num,"+"+item.getGoodsNum());//魔石数量

        if (item.getGoodsPromotion() != null) {
            helper.setText(R.id.tv_promotion,item.getGoodsPromotion());//赠送的
        }

        Glide.with(context1).load(item.getGoodsImg()).into((ImageView) helper.getView(R.id.iv_shop_bg));


    }


    /**
     * 设置字体
     */
    void setTextFont(TextView textView) {
        textView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/font1.ttf"));
    }

}

