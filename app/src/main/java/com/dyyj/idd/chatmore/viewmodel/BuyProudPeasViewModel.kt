package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.AlipayOrderResult
import com.dyyj.idd.chatmore.model.network.result.PayByCashResult
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 等级任务
 */
class BuyProudPeasViewModel : ViewModel() {


    fun netRecycleView() {
        mDataRepository.postShopRecycleData()?.subscribe({
            EventBus.getDefault().post(PeaListVM(obj = it.data?.deDou))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            niceToast("请求失败")
        })
    }

    class PeaListVM(val obj: List<RecycleShopResult.Data.Dedou>? = null)

    //class ownDeDouNum(val obj: String? = null)

    /**
     * 获取订单信息
     */
    fun netAliPayInfo(goodsId:String) {
        mDataRepository.netAliPayInfo(goodsId)
                .subscribe({
                    EventBus.getDefault().post(PayActionViewModel.PayByCashVM(obj = it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(PayActionViewModel.PayByCashVM())
                })
    }


    /**
     * 获取支付宝订单
     */
    fun netAlipayOrder(goodsId:String) {
        mDataRepository.postAlipayOrder(goodsId)
                .subscribe({
                    EventBus.getDefault().post(AlipayOrderVM(obj = it.data, goodsId = goodsId))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    niceToast(it.message)
                })
    }




    class PayByCashVM(val obj: PayByCashResult.Data? = null)




    class AlipayOrderVM(val obj: AlipayOrderResult.Data? = null, val goodsId: String)
}