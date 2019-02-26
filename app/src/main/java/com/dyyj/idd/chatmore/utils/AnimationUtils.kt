package com.dyyj.idd.chatmore.utils

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.model.network.result.GetGiftResult
import com.dyyj.idd.chatmore.ui.main.fragment.OpenCallFragment
import com.dyyj.idd.chatmore.viewmodel.OpenCallViewModel
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import com.dyyj.idd.chatmore.weiget.OverShootInterpolation
import com.gt.common.gtchat.extension.niceChatContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/16
 * desc   :
 */
object AnimationUtils {

  val cacheDrawableMap = HashMap<Int, WeakReference<Drawable>>()
  //AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
  fun start(context: Context, view: ImageView, name: String, from: Int, to: Int, duration: Int,
      listener: MyFrameAnimation.OnFrameAnimationListener) {
    val animationDrawable = MyFrameAnimation()
    for (i in from until to) {

      val number = when (i) {
        in 1..9 -> "0000$i"
        in 10..99 -> "000$i"
        in 100..999 -> "00$i"
        in 1000..9999 -> "0$i"
        in 10000..9999 -> i
        else -> {
          i
        }
      }
      val id = context.resources.getIdentifier("${name}_$number", "drawable", context.packageName)

      Timber.tag("resouceid").i(id.toString())
      var drawable: Drawable? = null
      if (cacheDrawableMap.containsKey(id)) {
        if ((cacheDrawableMap.get(id) != null) and (cacheDrawableMap.get(id)?.get() != null)) {
          Log.e("drawablecache", "hit")
        }
        drawable = cacheDrawableMap.get(id)?.get() ?: ContextCompat.getDrawable(context, id)
      } else {
        drawable = ContextCompat.getDrawable(context, id)
      }
      if (drawable != null) {
        cacheDrawableMap.put(id, WeakReference(drawable!!))
        animationDrawable.addFrame(drawable, duration)
      }
    }

    animationDrawable.setOnFrameAnimationListener(listener)

    animationDrawable.isOneShot = true
    view.setImageDrawable(animationDrawable)
    // 获取资源对象
    animationDrawable.stop()
    // 特别注意：在动画start()之前要先stop()，不然在第一次动画之后会停在最后一帧，这样动画就只会触发一次
    animationDrawable.start()
  }

  var animObj: ObjectAnimator? = null

  fun startSwitchLoading(view: View) {
    animObj = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
    animObj?.duration = 10 * 1000
    animObj?.start()
  }

  fun stopSwitchLoading() {
    if (animObj != null) {
      if (animObj?.isRunning!!) {
        animObj?.cancel()
      }
    }
  }

  var anim1: TranslateAnimation? = null
  var anim2: TranslateAnimation? = null
  private val overshoot = OverShootInterpolation()

  /**
   * 左右滑动
   */
  open fun startOverShootAnim(leftView: View, rightView: View) {
//        leftView.rotation = -30f
//        rightView.rotation = 30f
    if ((anim1 == null) or (anim2 == null)) {
      anim1 = TranslateAnimation(0f, DeviceUtils.dp2px(
          ChatApp.getInstance().niceChatContext().resources, 80f), 0f, 0f)
      anim1?.duration = 800
      anim1?.interpolator = overshoot
      anim1?.fillAfter = true
      anim2 = TranslateAnimation(0f, -DeviceUtils.dp2px(
          ChatApp.getInstance().niceChatContext().resources, 80f), 0f, 0f)
      anim2?.duration = 800
      anim2?.interpolator = overshoot
      anim2?.fillAfter = true
    }
    leftView.visibility = View.VISIBLE
    rightView.visibility = View.VISIBLE
    leftView.startAnimation(anim1)
    rightView.startAnimation(anim2)
  }

  /**
   * 小猪金币动画
   */
  open fun startPigGolds(target: View, wrap: ViewGroup, oneView: View, twoView: View,
      threeView: View) {
    wrap.visibility = View.VISIBLE

    val anim1 = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                             R.anim.alpha_pig_gold1)
    oneView.startAnimation(anim1)
    val anim2 = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                             R.anim.alpha_pig_gold2)
    threeView.startAnimation(anim2)
    val anim3 = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                             R.anim.alpha_pig_gold3)
    twoView.startAnimation(anim3)
    startShakeByPropertyAnim(target, 700)
  }

  /**
   * 奖励金币动画
   */
  open fun startGolds(view: View, innerView: View, outerView: View, isUp: Boolean, isFirst: Boolean,
      rlGold: View, txtGold: TextView) {
    view.visibility = View.VISIBLE
    val anim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                            R.anim.golds_anim)
    anim.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {
      }

      override fun onAnimationEnd(animation: Animation?) {
        view.visibility = View.GONE
      }

      override fun onAnimationStart(animation: Animation?) {
      }

    })
    view.startAnimation(anim)
//    startGoldBtn(innerView, outerView, isUp, isFirst, rlGold, txtGold)
  }

  /**
   * 开启聊天按钮动画
   */
  open fun startGoldBtn(innerView: View, outerView: View, isUp: Boolean, isFirst: Boolean,
      rlGold: View, txtGold: TextView) {
    if (isUp) {
      if (isFirst) {
        //首次增加
        val innerUpAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                       R.anim.alpha_gold_inner_up_anim)
        val outerUpAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                       R.anim.alpha_gold_outer_up_anim)
        innerUpAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            outerView.visibility = View.VISIBLE
            outerView.startAnimation(outerUpAnim)
          }

          override fun onAnimationStart(animation: Animation?) {
            innerView.visibility = View.VISIBLE
          }

        })
        outerUpAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            outerView.visibility = View.GONE
            rlGold.visibility = View.VISIBLE
            txtGold.text = "${OpenCallFragment.mGoldTake}枚"
            val scaleAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                         R.anim.scale_text_anim)
            txtGold.startAnimation(scaleAnim)
          }

          override fun onAnimationStart(animation: Animation?) {
          }

        })
        innerView.startAnimation(innerUpAnim)
      } else {
        val outerUpAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                       R.anim.alpha_gold_outer_up_anim)
        outerUpAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            outerView.visibility = View.GONE
            rlGold.visibility = View.VISIBLE
            txtGold.text = "${OpenCallFragment.mGoldTake}枚"
            val scaleAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                         R.anim.scale_text_anim)
            txtGold.startAnimation(scaleAnim)
          }

          override fun onAnimationStart(animation: Animation?) {
          }

        })
        outerView.startAnimation(outerUpAnim)
      }
    } else {
      if (OpenCallFragment.mGoldTake <= 0) {
        //消耗完了
        //内圈
        val innerDownAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                         R.anim.alpha_gold_inner_down_anim)
        innerDownAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            innerView.visibility = View.GONE
            rlGold.visibility = View.GONE
          }

          override fun onAnimationStart(animation: Animation?) {
          }

        })
        innerView.startAnimation(innerDownAnim)
      } else {
        //外圈
        val outerDownAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                         R.anim.alpha_gold_outer_down_anim)
        outerDownAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            rlGold.visibility = View.VISIBLE
            txtGold.text = "${OpenCallFragment.mGoldTake}枚"
            val scaleAnim = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                                         R.anim.scale_text_anim)
            txtGold.startAnimation(scaleAnim)
          }

          override fun onAnimationStart(animation: Animation?) {
          }

        })
        innerView.startAnimation(outerDownAnim)

      }
    }
  }

  /**
   * 奖励进小猪动画
   */
  open fun startPathAnim(context: Context, data: GetGiftResult.Data, clWrap: RelativeLayout,
      x1: Float, y1: Float, cash: View, coin: View, stone: View, wallet: View, x2: Float,
      y2: Float) {
    data?.let {
      val firstArray = hashMapOf<String, String>()
      if (it.cash != "0") {
        firstArray.put("cash", it.cash!!)
      }
      if (it.coin != "0") {
        firstArray.put("coin", it.coin!!)
      }
      if (it.stone != "0") {
        firstArray.put("stone", it.stone!!)
      }
      firstArray.keys.forEachIndexed { index, s ->
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_gift_anim, null)
        itemView.findViewById<TextView>(R.id.tv_gift_num).text = "+ ${firstArray[s]}"
        val location = IntArray(2)
        when (s) {
          "cash" -> {
            itemView.findViewById<ImageView>(R.id.iv_gift_icon).setImageResource(
                R.drawable.ic_gift_xianjin)
            cash.getLocationInWindow(location)
          }
          "coin" -> {
            itemView.findViewById<ImageView>(R.id.iv_gift_icon).setImageResource(
                R.drawable.ic_gift_xianjin)
            coin.getLocationInWindow(location)
          }
          "stone" -> {
            itemView.findViewById<ImageView>(R.id.iv_gift_icon).setImageResource(
                R.drawable.ic_gift_moshi)
            stone.getLocationInWindow(location)
          }
          else -> {
            wallet.getLocationInWindow(location)
          }
        }
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                 RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        clWrap.addView(itemView, params)


        val scaleAnim = ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                                       Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = 200
        scaleAnim.startOffset = (300 * index).toLong()
        scaleAnim.interpolator = OvershootInterpolator()
        scaleAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            val anim = TranslateAnimation(0f, location[0] - x1, 0f, location[1] - y1)
            anim.interpolator = AccelerateInterpolator()
            anim.duration = 500
            itemView.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
              override fun onAnimationRepeat(animation: Animation?) {
              }

              override fun onAnimationEnd(animation: Animation?) {
                itemView.visibility = View.GONE

                when (s) {
                  "cash" -> startShakeByPropertyAnim(cash, (index - 1) * 300 + 500)
                  "coin" -> startShakeByPropertyAnim(coin, (index - 1) * 300 + 500)
                  "stone" -> startShakeByPropertyAnim(stone, (index - 1) * 300 + 500)
                  else -> startShakeByPropertyAnim(wallet, (index - 1) * 300 + 500)
                }
              }

              override fun onAnimationStart(animation: Animation?) {
              }

            })
          }

          override fun onAnimationStart(animation: Animation?) {
          }

        })
        itemView.startAnimation(scaleAnim)
      }
      it.prop ?: return@let

      it.prop?.forEachIndexed { index, data ->
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_gift_anim, null)
        itemView.findViewById<TextView>(R.id.tv_gift_num).text = "+ ${data?.num}"
        Glide.with(context).load(data?.icon).asBitmap().crossFade().into(
            itemView.findViewById(R.id.iv_gift_icon))
//        val requestOption = RequestOptions().centerCrop()
//        requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//        Glide.with(mContext).load(data?.icon).apply(requestOption).into(itemView.findViewById(R.id.iv_gift_icon))
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                 RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        clWrap.addView(itemView, params)

        val location = IntArray(2)
        wallet.getLocationInWindow(location)
        val scaleAnim = ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                                       Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = 200
        scaleAnim.startOffset = (300 * (index + firstArray.size)).toLong()
        scaleAnim.interpolator = OvershootInterpolator()
        scaleAnim.setAnimationListener(object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }

          override fun onAnimationEnd(animation: Animation?) {
            val anim = TranslateAnimation(0f, location[0] - x1, 0f, location[1] - y1)
            anim.interpolator = AccelerateInterpolator()
            anim.duration = 500
            itemView.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
              override fun onAnimationRepeat(animation: Animation?) {
              }

              override fun onAnimationEnd(animation: Animation?) {
                startShakeByPropertyAnim(wallet, (index - 1) * 300 + 500)
                itemView.visibility = View.GONE
//                                        if (index == 0) {
//                                            startShakeByPropertyAnim(target, (firstArray.size - 1) * 300 + 500)
//                                        }
                EventBus.getDefault().post(OpenCallViewModel.RedPacketCloseVM())
              }

              override fun onAnimationStart(animation: Animation?) {
              }

            })
          }

          override fun onAnimationStart(animation: Animation?) {
          }

        })
        itemView.startAnimation(scaleAnim)
      }
    }
  }

  fun startRedpacket(context: Context, clWrap: RelativeLayout) {

    (0..2).forEach {
      val view = LayoutInflater.from(context).inflate(R.layout.animation_red_packet, clWrap, false)
      val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                               RelativeLayout.LayoutParams.WRAP_CONTENT)
      params.topMargin = DeviceUtils.dp2px(context.resources, it * 3f).toInt()
      params.addRule(RelativeLayout.CENTER_IN_PARENT)
      view.layoutParams = params

      clWrap.addView(view)
    }


  }

  /**
   * 抖动动画
   */
  open fun startShakeByPropertyAnim(view: View, duration: Int) {
    if (view == null) {
      return
    }
    val scaleLarge = 1.1f
    val scaleSmall = 0.9f
    val shakeDegrees = 20f
    //TODO 验证参数的有效性

    //先变小后变大
    val scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                                                             Keyframe.ofFloat(0f, 1.0f),
                                                             Keyframe.ofFloat(0.25f, scaleSmall),
                                                             Keyframe.ofFloat(0.5f, scaleLarge),
                                                             Keyframe.ofFloat(0.75f, scaleLarge),
                                                             Keyframe.ofFloat(1.0f, 1.0f));
    val scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                                                             Keyframe.ofFloat(0f, 1.0f),
                                                             Keyframe.ofFloat(0.25f, scaleSmall),
                                                             Keyframe.ofFloat(0.5f, scaleLarge),
                                                             Keyframe.ofFloat(0.75f, scaleLarge),
                                                             Keyframe.ofFloat(1.0f, 1.0f));

    //先往左再往右
    val rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                                                             Keyframe.ofFloat(0f, 0f),
                                                             Keyframe.ofFloat(0.1f, -shakeDegrees),
                                                             Keyframe.ofFloat(0.2f, shakeDegrees),
                                                             Keyframe.ofFloat(0.3f, -shakeDegrees),
                                                             Keyframe.ofFloat(0.4f, shakeDegrees),
                                                             Keyframe.ofFloat(0.5f, -shakeDegrees),
                                                             Keyframe.ofFloat(0.6f, shakeDegrees),
                                                             Keyframe.ofFloat(0.7f, -shakeDegrees),
                                                             Keyframe.ofFloat(0.8f, shakeDegrees),
                                                             Keyframe.ofFloat(0.9f, -shakeDegrees),
                                                             Keyframe.ofFloat(1.0f, 0f));

    val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder,
                                                               scaleYValuesHolder,
                                                               rotateValuesHolder)
    objectAnimator.duration = duration.toLong()
    objectAnimator.start()
  }

  open fun startTextTransAnim(view1: View, view2: View) {
    val animOut = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                               R.anim.trans_text_out)
    val animIn = AnimationUtils.loadAnimation(ChatApp.getInstance().niceChatContext(),
                                              R.anim.trans_text_in)
    animOut.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {
      }

      override fun onAnimationEnd(animation: Animation?) {
        view1.visibility = View.GONE
      }

      override fun onAnimationStart(animation: Animation?) {
      }

    })
    animIn.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {
      }

      override fun onAnimationEnd(animation: Animation?) {
      }

      override fun onAnimationStart(animation: Animation?) {
        view2.visibility = View.VISIBLE
      }

    })
    view1.startAnimation(animOut)
    view2.startAnimation(animIn)
  }

}