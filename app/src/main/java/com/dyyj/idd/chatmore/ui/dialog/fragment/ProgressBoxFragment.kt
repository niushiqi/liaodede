package com.dyyj.idd.chatmore.ui.dialog.fragment

import android.os.Bundle
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.DialogProgressBoxBinding
import com.dyyj.idd.chatmore.viewmodel.ProgressBoxViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/09
 * desc   : 礼盒进度条
 */
class ProgressBoxFragment:BaseFragment<DialogProgressBoxBinding, ProgressBoxViewModel>() {

  override fun onLayoutId(): Int {
    return R.layout.dialog_progress_box
  }

  override fun onViewModel(): ProgressBoxViewModel {
    return ProgressBoxViewModel()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lazyLoad()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    initView()
  }

  private fun initView() {
    mBinding.loadingLav.setMinAndMaxProgress(0.50f, 0.51f)
  }
}