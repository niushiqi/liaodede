package com.dyyj.idd.chatmore.ui.user.fragment

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentSendGiftBinding
import com.dyyj.idd.chatmore.model.mqtt.result.SendGiftOnChatResultOwn
import com.dyyj.idd.chatmore.ui.adapter.GiftGridAdapter
import com.dyyj.idd.chatmore.ui.adapter.GiftViewPagerAdapter
import com.dyyj.idd.chatmore.ui.main.fragment.CallOutgoingFragment
import com.dyyj.idd.chatmore.viewmodel.SendGiftInVoiceViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
* author : zwj
* time   : 2019/02/11
* desc   : 语音聊天时 礼物列表
*/
class SendGiftFragmentInVoice : BaseFragment<FragmentSendGiftBinding, SendGiftInVoiceViewModel>() {


    //盛放礼物页面
    private val arr = arrayOfNulls<GiftGridAdapter>(3)

    /*总的页数*/
    private var pageCount: Int = 0
    /*每一页显示的个数*/
    private val pageSize = 8
    /*当前显示的是第几页*/
    private var curIndex = 0

    companion object {
        var mInstance: SendGiftFragmentInVoice? = null
        //var matchStatusTitle: String? = null
        var mToUserid : String? = ""
        /**
         * 单例
         */
        fun instance(toUserid : String?): SendGiftFragmentInVoice {
            mToUserid = toUserid
            if (mInstance == null) {
                return SendGiftFragmentInVoice()
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
        return R.layout.fragment_send_gift
    }

    override fun onViewModel(): SendGiftInVoiceViewModel {
        return SendGiftInVoiceViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initData()
        initListener()
    }

    private fun initData() {
        mViewModel.getChatGiftsList()//获取礼物列表
    }

    @Subscribe
    fun OnSubscribBank(vm: SendGiftInVoiceViewModel.ChatGiftsDataVM) {
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
                    mViewModel.sendGift(CallOutgoingFragment.talkId!!,mToUserid!!, vm.obj.data?.giftList?.get((curIndex*8)+position)?.id!!)
                    if (vm.obj.data.ownDedouNum?.toInt()!! > vm.obj.data.giftList[(curIndex*8)+position].dedouNum!!) {//得豆足够
                        EventBus.getDefault().post(SendGiftOnChatResultOwn(vm.obj.data.giftList[(curIndex*8)+position].name,vm.obj.data.giftList[(curIndex*8)+position].icon))
                    }
                }
                mPagerList.add(gridview)
            }
            mBinding.vpViewPager.adapter = GiftViewPagerAdapter(mPagerList, mActivity)
            setOvalLayout()
        }
    }

    @Subscribe
    fun OnSubscribBank(vm: SendGiftInVoiceViewModel.SendGiftVM) {
        if (vm.success) {
            mBinding.tvProudPeas.text = vm.obj?.data?.ownDedouNum//赠送后重新获取剩余得豆
        }
    }

    @Subscribe
    fun OnSubscribBank(vm: SendGiftInVoiceViewModel.GoToBuyProudPeasPageVM) {
        if (vm.success) {
            EventBus.getDefault().post(SendGiftFragmentInVoice.showBuyPeaFrg())
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
            EventBus.getDefault().post(SendGiftFragmentInVoice.CloseGiftFrg())
        }
        mBinding.textView20.setOnClickListener { //展示购买得豆页面
            //clickListener?.onCloseBtnClick()
            EventBus.getDefault().post(SendGiftFragmentInVoice.showBuyPeaFrg())
        }
    }


    //关闭礼物页面
    class CloseGiftFrg
    //展示购买得豆页面
    class showBuyPeaFrg


}