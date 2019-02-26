package com.dyyj.idd.chatmore.listener

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/07
 * desc   : 语音/视频 接收
 */
interface AcceptListener {
  fun onAccept(fromUserid:String, toUserid:String, type:String)
}