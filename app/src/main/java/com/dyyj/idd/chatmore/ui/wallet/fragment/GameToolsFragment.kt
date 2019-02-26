package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentGameToolsBinding
import com.dyyj.idd.chatmore.ui.user.activity.ShopActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.MyMatchCardActivity
import com.dyyj.idd.chatmore.viewmodel.GameToolsInFrgViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/19
 * desc   : 道具
 */
class GameToolsFragment : BaseFragment<FragmentGameToolsBinding, GameToolsInFrgViewModel>() {
  override fun onLayoutId(): Int {
    return R.layout.fragment_game_tools
  }

  override fun onViewModel(): GameToolsInFrgViewModel {
    return GameToolsInFrgViewModel()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    onCreateEvenBus(this)
    mViewModel.ownPropNum()
    initListener()
  }

  private fun initListener() {
    mBinding.tvBuyMagicBtn.setOnClickListener { ShopActivity.start(mActivity,0) }
    mBinding.tvBuyCardBtn.setOnClickListener { ShopActivity.start(mActivity,2) }
    mBinding.tvBuyPeaBtn.setOnClickListener { ShopActivity.start(mActivity,1) }

    mBinding.lookCard.setOnClickListener {
      MyMatchCardActivity.start(mActivity)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    onDestryEvenBus(this)
  }

  @Subscribe
  fun onGameToolsVM(obj: GameToolsInFrgViewModel.GameToolsVM) {
    if (obj.isSuccess) {
      obj.obj?.let {
        mBinding.tvMagicNum.text = it.stone.toString()

        val sp = SpannableString(it.matchCard.toString() + "张")
        sp.setSpan(AbsoluteSizeSpan(20, true), it.matchCard.toString().length, it.matchCard.toString().length + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        mBinding.tvCardNum.text = sp
        mBinding.tvPeaNum.text = it.deDou.toString()
      }
    }
  }

}