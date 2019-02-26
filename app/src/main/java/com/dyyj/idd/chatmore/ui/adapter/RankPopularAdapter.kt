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
import com.dyyj.idd.chatmore.databinding.ItemRankPopularBinding
import com.dyyj.idd.chatmore.model.network.result.PopularityTopResult
import jp.wasabeef.glide.transformations.CropCircleTransformation

class RankPopularAdapter : BaseAdapterV2<PopularityTopResult.PopularityTopData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<PopularityTopResult.PopularityTopData, ItemRankPopularBinding>(parent,
            R.layout.item_rank_popular) {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(obj: PopularityTopResult.PopularityTopData, position: Int) {
            super.onBindViewHolder(obj, position)
//            Glide.with(mContext).load(obj.avatar).asBitmap().transform(
//                    CropCircleTransformation()).crossFade().error(R.drawable.bg_circle_black).placeholder(
//                    R.drawable.bg_circle_black).into(mBinding.ivAvatar)
            Glide.with(mContext).load(obj.avatar).asBitmap().transform(
                    CropCircleTransformation(mContext)).crossFade().error(
                    R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(mContext).load(obj.avatar).apply(requestOption).into(mBinding.ivAvatar)
            mBinding.txtName.text = obj.nickName
            mBinding.txtLevel.text = obj.friendsLevel
            mBinding.txtPopularNum.text = obj.friendsNum
            if (position == 0) {
                mBinding.txtName.setTextColor(Color.parseColor("#EB3D40"))
                mBinding.txtLevel.setBackgroundResource(R.drawable.ic_level_red)
                mBinding.txtNum.visibility = View.GONE
                mBinding.imgNum.visibility = View.VISIBLE
                mBinding.imgNum.setImageResource(R.drawable.ic_rank_pop_num_1)
                mBinding.ivAvatarTop.visibility = View.VISIBLE
                mBinding.ivAvatarTop.setImageResource(R.drawable.ic_rank_pop_img_1)
            } else if (position == 1) {
                mBinding.txtName.setTextColor(Color.parseColor("#FFC107"))
                mBinding.txtLevel.setBackgroundResource(R.drawable.ic_level_yellow)
                mBinding.txtNum.visibility = View.GONE
                mBinding.imgNum.visibility = View.VISIBLE
                mBinding.imgNum.setImageResource(R.drawable.ic_rank_pop_num_2)
                mBinding.ivAvatarTop.visibility = View.VISIBLE
                mBinding.ivAvatarTop.setImageResource(R.drawable.ic_rank_pop_img_2)
            } else if (position == 2) {
                mBinding.txtName.setTextColor(Color.parseColor("#339CFE"))
                mBinding.txtLevel.setBackgroundResource(R.drawable.ic_level_blue)
                mBinding.txtNum.visibility = View.GONE
                mBinding.imgNum.visibility = View.VISIBLE
                mBinding.imgNum.setImageResource(R.drawable.ic_rank_pop_num_3)
                mBinding.ivAvatarTop.visibility = View.VISIBLE
                mBinding.ivAvatarTop.setImageResource(R.drawable.ic_rank_pop_img_3)
            } else {
                mBinding.txtName.setTextColor(Color.parseColor("#333333"))
                mBinding.txtLevel.setBackgroundResource(R.drawable.ic_level_normal2)
                mBinding.txtNum.visibility = View.VISIBLE
                mBinding.imgNum.visibility = View.GONE
                mBinding.ivAvatarTop.visibility = View.GONE
                mBinding.txtNum.text = "${position + 1}"
            }
        }
    }

}