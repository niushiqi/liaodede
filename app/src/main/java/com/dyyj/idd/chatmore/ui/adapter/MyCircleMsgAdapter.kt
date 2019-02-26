package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemMycircleMsgBinding
import com.dyyj.idd.chatmore.model.network.result.TopicCommentResult
import com.dyyj.idd.chatmore.utils.DateUtils
import jp.wasabeef.glide.transformations.CropCircleTransformation

class MyCircleMsgAdapter : BaseAdapterV2<TopicCommentResult.ReplyComment>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<TopicCommentResult.ReplyComment, ItemMycircleMsgBinding>(
            viewGroup, R.layout.item_mycircle_msg) {
        override fun onBindViewHolder(obj: TopicCommentResult.ReplyComment, position: Int) {
            super.onBindViewHolder(obj, position)

            Glide.with(mContext).load(obj.avatar).asBitmap().transform(
                    CropCircleTransformation(mContext)).crossFade().error(
                    R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar)

            mBinding.txtName.text = obj.replyVirtualName
            mBinding.txtTime.text = DateUtils.getStrTime("MM月dd日 hh:mm", obj.addTimeStamp)
            mBinding.ivLike.visibility = if (obj.replyType == "2") View.VISIBLE else View.GONE
            mBinding.txtContent.text = if (obj.replyType == "2") "赞了你" else obj.replyMessage

            if (obj.topicType == "4") {
                mBinding.ivRight.visibility = View.VISIBLE
                mBinding.txtRight.visibility = View.GONE
                mBinding.ivRight.setImageResource(R.drawable.ic_msg_level)
            } else if (obj.topicType == "5") {
                mBinding.ivRight.visibility = View.VISIBLE
                mBinding.txtRight.visibility = View.GONE
                mBinding.ivRight.setImageResource(R.drawable.ic_msg_task)
            } else if (obj.topicType == "6") {
                mBinding.ivRight.visibility = View.VISIBLE
                mBinding.txtRight.visibility = View.GONE
                mBinding.ivRight.setImageResource(R.drawable.ic_msg_bank)
            } else {
                if (obj.topicImgs.isNullOrBlank()) {
                    mBinding.ivRight.visibility = View.GONE
                    mBinding.txtRight.visibility = View.VISIBLE
                    mBinding.txtRight.text = obj.topicBody
                } else {
                    mBinding.ivRight.visibility = View.VISIBLE
                    mBinding.txtRight.visibility = View.GONE
                    mBinding.ivRight.setImageResource(R.drawable.ic_default_img)
                    Glide.with(mContext).load(obj.topicImgs).into(mBinding.ivRight)
                }
            }
        }
    }
}