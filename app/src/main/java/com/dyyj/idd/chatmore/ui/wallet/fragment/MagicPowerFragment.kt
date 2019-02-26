package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentMagicPowerBinding
import com.dyyj.idd.chatmore.ui.task.activity.TaskSystemActivity
import com.dyyj.idd.chatmore.viewmodel.MagicPowerViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/23
 * desc   : 魔石
 */
class MagicPowerFragment : BaseFragment<FragmentMagicPowerBinding, MagicPowerViewModel>() {
  override fun onLayoutId(): Int {
    return R.layout.fragment_magic_power
  }

  override fun onViewModel(): MagicPowerViewModel {
    return MagicPowerViewModel()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    mViewModel.netWalletCash()
    mBinding.btnTakeTask.setOnClickListener {
      TaskSystemActivity.start(mActivity)
    }
    mBinding.btnBuy.setOnClickListener { Toast.makeText(ChatApp.getInstance().applicationContext, "功能尚未开放，敬请期待", Toast.LENGTH_SHORT).show() }
  }


  override fun onStart() {
    super.onStart()
    onCreateEvenBus(this)
  }

  override fun onPause() {
    super.onPause()
    onDestryEvenBus(this)
  }

  @Subscribe
  fun onCashVM(obj: MagicPowerViewModel.CashVM) {
    obj.obj?.let {
      mBinding.priceTv.text = "${it.stone}"
    }
  }
}