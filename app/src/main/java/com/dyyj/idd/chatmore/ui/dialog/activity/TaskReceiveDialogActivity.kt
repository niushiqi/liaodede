package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivityFriendRequestDialogBinding
import com.dyyj.idd.chatmore.ui.main.fragment.OpenCallFragment.Companion.startTime
import com.dyyj.idd.chatmore.utils.RxTimerUtil
import com.dyyj.idd.chatmore.viewmodel.FriendRequestViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class TaskReceiveDialogActivity : BaseActivityV2<ActivityFriendRequestDialogBinding, FriendRequestViewModel>() {

    companion object {
        private const val TITLE: String = "title"
        private const val TASK_ID: String = "taskId"
        fun start(context: Context, title: String, taskId: String) {
            val intent = Intent(context, TaskReceiveDialogActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(TASK_ID, taskId)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_friend_request_dialog
    }

    override fun onViewModel(): FriendRequestViewModel {
        return FriendRequestViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {

        mBinding.itemCl.setOnClickListener { finish() }
        mBinding.tvContent.text = intent.getStringExtra(TITLE)
        mBinding.actionTxt.setOnClickListener {

            mViewModel.goRewardTask(intent.getStringExtra(TASK_ID))
            finish()
        }


        RxTimerUtil.timer(5000, { finish() })
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTimerUtil.cancel()
    }

}
