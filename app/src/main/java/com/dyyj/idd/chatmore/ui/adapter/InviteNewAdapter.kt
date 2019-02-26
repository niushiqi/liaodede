package com.dyyj.idd.chatmore.ui.adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemInviteNewImageBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteGameActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.utils.EventTrackingUtils

class InviteNewAdapter : BaseAdapterV2<Any>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<Any, ItemInviteNewImageBinding>(
            viewGroup, R.layout.item_invite_new_image) {
        override fun onBindViewHolder(obj: Any, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.ivItem.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, mBinding.root.resources.displayMetrics.widthPixels / 5 * 2)
            if (position == 0) {
                mBinding.ivItem.setImageResource(R.drawable.banner2)
            } else if (position == 1) {
                mBinding.ivItem.setImageResource(R.drawable.banner3)
            }
            mBinding.root.setOnClickListener {
                if (position == 0) {
                    InviteNewActivity2.start(mContext)
                    EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1",""))
                } else {
                    InviteGameActivity.start(mContext)
                    EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite2",""))
                }
            }
        }
    }

}