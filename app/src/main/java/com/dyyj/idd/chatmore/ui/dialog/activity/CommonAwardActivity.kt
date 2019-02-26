package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogCommonAwardBinding
import com.dyyj.idd.chatmore.viewmodel.CommonAwardViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/17
 * desc   : 固定奖励
 */
class CommonAwardActivity:BaseActivityV2<DialogCommonAwardBinding, CommonAwardViewModel>() {

  companion object {
    const val TYPE_GOLD = "gold"
    const val TYPE_STONE = "stone"
    const val TYPE_MONEY = "money"
    const val TYPE_REWARDID = "rewardId"
    fun start(context: Context, gold: Double?, stone: Double?, money: Double?, rewardId: String?) {
      val intent = Intent(context, CommonAwardActivity::class.java)
      intent.putExtra(TYPE_GOLD, gold)
      intent.putExtra(TYPE_STONE, stone)
      intent.putExtra(TYPE_MONEY, money)
      intent.putExtra(CommonAwardActivity.TYPE_REWARDID, rewardId)
      context.startActivity(intent)
    }
  }
  override fun onLayoutId(): Int {
    return R.layout.dialog_common_award
  }

  override fun onViewModel(): CommonAwardViewModel {
    return CommonAwardViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateEvenbus(this)
    initView()
  }

  override fun onDestroy() {
    super.onDestroy()
    onDestryEvenbus(this)
  }

  /**
   * 金币
   */
  private fun getGold() = intent.getDoubleExtra(TYPE_GOLD, 0.0)

  /**
   * 魔石
   */
  private fun getStone() = intent.getDoubleExtra(TYPE_STONE, 0.0)

  /**
   * 现金
   */
  private fun getMoney() = intent.getDoubleExtra(TYPE_MONEY, 0.0)

  @SuppressLint("SetTextI18n")
  private fun initView() {

    mBinding.itemCl.setOnClickListener { onBackPressed() }
    mBinding.closeIv.setOnClickListener { onBackPressed() }
    mBinding.okBtn.setOnClickListener { onBackPressed() }
    mBinding.okBtn.setOnClickListener { intent.getStringExtra(TYPE_REWARDID)?.let {
      mViewModel.netGetReward(it)
    } }

    mBinding.gold = getGold()
    mBinding.stone = getStone()
    mBinding.money = getMoney()

    mBinding.goldTv.text = "+${getGold()}"
    mBinding.stoneTv.text = "+${getStone()}"
    mBinding.moneyTv.text = "+${getMoney()}"

    setTextFont(mBinding.goldTv)
    setTextFont(mBinding.stoneTv)
    setTextFont(mBinding.moneyTv)
  }

  override fun onResume() {
    super.onResume()
    startTime()
  }

  @SuppressLint("SetTextI18n")
  private fun startTime() {
    var count: Long = 3
    val subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
        .take(count + 1) //设置循环11次
        .map { count - it }.doOnSubscribe {}

        .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
        .subscribe({
                     mBinding.timeTv.text = "${it}s后消失"
                   }, {}, {
                     finish()
                   })
    mViewModel.mCompositeDisposable.add(subscribe)
  }

  @Subscribe
  fun onRewardVM(obj: CommonAwardViewModel.RewardVM) {
    finish()
  }
}