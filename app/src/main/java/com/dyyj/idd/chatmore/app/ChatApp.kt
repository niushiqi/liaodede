package com.dyyj.idd.chatmore.app

import android.app.ActivityManager
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.util.Log
import com.amitshekhar.DebugDB
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.BuildConfig
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.model.easemob.CallManager
import com.dyyj.idd.chatmore.model.mqtt.ManageMqtt
import com.dyyj.idd.chatmore.model.mqtt.MqttListener
import com.dyyj.idd.chatmore.model.mqtt.MqttService
import com.dyyj.idd.chatmore.model.mqtt.MqttTag
import com.dyyj.idd.chatmore.model.mqtt.params.MatchingBackupCancel
import com.dyyj.idd.chatmore.model.mqtt.result.*
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.Gson
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions
import com.ishumei.smantifraud.SmAntiFraud
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import com.umeng.socialize.PlatformConfig
import com.uuch.adlibrary.utils.DisplayUtil
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.LinkedBlockingDeque


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */
class ChatApp : Application() {

    var mOnlineTime: Long? = null
    fun setOnlineTime(time: Long) {
        mOnlineTime = time
    }

    fun getCurProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val mActivityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in mActivityManager
                .runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

    fun getOnlineTime() = mOnlineTime

    private val mDataRepository: DataRepository by lazy { DataRepository(this) }
    private val mMQTT: ManageMqtt by lazy { MqttService.getMyMqtt() }

    companion object {
        val LOGSTR = "logstr"
        var mInstance: ChatApp? = null
        fun getInstance(): ChatApp {
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        val pid = android.os.Process.myPid()
        val processAppName = getAppName(pid)
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName!!.equals(packageName, ignoreCase = true)) {

            // 则此application::onCreate 是被service 调用的，直接返回
            Log.e("chatapp", "enter-in")
            return
        }
        Log.e("chatapp", "enter-out")

        PreferenceUtil.init(this)
        initTimber()
        if (BuildConfig.BUILD_TYPE == "debug") {
            DebugDB.getAddressLog()
        }

//    initSIP()
//    initMqtt()
        initEM()

        if (getCurProcessName(this).equals(this.packageName)) {
            val option = SmAntiFraud.SmOption()
            val DEBUG_ORG = "CHqdtoFBs72IEZMUsmdY"
            option.organization = DEBUG_ORG
            option.channel = BuildConfig.FLAVOR
            SmAntiFraud.create(this, option)
        }


//    CrashUtil.getInstance().init(this)

        UMConfigure.init(this, "5b87c03ab27b0a2b03000024"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL)


        PlatformConfig.setWeixin("wxb1266d4ca8421274", "32e2b4484643375c9ad15c92996c7565");
        PlatformConfig.setSinaWeibo("818589070", "e319d8ebe54e61c2b3ab729477e96454", "http://sns.whalecloud.com");
        PlatformConfig.setQQZone("1107705387", "Rl4NBVTw6dtyxklE");
//    //豆瓣RENREN平台目前只能在服务器端配置
//    PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//    PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//    PlatformConfig.setAlipay("2015111700822536");
//    PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//    PlatformConfig.setPinterest("1439206");
//    PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
//    PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
//    PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
//    PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");
        AutoSizeConfig.getInstance().getUnitsManager().setSupportSubunits(Subunits.NONE)
        initDevelopLog()

        //初始化Fresco
        Fresco.initialize(this)
        initDisplayOpinion()
    }

    /**
     * 初始化Timber
     */
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(ThreadAwareDebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    /**
     * 获取应用包名
     */
    private fun getAppName(pID: Int): String? {
        var processName: String? = null
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.runningAppProcesses
        val i = l.iterator()
        val pm = this.packageManager
        while (i.hasNext()) {
            val info = i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid == pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }

        }
        return processName
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //MultiDex.install(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            //内存低的时候,清理Glide缓存
            Glide.get(this).clearMemory()
        }
        //交给 Glide 处理内存情况
        Glide.get(this).trimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        //内存低的时候,清理Glide缓存
        Glide.get(this).clearMemory()
    }

    open fun getDataRepository() = mDataRepository

    /**
     * mqtt
     */
    fun getMQTT() = mMQTT

    /**
     * 初始化mqtt
     */
    open fun initMqtt() {
        MqttService.addMqttListener(mMqttListener)
    }


    /**
     * 注册sip
     */
    private fun initSIP() {
        mDataRepository.initSip()
    }

    /**
     * 初始化环信
     */
    open fun initEM() {
        val options = EMOptions()
// 默认添加好友时，是不需要验证的，改成需要验证
        options.acceptInvitationAlways = false
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = true
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true)
        options.autoLogin = false
//初始化
        EMClient.getInstance().init(applicationContext, options)
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true)
//    mDataRepository.initEasemobListener()
        CallManager.getInstance().init(this)
    }

    /**
     * 回调事件
     */
    private val mMqttListener = object : MqttListener {
        var userid: String? = null
        override fun onConnected() {
            Log.i("MQTT", "连接成功")
            initSubscribe()
            userid = getDataRepository().getUserid()
            val topic = "/ldd/message/$userid"
            //订阅聊天室
            MqttService.getMyMqtt().subTopic(topic, MqttTag.MQTT_QOS)
        }

        /**
         * 初始化订阅类集合
         */
        private fun initSubscribe() {
            MqttService.getMyMqtt()
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_COMMON_AWARD,CommonAwardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_FIRST_CHAT, FirstChatResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_HANG_UP, HangUpResult::class.java)
//                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_LEVEL_UPGRADE, LevelUpgradeResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_NEWBIE_TASK,NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_RANG_AWARD, RangAwardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_NEWPEOPLE_FIRSTMATCHING,NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_VIDEO,CommonAwardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_FRIEND_TALK,FriendExperienceResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_FRIENDSHIP_REWARD,FriendshipRewardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_FRIEND_REQUEST,FriendRequestResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REQUEST_VIDEO_SWITCH,SwitchVideoRequest::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_RESPONSE_VIDEO_SWITCH,SwitchVideoResponse::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_USER_REPORT,UserReportResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_TALK_HANGUP, TalkHangupResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_OFFLINE, OfflineResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REWARD_SCENENTIMACY,CommonAwardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_INVITE_FIRSTINVITE,NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_NEWPEOPLE_SCENEDURATION,NewbieTaskResult3::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_NEWPEOPLE_FIRSTADDFRIEND,NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_INVITESUCCESS, NewbieTaskResult3::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_VIDEOTWICE, NewbieTaskResult3::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_ADDFRIEND_TWICE, NewbieTaskResult3::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SIGN, SignTaskResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_INVITESUM, NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_INVITE_NOFIRSTINVITE, NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_TALK_MATCHING_SWITCHVIDEO, MatchingRequestSwitchVideoResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_TALK_MATCHING_SWITCHVIDEO_RESPONSE, MatchingResponseSwitchVideoResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SIGN_OK, NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_HEART_BIT, HeartBitResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_FIRST_VIDEO, NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_TWICE_MATCHING, NewbieTaskResult3::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REWARD_SCENENTIMACY2, NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_ONLINEONEHOUR, NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_CHANGE_PERSION, ChangePersionResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_TALK_MATCHING, TalkMatchingResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SLYTHERIN_FRAME_HANGUP, SlytherinFrameHangupResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SWITCH_PERSON,SwitchPeopleResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_ONLINEONEHOUR,NewbieTaskResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_FREEZE_COIN, MatchCustomResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_GAME_REQUEST, GameRequestResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_GAME_REFUSE, GameRefuseResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_GAME_RESULT, GameResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_TALK_MATCHING_BACKUP, TalkMatchingBackupResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REWARD_TALK_MATCHING_BACKUP, RewardTalkMatchingBackupResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_MATCHING_BACKUP_CANCEL, MatchingBackupCancel.Data::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_RESET_STATUS, MatchingBackupResetStatus::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_BANK_NEW, BankNewResult::class.java)
                    .initRegisterMessageResult(MqttTag.TASK_REWARD, NoticeTaskRewardResult::class.java)
                    .initRegisterMessageResult(MqttTag.LEVEL_UP, LevelUpgradeResult::class.java)
                    .initRegisterMessageResult(MqttTag.THREEDAY_SUMMARY,ThreeDaySummaryResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_MATCHING_FAILED, MatchingFailedResult2::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REQUEST_GAME2, Game2RequestResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_RESPONSE_GAME2, Game2ResponseResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REJECT_GAME2, Game2RejectResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_RESULT_GAME2, Game2Result::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_NOTIFY_NEW_MESSAGE, NoticeMsgResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_UGC_COMMIT, UgcCommentResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_UGC_TOPIC, UgcTopicResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_UGC_VOTE, UgcVoteResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SYSTEM_MESSAGE, OfflineSystemMessageMqttResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SQUARE_MESSAGE_TASK, SquareMessageTaskRewardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_REWARD_SQUARE_FIRST_COMMENT, SquareFirstCommentRewardResult::class.java)
                    .initRegisterMessageResult(MqttTag.SUBSCRIBE_SEND_GIFT_ON_CHAT, SendGiftOnChatResult::class.java)
        }

        /**
         * 连接失败
         */
        override fun onFail() {
            Log.i("MQTT", "连接失败")
        }

        /**
         * 丢失连接
         */
        override fun onLost() {
            Log.i("MQTT", "丢失连接")
        }

        /**
         * 接收消息
         */
        override fun onReceive(message: String) {
            if (message.isNotEmpty()) {
                //收到消息
                Log.e("MQTT Rec: ", message)
                val jsonObject = JSONObject(message)
                val type = jsonObject.optString("messageType")
                val notificationId = jsonObject.optString("notificationId")
//        if (type == SUBSCRIBE_ONLINEONEHOUR) {
//          niceToast("收到切人mqtt")
//        }
                val data = jsonObject.optString("data")
                Timber.tag("MQTT").i("收到推送: " + message)
                //Log.i("MQTT", message)
//        mDataRepository.insertMQTTLog(type, data)
                val result = getMQTT().messageResults[type]
                if (result != null) {
                    val obj = Gson().fromJson(data, result)
                    if (type == MqttTag.SUBSCRIBE_HEART_BIT) {
                        Log.e("socketheard", "mqttindex-" + (obj as HeartBitResult).receipt)
                        val index = (obj as HeartBitResult).receipt
                        if (index > ChatApp.getInstance().getDataRepository().mqttHeardReturnIndex) {
                            ChatApp.getInstance().getDataRepository().mqttHeardReturnIndex = index
                        }
                    } else {
                        when (type) {
                            MqttTag.SUBSCRIBE_NEWBIE_TASK -> (obj as NewbieTaskResult2).title = "首次登录，新手福利"
                            MqttTag.SUBSCRIBE_FIRST_CHAT -> (obj as FirstChatResult).title = "首次开启聊天"
                            MqttTag.SUBSCRIBE_NEWPEOPLE_FIRSTADDFRIEND -> (obj as NewbieTaskResult2).title = "首次互加好友"
                            MqttTag.SUBSCRIBE_NEWPEOPLE_FIRSTMATCHING -> (obj as NewbieTaskResult2).title = "首次开启聊天"
                            MqttTag.SUBSCRIBE_SIGN_OK -> {
                                (obj as NewbieTaskResult2).title = "每日自动签到"
                                (obj as NewbieTaskResult2).autoClose = false
                            }
                            MqttTag.SUBSCRIBE_FIRST_VIDEO -> (obj as NewbieTaskResult2).title = "首次开视频"
                        }
                        if (!notificationId.isNullOrBlank()) {
                            mDataRepository.ReceiveNotification(notificationId).subscribe({
                                Log.d("notificationid", it.errorMsg)
                            },
                                    {
                                        Log.d("notificationid", it.message)
                                    })
                        }
                        if ((obj is NewbieTaskResult2)) {
                            queue.add(obj)
                            runTaskQueue()
                        } else {
                            EventBus.getDefault().post(obj)
                        }
                    }
                }
            }

        }

        /**
         * 发送消息成功
         */
        override fun onSendSucc() {
            Log.i("MQTT", "mqtt消息发送成功")

        }
    }

    private fun runTaskQueue() {
        if (queue.size == 1) {
            val obj = queue.peekFirst()
            EventBus.getDefault().post(obj)
        }
    }

    open fun runNestTaskQueue() {
        queue.pollFirst()
        if (queue.size != 0) {
            val obj = queue.peekFirst()
            EventBus.getDefault().post(obj)
        }
    }

    /**
     * UI无显示 BUG 修改记录
     * 添加原因：测试包混乱，导致经常出现测错包情况／打包混乱，经常出现改的功能未打入包情况
     * 添加建议：一些显示不到UI上的bug建议在次方法中打印log，方便知道测试包中有着功能
     */
    private fun initDevelopLog() {
        Timber.tag("developLog").i("修改功能记录：")
        Timber.tag("developLog").i("1.解决多用户崩溃问题 2.修改注册用户上报渠道号位置")
        Timber.tag("developLog").i("201812181614 解决拼手速崩溃问题，添加异常打印，关闭httplog打印")
    }

    open val queue: LinkedBlockingDeque<Any> = LinkedBlockingDeque<Any>()
    var userDetailInfo: UserDetailInfoResult.Data? = null

    private fun initDisplayOpinion() {
        var dm = getResources().getDisplayMetrics()
        DisplayUtil.density = dm.density
        DisplayUtil.densityDPI = dm.densityDpi
        DisplayUtil.screenWidthPx = dm.widthPixels
        DisplayUtil.screenhightPx = dm.heightPixels
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels.toFloat()).toFloat()
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels.toFloat()).toFloat()
    }

}
