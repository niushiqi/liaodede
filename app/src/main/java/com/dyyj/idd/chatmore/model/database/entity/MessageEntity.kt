package com.dyyj.idd.chatmore.model.database.entity

import com.hyphenate.chat.EMMessage

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/30
 * desc   : 消息详情+未读消息
 */
data class MessageEntity(val message:EMMessage, val unreadMessageList:List<UnreadMessageEntity>)