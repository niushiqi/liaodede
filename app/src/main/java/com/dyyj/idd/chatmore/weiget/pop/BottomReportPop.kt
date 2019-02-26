package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.PopBottomReportBinding
import com.dyyj.idd.chatmore.viewmodel.SystemReportViewModel

class BottomReportPop : PopupWindow {
    val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
    var mBinding: PopBottomReportBinding? = null
    val mViewModel by lazy { SystemReportViewModel() }
    var context: Context? = null

    constructor(context: Context?) : super(context) {
        this.context = context
        initContentView()
    }

    private fun initContentView() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pop_bottom_report, null, false)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        mBinding?.root?.layoutParams = params
        setContentView(mBinding?.root)

//        animationStyle = R.style.pop_anim_style
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(android.R.color.transparent)!!))

        mBinding?.txtCancel?.setOnClickListener {
            dismiss()
        }
        mBinding?.txtReport?.setOnClickListener {

        }
    }

    fun show() {
        showAtLocation(mBinding?.root, Gravity.BOTTOM, 0, 0)
    }

}