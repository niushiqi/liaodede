package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemWalletCashBinding
import com.dyyj.idd.chatmore.model.network.result.CashSumaryResult

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/29
 * desc   :
 */
class CashAdapter : BaseAdapterV2<CashSumaryResult.Data>() {


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return ViewHolder(parent)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is ViewHolder) {
      holder.onBindViewHolder(getList()[position], position)
    }
  }

  /**
   * 现金明细
   */
  inner class ViewHolder(
      viewGroup: ViewGroup?) : BaseViewHolder<CashSumaryResult.Data, ItemWalletCashBinding>(
      viewGroup, R.layout.item_wallet_cash) {
    override fun onBindViewHolder(obj: CashSumaryResult.Data, position: Int) {
      super.onBindViewHolder(obj, position)
//      mBinding.model = obj
    }
  }

}