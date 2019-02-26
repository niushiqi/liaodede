package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentGameToolsItemBinding
import com.dyyj.idd.chatmore.model.network.result.GameToolsResult
import com.dyyj.idd.chatmore.viewmodel.GameToolsItemViewModel
import com.dyyj.idd.chatmore.viewmodel.GoldViewModel
import com.dyyj.idd.chatmore.viewmodel.PopGameToolsItemViewModel
import com.dyyj.idd.chatmore.weiget.pop.GameToolsItemPop
import com.gt.common.gtchat.extension.niceGlide
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/19
 * desc   : 道具详情页
 */
class GameToolsItemFragment : BaseFragment<FragmentGameToolsItemBinding, GameToolsItemViewModel>() {

  private var mData: List<GameToolsResult.Data.Prop.PropItem>? = null

  fun setData(data: List<GameToolsResult.Data.Prop.PropItem>): GameToolsItemFragment {
    mData = data
    return this
  }

  override fun onLayoutId(): Int {
    return R.layout.fragment_game_tools_item
  }

  override fun onViewModel(): GameToolsItemViewModel {
    return GameToolsItemViewModel()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onCreateEvenBus(this)
    lazyLoad()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    onDestryEvenBus(this)
  }

  @SuppressLint("SetTextI18n")
  override fun lazyLoad() {
    super.lazyLoad()
    initListener()
    mData?.let { list ->


      val icons = arrayListOf(mBinding.iconIv1, mBinding.iconIv2, mBinding.iconIv3,
                              mBinding.iconIv4, mBinding.iconIv5, mBinding.iconIv6,
                              mBinding.iconIv7, mBinding.iconIv8, mBinding.iconIv9,
                              mBinding.iconIv10)

      val flags = arrayListOf(mBinding.flagTv1, mBinding.flagTv2, mBinding.flagTv3,
                              mBinding.flagTv4, mBinding.flagTv5, mBinding.flagTv6,
                              mBinding.flagTv7, mBinding.flagTv8, mBinding.flagTv9,
                              mBinding.flagTv10)

      val golds = arrayListOf(mBinding.goldTv1, mBinding.goldTv2, mBinding.goldTv3,
                              mBinding.goldTv4, mBinding.goldTv5, mBinding.goldTv6,
                              mBinding.goldTv7, mBinding.goldTv8, mBinding.goldTv9,
                              mBinding.goldTv10)
      val df = DecimalFormat("0.0")
      (0 until list.size).forEach {
        this@GameToolsItemFragment.niceGlide().load(list[it].icon).into(icons[it])
        flags[it].text = list[it].ownNum.toString()
        golds[it].text = df.format(list[it].coin?.toFloat())

      }
      setStatusView(0)
    }
  }

  /**
   * 点击事件
   */
  private fun initListener() {


    val clicks = arrayListOf(R.id.item_cl1, R.id.item_cl2, R.id.item_cl3, R.id.item_cl4,
                             R.id.item_cl5, R.id.item_cl6, R.id.item_cl7, R.id.item_cl8,
                             R.id.item_cl9, R.id.item_cl10)
    val selects = arrayListOf(mBinding.selectIv1, mBinding.selectIv2, mBinding.selectIv3,
                              mBinding.selectIv4, mBinding.selectIv5, mBinding.selectIv6,
                              mBinding.selectIv7, mBinding.selectIv8, mBinding.selectIv9,
                              mBinding.selectIv10)
//    val goldFlag = arrayListOf<ImageView>(mBinding.goldFlagIv1, mBinding.goldFlagIv2, mBinding.goldFlagIv3,
//                                          mBinding.goldFlagIv4, mBinding.goldFlagIv5, mBinding.goldFlagIv6,
//                                          mBinding.goldFlagIv7, mBinding.goldFlagIv8, mBinding.goldFlagIv9,
//                                          mBinding.goldFlagIv10)
//
//    val golds = arrayListOf<View>(mBinding.goldTv1, mBinding.goldTv2, mBinding.goldTv3, mBinding.goldTv4,
//                                  mBinding.goldTv5, mBinding.goldTv6, mBinding.goldTv7, mBinding.goldTv8,
//                                  mBinding.goldTv9, mBinding.goldTv10)
//    val flags = arrayListOf<View>(mBinding.flagTv1, mBinding.flagTv2, mBinding.flagTv3, mBinding.flagTv4,
//                                  mBinding.flagTv5, mBinding.flagTv6, mBinding.flagTv7, mBinding.flagTv8,
//                                  mBinding.flagTv9, mBinding.flagTv10)
    selects.forEach { it.visibility = View.GONE }
    mBinding.selectIv1.visibility = View.VISIBLE

    //全部隐藏
    clicks.forEach { mBinding.root.findViewById<View>(it).visibility = View.INVISIBLE }
//    (0 until clicks.size).forEach {
//      goldFlag[it].visibility = View.INVISIBLE
//      golds[it].visibility = View.INVISIBLE
//      flags[it].visibility = View.INVISIBLE
//    }

    //模拟单选
    mData?.let {
      (0 until it.size).forEach {
        val id = clicks[it]
        val view = mBinding.root?.findViewById<View>(id)

        //有数据的显示
        view?.visibility = View.VISIBLE
//        goldFlag[it].visibility = View.VISIBLE
//        golds[it].visibility = View.VISIBLE
//        flags[it].visibility = View.VISIBLE
        view?.setOnClickListener { view ->
          selects.forEach { it.visibility = View.GONE }
          clicks.forEach { id ->
            if (id == view.id) {
              selects[it].visibility = View.VISIBLE
            }
          }
          setStatusView(it)
        }
      }
    }
  }

  /**
   * 刷新选中状态
   */
  private fun setStatusView(index: Int = 0) {
    val selectedObj = mData?.get(index)

    mBinding.exchangeBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc13)
    mBinding.exchangeBtn.setTextColor(Color.parseColor("#fffadc46"))
    mBinding.iconIv11.niceGlide().load(selectedObj?.icon).into(mBinding.iconIv11)
    mBinding.titleTv.text = selectedObj?.name
    mBinding.contentTv.text = "总价值${selectedObj?.coin?.toFloat()!! * selectedObj?.ownNum}金币"
    mBinding.exchangeBtn.isEnabled = true

    if (selectedObj.ownNum < 30) {
      mBinding.exchangeBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc14)
      mBinding.exchangeBtn.setTextColor(Color.parseColor("#ffffff"))
      mBinding.exchangeBtn.isEnabled = false
    }

    mBinding.exchangeBtn.setOnClickListener {
      GameToolsItemPop(mActivity, selectedObj).show(it)
    }

  }

  @Subscribe
  fun onConvert2CoinVM(obj: PopGameToolsItemViewModel.Convert2CoinVM) {
    if (obj.isSuucess) {


      mData?.forEach {
        if (it.id == obj.propId && obj.remaomNum != null) {
          it.ownNum = obj.remaomNum
          EventBus.getDefault().post(GoldViewModel.RefreshGoldVM())
        }
      }
      lazyLoad()
    }
  }

}