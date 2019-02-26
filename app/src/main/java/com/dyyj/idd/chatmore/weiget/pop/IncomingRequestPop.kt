package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopIncomingRequestBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.IncomingRequestViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/22
 * desc   : 聊天界面来电
 */
class IncomingRequestPop(context: Context?) : BaseTipPop<PopIncomingRequestBinding, IncomingRequestViewModel>(context) {
    fun initData(fromUserId: String, toUserId: String, username: String, type: String, avatar: String, fromType: String) {
        mBinding.declineTv.setOnClickListener {
            //拒绝
            //openChatActivity(toUserId, fromUserId, username, avatar)
            /*val subscribe = Flowable.interval(1, 1, TimeUnit.SECONDS).take(1).observeOn(
                    AndroidSchedulers.mainThread()).subscribe({
                mViewModel.netResponseSwitchVideo(fromUserId, toUserId, "reject", type, username, avatar)
                dismiss()
            }, {

            }, {

            })
            mViewModel.mCompositeDisposable.add(subscribe)*/
            //发送拒绝消息
            val messageSend = mDataRepository.sendCallRejectMessage(fromUserId, type, toAvatar = avatar, toNickName = username, fromType = fromType)
            mDataRepository.postResponseFriendTalk(fromUserId, toUserId, 1, 2).subscribe({
                Timber.i(
                        it.toString())
            }, {
                Timber.i(
                        it.toString())
            })
            //显示未读提示
            var messageShow = mDataRepository.createVoiceCmdMessage(fromUserId!!, StatusTag.TYPE_VOICE, avatar!!,
                    username!!, toContent = "已拒绝", isCallingParty = false, fromType = fromType)
            mViewModel.insertReceivedMessage(messageShow)
            dismiss()
            EventTrackingUtils.joinPoint(EventBeans("ck_voicecall_cancel", fromUserId))
        }
        mBinding.answerTv.setOnClickListener {
            //接收
            openChatActivity(toUserId, fromUserId, username, avatar)
            val subscribe = Flowable.interval(1, 1, TimeUnit.SECONDS).take(1).observeOn(
                    AndroidSchedulers.mainThread()).subscribe({
                mViewModel.netResponseSwitchVideo(fromUserId, toUserId, "accept", type, username, avatar, fromType)
                dismiss()
            }, {

            }, {

            })
            mViewModel.mCompositeDisposable.add(subscribe)
            EventTrackingUtils.joinPoint(EventBeans("ck_voicecall_answer", fromUserId))
        }
        mBinding.nameTv.text = username
    }

    /**
     * 修改界面相关设置项
     */
    fun initView() {
        setBackgroundDrawable(
                ColorDrawable(context?.resources?.getColor(R.color.shadow2)!!))
    }

    override fun onLayoutId(): Int {
        return R.layout.pop_incoming_request
    }

    override fun onViewModel(): IncomingRequestViewModel {
        return IncomingRequestViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun show(view: View) {
        //showAtLocation(view, Gravity.TOP, 0, DeviceUtils.dp2px(view.resources, 92F).toInt())
        showAtLocation(view, Gravity.BOTTOM, 0, 0)
    }

    fun openChatActivity(toUserId: String, fromUserId: String, nickname: String, avatar: String) {
        if(ActManagerUtils.getAppManager().currentActivityVisible()!!::class.java != ChatActivity::class.java) {
            //当前在其他页面，直接打开ChatActivity
            try {
                ChatActivity.start(context!!, toUserId, nickname, avatar, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if((ActManagerUtils.getAppManager().currentActivityVisible() as ChatActivity).getToUserid() != toUserId) {
            //当前在其他好友页面，关闭当前好友页，打开ChatActivity（此处存在问题，尚未调试通过）
            ActManagerUtils.getAppManager().currentActivity().finish()
            try {
                ChatActivity.start(context!!, toUserId, nickname, avatar, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            //当前在好友页面，不进行操作
        }
    }
}