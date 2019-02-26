package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemEverydayNewbieBinding
import com.dyyj.idd.chatmore.databinding.ItemEverydayTitleBinding
import com.dyyj.idd.chatmore.model.network.result.EverydayResult
import com.dyyj.idd.chatmore.ui.dialog.activity.ToastActivity

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 日常任务
 */
class EverydayAdapter : BaseAdapterV2<EverydayResult>() {


  companion object {
    const val COMPLETE = 1
    const val COMPLETED = 2
    const val GET = 0
    const val OPTIONOR = -1

    val TYPE_TITLE = 0
    val TYPE_CONTENT = 1
  }
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return if (viewType == TYPE_TITLE) {
      ViewHolderTitle(parent)
    } else {
      ViewHolderNewbie(parent)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is ViewHolderNewbie) {
      holder.onBindViewHolder(getList()[position], position)
    }else if (holder is ViewHolderTitle) {
      holder.onBindViewHolder(getList()[position], position)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (getList()[position].type == TYPE_TITLE) {
      TYPE_TITLE
    } else {
      TYPE_CONTENT
    }

  }

  class ViewHolderNewbie(val viewGroup: ViewGroup?) : BaseViewHolder<EverydayResult, ItemEverydayNewbieBinding>(
      viewGroup, R.layout.item_everyday_newbie) {
    override fun onBindViewHolder(obj: EverydayResult, position: Int) {
      super.onBindViewHolder(obj, position)

      mBinding.model = obj

      mBinding.nameTv.text = obj.title
      mBinding.cashTv.setText(Html.fromHtml("现金<font color='#666666'>${obj.rewardCash}</font>元" + (if (obj.status == OPTIONOR) " 或" else "")),
                              null)
      mBinding.coinTv.setText(Html.fromHtml("金币<font color='#666666'>${obj.rewardCoin}</font>枚"),
                              null)
      mBinding.stoneTv.setText(Html.fromHtml("魔石<font color='#666666'>${obj.rewardStone}</font>颗"),
                               null)

      if (TextUtils.isEmpty(obj.rewardCash)) {
        mBinding.cashTv.visibility = View.GONE
      } else {
        mBinding.cashTv.visibility = View.VISIBLE
      }
      if (TextUtils.isEmpty(obj.rewardCoin)) {
        mBinding.coinTv.visibility = View.GONE
      } else {
        mBinding.coinTv.visibility = View.VISIBLE
      }
      if (TextUtils.isEmpty(obj.rewardStone)) {
        mBinding.stoneTv.visibility = View.GONE
      } else {
        mBinding.stoneTv.visibility = View.VISIBLE
      }

      mBinding.getBtn.setOnClickListener {
        val rewardStone = if (TextUtils.isEmpty(obj.rewardStone)) null else obj.rewardStone?.toDouble()
        val rewardCoin = if (TextUtils.isEmpty(obj.rewardCoin)) null else obj.rewardCoin?.toDouble()
        val rewardCash = if (TextUtils.isEmpty(obj.rewardCash)) null else obj.rewardCash?.toDouble()
        viewGroup?.context?.let {
          ToastActivity.start(it, rewardStone = rewardStone,
                              rewardCoin = rewardCoin,
                              rewardCash = rewardCash)
          mBinding.getBtn.visibility = View.GONE
          mBinding.completedBtn.visibility = View.VISIBLE
        }
      }
    }
  }

  /**
   * 标题
   */
  class ViewHolderTitle(viewGroup: ViewGroup?):BaseViewHolder<EverydayResult, ItemEverydayTitleBinding>(viewGroup, R.layout.item_everyday_title){

    override fun onBindViewHolder(obj: EverydayResult, position: Int) {
      super.onBindViewHolder(obj, position)
      mBinding.titleTv.setText(obj.title, null)
    }
  }
}