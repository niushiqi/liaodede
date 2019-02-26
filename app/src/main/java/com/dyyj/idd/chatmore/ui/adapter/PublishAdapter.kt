package com.dyyj.idd.chatmore.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemAddPublishBinding
import com.dyyj.idd.chatmore.ui.user.photo.Image
import com.dyyj.idd.chatmore.ui.user.photo.ImageAdapter
import com.dyyj.idd.chatmore.utils.DeviceUtils
import java.io.File

class PublishAdapter: BaseAdapterV2<Image>() {

    private var listener: ImageAdapter.ITakePicListener? = null

    fun setTakePicListener(listner: ImageAdapter.ITakePicListener) {
        this.listener = listner
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
            parent: ViewGroup?) : BaseViewHolder<Image, ItemAddPublishBinding>(parent,
            R.layout.item_add_publish) {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(obj: Image, position: Int) {
            super.onBindViewHolder(obj, position)
            val wh = (DeviceUtils.getScreenWidth(mContext.resources) - DeviceUtils.dp2px(mContext.resources, 20F)) / 3
            mBinding.clItem.layoutParams = RecyclerView.LayoutParams(wh.toInt(), wh.toInt())
            if (obj.type == 1) {
                mBinding.ivPhoto.setImageResource(R.drawable.ic_publish_add)
            } else {
                val url = Uri.decode(Uri.fromFile(File(obj.path)).toString())
                Glide.with(mContext).load(url).centerCrop().placeholder(R.drawable.ic_default_img).error(R.drawable.ic_default_img).into(mBinding.ivPhoto)
//                val requestOption = RequestOptions().centerCrop()
//                requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//                Glide.with(mContext).load(url).apply(requestOption).into(mBinding.ivPhoto)
            }
            mBinding.clItem.setOnClickListener {
                if (obj.type == 1) {
                    listener?.onTake()
                }
            }
        }
    }

}