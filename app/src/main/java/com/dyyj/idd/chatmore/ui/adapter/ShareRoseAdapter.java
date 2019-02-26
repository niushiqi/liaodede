package com.dyyj.idd.chatmore.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.network.result.MySharedRoseResult;
import com.dyyj.idd.chatmore.utils.DateUtils;

import java.util.List;


/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/14
 * desc   : 赠人玫瑰
 */
public class ShareRoseAdapter extends BaseQuickAdapter<MySharedRoseResult.Data.Gift, BaseViewHolder> {

    public Context mContext;
    public ShareRoseAdapter(Context context, @Nullable List<MySharedRoseResult.Data.Gift> data) {
        super(R.layout.item_shared_rose,data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper,MySharedRoseResult.Data.Gift item) {

        Target<GlideDrawable> into = Glide.with(mContext).load(item.getGift_icon())
                .placeholder(R.drawable.ic_gifts_placeholder)
                //.transform(new CropCircleTransformation())
                .error(R.drawable.ic_gifts_placeholder)
                .into((ImageView) helper.getView(R.id.give_out_gift_img));//送出的礼物


        helper.setText(R.id.tv_who,"送给 "+item.getReceiver_name()+"    ");//好友名称

        helper.setText(R.id.tv_gift_name, item.getGift_name()+" ×1");//礼物名称

        helper.setText(R.id.tv_time, DateUtils.timeToDate(item.getCtime()));//礼物送出时间

        Glide.with(mContext).load(item.getReceiver_avatar())
                //.transform(new CropCircleTransformation(mContext))//为什么不能用呢
                .placeholder(R.drawable.ic_gifts_placeholder)
                .error(R.drawable.ic_gifts_placeholder)
                .into((ImageView) helper.getView(R.id.that_girl_avatar));//给你个机会，送给那个女孩

    }

}