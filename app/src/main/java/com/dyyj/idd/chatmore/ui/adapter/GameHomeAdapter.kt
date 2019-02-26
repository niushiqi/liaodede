package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemGameHomeBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import org.greenrobot.eventbus.EventBus

class GameHomeAdapter : BaseAdapterV2<Any>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    /**
     * 现金明细
     */
    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<Any, ItemGameHomeBinding>(
            viewGroup, R.layout.item_game_home) {
        override fun onBindViewHolder(obj: Any, position: Int) {
            super.onBindViewHolder(obj, position)
            if (position == 0) {
                val param1 = RecyclerView.LayoutParams(DeviceUtils.dp2px(mContext.resources, 140f).toInt(), RelativeLayout.LayoutParams.MATCH_PARENT)
                param1.leftMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.rightMargin = DeviceUtils.dp2px(mContext.resources, 5f).toInt()
                param1.topMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.bottomMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                mBinding.root.layoutParams = param1
                mBinding.root.setBackgroundResource(R.drawable.bg_game_item_1)
                mBinding.ivGameIcon.setImageResource(R.drawable.ic_game_icon_1)
                mBinding.txtTitle1.text = "骰子比大小"
                mBinding.txtTitle2.text = "敢不敢玩一局!赢的人拿走对方10金币"
                mBinding.txtTitle2.visibility = View.VISIBLE
                mBinding.ivClose.visibility = View.GONE
                mBinding.root.setOnClickListener {
                    EventBus.getDefault().post(StartGameVM(0))
                    EventTrackingUtils.joinPoint(EventBeans("ck_treepage_startgame",(mContext as MainActivity).mFromUserId))
                }
            } else if (position == 1) {
                val param1 = RecyclerView.LayoutParams(DeviceUtils.dp2px(mContext.resources, 140f).toInt(), RelativeLayout.LayoutParams.MATCH_PARENT)
                param1.leftMargin = DeviceUtils.dp2px(mContext.resources, 5f).toInt()
                param1.rightMargin = DeviceUtils.dp2px(mContext.resources, 5f).toInt()
                param1.topMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.bottomMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                mBinding.root.layoutParams = param1
                mBinding.root.setBackgroundResource(R.drawable.bg_game_item_1)
                mBinding.ivGameIcon.setImageResource(R.drawable.ic_game_icon_2)
                mBinding.txtTitle1.text = "拼手速"
                mBinding.txtTitle2.text = "7秒内点击次数多的人获胜。"
                mBinding.txtTitle2.visibility = View.VISIBLE
                mBinding.ivClose.visibility = View.GONE
                mBinding.root.setOnClickListener {
                    EventBus.getDefault().post(StartGameVM(1))
                }
            } else if (position == 2) {
                val param1 = RecyclerView.LayoutParams(DeviceUtils.dp2px(mContext.resources, 140f).toInt(), RelativeLayout.LayoutParams.MATCH_PARENT)
                param1.leftMargin = DeviceUtils.dp2px(mContext.resources, 5f).toInt()
                param1.rightMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.topMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.bottomMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                mBinding.root.layoutParams = param1
                mBinding.root.setBackgroundResource(R.drawable.bg_game_item_2)
                mBinding.ivGameIcon.setImageResource(R.drawable.ic_game_icon_4)
                mBinding.txtTitle1.text = "陪看恐怖片"
                mBinding.txtTitle2.visibility = View.GONE
                mBinding.ivClose.visibility = View.VISIBLE
            } else if (position == 3) {
                val param1 = RecyclerView.LayoutParams(DeviceUtils.dp2px(mContext.resources, 140f).toInt(), RelativeLayout.LayoutParams.MATCH_PARENT)
                param1.leftMargin = DeviceUtils.dp2px(mContext.resources, 5f).toInt()
                param1.rightMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.topMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                param1.bottomMargin = DeviceUtils.dp2px(mContext.resources, 10f).toInt()
                mBinding.root.layoutParams = param1
                mBinding.root.setBackgroundResource(R.drawable.bg_game_item_2)
                mBinding.ivGameIcon.setImageResource(R.drawable.ic_game_icon_3)
                mBinding.txtTitle1.text = "陪听歌"
                mBinding.txtTitle2.visibility = View.GONE
                mBinding.ivClose.visibility = View.VISIBLE
            }
        }
    }

    class StartGameVM(val type: Int)
}