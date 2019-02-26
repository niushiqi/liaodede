package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.PopContactBinding
import com.dyyj.idd.chatmore.viewmodel.NoWorkViewModel
import org.greenrobot.eventbus.EventBus

class ContactPop : PopupWindow {
    val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
    var mBinding: PopContactBinding? = null;
    val mViewModel by lazy { onViewModel() }
    var context: Context? = null

    constructor(context: Context?) : super(context) {
        this.context = context
        width = context?.resources?.displayMetrics?.widthPixels!!
        height = context?.resources?.displayMetrics?.heightPixels!!
        isOutsideTouchable = true
        isTouchable = true
        initContentView()
    }

    private fun initContentView() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), onLayoutId(), null, false)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mBinding?.root?.layoutParams = params
        setContentView(mBinding?.root)
        animationStyle = R.style.pop_anim_style
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(android.R.color.transparent)!!))
        mBinding?.root?.setOnTouchListener { v, event ->
            dismiss()
            return@setOnTouchListener true
        }
        mBinding?.txtChat?.setOnClickListener {
            EventBus.getDefault().post(FinishViewVM())
            dismiss()
        }
    }

    @LayoutRes
    fun onLayoutId(): Int {
        return R.layout.pop_contact
    }

    fun onViewModel(): NoWorkViewModel {
        return NoWorkViewModel()
    }

    class FinishViewVM()

}