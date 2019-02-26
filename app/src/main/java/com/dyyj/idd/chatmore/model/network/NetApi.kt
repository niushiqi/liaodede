package com.gt.common.gtchat.model.network

import com.dyyj.idd.chatmore.model.mqtt.result.SignResult
import com.dyyj.idd.chatmore.model.network.result.*
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/08
 * desc   :
 */
interface NetApi {

    /**
     * 获取手机验证码
     */
    @POST("v1/login/mobile")
    fun postMobile(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 验证手机验证码
     */
    @POST("v1/login/verify")
    fun postVerify(@Body body: RequestBody): Flowable<VerifyResult>

    /**
     * 上传头像
     */
    @Multipart
    @POST("v1/user/uploadAvatar")
    fun postUpdateAvatar(@QueryMap map: Map<String, String>, @Part imgs: List<MultipartBody.Part>): Flowable<UploadAvatarResult>

    /**
     * 上传图片
     */
    @Multipart
    @POST("v1/friendship/imageMessage")
    fun postImageMessage(@QueryMap map: Map<String, String>, @Part imgs: List<MultipartBody.Part>): Flowable<ImageMessageResult>

    /**
     * 完善用户信息
     */
    @POST("v1/user/registerUserInfo")
    fun postRegisterUserInfo(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 审核失败上传头像
     */
    @Multipart
    @POST("v1/user/reUploadAvatar")
    fun postReupdateAvatar(@QueryMap map: Map<String, String>, @Part imgs: List<MultipartBody.Part>): Flowable<UploadAvatarResult>

    /**
     * 首次登录奖励
     */
    @POST("v1/task/getFirstLoginReward")
    fun getFirstLoginReward(@Body body: RequestBody): Flowable<FirstLoginResult>

    /**
     * 领取首次登录奖励
     */
    @POST("v1/task/receiveFirstLoginReward")
    fun postReceiveFirstLoginReward(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 消耗魔石
     */
    @POST("v1/matching/consumeMatchingStone")
    fun consumeMatchingStore(@Body body: RequestBody): Flowable<ConSumeMatchingStoreResult>

    /**
     * 取消匹配
     */
    @POST("v1/matching/cancelMatching")
    fun cancelMatching(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 魔石数量结果
     */
    @POST("/v1/matching/checkStartMatching")
    fun stoneNumber(@Body body: RequestBody): Flowable<StatusResult>
    /**
     * 获取当前是否有资格进入匹配
     */
    @POST("v1/matching/checkMessageMatching")
    fun checkMessageMatching(@Body body: RequestBody): Flowable<GetCanBeStartResult>

    /**
     * 匹配失败返还魔石
     */
    @POST("v1/matching/restoreMatchingStone")
    fun restoreMatchingStone(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 切换匹配
     */
    @POST("v1/matching/changeMatching")
    fun changeMatching(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 购买匹配次数
     */
    @POST("v1/matching/resetChange")
    fun resetChange(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 用户主要信息
     */
    @POST("v1/scene/main")
    fun postMain(@Body body: RequestBody): Flowable<MainResult>

    /**
     * 金币摘要
     */
    @POST("v1/wallet/coinSummary")
    fun postCoinSummary(@Body body: RequestBody): Flowable<CoinSummaryResult>

    /**
     * 现金摘要
     */
    @POST("v1/wallet/withdraw")
    fun postWithdrawSummary(@Body body: RequestBody): Flowable<WithdrawSumaryResult>

    /**
     * 选择额度风控检查
     */
    @POST("v1/wallet/withdrawCheck")
    fun postWithdrawCheck(@Body body: RequestBody): Flowable<WithdrawCheckResult>

    /**
     * 现金
     */
    @POST("v1/wallet/withdrawCashSummary")
    fun postCashSummary(@Body body: RequestBody): Flowable<CashSumaryResult>

    /**
     * 获取支付宝订单
     */
    @POST("v1/mall/payByAlipay")
    fun postAlipayOrder(@Body body: RequestBody): Flowable<AlipayOrderResult>

    /**
     * 现金余额支付
     */
    @POST("v1/mall/payByCash")
    fun postPayByCash(@Body body: RequestBody): Flowable<PayByCashResult>

    /**
     * 获取订单信息
     */
    @POST("v1/mall/getOrderInfo")
    fun netAliPayInfo(@Body body: RequestBody): Flowable<PayByCashResult>

    /**
     * 绑定支付宝账号
     */
    @POST("v1/wallet/bindAlipay")
    fun postUpdateAliNum(@Body body: RequestBody): Flowable<GeneralResult>

    /**
     * 提现提交
     */
    @POST("v1/wallet/applyWithdraw")
    fun postSummitWithdraw(@Body body: RequestBody): Flowable<GeneralResult>

    /**
     * 提现记录
     */
    @POST("v1/wallet/withdrawHistory")
    fun postWidthdrawRecord(@Body body: RequestBody): Flowable<WithdrawRecordResult>

    /**
     * 支付订单记录
     */
    @POST("v1/mall/getOrderList")
    fun postPayHistory(@Body body: RequestBody): Flowable<PayHistoryResult>

    /**
     * 获取道具商城数据(包含得豆)
     */
    @POST("v1/mall/props")
    fun postShopRecycleData(@Body body: RequestBody): Flowable<RecycleShopResult>

    /**
     * 魔石总数
     */
    @POST("v1/wallet/getAllStone")
    fun postGetAllStone(@Body body: RequestBody): Flowable<GetAllStoneResult>

    /**
     * 开始匹配
     */
    @POST("v1/matching/startMatching")
    fun startMatching(@Body body: RequestBody): Flowable<StatusResult>


    /**
     * 开始匹配
     */
    @POST("v1/matching/startMatchingManual")
    fun startMatchingManual(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 开始文字匹配
     */
    @POST("v1/matching/startMessageMatching")
    fun startTextMatching(@Body body: RequestBody): Flowable<MatchTextResult>

    /**
     * 获取当前匹配成功地址
     */
    @POST("v1/matching/getMatchingUserBaseInfo")
    fun getMatchingUserBaseInfo(@Body body: RequestBody): Flowable<StartMatchingResult>

    /**
     * 获取最后一个通话对方ID
     */
    @POST("v1/talking/getLastTalkUserId")
    fun getLastTalkUserId(@Body body: RequestBody): Flowable<GetLastTalkUserIdResult>

    /**
     * 上传通话状态
     */
    @POST("v1/talking/reportTalkingStatus")
    fun postReportTalkingStatus(@Body body: RequestBody): Flowable<ReportTalkingStatusResult>

    /**
     * 奖励记录
     */
    @POST("v1/reward/getReward")
    fun postGetReward(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 首次登陆
     */
    @POST("v1/scene/firstLogin")
    fun postFirstLogin(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取用户所有好友列表
     */
    @POST("v1/friendship/getAllMyFriends")
    fun getAllMyFriends(@Body body: RequestBody): Flowable<ContactsResult>

    /**
     * 获取用户所有好友列表
     */
    @POST("v1/friendship/getUnHandleFriendRequest")
    fun getUnHandleFriendRequest(@Body body: RequestBody): Flowable<UnHandleFriendResult>

    /**
     * 身份认证信息提交
     */
    @POST("v1/user/registerRealName")
    fun sendIdentityInfo(@Body body: RequestBody): Flowable<IdentityResult>

    /**
     * 用户基本信息
     */
    @POST("v1/scene/baseInfo")
    fun getUserCenterInfo(@Body body: RequestBody): Flowable<UserCenterInfoResult>

    /**
     * 获取用户详细信息
     */
    @POST("v1/scene/detailInfo")
    fun getUserDetailInfo(@Body body: RequestBody): Flowable<UserDetailInfoResult>

    /**
     * 好友申请列表
     */
    @POST("v1/friendship/requestFriendList")
    fun getApplyFriendsInfo(@Body body: RequestBody): Flowable<ApplyFriendResult>

    /**
     * 处理好友申请
     */
    @POST("v1/friendship/handleFriendRequest")
    fun doApplyFriend(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 申请添加好友
     */
    @POST("v1/friendship/requestAddFriend")
    fun addFriend(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 响应一对一好友通话
     */
    @POST("v1/friendship/responseFriendTalk")
    fun postResponseFriendTalk(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 职业列表
     */
    @POST("v1/info/getProfessionList")
    fun getProfessionList(@Body body: RequestBody): Flowable<ProfessionResult>

    /**
     * 地区列表
     */
    @POST("v1/info/getAreaList")
    fun getAreaLit(@Body body: RequestBody): Flowable<AreaResult>

    /**
     * 判断是否是好友
     */
    @POST("v1/friendship/isFriend")
    fun checkReltion(@Body body: RequestBody): Flowable<RelationResult>

    /**
     * 申请切换视频
     */
    @POST("v1/talking/requestSwitchVideo")
    fun reuqestSwitchVideo(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 处理切换视频请求
     */
    @POST("v1/talking/responseSwitchVideo")
    fun responseSwitchVideo(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 解除好友关系
     */
    @POST("v1/friendship/revokeFriendship")
    fun revokeFriendship(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取举报项
     */
    @POST("v1/report/getReportReason")
    fun getReportReason(@Body body: RequestBody): Flowable<ReportReasonResult>

    /**
     * 举报用户
     */
    @POST("v1/report/reportUser")
    fun reportUser(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取用户文字匹配结束时间
     */
    @POST("v1/matching/getMessageEndTime")
    fun getMessageEndTime(@Body body: RequestBody): Flowable<MessageEndTimeEntity>

    /**
     * 获取注册验证码
     */
    @POST("v1/verify/getRegVerifyCode")
    fun getRegisterCode(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 注册
     */
    @POST("v1/login/register")
    fun register(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 登陆
     */
    @POST("v1/login/loginVerify")
    fun login(@Body body: RequestBody): Flowable<LoginResult>

    /**
     * token登录
     */
    @POST("v1/login/loginToken")
    fun loginToken(@Body body: RequestBody): Flowable<LoginResult>

    /**
     * 渠道
     */
    @POST("v1/statistics/channel")
    fun uploadChannel(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 友好度查询
     */
    @POST("v1/friendship/getFriendExperience")
    fun getFriendExperience(@Body body: RequestBody): Flowable<FriendExperienceResult>

    /**
     * 获取树洞标签
     */
    @POST("v1/scene/getMyTags")
    fun getSigns(@Body body: RequestBody): Flowable<SignResult>

    /**
     * 兑换现金页面
     */
    @POST("v1/wallet/exchangeCash")
    fun getGoldConvert(@Body body: RequestBody): Flowable<CovertGoldResult>

    /**
     * 申请兑换现金
     */
    @POST("v1/wallet/applyExchangeCash")
    fun ConvertGold(@Body body: RequestBody): Flowable<GoldResult>

    /**
     * 修改密码
     */
    @POST("v1/user/changePassword")
    fun ChangePass(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取随机弹幕
     */
    @POST("v1/scene/getBarrage")
    fun getDanmuRank(@Body body: RequestBody): Flowable<DanmuResult>

    /**
     * 记录好友一对一文字消息
     */
    @POST("v1/friendship/textMessage")
    fun sendFriendTextMessage(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取标签库
     */
    @POST("v1/scene/getTagList")
    fun getTagList(@Body body: RequestBody): Flowable<TagsResult>

    /**
     * 保存标签
     */
    @POST("v1/scene/saveMyTags")
    fun saveTag(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 修改学校
     */
    @POST("v1/user/saveMySchool")
    fun changeSchool(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取签到状态
     */
    @POST("v1/task/getSignInStatus")
    fun getSignStatus(@Body body: RequestBody): Flowable<SignInStatusResult>

    /**
     * 获取三日任务数据
     */
    @POST("v1/task/getThreeDayTaskList")
    fun getThreeDayTask(@Body body: RequestBody): Flowable<ThreeDayTaskResult>

    /**
     * 获取每日任务数据
     */
    @POST("v1/task/getDailyTaskList")
    fun getEverydayTask(@Body body: RequestBody): Flowable<EverydayTaskResult>

    /**
     * 获取成就任务数据
     */
    @POST("v1/task/getAchievementTaskList")
    fun getAchievementTask(@Body body: RequestBody): Flowable<AchievementTaskResult>

    /**
     * 领取等级任务
     */
    @POST("v1/task/getLevelTaskList")
    fun getTaskLevelData(@Body body: RequestBody): Flowable<LevelTaskResult>

    /**
     * 领取任务奖励
     */
    @POST("v1/task/getTaskReward")
    fun getTaskRewardData(@Body body: RequestBody): Flowable<TaskRewardResult>

    /**
     * 首页任务列表
     */
    @POST("v1/task/getCurrentTaskList")
    fun getCurrentTaskList(@Body body: RequestBody): Flowable<CurrentTaskListResult>

    /**
     * 抽帧上传
     */
    @Multipart
    @POST("v1/slytherin/uploadVideoFrame")
    fun postUpdateVideoFrame(@QueryMap map: Map<String, String>, @Part imgs: List<MultipartBody.Part>): Flowable<UploadAvatarResult>

    /**
     * 响应收到匹配通知
     */
    @POST("v1/matching/reportMatchingPush")
    fun postReportMatchingPush(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 切人通知
     */
    @POST("v1/matching/switchPeople")
    fun postSwitchPerson(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 切人回执
     */
    @POST("v1/matching/reportMatchingSwitchPush")
    fun postSwitchPersonReturn(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取我的邀请码
     */
    @POST("v1/invite/getMyInviteCode")
    fun getMyInvite(@Body body: RequestBody): Flowable<MyInviteResult>

    /**
     * 验证邀请码
     */
    @POST("v1/verify/verifyInviteCode")
    fun verifyInviteCode(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 上传异常日志
     */
    @POST("v1/tools/reportExceptionLog")
    fun postReportExceptionLog(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 领取友好度等级奖励
     */
    @POST("v1/friendship/getFriendExperienceReward")
    fun getFriendExperienceReward(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 绑定个推cid
     */
    @POST("v1/user/bindPushId")
    fun postBinPushId(@Body body: RequestBody): Flowable<String>

    /**
     * 红包
     */
    @POST("v1/box/getBoxList")
    fun getRedPackageList(@Body body: RequestBody): Flowable<RedPackageResult>

    /*
     * 领取宝箱
     */
    @POST("v1/box/getGift")
    fun postGetGift(@Body body: RequestBody): Flowable<GetGiftResult>

    /**
     *通话超过1分钟领取金币
     */
    @POST("v1/reward/over1minReward")
    fun customMinuteCoin(@Body body: RequestBody): Flowable<CustomCoinResult>

    /**
     * 输入兑换码
     */
    @POST("v1/reward/exchangeCode")
    fun postExchangeCode(@Body body: RequestBody): Flowable<InviteCodeResult>

    /**
     * 使用邀请码
     */
    @POST("v1/invite/useInviteCode")
    fun postRegVerifyCode(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取所以道具
     */
    @POST("v1/props/index")
    fun postIndex(@Body body: RequestBody): Flowable<GameToolsResult>

    /**
     * 获取我的资产
     */
    @POST("v1/props/ownPropNum")
    fun ownPropNum(@Body body: RequestBody): Flowable<MyHeBaoResult>

    /**
     * 获取我的资产
     */
    @POST("v1/props/matchCardList")
    fun matchCardList(@Body body: RequestBody): Flowable<MyMatchCardResult>

    /**
     * 道具兑换奖励
     */
    @POST("/v1/props/convert2coin")
    fun postConvert2Coin(@Body body: RequestBody): Flowable<Conver2CoinResult>

    /**
     * 礼物兑换奖励
     */
    @POST("v1/props/exchange2coin")
    fun exchange2coin(@Body body: RequestBody): Flowable<Exchange2CoinResult>

    /**
     * 使用过的邀请码
     */
    @POST("v1/invite/usedInviteCode")
    fun postUseInviteCode(@Body body: RequestBody): Flowable<InviteCodeResult>

    /**
     * 非匹配开放时间弹窗内容
     */
    @POST("v1/matching/getMatchingTip")
    fun getMatchingTip(@Body body: RequestBody): Flowable<MatchingTipResult>

    /**
     *
     */
    @POST("v1/invite/getRecentInviteTask")
    fun getSystemNewMessages(@Body body: RequestBody): Flowable<SystemMessages>

    @POST("v1/invite/getMyInviteTask")
    fun getMyInviteTasks(@Body body: RequestBody): Flowable<MyTaskResult>

    @POST("v1/invite/getShareTip")
    fun getShareMessage(@Body body: RequestBody): Flowable<ShareMessageResult>

    /**
     * 领取三连开奖励
     */
    @POST("/v1/box/getBigGift")
    fun getBigGift(@Body body: RequestBody): Flowable<GetGiftResult>

    @POST("v1/box/getBoxList")
    fun getBoxList(@Body body: RequestBody): Flowable<GetGiftResult>

    /**
     * 骰子游戏开始
     */
    @POST("v1/game/startDice")
    fun gameStart(@Body body: RequestBody): Flowable<GameHttpRequestResult>

    /**
     * 骰子同意或拒绝
     */
    @POST("v1/game/endDice")
    fun gameAction(@Body body: RequestBody): Flowable<StatusResult>

    /**
     *上传通讯录
     */
    @POST("v1/invite/compareContacts")
    fun uploadContact(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 表达发送
     */
    @POST("v1/invite/like")
    fun biaobaiSend(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 匹配已邀请的
     */
    @POST("v1/invite/getMyLikeList")
    fun biaobaiMatch(@Body body: RequestBody): Flowable<BiaoBaiMatchResult>

    /**
     *
     */
    @POST("v1/statistics/inviteShareUrl")
    fun uploadShare(@Body body: RequestBody): Flowable<StatusResult>

    /*
     * 获取游戏信息
     */
    @POST("v1/game/getDiceGameInfo")
    fun getGameInfo(@Body body: RequestBody): Flowable<GameInfoResult>

    /**
     * 备选池用户获取通话ID
     */
    @POST("v1/talking/getUnknownTalkId")
    fun getUnknownTalkId(@Body body: RequestBody): Flowable<UnknownTalkIdResult>

    /**
     * 备选池被叫用户回传通话结果
     */
    @POST("v1/matching/reportBackupMatchingResult")
    fun reportBackupMatchingResult(@Body body: RequestBody): Flowable<UnknownTalkIdResult>

    /**
     * 银行开关
     */
    @POST("v1/bank/isOpen")
    fun getHankOpen(@Body body: RequestBody): Flowable<HankOpenResult>

    @POST("v1/login/checkMobile")
    fun checkRegister(@Body body: RequestBody): Flowable<CheckRegisterResult>

    @POST("v1/login/checkVerify")
    fun checkVerify(@Body body: RequestBody): Flowable<CheckRegisterResult>

    @POST("v1/matching/reportSipServiceStatus")
    fun reportSipServiceStatus(@Body body: RequestBody): Flowable<StatusResult>

    @POST("v1/matching/getMatchingUserPushStatus")
    fun getMatchingUserPushStatus(@Body body: RequestBody): Flowable<MatchingUserPushStatusResult>

    @POST("v1/matching/reportMatchingFailed")
    fun reportMatchingFailed(@Body body: RequestBody): Flowable<StatusResult>

    @POST("v1/tools/boxIsOpen")
    fun getBoxOpen(@Body body: RequestBody): Flowable<VivoResult>

    @POST("v1/tools/gameIsOpen")
    fun getGameOpen(@Body body: RequestBody): Flowable<VivoResult>

    /**
     * 获取用户发布动态
     */
    @POST("v1/topic/getMyUserTopicList")
    fun getUserTopicList(@Body body: RequestBody): Flowable<UserTopicResult>

    /**
     * 发布动态
     */
    @POST("v1/topic/createTopic")
    fun publishTopic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 点赞
     */
    @POST("v1/topic/createVote")
    fun voteTopic(@Body body: RequestBody): Flowable<LikeResult>

    /**
     * 评论
     */
    @POST("v1/topic/createReply")
    fun commentTopic(@Body body: RequestBody): Flowable<StatusResult>
    /**
     * 评论
     */
    @POST("v1/topic/createReplyNew")
    fun createReplyNew(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取首页消息接口
     */
    @POST("v1/topic/getIndexLog")
    fun getMainUnMsg(@Body body: RequestBody): Flowable<MainMsgResult>

    /**
     * 清空首页消息
     */
    @POST("v1/topic/deleteIndexLog")
    fun deleteMainUnMsg(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 图片上传
     */
    @Multipart
    @POST("v1/topic/uploadTopicImg")
    fun uploadPic(@QueryMap map: Map<String, String>, @Part imgs: MultipartBody.Part): Flowable<PicResult>

    /**
     * 获取用户评论动态
     */
    @POST("v1/topic/getUserReplyList")
    fun getCommentTopic(@Body body: RequestBody): Flowable<TopicCommentResult>

    /**
     * 获取我的动态
     */
    @POST("v1/topic/getMyTopicList")
    fun getMyTopicList(@Body body: RequestBody): Flowable<UserTopicResult>

    /**
     * 人气排行榜
     */
    @POST("v1/topic/getPopularityTopList")
    fun getPopularityTopList(@Body body: RequestBody): Flowable<PopularityTopResult>

    /**
     * 银行排行榜
     */
    @POST("v1/topic/getBankTopList")
    fun getBankTopList(@Body body: RequestBody): Flowable<BankTopResult>

    /**
     * 红包排行榜
     */
    @POST("v1/topic/getRedPacketTopList")
    fun getRedTopList(@Body body: RequestBody): Flowable<RedTopData>

    /**
     * 我的消息
     */
    @GET("v1/topic/getUserUnReadList")
    fun getMyMessage(@Query("userId") userId: String): Flowable<UserMessageResult>

    @GET("v1/topic/getMyFriendFirstNews")
    fun getMyFriendMsg(@Query("userId") userId: String): Flowable<UserMessageResult>

    /**
     * 更新未读消息
     */
    @GET("v1/topic/updateRead")
    fun getUnMyMessage(@Query("userId") userId: String): Flowable<StatusResult>

    /**
     * 获取离线期间系统未读通知
     */
    @POST("v1/systemmsg/getNewMsg")
    fun getOfflineSystemMessages(@Body body: RequestBody): Flowable<OfflineSystemMessageResult>

    /**
     * MQtt回执
     */
    @POST("v1/tools/receiveNotification")
    fun ReceiveNotification(@Body body: RequestBody): Flowable<StatusResult>

    /**
     *请求玩拼手速
     */
    @POST("v1/game/startRacing")
    fun requestGame2(@Body body: RequestBody): Flowable<Game2RequestResult>

    /**
     * 同意或不同意玩拼手速
     */
    @POST("v1/game/endRacing")
    fun responseGame2(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 游戏7s结束后,上报游戏得分
     */
    @POST("v1/game/uploadRacingNum")
    fun submitGame2(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 上报拨打电话
     */
    @POST("v1/matching/reportDialUserId")
    fun reportDialUserId(@Body body: RequestBody): Flowable<String>


    /**
     * 上传埋点 - 页面统计
     */
    @POST("v1/event/viewEventTrack")
    fun postViewEventTrack(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 上传埋点 - 按钮点击统计
     */
    @POST("v1/event/clickEventTrack")
    fun postClickEventTrack(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 好友互评//{"talkId":"973","userId":"10242","toUserId":"10242","rs":"1","reason":"","timestamp":"1544355042","deviceId":"99000983235380","sign":"db552fa36299c982df28ab175d0db6fe"}
     * talkId":"986","userId":"10145","toUserId":"10145","rs":"2","reason":"说不清","timestamp":"1544355902","deviceId":"99000983235380",      "sign":"f2dd0831d11086847f64e7f88855814a"}12-09 19:45:02.641 14695-15936/com.dyyj.idd.chatmore.dev I/<RxCachedThreadScheduler-27> okhttplog: {"

     */
    @POST("v1/talking/feedback")
    fun postFeedBack(@Body body: RequestBody): Flowable<FeedBackResult>

    /**
     * 在线状态
     */
    @POST("v1/friendship/getFriendOnlineStatus")
    fun getFriendOnlineStatus(@Body body: RequestBody): Flowable<OnlineStatus>

    /**
     * 匹配按钮状态
     */
    @POST("v1/matching/getMatchingButton")
    fun getMatchingButton(@Body body: RequestBody): Flowable<MatchingButtonEntity>

    /**
     * 广场获取话题列表
     */
    @POST("v1/square/getTopicList")
    fun getTopicList(@Body body: RequestBody): Flowable<PlazaTopicListResult>

    /**
     * 广场获取话题信息
     */
    @POST("v1/square/getTopic")
    fun getTopic(@Body body: RequestBody): Flowable<PlazaTopicResult>

    /**
     * 广场 我关注的话题
     */
    @POST("v1/square/getMyFollowTopic")
    fun getMyFollowTopic(@Body body: RequestBody): Flowable<PlazaTopicListResult>

    /**
     * 获取广场 随便看看帖子
     */
    @POST("v1/square/getFlow")
    fun getPlazaCardList(@Body body: RequestBody): Flowable<PlazaFlowCardResult>


    /**
     * 广场 获取话题热门贴
     */
    @POST("v1/square/getTopicHotComment")
    fun getTopicHotComment(@Body body: RequestBody): Flowable<PlazaCardResult>

    /**
     * 广场 获取话题新贴
     */
    @POST("v1/square/getTopicNewComment")
    fun getTopicNewComment(@Body body: RequestBody): Flowable<PlazaCardResult>

    /**
     * 广场帖子点赞
     */
    @POST("v1/square/postTopicCommentAgree")
    fun postTopicCommentAgree(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场 置顶话题
     */
    @POST("v1/square/topTopic")
    fun postTopTopic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场 取消置顶话题
     */
    @POST("v1/square/revokeTopTopic")
    fun revokeTopTopic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场 关注话题
     */
    @POST("v1/square/followTopic")
    fun postFollowTopic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场 取消关注话题
     */
    @POST("v1/square/revokeFollowTopic")
    fun postRevokeFollowTopic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 获取消息列表-广场-消息列表
     */
    @POST("v1/square/myMessageList")
    fun getMySquareMessageList(@Body body: RequestBody): Flowable<UserSquareMessageListResult>

    /**
     * 获取我的动态-广场-消息列表
     */
    @POST("v1/square/myDynamicList")
    fun getMySquareDynamicList(@Body body: RequestBody): Flowable<UserSquareDynamicListResult>

    /**
     * 广场图片上传
     */
    @Multipart
    @POST("v1/square/postSquareImages")
    fun uploadPicPlaza(@QueryMap map: Map<String, String>, @Part imgs: MultipartBody.Part): Flowable<PlazaPicResult>

    /**
     * 广场发帖
     */
    @POST("v1/square/postTopicComment")
    fun postTopicComment(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-通过ID获取评论（帖子）
     */
    @POST("v1/square/getComment")
    fun getCommentByID(@Body body: RequestBody): Flowable<UserSquareCommentResult>

    /**
     * 广场-发布评论（帖子）的回复
     */
    @POST("v1/square/postTopicCommentReply")
    fun postTopicCommentReply(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-对回复进行点赞
     */
    @POST("v1/square/postTopicCommentReplyAgree")
    fun postTopicCommentReplyAgree(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-获取某条评论（帖子）的回复
     */
    @POST("v1/square/getCommentReply")
    fun getCommentReply(@Body body: RequestBody): Flowable<UserSquareCommentReplyResult>

    /**
     * 广场-我删除我的回复
     */
    @POST("v1/square/deleteMyReply")
    fun deleteMyReply(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-删除一条动态
     */
    @POST("v1/square/deleteDynamic")
    fun deleteDynamic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 好友圈-删除一条动态
     */
    @POST("v1/topic/deleteTopic")
    fun deleteTopic(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-评论（帖子）主删除某条回复
     */
    @POST("v1/square/deleteReply")
    fun deleteReply(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-评论（帖子）主设置神评论
     */
    @POST("v1/square/setNbReply")
    fun setNbReply(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-评论（帖子）主取消神评论
     */
    @POST("v1/square/cancelNbReply")
    fun cancleNbReply(@Body body: RequestBody): Flowable<StatusResult>

    /*
     * 匹配按钮状态
     */
    @POST("v1/square/changeMatchingStatus")
    fun changeMatchingStatus(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 匹配按钮状态
     */
    @POST("v1/square/getMyMatchingStatus")
    fun getMyMatchingStatus(@Body body: RequestBody): Flowable<MyMatchStatus>

    /**
     * 获取其他用户匹配状态
     */
    @POST("v1/square/getUserMatchingStatus")
    fun getUserMatchingStatus(@Body body: RequestBody): Flowable<MyMatchStatus>

    /**
     *退出文字匹配
     */
    @POST("v1/matching/exitMessageMatching")
    fun exitMessageMatching(@Body body: RequestBody): Flowable<StatusResult>

    /**
     *退出文字匹配
     */
    @POST("v1/matching/matchingTimeOut")
    fun matchingTimeOut(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场-"1分钟内积极回复"任务
     * 上报广场信箱开始聊天
     */
    @POST("v1/square/startSquareMessage")
    fun startSquareMessage(@Body body: RequestBody): Flowable<StartSquareMessageResult>

    /**
     * 广场-"1分钟内积极回复"任务
     * 获取当前任务进行状态
     */
    @POST("v1/square/getSquareMessageStatus")
    fun getSquareMessageStatus(@Body body: RequestBody): Flowable<StartSquareMessageStatusResult>

    /**
     * 广场未读-获取未读消息
     */
    @POST("v1/square/getUnReadMessageNum")
    fun getSquareUnReadMessageNum(@Body body: RequestBody): Flowable<SquareUnReadMessageNumResult>

    /**
     * 广场未读-清空未读消息
     */
    @POST("v1/square/clearUnReadMessageNum")
    fun clearSquareUnReadMessage(@Body body: RequestBody): Flowable<StatusResult>

    /**
     * 广场信箱-获取开启私聊消耗魔石的数量
     */
    @POST("v1/square/getMatchConsumeStone")
    fun getSquareMessageConsumeStone(@Body body: RequestBody): Flowable<SquareMessageConsumeStoneResult>

    /**
     * 商城主界面
     */
    @POST("v1/props/getChatGifts")
    fun getChatGifts(@Body body: RequestBody): Flowable<ChatGiftsResult>

    /**
     * 赠送礼物
     */
    @POST("v1/talking/sendGift")
    fun sendGift(@Body body: RequestBody): Flowable<SendGiftResult>

    /**
     * 送出的礼物
     */
    @POST("v1/props/getMyGiftLogs")
    fun getMyGiftLogs(@Body body: RequestBody): Flowable<MySharedRoseResult>

    /**
     * 收到的礼物
     */
    @POST("v1/props/getMyGifts")
    fun getMyGifts(@Body body: RequestBody): Flowable<MyGiftsResult>

    /**
     * 获取广场弹窗配置项
     */
    @POST("/v1/settings/getconfig")
    fun getSquarePopConfig(@Body body: RequestBody): Flowable<SquarePopConfig>

    /*
     * 空间动态
     */
    @POST("v1/square/myDynamicList")
    fun myDynamicList(@Body body: RequestBody): Flowable<PlazaSpaceCardResult>

    /**
     * 好友圈-通过ID获取评论（帖子）
     */
    @POST("v1/topic/getTopicInfoById")
    fun getTopicInfoById(@Body body: RequestBody): Flowable<SpaceCircleResult>

    /**
     * 好友圈-好友圈的评论
     */
    @POST("v1/topic/getTopicReply")
    fun getTopicReply(@Body body: RequestBody): Flowable<SpaceCircleReplyResult>

    /**
     * 好友圈-获取用户信息
     */
    @POST("v1/square/getMyInfo")
    fun getMyInfo(@Body body: RequestBody): Flowable<SpaceUserInfoResult>

    /**
     * 话题投票测试提交
     */
    @POST("v1/square/postQuestionOption")
    fun postQuestionOption(@Body body: RequestBody): Flowable<PlazaTopicSubmitResult>
}

