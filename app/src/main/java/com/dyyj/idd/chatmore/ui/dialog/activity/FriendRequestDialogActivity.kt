package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivityFriendRequestDialogBinding
import com.dyyj.idd.chatmore.model.mqtt.result.FriendRequestResult
import com.dyyj.idd.chatmore.viewmodel.FriendRequestViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class FriendRequestDialogActivity : BaseActivityV2<ActivityFriendRequestDialogBinding, FriendRequestViewModel>() {

    companion object {
        const val REQUESTID: String = "requestid"
        fun start(context: Context, obj: FriendRequestResult) {
            val intent = Intent(context, FriendRequestDialogActivity::class.java)
            intent.putExtra(REQUESTID, obj.requestId)
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
        startTime()
        mBinding.actionTxt.setOnClickListener {
//            MessageSystemActivity.start(this@FriendRequestDialogActivity, 2)
            mViewModel.doFriendRequest(intent.getStringExtra(REQUESTID))
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTime() {
        var count: Long = 3
        val subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take(count + 1) //设置循环11次
                .map { count - it }.doOnSubscribe {}

                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({}, {}, {
                    finish()
                })
        mViewModel.mCompositeDisposable.add(subscribe)
    }
}
