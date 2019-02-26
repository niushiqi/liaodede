package com.dyyj.idd.chatmore.base

import android.app.ProgressDialog
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.fastjson.util.TypeUtils.getClass
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.ActivityBaseBinding
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/01
 * desc   : 基类Fragment
 */
open abstract class BaseFragment<D : ViewDataBinding, out VM : ViewModel> : Fragment() {

  val mActivity: AppCompatActivity by lazy { activity as AppCompatActivity }
  var mProgressDialog: ProgressDialog? = null
  lateinit var mBinding: D
  lateinit var mBaseBinding: ActivityBaseBinding
  open val mViewModel: VM by lazy { onViewModel() }
  val mDataRepository : DataRepository by lazy { ChatApp.getInstance().getDataRepository() }
  /**
   * Fragment是否显示
   */
  var mVisible = false

  /**
   * Fragment是否CreateView
   */
  var isCreateView = false

  /**
   * Fragment数据是否加载过
   */
  var isLazyLoad = false

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    setHasOptionsMenu(true)
    return initContentView(container)
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
   * 设置字体
   */
  fun setTextFont(textView: TextView) {
    val type = Typeface.createFromAsset(mActivity.assets, "fonts/font1.ttf")
    textView.typeface = type
  }

  override fun onDestroyView() {
    isCreateView = false
    super.onDestroyView()
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCreateView = true

    //如果当前Fragment是显示的,就加载
    if (mVisible) lazyLoad()
//    if (mVisible) {
//      lazyLoad()
//    }
  }

  /**
   * 初始化ContentView
   */
  private fun initContentView(container: ViewGroup?): View {

//    container?.setBackgroundResource(android.R.color.transparent)
//
//    mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.activity_base,
//                                           null, false)
//
//    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                             ViewGroup.LayoutParams.MATCH_PARENT)


    mBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), onLayoutId(), container,
                                       false)
//    mBinding.root.layoutParams = params
//    mBaseBinding.root.container.addView(mBinding.root)

    onLoadAfter()
    return mBinding.root
  }

  open fun onLoadAfter() {

  }

  /**
   * 打开等待条
   */
  fun showProgressDialog() {
    showProgressDialog("Loading...")
  }

  /**
   * 打开等待条
   */
  fun showProgressDialog(message: String) {
    if (mActivity.isDestroyed or mActivity.isFinishing) {
      return
    }
    mActivity.runOnUiThread {
      if (mProgressDialog != null && mProgressDialog!!.isShowing) {
        return@runOnUiThread
      }

      mProgressDialog = ProgressDialog(mActivity)
      // 进度条为水平旋转
      mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
      // 设置点击返回不能取消
      mProgressDialog!!.setCancelable(true)
      //设置触摸对话框以外的区域不会消失
      mProgressDialog!!.setCanceledOnTouchOutside(false)

      mProgressDialog!!.setMessage(message)
      mProgressDialog!!.show()
    }
  }

  /**
   * 关闭等待条
   */
  fun closeProgressDialog() {
    if (mActivity.isDestroyed or mActivity.isFinishing) {
      return
    }
    mActivity.runOnUiThread {
      if (mProgressDialog != null && mProgressDialog!!.isShowing) {
        mProgressDialog!!.dismiss()
      }
    }
  }

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    if (isVisibleToUser) {
      mVisible = true
      onVisible()
    } else {
      mVisible = false
      onInvisible()
    }
  }

  /**
   * 可见
   */
  open fun onVisible() {
    //View对象初始化&没有加载过
    if (isCreateView && !isLazyLoad) {
      lazyLoad()
    }

  }

  fun onCreateEvenBus(any: Any) {
    if (!EventBus.getDefault().isRegistered(any)) {
      EventBus.getDefault().register(this)
    }
  }

  fun onDestryEvenBus(any: Any) {
    if (EventBus.getDefault().isRegistered(any)) {
      EventBus.getDefault().unregister(any)
    }
  }


  /**
   * 不可见
   */
  open fun onInvisible() {


  }

  override fun onDestroy() {
    super.onDestroy()
    //翻译ViewModel资源
    mViewModel.destroy()
  }


  /**
   * 布局文件
   */
  @LayoutRes
  abstract fun onLayoutId(): Int

  /**
   * 获取ViewModel类
   */
  abstract fun onViewModel(): VM

  /**
   * 延迟加载
   * V2子类必须重写此方法
   */
  open fun lazyLoad() {
    isLazyLoad = true
  }

  /**
   * 界面图标
   */
  open fun onPageEmptyIcon(): Int? {
    return null
  }

  /**
   * 空界面描述
   */
  open fun onPageEmptyDesc(): Int? {
    return null
  }


  /**
   * 界面状态
   */
  enum class Status {
    /**
     * 空页面
     */
    Empty,

    /**
     * 通用异常
     */
    Error,

    /**
     * 加载中
     */
    Loading,

    /**
     * 正常
     */
    Normal,

    /**
     * 网络异常
     */
    Network
  }

}