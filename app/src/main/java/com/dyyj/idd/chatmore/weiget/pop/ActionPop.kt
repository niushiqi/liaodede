package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.PopActionBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.ActionPopViewModel
import org.greenrobot.eventbus.EventBus

class ActionPop : PopupWindow {
    val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
    var mBinding: PopActionBinding? = null
    val mViewModel by lazy { ActionPopViewModel() }
    var context: Context? = null

    constructor(context: Context?) : super(context) {
        this.context = context
        initContentView()
    }

    private fun initContentView() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pop_action, null, false)
//        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT)
//        mBinding?.root?.layoutParams = params
        setContentView(mBinding?.root)
        //Android 5.0 适配
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT

//        animationStyle = R.style.pop_anim_style
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(android.R.color.transparent)!!))

        mBinding?.llClose?.setOnClickListener {
//          if (!NotificationManagerCompat.from(context!!).areNotificationsEnabled()) {
//            val show = NotifactionPop(context!!).show(it, "好友发消息了,我们将立刻通知你", "不错过每一次畅聊","close")
//            if (show) return@setOnClickListener
//          }

            EventBus.getDefault().post(MainActivity.ChatCloseVM())
            dismiss()
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_hangup",(context as MainActivity).mFromUserId))
        }

        mBinding?.llReport?.setOnClickListener {
            EventBus.getDefault().post(MainActivity.ChatReportVM())
            dismiss()
        }
    }

    fun show(view: View?) {
//        showAsDropDown(view, 0, -Utils.convertDpToPixel(60f).toInt())
        showAtLocation(view, Gravity.RIGHT or Gravity.BOTTOM, 0, 0)
    }

}
