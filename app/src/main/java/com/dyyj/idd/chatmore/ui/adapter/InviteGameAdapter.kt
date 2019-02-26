package com.dyyj.idd.chatmore.ui.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemGameTop1Binding
import com.dyyj.idd.chatmore.databinding.ItemInviteGameBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import org.greenrobot.eventbus.EventBus

class InviteGameAdapter : BaseAdapterV2<Any>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return TopViewHolder(parent)
        } else {
            return ViewHolder(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        } else {
            return 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        } else if (holder is TopViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<Any, ItemInviteGameBinding>(
            viewGroup, R.layout.item_invite_game) {
        override fun onBindViewHolder(obj: Any, position: Int) {
            super.onBindViewHolder(obj, position)
            if (position == 1) {
                mBinding.imageView47.setImageResource(R.drawable.ic_default_game_item)
                mBinding.textView49.text = "成语猜猜猜"
                mBinding.textView50.text = "猜成语，拿现金"
                mBinding.textView51.text = "敬请期待"
//                mBinding.textView51.setTextColor(Color.parseColor("#FF287E"))
//                mBinding.textView51.setBackgroundResource(R.drawable.bg_rect_solid_red_stroke)
//                mBinding.textView51.setOnClickListener {
////                    EventBus.getDefault().post(ShareGameVM())
//                }
                mBinding.textView51.setTextColor(Color.parseColor("#CBCBCB"))
                mBinding.textView51.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mBinding.textView51.setOnClickListener {  }
            } else if (position == 2) {
                mBinding.imageView47.setImageResource(R.drawable.ic_default_game_item2)
                mBinding.textView49.text = "词语比拼"
                mBinding.textView50.text = "小学生都会，难不住我"
                mBinding.textView51.text = "敬请期待"
                mBinding.textView51.setTextColor(Color.parseColor("#CBCBCB"))
                mBinding.textView51.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mBinding.textView51.setOnClickListener {  }
            } else if (position == 3) {
                mBinding.imageView47.setImageResource(R.drawable.ic_default_game_item3)
                mBinding.textView49.text = "朋友找人"
                mBinding.textView50.text = "美女帅哥在哪里？"
                mBinding.textView51.text = "敬请期待"
                mBinding.textView51.setTextColor(Color.parseColor("#CBCBCB"))
                mBinding.textView51.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mBinding.textView51.setOnClickListener {  }
            }
        }
    }

    inner class TopViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<Any, ItemGameTop1Binding>(
            viewGroup, R.layout.item_game_top_1) {
        override fun onBindViewHolder(obj: Any, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.likewan.setOnClickListener{
                EventBus.getDefault().post(ShareGameVM())
                EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite2_play",""))
            }
        }
    }

    class ShareGameVM()

}