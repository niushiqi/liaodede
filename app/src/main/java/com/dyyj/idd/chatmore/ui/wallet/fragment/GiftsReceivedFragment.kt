package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.graphics.Color
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentGiftsReceivedBinding
import com.dyyj.idd.chatmore.model.network.result.MyGiftsResult
import com.dyyj.idd.chatmore.ui.adapter.ReceivedGiftsAdapter
import com.dyyj.idd.chatmore.viewmodel.GiftsReceivedViewModel
import com.dyyj.idd.chatmore.viewmodel.PopGiftsItemViewModel
import com.dyyj.idd.chatmore.weiget.pop.GiftsToolsItemPop
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/18
 * desc   : 礼物
 */
class GiftsReceivedFragment : BaseFragment<FragmentGiftsReceivedBinding, GiftsReceivedViewModel>() {

    override fun onLayoutId(): Int {
        return R.layout.fragment_gifts_received
    }

    var mSelectedGift: MyGiftsResult.Gift? = null
    //var mAllGifs: List<MyGiftsResult.Gift>? = listOf()

    override fun onViewModel(): GiftsReceivedViewModel {
        return GiftsReceivedViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initData()
        mViewModel.netReceivedRose()
        initListener()
    }

  private fun initData() {
      mBinding.rvReceivedGifts.layoutManager = GridLayoutManager(mActivity,4)
  }

  private fun initListener() {
      //点击兑换
      mBinding.clickAndConversion.setOnClickListener {
        //TODO
          if (mSelectedGift != null) {
              if (mSelectedGift?.num!! > 4) {
                  GiftsToolsItemPop(mActivity,mSelectedGift).show(mBinding.root)
              }
          }
      }
  }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEvenBus(this)
    }

    @Subscribe
    fun onGiftsReceivedVM(obj: GiftsReceivedViewModel.ReceivedRoseVM) {
        /*var list: ArrayList<MyGiftsResult.Gift> = ArrayList()//<ChatGiftsResult.Data.Gift>() as ArrayList<ChatGiftsResult.Data.Gift>
        list.add(MyGiftsResult.Gift("39", "鼓掌","http://api.ddaylove.com/img/newprop/guzhang_4074aa9507fc9c9c97a2e550f47f806d.png"         ,4,"4"))
        list.add(MyGiftsResult.Gift("40", "皇冠","http://api.ddaylove.com/img/newprop/huangguan_936201d5e98fbb7b06b78001754903f7.png"       ,1,"1"))
        list.add(MyGiftsResult.Gift ("48","苹果","http://api.ddaylove.com/img/newprop/pingguo_303c358a69d7a5ff7a2424172234e40b.png"         ,8,"8"))
        list.add(MyGiftsResult.Gift ("49","小熊","http://api.ddaylove.com/img/newprop/xiaoxiong_b31eedcfd895752e683826237c6c33f7.png"       ,6,"6"))
        list.add(MyGiftsResult.Gift ("51","心钻","http://api.ddaylove.com/img/newprop/xinzhuan_aea088646b813ec14f3276f31788e33b.png"        ,8,"8"))
        list.add(MyGiftsResult.Gift ("37","棒棒","http://api.ddaylove.com/img/newprop/bangbangtang_a9cb96c40b32dbcc2ca1ea7432984068.png"    ,6,"6"))
        list.add(MyGiftsResult.Gift ("52","雪糕","http://api.ddaylove.com/img/newprop/xuegao_8230e7f3cdbbc9e597e558ad194d7fb0.png"          ,6,"6"))
        list.add(MyGiftsResult.Gift ("45","玫瑰","http://api.ddaylove.com/img/newprop/meigui_e1f90ed665614acff25e1c340d0a5665.png"          ,8,"8"))
        list.add(MyGiftsResult.Gift ("41","金卡","http://api.ddaylove.com/img/newprop/jinka_e2c2178a378748fe5eb0c95fac3abc46.png"           ,9,"9"))
        list.add(MyGiftsResult.Gift ("47","啤酒","http://api.ddaylove.com/img/newprop/pijiu_980fb4fe4a10a2b11b4d1aa4ab182cfa.png"           ,1,"1"))
        list.add(MyGiftsResult.Gift ("46","名包","http://api.ddaylove.com/img/newprop/mingbao_da53006cd2e8cdcea43ceb85074ecde9.png"         ,8,"8"))
        list.add(MyGiftsResult.Gift ("42","哆啦","http://api.ddaylove.com/img/newprop/jiqimao_95ddc93432ed5deaaae73ee4fd8ee366.png"         ,6,"6"))
        list.add(MyGiftsResult.Gift ("43","救命","http://api.ddaylove.com/img/newprop/jiumingyao_6decbd0c2d9c13d5308001625ef918d5.png"      ,8,"8"))
        list.add(MyGiftsResult.Gift ("38","蛋糕","http://api.ddaylove.com/img/newprop/dangao_6c5638bdad4a02f44bbbd1c3694e3d42.png"          ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6,"6"))
        list.add(MyGiftsResult.Gift ("50","星星","http://api.ddaylove.com/img/newprop/xingxing_a9a23e435b2eafcccda761176359a45e.png"        ,0,"0"))*/
        //可兑换金币
        mBinding.tvValueHowMuch.text = "可兑换${obj.obj?.data?.get(0)?.coin?.toInt()!!*obj.obj?.data?.get(0)?.num!!}金币"
        obj.obj.data[0].selected = 1
        setStatus(obj.obj.data[0])
        mBinding.rvReceivedGifts.adapter = ReceivedGiftsAdapter(mActivity,obj.obj.data)
        (mBinding.rvReceivedGifts.adapter as ReceivedGiftsAdapter).setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.cl_container) {
                obj.obj.data.forEachIndexed { index, gift ->
                    if (index == position) {
                        obj.obj.data[index].selected = 1
                        setStatus(obj.obj.data[index])
                    }else{
                        obj.obj.data[index].selected = 0
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe
    fun onExchange2CoinVM(obj: PopGiftsItemViewModel.Exchange2CoinVM) {
        if (obj.isSuucess) {
            context?.niceToast("兑换成功,金币已入帐")
            mViewModel.netReceivedRose()
        }
    }

    fun setStatus(selectedGift: MyGiftsResult.Gift?) {
        mSelectedGift = selectedGift
        mBinding.tvValueHowMuch.text = "可兑换${selectedGift?.coin?.toInt()!! * selectedGift.num!!}金币"
        if (5 > selectedGift.num) {
            mBinding.clickAndConversion.isEnabled = false//数量小于5时为true，可点击
            mBinding.clickAndConversion.setTextColor(Color.parseColor("#c2c2c2"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.clickAndConversion.background = ContextCompat.getDrawable(mActivity, R.drawable.shape_bg_btn_unenable)
            }
        }else{
            mBinding.clickAndConversion.isEnabled = true //数量大于4时为true，可点击
            mBinding.clickAndConversion.setTextColor(Color.parseColor("#FADC46"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBinding.clickAndConversion.background = ContextCompat.getDrawable(mActivity, R.drawable.shape_bg_btn_brown)
            }
        }
    }
}