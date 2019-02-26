package com.dyyj.idd.chatmore.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dyyj.idd.chatmore.app.ChatApp
import java.lang.ref.WeakReference

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/02/05
 * desc   : ViewHolder基类.绑定ViewDatagind
 */
abstract class BaseViewHolder<in T : Any, out D : ViewDataBinding>(
    viewGroup: ViewGroup?, @LayoutRes private val layoutId: Int,
    val isExecute: Boolean = true, val isViewGroupNull: Boolean = false) : RecyclerView.ViewHolder(
    DataBindingUtil.inflate<D>(LayoutInflater.from(viewGroup?.context), layoutId, if (isViewGroupNull) null else viewGroup,
                               false).root) {
  val mBinding: D = DataBindingUtil.getBinding(itemView)!!

  val mContext: Context by lazy { mBinding.root.context }

  /**
   * 数据
   */
  val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }

  /**
   * 当数据改变时，binding会在下一帧去改变数据，如果我们需要立即改变，就去调用executePendingBindings方法。
   */
  fun onBaseBindViewHolder(obj: T, position: Int) {
    onBindViewHolder(obj, position)
    if (isExecute) {
      mBinding.executePendingBindings()
    }
  }


  /**
   * 当数据改变时，binding会在下一帧去改变数据，如果我们需要立即改变，就去调用executePendingBindings方法。
   */
  fun onBaseBindViewHolder(obj: T, position: Int, type: Int) {
    onBindViewHolder(obj, position, type)
    if (isExecute) {
      mBinding.executePendingBindings()
    }
  }


  /**
   * 获取Glide
   */
  fun getGlide(): RequestManager {
    return when (mContext) {
      is AppCompatActivity -> Glide.with(mContext as AppCompatActivity)
      is Fragment -> Glide.with(mContext as Fragment)
      else -> Glide.with(mContext)
    }
  }

  /**
   * 软引用保存ImageView
   */
  fun getImageView(imageView: ImageView): ImageView? {
    val imageViewWeakReference = WeakReference(imageView)
    return imageViewWeakReference.get()
  }

  /**
   * view绑定
   */
  open fun onBindViewHolder(obj: T, position: Int, type: Int){}

  /**
   * view绑定
   */
  open fun onBindViewHolder(obj: T, position: Int){}

}