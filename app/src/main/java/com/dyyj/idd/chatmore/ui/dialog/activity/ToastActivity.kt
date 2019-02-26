package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityToastBinding
import com.dyyj.idd.chatmore.viewmodel.TaostViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/26
 * desc   : 领取奖励
 */
class ToastActivity : BaseActivity<ActivityToastBinding, TaostViewModel>() {
  companion object {
    const val REWARDCOIN = "rewardCoin"
    const val REWARDSTONE = "rewardStone"
    const val REWARDCASH = "rewardCash"
    fun start(context: Context, rewardCoin: Double?, rewardStone: Double?, rewardCash: Double?) {
      val intent = Intent(context, ToastActivity::class.java)
      intent.putExtra(REWARDCOIN, rewardCoin)
      intent.putExtra(REWARDSTONE, rewardStone)
      intent.putExtra(REWARDCASH, rewardCash)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_toast
  }

  override fun onViewModel(): TaostViewModel {
    return TaostViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initView()
  }

  fun getRewardCoin() = intent.getDoubleExtra(REWARDCOIN,0.0)
  fun getRewardStone() = intent.getDoubleExtra(REWARDSTONE,0.0)
  fun getRewardCash() = intent.getDoubleExtra(REWARDCASH,0.0)

  fun initView() {
    mBinding.rewardCoin = getRewardCoin()
    mBinding.rewardStone = getRewardStone()
    mBinding.rewardCash = getRewardCash()


    mBinding.goldTv.text = "+${getRewardCoin()}"
    mBinding.stoneTv.text = "+${getRewardStone()}"
    mBinding.moneyTv.text = "+${getRewardCash()}"

    setTextFont(mBinding.goldTv)
    setTextFont(mBinding.stoneTv)
    setTextFont(mBinding.moneyTv)
    timberTime()
  }

  fun timberTime() {
    val count: Long = 2
    val subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置2延迟，每隔一秒发送一条数据
        .take(count + 1) //设置循环11次
        .map { count - it }.observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
        .subscribe({

                   }, {}, {
                     finish()
                   })
    mViewModel.mCompositeDisposable.add(subscribe)
  }
}