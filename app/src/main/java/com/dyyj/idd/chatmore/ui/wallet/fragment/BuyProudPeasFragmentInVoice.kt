package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.alipay.sdk.app.PayTask
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentBuyProudPeasInVoiceBinding
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult
import com.dyyj.idd.chatmore.ui.adapter.MultPriceAdapter
import com.dyyj.idd.chatmore.utils.PayResult
import com.dyyj.idd.chatmore.viewmodel.BuyProudPeasInVoiceViewModel
import com.gt.common.gtchat.extension.niceToast
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * createBy  2019/1/11 zwj
 * desc 买得豆页面
 */
class BuyProudPeasFragmentInVoice : BaseFragment<FragmentBuyProudPeasInVoiceBinding, BuyProudPeasInVoiceViewModel>() {

    private val SDK_PAY_FLAG = 1

    var paidFor: List<RecycleShopResult.Data.Dedou>? = null
    companion object {
        var mInstance: BuyProudPeasFragmentInVoice? = null
        var matchStatusTitle: String? = null
        /**
         * 单例
         */
        fun instance(): BuyProudPeasFragmentInVoice {
            if (mInstance == null) {
                return BuyProudPeasFragmentInVoice()
            }
            return mInstance!!
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_buy_proud_peas_in_voice
    }

    override fun onViewModel(): BuyProudPeasInVoiceViewModel {
        return BuyProudPeasInVoiceViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initView()
        initData()
        initListener()
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

    private fun initView() {
    }

    private fun initData() {
        mViewModel.netRecycleView()
    }

    private fun initListener() {
        mBinding.ivClose.setOnClickListener {
            EventBus.getDefault().post(CloseBuyPeaFrg())
        }
        //支付
        mBinding.okAndPay.setOnClickListener {
            if (paidFor != null) {
                paidFor?.forEachIndexed { index, dedou ->
                    if (paidFor!![index].isSelected == 1) {//当前选中
                        //PayActionActivity.start(mActivity, paidFor!![index].goodsId!!,paidFor!![index].goodsPrice!!,paidFor!![index].goodsDesc!! )
                        mViewModel.netAlipayOrder(paidFor!![index].goodsId!!)
                    }
                }
            }else{
                mActivity.niceToast("请重新获取支付项")
            }
        }
    }

    //关闭礼物页面
    class CloseBuyPeaFrg

    @Subscribe
    fun onDataSuc(obj: BuyProudPeasInVoiceViewModel.PeaListVM) {
        initRecycleView(obj)
    }


    fun initRecycleView(obj: BuyProudPeasInVoiceViewModel.PeaListVM) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
                ContextCompat.getColor(mActivity, android.R.color.transparent)).sizeResId(
                R.dimen.item_decoration_2px).build()
        mBinding.rvRecyView.addItemDecoration(decoration)
        mBinding.rvRecyView.layoutManager = LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false)

        if (obj.obj != null && obj.obj.size > 0) {
            obj.obj[0].isSelected = 1//默认选中第一条
            paidFor = obj.obj
            mBinding.rvRecyView.adapter = MultPriceAdapter(obj.obj)
        }
        (mBinding.rvRecyView.adapter as MultPriceAdapter).setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.cl_container) {
                obj.obj?.forEachIndexed { index, dedou ->
                    if (index == position) {
                        obj.obj[index].isSelected = 1
                    }else{
                        obj.obj[index].isSelected = 0
                    }
                }
                paidFor = obj.obj
                adapter.notifyDataSetChanged()
            }
        }
    }


    var orderBizNo = ""
    var orderGoodsId = ""
    @Subscribe
    fun onAlipayOrderVM(obj: BuyProudPeasInVoiceViewModel.AlipayOrderVM) {
        var req_data = obj.obj?.orderString
        orderBizNo = obj.obj?.orderBizNo!!
        orderGoodsId = obj.goodsId
        val payRunnable = Runnable {
            val alipay = PayTask(mActivity)
            val result = alipay.payV2(req_data, true)
            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }
        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val obj = msg.obj
            val s = obj.toString()
            val payResult = PayResult(msg.obj as Map<String, String>)

            val resultInfo = payResult.getResult()// 同步返回需要验证的信息
            val resultStatus = payResult.getResultStatus()
            if (TextUtils.equals(resultStatus, "9000")) {
//                niceToast("支付成功")
                //回调接口再次确认订单信息
                dialog(0)
            } else {
                if (TextUtils.equals(resultStatus, "8000")) {
                    mActivity.niceToast("支付结果确认中")
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    mActivity.niceToast("您取消了支付")
                } else {
                    mActivity.niceToast("支付失败")
                }
            }
        }
    }


    fun dialog(index:Int){
        val dialog1 = Dialog(mActivity)

        val contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.dialog_pay_countdown, null)
        dialog1.setContentView(contentView)
        dialog1.setCanceledOnTouchOutside(false)
        dialog1.show()
        if(index == 0){
            var  tv_name = contentView.findViewById<TextView>(R.id.tv_name) as TextView
            tv_name.text = "购买得得豆支付中"
        }
        var  num_tv = contentView.findViewById<TextView>(R.id.tv_pay_countdown) as TextView
        val timer = Timer()
        var time:Int = 3
        var task: TimerTask = object : TimerTask() {
            override fun run() {
                mActivity.runOnUiThread {
                    num_tv.text = time.toString()
                    time--
                    if(time < 0){
                        mActivity.niceToast("支付成功")
                        dialog1.dismiss()
                        timer.cancel()
                        if(index == 0){
                            mViewModel.netAliPayInfo(orderBizNo)
                        }
                    }
                }
            }
        }
        timer.schedule(task, 100, 1000)
    }


}