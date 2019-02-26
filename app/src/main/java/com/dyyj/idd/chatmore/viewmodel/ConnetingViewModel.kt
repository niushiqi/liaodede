package com.dyyj.idd.chatmore.viewmodel

import android.app.Activity
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/07
 * desc   : 呼叫中
 */
class ConnetingViewModel : ViewModel() {

    var mCallSuccess: Boolean = false
    var mCallStartTime: Long = 0
    var mTypeCall: String = "true"
    var mfromType: String = "1"


    /**
     * 通话取消
     */
    fun getVoiceMessage(fromUserid: String, toUserid: String, type: String): VoiceMessage {

        return VoiceMessage(status = StatusTag.STATUS_CANCEL, fromUserid = fromUserid,
                toUserid = toUserid, type = type)
    }

    /**
     * 通话拒绝
     */
    fun getVoiceMessageReject(fromUserid: String, toUserid: String, type: String): VoiceMessage {
        return VoiceMessage(status = StatusTag.STATUS_REJEC, fromUserid = fromUserid,
                toUserid = toUserid, type = type)
    }


    /**
     * 获取来电用户信息
     */
    fun netGetMatchingUserBaseInfo(username: String) {
        val subscribe = mDataRepository.getMatchingUserBaseInfo(username).subscribe({
            EventBus.getDefault().post(
                    StartMatchingVM(
                            it))
        }, {
            EventBus.getDefault().post(
                    StartMatchingVM(
                            isSuccess = false))
        })
        mCompositeDisposable.add(subscribe)
    }


    /**
     * 呼叫超时
     */
    private fun sendCallOutTime(fromUserid: String, toUserid: String, type: String) {
        val message = VoiceMessage(status = StatusTag.STATUS_OUT_TIME, fromUserid = fromUserid,
                toUserid = toUserid, type = type)
        EventBus.getDefault().postSticky(message)
    }

    /**
     * 无接听超时，超过60秒就认为超时
     */
    fun callTimeoutTimer(activity: Activity, fromUserid: String, toUserid: String, type: String, avatar: String, nickname: String) {
        if (toUserid == null || type == null || fromUserid == null) return
        val subscribe = Flowable.interval(60, 1, TimeUnit.SECONDS).take(1).observeOn(
                AndroidSchedulers.mainThread()).subscribe({
            if(mCallSuccess == false) {
                callTimeoutModel(activity, fromUserid, toUserid, type, avatar, nickname)
            }
        }, {

        }, {

        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     *  挂断通话
     */
    fun callHangUpModel(activity: Activity, fromUserid: String, toUserid: String, type: String, avatar: String, nickname: String) {
        var callTimeString: String = "00:00:00"
        var nh: Long = 1000 * 60 * 60
        var nm: Long = 1000 * 60
        var ns: Long = 1000
        var diff: Long = 0
        var hour: Long = 0
        var minute: Long = 0
        var second: Long = 0

        //if (mFromUserid == null || mToUserid == null || mType == null) return@setOnClickListener
        //EventBus.getDefault().postSticky(mViewModel.getVoiceMessage(mFromUserid!!, mToUserid!!, mType!!))
        if(mCallStartTime < System.currentTimeMillis()) {
            //自己计算时间，使用calendar总是多8小时
            diff = System.currentTimeMillis() - mCallStartTime
            hour = diff / nh
            minute = diff % nh / nm
            second = diff % nh % nm /ns
            callTimeString = "$hour:$minute:$second"
        }
        if (mTypeCall == "true") {
            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("聊天时长:"+callTimeString,
                    false, false, true, mfromType))
        } else {
            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("聊天时长:"+callTimeString,
                    false, false, false, mfromType))
        }
        EventBus.getDefault().post(ConnetingViewModel.VideoDisconnetedVM(fromUserid, toUserid, type,
                avatar, nickname, callTimeString, mfromType))
        mDataRepository.endCall()
        activity.finish()
    }

    /**
     *  取消通话
     */
    fun callCancelModel(activity: Activity, fromUserid: String, toUserid: String, type: String, avatar: String, nickname: String, isCallingParty: String) {
        //if (mFromUserid == null || mToUserid == null || mType == null) return@setOnClickListener
        //EventBus.getDefault().postSticky(mViewModel.getVoiceMessage(mFromUserid!!, mToUserid!!, mType!!))
        //2018.12.4日过渡版本-通话建立成功直接开启树洞 - 先用time位传递party
        EventBus.getDefault().post(ConnetingViewModel.VideoCancelVM(fromUserid, toUserid, type, avatar, nickname, isCallingParty, mfromType))
        if (mTypeCall == "true") {
            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("已取消",
                    false, false, true, mfromType))
        } else {
            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("已取消",
                    false, false, false, mfromType))
        }
        mDataRepository.endCall()
        activity.finish()
    }

    /**
     *  通话超时
     */
    fun callTimeoutModel(activity: Activity, fromUserid: String, toUserid: String, type: String, avatar: String, nickname: String) {
        //if (mFromUserid == null || mToUserid == null || mType == null) return@setOnClickListener
        //EventBus.getDefault().postSticky(mViewModel.getVoiceMessage(mFromUserid!!, mToUserid!!, mType!!))
        EventBus.getDefault().post(ConnetingViewModel.VideoCancelVM(fromUserid, toUserid, type, avatar, nickname, "true", mfromType))
        activity.runOnUiThread {
            niceToast("对方的手机可能不再身边，无应答，请稍后再试")
        }
        EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("对方无应答",
                true, true, true, mfromType))
        mDataRepository.endCall()
    }

    class StartMatchingVM(val obj: StartMatchingResult? = null, val isSuccess: Boolean = true)

    class VideoCancelVM(val fromUserId: String, val toUserId: String, val type: String,
                        val avatar: String, val nickname: String, val isCallingParty: String, val fromType: String)

    /**
     * 挂断聊天
     */
    class VideoDisconnetedVM(val fromUserId: String, val toUserId: String, val type: String,
                             val avatar: String, val nickname: String, val time: String, val fromType: String)

    class VideoSuccessVM(val fromUserId: String, val toUserId: String, val type: String,
                         val avatar: String, val nickname: String, val fromType: String)

    class VoiceStartCallVM(val isSuccess: Boolean = true)

}