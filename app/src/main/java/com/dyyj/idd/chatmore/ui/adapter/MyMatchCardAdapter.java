package com.dyyj.idd.chatmore.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.MyMatchCardResult;
import com.dyyj.idd.chatmore.utils.DateUtils;

import java.util.List;

public class MyMatchCardAdapter extends BaseQuickAdapter<MyMatchCardResult.Data, BaseViewHolder> {
    public MyMatchCardAdapter(@Nullable List<MyMatchCardResult.Data> data) {
        super(R.layout.item_match_card, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyMatchCardResult.Data item) {

        helper.setText(R.id.tv_indate, "有效期限：" + DateUtils.timeToDate(item.getStartTime())+"至"+DateUtils.timeToDate(item.getEndTime()));

        Glide.with(mContext).load(item.getBackgroundImg())
                .into((ImageView)helper.getView(R.id.img_card));
    }
}
