package com.dyyj.idd.chatmore.ui.dialog.fragment

import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentSlidingviewpagerBinding
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.model.network.result.RedPackageResult
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.viewmodel.SlidingViewPagerViewModel
import com.dyyj.idd.chatmore.weiget.pop.NotifactionPop
import com.dyyj.idd.chatmore.weiget.pop.RedPacketPop
import com.gt.common.gtchat.extension.niceGlide
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 红包界面
 */
class SlidingViewPagerFragment : BaseFragment<FragmentSlidingviewpagerBinding, SlidingViewPagerViewModel>() {

  override fun onLayoutId(): Int {
    return R.layout.fragment_slidingviewpager
  }

  override fun onViewModel(): SlidingViewPagerViewModel {
    return SlidingViewPagerViewModel()
  }

  override fun onLoadAfter() {
    initData()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onCreateEvenBus(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    onDestryEvenBus(this)
  }

  private var index: RedPackageResult.RedPackageEntry? = null

  open fun initData() {
    index = arguments?.getParcelable("index") as RedPackageResult.RedPackageEntry
    mBinding.textView27.text = index?.tip
    niceGlide().load(index?.icon).asBitmap().crossFade().error(R.drawable.bg_circle_black).into(
        mBinding.imageView27)
//    val requestOption = RequestOptions().optionalCenterCrop()
//    requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//    Glide.with(mActivity).load(index?.icon).apply(requestOption).into(mBinding.imageView27)
    niceGlide().load(index?.smallIcon).asBitmap().crossFade().error(
        R.drawable.bg_circle_black).into(mBinding.imageView29)

//    val requestOption2 = RequestOptions().optionalCenterCrop()
//    requestOption2.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//    Glide.with(mActivity).load(index?.smallIcon).apply(requestOption2).into(mBinding.imageView29)
    mBinding.imageView29.setOnClickListener {
      //            Toast.makeText(activity, index?.smallTip, Toast.LENGTH_SHORT).show()
      mBinding.textviewThird.visibility = View.VISIBLE
      mBinding.textviewThird.text = index?.smallTip
      mBinding.textviewThird.postDelayed({ mBinding.textviewThird.visibility = View.GONE }, 3000)
    }
    mBinding.textView28.text = index?.name
    if (index?.status == 0) {//锁定
      mBinding.imageView27.setOnClickListener {
        //                Toast.makeText(ChatApp.getInstance().niceChatContext(), index?.clickTip, Toast.LENGTH_SHORT).show()
        mBinding.textviewSecond.visibility = View.VISIBLE
        mBinding.textviewSecond.text = index?.clickTip
        mBinding.textviewSecond.postDelayed({ mBinding.textviewSecond.visibility = View.GONE },
                                            3000)
      }
      mBinding.textView27.visibility = View.VISIBLE
    } else if (index?.status == 1) {//成长中
      mBinding.rlProgress.visibility = View.VISIBLE
      mBinding.textView27.visibility = View.GONE
      mBinding.textviewSecond.visibility = View.VISIBLE
      mBinding.textviewSecond.text = index?.tip
      mBinding.imageView27.setOnClickListener {
          if (System.currentTimeMillis() > index?.availableTime?.toLong()!!) {
              EventBus.getDefault().post(RefreshGiftVM())
          } else {
              mBinding.textviewSecond.visibility = View.VISIBLE
              mBinding.textviewSecond.text = index?.clickTip
              mBinding.textviewSecond.postDelayed({mBinding.textviewSecond.visibility = View.GONE}, 3000)
          }
      }
      mDataRepository.startGrawTime(index?.availableTime!!.toLong(),
                                    index?.availableTime!!.toLong() - index?.startTime!!.toLong())
//      mDataRepository.startGrawTime(index?.availableTime!!.toLong(),
//                                    3)
    } else if (index?.status == 2) {//可领取
      if (!NotificationManagerCompat.from(mActivity).areNotificationsEnabled()) {
        val show = NotifactionPop(mActivity).show(mBinding.textView27, "红包可领时，我们将立刻通知你", "不错过每一个红包","sliding")
        if (show) return
      }
      mBinding.textView27.visibility = View.VISIBLE
      mBinding.imageView27.setOnClickListener {
        RedPacketPop(activity).show((mActivity as MainActivity).mBinding.root,
                                    index?.giftId.toString())
      }
    }
  }

  @Subscribe
  fun onRedTimeVM(obj: DataRepository.RedPacketTimeVM) {
    mBinding.root.post {
      if (index?.status == 1) {
        mBinding.pbProgress.progress = obj.progress
        mBinding.txtTime.text = obj.time
        if (obj.finish) {
          EventBus.getDefault().post(RefreshGiftVM())
        }
      }
    }
  }

  class RefreshGiftVM()
}