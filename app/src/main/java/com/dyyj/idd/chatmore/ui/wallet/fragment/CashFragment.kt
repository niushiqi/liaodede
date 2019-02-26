package com.dyyj.idd.chatmore.ui.wallet.fragment

import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentCashBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WithdrawActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.CashViewModel
import com.dyyj.idd.chatmore.viewmodel.GoldViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/23
 * desc   : 现金
 */
class CashFragment : BaseFragment<FragmentCashBinding, CashViewModel>() {
  override fun onLayoutId(): Int {
    return R.layout.fragment_cash
  }

  override fun onViewModel(): CashViewModel {
    return CashViewModel()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    mViewModel.netWalletCash()
    initView()
  }

  override fun onResume() {
    super.onResume()
  }

  override fun onStart() {
    super.onStart()
//    onCreateEvenBus(this)
  }

  override fun onPause() {
    super.onPause()
//    onDestryEvenBus(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    onDestryEvenBus(this)
  }

  fun initView() {
    onCreateEvenBus(this)
    mBinding.withdrawBtn.setOnClickListener {
      WithdrawActivity.start(mActivity,"social")
      EventTrackingUtils.joinPoint(EventBeans("ck_wallet_withdraw_cash1",""))
    }
    mBinding.withdrawBtn2.setOnClickListener {
      WithdrawActivity.start(mActivity,"invite")
      EventTrackingUtils.joinPoint(EventBeans("ck_wallet_withdraw_cash2",""))
    }
    mBinding.ivBg2.setOnClickListener { InviteNewActivity2.start(mActivity) }
//    mBinding.txtTixianshuoming.setOnClickListener { CashNoticeActivity.start(mActivity) }
  }

//  fun initRecycleView(list: List<CashSumaryResult.Data.CashHistory>) {
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

  @Subscribe
  fun onCashVM(obj: CashViewModel.CashVM) {
    obj.obj?.let {
      mBinding.model = it
//      mBinding.balanceValueTv.text = "¥ ${it.cash}"
//      mBinding.totalMoneyValueTv.text = "¥ ${it.totalGetCash}"
//      mBinding.currentMoneyValueTv.text = "¥ ${it.todayGetCash}"
      mBinding.balanceValueTv.text = "${it.socialNum}"
      mBinding.balanceValueTv2.text = "${it.inviteNum}"
      mBinding.tvSocialTip.text = "${it.socialTip}"
      mBinding.tvInviteTip.text = "${it.inviteTip}"

//      it.cashHistory = (1..10).map {
//        CashSumaryResult.Data.CashHistory("consumeValue$it", "consumeMark$it",
//                                          Date().time.toString())
//      }
//      it.cashHistory?.let {
//        initRecycleView(it)
//      }

    }

  }

  @Subscribe
  fun onRefreshVM(obj: GoldViewModel.RefreshGoldVM) {
    mViewModel.netWalletCash()
  }
}