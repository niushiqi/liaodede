package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopFriendRequestBinding
import com.dyyj.idd.chatmore.model.mqtt.result.FriendRequestResult
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.viewmodel.FriendRequestPopViewModel

/**
 * 添加好友
 */
class FriendRequestPop(context: Context?) : BaseTipPop<PopFriendRequestBinding, FriendRequestPopViewModel>(context) {

    open fun initData(obj: FriendRequestResult) {
        mViewModel.mData = obj
        startTime()

        mBinding.titleTxt.text = (obj.nickname + "申请添加你为好友，是否同意？")
        mBinding.actionTxt.setOnClickListener {
            mViewModel.netDoFriendRequest()
            dismiss()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.pop_friend_request
    }

    override fun onViewModel(): FriendRequestPopViewModel {
        return FriendRequestPopViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = DisplayUtils.dp2px(context, 65.toFloat())
    }

    open fun show(view: View) {
        showAtLocation(view, Gravity.TOP,0, DeviceUtils.dp2px(view.resources, 92F).toInt())
    }

}