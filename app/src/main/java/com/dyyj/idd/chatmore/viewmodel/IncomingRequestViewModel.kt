package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/22
 * desc   : 聊天界面来电
 */
class IncomingRequestViewModel : ViewModel() {

    fun netResponseSwitchVideo(fromUserId: String, toUserId: String,
                               responseResult: String, type: String, username: String, avatar: String, fromType: String) {
        if (responseResult == "reject") {
            /*mDataRepository.sendCallRejectMessage(toUserId, type, toAvatar = avatar,
                                                  toNickName = username)*/
            //1对1通话：B拒绝，B的处理逻辑
            EventBus.getDefault().post(VideoRequestRejectVM(fromUserId, toUserId, type, avatar, username, fromType))
            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("已拒绝",
                    false, false, false, fromType))
        } else if (responseResult == "accept") {
            //接收
            EventBus.getDefault().post(VideoRequestAcceptVM(fromUserId, toUserId, type, avatar, username, fromType))
            //EventBus.getDefault().post(IncomingRequestViewModel.VideoRequestAccept())
        }
    }

    /**
     * 向服务器上传 响应一对一好友通话
     */
    /*fun VoiceResponseOK(fromUserId: String, toUserId: String, responseResult: String,
                        type:String, username:String, avatar:String) {
      mViewModel.sendAcceptMessage(fromUserId, type, avatar, username)
      val callStatus = if (type.contains(StatusTag.TYPE_VOICE)) 1 else 2
      mViewModel.netFriendExperience(fromUserid = fromUserid, toUserid = toUserid,
              type = callStatus, status = 1)
    }*/


    /**
     * 接受聊天
     */
    class VideoRequestAcceptVM(val fromUserId: String, val toUserId: String, val type: String,
                               val avatar: String, val nickname: String, val fromType: String)

    /**
     * 拒绝聊天
     */
    class VideoRequestRejectVM(val fromUserId: String, val toUserId: String, val type: String,
                               val avatar: String, val nickname: String, val fromType: String)
}