package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPlazaFlowCardBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult

/**
 * 附加上拉加载view 的recycleView 适配器
 */
abstract class LoadMoreAdapter<T> : BaseAdapterV2<T>() {
    val TYPE_LOADING = -1

    var isLoadMore = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return LoadingViewHolder(p0)
    }

    override fun getItemCount(): Int {
        if (isLoadMore) return super.getItemCount() + 1
        return super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {

        if (isLoadMore && position == itemCount - 1) return TYPE_LOADING

        return getItemViewTypeSelf(position)
    }

    override fun moreData(list: List<T>) {
        if (isLoadMore) notifyItemRemoved(itemCount - 1)
        super.moreData(list)
    }

    /**
     * 代替getItemViewType
     */
    open fun getItemViewTypeSelf(position: Int): Int {
        return super.getItemViewType(position)
    }

    /**
     * 正在加载
     */
    inner class LoadingViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<PlazaTopicListResult.Topic, ItemPlazaFlowCardBinding>(
            viewGroup, R.layout.layout_bottom_load_more) {

        override fun onBindViewHolder(obj: PlazaTopicListResult.Topic, position: Int) {
            super.onBindViewHolder(obj, position)
        }

    }

}