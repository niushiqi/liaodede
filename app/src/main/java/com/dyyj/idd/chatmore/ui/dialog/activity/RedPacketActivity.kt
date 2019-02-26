package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityRedPacket3Binding
import com.dyyj.idd.chatmore.viewmodel.RedPacketPopViewModel2
import com.dyyj.idd.chatmore.viewmodel.RedPacketViewModel3
import com.gt.common.gtchat.extension.niceGlide
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/09/13
 * desc   : 三连开红包
 */
class RedPacketActivity : BaseActivity<ActivityRedPacket3Binding, RedPacketViewModel3>() {

  companion object {
    fun start(context: Context, giftId: String) {
      val intent = Intent(context, RedPacketActivity::class.java)
      intent.putExtra("id", giftId)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_red_packet3
  }

  override fun onViewModel(): RedPacketViewModel3 {
    return RedPacketViewModel3()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateEvenbus(this)
    this.setFinishOnTouchOutside(true)
    mViewModel.netGetGift(intent.getStringExtra("id"))
    initListener()
  }

  /**
   * 点击事件
   */
  fun initListener() {

    mBinding.getBtn.setOnClickListener {
      onBackPressed()
    }
    mBinding.closeIv.setOnClickListener {
      onBackPressed()
    }
  }

  override fun onBackPressed() {
    onDestryEvenbus(this)

    if (mViewModel.mData != null) {
      EventBus.getDefault().post(RedPacketPopViewModel2.GetGiftOkVM(mViewModel.mData!!))
    } else {
      EventBus.getDefault().post(RedPacketPopViewModel2.GetGiftOkVM())
    }
    finish()

  }


  /**
   * 领取红包
   */
  @Subscribe
  fun onGetGiftVM(obj: RedPacketViewModel3.GetGiftVM) {
    if (obj.isSuccess) {

//      obj.obj?.let {
      if (obj.obj != null) {
        //金币/魔石/现金
        mBinding.coinTv.text = "+${obj.obj.coin}"
        mBinding.stoneTv.text = "+${obj.obj.stone}"
        mBinding.cashTv.text = "+${obj.obj.cash}"
        mBinding.titleTv.text = obj.obj.envelope
        setTextFont(mBinding.coinTv)
        setTextFont(mBinding.stoneTv)
        setTextFont(mBinding.cashTv)
        if (obj.obj.coin?.toFloat() == 0f) {
          mBinding.coinLl.visibility = View.GONE
        }
        if (obj.obj.stone?.toFloat() == 0f) {
          mBinding.stoneLl.visibility = View.GONE
        }
        if (obj.obj.cash?.toFloat() == 0f) {
          mBinding.cashLl.visibility = View.GONE
        }
        obj.obj.prop ?: return
        if (obj.obj.prop?.size == 0) {
          return
        }

        val list = arrayListOf(R.id.item_ll2, R.id.item_ll3, R.id.item_ll4)
        val views = arrayListOf<LinearLayout>()
        list.forEachIndexed { index, i ->
          val parent = findViewById<LinearLayout>(i)
          (0 until parent.childCount).forEach { views.add(parent.getChildAt(it) as LinearLayout) }
        }

        views.forEachIndexed { index, linearLayout ->

          val prop = mViewModel.mData?.prop?.get(index)

          if (prop == null) {//隐藏
            linearLayout.visibility = View.GONE
          } else {//显示
            linearLayout.visibility = View.VISIBLE
            val relativeLayout = linearLayout.getChildAt(0) as RelativeLayout
            val textView = linearLayout.getChildAt(1) as TextView
            textView.text = "+${prop?.num}"
            setTextFont(textView)

            val imageView = relativeLayout.getChildAt(1) as ImageView
            imageView.niceGlide().load(prop.icon).into(imageView)
          }

        }
      }
    }
  }
}