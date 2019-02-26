package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPlazaTopics2Binding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicActivity
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaTopicFocusPop
import com.dyyj.idd.chatmore.utils.DateUtils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * 广场关注de话题列表，
 */
class PlazaTopicsFocusAdapter : BaseAdapterV2<PlazaTopicListResult.Topic>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<PlazaTopicListResult.Topic, ItemPlazaTopics2Binding>(parent,
            R.layout.item_plaza_topics2) {
        override fun onBindViewHolder(obj: PlazaTopicListResult.Topic, position: Int) {
            super.onBindViewHolder(obj, position)

            if (obj.newComments > 0) {
                mBinding.tvNum.text = "${obj?.newComments}"
                mBinding.tvNum.visibility = View.VISIBLE
            } else {
                mBinding.tvNum.visibility = View.GONE
            }

            mBinding.tvIstop.visibility = if (obj.top == 1) View.VISIBLE else View.GONE

            mBinding.tvTime.text = DateUtils.getStrTime("MM月dd日 HH:mm", obj.squareTopicPubTimestamp);//yyyy/MM/dd HH:mm

            Glide.with(mContext).load(obj.squareTopicImageList).asBitmap()
                    .transform(CenterCrop(mContext), RoundedCornersTransformation(mContext, 30, 0))
                    .crossFade()
                    .placeholder(R.drawable.shape_bg_black)
                    .error(R.drawable.shape_bg_black)
                    .into(mBinding.iv)

            mBinding.root.setOnClickListener({
                PlazaTopicActivity.start(it.context, obj.squareTopicId)
            })
            mBinding.root.setOnLongClickListener({
                PlazaTopicFocusPop(it.context).show(it, obj)
                return@setOnLongClickListener true
            })
        }

    }

}