package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPlazaTopicsPostedBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import org.greenrobot.eventbus.EventBus

/**
 * 广场热门话题列表，
 */
class PlazaTopicsPostedAdapter : BaseAdapterV2<PlazaTopicListResult.Topic>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<PlazaTopicListResult.Topic, ItemPlazaTopicsPostedBinding>(parent,
            R.layout.item_plaza_topics_posted) {
        override fun onBindViewHolder(obj: PlazaTopicListResult.Topic, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.tvTitle.text = obj.squareTopicTitle
            mBinding.tvPeopleNum.text = "参与 ${obj.squareTopicAgreeNum + obj?.squareTopicCommentsNum}人"

            Glide.with(mContext).load(obj.squareTopicImageList).asBitmap()
                    .placeholder(R.drawable.shape_bg_black)
                    .error(R.drawable.shape_bg_black)
                    .into(mBinding.iv)

            mBinding.root.setOnClickListener({
                EventBus.getDefault().post(SelectTopicVM(obj.squareTopicId, obj.squareTopicTitle))
            })
        }

    }

    class SelectTopicVM(val topicID: String?, val title: String?)


}