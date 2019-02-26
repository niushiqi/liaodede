package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivityRequestVideoDialogBinding
import com.dyyj.idd.chatmore.viewmodel.RequestVideoDialogViewModel

class RequestVideoDialogActivity : BaseActivityV2<ActivityRequestVideoDialogBinding, RequestVideoDialogViewModel>() {

    companion object {
        const val FROMUSERID = "userid"
        const val TOUSERID = "nickname"
        const val TALKID = "avatar"
        fun start(context: Context, fromUserId: String, toUserId: String, talkId: String) {
            val intent = Intent(context, RequestVideoDialogActivity::class.java)
            intent.putExtra(FROMUSERID, fromUserId)
            intent.putExtra(TOUSERID, toUserId)
            intent.putExtra(TALKID, talkId)
            context.startActivity(intent)
        }
    }

    override fun onViewModel(): RequestVideoDialogViewModel {
        return RequestVideoDialogViewModel()
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_request_video_dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        mBinding.declineTv.setOnClickListener {
            mViewModel.netResponseSwitchVideo(intent.getStringExtra(TALKID), intent.getStringExtra(FROMUSERID), intent.getStringExtra(TOUSERID), "2")
            finish()
        }
        mBinding.answerTv.setOnClickListener {
            mViewModel.netResponseSwitchVideo(intent.getStringExtra(TALKID), intent.getStringExtra(FROMUSERID), intent.getStringExtra(TOUSERID), "1")
            finish()
        }
    }
}
