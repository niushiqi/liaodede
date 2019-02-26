package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.AlipayOrderResult
import com.dyyj.idd.chatmore.model.network.result.CashSumaryResult
import com.dyyj.idd.chatmore.model.network.result.PayByCashResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class PayActionViewModel : ViewModel() {
    var currentSelect = -1

    /**
     * 现金概要
     */
    fun netWalletCash() {
        mDataRepository.postCashSummary()
                .subscribe({
                    EventBus.getDefault().post(CashVM(obj = it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(CashVM())
                })
    }

    class CashVM(val obj: CashSumaryResult.Data? = null)

    /**
     * 获取支付宝订单
     */
    fun netAlipayOrder(goodsId:String) {
        mDataRepository.postAlipayOrder(goodsId)
                .subscribe({
                    EventBus.getDefault().post(AlipayOrderVM(obj = it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(AlipayOrderVM())
                })
    }

    class AlipayOrderVM(val obj: AlipayOrderResult.Data? = null)

    /**
     * 现金余额支付
     */
    fun netPayByCash(goodsId:String) {
        mDataRepository.postPayByCash(goodsId)
                .subscribe({
                    EventBus.getDefault().post(PayByCashVM(obj = it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(PayByCashVM())
                })
    }

    /**
     * 获取订单信息
     */
    fun netAliPayInfo(goodsId:String) {
        mDataRepository.netAliPayInfo(goodsId)
                .subscribe({
                    EventBus.getDefault().post(PayByCashVM(obj = it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(PayByCashVM())
                })
    }

    class PayByCashVM(val obj: PayByCashResult.Data? = null)

}