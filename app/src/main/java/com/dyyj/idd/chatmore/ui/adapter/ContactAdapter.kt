package com.dyyj.idd.chatmore.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemContactUserBinding
import com.dyyj.idd.chatmore.model.network.result.ContactUser
import org.greenrobot.eventbus.EventBus

class ContactAdapter : BaseAdapterV2<ContactUser>() {

    open var posSelect = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    fun getFirstPositionByChar(sign: Char): Int {
        if (sign == '↑' || sign == '☆') {
            return 0
        }
        for (i in getList().indices) {
            if (getList().get(i).headLetter.value === sign) {
                return i
            }
        }
        return -1
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<ContactUser, ItemContactUserBinding>(
            viewGroup, R.layout.item_contact_user) {
        override fun onBindViewHolder(obj: ContactUser, position: Int) {
            super.onBindViewHolder(obj, position)
            if (position == 0) {
                mBinding.txtZimu.visibility = View.VISIBLE
            } else if (getList()[position - 1].headLetter.value.toString() != obj.headLetter.value.toString()) {
//            } else if (!TextUtils.equals(getList()[position - 1].headLetter, obj.headLetter)) {
                mBinding.txtZimu.visibility = View.VISIBLE
            } else {
                mBinding.txtZimu.visibility = View.GONE
            }
            mBinding.ivSelect.setImageResource(if (position == posSelect) R.drawable.ic_contact_selected else R.drawable.ic_contact_select)
            mBinding.txtZimu.text = obj.headLetter.value.toString()
            mBinding.textView53.text = obj.name
            mBinding.itemView.setOnClickListener {
                posSelect = position
                EventBus.getDefault().post(ShowSendVM(obj))
                notifyDataSetChanged()
            }
        }
    }

    class ShowSendVM(val user: ContactUser)

}