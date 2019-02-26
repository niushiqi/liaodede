package com.dyyj.idd.chatmore.model.network.result

data class UserDetailInfoResult(val errorCode: Int? = 0, val errorMsg: String? = "",
                                val data: Data? = Data()) {

    data class Data(val user: DetailUser = DetailUser(),
                    val userBaseInfo: DetailUserBaseInfo = DetailUserBaseInfo(),
                    val realNameInfo: DetailRealNameInfo = DetailRealNameInfo(),
                    val userExtraInfo: DetailUserExtraInfo = DetailUserExtraInfo())

    data class DetailUser(val userId: String = "", //MainResult.User
                          val mobile: String = "",
                          val registerTime: String = "",
                          val lastLoginTime: String = "",
                          var token: String? = "",
                          val firstLogin: String = "")

    data class DetailUserBaseInfo(val userId: String = "", //MainResult.UserBaseInfo
                                  var nickname: String = "",
                                  var gender: String = "",
                                  val userLevel: String = "",
                                  val isVip: String = "",
                                  var birthday: String = "",
                                  var age: Int = 0,
                                  var avatar: String = "",
                                  var nextLevelExperience: Int = 0, var userExperience: Int = 0)

    data class DetailRealNameInfo(val name: String = "", val auth: String = "")

    data class DetailUserExtraInfo(var professionId: String? = "",
                                   var professionName: String? = "",
                                   var areaId: String? = "",
                                   var area: String? = "", var address: String? = "",
                                   var school: String? = "")
}