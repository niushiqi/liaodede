package com.dyyj.idd.chatmore.model.network.result


data class StartMatchingResult(val errorCode: Int? = 0, val errorMsg: String? = "",
    val data: Data? = Data()) {
  data class Data(val matchingUserId: String? = "", val matchingUserNickname: String? = "",
      val matchingUserGender: Int? = 0, val matchingUserLevel: Int? = 0,
      val matchingUserIsVip: Int? = 0, val matchingUserAvatar: String? = "")
}