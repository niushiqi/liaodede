package com.dyyj.idd.chatmore.ui.user.fragment

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentSendGiftInChatActBinding
import com.dyyj.idd.chatmore.ui.adapter.GiftGridAdapter
import com.dyyj.idd.chatmore.ui.adapter.GiftViewPagerAdapter
import com.dyyj.idd.chatmore.viewmodel.SendGiftInChatViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/17
 * desc   : 好友聊天 礼物列表
 */
class SendGiftFragmentInChat : BaseFragment<FragmentSendGiftInChatActBinding, SendGiftInChatViewModel>() {

    //盛放礼物页面
    private val arr = arrayOfNulls<GiftGridAdapter>(3)

    /*总的页数*/
    private var pageCount: Int = 0
    /*每一页显示的个数*/
    private val pageSize = 8
    /*当前显示的是第几页*/
    private var curIndex = 0

    companion object {
        var mInstance: SendGiftFragmentInChat? = null
        //var matchStatusTitle: String? = null
        var mToUserid : String? = ""
        /**
         * 单例
         */
        fun instance(toUserid : String?): SendGiftFragmentInChat {
            mToUserid = toUserid
            if (mInstance == null) {
                return SendGiftFragmentInChat()
            }
            return mInstance!!
        }
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

    override fun onLayoutId(): Int {
        return R.layout.fragment_send_gift_in_chat_act
    }

    override fun onViewModel(): SendGiftInChatViewModel {
        return SendGiftInChatViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initData()
        initListener()
    }

    private fun initView() {
        //var list: ArrayList<ChatGiftsResult.Data.Gift> = ArrayList()//<ChatGiftsResult.Data.Gift>() as ArrayList<ChatGiftsResult.Data.Gift>
        //list.add(ChatGiftsResult.Data.Gift("39", "鼓掌","http://api.ddaylove.com/img/newprop/guzhang_4074aa9507fc9c9c97a2e550f47f806d.png"         ,4))
        //list.add(ChatGiftsResult.Data.Gift("40", "皇冠","http://api.ddaylove.com/img/newprop/huangguan_936201d5e98fbb7b06b78001754903f7.png"       ,12))
        //list.add(ChatGiftsResult.Data.Gift ("48","苹果","http://api.ddaylove.com/img/newprop/pingguo_303c358a69d7a5ff7a2424172234e40b.png"         ,8))
        //list.add(ChatGiftsResult.Data.Gift ("49","小熊","http://api.ddaylove.com/img/newprop/xiaoxiong_b31eedcfd895752e683826237c6c33f7.png"       ,6))
        //list.add(ChatGiftsResult.Data.Gift ("51","心钻","http://api.ddaylove.com/img/newprop/xinzhuan_aea088646b813ec14f3276f31788e33b.png"        ,8))
        //list.add(ChatGiftsResult.Data.Gift ("37","棒棒","http://api.ddaylove.com/img/newprop/bangbangtang_a9cb96c40b32dbcc2ca1ea7432984068.png"    ,6))
        //list.add(ChatGiftsResult.Data.Gift ("52","雪糕","http://api.ddaylove.com/img/newprop/xuegao_8230e7f3cdbbc9e597e558ad194d7fb0.png"          ,6))
        //list.add(ChatGiftsResult.Data.Gift ("45","玫瑰","http://api.ddaylove.com/img/newprop/meigui_e1f90ed665614acff25e1c340d0a5665.png"          ,8))
        //list.add(ChatGiftsResult.Data.Gift ("41","金卡","http://api.ddaylove.com/img/newprop/jinka_e2c2178a378748fe5eb0c95fac3abc46.png"           ,9))
        //list.add(ChatGiftsResult.Data.Gift ("47","啤酒","http://api.ddaylove.com/img/newprop/pijiu_980fb4fe4a10a2b11b4d1aa4ab182cfa.png"           ,12))
        //list.add(ChatGiftsResult.Data.Gift ("46","名包","http://api.ddaylove.com/img/newprop/mingbao_da53006cd2e8cdcea43ceb85074ecde9.png"         ,8))
        //list.add(ChatGiftsResult.Data.Gift ("42","哆啦","http://api.ddaylove.com/img/newprop/jiqimao_95ddc93432ed5deaaae73ee4fd8ee366.png"         ,6))
        //list.add(ChatGiftsResult.Data.Gift ("43","救命","http://api.ddaylove.com/img/newprop/jiumingyao_6decbd0c2d9c13d5308001625ef918d5.png"      ,8))
        //list.add(ChatGiftsResult.Data.Gift ("38","蛋糕","http://api.ddaylove.com/img/newprop/dangao_6c5638bdad4a02f44bbbd1c3694e3d42.png"          ,6))
        //list.add(ChatGiftsResult.Data.Gift ("44","红唇","http://api.ddaylove.com/img/newprop/hongchun_c7de1d1ae92ffb5dcbc745e49b786e86.png"        ,6))
        //list.add(ChatGiftsResult.Data.Gift ("50","星星","http://api.ddaylove.com/img/newprop/xingxing_a9a23e435b2eafcccda761176359a45e.png"        ,0))
    }

    private fun initData() {
        mViewModel.getChatGiftsList()//获取礼物列表
    }

    @Subscribe
    fun OnSubscribBank(vm: SendGiftInChatViewModel.ChatGiftsDataVM) {
        if (vm.success) {
            mBinding.tvProudPeas.text = vm.obj?.data?.ownDedouNum//获取我剩余的得豆
            //总的页数=总数/每页数量，并取整vi
            pageCount = Math.ceil(vm.obj!!.data?.giftList!!.size * 1.0 / pageSize).toInt()
            //viewpager
            val mPagerList = ArrayList<GridView>()
            for (curIndex in 0 until pageCount) {
                val gridview = LayoutInflater.from(mActivity).inflate(R.layout.gift_grid_layout, mBinding.vpViewPager, false) as GridView
                arr[curIndex] = GiftGridAdapter(mActivity, vm.obj.data?.giftList, curIndex)
                gridview.adapter = arr[curIndex]
                gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    // 调用消耗得豆的接口
                    mViewModel.sendGift(SendGiftFragmentInChat.mToUserid!!, vm.obj.data?.giftList?.get((curIndex*8)+position)?.id!!)
                }
                mPagerList.add(gridview)
            }
            mBinding.vpViewPager.adapter = GiftViewPagerAdapter(mPagerList, mActivity)
            setOvalLayout()
        }
    }


    @Subscribe
    fun OnSubscribBank(vm: SendGiftInChatViewModel.SendGiftVM) {
        if (vm.success) {
            mBinding.tvProudPeas.text = vm.obj?.data?.ownDedouNum//赠送后重新获取剩余得豆
        }
    }

    @Subscribe
    fun OnSubscribBank(vm: SendGiftInChatViewModel.GoToBuyProudPeasPageVM) {
        if (vm.success) {
            EventBus.getDefault().post(showBuyPeaFrg())
        }
    }

    /**
     * 设置圆点
     */
    fun setOvalLayout() {
        for (i in 0 until pageCount) {
            mBinding.pointContainer.addView(LayoutInflater.from(mActivity).inflate(R.layout.dot, null))
        }
        // 默认显示第一页
        mBinding.pointContainer.getChildAt(0).findViewById<View>(R.id.v_dot).setBackgroundResource(R.drawable.dot_selected)
    }

    private fun initListener() {

        mBinding.vpViewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {//滑动效果
            override fun onPageSelected(position: Int) {
            arr[0]?.notifyDataSetChanged()
            arr[1]?.notifyDataSetChanged()
            arr[2]?.notifyDataSetChanged()
            // 取消圆点选中
                mBinding.pointContainer.getChildAt(curIndex)
                        .findViewById<View>(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_normal)
                // 圆点选中
                mBinding.pointContainer.getChildAt(position)
                        .findViewById<View>(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_selected)
            curIndex = position
        }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

            override fun onPageScrollStateChanged(arg0: Int) {}
        })

        mBinding.ivClose.setOnClickListener { //关掉此礼物页面
            //clickListener?.onCloseBtnClick()
            EventBus.getDefault().post(CloseGiftFrg())
        }
        mBinding.textView20.setOnClickListener { //展示购买得豆页面
            //clickListener?.onCloseBtnClick()
            EventBus.getDefault().post(showBuyPeaFrg())
        }

    }


    //关闭礼物页面
    class CloseGiftFrg
    //展示购买得豆页面
    class showBuyPeaFrg

}