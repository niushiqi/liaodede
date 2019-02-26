package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemCircleCommentBinding
import com.dyyj.idd.chatmore.model.network.result.UserTopicResult
import com.dyyj.idd.chatmore.utils.Utils

class CircleCommentAdapter : BaseAdapterV2<UserTopicResult.UserReply>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<UserTopicResult.UserReply, ItemCircleCommentBinding>(
            viewGroup, R.layout.item_circle_comment) {
        override fun onBindViewHolder(obj: UserTopicResult.UserReply, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.txtComment.text = Utils.getCText(obj.replyVirtualName + "：" + obj.replyMessage, "#5985B2", 0, obj.replyVirtualName?.length!!)
        }
    }

}