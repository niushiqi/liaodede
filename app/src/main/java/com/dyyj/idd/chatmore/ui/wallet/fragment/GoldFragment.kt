package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentGoldBinding
import com.dyyj.idd.chatmore.ui.user.activity.ConvertGoldActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.viewmodel.GoldViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/23
 * desc   : 金币
 */
class GoldFragment:BaseFragment<FragmentGoldBinding, GoldViewModel>() {
  override fun onLayoutId(): Int {
    return R.layout.fragment_gold
  }

  override fun onViewModel(): GoldViewModel {
    return GoldViewModel()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    mViewModel.netWalletCash()
    initListener()
  }

  private fun initListener() {
    mBinding.btnConvert.setOnClickListener {
      ConvertGoldActivity.start(activity!!)
    }
    mBinding.ivZhuanqian.setOnClickListener {
      InviteNewActivity2.start(mActivity)
    }
    mBinding.ivBg2.setOnClickListener {
      InviteNewActivity2.start(mActivity)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    onCreateEvenBus(this)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onDestroyView() {
    onDestryEvenBus(this)
    super.onDestroyView()
  }

//  fun initRecycleView(list: List<CoinSummaryResult.Data.CoinHistory>) {
//    //设置线
//    val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
//        ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
//        R.dimen.item_decoration_2px).build()
//    mViewModel.getAdapter().initData(list)
//    mBinding.recyclerview.addItemDecoration(decoration)
//    mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
//    mBinding.recyclerview.layoutManager = LinearLayoutManager(activity)
//    mBinding.recyclerview.adapter = mViewModel.getAdapter()
//
//  }

  @SuppressLint("SetTextI18n")
  @Subscribe
  fun onCashVM(obj: GoldViewModel.CashVM) {
    obj.obj?.let {
      mBinding.model = it
      mBinding.moshiTv.text = "${it.coin}"
//      mBinding.balanceValueTv.text = "${it.coin}"
//      mBinding.totalMoneyValueTv.text = "${it.totalGetCoin}"
//      mBinding.currentMoneyValueTv.text = "${it.todayGetCoin}"

//      it.coinHistory?.let {
//        initRecycleView(it)
//      }
    }

  }

  @Subscribe
  fun onRefreshVM(obj: GoldViewModel.RefreshGoldVM) {
    mViewModel.netWalletCash()
  }


}