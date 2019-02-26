package com.dyyj.idd.chatmore.ui.adapter

import android.app.Activity
import android.content.ComponentName
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseChatAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemChatMessageTimeBinding
import com.dyyj.idd.chatmore.databinding.ItemSystemMessageImageBinding
import com.dyyj.idd.chatmore.databinding.ItemSystemMessageTextBinding
import com.dyyj.idd.chatmore.ui.task.activity.SystemMessageActivity
import com.dyyj.idd.chatmore.ui.user.activity.AppUserTutorialActivity
import com.dyyj.idd.chatmore.ui.user.activity.LevelUpgradeTutorialActivity
import com.dyyj.idd.chatmore.ui.user.activity.MakeMoneyTutorialActivity
import com.dyyj.idd.chatmore.utils.DateFormatter
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.utils.SpanStringUtils
import com.dyyj.idd.chatmore.viewmodel.SystemMessageViewModel
import com.gt.common.gtchat.extension.getExtMap
import com.hyphenate.chat.EMMessage
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*

/**
 * Created by wangbin on 2018/12/9.
 */
class SystemMessageAdapter : BaseChatAdapterV2() {

    companion object {
        const val TYPE_TIME = 1
        const val TYPE_TEXT = 2
        const val TYPE_IMAGE = 3
    }

    override fun getItemViewType(position: Int): Int {
        val item = getList()[position].item
        return when (item) {
            is Date -> TYPE_TIME
            is EMMessage -> {
                val type = item.getExtMap("type")
                when (type) {
                    "1" -> TYPE_TEXT
                    "2" -> TYPE_IMAGE
                    else -> TYPE_TEXT
                }
            }
            else -> TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TIME -> ViewHolderTime(parent)
            TYPE_TEXT -> ViewHolderText(parent)
            TYPE_IMAGE -> ViewHolderImage(parent)
            else -> ViewHolderText(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getList()[position].item
        when (holder) {
            is ViewHolderTime -> holder.onBindViewHolder(item as Date, position)
            is ViewHolderText -> holder.onBindViewHolder(item as EMMessage, position)
            is ViewHolderImage -> holder.onBindViewHolder(item as EMMessage, position)
        }
    }

    /**
     * 时间
     */
    class ViewHolderTime(parent: ViewGroup?) : BaseViewHolder<Date, ItemChatMessageTimeBinding>(
            parent, R.layout.item_chat_message_time) {
        override fun onBindViewHolder(obj: Date, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.dateTv.text = DateFormatter.LongFormatTime(obj.time.toString())
        }
    }

    /**
     * 文字
     */
    class ViewHolderText(parent: ViewGroup?) : BaseViewHolder<EMMessage, ItemSystemMessageTextBinding>(
            parent, R.layout.item_system_message_text) {
        override fun onBindViewHolder(obj: EMMessage, position: Int) {
            super.onBindViewHolder(obj, position)
            var msg = obj.getExtMap("msg")
            var url_type = obj.getExtMap("url_type")
            var url_param = obj.getExtMap("url_param")

            mBinding.messageBtv.text = SpanStringUtils.getTextWithURLContent(msg, object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    if(url_type != "") {
                        if(url_type == "AppUserTutorialActivity") {
                            //跳转 聊得得怎么玩
                            AppUserTutorialActivity.start((mContext as SystemMessageActivity))
                        } else if (url_type == "MakeMoneyTutorialActivity") {
                            //跳转 怎么赚钱
                            MakeMoneyTutorialActivity.start((mContext as SystemMessageActivity))
                        } else if (url_type == "LevelUpgradeTutorialActivity") {
                            //跳转等级说明页
                            LevelUpgradeTutorialActivity.start((mContext as SystemMessageActivity))
                        } /*else if (url_type == "") {
                            //跳转话题本身
                        }*/
                    }
                }

                override fun updateDrawState(ds: TextPaint?) {
                    ds?.let {
                        it.color = Color.RED
                    }
                }
            })
            mBinding.messageBtv.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    /**
     * 图片
     */
    class ViewHolderImage(parent: ViewGroup?) : BaseViewHolder<EMMessage, ItemSystemMessageImageBinding>(
            parent, R.layout.item_system_message_image) {
        override fun onBindViewHolder(obj: EMMessage, position: Int) {
            super.onBindViewHolder(obj, position)

            //判断
            val tag = mBinding.messageBtv.getTag()
            //解决recyclerview viewholder复用问题
            if (tag != null && tag as Int != position) {
                mBinding.messageBtv.setImageDrawable(null)
                var params: ViewGroup.LayoutParams = mBinding.messageBtv.getLayoutParams()
                params.height = DisplayUtils.dp2px(mContext, 160.toFloat())
                params.width = DisplayUtils.dp2px(mContext, 160.toFloat())
            }
            Glide.with(mContext)
                    .load(obj.getExtMap("msg"))
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .centerCrop()
                    .into(object : SimpleTarget<GlideDrawable>() {
                        override fun onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation<in GlideDrawable>) {
                            //动态设置图片大小
                            var params: ViewGroup.LayoutParams = mBinding.messageBtv.getLayoutParams();
                            if (resource.intrinsicHeight > resource.intrinsicWidth) {
                                params.height = DisplayUtils.dp2px(mContext, 160.toFloat())
                                params.width = params.height * resource.intrinsicWidth / resource.intrinsicHeight
                            } else {
                                params.width = DisplayUtils.dp2px(mContext, 160.toFloat())
                                params.height = params.width * resource.intrinsicHeight / resource.intrinsicWidth
                            }
                            mBinding.messageBtv.setImageDrawable(resource)
                            mBinding.messageBtv.setTag(position)
                        }
                    })

            //点击放大图片
            mBinding.messageBtv.setOnClickListener {
                EventBus.getDefault().post(SystemMessageViewModel.ShowLargeImageVM(obj.getExtMap("msg")!!))
            }
        }
    }
}