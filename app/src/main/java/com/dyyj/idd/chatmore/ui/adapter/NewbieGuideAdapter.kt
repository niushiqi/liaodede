package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemInviteGameBinding
import com.dyyj.idd.chatmore.databinding.ItemMeNewbieGuideBinding
import com.dyyj.idd.chatmore.ui.user.activity.AppUserTutorialActivity
import com.dyyj.idd.chatmore.ui.user.activity.LevelUpgradeTutorialActivity
import com.dyyj.idd.chatmore.ui.user.activity.MakeMoneyTutorialActivity
import com.dyyj.idd.chatmore.ui.user.activity.NewbieGuideActivity

/**
 * Created by wangbin on 2019/1/19.
 */
class NewbieGuideAdapter: BaseAdapterV2<Any>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class ViewHolder(viewGroup: ViewGroup) : BaseViewHolder<Any, ItemMeNewbieGuideBinding>(viewGroup, R.layout.item_me_newbie_guide) {
        override fun onBindViewHolder(obj: Any, position: Int) {
            super.onBindViewHolder(obj, position)
            if(obj.equals("ZenMeWan")) {
                Glide.with(mContext).load(R.drawable.img_me_guide1).asBitmap().error(
                        R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.iv)
                mBinding.iv.setOnClickListener {
                    AppUserTutorialActivity.start(mContext as NewbieGuideActivity)
                }
            } else if(obj.equals("TiXian")) {
                Glide.with(mContext).load(R.drawable.img_me_guide2).asBitmap().error(
                        R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.iv)
                mBinding.iv.setOnClickListener {
                    MakeMoneyTutorialActivity.start(mContext as NewbieGuideActivity)
                }
            } else if(obj.equals("ShengDengJi")) {
                Glide.with(mContext).load(R.drawable.img_me_guide3).asBitmap().error(
                        R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.iv)
                mBinding.iv.setOnClickListener {
                    LevelUpgradeTutorialActivity.start(mContext as NewbieGuideActivity)
                }
            }
        }
    }
}
