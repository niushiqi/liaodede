package com.dyyj.idd.chatmore.ui.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPlazaCardBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.PlazaCardResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaSpaceActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicActivity
import com.dyyj.idd.chatmore.ui.user.activity.CommentsActivity
import com.dyyj.idd.chatmore.utils.DateUtils
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.gt.common.gtchat.extension.niceChatContext
import jp.wasabeef.glide.transformations.CropCircleTransformation

/**
 * 广场帖子
 */
class PlazaCardAdapter : LoadMoreAdapter<PlazaCardResult.PlazaTopic>() {

    companion object {
        const val KEY_GUDIE = "plaza_gudie_poset"
    }

    var _type = 0

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
            viewGroup: ViewGroup?) : BaseViewHolder<PlazaCardResult.PlazaTopic, ItemPlazaCardBinding>(
            viewGroup, R.layout.item_plaza_card) {
        override fun onBindViewHolder(obj: PlazaCardResult.PlazaTopic, position: Int) {
            super.onBindViewHolder(obj, position)

            mBinding.setVariable(BR.obj, obj)
            mBinding.setVariable(BR.ViewHolder, this)

            mBinding.clTop.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(mContext).load(obj.userInfo?.avatar).apply(requestOption).into(mBinding.ivAvatar)
            Glide.with(mContext).load(obj?.avatar).asBitmap().transform(
                    CropCircleTransformation(mContext)).crossFade().error(
                    R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar)

            if (TextUtils.isEmpty(obj.squareTopicTitle)) {
                mBinding.tvTopic.text = "#随便聊聊"
            } else {
                mBinding.tvTopic.text = "#${obj.squareTopicTitle}"
            }

            mBinding.textView21.text = obj.nickname
            mBinding.textView63.text = obj.userLevel
            mBinding.textView65.text = obj.commentContent
            mBinding.txtTime.text = DateUtils.getStrTime("MM月dd日 HH:mm", obj.commentTimestamp);//yyyy/MM/dd HH:mm

            //加精
            mBinding.ivJing.visibility = if (obj.commentIsHot == 1) View.VISIBLE else View.INVISIBLE

            mBinding.txtComment.text = "${obj.commentReplyNum}"

            /**
             * 赞信息
             */
            mBinding.txtLike.text = "+${obj.commentAgreeNum}"
            mBinding.ivLike.setImageResource(if (obj.agree != 0) R.drawable.ic_dynamic_like_ok else R.drawable.ic_dynamic_like)
            mBinding.txtLike.setTextColor(if (obj.agree != 0) Color.parseColor("#FF715F") else Color.parseColor("#7F8D9C"))

            /**
             * 图片信息
             */
            if (obj.commentImage?.size ?: 0 > 0) {
                mBinding.rvPhoto.visibility = View.VISIBLE
                mBinding.rvPhoto.layoutManager = GridLayoutManager(mContext, 3)
                mBinding.rvPhoto.adapter = PlazaPhotoAdapter(mContext, obj.commentImage, obj.squareTopicCommentId)
                mBinding.rvPhoto.setHasFixedSize(true)
                mBinding.rvPhoto.isNestedScrollingEnabled = false
            } else {
                mBinding.rvPhoto.visibility = View.GONE
            }

            mBinding.tvTip.visibility = View.GONE
            mBinding.tvTip2.visibility = View.GONE
            if (_type == 0 && position == 0) {
                var int = PreferenceUtil.getInt("plaza_gudie_num", 0)
                val time = PreferenceUtil.getLong("plaza_gudie_time", 0)
                if (System.currentTimeMillis() - time > 18 * 60 * 60 * 1000) {
                    int = 0
                }
                if (int < 3) {
                    PreferenceUtil.commitInt("plaza_gudie_num", int + 1)
                    PreferenceUtil.commitLong("plaza_gudie_time", System.currentTimeMillis())
                    guide(mBinding.tvTip2)
                }
            }
        }

        fun onLike(view: View, obj: PlazaCardResult.PlazaTopic) {
            if (obj.agree == 0) netLike(obj)
            EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_like", obj.userId?:""))
        }

        fun onPosted(view: View, obj: PlazaCardResult.PlazaTopic) {
            PlazaPostedActivity.start(view.context, obj.squareTopicId, obj.squareTopicTitle)
            EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_post2", obj.userId?:""))
        }

        fun onComments(view: View, obj: PlazaCardResult.PlazaTopic) {
            CommentsActivity.launch(view.context, obj.squareTopicCommentId,obj.squareTopicId)
            EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_coment", obj.userId?:""))
        }

        fun onAvatar(view: View, obj: PlazaCardResult.PlazaTopic) {
            PlazaSpaceActivity.start(view.context,obj.userId?:"" , obj.squareTopicId)
        }

        fun onTopic(view: View, obj: PlazaCardResult.PlazaTopic) {
            if (obj.squareTopicId != null) {
                PlazaTopicActivity.start(view.context, obj.squareTopicId)
            }
        }

        /**
         * 显示引导
         */
        private fun guide(view: View) {
            view.visibility = View.VISIBLE

            //动画
            val toFloat = DisplayUtils.dp2px(mBinding.root.context, 10f).toFloat()
            var objectAnimator = ObjectAnimator.ofFloat(view, "translationY", toFloat)
            objectAnimator.setDuration(1000);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                objectAnimator.setAutoCancel(true)
            }
            objectAnimator.start()
        }

        private fun netLike(obj: PlazaCardResult.PlazaTopic) {
            if (obj.squareTopicCommentId == null) return

            val subscribe = mDataRepository.postTopicCommentAgree(mDataRepository.getUserid()!!, obj.squareTopicId, obj.squareTopicCommentId)
                    .subscribe {
                        if (it.errorCode == 200) {
                            obj.agree = 1
                            obj.commentAgreeNum += 1
                            mBinding.ivLike.setImageResource(R.drawable.ic_dynamic_like_ok)
                            mBinding.txtLike.text = "+${obj.commentAgreeNum}"
                            mBinding.txtLike.setTextColor(Color.parseColor("#FF715F"))
                        } else {
                            Toast.makeText(niceChatContext(), it.errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
            EventTrackingUtils.joinPoint(EventBeans("ck_square_like", obj.userId?:""))
        }
    }

}