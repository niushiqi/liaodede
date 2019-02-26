package com.dyyj.idd.chatmore.model.mqtt.result

/**
 * Created by zwj on 2018/1/18
 */
data class SendGiftOnChatResult(var sender_id: String? = "",
                                var sender_name: String? = "",
                                var receiver_id: String? = "",
                                var receiver_name: String? = "",
                                var gift_name: String? = "",
                                var gift_icon: String? = ""
)