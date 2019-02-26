package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopPlazaTopicFocusBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import com.dyyj.idd.chatmore.weiget.pop.BaseTipPop
import org.greenrobot.eventbus.EventBus

/**
 * desc   : 关注话题列表 长按弹框
 */
open class PlazaTopicFocusPop(context: Context?) : BaseTipPop<PopPlazaTopicFocusBinding, EmptyAcitivityViewModel>(context) {

    var topic: PlazaTopicListResult.Topic? = null

    override fun onLayoutId(): Int {
        return R.layout.pop_plaza_topic_focus
    }

    override fun onViewModel(): EmptyAcitivityViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun onInitData() {
        super.onInitData()
//        onCreateEvenbus(this)
        // 设置点击窗口外边窗口消失
        isOutsideTouchable = true
        // 设置此参数获得焦点，否则无法点击
        isFocusable = true
    }

    private fun initListener() {

        mBinding.btnTop.text = if (topic!!.top != 0) "取消置顶" else "置顶"

        mBinding.btnClose.setOnClickListener { dismiss() }
        mBinding.btnTop.setOnClickListener {
            EventBus.getDefault().post(TopVM(topic!!))
            dismiss()
        }
        mBinding.btnFocus.setOnClickListener {
            EventBus.getDefault().post(FocusVM(topic!!))
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
//        onDestryEvenbus(this)
    }

    open fun show(view: View, obj: PlazaTopicListResult.Topic) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0)
        topic = obj

        initListener()
    }


    class TopVM(val data: PlazaTopicListResult.Topic)
    class FocusVM(val data: PlazaTopicListResult.Topic)

}