package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopPublishBinding
import com.dyyj.idd.chatmore.viewmodel.PublishPopViewModel

class PublishPop(context: Context) : BaseTipPop<PopPublishBinding, PublishPopViewModel>(context) {

    interface IPublishListener {
        fun onPublish(exit: Boolean)
    }

    private var listener: IPublishListener? = null

    override fun onLayoutId(): Int {
        return R.layout.pop_publish
    }

    override fun onViewModel(): PublishPopViewModel {
        return PublishPopViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    fun show(view: View, listener: IPublishListener) {
        mBinding.txtLeft.setOnClickListener {
            listener?.let {
                listener.onPublish(true)
            }
        }
        mBinding.txtRight.setOnClickListener {
            listener?.let {
                listener.onPublish(false)
            }
        }
        isOutsideTouchable = true
        isTouchable = true
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(android.R.color.transparent)!!))
        showAtLocation(view, Gravity.CENTER, 0, 0)
    }

}