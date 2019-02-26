package com.dyyj.idd.chatmore.model.network.result


data class ContactsResult(var errorCode: Int?, var errorMsg: String?, var data: Data?) {

  data class Data(var friendsList: List<Friends>?) {

    data class Friends(var friendUserId: String?, var friendNickname: String?,
        var friendGender: String?, var friendUserLevel: String?, var friendIsVip: String?,
        var friendAvatar: String?, var friendshipLevel: String?, var friendAge:Int = 0, var friendshipExperience: String,
                       var newReward: Int, var talkingStatus: Int,var friendshipSource: Int,var friendshipSouceTip: String?)
  }
}