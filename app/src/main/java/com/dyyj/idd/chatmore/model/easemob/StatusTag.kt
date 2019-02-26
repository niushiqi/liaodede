package com.dyyj.idd.chatmore.model.easemob

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/04
 * desc   : 一对一聊天通话状态
 *
 * 呼叫中(接收/拒绝)
 * 呼叫已过期(超时了)
 * 通话中(面对对聊天 不做)
 * 通话结束(语音时长/视频时长)
 */
class StatusTag {
  companion object {

    /**
     * 自定义消息类型
     */
    const val ATTRIBUTE_CALL = "call"

    /**
     * 语音
     */
    const val TYPE_VOICE = "voice"

    /**
     * 视频
     */
    const val TYPE_VIDEO = "video"

    /**
     * 正在连接对方
     */
    const val STATUS_CONNECTING = "connecting"

    /**
     * 接通成功
     */
    const val STATUS_ACCEPTED = "accepted"

    /**
     * 通话结束
     */
    const val STATUS_DISCONNECTED = "disconnected"

    /**
     * 拒绝
     */
    const val STATUS_REJEC = "rejec"

    /**
     * 呼叫过期
     */
    const val STATUS_OUT_TIME = "out_time"

    /**
     * 呼叫取消
     */
    const val STATUS_CANCEL = "cancel"

    /**
     * 通话中
     */
    const val STATUS_CALL_IN = "call_in"

    /**
     * 忙线中
     */
    const val STATUS_BUSY = "call_busy"

    /**
     * 自定义左侧消息
     */
    const val CUSTOM_LEFT_TEXT = "custom_left_text"

    /**
     * 添加好友 发起语音消息
     */
    const val CUSTOM_FRIEND_ADD_VIOCE = "friend_and_voice"
  }
}