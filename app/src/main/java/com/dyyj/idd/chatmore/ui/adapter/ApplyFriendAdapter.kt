package com.dyyj.idd.chatmore.ui.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemApplyBinding
import com.dyyj.idd.chatmore.model.network.result.ApplyFriendResult
import com.dyyj.idd.chatmore.utils.DateFormatter
import com.dyyj.idd.chatmore.viewmodel.ApplyViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageSystemViewModel
import com.gt.common.gtchat.extension.niceChatContext
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus

class ApplyFriendAdapter : BaseAdapterV2<ApplyFriendResult.ApplyFriend>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    class ViewHolder(
            parent: ViewGroup?) : BaseViewHolder<ApplyFriendResult.ApplyFriend, ItemApplyBinding>(parent,
            R.layout.item_apply) {

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun onBindViewHolder(obj: ApplyFriendResult.ApplyFriend, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.model = obj

            if ("0".equals(obj.handleResult)) {
                mBinding.tvRefuse.visibility = View.VISIBLE
            } else {
                mBinding.tvRefuse.visibility = View.GONE
            }

            if (obj.isNew == true) mBinding.messageTipIv.visibility = View.VISIBLE else mBinding.messageTipIv.visibility = View.GONE
            doActionButtons(obj)
//      val formater = SimpleDateFormat("yyyyMMdd")
            mBinding.timeTv.text = DateFormatter.LongFormatTime(obj.requestTimestamp)
            mBinding.textView.setOnClickListener {
                if (obj.isNew == true) {
                    obj.isNew = false
                    mBinding.messageTipIv.visibility = View.GONE
                }
                val subscribe = mDataRepository.doApplyFriendAction(obj.requestId!!, "1").subscribe({
                    EventBus.getDefault().post(ApplyViewModel.RefreshFriend())
                    EventBus.getDefault().post(ApplyViewModel.RefreshData())
                    if (it.errorCode == 200) {
                        obj.handleResult = "1"
                        doActionButtons(
                                obj)
                        if (it.errorCode != 200) {
                            Toast.makeText(ChatApp.niceChatContext(), it.errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                                mBinding.niceChatContext(),
                                it.errorMsg,
                                Toast.LENGTH_SHORT).show()
                    }
                }, { })
                CompositeDisposable().add(subscribe)
            }

            mBinding.tvRefuse.setOnClickListener({
                if (obj.isNew == true) {
                    obj.isNew = false
                    mBinding.messageTipIv.visibility = View.GONE
                }
                val subscribe = mDataRepository.doApplyFriendAction(obj.requestId!!, "2").subscribe({

                    EventBus.getDefault().post(ApplyViewModel.RefreshFriend())
                    EventBus.getDefault().post(ApplyViewModel.RefreshData())
                    if (it.errorCode == 200) {
                        obj.handleResult = "2"
                        doActionButtons(
                                obj)
                        if (it.errorCode != 200) {
                            Toast.makeText(ChatApp.niceChatContext(), it.errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                                mBinding.niceChatContext(),
                                it.errorMsg,
                                Toast.LENGTH_SHORT).show()
                    }
                }, { })
                CompositeDisposable().add(subscribe)
            })

            mBinding.avatarIv.setOnClickListener {
                EventBus.getDefault().post(MessageSystemViewModel.ShowUserInfoVM(obj.requestUserId!!))
            }
        }

        private fun doActionButtons(obj: ApplyFriendResult.ApplyFriend) {
            if (obj.handleResult.equals("0")) {
                mBinding.textView.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
                mBinding.textView.text = "同意"
                mBinding.textView.isEnabled = true

            } else if (obj.handleResult.equals("1")) {
                mBinding.textView.text = "已同意"
                mBinding.textView.isEnabled = false
                mBinding.textView.setBackgroundResource(R.drawable.rect_rounded_left_right_arc4)
            } else if (obj.handleResult.equals("2")) {
                mBinding.textView.text = "已拒绝"
                mBinding.textView.isEnabled = false
                mBinding.textView.setBackgroundResource(R.drawable.rect_rounded_left_right_arc4)
            }
        }

    }
}