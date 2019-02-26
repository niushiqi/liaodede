package com.dyyj.idd.chatmore.ui.model

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.utils.DeviceUtils

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/22
 * desc   : 礼箱
 */
class BoxUi(val context: Context, val type: Int) {

  companion object {
    /**
     * 已打开
     */
    const val BOX_OPENED = 0

    /**
     * 即将打开
     */
    const val BOX_PREOPEN = 1

    /**
     * 未打开(暗)
     */
    const val BOX_UNOPEN = 2

    /**
     * 打开动效
     */
    const val BOX_OPENING = 3


  }

  fun getViewFromType(): View {
    return when (type) {
      BOX_OPENED -> getOpenedView()
      BOX_PREOPEN -> getPreopenView()
      BOX_UNOPEN -> getUnopenView()
      BOX_OPENING -> getOpeningView()
      else -> {
        getUnopenView()
      }
    }
  }

  /**
   * 已打开箱子
   */
  private fun getOpenedView(): ImageView {
    val imageView = ImageView(context)
    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    imageView.setImageResource(R.drawable.ic_chat_box_selected)
    return imageView
  }

  /**
   * 未打开箱子
   */
  private fun getUnopenView(): ImageView {
    val imageView = ImageView(context)
    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    imageView.setImageResource(R.drawable.ic_chat_box_normal)
    return imageView
  }

  /**
   * 打开动效箱子
   */
  @SuppressLint("ResourceAsColor")
  private fun getOpeningView(): LottieAnimationView {
    val view = LottieAnimationView(context)
    view.setAnimation("lottie/box_open.json")
//    view.background = ContextCompat.getDrawable(context, R.color.accent)
    view.scale = 0.23f
    view.loop(true)
    view.setPadding(0, 0, 0, DeviceUtils.dp2px(context.resources, 20f).toInt())
    view.playAnimation()
    return view
  }

  /**
   * 即将打开箱子
   */
  private fun getPreopenView(): ImageView {
    val imageView = ImageView(context)
    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    imageView.setImageResource(R.drawable.ic_chat_box_prepare)
    imageView.setBackgroundResource(R.drawable.ic_chat_box_prepare_bg)
    return imageView
  }
}