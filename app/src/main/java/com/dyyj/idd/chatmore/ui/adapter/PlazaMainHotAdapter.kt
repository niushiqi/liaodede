package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPlazaHotSubjectItemBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class PlazaMainHotAdapter : BaseAdapterV2<PlazaTopicListResult.Topic>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<PlazaTopicListResult.Topic, ItemPlazaHotSubjectItemBinding>(parent,
            R.layout.item_plaza_hot_subject_item) {
        override fun onBindViewHolder(obj: PlazaTopicListResult.Topic, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.tv.text = "参与 ${obj.squareTopicAgreeNum + obj?.squareTopicCommentsNum}人"

            Glide.with(mContext).load(obj.squareTopicImageList).asBitmap()
                    .transform(CenterCrop(mContext), RoundedCornersTransformation(mContext, 10, 0))
                    .crossFade()
                    .placeholder(R.drawable.shape_bg_black)
                    .error(R.drawable.shape_bg_black)
                    .into(mBinding.iv)

            mBinding.root.setOnClickListener({
                PlazaTopicActivity.start(it.context, obj.squareTopicId)
                EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_talktheme", obj.squareTopicId))
            })
        }

    }

}