package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityToastConnectAwardBinding
import com.dyyj.idd.chatmore.utils.RxTimerUtil
import com.dyyj.idd.chatmore.viewmodel.OpenCallViewModel
import com.dyyj.idd.chatmore.viewmodel.ToastConnectAwardViewModel
import com.gt.common.gtchat.extension.niceTextFont
import org.greenrobot.eventbus.EventBus


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/09/18
 * desc   : 接通呼叫奖励
 */
@SuppressLint("Registered")
class ToastConnectAwardActivity : BaseActivity<ActivityToastConnectAwardBinding, ToastConnectAwardViewModel>() {

    companion object {
        const val REWARDCOIN = "rewardCoin"
        const val REWARDSTONE = "rewardStone"
        const val REWARDCASH = "rewardCash"
        const val TITLE = "title"
        fun start(context: Context, title: String, rewardCoin: Double?, rewardStone: Double?, rewardCash: Double?) {
            val intent = Intent(context, ToastConnectAwardActivity::class.java)
            intent.putExtra(REWARDCOIN, rewardCoin)
            intent.putExtra(REWARDSTONE, rewardStone)
            intent.putExtra(REWARDCASH, rewardCash)
            intent.putExtra(TITLE, title)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_toast_connect_award
    }

    override fun onViewModel(): ToastConnectAwardViewModel {
        return ToastConnectAwardViewModel()
    }


    private fun getRewardCoin() = intent.getDoubleExtra(REWARDCOIN, 0.0)
    private fun getRewardStone() = intent.getDoubleExtra(REWARDSTONE, 0.0)
    private fun getRewardCash() = intent.getDoubleExtra(REWARDCASH, 0.0)
    private fun getTitleName() = intent.getStringExtra(TITLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    /**
     * 初始化view
     */
    @SuppressLint("SetTextI18n")
    fun initView() {
        val rewardCoin = getRewardCoin()
        val rewardStone = getRewardStone()
        val rewardCash = getRewardCash()

        mBinding.itemCl.setOnClickListener { finish() }

        mBinding.rewardCoin = rewardCoin
        mBinding.rewardStone = rewardStone
        mBinding.rewardCash = rewardCash


        mBinding.goldTv.text = "+$rewardCoin"
        mBinding.stoneTv.text = "+$rewardStone"
        mBinding.moneyTv.text = "+$rewardCash"

        mBinding.titleTv.text = getTitleName()

        mBinding.goldTv.niceTextFont()
        mBinding.stoneTv.niceTextFont()
        mBinding.moneyTv.niceTextFont()
        timberTime()

        //刷新主页信息
        EventBus.getDefault().post(OpenCallViewModel.RefreshMain(true))
    }

    /**
     * 定时关闭
     */
    private fun timberTime() {
        RxTimerUtil.timer(3000, { finish() })
//    val count: Long = 5
//    val subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置2延迟，每隔一秒发送一条数据
//        .take(count + 1) //设置循环11次
//        .map { count - it }.observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
//        .subscribe({
//
//                   }, {}, {
//                     finish()
//                   })
//    mViewModel.mCompositeDisposable.add(subscribe)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTimerUtil.cancel()
    }
}