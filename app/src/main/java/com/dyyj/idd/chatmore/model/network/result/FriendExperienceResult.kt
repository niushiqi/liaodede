package com.dyyj.idd.chatmore.model.network.result

import com.dyyj.idd.chatmore.model.mqtt.result.FriendshipRewardResult


data class FriendExperienceResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var friendshipId: String? = "", var friendUserId: String? = "",
      var friendshipLevel: String? = "", var friendshipBox: Int? = 1,var friendshipLastRewardLevel:String? = "",
      var friendshipExperience: String? = "", var friendshipLevelAlias: String? = "", var friendshipRewardAddition:String? = "", var friendshipReward:FriendshipRewardResult? = null)
}