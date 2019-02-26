package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogRangAwardV2Binding
import com.dyyj.idd.chatmore.model.mqtt.result.RangAwardResult
import com.dyyj.idd.chatmore.utils.AnimationUtils
import com.dyyj.idd.chatmore.utils.StatusBarUtilV2
import com.dyyj.idd.chatmore.viewmodel.RangAwardViewModelV2
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/08
 * desc   : 随机奖励 V2
 */
class RangAwardActivityV2 : BaseActivityV2<DialogRangAwardV2Binding, RangAwardViewModelV2>(), MyFrameAnimation.OnFrameAnimationListener {


  companion object {
    const val REWARDID = "data"
    fun start(context: Context, data: RangAwardResult) {
      val intent = Intent(context, RangAwardActivityV2::class.java)
      intent.putExtra(REWARDID, data)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.dialog_rang_award_v2
  }

  override fun onViewModel(): RangAwardViewModelV2 {
    return RangAwardViewModelV2()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //状态栏透明和间距处理
    StatusBarUtilV2.immersive(this)
    initView()
  }

  private fun getRangAwardResult() = intent.getParcelableExtra<RangAwardResult>(REWARDID)

  private fun initView() {
    AnimationUtils.start(this, mBinding.rangAwardIv, "ts", 1, 107, 65, this)
  }


  @SuppressLint("SetTextI18n")
  override fun onStartFrameAnimation() {
    val count: Long = 5
    val subscribe = Flowable.interval(2, 1, TimeUnit.SECONDS)//设置2延迟，每隔一秒发送一条数据
        .take(count + 1) //设置循环11次
        .map { count - it }.observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
        .subscribe({
                     mBinding.rangBtn.visibility = View.VISIBLE
                     mBinding.rangBtn.text = "领取 ${it}s"
                     if (it.toInt() == 5) {
                       setRangWaredListener()
                     }

                   }, {}, {
                     mBinding.rangBtn.visibility = View.GONE
                     mBinding.rangAwardIv.visibility = View.GONE
                     onBackPressed()
                   })
    mViewModel.mCompositeDisposable.add(subscribe)
    mBinding.rangAwardIv.visibility = View.VISIBLE
  }

  override fun onEnd() {
  }

  /**
   * 随机奖励事件设置
   */
  private fun setRangWaredListener() {
    mBinding.rangBtn.setOnClickListener {

      //领取奖励
      getRangAwardResult()?.rewardId?.let {
        mViewModel.netGetReward(it)
      }

      //打开随机奖励Toast
      ToastActivity.start(this, getRangAwardResult()?.rewardCoin, getRangAwardResult()?.rewardStone,
                          getRangAwardResult()?.rewardCash)
      mBinding.rangBtn.visibility = View.GONE
      mBinding.rangAwardIv.visibility = View.GONE
    }

    mBinding.rangAwardIv.setOnClickListener {

      //领取奖励
      getRangAwardResult()?.rewardId?.let {
        mViewModel.netGetReward(it)
      }

      //打开随机奖励Toast
      ToastActivity.start(this, getRangAwardResult()?.rewardCoin, getRangAwardResult()?.rewardStone,
                          getRangAwardResult()?.rewardCash)
      mBinding.rangBtn.visibility = View.GONE
      mBinding.rangAwardIv.visibility = View.GONE
      onBackPressed()

    }
  }
}