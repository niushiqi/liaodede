package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult;

import java.util.List;


/**
 * Created by zwj on 2019/1/15
 * desc: 匹配卡条目
 **/

public class MatchingCardShopAdapter extends BaseQuickAdapter<RecycleShopResult.Data.MatchCard, BaseViewHolder> {

    public  Context mContext;
    public MatchingCardShopAdapter(Context context, @Nullable List<RecycleShopResult.Data.MatchCard> data) {
        super(R.layout.item_match_card_shop,data);
         mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecycleShopResult.Data.MatchCard item) {

        helper.setText(R.id.atv_go,item.getGoodsPrice()+"元");

        helper.setText(R.id.tv_dedou_price,item.getGoodsOncePrice()+"元/次");

        helper.setText(R.id.tv_desc,"每天多加"+item.getGoodsEveryDayNum()+"次机会");

        if (item.getGoodsPromotion() != null) {
            helper.setText(R.id.tv_promotion,item.getGoodsPromotion());
        }

        Glide.with(mContext).load(item.getGoodsImg()).into((ImageView) helper.getView(R.id.iv_shop_bg));

    }

}

