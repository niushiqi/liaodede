package com.dyyj.idd.chatmore.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemContactsBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.ContactsResult
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.MessageFragmentViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageSystemViewModel
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 好友列表
 */
class ContactsAdapter : BaseAdapterV2<ContactsResult.Data.Friends>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<ContactsResult.Data.Friends, ItemContactsBinding>(parent,
            R.layout.item_contacts) {
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(obj: ContactsResult.Data.Friends, position: Int) {
            super.onBindViewHolder(obj, position)

            mBinding.model = obj

//            if (obj.newReward == 1) {
//                mBinding.clickRightTv.visibility = View.VISIBLE
//                mBinding.clickRightTv.text = "领取"
//                mBinding.clickRightTv.setTextColor(Color.parseColor("#884D00"))
//                mBinding.clickRightTv.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
//                mBinding.clickRightTv.setOnClickListener {
////                    addFriend()
//                }
//            } else if (obj.talkingStatus == 1) {
//                mBinding.clickRightTv.visibility = View.VISIBLE
//                mBinding.clickRightTv.text = "通话中"
//                mBinding.clickRightTv.setTextColor(Color.parseColor("#01A9F0"))
//                mBinding.clickRightTv.setBackgroundColor(mContext.resources.getColor(android.R.color.transparent))
//            } else {
//                mBinding.clickRightTv.visibility = View.GONE
//            }

            val percent33 = 100 / 3.00
            val percentExperience = obj.friendshipExperience.toDouble() / 100 * percent33
            if (obj.friendshipLevel?.toInt()?:0 == 0) {
                mBinding.csProgress.setNodeOkNums(0)
                mBinding.csProgress.setProgress(0.00)
                mBinding.csProgress.setNodeSrc(R.drawable.img_ok_yellow)
                mBinding.csProgress.setProgressColor(Color.parseColor("#F8CF2A"))
            } else if (obj.friendshipLevel?.toInt()?:0 < 4) {
                val index = obj.friendshipLevel?.toInt()!!
                val progress = percent33 * (index - 1) + percentExperience
                mBinding.csProgress.setProgress(progress)
                mBinding.csProgress.setNodeOkNums(index)
                mBinding.csProgress.setNodeSrc(R.drawable.img_ok_yellow)
                mBinding.csProgress.setProgressColor(Color.parseColor("#F8CF2A"))
            } else if (obj.friendshipLevel?.toInt()?:0 < 7) {
                val index = obj.friendshipLevel?.toInt()!! - 3
                val progress = percent33 * (index - 1) + percentExperience
                mBinding.csProgress.setProgress(progress)
                mBinding.csProgress.setNodeOkNums(index)
                mBinding.csProgress.setNodeSrc(R.drawable.img_ok_blue)
                mBinding.csProgress.setProgressColor(Color.parseColor("#1DB2F2"))
            } else if (obj.friendshipLevel?.toInt()?:0 < 10) {
                val index = obj.friendshipLevel?.toInt()!! - 6
                val progress = percent33 * (index - 1) + percentExperience
                mBinding.csProgress.setProgress(progress)
                mBinding.csProgress.setNodeOkNums(index)
                mBinding.csProgress.setNodeSrc(R.drawable.img_ok_red)
                mBinding.csProgress.setProgressColor(Color.parseColor("#FD656A"))
            } else {
                mBinding.csProgress.setNodeOkNums(4)
                mBinding.csProgress.setProgress(100.00)
                mBinding.csProgress.setNodeSrc(R.drawable.img_ok_red)
                mBinding.csProgress.setProgressColor(Color.parseColor("#FD656A"))
            }

            mBinding.levelTv.text = obj.friendUserLevel
            mBinding.nameTv.text = obj.friendNickname
            mBinding.textView4.text = "F${obj.friendshipLevel}"

            val gender = if (obj.friendGender == "1") "男" else "女"
            mBinding.ageTv.text = "$gender/${obj.friendAge}岁"
            mBinding.root.setOnClickListener {
                if(obj.friendshipSource == ChatActivity.FROM_TYPE_SQUARE) {
                    //来自广场
                    obj.friendUserId?.let {
                        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_fridlist", it))
                        ChatActivity.startBySquare(mContext, it, obj.friendNickname, obj.friendAvatar, null)
                    }

                } else {
                    //来自其他
                    obj.friendUserId?.let {
                        val b = Bundle()
                        b.putString("friendshipLevel", obj.friendshipLevel)
                        b.putInt("fromType", obj.friendshipSource)
                        b.putBoolean("isFriend", true)
                        ChatActivity.start(mContext, it, obj.friendNickname, obj.friendAvatar, b)
                        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_fridlist", it))
                    }
                }
            }
            mBinding.avatarIv.setOnClickListener {
                EventBus.getDefault().post(MessageFragmentViewModel.ShowUserInfoVM(obj.friendUserId!!))
            }
        }

    }

}