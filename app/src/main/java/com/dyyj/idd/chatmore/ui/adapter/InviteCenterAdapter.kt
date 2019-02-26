package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemInviteCenterBinding
import com.dyyj.idd.chatmore.model.network.result.MyTaskResult
import org.greenrobot.eventbus.EventBus

class InviteCenterAdapter : BaseAdapterV2<MyTaskResult.Data.MyTask>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    override fun getItemCount(): Int {
        return getList().size
    }

    /**
     * 现金明细
     */
    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<MyTaskResult.Data.MyTask, ItemInviteCenterBinding>(
            viewGroup, R.layout.item_invite_center) {
        override fun onBindViewHolder(obj: MyTaskResult.Data.MyTask, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.textView1.text = obj.invitedUsername
            mBinding.textView2.text = obj.invitedMobile
            mBinding.textView3.text = "${obj.rewardNum}元"
            mBinding.textView4.text = "${obj.totalRewardNum}元"
            if (obj.status == "1") {
                mBinding.textView5.visibility = View.VISIBLE
                mBinding.textView5.setOnClickListener {
//                    ShareUtils.shareWeb(mActivity, "http://www.baidu.com", "title", "des", false)
//                    EventBus.getDefault().post(ShowMessageToFriendVM(obj))
                    EventBus.getDefault().post(ShowInvitePopVM(obj))
                }
            } else {
                mBinding.textView5.visibility = View.INVISIBLE
            }
        }
    }

    class ShowInvitePopVM(val obj: MyTaskResult.Data.MyTask)
    class ShowMessageToFriendVM(val obj: MyTaskResult.Data.MyTask)

}