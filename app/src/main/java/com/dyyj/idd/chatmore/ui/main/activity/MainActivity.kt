package com.dyyj.idd.chatmore.ui.main.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMainBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.model.easemob.AudioMngHelper
import com.dyyj.idd.chatmore.model.easemob.CallManager
import com.dyyj.idd.chatmore.model.easemob.EasemobManager
import com.dyyj.idd.chatmore.model.getui.BadgeIntentService
import com.dyyj.idd.chatmore.model.getui.GeTuiManaage
import com.dyyj.idd.chatmore.model.mqtt.MqttService
import com.dyyj.idd.chatmore.model.mqtt.result.*
import com.dyyj.idd.chatmore.ui.dialog.activity.*
import com.dyyj.idd.chatmore.ui.event.ReceiveTaskEvent
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.main.fragment.CallFragment
import com.dyyj.idd.chatmore.ui.main.fragment.CallOutgoingFragment
import com.dyyj.idd.chatmore.ui.main.fragment.OpenCallFragment
import com.dyyj.idd.chatmore.ui.main.fragment.SystemMessageFragment
import com.dyyj.idd.chatmore.ui.user.activity.MeActivity
import com.dyyj.idd.chatmore.ui.user.activity.RankingActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WalletActivity
import com.dyyj.idd.chatmore.utils.*
import com.dyyj.idd.chatmore.viewmodel.*
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import com.dyyj.idd.chatmore.weiget.pop.*
import com.google.gson.Gson
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.taobao.sophix.SophixManager
import com.umeng.analytics.MobclickAgent
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/13
 * desc   : 主页
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MyFrameAnimation.OnFrameAnimationListener, EMMessageListener, SensorEventListener {

    var mContent: Fragment? = null
    var mForContent: Fragment? = null
    private var headsetReceiver: HeadsetReceiver? = null
    private var isForeground = true

    open var api = null
    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null
    var powerManager: PowerManager? = null
    var wakeLock: PowerManager.WakeLock? = null
    private var audioMngHelper: AudioMngHelper? = null
    var obj: TalkMatchingResult? = null


    companion object {

        //通话中(拨入)
        const val TYPE_CALL_IN = "call_in"
        //通话中(拨出)
        const val TYPE_CALL_OUT = "call_out"
        //开启聊天
        const val TYPE_OPEN_CALL = "open_call"
        //拨打电话
        const val TYPE_OUTGOING = "outgoing"
        //视频呼入方
//        const val TYPE_VIDEO_IN = "video_in"
        //视频拨出方
//        const val TYPE_VIDEO_OUT = "video_out"
        //对方用户id
        const val OUTGOING_USERNAME = "username"
        //通话发起方
        const val FROM_USERID = "from_userid"
        //通话被叫方
        const val TO_USERID = "to_userid"
        //通话状态ID
        const val TALKID = "talkId"
        const val RESUME = "resume"
        //拨打电话类型
        var isVoiceCall: Boolean = true

        var MSG_LOAD_UNREAD_MESSAGE: Int = 100
        var MSG_TASK: Int = 101
        var unReadFriendCount: Int = 0
        var unReadCircleCount: Int = 0
        var unReadMessageCount: Int = 0
        var unReadSquareCount: Int = 0

        //广场开启弹窗 - 控制位
        var isSquarePop: Boolean = true
        var squarePopNumber: Int = 40
        var squarePopImage: String = Uri.parse("res://com.dyyj.idd.chatmore.dev/" + R.drawable.bg_square_tanchuang).toString()
        var squarePopGotoTarget: String = "square#1"

        /**
         * 通话中
         */
        fun startCallOut(context: Context, fromUserid: String, toUserid: String, resume: Boolean = false) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(TYPE_CALL_OUT, true)
            intent.putExtra(FROM_USERID, fromUserid)
            intent.putExtra(TO_USERID, toUserid)
            intent.putExtra(RESUME, resume)
            context.startActivity(intent)
        }

        /**
         * 通话中(呼叫进来)
         */
        fun startCallIn(context: Context, fromUserid: String, toUserid: String,
                        resume: Boolean = false) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(TYPE_CALL_IN, true)
            intent.putExtra(FROM_USERID, fromUserid)
            intent.putExtra(TO_USERID, toUserid)
            intent.putExtra(RESUME, resume)
            context.startActivity(intent)
        }

        /**
         * 开启聊天
         */
        fun startOpenCall(context: Context) {
            EasemobManager.isStartMatch = false
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(TYPE_OPEN_CALL, true)//TYPE_OPEN_CALL = open_call
            context.startActivity(intent)
        }

        /**
         * 匹配中界面
         */
        fun startOutgoing(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(TYPE_OUTGOING, true)
            context.startActivity(intent)
        }

        /**
         * 语音视频(拨出)
         */
//        fun startVideoOut(context: Context, fromUserid: String, toUserid: String, talkId: String) {
//            val intent = Intent(context, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra(TYPE_VIDEO_OUT, true)
//            intent.putExtra(FROM_USERID, fromUserid)
//            intent.putExtra(TO_USERID, toUserid)
//            intent.putExtra(TALKID, talkId)
//            context.startActivity(intent)
//        }

        /**
         * 语音视频(来电呼叫)
         */
//        fun startVideoIn(context: Context, fromUserid: String, toUserid: String, talkId: String) {
//            val intent = Intent(context, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra(TYPE_VIDEO_IN, true)
//            intent.putExtra(FROM_USERID, fromUserid)
//            intent.putExtra(TO_USERID, toUserid)
//            intent.putExtra(TALKID, talkId)
//            context.startActivity(intent)
//        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onViewModel(): MainViewModel {
        return MainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //状态栏透明和间距处理
        StatusBarUtilV2.immersive(this)
        mToolbar?.let {
            StatusBarUtilV2.setPaddingSmart(this, it)
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        super.onCreate(savedInstanceState)
        //适配状态栏
        StatusBarUtilV2.immersive(this)
        val layoutParams = mBinding.statusbarIv.layoutParams
        layoutParams.height = getStatusBarHeight()
        mBinding.statusbarIv.layoutParams = layoutParams
        setStatusUI(Status.Loading)
        //    mViewModel.netMainInfo()
        initListener()
        MqttService.start(this)
        initCallStatus()
        initEM()
        mViewModel.netMainInfo()
//    MqttService.addMqttListener(mattListener)
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_UNREAD_MESSAGE, 1000)
        mHandler.sendEmptyMessageDelayed(MSG_TASK, 1000)

        registerReceiver()
        mViewModel.checkVersionUpdate(this)
        GeTuiManaage.init(this)
        //感光不好用,暂时注掉
//    registerProximitySensorListener()
        audioMngHelper = AudioMngHelper(this)
//    RedPacketPop(this).show(mBinding.root)

        mBinding.rlRedTake.visibility = View.GONE

//        val subscribe1 = Observable.interval(5 * 10, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { mViewModel.loadUnreadMessage() }
//        val subscribe2 = Observable.interval(30 * 1000, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    if (isForeground) {
//                        mViewModel.getUserData()
//                        mViewModel.getApplyNum()
//                    }
//                }
//        mViewModel.mCompositeDisposable.add(subscribe1)
//        mViewModel.mCompositeDisposable.add(subscribe2)

        //进入主页面,获取离线未读系统消息
        mViewModel.getOfflineSystemMessages()

        //检查热修复版本
        SophixManager.getInstance().queryAndLoadNewPatch()

        //获取广场弹窗配置项
        mViewModel.getSquarePopConfig()

        /*try {
            val badgeCount = 2
            BadgeUtils.setBadgeCount(this, badgeCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        /*try {
            var field = notification.getClass().getDeclaredField("extraNotification")
            var extraNotification = field.get(notification)
            var method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class)
            method.invoke(extraNotification, 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (CallManager.getInstance().audioManager.isWiredHeadsetOn) {      // 如果耳机已插入，设置距离传感器失效
            return
        }
        if (event == null) return

        if (CallManager.getInstance().isOpenSpeaker) {  // 如果音频正在播放
            val distance = event.values[0]
            if (sensor != null && distance >= sensor?.maximumRange!!) {     // 用户远离听筒，音频外放，亮屏
                CallManager.getInstance().openSpeaker()
                setScreenOn()
            } else {    // 用户贴近听筒，切换音频到听筒输出，并且熄屏防误触
                CallManager.getInstance().closeSpeaker()
                setScreenOff()
            }
        } else {    // 音频播放完了
            if (wakeLock != null && wakeLock?.isHeld!!) {    // 还没有release点亮屏幕
                wakeLock?.release()
                wakeLock = null
            }
        }

    }

    /**
     * 熄屏
     */
    private fun setScreenOff() {
        if (wakeLock == null) {
            wakeLock = powerManager?.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                    MainActivity::class.java.simpleName)
        }
        wakeLock?.acquire()
    }

    /**
     * 亮屏
     */
    private fun setScreenOn() {
        if (wakeLock != null) {
            wakeLock?.setReferenceCounted(false)
            wakeLock?.release()
            wakeLock = null
        }
    }


    /**
     * 注册距离感应器监听器，监测用户是否靠近手机听筒
     */
    private fun registerProximitySensorListener() {
        powerManager = getSystemService(POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, MainActivity::class.java.simpleName)

        sensorManager = getSystemService(SENSOR_SERVICE) as? SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun registerReceiver() {
        //注册扬声器广播(扬声器/耳机)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        headsetReceiver = HeadsetReceiver()
        registerReceiver(headsetReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        //当用户使用自有账号登录时，可以这样统计：
        MobclickAgent.onProfileSignIn(mDataRepository.getUserid());
        //当用户使用第三方账号（如新浪微博）登录时，可以这样统计：
        //MobclickAgent.onProfileSignIn("WB","userID");
        //登出
        //MobclickAgent.onProfileSignOff();
        ChatApp.getInstance().initEM()
    }

    /**
     * 登陆环信帐号
     */
    fun initEM() {
        val userid = mDataRepository.getUserid()
        mViewModel.netLoginEM(this, userid ?: "0", userid ?: "0")
    }

    /**
     * 初始化通话状态
     */
    fun initCallStatus() {
        mDataRepository.registerCall(this)
    }

    fun initListener() {
        /*mBinding.vipIv.setOnClickListener {
            niceToast("动画开始")
            showAnim()
        }*/
        mBinding.horizontalProgressBar.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            //点击升级跳转meAct
            MeActivity.start(this)
        }
        //新金币提示的图标
        mBinding.imgNewCoin.setOnClickListener({
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            mBinding.imgNewCoin.setVisibility(View.GONE)
            val netUrl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserid()}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
            H5Activity.start(this, netUrl, "社交银行", true)
        })
        if (EMClient.getInstance().chatManager() != null) {
            EMClient.getInstance().chatManager().addMessageListener(this)
        }
        /*mBinding.tipIv.setOnClickListener {//1.4版本废弃
            if (!NotificationManagerCompat.from(this@MainActivity).areNotificationsEnabled()) {
                val show = NotifactionPop(this@MainActivity).show(it, "红包可领时，我们将立刻通知你", "不错过每一个红包", "tip")
                if (show) return@setOnClickListener
            }
            WalletActivity.start(this)
        }*/
        mBinding.avatarIv.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            MeActivity.start(this)
//            MyCircleActivity.start(this@MainActivity)
            EventTrackingUtils.joinPoint(EventBeans("ck_home_head", ""))
        }

        mBinding.walletLl.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            //提现的整个ll跳转到钱包
            WalletActivity.start(this)
            EventTrackingUtils.joinPoint(EventBeans("ck_home_wallet", ""))
        }
        mBinding.walletLl2.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            //提现的整个ll跳转到钱包
            WalletActivity.start(this)
            EventTrackingUtils.joinPoint(EventBeans("ck_home_wallet", ""))
        }

        mBinding.tvLevelTag.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            //点击升级跳转meAct
            MeActivity.start(this)
        }

        //去往排行榜
        mBinding.viewRanking.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }
            EventTrackingUtils.joinPoint(EventBeans("ck_home_rank",""))
            RankingActivity.start(this)
        }

        /*mBinding.niushiqiTest.setOnClickListener {
            var advList : ArrayList<AdInfo> = arrayListOf()
            var adInfo = AdInfo()
            //adInfo.activityImg = "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png"
            adInfo.activityImg = Uri.parse("res://" + getPackageName() + "/" + R.drawable.bg_square_tanchuang).toString()
            advList.add(adInfo)

            *//*adInfo = AdInfo()
            adInfo.activityImg = "https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage2.png"
            advList.add(adInfo)*//*

            var adManager = AdManager(this@MainActivity, advList)
            adManager.setOverScreen(true)
                    .setDialogCloseable(true)
                    .setWidthPerHeight(0.86f)
                    .showAdDialog(AdConstant.ANIM_DOWN_TO_UP)*//*
            SophixManager.getInstance().queryAndLoadNewPatch()
        }*/

//    mBinding.inviteTv.setOnClickListener {
//      InviteNewActivity.start(this@MainActivity)
//    }
//    mBinding.taskTv.setOnClickListener { TaskSystemActivity.start(this) }
//    mBinding.messageTv.setOnClickListener {
//      if (!NotificationManagerCompat.from(this@MainActivity).areNotificationsEnabled()) {
//        val show = NotifactionPop(this@MainActivity).show(it, "好友发消息了,我们将立刻通知你", "不错过每一次畅聊",
//                                                          "message")
//        if (show) return@setOnClickListener
//      }
//      MessageSystemActivity.start(this)
//    }
    }

    /**
     * 随机奖励事件设置
     */
    fun setRangWaredListener() {

        mBinding.rangBtn.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }

            //领取奖励
            mViewModel.getRangAwardResult()?.rewardId?.let {
                mViewModel.netGetReward(it)
            }

            //打开随机奖励Toast
            ToastActivity.start(this@MainActivity, mViewModel.getRangAwardResult()?.rewardCoin,
                    mViewModel.getRangAwardResult()?.rewardStone,
                    mViewModel.getRangAwardResult()?.rewardCash)
            mBinding.rangBtn.visibility = View.GONE
            mBinding.rangAwardIv.visibility = View.GONE
        }

        mBinding.rangAwardIv.setOnClickListener {
            if (!DeviceUtils.isConn(this)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                niceToast("当前无可用网络")
                return@setOnClickListener
            }

            //领取奖励
            mViewModel.getRangAwardResult()?.rewardId?.let {
                mViewModel.netGetReward(it)
            }

            //打开随机奖励Toast
            ToastActivity.start(this@MainActivity, mViewModel.getRangAwardResult()?.rewardCoin,
                    mViewModel.getRangAwardResult()?.rewardStone,
                    mViewModel.getRangAwardResult()?.rewardCash)
            mBinding.rangBtn.visibility = View.GONE
            mBinding.rangAwardIv.visibility = View.GONE

        }
    }

    var mFromUserId: String? = null // from 自己
    var mToUserId: String? = null // to 别人
    private var mResume: Boolean = false

    private fun getStatusBarHeight(): Int {
        val resources = resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 跳转不同的Fragment
     */
    private fun goFragment(intent: Intent) {
        when {
            //拨入语音
            intent.getBooleanExtra(TYPE_CALL_IN, false) -> {
                mFromUserId = intent.getStringExtra(FROM_USERID)
                mToUserId = intent.getStringExtra(TO_USERID)
                mResume = intent.getBooleanExtra(RESUME, false)
                addFragment(CallFragment.instance())
                mViewModel.isChating = true
                hideFragment2()
            }
            //拨出语音
            intent.getBooleanExtra(TYPE_CALL_OUT, false) -> {
                mFromUserId = intent.getStringExtra(FROM_USERID)
                mToUserId = intent.getStringExtra(TO_USERID)
                mResume = intent.getBooleanExtra(RESUME, false)
                addFragment(CallFragment.instance())

                mViewModel.isChating = true
                hideFragment2()
            }
            //开启聊天界面
            intent.getBooleanExtra(TYPE_OPEN_CALL, false) -> {
                addFragment(OpenCallFragment.instance())
                mViewModel.isChating = false
                hideFragment2()
            }
//              intent.getBooleanExtra(TYPE_OPEN_CALL, false) -> addFragment(CallFragment.instance().setFromUserId(intent.getStringExtra(FROM_USERID)).setToUserId(
//              intent.getStringExtra(TO_USERID)).setResume(intent.getBooleanExtra(RESUME, false)))
            //匹配中界面
            intent.getBooleanExtra(TYPE_OUTGOING, false) -> {
                addFragment(CallOutgoingFragment.instance())
                mViewModel.isChating = false
                hideFragment2()
            }

//        //拨入视频
//            intent.getBooleanExtra(TYPE_VIDEO_IN, false) -> {
//                addFragment(VideoFragment.instance().setVideoIn(true).setFromUserId(
//                        intent.getStringExtra(FROM_USERID)).setToUserId(
//                        intent.getStringExtra(TO_USERID)).setTalkId(intent.getStringExtra(TALKID)))
//                mViewModel.isChating = true
//            }
//        //拨出视频
//            intent.getBooleanExtra(TYPE_VIDEO_OUT, false) -> {
//                addFragment(VideoFragment.instance().setVideoOut(true).setFromUserId(
//                        intent.getStringExtra(FROM_USERID)).setToUserId(
//                        intent.getStringExtra(TO_USERID)).setTalkId(intent.getStringExtra(TALKID)))
//                mViewModel.isChating = true
//            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // 刷新未读
        mViewModel.loadUnreadMessage()
        mViewModel.getApplyNum()
        intent?.let {
            goFragment(it)
        }
    }


    /**
     * 替换Fragment
     */
    fun addFragment(fragment: Fragment) {
        if (mContent != fragment) {
            mContent?.onDestroy()
            mContent = fragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment, fragment::class.java.simpleName)
            try {
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addFragment2(fragment: SystemMessageFragment) {
        /*if (mForContent != null) {
            return
        }*/
        //隐藏首页头部信息
        mBinding.toolbarBgIv.visibility = View.GONE
        mBinding.userCl.visibility = View.GONE
        mBinding.walletLl2.visibility = View.VISIBLE
        val layoutParams = mBinding.statusbarIv.layoutParams
        layoutParams.height = getStatusBarHeight()+ DisplayUtils.dp2px(this, 25f)
        mBinding.statusbarIv.layoutParams = layoutParams


        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
                R.anim.fragment_silde_up_in,
                R.anim.fragment_silde_down_out,
                R.anim.fragment_silde_up_in,
                R.anim.fragment_silde_down_out)
        if (mForContent != null) {
            transaction.remove(mForContent!!)
            mForContent = null
        } else {
            fragment.refresUnMsg()
            transaction.replace(R.id.fragmentContainer2, fragment, fragment::class.java.simpleName)
            mForContent = fragment
        }
        try {
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun addFragment2(fragment: SystemMessageFragment) {
//        var fragment2 = ForContentFragment.instance()
//
//        if (mForContent != null) {
//            return
//        }
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(
//                R.anim.fragment_silde_up_in,
//                R.anim.fragment_silde_down_out,
//                R.anim.fragment_silde_up_in,
//                R.anim.fragment_silde_down_out)
//        if (mForContent != null) {
//            transaction.remove(mForContent)
//            mForContent = null
//        } else {
//            transaction.replace(R.id.fragmentContainer2, fragment2, fragment2::class.java.simpleName)
//            mForContent = fragment2
//        }
//        try {
//            transaction.commitAllowingStateLoss()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun showAnim() {
        // 步骤1：设置动画属性的初始值 & 结束值
        val valueAnimator = ValueAnimator.ofInt(mBinding.userCl.getLayoutParams().height, 0)

        // 2. 将传入的多个Int参数进行平滑过渡:此处传入0和1,表示将值从0平滑过渡到1
        // 如果传入了3个Int参数 a,b,c ,则是先从a平滑过渡到b,再从b平滑过渡到C，以此类推
        // ValueAnimator.ofInt()内置了整型估值器,直接采用默认的.不需要设置，即默认设置了如何从初始值 过渡到 结束值
        // 关于自定义插值器我将在下节进行讲解
        // 下面看看ofInt()的源码分析 ->>关注1

        // 步骤2：设置动画的播放各种属性
        valueAnimator.duration = 2000
        // 设置动画运行的时长
        valueAnimator.addUpdateListener { animator ->
            val currentValue = animator.animatedValue as Int
            // 获得每次变化后的属性值
            println(currentValue)
            // 输出每次变化后的属性值进行查看

            mBinding.userCl.getLayoutParams().height = currentValue
            // 每次值变化时，将值手动赋值给对象的属性
            // 即将每次变化后的值 赋 给按钮的宽度，这样就实现了按钮宽度属性的动态变化

            // 步骤4：刷新视图，即重新绘制，从而实现动画效果
            mBinding.userCl.requestLayout()
        }
        valueAnimator.start()
    }

    fun hideFragment2() {
        //显示首页头部信息

        mBinding.toolbarBgIv.visibility = View.VISIBLE
        mBinding.userCl.visibility = View.VISIBLE
        mBinding.walletLl2.visibility = View.GONE
        val layoutParams = mBinding.statusbarIv.layoutParams
        layoutParams.height = getStatusBarHeight()//+ DisplayUtils.dp2px(this, 25f)
        mBinding.statusbarIv.layoutParams = layoutParams


        if (mForContent != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out,
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out)
            transaction.remove(mForContent!!)
            mForContent = null
            try {
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 定时是否首次登陆
     */
    fun timberFirstLogin() {
        mViewModel.getMainResult()?.data?.user?.firstLogin?.let {

            //如果是第一次登陆,请求首次登陆奖励mqtt接口
            if (it == 1) {
                mViewModel.timberFirstLogin()
            }
        }
    }

    override fun onStartFrameAnimation() {

        val count: Long = 5
        val subscribe = Flowable.interval(2, 1, TimeUnit.SECONDS)//设置2延迟，每隔一秒发送一条数据
                .take(count + 1) //设置循环11次
                .map { count - it }.observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({
                    mBinding.rangBtn.visibility = View.VISIBLE
                    mBinding.rangBtn.text = "领取 ${it}s"
                    if (it.toInt() == 5) {
                        setRangWaredListener()
                    }

                }, {}, {
                    mBinding.rangBtn.visibility = View.GONE
                    mBinding.rangAwardIv.visibility = View.GONE
                })
        mViewModel.mCompositeDisposable.add(subscribe)
        mBinding.rangAwardIv.visibility = View.VISIBLE
    }

    override fun onEnd() {
    }

    override fun onDestroy() {
        if (headsetReceiver != null) {
            unregisterReceiver(headsetReceiver)
            sensorManager?.unregisterListener(this)
        }

        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        MqttService.stop(this)
        if (EMClient.getInstance().chatManager() != null) {
            EMClient.getInstance().chatManager().removeMessageListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
        mViewModel.netNewMsg()
        mViewModel.getSquareUnreadMessage()
//        mViewModel.netMainInfo()
    }

    override fun onPause() {
        super.onPause()
        isForeground = false
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        for (fragment in supportFragmentManager.fragments) {
            fragment?.let {
                it.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    /**
     * 随机奖励
     */
    @Subscribe
    fun onSubscribeResult(obj: RangAwardResult) {
        if (isCureentActivity()) {
            mViewModel.setRangAwardResult(obj)
            AnimationUtils.start(this, mBinding.rangAwardIv, "ts", 1, 107, 65, this)
            EventBus.getDefault().post(CallViewModel.RefreshWalletCallFragment())
        }
    }

    @Subscribe
    fun onAddFragment(tag: String) {
        if (tag == CallFragment::class.java.name) {
            addFragment(CallFragment.instance())
        }
    }

    @Subscribe
    fun onAvatarVM(obj: UploadAvatarViewModel.AvatarVM) {
        showProgressDialog()
        mViewModel.netUploadAvatar(obj.obj)
    }

    @Subscribe
    fun onUploadAvatar(obj: MainViewModel.AvatarVM) {
        closeProgressDialog()
        obj.obj?.avatar?.let {
            mViewModel.netReuploadAvatar(arrayListOf(it))
        }
    }

    @Subscribe
    fun onEventMessage(msg: EventMessage<Any>) {
        if (msg.tag.equals(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT)){
            when(msg.what){
                EventConstant.WHAT.WHAT_UNREAD_FRIEND_COUNT -> {
                    unReadFriendCount = msg.obj as Int
                }
                EventConstant.WHAT.WHAT_UNREAD_MESSAGE_COUNT ->{
                    unReadMessageCount = msg.obj as Int
                }
                EventConstant.WHAT.WHAT_UNREAD_CIRCLE_COUNT -> {
                    unReadCircleCount = msg.obj as Int
                }
                EventConstant.WHAT.WHAT_UNREAD_SQUARE_COUNT -> {
                    unReadSquareCount = msg.obj as Int
                }
            }
        }
    }

    //新金币动画提示
    @Subscribe
    fun onBanknewVM(obj: BankNewResult) {
        if (obj.message?.line1 != null) {
            Log.e("onBanknewVM", "收到新金币了")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBinding.imgNewCoin.z = 1f
            }
            mBinding.imgNewCoin.setVisibility(View.VISIBLE)
            val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.coin_new)
            mBinding.imgNewCoin.startAnimation(animation)
        }
    }

    @Subscribe
    fun onRewardVM(obj: MainViewModel.RewardVM) {
//    finish()
    }

    @Subscribe
    fun onFriendRequestVM(obj: FriendRequestResult) {
//      FriendRequestDialogActivity.start(this@MainActivity, obj)
        val friendPop = FriendRequestPop(this@MainActivity)
        friendPop.initData(obj)
        friendPop.show(mBinding.root)
    }

    /**
     * 条幅显示
     */
    @Subscribe
    fun onSubscribeMessageResult(obj: NewbieTaskResult3) {
        if (isCureentActivity()) {
            MessageTipPop(this@MainActivity).initData(obj.rewardId.toString(),
                    obj?.message?.line1!!).enableTime().show(
                    mBinding.root)
        }
    }

    /**
     * 消耗奖金
     */
    @Subscribe
    fun onCustomCoin(obj: CallViewModel.CustomCoin) {
        mBinding.txtGoldSmall1.text = "+ ${obj.rewardCoin}"
        mBinding.txtGoldSmall2.text = "+ ${obj.rewardCoin}"
        mBinding.txtGoldSmall3.text = "+ ${obj.rewardCoin}"
        AnimationUtils.startPigGolds(mBinding.tipIv, mBinding.clGoldSmall, mBinding.llGoldSmall1,
                mBinding.llGoldSmall2, mBinding.llGoldSmall3)
    }

    /**
     * 匹配成功后消耗金币
     */
    @Subscribe
    fun onMatchCustomCoin(obj: MatchCustomResult) {
        OpenCallFragment.mGoldTake = obj.freezeCoin?.toInt() ?: 0
        mBinding.txtGoldSmall1.text = "+ ${obj.rewardCoin}"
        mBinding.txtGoldSmall2.text = "+ ${obj.rewardCoin}"
        mBinding.txtGoldSmall3.text = "+ ${obj.rewardCoin}"
        AnimationUtils.startPigGolds(mBinding.tipIv, mBinding.clGoldSmall, mBinding.llGoldSmall1,
                mBinding.llGoldSmall2, mBinding.llGoldSmall3)
    }

    @Subscribe
    fun onMainVM(obj: MainViewModel.MainVM) {

        setStatusUI(Status.Normal)
//    MqttService.removeMqttListener(mattListener)
        obj.isSuccess?.let {
            if (it) {
                if (obj?.obj?.data?.userBaseInfo?.nextLevelExperience != 0) {
                    if (obj?.obj?.data?.userBaseInfo?.userExperience!! >= obj?.obj?.data?.userBaseInfo?.nextLevelExperience!!) {
                        mBinding.tvLevelTag.visibility = View.VISIBLE
                    } else {
                        mBinding.tvLevelTag.visibility = View.GONE
                    }

                    mBinding.horizontalProgressBar.max = obj?.obj?.data?.userBaseInfo?.nextLevelExperience!!
                    mBinding.tvJingyan.text = "" + obj?.obj?.data?.userBaseInfo?.userExperience + "/" + obj?.obj?.data?.userBaseInfo?.nextLevelExperience
                    mBinding.horizontalProgressBar.progress = obj?.obj?.data?.userBaseInfo?.userExperience!!
                } else {
                    mBinding.horizontalProgressBar.max = 100
                    mBinding.horizontalProgressBar.progress = 100
                }
                OpenCallFragment.mGoldTake = obj.obj?.data?.userWallet?.freezeCoin?.toInt() ?: 0
                OpenCallFragment.startTime = obj.obj?.data?.matching?.startTime ?: 0
                OpenCallFragment.endTime = obj.obj?.data?.matching?.endTime ?: 0
                goFragment(intent)
                timberFirstLogin()
                obj.obj?.data?.let {
                    mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(it))
                    mDataRepository.saveUserWalletEntity(it).subscribe({}, {})
                }
            } else {
                Log.i("mytag", "数据获取失败,请重新登录")
                niceToast("数据获取失败,请重新登录")
            }
        }
    }

    /**
     * 红包生长中刷新
     */
    @Subscribe
    fun onRedTimeVM(obj: DataRepository.RedPacketTimeVM) {
        mBinding.root.post {
            mBinding.pbRedTake.progress = obj.progress
            mBinding.txtTime.text = obj.time
            mBinding.txtDes.visibility = View.GONE
            if (obj.finish) {
                mBinding.pbRedTake.progress = 100
                mBinding.txtTime.visibility = View.GONE
                mBinding.txtDes.visibility = View.VISIBLE
            }
        }
    }

//  @Subscribe
//  fun onUnreadMessageVM(obj: MainViewModel.UnreadMessageVM) {
//    if (obj.isSuccess) {
//      obj.list?.let {
//        if (it.isNotEmpty()) {
//          mBinding.messageV.visibility = View.VISIBLE
//          UnreadMsgUtils.show(mBinding.messageV, it.size)
//        } else {
//          mBinding.messageV.visibility = View.INVISIBLE
//        }
//      }
//    }
//  }

    @Subscribe
    fun onMatchTimeOutVM(obj: CallOutgoingViewModel.MatchTimeOut) {
        startOpenCall(this@MainActivity)
//    ChatLimitPop(this@MainActivity).show(mBinding.root, obj.message)
        val wallet = mViewModel.getMainResult()?.data?.userWallet
        val matching = mViewModel.getMainResult()?.data?.matching
        NotOpenTimePop(this@MainActivity).show(mBinding.root)
    }

    @Subscribe
    fun onMatchSuccessVM(obj: CallOutgoingViewModel.MatchSuccess) {
        mViewModel.netConsumeMatchingStore()
    }

    @Subscribe
    fun onTalkMatchingResult2(obj: TalkMatchingResult) {
        this.obj = obj
    }

    class ChatReportVM
    class ChatCloseVM

    override fun onMessageRecalled(p0: MutableList<EMMessage>?) {
    }

    override fun onMessageChanged(p0: EMMessage?, p1: Any?) {
    }

    override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {
        p0?.let {
            EventBus.getDefault().post(p0)
        }
    }

    override fun onMessageReceived(p0: MutableList<EMMessage>?) {
        p0?.forEach {
            //处理系统推送消息
            if (it.from == "1") {
                val map = hashMapOf<String, String>()
                map["from_avatar"] = "http://api.liaodede.net:8083/img/xjj.jpeg"
                map["from_nickname"] = "聊得得小姐姐"
                map["to_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
                map["to_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
                it.setAttribute("ext", Gson().toJson(map))
            }
            mViewModel.insertReceivedMessage(it)
        }
        // 收到消息后 刷新未读
        mViewModel.loadUnreadMessage()
    }

    override fun onMessageDelivered(p0: MutableList<EMMessage>?) {
    }

    override fun onMessageRead(p0: MutableList<EMMessage>?) {
    }

    /**
     * 监听通话状态(扬声器/耳机)
     */
    inner class HeadsetReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", 0)
                if (state == 0) {   // 耳机拔出
                    CallManager.getInstance().openSpeaker()
                } else if (state == 1) {    // 耳机插入
                    CallManager.getInstance().closeSpeaker()
                }
            }
        }
    }

    /**
     * 监听音量实体按键
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val a: Int
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                audioMngHelper?.subVoiceSystem()

                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                audioMngHelper?.addVoiceSystem()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_MUTE -> {
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (mForContent != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out,
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out)
            transaction.remove(mForContent!!)
            mForContent = null
            try {
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            if (mViewModel.isChating) {
                ExitChatPop(this@MainActivity).initData().show(mBinding.root)
                return
            } else {
//                moveTaskToBack(true)
                super.onBackPressed()
            }
        }
    }

    /**
     * 升级
     */
    @Subscribe
    fun onLevelUp(obj: NoticeLevelUpResult) {
        if (obj.level!! >= 0) {
            LevelUpgradeActivity.start(context = this, level = obj.level.toString(), title = obj.message!!.line1!!,
                    desc = obj.message!!.line2!!, value = obj.message!!.line3!!)
        }
    }

    /**
     * 领取礼物
     */
    @Subscribe
    fun onTaskReward(obj: NoticeTaskRewardResult) {
        when (obj.taskType) {
            1 -> {
                // 1.三日任务
                ReceiveTaskActivity.start(this, title = obj.rewardTitle!!, gold = obj.rewardCoin,
                        stone = obj.rewardStone, money = obj.rewardCash,
                        rewardId = obj.taskId!!, autoClose = false)
            }
            2 -> {
//                // 2.每日任务
//                TaskReceiveDialogActivity.start(this, obj.rewardTitle!!, obj.taskId!!)
                TaskTipPop(this@MainActivity).initData(obj.taskId!!,
                        obj.rewardTitle!!).enableTime().show(mBinding.root)
            }
            3 -> {
                // 3.成就任务
                ReceiveTaskActivity.start(this, title = obj.rewardTitle!!, gold = obj.rewardCoin,
                        stone = obj.rewardStone, money = obj.rewardCash,
                        rewardId = obj.taskId!!, autoClose = false)
            }
        }
    }

    @Subscribe
    fun onTaskReward2(obj: ThreeDaySummaryResult) {
        ReceiveThreeTaskActivity.start(this, title = "共完成" + obj.completeTask + "项，恭喜获得", gold = obj.rewardSum.rewardCoin,
                stone = obj.rewardSum.rewardStone, money = obj.rewardSum.rewardCash,
                rewardId = "", autoClose = false)
    }

    /**
     * 用户信息
     */
    @Subscribe
    fun onGetUserData(obj: MainViewModel.UserData) {
        if (obj.obj!!.userBaseInfo.nextLevelExperience != 0) {
            if (obj.obj.userBaseInfo.userExperience!! >= obj.obj.userBaseInfo?.nextLevelExperience!!) {
                mBinding.tvLevelTag.visibility = View.VISIBLE
            } else {
                mBinding.tvLevelTag.visibility = View.GONE
            }
            mBinding.horizontalProgressBar.max = obj.obj!!.userBaseInfo.nextLevelExperience
            mBinding.tvJingyan.text = "" + obj.obj!!.userBaseInfo.userExperience + "/" + obj.obj!!.userBaseInfo.nextLevelExperience
            mBinding.horizontalProgressBar.progress = obj.obj!!.userBaseInfo.userExperience
        }
    }

    @Subscribe
    fun TaskRewardData(obj: ThreeDayViewModel.TaskRewardData) {
        if (obj.success) {
//            Toast.makeText(this, "金币 ： " + obj.obj!!.rewardCoin + " 现金" + obj.obj!!.rewardCash + " 宝石" + obj.obj!!.rewardStone, Toast.LENGTH_SHORT).show()

            ToastConnectAwardActivity.start(this, "奖励领取成功", obj.obj!!.rewardCoin?.toDouble(),
                    obj.obj!!.rewardStone?.toDouble(), obj.obj!!.rewardCash?.toDouble())
        }
    }

    /**
     * 用户信息
     */
    @Subscribe
    fun onReceiveTask(obj: ReceiveTaskEvent) {
        mViewModel.getTaskReward(obj.taskId)
    }

    @Subscribe
    fun onClearUnreadMessage(obj: ChatViewModel.ClearUnreadMessage) {
        mViewModel.loadUnreadMessage()
    }

    @Subscribe
    fun onClearUnreadSystemMessage(obj: SystemMessageViewModel.ClearUnreadMessage) {
        mViewModel.loadUnreadMessage()
    }

    @Subscribe
    fun onSystemMessageResult(obj: MainViewModel.SystemMessageResult) {
        //SystemMessageResult创建EMMessage
        mDataRepository.createEMMessageBySystemMessage(obj).let {
            //插入数据库
            mViewModel.insertReceivedMessage(it)
        }

        //收到消息后 刷新首页未读消息
        mViewModel.loadUnreadMessage()
    }

    @Subscribe
    fun onOfflineSystemMessageMqttResult(obj: OfflineSystemMessageMqttResult) {
        var type = ""
        var msg = ""
        var activityName = ""
        var param_value = ""
        var ctime = ""
        //从data中获取参数type/msg/activityName/param_value/ctime
        if (obj.type != "" && obj.msg != "" && obj.ctime != "") {
            type = obj.type!!
            msg = obj.msg!!
            ctime = obj.ctime!!
            if (obj.router?.activityName != "") {
                activityName = obj.router?.activityName!!
                obj.router?.param?.forEach {
                    if (it.value != "") {
                        param_value = it.value!!
                    }
                }
            }
        }
        EventBus.getDefault().post(MainViewModel.SystemMessageResult(type, msg, activityName, param_value, ctime))
    }

    /**
     * 广场-"首次发帖"任务
     * 任务完成下发奖励弹窗
     */
    @Subscribe
    fun onSquareFirstCommentRewardResult(obj: SquareFirstCommentRewardResult) {
        NewbieTaskActivity.start(this, title = obj.message!!.line1!!, gold = obj.rewardCoin!!.toDouble(),
                stone = obj.rewardStone!!.toDouble(), money = obj.rewardCash!!.toDouble(),
                rewardId = obj.rewardId!!)
    }

    private val mHandler = MyHandler(this)

    private inner class MyHandler(holder: MainActivity) : Handler() {

        private val mHolder: WeakReference<MainActivity> = WeakReference(holder)

        override fun handleMessage(message: Message) {
            val holder = mHolder.get()
            if (holder != null && message.what == MSG_LOAD_UNREAD_MESSAGE) {
                mViewModel.loadUnreadMessage()
                mHandler.sendEmptyMessageDelayed(MSG_LOAD_UNREAD_MESSAGE, 10 * 1000)
            } else if (message.what == MSG_TASK) {
                if (isForeground) {
                    mViewModel.getUserData()
                    mViewModel.getApplyNum()
                }
                mHandler.sendEmptyMessageDelayed(MSG_TASK, 30 * 1000)
            }

        }
    }
}
