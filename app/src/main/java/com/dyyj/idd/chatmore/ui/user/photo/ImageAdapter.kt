package com.dyyj.idd.chatmore.ui.user.photo

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemPhotoBinding
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.gt.common.gtchat.extension.niceChatContext
import java.io.File

class ImageAdapter : BaseAdapterV2<Image>() {

    interface ITakePicListener {
        fun onTake()
    }

    private var photoList: ArrayList<Image> = arrayListOf()
    private var listener: ITakePicListener? = null

    open fun getSelectList() : ArrayList<Image> {
        return photoList
    }

    open fun setTakePicListener(listener: ITakePicListener) {
        this.listener = listener
    }

    open fun addSelectImage(image: Image) {
        photoList.add(image)
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

    fun refreshImages(obj: ArrayList<Image>) {
        photoList = obj
        notifyDataSetChanged()
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<Image, ItemPhotoBinding>(
            viewGroup, R.layout.item_photo) {
        override fun onBindViewHolder(obj: Image, position: Int) {
            super.onBindViewHolder(obj, position)
            val photoWH = (DeviceUtils.getScreenWidth(mContext.resources) - DeviceUtils.dp2px(mContext.resources, 3F)) / 4
            mBinding.clItem.layoutParams = RecyclerView.LayoutParams(photoWH.toInt(), photoWH.toInt())
            if (obj.type == 1) {
                mBinding.ivBottom.setBackgroundResource(R.drawable.ic_photo_default)
                mBinding.ivBottom.setImageResource(R.drawable.ic_photo_default_center)
                mBinding.ivSelect.visibility = View.GONE
                mBinding.clItem.setOnClickListener {
//                    EventBus.getDefault().post(TakePic())
                    if (photoList.size >= 9) {
                        Toast.makeText(niceChatContext(), "最多选择9张图片", Toast.LENGTH_SHORT).show()
                    } else {
                        listener?.onTake()
                    }
                }
            } else {
                mBinding.ivBottom.setBackgroundColor(mContext.resources.getColor(android.R.color.transparent))
                val url = Uri.decode(Uri.fromFile(File(obj.path)).toString())
                Glide.with(mContext).load(url).centerCrop().placeholder(R.drawable.ic_default_img).error(R.drawable.ic_default_img).into(mBinding.ivBottom)
//                val requestOption = RequestOptions().centerCrop()
//                requestOption.error(R.drawable.ic_default_img).placeholder(R.drawable.ic_default_img)
//                Glide.with(mContext).load(url).apply(requestOption).into(mBinding.ivBottom)
                mBinding.ivSelect.visibility = View.VISIBLE
                if (photoList.contains(obj)) {
                    mBinding.ivSelect.setImageResource(R.drawable.ic_photo_selected)
                } else {
                    mBinding.ivSelect.setImageResource(R.drawable.ic_photo_select)
                }
                mBinding.ivSelect.setOnClickListener {
                    if (photoList.contains(obj)) {
                        photoList.remove(obj)
                        notifyDataSetChanged()
                    } else {
                        if (photoList.size >= 9) {
                            Toast.makeText(niceChatContext(), "最多选择9张图片", Toast.LENGTH_SHORT).show()
                        } else {
                            photoList.add(obj)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}