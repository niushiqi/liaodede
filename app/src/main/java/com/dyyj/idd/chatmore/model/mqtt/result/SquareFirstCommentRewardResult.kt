package com.dyyj.idd.chatmore.model.mqtt.result

/**
 * Created by wangbin on 2018/12/31.
 */
data class SquareFirstCommentRewardResult(var rewardType: String? = "",
                                          var rewardId: Int? = 0,
                                          var rewardCoin: String? = "",
                                          var rewardStone: String? = "",
                                          var rewardCash: String? = "",
                                          var message: Message? = Message()
) {
    data class Message(var line1: String? = "")
}
