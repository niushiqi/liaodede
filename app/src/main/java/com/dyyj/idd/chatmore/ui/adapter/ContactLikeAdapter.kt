package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemContactLikeBinding
import com.dyyj.idd.chatmore.model.network.result.ContactUser

class ContactLikeAdapter : BaseAdapterV2<ContactUser>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<ContactUser, ItemContactLikeBinding>(
            viewGroup, R.layout.item_contact_like) {
        override fun onBindViewHolder(obj: ContactUser, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.textView57.text = obj.name
        }
    }
}