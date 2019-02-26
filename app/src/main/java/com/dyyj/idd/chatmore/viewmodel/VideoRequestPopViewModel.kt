package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

class VideoRequestPopViewModel : ViewModel() {
  fun netResponseSwitchVideo(talkId: String, fromUserId: String, toUserId: String,
      responseResult: String) {
    if (responseResult == "2") {
      //拒绝
      val subscribe = mDataRepository.responseSwitchVideo(talkId, fromUserId, toUserId,
                                                          responseResult).subscribe({})
      mCompositeDisposable.add(subscribe)
    } else {
      //接收
      EventBus.getDefault().post(VideoRequestOk(talkId, fromUserId, toUserId))
    }
  }

  /**
   * 接受聊天
   */
  class VideoRequestOk(val talkId: String, val fromUserId: String, val toUserId: String)

  /**
   * 拒绝聊天
   */
  class VideoRequestReject(val talkId: String, val fromUserId: String, val toUserId: String)
}