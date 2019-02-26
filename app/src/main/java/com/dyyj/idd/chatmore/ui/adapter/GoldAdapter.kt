package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemWalletGoldBinding
import com.dyyj.idd.chatmore.model.network.result.CoinSummaryResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/29
 * desc   :
 */
class GoldAdapter() : BaseAdapterV2<CoinSummaryResult.Data.CoinHistory>() {
  var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

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
      viewGroup: ViewGroup?) : BaseViewHolder<CoinSummaryResult.Data.CoinHistory, ItemWalletGoldBinding>(
      viewGroup, R.layout.item_wallet_gold) {
    override fun onBindViewHolder(obj: CoinSummaryResult.Data.CoinHistory, position: Int) {
      super.onBindViewHolder(obj, position)
      mBinding.model = obj
      mBinding.timeTv.text = format.format(Date(obj.consumeTimestamp?.toLong()!!))
    }
  }

}