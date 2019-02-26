package com.dyyj.idd.chatmore.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemRankRedBinding
import com.dyyj.idd.chatmore.model.network.result.RedTopData
import jp.wasabeef.glide.transformations.CropCircleTransformation

class RankRedAdapter : BaseAdapterV2<RedTopData.Data>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<RedTopData.Data, ItemRankRedBinding>(parent,
            R.layout.item_rank_red) {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(obj: RedTopData.Data, position: Int) {
            super.onBindViewHolder(obj, position)
            Glide.with(mContext).load(obj.avatar).asBitmap().transform(
                    CropCircleTransformation(mContext)).crossFade().error(R.drawable.bg_circle_black).placeholder(
                    R.drawable.bg_circle_black).into(mBinding.ivAvatar)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(mContext).load(obj.avatar).apply(requestOption).into(mBinding.ivAvatar)
            mBinding.txtName.text = obj.nickName
            mBinding.txtInfo.text = "红包 ${obj.opendRedEnvelopeNum}个"
            mBinding.txtFriendNum.text = obj.userCash.toString()
            mBinding.txtGoldNum.text = obj.userCoin.toString()
            if (position == 0) {
                mBinding.txtName.setTextColor(Color.parseColor("#EB3D40"))
                mBinding.txtInfo.setBackgroundResource(R.drawable.ic_bank_red)
                mBinding.txtNum.visibility = View.GONE
                mBinding.imgNum.visibility = View.VISIBLE
                mBinding.imgNum.setImageResource(R.drawable.ic_rank_red_num_1)
                mBinding.ivAvatarTop.visibility = View.VISIBLE
                mBinding.ivAvatarTop.setImageResource(R.drawable.ic_rank_red_img_1)
            } else if (position == 1) {
                mBinding.txtName.setTextColor(Color.parseColor("#FFC107"))
                mBinding.txtInfo.setBackgroundResource(R.drawable.ic_bank_yellow)
                mBinding.txtNum.visibility = View.GONE
                mBinding.imgNum.visibility = View.VISIBLE
                mBinding.imgNum.setImageResource(R.drawable.ic_rank_red_num_2)
                mBinding.ivAvatarTop.visibility = View.VISIBLE
                mBinding.ivAvatarTop.setImageResource(R.drawable.ic_rank_red_img_2)
            } else if (position == 2) {
                mBinding.txtName.setTextColor(Color.parseColor("#339CFE"))
                mBinding.txtInfo.setBackgroundResource(R.drawable.ic_bank_blue)
                mBinding.txtNum.visibility = View.GONE
                mBinding.imgNum.visibility = View.VISIBLE
                mBinding.imgNum.setImageResource(R.drawable.ic_rank_red_num_3)
                mBinding.ivAvatarTop.visibility = View.VISIBLE
                mBinding.ivAvatarTop.setImageResource(R.drawable.ic_rank_red_img_3)
            } else {
                mBinding.txtName.setTextColor(Color.parseColor("#333333"))
                mBinding.txtInfo.setBackgroundResource(R.drawable.ic_bank_gray)
                mBinding.txtNum.visibility = View.VISIBLE
                mBinding.imgNum.visibility = View.GONE
                mBinding.ivAvatarTop.visibility = View.GONE
                mBinding.txtNum.text = "${position + 1}"
            }
        }

    }

}