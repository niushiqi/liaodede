package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPlazaSpaceCardBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaSpaceCardResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicActivity
import com.dyyj.idd.chatmore.ui.user.activity.CircleCommentsActivity
import com.dyyj.idd.chatmore.ui.user.activity.CommentsActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 我的空间 动态
 */
class PlazaSpaceCardAdapter(var userID: String) : LoadMoreAdapter<PlazaSpaceCardResult.Detail>() {

    var format = SimpleDateFormat("HH:mm")
    var formatDate = SimpleDateFormat("MM/dd")
    var calendar: Calendar = Calendar.getInstance()

    init {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) return ViewHolder(parent)
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<PlazaSpaceCardResult.Detail, ItemPlazaSpaceCardBinding>(
            viewGroup, R.layout.item_plaza_space_card) {
        override fun onBindViewHolder(obj: PlazaSpaceCardResult.Detail, position: Int) {
            super.onBindViewHolder(obj, position)

            mBinding.setVariable(BR.obj, obj)
            mBinding.setVariable(BR.ViewHolder, this)

            mBinding.tvTopic.visibility = View.GONE
            if (!TextUtils.isEmpty(obj.squareTopicTitle)) {
                mBinding.tvTopic.visibility = View.VISIBLE
                mBinding.tvTopic.text = "#${obj.squareTopicTitle}"
            }

            mBinding.tvDesc.text = obj.topicBody

            if (obj.addTimeStamp > 0) {
                val toLong = obj.addTimeStamp * 1000
                mBinding.tvTime.text = format.format(Date(toLong))

                if (toLong > calendar.timeInMillis) {
                    mBinding.tvDate.text = "今天"
                } else if (calendar.timeInMillis - toLong < 24 * 3600 * 1000) {
                    mBinding.tvDate.text = "昨天"
                } else {
                    mBinding.tvDate.text = formatDate.format(Date(toLong))
                }
            }

            mBinding.tvVisiable.text = if (obj.squareTopicType == 1) "广场可见" else "好友圈可见"

            //加精
//            mBinding.ivJing.visibility = if (obj.commentIsHot == 1) View.VISIBLE else View.INVISIBLE

            mBinding.txtComment.text = "${obj.topicReplyCount}"

            /**
             * 赞信息
             */
            mBinding.txtLike.text = "+${obj.topicAgreeCount + obj.topicVoteCount}"
//            mBinding.ivLike.setImageResource(if (obj.agree != 0) R.drawable.ic_dynamic_like_ok else R.drawable.ic_dynamic_like)
//            mBinding.txtLike.setTextColor(if (obj.agree != 0) Color.parseColor("#FF715F") else Color.parseColor("#7F8D9C"))

            var hasMore = false
            /**
             * 图片信息
             */
            if (obj.squareTopicCommentImage.isNotEmpty()) {
                mBinding.rvPhoto.visibility = View.VISIBLE
                mBinding.rvPhoto.layoutManager = GridLayoutManager(mContext, 2)
                var subList = obj.squareTopicCommentImage
                if (obj.squareTopicCommentImage.size > 3) {
                    hasMore = true
                }
                val plazaPhotoAdapter = PlazaSpacePhotoAdapter(mContext, subList, obj.id)
                mBinding.rvPhoto.adapter = plazaPhotoAdapter
//                mBinding.rvPhoto.setHasFixedSize(true)
                mBinding.rvPhoto.isNestedScrollingEnabled = false
            } else {
                mBinding.rvPhoto.visibility = View.GONE
            }


            if (hasMore){
                mBinding.txtMore.visibility = View.VISIBLE
            }else{
                mBinding.txtMore.visibility = View.INVISIBLE
            }

        }

        fun onComments(view: View, obj: PlazaSpaceCardResult.Detail) {
            if (obj.squareTopicType == 1) {
                CommentsActivity.launch(view.context, obj.commentId,obj.id)//广场评论页面
            } else {
                CircleCommentsActivity.launch(view.context, userID, obj.id)//好友圈评论页面
            }
        }

        fun onTopic(view: View, obj: PlazaSpaceCardResult.Detail) {
            if (obj.squareTopicId != null) {
                PlazaTopicActivity.start(view.context, obj.squareTopicId)
            }
        }

    }

}