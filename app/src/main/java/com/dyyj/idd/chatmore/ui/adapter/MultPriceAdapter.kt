package com.dyyj.idd.chatmore.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult

class MultPriceAdapter(data: List<RecycleShopResult.Data.Dedou>?): BaseQuickAdapter<RecycleShopResult.Data.Dedou,BaseViewHolder>(R.layout.item_peas_price, data) {

    override fun convert(helper: BaseViewHolder?, item: RecycleShopResult.Data.Dedou?) {
        helper?.setText(R.id.tv_buy_pea_nums, item?.goodsDesc!!.replace("得得豆X",""))
        helper?.setText(R.id.tv_pay_cash, "￥${item?.goodsPrice!!}")
        helper?.addOnClickListener(R.id.cl_container)
        if (null != item?.goodsPromotion || !"".equals(item?.goodsPromotion)) {
            helper?.setText(R.id.tv_promotion,item?.goodsPromotion)
        }
        if (item?.isSelected != null && item.isSelected == 1) {
            helper?.setBackgroundRes(R.id.cl_container,R.drawable.shape_rect_gold_stroke)
        }else{
            helper?.setBackgroundRes(R.id.cl_container,R.drawable.shape_rect_normal_stroke)
        }
    }

}