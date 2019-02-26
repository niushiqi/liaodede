package com.dyyj.idd.chatmore.ui.user.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.alipay.sdk.app.PayTask
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPayActionBinding
import com.dyyj.idd.chatmore.ui.dialog.activity.PayResultDialogActivity
import com.dyyj.idd.chatmore.utils.PayResult
import com.dyyj.idd.chatmore.viewmodel.PayActionViewModel
import com.gt.common.gtchat.extension.niceToast
import com.othershe.nicedialog.NiceDialog
import org.greenrobot.eventbus.Subscribe
import java.util.*


class PayActionActivity : BaseActivity<ActivityPayActionBinding, PayActionViewModel>() {


    companion object {
        const val TYPE_STONE: Int = 1
        const val TYPE_PEA: Int = 2
        const val GOODSID = "GOODSID"
        const val GOODSPRICE = "GOODSPRICE"
        const val GOODSDETAIL = "GOODSDETAIL"

        fun start(context: Context, goodsID: String, goodsPrice: String, goodsDetail: String) {
            val intent = Intent(context, PayActionActivity::class.java)
            intent.putExtra(GOODSID, goodsID)
            intent.putExtra(GOODSPRICE, goodsPrice)
            intent.putExtra(GOODSDETAIL, goodsDetail)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_pay_action
    }

    override fun onViewModel(): PayActionViewModel {
        return PayActionViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    var prices: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "付款"
        mBinding.textView23.text = intent.getStringExtra(GOODSDETAIL)
        mBinding.textView24.text = "￥ " + intent.getStringExtra(GOODSPRICE)

        initBalanceValue()
        initListener()

        //设置默认选中支付宝
        mBinding.ivBalanceBtn1.setImageResource(R.drawable.ic_pay_select)
        mBinding.ivAlipayBtn2.setImageResource(R.drawable.ic_pay_selected)
        mViewModel.currentSelect = 1
    }

    private fun initBalanceValue() {
        mViewModel.netWalletCash()
    }

    //确认购买弹窗
    private fun isPayDialog(index: Int) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_is_pay)
                .setConvertListener { holder, dialog ->
                    //进行相关View操作的回调
                    holder.setOnClickListener(R.id.isPay_cancel_btn) {
                        dialog.dismiss()
                    }
                    holder.setOnClickListener(R.id.isPay_ok_btn) {
                        dialog.dismiss()
                        if (intent.getStringExtra(GOODSPRICE).toDouble() <= balanceNum) {
                            //mViewModel.netPayByCash(intent.getStringExtra(GOODSID))
                            dialog(index)
                        } else {
                            niceToast("余额不足，请重试")
                        }
                    }
                }
                .setDimAmount(0.8f)     //调节灰色背景透明度[0-1]，默认0.5f
                .setShowBottom(false)     //是否在底部显示dialog，默认flase
                .setWidth(220)
                .setHeight(150)
                .setOutCancel(false)
                //                .setAnimStyle(R.style.EnterExitAnimation)//设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
                .show(supportFragmentManager)     //显示dialog
    }

    private val SDK_PAY_FLAG = 1
    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.btnLogout.setOnClickListener {
            if (mViewModel.currentSelect == -1) {
                niceToast("请选择支付方式")
                return@setOnClickListener
            } else if (mViewModel.currentSelect == 0) { //余额支付
                isPayDialog(1)
            } else if (mViewModel.currentSelect == 1) { //支付宝支付
                mViewModel.netAlipayOrder(intent.getStringExtra(GOODSID))
            }

        }
        mBinding.constraintLayout2.setOnClickListener {
            mBinding.ivBalanceBtn1.setImageResource(R.drawable.ic_pay_selected)
            mBinding.ivAlipayBtn2.setImageResource(R.drawable.ic_pay_select)
            mViewModel.currentSelect = 0;
        }

        mBinding.constraintLayout3.setOnClickListener {
            mBinding.ivBalanceBtn1.setImageResource(R.drawable.ic_pay_select)
            mBinding.ivAlipayBtn2.setImageResource(R.drawable.ic_pay_selected)
            mViewModel.currentSelect = 1;
        }
    }

    var balanceNum = 0.00
    @Subscribe
    fun onCashVM(obj: PayActionViewModel.CashVM) {
        balanceNum = obj.obj?.socialNum!!.toDouble() + obj.obj?.inviteNum!!.toDouble()
        mBinding.tvBalanceValue.text = balanceNum.toString()

    }

    var orderBizNo = ""
    @Subscribe
    fun onAlipayOrderVM(obj: PayActionViewModel.AlipayOrderVM) {
        var req_data = obj.obj?.orderString
        orderBizNo = obj.obj?.orderBizNo!!
        val payRunnable = Runnable {
            val alipay = PayTask(this)
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

    @Subscribe
    fun onPayByCashVM(obj: PayActionViewModel.PayByCashVM) {
        var orderStatus = obj.obj?.orderStatus
        if ("0".equals(orderStatus)){
            niceToast("订单未处理")
        }else if("1".equals(orderStatus)){
            niceToast("订单处理中")
        }else if("2".equals(orderStatus)){
            PayResultDialogActivity.start(this,0,intent.getStringExtra(GOODSID),intent.getStringExtra(GOODSPRICE),intent.getStringExtra(GOODSDETAIL))
            finish()
        }else if("3".equals(orderStatus)){
            PayResultDialogActivity.start(this,1,intent.getStringExtra(GOODSID),intent.getStringExtra(GOODSPRICE),intent.getStringExtra(GOODSDETAIL))
            finish()
        }
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
//                mViewModel.netAliPayInfo(orderBizNo)
                dialog(0)

            } else {
                if (TextUtils.equals(resultStatus, "8000")) {
                    niceToast("支付结果确认中")
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    niceToast("您取消了支付")
                } else {
                    niceToast("支付失败")
                }
            }
        }
    }

     fun dialog(index:Int){
        val dialog1 = Dialog(this)

        val contentView = LayoutInflater.from(this).inflate(
                R.layout.dialog_pay_countdown, null)
        dialog1.setContentView(contentView)
        dialog1.setCanceledOnTouchOutside(false)
        dialog1.show()
         val witch_goods = intent.getStringExtra(GOODSDETAIL)
         if(witch_goods.contains("魔石")){
             var  tv_name = contentView.findViewById<TextView>(R.id.tv_name) as TextView
             tv_name.text = "购买魔石支付中"
         }else if(witch_goods.contains("得得豆")){
             var  tv_name = contentView.findViewById<TextView>(R.id.tv_name) as TextView
             tv_name.text = "购买得得豆支付中"
         }else{
             var  tv_name = contentView.findViewById<TextView>(R.id.tv_name) as TextView
             tv_name.text = "购买匹配卡支付中"
         }
        var  num_tv = contentView.findViewById<TextView>(R.id.tv_pay_countdown) as TextView
        val timer = Timer()
        var time:Int = 3
        var task: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    num_tv.text = time.toString()
                    time--
                   if(time < 0){
                       dialog1.dismiss()
                       timer.cancel()
                       if(index == 0){
                           mViewModel.netAliPayInfo(orderBizNo)
                       }else if(index == 1){
                           mViewModel.netPayByCash(intent.getStringExtra(GOODSID))
                       }


                   }
                }
            }
        }
        timer.schedule(task, 100, 1000);

    }


}