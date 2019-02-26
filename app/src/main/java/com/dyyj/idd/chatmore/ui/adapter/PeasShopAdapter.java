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
 * Created by zwj on 2019/1/15
 * desc: 得豆条目
 **/

public class PeasShopAdapter extends BaseQuickAdapter<RecycleShopResult.Data.Dedou, BaseViewHolder> {

    public  Context mContext;
    public PeasShopAdapter(Context context, @Nullable List<RecycleShopResult.Data.Dedou> data) {
        super(R.layout.item_shop,data);
         mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecycleShopResult.Data.Dedou item) {

        helper.setText(R.id.atv_go,item.getGoodsPrice()+"元");

        setTextFont(helper.getView(R.id.tv_num));
        helper.setText(R.id.tv_num,"+"+item.getGoodsNum());

        if (item.getGoodsPromotion() != null) {
            helper.setText(R.id.tv_promotion,item.getGoodsPromotion());//赠送的
        }

        Glide.with(mContext).load(item.getGoodsImg()).into((ImageView) helper.getView(R.id.iv_shop_bg));

    }

    /**
     * 设置字体
     */
    void setTextFont(TextView textView) {
        textView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/font1.ttf"));
    }
}

