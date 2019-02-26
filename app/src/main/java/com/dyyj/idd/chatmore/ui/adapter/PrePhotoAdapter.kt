package com.dyyj.idd.chatmore.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPrePhotoBinding
import com.dyyj.idd.chatmore.ui.user.photo.Image
import com.dyyj.idd.chatmore.utils.DeviceUtils
import java.io.File

class PrePhotoAdapter: BaseAdapterV2<Image>() {

    interface ISmallPhotoListener {
        fun onSelect(position: Int)
    }

    private var smallListener: ISmallPhotoListener? = null

    fun setSmallListener(listener: ISmallPhotoListener) {
        this.smallListener = listener
    }

    private var currentSelect: Int = 0

    fun setCurrentSelect(position: Int) {
        currentSelect = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<Image, ItemPrePhotoBinding>(parent,
            R.layout.item_pre_photo) {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(obj: Image, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.clItem.layoutParams = RecyclerView.LayoutParams(DeviceUtils.dp2px(mContext.resources, 70F).toInt(), DeviceUtils.dp2px(mContext.resources, 70F).toInt())
            val url = Uri.decode(Uri.fromFile(File(obj.path)).toString())
            Glide.with(mContext).load(url).centerCrop().placeholder(R.drawable.ic_default_img).error(R.drawable.ic_default_img).into(mBinding.ivImage)
//            val requestOption = RequestOptions().optionalCenterCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(mContext).load(url).apply(requestOption).into(mBinding.ivImage)
            if (currentSelect == position) {
                mBinding.ivCover.visibility = View.VISIBLE
            } else {
                mBinding.ivCover.visibility = View.GONE
            }

            mBinding.clItem.setOnClickListener {
                currentSelect = position
                notifyDataSetChanged()
                smallListener?.onSelect(position)
            }
        }
    }

}