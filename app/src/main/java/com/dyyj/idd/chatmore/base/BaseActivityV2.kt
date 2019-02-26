package com.dyyj.idd.chatmore.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.ActivityBaseBinding
import kotlinx.android.synthetic.main.activity_base.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class BaseActivityV2<D : ViewDataBinding, out VM : ViewModel> : AppCompatActivity() {
  val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
  lateinit var mBinding: D
  lateinit var mBaseBinding: ActivityBaseBinding
  val mViewModel: VM by lazy { onViewModel() }
  private val mViewModelTag: Int?  by lazy { onViewModelTag() }
  var mToolbar: Toolbar? = null

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    initContentView()
    mToolbar = onToolbar()
    if (mToolbar != null) {
      setSupportActionBar(mToolbar)
    }
    onBindViewModel(mViewModelTag, mViewModel)
    onCreateEvenbus(this)
  }

  override fun onResume() {
    super.onResume()
    mViewModel.sendViewEventTrackingMessage("sw_" + this.javaClass.simpleName + "_in")
  }

  override fun onPause() {
    super.onPause()
    mViewModel.sendViewEventTrackingMessage("sw_" + this.javaClass.simpleName + "_out")
  }

  /**
   * 设置Activity @setContentView
   * 根布局套单个子Activity布局,使用统一状态
   * Loding/Error/Empty
   */
  private fun initContentView() {
    //根布局
    mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null,
                                           false)

    //child Activity传递上传的布局
    mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), onLayoutId(), null, false)
    // content
    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                             ViewGroup.LayoutParams.MATCH_PARENT)
    mBinding.root.layoutParams = params
    mBaseBinding.root.container.addView(mBinding.root)
    window.setContentView(mBaseBinding.root)
    window.setBackgroundDrawableResource(android.R.color.transparent)
  }


  override fun onDestroy() {
    super.onDestroy()
    //翻译ViewModel资源
    mViewModel.destroy()
    onDestryEvenbus(this)

  }

  /**
   * 绑定ViewModel
   */
  private fun onBindViewModel(tag: Int?, viewmodel: ViewModel) {
    if (tag != null) {
      mBinding.setVariable(tag, viewmodel)
    }
  }

  override fun onBackPressed() {
    BGAKeyboardUtil.closeKeyboard(this)
    this.finish()
    BGASwipeBackHelper.executeBackwardAnim(this)
  }

  open fun onDrawerLayout(): DrawerLayout? = null

  fun onCreateEvenbus(any: Any) {
    if (!EventBus.getDefault().isRegistered(any)) {
      EventBus.getDefault().register(any)
    }
  }

  fun onDestryEvenbus(any: Any) {
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(any)
    }
  }

  /**
   * 设置字体
   */
  fun setTextFont(textView: TextView) {
    val type = Typeface.createFromAsset(assets, "fonts/font1.ttf")
    textView.typeface = type
  }

  /**
   * Activity 布局文件
   */
  @LayoutRes
  abstract fun onLayoutId(): Int

  /**
   * 获取Toolbar
   */
  open fun onToolbar(): Toolbar? {
    return null
  }

  /**
   * 获取ViewModel类
   */
  abstract fun onViewModel(): VM

  /**
   * ViewModel类Tag
   */
  open fun onViewModelTag(): Int? {
    return null
  }

  class TestVM(val success: Boolean)

  @Subscribe
  fun OnTextSub(obj: TestVM) {

  }


}