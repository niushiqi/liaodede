package com.dyyj.idd.chatmore.model.mqtt.result

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/05
 * desc   : 视频内容违章
 */
data class SlytherinFrameHangupResult(var talkId: Int? = 0, var message: Message? = Message()) {

  data class Message(var line1: String? = "")
}