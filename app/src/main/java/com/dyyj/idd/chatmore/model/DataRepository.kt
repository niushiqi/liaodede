package com.dyyj.idd.chatmore.model

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.eventtracking.EventGiftMessage
import com.dyyj.idd.chatmore.model.database.AppDatabase
import com.dyyj.idd.chatmore.model.database.ConvertersFactory
import com.dyyj.idd.chatmore.model.database.entity.*
import com.dyyj.idd.chatmore.model.easemob.EasemobManager
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.ImageMessage
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.mqtt.MqttService
import com.dyyj.idd.chatmore.model.mqtt.MqttTag
import com.dyyj.idd.chatmore.model.mqtt.result.SignResult
import com.dyyj.idd.chatmore.model.network.NetManager
import com.dyyj.idd.chatmore.model.network.params.ImageMessageParams
import com.dyyj.idd.chatmore.model.network.params.MobileParams
import com.dyyj.idd.chatmore.model.network.params.UploadAvatarParams
import com.dyyj.idd.chatmore.model.network.params.VerifyParams
import com.dyyj.idd.chatmore.model.network.result.*
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.MD5
import com.dyyj.idd.chatmore.utils.RequestBodyUtils
import com.dyyj.idd.chatmore.viewmodel.MainViewModel
import com.google.gson.Gson
import com.gt.common.gtchat.extension.*
import com.gt.common.gtchat.model.assets.AssetsManager
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.ishumei.smantifraud.SmAntiFraud
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import top.zibin.luban.Luban
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.collections.set


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/07
 * desc   : 数据存储类(网络/数据库/xml等)
 */
class DataRepository(val context: Context) {

    val CURRENT_USER_ID = "currentUserId";


    private var mUserInfoEntity: UserInfoEntity? = null

    var mCurrentStone: String? = null

    /**
     * 网络
     */
    private val mNetManager by lazy { NetManager() }

    /**
     * spi
     */
//  private val mSipManager by lazy { SipManager(context.applicationContext) }

    /**
     * Assets资源
     */
    private val mAssetsManager by lazy { AssetsManager(niceChatContext()) }

    /**
     * 数据库
     */
    private val mDatabase by lazy { AppDatabase.getInstance(context.applicationContext) }

    /**
     * 环信管理类
     */
    private val mEasemobManager by lazy { EasemobManager() }

    /**
     * 提供网络操作
     */
    fun getNetApi() = mNetManager.getNetApi()

    /**
     * 提供数据库操作
     */
    fun getDatabase() = mDatabase

    /**
     * 获取现在魔石数量
     */
    fun getCurrentStone() = mCurrentStone


    /**
     * MQTT
     */
    fun getMQtt() = MqttService.getMyMqtt()

    /**
     * 发送MQTT消息
     */
    fun pubMsg(message: Any) {
        getMQtt().pubMsg(MqttTag.MQTT_TOPIC, Gson().toJson(message), MqttTag.MQTT_QOS)
    }

    fun pubSocket() {
        val accountInfo = getUserInfoEntity()
        if (getMQtt() != null) {
            getMQtt().send("${accountInfo?.userId}|${Build.ID}|${0}")
        }

    }

    private var subscribe: Disposable? = null
    private var accountInfo: LoginResult.Data? = null
    private val period: Long = 10
    open var socketHeardSendIndex: Long = 0
    open var socketHeardReturnIndex: Long = 0
    open var mqttHeardReturnIndex: Long = 0

    open var socketHeadSendStr: String = ""

    fun startHeardSocket() {
        socketHeardSendIndex = 0
        socketHeardReturnIndex = 0
        mqttHeardReturnIndex = 0
        subscribe = Flowable.interval(0, period, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe(
                {
                    if (socketHeardReturnIndex != socketHeardSendIndex) {//socket 返回    ！=   socket 发送
                        //EventBus.getDefault().post("socket-timeout")
                        Log.e("socketheard", "socket-timeout")
                        socketHeardSendIndex = 0
                        socketHeardReturnIndex = 0

                        mqttHeardReturnIndex = 0
                        subscribe?.dispose()
                        getMQtt().reSetSocket()
                        return@subscribe
                    }
                    if (socketHeardSendIndex != mqttHeardReturnIndex) {//socket 发送    ！=    mqtt 返回
                        //EventBus.getDefault().post("mqtt-break")
                        Log.e("socketheard", "mqtt-break")//mqtt-break
                        getMQtt().disConnectMqtt()
                        getMQtt().connectMqtt()
                    }
//      if ((it % (period * 3)).compareTo(0) == 0) {
                    socketHeardSendIndex = it
                    socketHeadSendStr = "${accountInfo?.userId?: getUserid()}|${Build.ID}|${socketHeardSendIndex}"
                    getMQtt().send(socketHeadSendStr)
//      }
                }, {
                    Timber.tag("niushiqi-bengkui").i("startHeardSocket异常 崩溃")
                }
        )
        CompositeDisposable().add(subscribe!!)
    }

    private var subscribeTime: Disposable? = null
    private val mCmpositeDisposable by lazy {
        CompositeDisposable()
    }

    fun startGrawTime(time: Long, totalTime: Long) {
        mCmpositeDisposable.clear()
        subscribeTime = Flowable.interval(1, 1, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe({
            val currentTime = System.currentTimeMillis() / 1000
            if (currentTime >= time) {
                EventBus.getDefault().post(
                        RedPacketTimeVM(
                                "00:00",
                                100,
                                true))
                subscribeTime?.dispose()
//                mCmpositeDisposable.delete(subscribeTime!!)
//                mCmpositeDisposable.dispose()
                mCmpositeDisposable.clear()
            } else {
                val centerTime = time - currentTime
                val centerDu: Int = 100 - (centerTime.toDouble() / totalTime * 100).toInt()
                val sb = StringBuilder()
                sb.append(
                        if (centerTime / 60 < 10) "0${centerTime / 60}" else "${centerTime / 60}")
                sb.append(
                        ":")
                sb.append(
                        if (centerTime % 60 < 10) "0${centerTime % 60}" else "${centerTime % 60}")
                EventBus.getDefault().post(
                        RedPacketTimeVM(
                                sb.toString(),
                                centerDu))
            }
        }, {
            Timber.tag("niushiqi-bengkui").i("startGrawTime 崩溃")
        })
        mCmpositeDisposable.add(subscribeTime!!)
//    CompositeDisposable().add(subscribeTime!!)
    }

    class RedPacketTimeVM(val time: String, val progress: Int, val finish: Boolean = false)

    fun stopHeardSocket() {
        subscribe?.dispose()
    }

    /**
     * 发送MQTT消息
     */
    fun pubMsg(message: String) {
        val topic = "/ldd/message/1111"
        getMQtt().pubMsg(MqttTag.MQTT_TOPIC, message, MqttTag.MQTT_QOS)
    }

    /**
     * 获取手机验证码
     */
    fun postMobile(params: MobileParams): Flowable<StatusResult> {
        return getNetApi().postMobile(niceRequestBody(params)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 验证手机验证码
     */
    fun postVerify(params: VerifyParams): Flowable<VerifyResult> {
        return getNetApi().postVerify(niceRequestBody(params)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 保存我的钱包
     */
    fun saveUserWalletEntity(data: MainResult.Data): Flowable<Unit> {
        return Flowable.just(UserWalletEntity.createUserWalletEntity(data)).map {
            val walletEntity = mDatabase.userDao().queryUserWalletEntity(it.userId)
            if (walletEntity == null) {
                mDatabase.userDao().insert(UserWalletEntity.createUserWalletEntity(data))
            } else {
                val entity = walletEntity.createUserWalletEntity(it)
                mDatabase.userDao().update(entity)
            }
        }
    }

    /**
     * 保存帐号信息
     */
    fun saveUserInfoEntity(userInfo: UserInfoEntity?) {
        if (userInfo == null) return

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Timber.tag("").i("")
        }
        Flowable.just(userInfo).map {
            val entity = mDatabase.userDao().queryAccountEntityLast()
            //插入帐号信息到数据库
            if (entity == null) {
                mDatabase.userDao().insert(AccountEntity.createAccountEntity(userInfo))
                mDatabase.userDao().insert(userInfo)
                //保存到内存
                mUserInfoEntity = userInfo
            } else {
                //更新帐号信息
                val queryUserInfoEntityList = mDatabase.userDao().queryUserInfoEntityList()
                if(queryUserInfoEntityList != null) {
                    val userInfoEntity = queryUserInfoEntityList.updateUserInfoEntity(userInfo)
                    mDatabase.userDao().update(userInfoEntity)
                    entity.localLastLoginTime = System.currentTimeMillis()
                    mDatabase.userDao().update(entity)
                    mUserInfoEntity = queryUserInfoEntityList
                } else {
                    mDatabase.userDao().insert(userInfo)
                    mUserInfoEntity = userInfo
                }
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Timber.tag(
                    "").i(
                    "")
        }, {
            Timber.tag(
                    "viewmodel").i(
                    it.message)
        })
    }

    /**
     * 获取钱包
     */
    fun getUserWalletEntity(): UserWalletEntity? {
        getUserInfoEntity()?.userId?.let {
            return mDatabase.userDao().queryUserWalletEntity(it)
        }
        return null
    }

    /**
     * 获取帐号信息
     */
    fun getUserInfoEntity(): UserInfoEntity? {
        return if (mUserInfoEntity != null) {
            mUserInfoEntity
        } else {
            mUserInfoEntity = mDatabase.userDao().queryUserInfoEntityList()
            return mUserInfoEntity
        }
    }

    fun getUserid() = getUserInfoEntity()?.userId

    fun getSPUserid() = PreferenceUtil.getString(CURRENT_USER_ID,null)

//    fun getUserid() = "10372"
    /**
     * 查询个人基本信息是否完整
     */
    fun checkUserInfoNull(userInfo: UserDetailInfoResult.Data?): Boolean {
        if (TextUtils.isEmpty(userInfo?.userBaseInfo?.nickname) or TextUtils.isEmpty(
                        userInfo?.userBaseInfo?.birthday) or TextUtils.equals(
                        userInfo?.userBaseInfo?.gender.toString(), "0")) {
            return true
        }
        return false
    }

    /**
     * 清空帐号
     */
    fun cleanAccountInfo() {
        val userId = getUserid()
        Flowable.just(userId).map {
            mDatabase.userDao().run {
                this.deleteUserWalletEntity(userId)
                this.deleteUserInfoEntity(userId)
                this.deleteAccountEntity(userId)
                mUserInfoEntity = null
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({}, {})
    }

    /**
     * 保存消费魔石id
     */
    fun saveConsumeId(consumeId: Int) {
        mDatabase.userDao().insert(UserWalletEntity().apply {
            timestamp = System.currentTimeMillis()
            consumeStoneId = consumeId.toString()
        })
    }

    /**
     * 保存系统消息
     */
    fun saveSystemMessage() {
        if (mDatabase.userDao().querySystemMessageEntity() == null) {
            //保存系统消息
            mDatabase.userDao().insert(SystemMessageEntity().apply {
                userId = getUserid()
                count = 0
            })
        } else {
            //更新系统消息
            mDatabase.userDao().updateSystemMessageEntity()
        }
    }

    /**
     * 获取系统消息
     */
    fun getSystemMessage(): Int {
        val entity = mDatabase.userDao().querySystemMessageEntity()
        return if (entity != null && entity.count > 0) entity.count else 0
    }

    /**
     * 清空系统消息
     */
    fun clearSystemMessage() {
        val userId = getUserid()
        mDatabase.userDao().deleteSystemMessageEntity(userId)
    }

    /**
     * 获取消费魔石id
     */
    fun getConsumeId() = mDatabase.userDao().queryUserWalletEntity(getUserid())?.consumeStoneId

    /**
     * 获取密码
     */
    fun getLoginToken(): String? {
        return getUserInfoEntity()?.token
    }

    /**
     * 保存密码
     */
    fun saveLoginToken(pass: String) {
        val entity = getUserInfoEntity()
        entity?.token = pass
        entity?.let { saveUserInfoEntity(it) }
    }

    /**
     * 是否
     */

    /**
     * 头像压缩并上传
     */
    fun postUploadAvatar(images: List<String>): Flowable<UploadAvatarResult> {
        return Flowable.just(images).map {
            return@map Luban.with(niceChatContext()).load(it).get()
        }.map {
            return@map RequestBodyUtils.getMultiparBodyListFormFile(it)
        }.flatMap {
            return@flatMap getNetApi().postUpdateAvatar(
                    niceQueryMap(UploadAvatarParams(userId = getUserid()!!)), imgs = it)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 记录好友一对一图片 - 图片压缩并上传
     */
    fun postImageMessage(image: String): Flowable<ImageMessageResult> {
        return Flowable.just(image).map {
            return@map Luban.with(niceChatContext()).load(it).get()
        }.map {
            return@map RequestBodyUtils.getMultiparBodyListFormFile(it)
        }.flatMap {
            return@flatMap getNetApi().postImageMessage(
                    niceQueryMap(ImageMessageParams(userId = getUserid()!!)), imgs = it)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 完善用户信息
     */
    fun postRegisterUserInfo(nickname: String, gender: Int, birthday: String, avatar: String,
                             areaCodeId: String, professionId: String): Flowable<StatusResult> {

        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["nickname"] = URLEncoder.encode(nickname, "utf-8")
        map["gender"] = gender.toString()
        map["birthday"] = birthday
        map["avatar"] = avatar
        map["areaCodeId"] = areaCodeId
        map["professionId"] = professionId
        return getNetApi().postRegisterUserInfo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 审核失败头像压缩并上传
     */
    fun postReuploadAvatar(images: List<String>): Flowable<UploadAvatarResult> {
        return Flowable.just(images).map {
            return@map Luban.with(niceChatContext()).load(it).get()
        }.map {
            return@map RequestBodyUtils.getMultiparBodyListFormFile(it)
        }.flatMap {
            return@flatMap getNetApi().postReupdateAvatar(
                    niceQueryMap(UploadAvatarParams(userId = getUserid()!!)), imgs = it)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 首次登录奖励
     */
    fun getFirstLoginReward(): Flowable<FirstLoginResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!

        return getNetApi().getFirstLoginReward(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 领取首次登录奖励
     */
    fun postReceiveFirstLoginReward(rewardId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["rewardId"] = rewardId
        return getNetApi().postReceiveFirstLoginReward(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 用户主要信息
     */
    @Synchronized
    fun postMain(): Flowable<MainResult> {
        val map = linkedMapOf<String, String>()
        if ((getUserInfoEntity() == null) or (getUserid() == null)) {
            map["userId"] = ChatApp.getInstance().userDetailInfo?.userBaseInfo?.userId ?: ""
        } else {
            map["userId"] = getSPUserid()!!
        }
        return getNetApi().postMain(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 消耗魔石
     */
    fun consumeMatchingStore(userStone: Int): Flowable<ConSumeMatchingStoreResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["userStone"] = userStone.toString()
        return getNetApi().consumeMatchingStore(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 取消匹配
     */
    fun cancelMatching(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().cancelMatching(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     *  魔石数量结果
     */
    fun requestStoneNumber(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().stoneNumber(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     *  开启匹配判断 当前魔石、次数  返回errorCode
     */
    fun netCanStart(): Flowable<GetCanBeStartResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().checkMessageMatching(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 匹配失败返还魔石
     */
    fun restoreMatchingStone(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["consumeId"] = getConsumeId().toString()
        return getNetApi().restoreMatchingStone(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 切换匹配
     */
    fun changeMatching(consumeId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!

        return getNetApi().changeMatching(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 购买匹配次数
     */
    fun resetChange(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().resetChange(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 金币摘要
     */
    fun postCoinSummary(): Flowable<CoinSummaryResult> {
        val map = linkedMapOf<String, String>()
//    map["userId"] = getAccountInfo().userId!!
        map["userId"] = getSPUserid()!!
        return getNetApi().postCoinSummary(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 现金
     */
    fun postCashSummary(): Flowable<CashSumaryResult> {
        val map = linkedMapOf<String, String>()
//    map["userId"] = getAccountInfo().userId!!
        map["userId"] = getSPUserid()!!
        return getNetApi().postCashSummary(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取支付宝订单
     */
    fun postAlipayOrder(goodsId: String): Flowable<AlipayOrderResult> {
        val map = linkedMapOf<String, String>()
        map["goodsId"] = goodsId
        map["userId"] = getSPUserid()!!
        return getNetApi().postAlipayOrder(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 退出文字匹配
     */
    fun exitMessageMatching(matchingUserId: String): Flowable<PayByCashResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["matchingUserId"] = matchingUserId
        return getNetApi().postPayByCash(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 现金余额支付
     */
    fun postPayByCash(goodsId: String): Flowable<PayByCashResult> {
        val map = linkedMapOf<String, String>()
        map["goodsId"] = goodsId
        map["userId"] = getSPUserid()!!
        return getNetApi().postPayByCash(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取订单信息
     */
    fun netAliPayInfo(orderBizNo: String): Flowable<PayByCashResult> {
        val map = linkedMapOf<String, String>()
        map["orderBizNo"] = orderBizNo
        map["userId"] = getSPUserid()!!
        return getNetApi().netAliPayInfo(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 现金摘要
     */
    fun postWidthdrawSummary(type: String): Flowable<WithdrawSumaryResult>? {
        val map = linkedMapOf<String, String>()
        map["type"] = type
        map["userId"] = getSPUserid()!!
        return getNetApi().postWithdrawSummary(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 提现页面 选择额度风控检查
     */
    fun postWithdrawCheck(type: String, num: String): Flowable<WithdrawCheckResult>? {
        val map = linkedMapOf<String, String>()
        map["type"] = type
        map["num"] = num
        map["userId"] = getSPUserid()!!
        return getNetApi().postWithdrawCheck(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 绑定/修改支付宝账号
     */
    fun postUpdateAliNum(alipayAccount: String, alipayRealName:String): Flowable<GeneralResult>? {
        val map = linkedMapOf<String, String>()
        map["alipayAccount"] = alipayAccount
        map["alipayRealName"] = URLEncoder.encode(alipayRealName, "utf-8")
        map["userId"] = getSPUserid()!!
        return getNetApi().postUpdateAliNum(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 提现提交
     */
    fun postSummitWithdraw(alipayAccount: String, alipayRealName:String, withdrawNum: String, withdrawType: String): Flowable<GeneralResult>? {
        val map = linkedMapOf<String, String>()
        map["num"] = withdrawNum
        map["type"] = withdrawType
        map["alipayAccount"] = alipayAccount
        map["alipayRealName"] = URLEncoder.encode(alipayRealName, "utf-8")
        map["userId"] = getSPUserid()!!
        return getNetApi().postSummitWithdraw(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 提现记录
     */
    fun postWidthdrawRecord(): Flowable<WithdrawRecordResult>? {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().postWidthdrawRecord(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 支付订单记录
     */
    fun postPayHistory(): Flowable<PayHistoryResult>? {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().postPayHistory(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 获取道具商城数据（包含得豆）
     */
    fun postShopRecycleData(): Flowable<RecycleShopResult>? {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().postShopRecycleData(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 魔石总数
     */
    fun postGetAllStone(): Flowable<GetAllStoneResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().postGetAllStone(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 注册帐号
     */
    fun retisterSip(username: String, password: String, domain: String) {
//    mSipManager.registerListener()
//    if (LinphoneManager.getLc().defaultProxyConfig == null) {
//      mSipManager.saveAccount(username = username, password = password, domain = domain)
//    }
    }

    /**
     * 初始化Sip
     */
    fun initSip() {
//    mSipManager.init()
    }

    /**
     * 拨打电话
     */
    fun callOngoing(username: String) {
//    mSipManager.callOngoning(username, "sip.sunchao.pro")
    }

    /**
     * 开始匹配
     */
    fun startMatching(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().startMatching(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }


    /**
     * 开始匹配
     */
    fun startMatchingManual(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().startMatchingManual(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 文字匹配
     */
    fun startTextMatching(): Flowable<MatchTextResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().startTextMatching(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取匹配用户信息
     */
    fun getMatchingUserBaseInfo(username: String): Flowable<StartMatchingResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["matchingUserId"] = username
        return getNetApi().getMatchingUserBaseInfo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取最后一个通话的对方用户ID
     */
    fun getLastTalkUserId(): Flowable<GetLastTalkUserIdResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getLastTalkUserId(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getToUserid(fromid: String, toid: String): String? {
        if (fromid != getUserid()) {
            return fromid
        }
        if (toid != getUserid()) {
            return toid
        }
        return null
    }

    /**
     * 环信连接状态注册
     */
    fun initEasemobListener() {
        mEasemobManager.initListener()
    }

    /**
     * 拨打语音电话
     */
    fun callVoice(username: String) {
        mEasemobManager.callVoice(username)
    }

    /**
     * 拨打语音电话 - 添加扩展内容，用于区分 1对1通话 和 匹配通话
     */
    fun callVoiceExt(username: String) {
        mEasemobManager.callVoiceExt(username)
    }

    /**
     * 拨打视频电话
     */
    fun callVideo(username: String) {
        mEasemobManager.callVideo(username)
    }

    /**
     * 恢复视频（图像）数据传输
     */
    fun resumeVideoTransfer() {
        EMClient.getInstance().callManager().resumeVideoTransfer()
    }

    fun resumeVoiceTransfer() {
        EMClient.getInstance().callManager().resumeVoiceTransfer()
    }

    fun pauseVideoTransfer() {
        EMClient.getInstance().callManager().pauseVideoTransfer()
    }

    /**
     * 接听通话
     */
    fun answerCall() {
        mEasemobManager.answerCall()
    }

    /**
     * 拒绝接听
     */
    fun rejectCall() {
        mEasemobManager.rejectCall()
    }

    /**
     * 挂断通话
     */
    fun endCall() {
        EasemobManager.isStartMatch = false
        mEasemobManager.endCall()
        Log.i("mytag","连接断开,挂断电话")
    }

    /**
     * 注册通话状态
     */
    fun registerCall(context: Context) {
        mEasemobManager.registerCall(context)
    }

    /**
     * 设置通话循环
     */
    fun setEasemobStatus(context: Context) {
        mEasemobManager.setReceiveMsg(context)
    }

    /**
     * 初始化通话记录
     */
    fun initCallListener() {
        mEasemobManager.initCallListener()
    }

    /**
     * 上传通话状态
     */
    fun postReportTalkingStatus(talkId: String = "0", talkType: String = "1", fromUserId: String,
                                toUserId: String, reportType: String,
                                reportValue: String): Flowable<ReportTalkingStatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        map["talkType"] = talkType
        map["fromUserId"] = fromUserId
        map["toUserId"] = toUserId
        map["reportType"] = reportType
        map["reportValue"] = reportValue
        return getNetApi().postReportTalkingStatus(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 奖励记录
     */
    fun postGetReward(rewardId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["rewardId"] = rewardId
        return getNetApi().postGetReward(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 首次登陆
     */
    fun postFirstLogin(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().postFirstLogin(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取用户所有好友列表
     */
    fun getAllMyFriends(): Flowable<ContactsResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["userId"] = "1000000001"
        return getNetApi().getAllMyFriends(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取未处理的好友数量
     */
    fun getUnHandleFriendRequest(): Flowable<UnHandleFriendResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["userId"] = "1000000001"
        return getNetApi().getUnHandleFriendRequest(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 发送消息
     */
    fun sendMessage(content: String, toChatUsername: String, toNickName: String,
                    toAvatar: String,fromType: Int,isFriend: Boolean,matchTime: Long): EMMessage {
        val message = mEasemobManager.sendMessage(content, toChatUsername, toNickName, toAvatar, fromType, isFriend, matchTime)
        return message
    }

    /**
     * 通过广场发送消息
     */
    fun sendMessageBySquare(content: String, toChatUsername: String, toNickName: String,
                            toAvatar: String, squareTopicId: String?): EMMessage {
        val message = mEasemobManager.sendMessageBySquare(content, toChatUsername, toNickName, toAvatar, squareTopicId)
        return message
    }

    /**
     * 发送图片消息
     */
    /*fun sendImageMessage(imagePath: String, toChatUsername: String, toNickName: String,
                    toAvatar: String): EMMessage? {
        val message = mEasemobManager.sendImageMessage(imagePath, toChatUsername, toNickName, toAvatar)
        //insertEMMesage(message)
        return message
    }*/

    /**
     * 发送图片URL消息
     */
    fun sendImageURLMessage(imageMessage: ImageMessage, toChatUsername: String, toNickName: String,
                            toAvatar: String,fromType: Int,isFriend: Boolean,matchTime: Long): EMMessage {
        val message = mEasemobManager.sendImageURLMessage(imageMessage, toChatUsername, toNickName, toAvatar, fromType, isFriend, matchTime)
        return message
    }

    /**
     * 广场聊天发送图片URL消息
     */
    fun sendImageURLMessageBySquare(imageMessage: ImageMessage, toChatUsername: String, toNickName: String,
                            toAvatar: String, squareTopicId: String?): EMMessage {
        val message = mEasemobManager.sendImageURLMessageBySquare(imageMessage, toChatUsername, toNickName, toAvatar, squareTopicId)
        return message
    }

    /**
     * 发送礼物卡消息
     */
    fun sendGiftCardMessage(obj: EventGiftMessage, toChatUsername: String, toNickName: String,
                            toAvatar: String,fromType: Int,isFriend: Boolean,matchTime: Long): EMMessage {
        val message = mEasemobManager.sendGiftCardMessage(obj, toChatUsername, toNickName, toAvatar, fromType, isFriend, matchTime)
        return message
    }

    /**
     * 广场聊天发送礼物卡消息
     */
    fun sendGiftCardMessageBySquare(obj: EventGiftMessage, toChatUsername: String, toNickName: String,
                                    toAvatar: String, squareTopicId: String?): EMMessage {
        val message = mEasemobManager.sendGiftCardMessageBySquare(obj, toChatUsername, toNickName, toAvatar, squareTopicId)
        return message
    }

    /**
     * 查询聊天记录
     */
    fun loadMessageList(username: String, fromSize: String = "0",
                        pageSize: Int = 30): List<EMMessage> {
        val conversation = EMClient.getInstance().chatManager().getConversation(username,
                EMConversation.EMConversationType.Chat)
        conversation ?: return emptyList()
//    conversation.markAllMessagesAsRead()
//SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
//获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
        return conversation.allMessages
    }

    /**
     * 查询个人基本信息
     */
    fun getUserCenterApi(): Flowable<UserCenterInfoResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getUserCenterInfo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 身份认证提交
     */
    fun sendIdentityApi(realName: String, idNo: String): Flowable<IdentityResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["realName"] = realName
        map["idNo"] = idNo
        return getNetApi().sendIdentityInfo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 用户详细信息
     */
    fun getUserDetailInfo(userid: String? = null): Flowable<UserDetailInfoResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userid ?: getSPUserid()!!
        return getNetApi().getUserDetailInfo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友申请列表
     */
    fun getApplyFriendList(): Flowable<ApplyFriendResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["userId"] = "1000000027"
        return getNetApi().getApplyFriendsInfo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 处理好友申请
     */
    fun doApplyFriendAction(requestId: String, handleResult: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["userId"] = "1000000027"
        map["requestId"] = requestId
        map["handleResult"] = handleResult
        map["handleMessage"] = ""
        return getNetApi().doApplyFriend(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 申请好友
     */
    fun addFriendAction(toUserId: String, talkId: String,
                        requestMessage: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["toUserId"] = toUserId
        map["talkId"] = talkId
        map["requestMessage"] = requestMessage
        return getNetApi().addFriend(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 申请好友-文字匹配
     */
    fun addFriendActionFromTextMatch(toUserId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["toUserId"] = toUserId
        map["source"] = "2"
        map["requestMessage"] = " "
        return getNetApi().addFriend(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 申请好友-广场
     */
    fun addFriendActionFromSquare(toUserId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["toUserId"] = toUserId
        map["source"] = "4"
        map["requestMessage"] = " "
        return getNetApi().addFriend(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 语音匹配失败
     */
    fun matchingTimeOut(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().matchingTimeOut(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 聊天拨打中
     */
    fun sendConnetingMessage(toChatUsername: String, type: String, toAvatar: String,
                             toNickName: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_CONNECTING, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = "", fromType = fromType)
    }

    /**
     * 聊天拨打结束
     */
    fun sendCallEndMessage(fromChatUsername: String, toChatUsername: String, type: String, toAvatar: String,
                           toNickName: String, time: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_DISCONNECTED, fromUserid = fromChatUsername,
                        type = type, toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = time, fromType = fromType)
    }

    /**
     * 聊天接受
     */
    fun sendCallAcceptMessage(toChatUsername: String, type: String, toAvatar: String,
                              toNickName: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_ACCEPTED, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = "", fromType = fromType)
    }

    /**
     * 聊天拒绝
     */
    fun sendCallRejectMessage(toChatUsername: String, type: String, toAvatar: String,
                              toNickName: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_REJEC, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = "", fromType = fromType)
    }

    /**
     * 呼叫超时
     */
    fun sendCallOutTimeMessage(toChatUsername: String, type: String, toAvatar: String,
                               toNickName: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_OUT_TIME, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = "", fromType = fromType)
    }

    /**
     * 呼叫取消
     */
    fun sendCallCancelMessage(toChatUsername: String, type: String, toAvatar: String,
                              toNickName: String, isCallingParty: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_CANCEL, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = isCallingParty, fromType = fromType)
    }

    /**
     * 通话中
     */
    fun sendCallInMessage(toChatUsername: String, type: String, toAvatar: String,
                          toNickName: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_CALL_IN, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = "", fromType = fromType)
    }

    /**
     * 忙线中
     */
    fun sendBusyMessage(toChatUsername: String, type: String, toAvatar: String,
                        toNickName: String, fromType: String): EMMessage {
        return mEasemobManager.sendExtraMessage(
                VoiceMessage(status = StatusTag.STATUS_BUSY, fromUserid = getSPUserid()!!, type = type,
                        toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                toNickName = toNickName, extra = "", fromType = fromType)
    }

    /**
     * 响应一对一好友通话
     */
    fun postResponseFriendTalk(fromUserid: String, toUserid: String, type: Int,
                               status: Int): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["fromUserId"] = fromUserid
        map["toUserId"] = toUserid
        map["talkType"] = type.toString()
        map["responseResult"] = status.toString()
        return getNetApi().postResponseFriendTalk(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 创建1对1语音控制命令-文字消息-不发送
     */
    fun createVoiceCmdMessage(toChatUsername: String, type: String, toAvatar: String,
                              toNickName: String, toContent: String, isCallingParty: Boolean, fromType: String): EMMessage {
        if (isCallingParty) {
            return mEasemobManager.createVoiceCmdMessage(
                    VoiceMessage(status = StatusTag.STATUS_CONNECTING, fromUserid = getSPUserid()!!, type = type,
                            toUserid = toChatUsername), toChatUsername, toAvatar = toAvatar,
                    toNickName = toNickName, toContent = toContent, fromType = fromType)
        } else {
            val message = mEasemobManager.createVoiceCmdMessage(
                    VoiceMessage(status = StatusTag.STATUS_CONNECTING, fromUserid = toChatUsername, type = type,
                            toUserid = getSPUserid()!!), getSPUserid()!!, toAvatar = toAvatar,
                    toNickName = toNickName, toContent = toContent, fromType = fromType)
            message.setDirection(EMMessage.Direct.RECEIVE)
            message.from = toChatUsername
            return message
        }
    }

    /**
     * 创建系统消息EMMessage
     */
    fun createEMMessageBySystemMessage(systemMessage: MainViewModel.SystemMessageResult): EMMessage {
        val message = mEasemobManager.createEMMessageBySystemMessage(systemMessage)
        return message
    }

    /**
     * 获取职业列表
     */
    fun getProfessionList(): Flowable<ProfessionResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getProfessionList(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取地区列表
     */
    fun getAreaList(index: Int): Flowable<AreaResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["areaCodeId"] = index.toString()
        return getNetApi().getAreaLit(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 聊天界面发起的语音视频信息
     */
    fun setChatCallInfo(userid: String?, talkId: String?, type: String) {
        userid ?: return
        talkId ?: return
        mEasemobManager.setChatCallInfo(userid = userid, talkId = talkId, type = type)
    }

    /**
     * 判断好友关系
     */
    fun checkRelationApi(friendUserId: String): Flowable<RelationResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["friendUserId"] = friendUserId
        return getNetApi().checkReltion(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 申请切换视频
     */
    fun requestSwitchVideo(talkId: String, fromUserId: String,
                           toUserId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        map["fromUserId"] = fromUserId
        map["toUserId"] = toUserId
        return getNetApi().reuqestSwitchVideo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 回应切换视频请求
     */
    fun responseSwitchVideo(talkId: String, fromUserId: String, toUserId: String,
                            responseResult: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        map["fromUserId"] = fromUserId
        map["toUserId"] = toUserId
        map["responseResult"] = responseResult
        return getNetApi().responseSwitchVideo(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 通话id
     */
    fun getTalkId() = mEasemobManager.getTalkId()

    /**
     * 通话类型(语音/视频)
     */
    fun getCallType() = mEasemobManager.getCallType()

    /**
     * 通话对方id
     */
    fun getAcceptedUserId() = mEasemobManager.getAcceptedUserId()

    /**
     * 解除好友关系
     */
    fun postRevokeFriendship(revokeUserId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["revokeUserId"] = revokeUserId
        return getNetApi().revokeFriendship(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取举报项
     */
    fun postReportReason(): Flowable<ReportReasonResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getReportReason(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 举报用户
     */
    fun postReportUser(reportUserId: String, reportReasonId: String, talkId: String? = "0",
                       talkDuration: String? = "0"): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["reportUserId"] = reportUserId
        map["reportReasonId"] = reportReasonId
        map["talkId"] = talkId.toString()
        map["talkDuration"] = talkDuration.toString()
        return getNetApi().reportUser(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }


    /**
     * 获取文字匹配结束时间
     */
    fun getMessageEndTime(matchUserId: String): Flowable<MessageEndTimeEntity> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["matchUserId"] = matchUserId
        return getNetApi().getMessageEndTime(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取注册验证码
     */
    fun getRegisterCode(mobile: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["mobile"] = mobile
        return getNetApi().getRegisterCode(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 注册
     */
    fun register(mobile: String, password: String, verifyCode: String,
                 inviteCode: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["mobile"] = mobile
        map["password"] = password
        map["verifyCode"] = verifyCode
        map["smDeviceId"] = SmAntiFraud.getDeviceId()
        if (inviteCode.isNotEmpty()) {
            map["inviteCode"] = inviteCode
        }
        return getNetApi().register(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 登陆
     */
    fun login(mobile: String, verifyType: String, verifyCode: String): Flowable<LoginResult> {
        val map = linkedMapOf<String, String>()
        map["deviceInfo"] = Build.MODEL//获取手机 机型
        map["mobile"] = mobile
        map["verifyType"] = verifyType
        map["verifyCode"] = verifyCode
        map["smDeviceId"] = SmAntiFraud.getDeviceId()
//        map["smDeviceId"] = "20181205182810eb6529b1160a46cb936bad0fe235f7740198cffe59f2b0ca"
        return getNetApi().login(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 渠道
     */
    fun uploadChannel(channelCode: String, userId: String, gender: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["channelCode"] = channelCode
        map["uuid"] = DeviceUtils.getDeviceID(niceChatContext())
        map["userId"] = userId
        map["gender"] = gender
        return getNetApi().uploadChannel(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
        )
    }

    /**
     * token登录
     */
    fun loginToken(token: String): Flowable<LoginResult> {
        val map = linkedMapOf<String, String>()
        map["token"] = token
        return getNetApi().loginToken(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 友好度查询
     */
    fun getFriendExperience(friendUserId: String): Flowable<FriendExperienceResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["friendUserId"] = friendUserId
        return getNetApi().getFriendExperience(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取树洞标签
     */
    fun getSigns(uid: String): Flowable<SignResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = uid
        return getNetApi().getSigns(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 兑换现金页面
     */
    fun getConvertGold(gold: Int): Flowable<CovertGoldResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["userId"] = "1000000001"
        map["exchangeCoin"] = gold.toString()
        return getNetApi().getGoldConvert(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 申请兑换现金
     */
    fun ConvertGold(gold: Int): Flowable<GoldResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["userId"] = "1000000001"
        map["exchangeCoin"] = gold.toString()
        return getNetApi().ConvertGold(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 保存聊天界面亲密度
     */
    fun saveFriendExperienceResult(obj: FriendExperienceResult) {
        PreferenceUtil.commitString("FriendExperienceResult_${obj.data?.friendUserId}",
                Gson().toJson(obj))
    }

    /**
     * 聊天界面亲密度
     */
    fun getFriendExperienceResult(friendUserId: String): FriendExperienceResult? {
        val json = PreferenceUtil.getString("FriendExperienceResult_${friendUserId}", "")
        if (TextUtils.isEmpty(json)) return null
        return Gson().fromJson(json, FriendExperienceResult::class.java)

    }

    /**
     * 修改密码
     */
    fun ChangePass(oldPass: String, newPass: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["oldPassword"] = MD5.MD5(MD5.MD5(oldPass))
        map["newPassword"] = MD5.MD5(MD5.MD5(newPass))
        return getNetApi().ChangePass(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取随机弹幕
     */
    fun getDanmuRank(uid: String): Flowable<DanmuResult> {
        val map = linkedMapOf<String, String>()
//    map["userId"] = getAccountInfo().userId!!
        map["userId"] = uid
        return getNetApi().getDanmuRank(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 记录好友一对一文字消息
     */
    fun sendFriendTextMessage(friendUserId: String, messageContent: String, isTextMatch: Int): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["friendUserId"] = friendUserId
        map["isMatch"] = isTextMatch.toString()
        map["messageContent"] = URLEncoder.encode(messageContent, "utf-8")
        return getNetApi().sendFriendTextMessage(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 插入消息
     */
    fun insertEMMesage(msg: EMMessage, isRead: Boolean) {
        val dao = mDatabase.conversationDao()
        dao.insert(ConvertersFactory.EMMessageToconversationEntity(msg)).forEach { id ->
            dao.insert(UnreadMessageEntity().apply {
                this.from = msg.from
                this.to = msg.to
                this.conversationId = id.toInt()
                if (isRead) this.isRead = 1 else this.isRead = 0
            })
            dao.insert(ConvertersFactory.EMMessageToPayloadEntity(msg, id.toInt())).forEach {
                dao.insert(ConvertersFactory.EMMessageToBodiesEntity(msg, id.toInt()))
            }
        }

    }

    /**
     * 获取标签
     */
    fun getTagList(userId: String, gender: String): Flowable<TagsResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["gender"] = gender
        return getNetApi().getTagList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 保存标签
     */
    fun saveTag(userId: String, tags: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["tags"] = tags
        return getNetApi().saveTag(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 保存学校
     */
    fun saveSchool(school: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["school"] = URLEncoder.encode(school, "UTF-8")
//    map["school"] = school
        return getNetApi().changeSchool(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 消息列表
     */
    fun queryConversationEntityList(toUserid: String): Flowable<List<EMMessage>> {
        val dao = mDatabase.conversationDao()
        val userid = getSPUserid()!!
        return Flowable.just(toUserid).map {
            dao.queryConversationEntityListByFromAndTo(it, userid).map {
                it.payload = dao.queryPayloadEntityList(it.id)
                it.payload.bodiesEntity = dao.queryBodiesEntityList(it.id)
                ConvertersFactory.conversationEntityToEMMessage(it)
            }.filter { it != null }.toList()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 清除未读消息
     */
    fun clearUnreadMessage(friendUserId: String) {
        mDatabase.conversationDao().update(friendUserId)
    }

    /**
     * 获取系统消息 V2.0
     * 备注：系统消息的fromUserId为2
     */
    fun querySystemMessageList(userId: String): Flowable<List<EMMessage>> {
        val dao = mDatabase.conversationDao()
        return Flowable.just(userId).map {
            dao.querySystemMessageListByUserId(it).map {
                it.payload = dao.queryPayloadEntityList(it.id)
                it.payload.bodiesEntity = dao.queryBodiesEntityList(it.id)
                ConvertersFactory.conversationEntityToEMMessage(it)
            }.filter { it != null }.toList()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取签到状态
     */
    fun getSigeInStatus(): Flowable<SignInStatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getSignStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取三日任务数据
     */
    fun getThreeDayTaskData(): Flowable<ThreeDayTaskResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getThreeDayTask(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取每日任务
     */
    fun getEverydayTaskData(): Flowable<EverydayTaskResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getEverydayTask(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取每日任务
     */
    fun getAchievementTaskData(): Flowable<AchievementTaskResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getAchievementTask(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取等级任务
     */
    fun getLevelTaskData(): Flowable<LevelTaskResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getTaskLevelData(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 领取任务奖励
     */
    fun getTaskRewardData(taskId: String): Flowable<TaskRewardResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["taskId"] = taskId!!
        return getNetApi().getTaskRewardData(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 首页任务列表
     */
    fun getCurrentTaskList(): Flowable<CurrentTaskListResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getCurrentTaskList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取所有未读消息
     */
    fun queryUnreadMessageAll(): Flowable<List<UnreadMessageEntity>> {
        val userid = getUserid() ?: return Flowable.just(arrayListOf())!!
        return mDatabase.conversationDao().queryUnreadMessageEntityAll(userid).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun queryUnreadMessage(): List<UnreadMessageEntity> {
        val userId = getUserid() ?: return arrayListOf()
        return mDatabase.conversationDao().queryUnreadMessage(userId)
    }


    /**
     * 删除好友聊天记录
     */
    fun deleteFriendConversation(friendUserId: String) {
        mDatabase.conversationDao().update(friendUserId)
        mDatabase.conversationDao().deleteConversationEntityById(friendUserId)
    }

    /**
     * 查询最新联系人聊天记录(最新一条消息)
     */
    fun queryLastConversationAllByFriend(): Flowable<List<MessageEntity>> {
        val userid = getSPUserid()!!
        val dao = mDatabase.conversationDao()
        return dao.queryLastConversationEntityAllByFriend(getSPUserid()!!).flatMap { conversationList ->
            val list = arrayListOf<MessageEntity>()


//      querySystemMessageById(userid)?.let {
//        list.add(it)
//      }
            //查找聊天记录
            conversationList.map {
                it.payload = dao.queryPayloadEntityList(it.id)
                it.payload.bodiesEntity = dao.queryBodiesEntityList(it.id)
                ConvertersFactory.conversationEntityToEMMessage(it)
            }.toList().forEach {
                if (it.from == "1" || it.to == "1") {
                    val unreadMessageList = dao.queryUnreadMessageEntityById(it.from, userid)
                    list.add(0, MessageEntity(message = it, unreadMessageList = unreadMessageList))
                    //系统消息排第一个
                } else if (it.from == userid) {
                    val unreadMessageList = dao.queryUnreadMessageEntityById(it.to, userid)
                    list.add(MessageEntity(message = it, unreadMessageList = unreadMessageList))

                } else {
                    val unreadMessageList = dao.queryUnreadMessageEntityById(it.from, userid)
                    list.add(MessageEntity(message = it, unreadMessageList = unreadMessageList))
                }
            }


            return@flatMap Flowable.just(list.toList())

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 查找系统消息
     */
    fun querySystemMessageById(userid: String): MessageEntity? {
        val dao = mDatabase.conversationDao()
        //获取系统消息
        val systemMessage = dao.querySystemMessage(userid)
        if (systemMessage != null) {
            systemMessage.payload = dao.queryPayloadEntityList(systemMessage.id)
            systemMessage.payload.bodiesEntity = dao.queryBodiesEntityList(systemMessage.id)
            val emMessage = ConvertersFactory.conversationEntityToEMMessage(systemMessage)

            val unreadMessageList = dao.queryUnreadMessageEntityById(systemMessage.to, userid)
            return MessageEntity(message = emMessage, unreadMessageList = unreadMessageList)
        }
        return null
    }

    /**
     * 插入mqtt记录
     */
    fun insertMQTTLog(receive: String = "", status: String = "",
                      timestamp: Long = System.currentTimeMillis()): Flowable<Long> {
        val logEntity = MQTTLogEntity(receive, status, timestamp)
        return Flowable.just("").map { mDatabase.logDao().insert(logEntity) }.subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 插入socket记录
     */
    fun insertSocketLog(url: String = "47.104.216.222:51900", params: String, result: String = "",
                        paramsTimestamp: Long, resultTimestamp: Long): Flowable<Long> {
        return Flowable.just("").map {
            mDatabase.logDao().insert(
                    SocketLogEntity(url, params, result, paramsTimestamp, resultTimestamp))
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 插入http日志
     */
    fun insertHttpLog(url: String, params: String, result: String, paramsTimestamp: Long,
                      resultTimestamp: Long): Flowable<Long> {
        return Flowable.just("").map {
            mDatabase.logDao().insert(
                    HttpLogEntity(url, params, result, paramsTimestamp, resultTimestamp))
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 视频抽帧
     */
    fun postUploadVideoFrame(talkId: String, fromUserId: String, toUserId: String,
                             imageSeconds: String, images: List<String>): Flowable<UploadAvatarResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        map["fromUserId"] = fromUserId
        map["toUserId"] = toUserId
        map["imageSeconds"] = imageSeconds
        return Flowable.just(images).map {
            Timber.tag("UploadVideoFrame").i(it.toString())
            return@map Luban.with(niceChatContext()).load(it).get()
        }.map {
            Timber.tag("UploadVideoFrame").i(it.toString())
            return@map RequestBodyUtils.getMultiparBodyListFormVideo(it)
        }.flatMap {
            Timber.tag("UploadVideoFrame").i(it.toString())
            return@flatMap getNetApi().postUpdateVideoFrame(niceQueryMap(map), imgs = it)
        }
    }

    /**
     * 截图
     */
    fun takePicture(activity: Activity): String {
        return mEasemobManager.takePicture(activity)
    }

    /**
     * 获取截图路径
     */
    fun getTakePicturePath() = mEasemobManager.takePicturePath

    /**
     * 响应收到匹配通知
     */
    fun postReportMatchingPush(talkId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        return getNetApi().postReportMatchingPush(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 保存通讯录
     */
    fun saveContactsList(obj: ContactsResult) {
        val data = obj.data?.friendsList ?: return
        Flowable.just(data).map {
            it.forEach { contact ->
                mDatabase.contactsDao().run {
                    val contactsEntity = this.queryContactsEntityByFriendid(contact.friendUserId)
                    if (contactsEntity == null) {
                        //插入数据
                        this.insert(
                                com.dyyj.idd.chatmore.model.database.entity.ContactsEntity.createContactsEntity(
                                        contact, getSPUserid()!!))
                    } else {
                        //更新数据
                        this.update(contactsEntity.createContactsEntity(
                                ContactsEntity.createContactsEntity(contact, getSPUserid()!!)))
                    }
                }
            }
        }.subscribeOn(Schedulers.io()).subscribe({}, {})
    }

    fun isFriend(friendUserId: String): Boolean {
        return mDatabase.contactsDao().queryContactsEntityByFriendid(friendUserId) != null
    }

    /**
     * 切人通知
     */
    fun postSwitchPerson(talkId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        return getNetApi().postSwitchPerson(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 切人回执
     */
    fun postSwitchPersonReturn(talkId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        return getNetApi().postSwitchPersonReturn(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取我饿验证码
     */
    fun getMyInvite(): Flowable<MyInviteResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getMyInvite(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 验证邀请码
     */
    fun checkInviteCode(code: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["inviteCode"] = code
        return getNetApi().verifyInviteCode(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 上传异常日志
     */
    fun reportExceptionLog(log: String) {
        val map = linkedMapOf<String, String>()
        map["log"] = log
        Timber.tag("crash").i(log)
        Timber.tag("crash").i("params=${niceRequestBody(map)}")
        getNetApi().postReportExceptionLog(niceRequestBody(map)).subscribeOn(Schedulers.io()).subscribe(
                {
                    Timber.tag("crash").i(it.toString())
                }, {
            Timber.tag("crash").i(it.message)
        })
    }

    /**
     * 领取友好度等级奖励
     */
    fun getFriendExperienceReward(friendUserId: String, rewardLevel: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["friendUserId"] = friendUserId
        map["rewardLevel"] = (rewardLevel.toInt() + 1).toString()
        return getNetApi().getFriendExperienceReward(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 绑定个推cid
     */
    fun postBinPushId(cid: String) {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["cid"] = cid
        getNetApi().postBinPushId(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe({
            Timber.tag("DataRepository").i(it.toString())
        }, {
            Timber.tag("DataRepository").e(it.message)
        })
    }

    /**
     * 红包
     */
    fun getRedPackageList(): Flowable<RedPackageResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getRedPackageList(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /*
   * 领取宝箱
   */
    fun postGetGift(giftId: String): Flowable<GetGiftResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["giftId"] = giftId
        return getNetApi().postGetGift(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 通话超过1分钟领取金币
     */
    fun customMinuteCoin(talkId: String, duration: String): Flowable<CustomCoinResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["talkId"] = talkId
        map["duration"] = duration
        return getNetApi().customMinuteCoin(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 输入兑换码
     */
    fun postExchangeCode(exchangeCode: String): Flowable<InviteCodeResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["exchangeCode"] = exchangeCode
        return getNetApi().postExchangeCode(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 使用邀请码
     */
    fun postRegVerifyCode(inviteCode: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["inviteCode"] = inviteCode
        return getNetApi().postRegVerifyCode(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 道具界面
     */
    fun postIndex(): Flowable<GameToolsResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().postIndex(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 我的匹配卡
     */
    fun matchCardList(): Flowable<MyMatchCardResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().matchCardList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 我的资产
     */
    fun ownPropNum(): Flowable<MyHeBaoResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().ownPropNum(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 道具兑换金币
     */
    fun postConvert2Coin(propNum: String, propId: String): Flowable<Conver2CoinResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["propId"] = propId
        map["propNum"] = propNum
        return getNetApi().postConvert2Coin(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 礼物兑换金币
     */
    fun exchange2coin(propNum: String, propId: String): Flowable<Exchange2CoinResult> {
        val map = linkedMapOf<String, String>()
        map["propId"] = propId
        map["propNum"] = propNum
        map["userId"] = getUserid()!!
        return getNetApi().exchange2coin(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 使用过的邀请码
     */
    fun postUseInviteCode(): Flowable<InviteCodeResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().postUseInviteCode(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 非匹配开放时间弹窗内容
     */
    fun getMatchingTip(): Flowable<MatchingTipResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getMatchingTip(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 获取通知栏点击行为
     */
    fun getNotifactionStatus(key: String): Boolean {
        val time = PreferenceUtil.getLong(key, System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2)
        if (System.currentTimeMillis() - time > 1000 * 60 * 60 * 24) {
            PreferenceUtil.commitLong(key, System.currentTimeMillis())
        }
        return System.currentTimeMillis() - time < 1000 * 60 * 60 * 24
    }

    /**
     * 'userId', 'timestamp', 'deviceId', 'sign'
     */
    fun getSystemMessages(): Flowable<SystemMessages> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getSystemNewMessages(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     *
     */
    fun getMyTasks(): Flowable<MyTaskResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getMyInviteTasks(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getShareMessage(type: String): Flowable<ShareMessageResult> {
        val map = linkedMapOf<String, String>()
        map["type"] = type
        map["userId"] = getSPUserid()!!
        return getNetApi().getShareMessage(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 保存引导页状态
     */
    fun saveGuide() {
        PreferenceUtil.commitBoolean("guide_${getUserid()}", true)
    }

    /**
     * 获取引导页状态
     */
    fun getGuideStatus(): Boolean {
        return PreferenceUtil.getBoolean("guide_${getUserid()}", false)
    }

    fun saveUnReadGuide() {
        PreferenceUtil.commitBoolean("guide_unread_${getUserid()}", true)
    }

    fun getUnReadGuideStatus(): Boolean {
        return PreferenceUtil.getBoolean("guide_unread_${getUserid()}", false)
    }

    fun saveGameGuide() {
        PreferenceUtil.commitBoolean("guide_game${getUserid()}", true)
    }

    fun getGameGuideStatus(): Boolean {
        return PreferenceUtil.getBoolean("guide_game${getUserid()}", false)
    }

    fun saveGameGuide2() {
        PreferenceUtil.commitBoolean("guide_game2${getUserid()}", true)
    }

    fun getGameGuideStatus2(): Boolean {
        return PreferenceUtil.getBoolean("guide_game2${getUserid()}", false)
    }

    fun saveGameGuide1() {
        PreferenceUtil.commitBoolean("guide_game1${getUserid()}", true)
    }

    fun getGameGuideStatus1(): Boolean {
        return PreferenceUtil.getBoolean("guide_game1${getUserid()}", false)
    }

    /**
     * 领取三连开红包
     */
    fun getBigGift(): Flowable<GetGiftResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
//    map["isBigPackage"] = "1"
        return getNetApi().getBigGift(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 骰子游戏开始
     * 1小2大
     */
    fun gameStart(toUid: String, expectRs: String): Flowable<GameHttpRequestResult> {
        val map = linkedMapOf<String, String>()
        map["targetUid"] = toUid
        map["expectRs"] = expectRs
        map["userId"] = getSPUserid()!!
        return getNetApi().gameStart(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 骰子游戏处理
     * 'targetUid',//对方uid,即userA
     * 'gameId', // mqtt消息给你的gameId
     * 'responseRs',//0拒绝  1 同意
     */
    fun gameAction(toUid: String, gameId: String, responseRs: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["targetUid"] = toUid
        map["gameId"] = gameId
        map["responseRs"] = responseRs
        map["userId"] = getSPUserid()!!
        return getNetApi().gameAction(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取通知栏点击行为
     */
    fun getConvention(): Boolean {
        val time = PreferenceUtil.getLong("convention_${getUserid()}", System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2)
        if (System.currentTimeMillis() - time > 1000 * 60 * 60 * 24) {
            PreferenceUtil.commitLong("convention_${getUserid()}", System.currentTimeMillis())
        }
        return System.currentTimeMillis() - time > 1000 * 60 * 60 * 24
    }

    /*
   * 上传通讯录
   */
    fun uploadContact(phones: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["mobileNumbers"] = phones
        return getNetApi().uploadContact(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 表白发送
     */
    fun biaodaSendMsg(phone: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["mobile"] = phone
        return getNetApi().biaobaiSend(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

//    /**
//     * 匹配已表白的
//     */
//    fun biaobaiMatch(): Flowable<BiaoBaiMatchResult> {
//        val map = linkedMapOf<String, String>()
//        map["userId"] = getSPUserid()!!
//        return getNetApi().biaobaiMatch(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }

    fun uploadShare(inviteCode: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
//        map["type"] = "shareUrl"
        map["inviteCode"] = inviteCode
        map["userId"] = getSPUserid()!!
        return getNetApi().uploadShare(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取游戏信息
     */
    fun getGameInfo(gameId: String): Flowable<GameInfoResult> {
        val map = linkedMapOf<String, String>()
        map["gameId"] = gameId;
        map["userId"] = getSPUserid()!!
        return getNetApi().getGameInfo(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 匹配已表白的
     */
    fun biaobaiMatch(): Flowable<BiaoBaiMatchResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().biaobaiMatch(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
    }

    /**
     * 备选池用户获取通话ID
     */
    fun getUnknownTalkId(fromid: String): Flowable<UnknownTalkIdResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["fromUserId"] = fromid
        return getNetApi().getUnknownTalkId(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 备选池被叫用户回传通话结果
     */
    fun reportBackupMatchingResult(result: Int): Flowable<UnknownTalkIdResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["result"] = result.toString()
        return getNetApi().reportBackupMatchingResult(niceRequestBody(map)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 银行开关
     */
    fun getHankOpen(): Flowable<HankOpenResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getHankOpen(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun checkRegister(mobile: String): Flowable<CheckRegisterResult> {
        val map = linkedMapOf<String, String>()
        map["mobile"] = mobile
        return getNetApi().checkRegister(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun checkVerify(mobile: String, code: String): Flowable<CheckRegisterResult> {
        val map = linkedMapOf<String, String>()
        map["mobile"] = mobile
        map["verifyCode"] = code
        return getNetApi().checkVerify(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun reportSipServiceStatus(userId: String, talkId: String, status: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["talkId"] = talkId
        map["status"] = status
        return getNetApi().reportSipServiceStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getMatchingUserPushStatus(userId: String, matchingUserId: String, talkId: String): Flowable<MatchingUserPushStatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["matchingUserId"] = matchingUserId
        map["talkId"] = talkId
        return getNetApi().getMatchingUserPushStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun reportMatchingFailed(userId: String, talkId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["talkId"] = talkId
        return getNetApi().reportMatchingFailed(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getBoxOpen(userId: String, channelId: String): Flowable<VivoResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["channelId"] = channelId
        return getNetApi().getBoxOpen(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getGameOpen(userId: String, channelId: String): Flowable<VivoResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["channelId"] = channelId
        return getNetApi().getGameOpen(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getTopicList(userId: String, page: String, pageSize: String): Flowable<UserTopicResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["page"] = page
        map["pageSize"] = pageSize
        return getNetApi().getUserTopicList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getMyTopicList(userId: String, page: String, pageSize: String): Flowable<UserTopicResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["page"] = page
        map["pageSize"] = pageSize
        return getNetApi().getMyTopicList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun publishTopic(userId: String, imgs: String, body: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["imgs"] = imgs
        map["body"] = body
        return getNetApi().publishTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun voteTopic(userId: String, topicId: String): Flowable<LikeResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["topicId"] = topicId
        return getNetApi().voteTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun commentTopic(userId: String, replyMessage: String, topicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["topicId"] = topicId
        map["replyMessage"] = replyMessage
        return getNetApi().commentTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun createReplyNew(userId: String, replyMessage: String, topicId: String,replyToUserId:String,replyId:String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["topicId"] = topicId
        map["replyToUserId"] = replyToUserId
        map["replyId"] = replyId
        map["replyMessage"] = replyMessage
        return getNetApi().createReplyNew(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun postUploadPic(images: List<String>): Flowable<ArrayList<PicResult>> {
        return Flowable.just(images).map {
            return@map Luban.with(niceChatContext()).load(it).filter {
                return@filter !(TextUtils.isEmpty(it) || it.toLowerCase().endsWith(".gif"))
            }.get()
        }.map {
            return@map RequestBodyUtils.getPicMultiparBodyListFormFile("imgs",it)
        }.map {
            val flowables = arrayListOf<PicResult>();
            it.forEach {
                getNetApi().uploadPic(niceQueryMap(UploadAvatarParams(userId = getSPUserid()!!)), imgs = it)
                        .subscribe({
                            if (it.errorCode == 200) {
                                flowables.add(it)
                            } else {
                                EventBus.getDefault().post(PicUploadFaile(it.errorMsg))
                            }
                        }, {
                            EventBus.getDefault().post(PicUploadFaile(it.message))
                        })
            }
            return@map flowables
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    class PicUploadFaile(val msg: String?)

    fun getTopicComment(userId: String): Flowable<TopicCommentResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().getCommentTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getPopulatiryTopList(userId: String, page: String, pageSize: String): Flowable<PopularityTopResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["page"] = page
        map["pageSize"] = pageSize
        return getNetApi().getPopularityTopList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getBankTopList(userId: String, page: String, pageSize: String): Flowable<BankTopResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["page"] = page
        map["pageSize"] = pageSize
        return getNetApi().getBankTopList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getRedTopList(userId: String, page: String, pageSize: String): Flowable<RedTopData> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["page"] = page
        map["pageSize"] = pageSize
        return getNetApi().getRedTopList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserMessage(uid: String): Flowable<UserMessageResult> {
        val userId = getSPUserid()!!
        return getNetApi().getMyMessage(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getMyFriendMsg(): Flowable<UserMessageResult> {
        return getNetApi().getMyFriendMsg(getSPUserid()!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    fun getUnUserMessage(userId: String): Flowable<StatusResult> {
        return getNetApi().getUnMyMessage(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
    /**
     * 获取离线期间系统未读通知
     */
    fun getOfflineSystemMessages(): Flowable<OfflineSystemMessageResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getOfflineSystemMessages(niceRequestBody(map))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getMainMsg(): Flowable<MainMsgResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getMainUnMsg(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteMainMsg(): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().deleteMainUnMsg(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun ReceiveNotification(notificationId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["notificationId"] = notificationId
        return getNetApi().ReceiveNotification(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 请求玩拼手速
     * 'targetUid',  //对方uid,即userB
    'userId',
     */
    fun requestGame2(targetUid: String, userId: String): Flowable<Game2RequestResult> {
        val map = linkedMapOf<String, String>()
        map["targetUid"] = targetUid
        map["userId"] = userId
        return getNetApi().requestGame2(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 同意或不同意玩拼手速
     * 'targetUid',//对方uid,即userA
    'gameId', // mqtt消息给你的gameId
    'responseRs',//0拒绝  1 同意
    'userId'
     */
    fun responseGame2(targetUid: String, gameId: String, responseRs: String, userId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["targetUid"] = targetUid
        map["gameId"] = gameId
        map["responseRs"] = responseRs
        map["userId"] = userId
        return getNetApi().responseGame2(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 游戏7s结束后,上报游戏得分
     * 'gameId', // mqtt消息给你的gameId
    'num',//游戏得分
    'userId'
     */
    fun submitGame2(gameId: String, num: String, userId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["gameId"] = gameId
        map["num"] = num
        map["userId"] = userId
        return getNetApi().submitGame2(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun reportDialUserId(userId: String, toUserId: String, talkId: String): Flowable<String> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["talkId"] = talkId
        map["toUserId"] = toUserId
        return getNetApi().reportDialUserId(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 埋点上传 - 页面统计
     */
    fun postViewEventTrackMessage(eventName: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        if(getSPUserid() == null ) {
            map["userId"] = ""
        } else {
            map["userId"] = getSPUserid()!!
        }
        val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName,
                PackageManager.GET_META_DATA).metaData.getInt("definechannel")
        /*map["channelCode"] = value.toString()*/
        map["eventName"] = eventName
        map["extra"] = value.toString()

        return getNetApi().postViewEventTrack(niceRequestBodyForEventTracking(map))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 埋点上传 - 按钮点击统计
     */
    fun postClickEventTrackMessage(eventName: String, extra: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        if(getSPUserid() != null) {
            map["userId"] = getSPUserid()!!
        } else {
            map["userId"] = ""
        }
        /*val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName,
                PackageManager.GET_META_DATA).metaData.getInt("definechannel")
        map["channelCode"] = value.toString()*/
        map["eventName"] = eventName
        map["extra"] = extra

        return getNetApi().postClickEventTrack(niceRequestBodyForEventTracking(map))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友互评
     * rs 			//1好评 2差评
     * reason		//差评原因，无原因时为空字符串
     */
    fun postFeedBack(toUserId: String, talkId: String, rs: String, reason: String): Flowable<FeedBackResult> {
        val map = linkedMapOf<String, String>()
        map["talkId"] = talkId
        if(getSPUserid() != null) {
            map["userId"] = getSPUserid()!!
        } else {
            map["userId"] = ""
        }
        map["toUserId"] = toUserId
        map["rs"] = rs
        map["reason"] = URLEncoder.encode(reason, "utf-8")
        return getNetApi().postFeedBack(niceRequestBody(map))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取好友在线状态
     */
    fun getFriendOnlineStatus(friendUserId: String): Flowable<OnlineStatus> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["friendUserId"] = friendUserId
        return getNetApi().getFriendOnlineStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取匹配按钮状态
     */
    fun getMatchingButton(): Flowable<MatchingButtonEntity> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getMatchingButton(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取话题列表
     */
    fun getPlazaListInPlaza(): Flowable<PlazaTopicListResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().getTopicList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取话题列表
     */
    fun getPlazaTopic(topicId: String): Flowable<PlazaTopicResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = topicId
        return getNetApi().getTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场 获取 我关注的话题列表
     */
    fun getMyFollowTopic(): Flowable<PlazaTopicListResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().getMyFollowTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场 随便看看
     */
    fun getPlazaCardList(page: String, pageSize: String): Flowable<PlazaFlowCardResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        return getNetApi().getPlazaCardList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场 获取话题热门帖
     */
    fun getTopicHotComment(squareTopicId: String, page: String, pageSize: String): Flowable<PlazaCardResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = squareTopicId
        map["page"] = page
        return getNetApi().getTopicHotComment(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场 获取话题新帖
     */
    fun getTopicNewComment(squareTopicId: String, page: String, pageSize: String): Flowable<PlazaCardResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = squareTopicId
        map["page"] = page
        return getNetApi().getTopicNewComment(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场帖子点赞
     */
    fun postTopicCommentAgree(userId: String, squareTopicId: String, squareTopicCommentId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId
        map["squareTopicCommentId"] = squareTopicCommentId
        return getNetApi().postTopicCommentAgree(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场 置顶话题
     */
    fun postTopTopic(squareTopicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = squareTopicId
        return getNetApi().postTopTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场 取消置顶话题
     */
    fun revokeTopTopic(squareTopicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = squareTopicId
        return getNetApi().revokeTopTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场关注话题
     */
    fun postFollowTopic(squareTopicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = squareTopicId
        return getNetApi().postFollowTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场取消关注话题
     */
    fun postRevokeFollowTopic(squareTopicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["squareTopicId"] = squareTopicId
        return getNetApi().postRevokeFollowTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    /**
     * 广场发布帖子-上传文件
     */
    fun postUploadPicPlaza(images: List<String>): Flowable<ArrayList<PlazaPicResult>> {
        return Flowable.just(images).map {
            return@map Luban.with(niceChatContext()).load(it).filter {
                return@filter !(TextUtils.isEmpty(it) || it.toLowerCase().endsWith(".gif"))
            }.get()
        }.map {
            return@map RequestBodyUtils.getPicMultiparBodyListFormFile("image", it)
        }.map {
            val flowables = arrayListOf<PlazaPicResult>();
            it.forEach {
                getNetApi().uploadPicPlaza(niceQueryMap(UploadAvatarParams(userId = getUserid()!!)), imgs = it)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.errorCode == 200) {
                                flowables.add(it)
                            } else {
                                EventBus.getDefault().post(PicUploadFaile(it.errorMsg))
                            }
                        }, {
                            EventBus.getDefault().post(PicUploadFaile(it.message))
                        })
            }
            return@map flowables
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场发帖
     */
    fun postTopicComment(userId: String, squareTopicId: String?, imgs: String, body: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId ?: ""
        map["commentContent"] = URLEncoder.encode(body, "utf-8")
        map["commentImage"] = imgs
        return getNetApi().postTopicComment(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取消息列表-广场-消息列表
     */
    fun getMySquareMessageList(userId: String): Flowable<UserSquareMessageListResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().getMySquareMessageList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取我的动态-广场-消息列表
     */
    fun getMySquareDynamicList(userId: String): Flowable<UserSquareDynamicListResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().getMySquareDynamicList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-通过ID获取评论（帖子）
     */
    fun getCommentByID(userId: String, squareTopicCommentId: String): Flowable<UserSquareCommentResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicCommentId"] = squareTopicCommentId
        return getNetApi().getCommentByID(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-发布评论（帖子）的回复
     */
    fun postTopicCommentReply(userId: String, squareTopicId: String, squareTopicCommentId: String, replyToReplyId: String,
                              replyImage: String, replyToUserId: String, replyContent: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId                //话题ID
        map["squareTopicCommentId"] = squareTopicCommentId    //评论ID
        map["replyToReplyId"] = replyToReplyId                //回复某个评论时，该评论的ID，否则为空
        map["replyToUserId"] = replyToUserId                //回复某个用户时，该用户的ID，否则为空
        map["replyContent"] = URLEncoder.encode(replyContent, "utf-8")                    //回复内容
        map["replyImage"] = replyImage                        //回复图片
        return getNetApi().postTopicCommentReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-对回复进行点赞
     */
    fun postTopicCommentReplyAgree(userId: String, squareTopicId: String, squareTopicCommentId: String,
                                   squareTopicCommentReplyId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId                //话题ID
        map["squareTopicCommentId"] = squareTopicCommentId    //评论ID
        map["squareTopicCommentReplyId"] = squareTopicCommentReplyId    //回复ID
        return getNetApi().postTopicCommentReplyAgree(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-获取某条评论（帖子）的回复
     */
    fun getCommentReply(userId: String, squareTopicCommentId: String): Flowable<UserSquareCommentReplyResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicCommentId"] = squareTopicCommentId
        return getNetApi().getCommentReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-我删除我的回复
     */
    fun deleteMyReply(userId: String, squareTopicId: String, squareTopicCommentId: String,
                      squareTopicCommentReplyId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId                //话题ID
        map["squareTopicCommentId"] = squareTopicCommentId    //评论ID
        map["squareTopicCommentReplyId"] = squareTopicCommentReplyId    //回复ID
        return getNetApi().deleteMyReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-删除一条动态
     */
    fun deleteDynamic(userId: String, dynamicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["dynamicId"] = dynamicId                //动态ID
        return getNetApi().deleteDynamic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友圈-删除一条动态
     */
    fun deleteCircleDynamic(userId: String, dynamicId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["topicId"] = dynamicId                //动态ID
        return getNetApi().deleteTopic(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-评论（帖子）主删除某条回复
     */
    fun deleteReply(userId: String, squareTopicId: String, squareTopicCommentId: String,
                    squareTopicCommentReplyId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId                //话题ID
        map["squareTopicCommentId"] = squareTopicCommentId    //评论ID
        map["squareTopicCommentReplyId"] = squareTopicCommentReplyId    //回复ID
        return getNetApi().deleteReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-评论（帖子）主设置神评论
     */
    fun setNbReply(userId: String, squareTopicId: String, squareTopicCommentId: String,
                   squareTopicCommentReplyId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId                //话题ID
        map["squareTopicCommentId"] = squareTopicCommentId    //评论ID
        map["squareTopicCommentReplyId"] = squareTopicCommentReplyId    //回复ID
        return getNetApi().setNbReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-评论（帖子）主取消神评论
     */
    fun cancleNbReply(userId: String, squareTopicId: String, squareTopicCommentId: String,
                      squareTopicCommentReplyId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["squareTopicId"] = squareTopicId                //话题ID
        map["squareTopicCommentId"] = squareTopicCommentId    //评论ID
        map["squareTopicCommentReplyId"] = squareTopicCommentReplyId    //回复ID
        return getNetApi().cancleNbReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /*
    * 匹配设置
    * 1允许匹配 2仅允许广场匹配 3仅允许文字匹配 4拒绝匹配
    */
    fun changeMatchingStatus(status: Int): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()!!
        map["status"] = status.toString()
        return getNetApi().changeMatchingStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 匹配设置
     * 1允许匹配 2仅允许广场匹配 3仅允许文字匹配 4拒绝匹配
     */
    fun myMatchStatus(): Flowable<MyMatchStatus> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        return getNetApi().getMyMatchingStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取其他用户匹配状态
     */
    fun getUserMatchingStatus(userId: String): Flowable<MyMatchStatus> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getSPUserid()!!
        map["requestUserId"] = userId
        return getNetApi().getUserMatchingStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-"1分钟内积极回复"任务
     * 上报广场信箱开始聊天
     */
    fun startSquareMessage(userId: String, messageUserId: String): Flowable<StartSquareMessageResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["messageUserId"] = messageUserId
        return getNetApi().startSquareMessage(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场-"1分钟内积极回复"任务
     * 获取当前任务进行状态
     */
    fun getSquareMessageStatus(userId: String, messageUserId: String): Flowable<StartSquareMessageStatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        map["messageUserId"] = messageUserId
        return getNetApi().getSquareMessageStatus(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场未读-获取未读消息
     */
    fun getSquareUnReadMessageNum(userId: String): Flowable<SquareUnReadMessageNumResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().getSquareUnReadMessageNum(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场未读-清空未读消息
     */
    fun clearSquareUnReadMessage(userId: String): Flowable<StatusResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().clearSquareUnReadMessage(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 广场信箱-获取开启私聊消耗魔石的数量
     */
    fun getSquareMessageConsumeStone(userId: String): Flowable<SquareMessageConsumeStoneResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().getSquareMessageConsumeStone(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友聊天-获取礼物列表
     */
    fun getChatGifts(): Flowable<ChatGiftsResult> {
        val map = linkedMapOf<String, String>()
        if (getUserid() != null) {
            map["userId"] = getUserid()!!
        }else{
            map["userId"] = ""
            niceChatContext().niceToast("账号状态存在异常，请重新登录")
        }
        return getNetApi().getChatGifts(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友聊天-赠送礼物
     */
    fun sendGift(toUserId: String, giftId: String): Flowable<SendGiftResult> {
        val map = linkedMapOf<String, String>()
        if (getUserid() != null) {
            map["talkId"] = ""
            map["toUserId"] = toUserId
            map["giftId"] = giftId
            map["userId"] = getUserid()!!
        }else{
            map["userId"] = ""
            niceChatContext().niceToast("账号状态存在异常，请重新登录")
        }
        return getNetApi().sendGift(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友聊天-赠送礼物
     */
    fun sendGiftInVoice(talkId: String, toUserId: String, giftId: String): Flowable<SendGiftResult> {
        val map = linkedMapOf<String, String>()
        if (getUserid() != null) {
            map["talkId"] = talkId
            map["toUserId"] = toUserId
            map["giftId"] = giftId
            map["userId"] = getUserid()!!
        }else{
            map["userId"] = ""
            niceChatContext().niceToast("账号状态存在异常，请重新登录")
        }
        return getNetApi().sendGift(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }



    /**
     * 我的礼物- 送出
     */
    fun getMyGiftLogs(): Flowable<MySharedRoseResult> {
        val map = linkedMapOf<String, String>()
        if (getUserid() != null) {
            map["userId"] = getUserid()!!
        }else{
            map["userId"] = ""
            niceChatContext().niceToast("账号状态存在异常，请重新登录")
        }
        return getNetApi().getMyGiftLogs(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 我的礼物 - 收到
     */
    fun getMyGifts(): Flowable<MyGiftsResult> {
        val map = linkedMapOf<String, String>()
        if (getUserid() != null) {
            map["userId"] = getUserid()!!
        }else{
            map["userId"] = ""
            niceChatContext().niceToast("账号状态存在异常，请重新登录")
        }
        return getNetApi().getMyGifts(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取广场弹窗配置项
     */
    fun getSquarePopConfig(): Flowable<SquarePopConfig> {
        val map = linkedMapOf<String, String>()
        if (getUserid() != null) {
            map["userId"] = getUserid()!!
        } else {
            map["userId"] = ""
            niceChatContext().niceToast("账号状态存在异常，请重新登录")
        }
        return getNetApi().getSquarePopConfig(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /*
     * 空间动态
     */
    fun myDynamicList(friendId:String, page: String, pageSize: String): Flowable<PlazaSpaceCardResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()?:""
        map["friendId"] =friendId
        map["page"] = page
        map["pageSize"] = pageSize
        return getNetApi().myDynamicList(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 好友圈-通过ID（帖子）
     */
    fun getTopicInfoById(topicId: String): Flowable<SpaceCircleResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid()?:""
        map["topicId"] = topicId
        return getNetApi().getTopicInfoById(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
    /**
     * 好友圈-通过ID获取评论（帖子）
     */
    fun getTopicReply(topicId: String): Flowable<SpaceCircleReplyResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] =  getUserid()?:""
        map["topicId"] = topicId
        return getNetApi().getTopicReply(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
    /**
     * 好友圈-获取用户信息
     */
    fun getMyInfo(userId: String): Flowable<SpaceUserInfoResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = userId
        return getNetApi().getMyInfo(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 话题投票测试提交
     */
    fun postQuestionOption(squareTopicId: String, squareTopicType: String, question: String): Flowable<PlazaTopicSubmitResult> {
        val map = linkedMapOf<String, String>()
        map["userId"] = getUserid() ?: ""
        map["squareTopicId"] = squareTopicId
        map["question"] = question//URLEncoder.encode(question, "utf-8")
        map["squareTopicType"] = squareTopicType
        return getNetApi().postQuestionOption(niceRequestBody(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}