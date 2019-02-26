package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogMatchingFailedBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.CancelMatchingViewModel
import com.dyyj.idd.chatmore.viewmodel.MatchingFailedViewModel
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.chat.adapter.EMACallManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/20
 * desc   : 匹配失败
 */
class MatchingFailedActivity : BaseActivityV2<DialogMatchingFailedBinding, MatchingFailedViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MatchingFailedActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_matching_failed
    }

    override fun onViewModel(): MatchingFailedViewModel {
        return MatchingFailedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEvenbus(this)
        mDataRepository.endCall()
        initListener()
    }

    private fun initListener() {

        mBinding.closeIv.setOnClickListener {
//            MainActivity.startOpenCall(this) //修改点击匹配页面中关闭按钮，弹出失败缘分就在下一秒页面，修改这里面关闭按钮未等同继续找按钮同等功能
            MainActivity.startOutgoing(this@MatchingFailedActivity)
            EventBus.getDefault().post(MatchRestartVM())
            finish()
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_timeout_close",""))
        }
        mBinding.okBtn.setOnClickListener {

            MainActivity.startOutgoing(this@MatchingFailedActivity)
            EventBus.getDefault().post(MatchRestartVM())
            finish()
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_timeout_rematch",""))
        }
        mBinding.cancelBtn.setOnClickListener {
//            mViewModel.netRestoreMatchingStone()
            mViewModel.endCall();
            mViewModel.netCancelMatching();// 修改点击不等待，先取消匹配池接口，在回调启动主界面

//            MainActivity.startOpenCall(this)
//            finish()
//            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_timeout_quit",""))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEvenbus(this)
    }

    @Subscribe
    fun onRestoreMatchingStoneVM(obj: MatchingFailedViewModel.RestoreMatchingStoneVM) {
//    closeProgressDialog()
        if (obj.isSuccess) {
            MainActivity.startOpenCall(this)
            finish()
        } else {
            niceToast("返还魔石")
        }
    }

    @Subscribe
    fun onCancelMatchingVM(obj: MatchingFailedViewModel.MatchingFailedVM) {
        if (obj.isSuccess) {
//      decline()
            MainActivity.startOpenCall(this)
            finish()
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_timeout_quit",""))
        }
    }

    class MatchRestartVM
}