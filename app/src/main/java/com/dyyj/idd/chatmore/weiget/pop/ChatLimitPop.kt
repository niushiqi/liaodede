package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopChatLimitBinding
import com.dyyj.idd.chatmore.viewmodel.InviteActionViewModel

class ChatLimitPop(context: Context) : BaseTipPop<PopChatLimitBinding, InviteActionViewModel>(context) {
    override fun onLayoutId(): Int {
        return R.layout.pop_chat_limit
    }

    override fun onViewModel(): InviteActionViewModel {
        return InviteActionViewModel()
    }

    fun show(view: View, message: String) {
        mBinding.txtTime.text = message
        isOutsideTouchable = true
        isTouchable = true
        showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }

}