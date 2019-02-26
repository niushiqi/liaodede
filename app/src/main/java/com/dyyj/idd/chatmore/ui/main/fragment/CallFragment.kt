package com.dyyj.idd.chatmore.ui.main.fragment

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.lottie.LottieAnimationView
import com.app.hubert.guide.NewbieGuide
import com.app.hubert.guide.core.Controller
import com.app.hubert.guide.model.GuidePage
import com.app.hubert.guide.model.HighLight
import com.app.hubert.guide.model.HighlightOptions
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentCallBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.easemob.CallManager
import com.dyyj.idd.chatmore.model.easemob.EasemobManager
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.mqtt.result.*
import com.dyyj.idd.chatmore.model.network.result.ReportReasonResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.adapter.GameHomeAdapter
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.user.fragment.SendGiftFragmentInChat
import com.dyyj.idd.chatmore.ui.user.fragment.SendGiftFragmentInVoice
import com.dyyj.idd.chatmore.ui.wallet.fragment.BuyProudPeasFragmentInVoice
import com.dyyj.idd.chatmore.utils.*
import com.dyyj.idd.chatmore.utils.DeviceUtils.getScreenWidth
import com.dyyj.idd.chatmore.utils.DisplayUtils.dp2px
import com.dyyj.idd.chatmore.viewmodel.*
import com.dyyj.idd.chatmore.weiget.GameWidget
import com.dyyj.idd.chatmore.weiget.GameWidget2
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import com.dyyj.idd.chatmore.weiget.pop.ChatTipPop
import com.example.zhouwei.library.CustomPopWindow
import com.google.android.flexbox.FlexboxLayout
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.chat.EMCallStateChangeListener
import com.hyphenate.chat.EMClient
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import me.jessyan.autosize.utils.AutoSizeUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/19
 * desc   : 通话中
 */
@RuntimePermissions
class CallFragment : BaseFragment<FragmentCallBinding, CallViewModel>(), MyFrameAnimation.OnFrameAnimationListener {

    var handler: Handler = Handler()

    var switchGiftFlag: Boolean = true

    var size1 = 0
    var size2 = 0
    private var size3 = 0
    private var size4 = 0
    private var size5 = 0   //广场未读
    private var mPopWindow: CustomPopWindow? = null
    var mForContent: Fragment? = null

    override fun onStartFrameAnimation() {
        mBinding.moshiIv.visibility = View.VISIBLE
//    Toast.makeText(ChatApp.niceChatContext(), "动画开始ok", Toast.LENGTH_SHORT).show()
    }

    override fun onEnd() {

//    Toast.makeText(ChatApp.niceChatContext(), "动画结束ok", Toast.LENGTH_SHORT).show()
        //魔石动画播放完成,打开匹配界面
        mBinding.moshiIv.visibility = View.GONE
        EasemobManager.isStartMatch = false
        mViewModel.isSwitch = true
//    startSwitchTime()
       mViewModel.netChangePerson()
//    mViewModel.endCall()
//    MainActivity.startOutgoing(mActivity)
    }

    @Subscribe
    fun onSwitchSendVM(obj: CallViewModel.SwitchSendVM) {
        mViewModel.endCall()
        startSwitchTime()
    }

    @Subscribe
    fun onSwitchVM(obj: SwitchPeopleResult) {
        mViewModel.netChangePersonReturn()
        if (!mViewModel.isSwitch) {
            mViewModel.isSwitch = true
            mViewModel.endCall()
        }
    }

    private var mResume: Boolean = false
    private var mCanClick: Boolean = false
    private var mReport: Boolean = false

    private val colorList = arrayListOf(Color.WHITE, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY,
            Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.RED,
            Color.WHITE, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY,
            Color.GREEN)
    private val sizeList = arrayListOf(10, 11, 12, 13, 14, 15)

    private val damaskContext = DanmakuContext.create()

    companion object {
        var mInstance: CallFragment? = null
        const val TAG = "SIP"
        var unknownTalkId: String = ""
        var talkUserId: String = ""
        fun instance(): CallFragment {
            if (mInstance == null) {
                return CallFragment()
            }
            return mInstance!!
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_call
    }

    override fun onViewModel(): CallViewModel {
        return CallViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateEvenBus(this)
//    DanmuUtils.init(mActivity, damaskContext, mBinding.sv2Danmaku, cacehStuffer, null)
        if (mViewModel.mTalkId == null) {
            mBinding.rlSwitch.visibility = View.GONE
            mBinding.rlSwitch2.visibility = View.VISIBLE
        }else{
            mBinding.rlSwitch.visibility = View.VISIBLE
            mBinding.rlSwitch2.visibility = View.GONE
        }
        lazyLoad()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置通话界面属性，保持屏幕常亮，关闭输入法，以及解锁
        mActivity.window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    override fun lazyLoad() {
        super.lazyLoad()
        mInstance = this
        initListener()
        initView()
        //netUserInfo()
        mViewModel.netTalkUserId()

//    Toast.makeText(ChatApp.niceChatContext(), "3:" + mFromUserId + "!=" + mToUserId, Toast.LENGTH_LONG).show()
        mViewModel.netSigns(
                if (mDataRepository.getUserid() == (activity as MainActivity).mToUserId) (activity as MainActivity).mFromUserId!! else (activity as MainActivity).mToUserId!!)
        mViewModel.netCheckRelation(
                toUserId = if (mDataRepository.getUserid() == (activity as MainActivity).mToUserId) (activity as MainActivity).mFromUserId!! else (activity as MainActivity).mToUserId!!)
        if (!CallManager.getInstance().audioManager.isWiredHeadsetOn) {
            CallManager.getInstance().openSpeaker()
        }

        if (PreferenceUtil.getBoolean("firstCall", true)) {
            PreferenceUtil.commitBoolean("firstCall", false)
            ChatTipPop(mActivity).showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
        }
    }

    private fun initGame() {
//    val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
//            ContextCompat.getColor(activity!!, android.R.color.transparent)).sizeResId(
//            R.dimen.item_decoration_10dp).build()
        mViewModel.getAdapter().initData(arrayListOf(1, 2, 3, 4))
//    mBinding.rvGame.addItemDecoration(decoration)
        mBinding.rvGame.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        var linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rvGame.layoutManager = linearLayoutManager
        mBinding.rvGame.adapter = mViewModel.getAdapter()

//    mBinding.txtMoney.text = (mActivity as MainActivity).mViewModel.getMainResult()?.data?.userWallet?.userCash
//    mBinding.txtGold.text = (mActivity as MainActivity).mViewModel.getMainResult()?.data?.userWallet?.userCoin
//    mBinding.txtMoshi.text = (mActivity as MainActivity).mViewModel.getMainResult()?.data?.userWallet?.userStone
        (mActivity as MainActivity).mBinding.model = (mActivity as MainActivity).mViewModel.getMainResult()?.data

        mBinding.rvGame.visibility = View.VISIBLE
        mBinding.csGame2.visibility = View.INVISIBLE
        mBinding.csGame2.setStatue(GameWidget2.STATUE_READY)
        mBinding.csGame.visibility = View.INVISIBLE
        mBinding.csGame.setStatue(GameWidget.STATUE_READY)

        mBinding.csGame.findViewById<ImageView>(R.id.imageView48).setOnClickListener {
            mBinding.rvGame.visibility = View.VISIBLE
            mBinding.csGame.visibility = View.GONE
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_gamelist", (activity as MainActivity).mFromUserId!!))
        }
        mBinding.csGame.setToUserId(
                if (mDataRepository.getUserid() == (activity as MainActivity).mFromUserId) (activity as MainActivity).mToUserId!! else (activity as MainActivity).mFromUserId!!)

        mBinding.csGame2.findViewById<ImageView>(R.id.imageView48).setOnClickListener {
            mBinding.rvGame.visibility = View.VISIBLE
            mBinding.csGame2.visibility = View.GONE
        }
        mBinding.csGame2.setToUserId(if (mDataRepository.getUserid() == (activity as MainActivity).mFromUserId) (activity as MainActivity).mToUserId!! else (activity as MainActivity).mFromUserId!!)
        mBinding.csGame2.setListener {
            mViewModel.netMainInfo()
        }

        //引导页
        if (!mViewModel.getGuideStatus()) {
            guide()
            PreferenceUtil.commitBoolean("firstCall", false)
            mViewModel.saveGuideStatus()
        }

        mBinding.txtMessageTop.setOnClickListener {
            (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance(),true)
        }
    }

    @Subscribe
    fun onRefreshWalletVM(obj: CallViewModel.RefreshWallet) {
//    mBinding.txtMoney.text = obj.obj?.userWallet?.userCash
//    mBinding.txtGold.text = obj.obj?.userWallet?.userCoin
//    mBinding.txtMoshi.text = obj.obj?.userWallet?.userStone
        (mActivity as MainActivity).mBinding.model = obj.obj
    }

    var controller: Controller? = null
    private fun guide() {
        val options = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
            val paint = Paint()
            paint.color = Color.parseColor("#FFEC1C")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
            canvas.drawRect(rectF, paint)
            paint.pathEffect = DashPathEffect(floatArrayOf(24f, 20f), 0f)
            canvas.drawRoundRect(rectF, AutoSizeUtils.dp2px(activity, 10f).toFloat(), AutoSizeUtils.dp2px(activity, 10f).toFloat(), paint)
        }.build()

        val page = GuidePage.newInstance().addHighLightWithOptions(mBinding.rvGame,
                HighLight.Shape.ROUND_RECTANGLE,
                options).setLayoutRes(
                R.layout.layout_guide_game_1).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))

        controller = NewbieGuide.with(this).setLabel("guide").alwaysShow(true).addGuidePage(page).show()
    }

    var controller1: Controller? = null
    private fun guide1() {
        controller?.remove()
        val options2 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
            val paint = Paint()
            paint.color = Color.parseColor("#FFEC1C")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
            canvas.drawRect(rectF, paint)
        }.setOnClickListener {
            controller1?.remove()
        }.build()

        val page2 = GuidePage.newInstance().addHighLightWithOptions(
                mBinding.csGame.findViewById<View>(R.id.guide_2), HighLight.Shape.ROUND_RECTANGLE,
                options2).setLayoutRes(R.layout.layout_guide_game2).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))

        controller1 = NewbieGuide.with(this)//传入fragment
                .setLabel("guidegame1")//设置引导层标示，必传！否则报错
                .alwaysShow(true).addGuidePage(page2).show()//直接显示引导层
    }

    var controller2: Controller? = null
    private fun guide2() {
        controller2?.remove()
        val options2 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
            val paint = Paint()
            paint.color = Color.parseColor("#FFEC1C")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
            canvas.drawRect(rectF, paint)
        }.setOnClickListener {
            controller2?.remove()
        }.build()

        val page2 = GuidePage.newInstance().addHighLightWithOptions(
                mBinding.csGame2.findViewById<View>(R.id.guide_3), HighLight.Shape.ROUND_RECTANGLE,
                options2).setLayoutRes(R.layout.layout_guide_game3).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))

        controller2 = NewbieGuide.with(this)//传入fragment
                .setLabel("guidegame2")//设置引导层标示，必传！否则报错
                .alwaysShow(true).addGuidePage(page2).show()//直接显示引导层
    }

    private fun closeGuideControllers() {
        //fragment关闭时，引导页未关闭，就关闭
        if (controller != null) {
            controller!!.remove()
        }
        if (controller1 != null) {
            controller1!!.remove()
        }
        if (controller2 != null) {
            controller2!!.remove()
        }
    }

    @Subscribe
    fun onStartGameVM(obj: GameHomeAdapter.StartGameVM) {
        if (OpenCallFragment.isGameClose) {
            mBinding.rvGame.visibility = View.INVISIBLE
            mBinding.csGame.visibility = View.INVISIBLE
            mBinding.csGame2.visibility = View.INVISIBLE
        } else {
            if (obj.type == 0) {
                mBinding.rvGame.visibility = View.INVISIBLE
                mBinding.csGame.visibility = View.VISIBLE
                mBinding.csGame2.visibility = View.INVISIBLE
                mBinding.csGame.setStatue(GameWidget.STATUE_READY)
                //引导页
                if (!mViewModel.getGuideGame1Status()) {
                    guide1()
                    mViewModel.saveGuideGame1Status()
                }
            } else if (obj.type == 1) {
                mBinding.rvGame.visibility = View.INVISIBLE
                mBinding.csGame.visibility = View.INVISIBLE
                mBinding.csGame2.visibility = View.VISIBLE
                mBinding.csGame2.setStatue(GameWidget2.STATUE_READY)
                //引导页
                /*if (!mViewModel.getGuideGame2Status()) {
                    guide2()
                    mViewModel.saveGuideGame2Status()
                }*/
            }
        }
    }

    /**
     * 骰子请求
     */
    @Subscribe
    fun onGameRequestVM(obj: GameRequestResult) {
        if (OpenCallFragment.isGameClose) {
            mBinding.rvGame.visibility = View.INVISIBLE
            mBinding.csGame.visibility = View.INVISIBLE
        } else {
            mBinding.rvGame.visibility = View.INVISIBLE
            mBinding.csGame2.visibility = View.INVISIBLE
            mBinding.csGame.visibility = View.VISIBLE
            mBinding.csGame.setMyBig(obj.expectRs == 2)
            mBinding.csGame.setGameId(obj.gameId.toString())
            mBinding.csGame.startWaitingSelf()
            //引导页
            if (!mViewModel.getGuideGame1Status()) {
                guide1()
                mViewModel.saveGuideGame1Status()
            }
        }
    }

    /**
     * 骰子拒绝
     */
    @Subscribe
    fun onGameRefuseVM(obj: GameRefuseResult) {
        mBinding.csGame.setStatue(GameWidget.STATUE_READY)
    }

    /**
     * 骰子结果
     */
    @Subscribe
    fun onGameResultVM(obj: GameResult) {
//        mBinding.csGame.isBigWin = (obj.isBig == "big") and (obj.isWin == "win")
        mBinding.csGame.setGameResult(obj)
        mBinding.csGame.setStatue(GameWidget.STATUE_PLAYING)
        mBinding.csGame.setListener {
            mViewModel.netMainInfo()
        }
    }

    /**
     * 手速请求
     */
    @Subscribe
    fun onGame2RequestVM(obj: Game2RequestResult) {
        if (OpenCallFragment.isGameClose) {
            mBinding.rvGame.visibility = View.INVISIBLE
            mBinding.csGame2.visibility = View.INVISIBLE
        } else {
            mBinding.rvGame.visibility = View.INVISIBLE
            mBinding.csGame.visibility = View.INVISIBLE
            mBinding.csGame2.visibility = View.VISIBLE
            mBinding.csGame2.setGameId(obj.gameId.toString())
            mBinding.csGame2.startWaitingSelf()
            //引导页
            /*if (!mViewModel.getGuideGame2Status()) {
                guide2()
                mViewModel.saveGuideGame2Status()
            }*/
        }
    }

    /**
     * 手速同意返回
     */
    @Subscribe
    fun onGame2ResponseVM(obj: Game2ResponseResult) {
        mBinding.csGame2.setIsGame2Sender(obj.useraId, obj.userbId)
        mBinding.csGame2.playGame()
        //引导页
        if (!mViewModel.getGuideGame2Status()) {
            guide2()
            mViewModel.saveGuideGame2Status()
        }
    }

    /**
     * 手速拒绝
     */
    @Subscribe
    fun onGame2RefuseVM(obj: Game2RejectResult) {
        mBinding.csGame2.setStatue(GameWidget2.STATUE_READY)
    }

    /**
     * 手速结果
     */
    @Subscribe
    fun onGame2ResultVM(obj: Game2Result) {
        controller2?.remove()
        mBinding.csGame2.setGameResult(obj)
//        mBinding.csGame2.setListener {
//            mViewModel.netMainInfo()
//        }
    }

    @Subscribe
    fun onRefreshWalletVM(obj: CallViewModel.RefreshWalletCallFragment) {
        mViewModel.netMainInfo()
    }

    /**
     * 加载数据
     */
    @Subscribe
    fun netUserInfo(obj: CallViewModel.TalkUserId) {
        val userId = mDataRepository.getUserid()
        /*
        if ((activity as MainActivity).mFromUserId != null && (activity as MainActivity).mToUserId != null) {
            mViewModel.netGetMatchingUserBaseInfo(
                    if (userId == (activity as MainActivity).mFromUserId) (activity as MainActivity).mToUserId!! else (activity as MainActivity).mFromUserId!!)
        }*/
        mViewModel.netGetMatchingUserBaseInfo(obj.userId)
    }

    /**
     * 亲密度计算
     */
    private fun initIntimacyProgress(time: Long) {

        if (time > 180) return

        val progress = mBinding.root.findViewById<LottieAnimationView>(R.id.loading_lav)
        val box = mBinding.root.findViewById<ImageView>(R.id.box_iv)
        val df = DecimalFormat("0.00")
        val min = df.format(100.00 / 180.00 * time / 100 - 0.01f)
        val max = df.format(100.00 / 180.00 * time / 100 + 0.01f)
        Timber.tag("progress").i("min=$min  main=$max")
        progress.setMinAndMaxProgress(min.toFloat(), max.toFloat())

        when {
            time < 30 -> box.setImageResource(R.drawable.ic_intimacy_box1_normal)
            time < 90 -> box.setImageResource(R.drawable.ic_intimacy_box2_normal)
            time < 180 -> box.setImageResource(R.drawable.ic_intimacy_box3_normal)
            else -> box.setImageResource(R.drawable.ic_intimacy_box4_normal)
        }

        if (time == 30.toLong()) {
            ShowRule()
        }
    }

    /**
     * 提示用户添加好友的动画Toast
     */

    fun remindUserToAddFriend() {
        //获取内容
        val contentView = View.inflate(ChatApp.niceChatContext(), R.layout.remind_user_add_friend_pop_layout, null)
        val mTV = contentView.findViewById<TextView>(R.id.add_textView)
        mTV.text = "加个好友吧关闭聊天就找不到了"
        val popupWindow = PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.isTouchable = true
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.animationStyle = R.style.pop_animation//pop弹入弹出的样式
        popupWindow.showAtLocation(mBinding.ivFriend, Gravity.BOTTOM, 0, 100)
        Handler().postDelayed({ popupWindow.dismiss() }, 5000)
    }
    //定义提示规则
    private fun ShowRule() {
        val sp = mActivity.getSharedPreferences("UserUseTimeOfDate", MODE_PRIVATE)//用户使用的日期
        val sp2 = mActivity.getSharedPreferences("UserUseTime", MODE_PRIVATE)//用户使用次数

        val userUseTime = sp.getStringSet("Date_of_user_used", LinkedHashSet<String>())//获取用户使用过的日期(set)
        var time = sp2.getInt("Number_of_times_per_day", 0)//获取每天用户使用次数(int)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val format = sdf.format(Calendar.getInstance().time)
        if (userUseTime!!.size <= 3) {//判断用户使用日期是否大于3天，大于则不提示，小于则添加当前用户使用日期
            val lhs = LinkedHashSet<String>()
            lhs.addAll(userUseTime)
            if (lhs.add(format)) {
                sp2.edit().putInt("Number_of_times_per_day", 0).apply()
            }
            sp.edit().putStringSet("Date_of_user_used", lhs).apply()
            if (time < 3) {//判断用户在一天内使用的次数，大于3则不提示，小于+1
                remindUserToAddFriend()
                sp2.edit().putInt("Number_of_times_per_day", ++time).apply()
            }
        }
    }

    /**
     * 通话开始
     */
    private fun netWork() {
//        if (!TextUtils.isEmpty(mFromUserId) && !TextUtils.isEmpty(mToUserId)) {
//
//            //备选池主叫方
//            if (mToUserId == mDataRepository.getUserid() && unknownTalkId.isNotEmpty()) {
//                mViewModel.netReportTalkingStatus(talkId = unknownTalkId, fromUserId = mFromUserId!!,
//                        toUserId = mToUserId!!, reportType = "1",
//                        reportValue = getMainActivity()?.mViewModel?.getStartTime()?.toString()!!)
//                //备选池被叫方
//            } else {
//                mViewModel.netReportTalkingStatus(
//                        talkId = getMainActivity()?.obj?.talkId?.toString()
//                                ?: "0", fromUserId = mFromUserId!!,
//                        toUserId = mToUserId!!, reportType = "1",
//                        reportValue = getMainActivity()?.mViewModel?.getStartTime()?.toString()!!)
//            }
//
//            if (CallOutgoingFragment.talkId == null) {
//
//            } else {
//                mViewModel.mTalkId = CallOutgoingFragment.talkId
//            }
//
//            mViewModel.timerTalkingStatus(fromUserId = mFromUserId!!, toUserId = mToUserId!!,
//                    startTime = getMainActivity()?.mViewModel?.getStartTime()!!)
//        }

        if (!TextUtils.isEmpty((activity as MainActivity).mFromUserId) && !TextUtils.isEmpty((activity as MainActivity).mToUserId)) {
            if (unknownTalkId.isEmpty()) {
                mViewModel.netReportTalkingStatus(
                        talkId = getMainActivity()?.obj?.talkId?.toString()
                                ?: "0", fromUserId = (activity as MainActivity).mFromUserId!!,
                        toUserId = (activity as MainActivity).mToUserId!!, reportType = "1",
                        reportValue = getMainActivity()?.mViewModel?.getStartTime()?.toString()!!)
            } else {
                mViewModel.netReportTalkingStatus(
                        talkId = unknownTalkId, fromUserId = (activity as MainActivity).mFromUserId!!,
                        toUserId = (activity as MainActivity).mToUserId!!, reportType = "1",
                        reportValue = getMainActivity()?.mViewModel?.getStartTime()?.toString()!!)
            }

            if (CallOutgoingFragment.talkId == null) {

            } else {
                mViewModel.mTalkId = CallOutgoingFragment.talkId
            }

            mViewModel.timerTalkingStatus(fromUserId = (activity as MainActivity).mFromUserId!!, toUserId = (activity as MainActivity).mToUserId!!,
                    startTime = getMainActivity()?.mViewModel?.getStartTime()!!)
        }

    }

    fun setToUserId(toUserid: String): CallFragment {
        (activity as MainActivity).mToUserId = toUserid
        return this
    }

    @Subscribe
    fun OnRelationVM(obj: CallViewModel.RelationVM) {
        if (obj.success) {
            if (obj.friend) {
                mBinding.ivFriend.setImageResource(R.drawable.ic_friend_yes)
                mBinding.rlFriend.isEnabled = false
                mViewModel.timerFriendExperience(
                        if (mDataRepository.getUserid() == (activity as MainActivity).mToUserId) (activity as MainActivity).mFromUserId!! else (activity as MainActivity).mToUserId!!)
            } else {
                //Toast.makeText(ChatApp.mInstance?.applicationContext, "不是好友", Toast.LENGTH_SHORT).show()
                mBinding.rlFriend.isEnabled = false
//                mBinding.layoutProgressBox1?.title_iv?.visibility = View.INVISIBLE
//                mBinding.layoutProgressBox1?.level_tv?.visibility = View.INVISIBLE
//                mBinding.layoutProgressBox1?.schedule_tv?.visibility = View.INVISIBLE
            }
        } else {
//      Toast.makeText(ChatApp.mInstance?.applicationContext, "关系获取失败", Toast.LENGTH_SHORT).show()
            mBinding.rlFriend.isEnabled = false
            mBinding.ivFriend.setImageResource(R.drawable.ic_friend_no)
        }
        netWork()
    }

    @Subscribe
    fun onCancelMatchingVM(obj: CallViewModel.CallMatchFailedVM) {
        if (obj.isSuccess) {
//      decline()
            MainActivity.startOpenCall(mActivity)
        }
    }

    /**
     * 初始化view
     */
    private fun initView() {
        if (OpenCallFragment.isGameClose) {
            mBinding.rvGame.visibility = View.INVISIBLE
            mBinding.csGame.visibility = View.INVISIBLE
            mBinding.csGame2.visibility = View.INVISIBLE
        } else {
            initGame()
        }
        mBinding.callTimer.format = "%s"
        mBinding.callTimer.base = if (mResume) ChatApp.mInstance?.getOnlineTime()
                ?: SystemClock.elapsedRealtime() else SystemClock.elapsedRealtime()
        mBinding.callTimer.start()
        mBinding.callTimer.onChronometerTickListener = Chronometer.OnChronometerTickListener {

            Timber.tag("progress").i("base = ${(SystemClock.elapsedRealtime() - it.base) / 1000}")
            initIntimacyProgress((SystemClock.elapsedRealtime() - it.base) / 1000)
            if (this@CallFragment.isVisible and !mCanClick and ((SystemClock.elapsedRealtime() - it.base >= 60 * 1000))) {
                mCanClick = true
                mBinding.ivCamera.setImageResource(R.drawable.ic_camera_yes)
                if (!mReport) {
                    mReport = true
                    mViewModel.timerReport(60)
                }
            }
            if (!mViewModel.isFriend and this@CallFragment.isVisible and !mBinding.rlFriend.isEnabled and ((SystemClock.elapsedRealtime() - it.base >= 30 * 1000))) {
                mBinding.rlFriend.isEnabled = true
                mBinding.ivFriend.setImageResource(R.drawable.ic_friend_yes_add)
                mBinding.ivBottomAdd.visibility = View.VISIBLE
            }
//      initNear(SystemClock.elapsedRealtime() - it.base)
        }
        val model = getMainActivity()?.mViewModel?.getStartMatchingResult()
        mBinding.model = model
        //Log.e("AvatarCallF","Avatar="+ model?.matchingUserAvatar)
        DataBindingUtils.loadAvatar(mBinding.avatarIv, model?.matchingUserAvatar)
        mBinding.csGame.setToUserInfo(model)
        mBinding.csGame2.setToUserInfo(model)
    }


    /**
     * 初始化点击事件
     */
    private fun initListener() {

//    mBinding.imageView19.setOnClickListener {
//      //      decline()
//      mViewModel.endCall()
//    }

        mBinding.tipRl.setOnClickListener { it.visibility = View.GONE }


        mBinding.layoutProgressBox1?.findViewById<TextView>(R.id.tv_report)?.setOnClickListener {
            mViewModel.netReportReason()//举报
        }

        mBinding.layoutProgressBox1?.findViewById<TextView>(R.id.tv_close)?.setOnClickListener {
            if (mViewModel.mTalkId != null)//关闭此聊天过程
                showDialogChatHow(CLOSE)
            else
                mViewModel.endCall()
        }

        mBinding.rlSwitch.setOnClickListener {
            //      Toast.makeText(ChatApp.niceChatContext(), "点击ok", Toast.LENGTH_SHORT).show()
//      if (mDataRepository.getUserWalletEntity()?.userStone?.toInt()?:0 < 3) {
//        Toast.makeText(ChatApp.mInstance?.applicationContext, "魔石不足", Toast.LENGTH_SHORT).show()
//      } else {
////        showProgressDialog()
////        mViewModel.netConsumeMatchingStore()
            //Log.i("mytag","if (mViewModel.mTalkId != null){++++++++++++++++++++++")
            if (mViewModel.mTalkId != null){
                //Log.i("mytag","if (mViewModel.mTalkId != null){进来了_______________")
                //如果是从ChatActivity中开启一对一通话,就不弹出,because一对一通话会跑到这里执行通过过程
                //Log.i("mytag","mViewModel.mTalkId"+mViewModel.mTalkId)
                showDialogChatHow(CHANGE_PERSON)
            } else{
                MainActivity.startOutgoing(mActivity)
                AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this@CallFragment)
            }
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_change",(activity as MainActivity).mFromUserId!!))
        }

        mBinding.rlFriend.setOnClickListener {
            showProgressDialog()
            /*var talkId: String?
            if (mViewModel.mTalkId == null) {
                talkId = mDataRepository.getTalkId()
            } else {
                talkId = mViewModel.mTalkId
            }*/
            //加好友要考虑语音匹配和1对1通话
            mDataRepository.addFriendAction(mDataRepository.getAcceptedUserId()!!,
                    mDataRepository.getTalkId()!!, "").subscribe({
                closeProgressDialog()
                if (it.errorCode == 200) {
                    Toast.makeText(ChatApp.mInstance?.applicationContext,
                     "申请已经发送", Toast.LENGTH_SHORT).show()
                    mBinding.ivFriend.setImageResource(
                            R.drawable.ic_friend_yes)
                    mBinding.ivBottomAdd.visibility = View.GONE
                    mBinding.rlFriend.isEnabled = false
                } else {
                    Toast.makeText(
                            ChatApp.mInstance?.applicationContext,
                            it.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }, {
                closeProgressDialog()
                Toast.makeText(
                        ChatApp.mInstance?.applicationContext, "添加失败",
                        Toast.LENGTH_SHORT).show()
            })
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_addfriend",(activity as MainActivity).mFromUserId!!))
        }

        /**
         * 切换视频
         */
        mBinding.rlCamera.setOnClickListener {
            if (mCanClick && !mViewModel.isSendVideo) {
//        mViewModel.resumeVideoTransfer()
//        ChatApp.getInstance().setOnlineTime(mBinding.callTimer.base)
//        MainActivity.startVideoOut(mActivity, mFromUserId!!, mToUserId!!, mViewModel.getTalkId()!!)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    switchVideoWithPermissionCheck()
                } else {
                    switchVideo()
                }
            } else if (!mCanClick) {
                Toast.makeText(ChatApp.mInstance?.applicationContext, "沟通时长不足", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(ChatApp.mInstance?.applicationContext, "对方已经拒绝了你的申请",
                        Toast.LENGTH_SHORT).show()
            }
        }

        EMClient.getInstance().callManager().addCallStateChangeListener(listener)

        mBinding.rlMore.setOnClickListener {//1.7版本去掉 “更多” 弹窗，换成送礼物
            /*val pop = ActionPop(activity)
            pop.isOutsideTouchable = true
            pop.isFocusable = true
            pop.show(mBinding.root)*/
            /*mBinding.replaceFrg.visibility = View.VISIBLE
            addFragment(SendGiftFragmentInVoice.instance())*/
            if (switchGiftFlag) {
                switchGiftFlag = false
                reSetAboutGiftStatus(true)
                addFuncFragment(SendGiftFragmentInVoice.instance(getMainActivity()?.mViewModel?.getStartMatchingResult()?.matchingUserId))
            }else{
                switchGiftFlag = true
                reSetAboutGiftStatus(false)
                removeFragment(SendGiftFragmentInVoice.instance(null))
            }

        }
    }

    /**
     * 当礼物打开时,
     * 1.隐藏其他功能模块
     * 2.添加礼物碎片
     * 3...
     */
    private fun reSetAboutGiftStatus(isShowGift: Boolean) {
        if (isShowGift) {//当显示礼物|充值的时候
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBinding.replaceFrg.z = 1f
            }
            mBinding.replaceFrg.visibility = View.VISIBLE
        }else{
            mBinding.replaceFrg.visibility = View.GONE
        }
    }

    /**
     * 替换功能模块的Fragment
     */
    fun addFuncFragment(fragment: Fragment) {
        if (mForContent != fragment) {
            mForContent?.onDestroy()
            mForContent = fragment
            val transaction = mActivity.supportFragmentManager.beginTransaction()
            /*transaction.setCustomAnimations(
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out,
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out)*/
            transaction.replace(R.id.replace_frg, fragment, fragment::class.java.simpleName)
            try {
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 关闭送礼物
     */
    @Subscribe
    fun onCloseGiftFrg(obj: SendGiftFragmentInVoice.CloseGiftFrg) {
        reSetAboutGiftStatus(false)
        removeFragment(SendGiftFragmentInChat.instance(null))
    }

    /**
     * 展示购买得豆页面
     */
    @Subscribe
    fun onShowBuyPeaFrg(obj: SendGiftFragmentInVoice.showBuyPeaFrg) {
        reSetAboutGiftStatus(true)
        addFuncFragment(BuyProudPeasFragmentInVoice.instance())
    }

    /**
     * 关闭购买得豆页面
     */
    @Subscribe
    fun onCloseBuyPeaFrg(obj: BuyProudPeasFragmentInVoice.CloseBuyPeaFrg) {
        reSetAboutGiftStatus(true)
        addFuncFragment(SendGiftFragmentInVoice.instance((activity as MainActivity).mToUserId!!))
    }

    /**
     * 对方收到送礼物推送
     */
    @Subscribe
    fun onReceivedGiftPush(obj: SendGiftOnChatResult) {
        if (mDataRepository.getUserid() != null) {
            if (mDataRepository.getUserid().equals(obj.receiver_id)) {// Pop启动
                showPopNotificationsEnable(obj)
            }
        }else{
            mActivity.niceToast("账号状态存在异常，请重新登录")
        }
    }

    /**
     * 自己发的礼物
     */
    @Subscribe
    fun onReceivedGiftPush(obj: SendGiftOnChatResultOwn) {
        // Pop启动
        showPopNotificationsEnable(obj)
    }

    fun showPopNotificationsEnable(obj: SendGiftOnChatResultOwn) {//平移显示动画
        /*val contentView = View.inflate(mActivity, R.layout.send_gift_pop, null)
        setView(contentView,obj)
        createReceivedGiftPopWindow(contentView)*/
        val toastRoot = View.inflate(mActivity, R.layout.send_gift_pop, null)
        setView(toastRoot,obj)
        var mtoast = Toast(mActivity)
        mtoast.setView(toastRoot)
        mtoast.setDuration(Toast.LENGTH_SHORT)
        mtoast.setGravity(Gravity.LEFT or Gravity.CENTER_VERTICAL, 20, DisplayUtils.dp2px(mActivity, -50f))
        mtoast.show()
    }

    fun showPopNotificationsEnable(obj: SendGiftOnChatResult) {//平移显示动画
        /*val contentView = View.inflate(mActivity, R.layout.send_gift_pop, null)
        setView(contentView,obj)
        createReceivedGiftPopWindow(contentView)*/
        val toastRoot = View.inflate(mActivity, R.layout.send_gift_pop, null)
        setView(toastRoot,obj)
        var mtoast = Toast(mActivity)
        mtoast.setView(toastRoot)
        mtoast.setDuration(Toast.LENGTH_SHORT)
        mtoast.setGravity(Gravity.LEFT or Gravity.CENTER_VERTICAL, 20, DisplayUtils.dp2px(mActivity, -50f))
        mtoast.show()
    }

    fun setView(contentView: View,obj: SendGiftOnChatResult) {
            contentView.findViewById<TextView>(R.id.received_name).text = "${obj.sender_name}送你"
            contentView.findViewById<TextView>(R.id.received_gift_name).text = "${obj.gift_name} ×1"
            val receivedGiftImg = contentView.findViewById<ImageView>(R.id.received_gift_img)
            Glide.with(this).load(obj.gift_icon).asBitmap().crossFade().placeholder(
                    R.drawable.ic_gifts_placeholder).into(receivedGiftImg)
            /*{//如果赠送者是自己
                contentView.findViewById<TextView>(R.id.received_name).text = "赠送给${obj.receiver_name}"
                contentView.findViewById<TextView>(R.id.received_gift_name).text = "${obj.gift_name} ×1"
                val receivedGiftImg = contentView.findViewById<ImageView>(R.id.received_gift_img)
                Glide.with(this).load(obj.gift_icon).asBitmap().crossFade().placeholder(
                        R.drawable.ic_gifts_placeholder).into(receivedGiftImg)
            }else*/
    }

    fun setView(contentView: View,obj: SendGiftOnChatResultOwn) {
        if (mDataRepository.getUserid() != null) {
            contentView.findViewById<TextView>(R.id.received_name).text = "赠送给${getMainActivity()?.mViewModel?.getStartMatchingResult()?.matchingUserNickname}"
            contentView.findViewById<TextView>(R.id.received_gift_name).text = "${obj.gift_name} ×1"
            val receivedGiftImg = contentView.findViewById<ImageView>(R.id.received_gift_img)
            Glide.with(this).load(obj.gift_icon).asBitmap().crossFade().placeholder(
                    R.drawable.ic_gifts_placeholder).into(receivedGiftImg)
        }else{
            mActivity.niceToast("账号状态存在异常，请重新登录")
        }
    }


    fun createReceivedGiftPopWindow(contentView: View?) {
        mPopWindow = CustomPopWindow.PopupWindowBuilder(mActivity)
                .setView(contentView)
                .setOutsideTouchable(true)
                .enableOutsideTouchableDissmiss(true)
                .setAnimationStyle(R.style.PopSendGiftEnterExitAnimation)
                .size(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
                //.size(mActivity.DeviceUtils.getScreenWidth(this) - DisplayUtils.dp2px(this, 200f), DisplayUtils.dp2px(this, 100F))
                .create()
                .showAtLocation(mBinding.root, Gravity.NO_GRAVITY, DisplayUtils.dp2px(mActivity, 1f), DisplayUtils.dp2px(mActivity, 1F))

        handler.postDelayed(Runnable {
            mActivity.runOnUiThread {
                mPopWindow?.dissmiss()
            }
        }, 2000)
    }

    /**
     * 移除功能模块的Fragment
     */
    fun removeFragment(fragment: Fragment) {
        if (mForContent != fragment && mForContent != null) {
            mForContent?.onDestroy()
            //mForContent = null
        }
    }

    fun newCustomPopWindow(contentView: View?) {
        mPopWindow = CustomPopWindow.PopupWindowBuilder(mActivity)
                .setView(contentView)
                .setOutsideTouchable(true)
                .enableOutsideTouchableDissmiss(false)// 设置点击PopupWindow之外的地方，不会dismiss
                .setAnimationStyle(R.style.DialogChatHowEnterExitAnimation) // 添加自定义显示和消失动画
                .size(getScreenWidth(mActivity),dp2px(mActivity, 300F))//显示大小
                .create()
                .showAsDropDown(mBinding.llTime, dp2px(mActivity, 0f), dp2px(mActivity, 5F))
    }

    fun showDialogWhichBadReason(i: Int) {//弹出差评弹窗
        val contentView = View.inflate(mActivity, R.layout.dialog_which_bad_reason, null)
        handleWhichBadReason(contentView,i)//点击事件
        newCustomPopWindow(contentView)
    }

    fun showDialogChatHow(i: Int) {//弹出互评弹窗
        val contentView = View.inflate(mActivity, R.layout.dialog_chat_how_layout, null)
        handleChatHowLogic(contentView, i)//点击事件
        newCustomPopWindow(contentView)
    }

    private val CHANGE_PERSON = 0   //切人
    private val CLOSE = 2   //关闭

    //聊的怎样？互评点击事件处理
    private fun handleChatHowLogic(contentView: View?, i: Int) {
        val listener = View.OnClickListener { v ->
            if (mPopWindow != null) {
                mPopWindow!!.dissmiss()
            }
            when (v.id) {
                R.id.laugh_face -> {
                    EventTrackingUtils.joinPoint(EventBeans("ck_matchevaluate_good",""))

                    if(activity != null) {
                        mActivity.niceToast("评价成功 ")
                        //我方评价中，对方切人，fragment会销毁，getActivity无法获取
                        val subscribe = mDataRepository.postFeedBack(
                                if (mDataRepository.getUserid() == (activity as MainActivity).mToUserId) (activity as MainActivity).mFromUserId!! else (activity as MainActivity).mToUserId!!,
                                mViewModel.mTalkId!!, "1", "").subscribe({
                            if (it.data != null) {
                                Log.i("postFeedBack", ""+it.data)
                                if(it.data.rs != null)
                                    Log.i("postFeedBack", ""+it.data?.rs)
                            }
                        })
                        mViewModel.mCompositeDisposable.add(subscribe)
                    }
                    if(i == CHANGE_PERSON) {//点击关闭时不执行，点击关闭时传入参数2
                        AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this@CallFragment)
                    } else if (i == CLOSE) {
                        mViewModel.endCall()
                    }
                }

                R.id.cry_face -> {
                    EventTrackingUtils.joinPoint(EventBeans("ck_matchevaluate_bad","o"))
                    showDialogWhichBadReason(i)
                }
                R.id.close_iv -> {
                    EventTrackingUtils.joinPoint(EventBeans("ck_matchevaluate_close",""))
                    if (i == CHANGE_PERSON) {//点击关闭时不执行，点击关闭时传入参数2
                        mViewModel.endCall()
                        mViewModel.netCancelMatching()
                        AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this@CallFragment)

                    }else if (i == CLOSE) {
                        mViewModel.endCall()
                        mViewModel.netCancelMatching()
                    }
                }
            }
        }
        contentView?.findViewById<ImageView>(R.id.laugh_face)?.setOnClickListener(listener)
        contentView?.findViewById<ImageView>(R.id.cry_face)?.setOnClickListener(listener)
        contentView?.findViewById<ImageView>(R.id.close_iv)?.setOnClickListener(listener)
    }

    //不好的原因点击事件处理
    private fun handleWhichBadReason(contentView: View, i: Int) {
        val listener = View.OnClickListener { v ->
            if (mPopWindow != null) {
                mPopWindow!!.dissmiss()
            }
            if (R.id.which_bad_close_iv == v.id) {
                EventTrackingUtils.joinPoint(EventBeans("ck_matchevaluate_badclose",""))
                if (i == CHANGE_PERSON) {
                    AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this@CallFragment)
                }else if (i == CLOSE) {
                    mViewModel.endCall()
                }
                return@OnClickListener
            }
            var reason = ""
            var reasonNum = 0
            when (v.id) {
                R.id.tv1 -> {
                    reason = "没声音，不说话"
                    reasonNum = 1
                }
                R.id.tv2 -> {
                    reason = "色情/威胁/辱骂"
                    reasonNum = 2
                }
                R.id.tv3 -> {
                    reason = "发广告"
                    reasonNum = 3
                }
                R.id.tv4 -> {
                    reason = "说不清"
                    reasonNum = 4
                }
            }
            if (reasonNum != 0) {
                EventTrackingUtils.joinPoint(EventBeans("ck_matchevaluate_badreason","$reasonNum"))
            }
            mActivity.niceToast("评价成功")
            if(activity != null) {
                //我方评价中，对方切人，fragment会销毁，getActivity无法获取
                val subscribe = mDataRepository.postFeedBack(
                        if (mDataRepository.getUserid() == (activity as MainActivity).mToUserId) (activity as MainActivity).mFromUserId!! else (activity as MainActivity).mToUserId!!,
                        mViewModel.mTalkId!!, "2", reason).subscribe({
                    if (it.data != null) {
                        Log.i("postFeedBack", ""+it.data)
                        if(it.data.rs != null)
                            Log.i("postFeedBack", ""+it.data?.rs)
                    }
                })
                //mViewModel.mCompositeDisposable.add(subscribe)
            }
            if(i == CHANGE_PERSON){
                AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this@CallFragment)
            }else if (i == CLOSE) {
                mViewModel.endCall()
            }
        }
        contentView.findViewById<TextView>(R.id.tv1).setOnClickListener(listener)
        contentView.findViewById<TextView>(R.id.tv2).setOnClickListener(listener)
        contentView.findViewById<TextView>(R.id.tv3).setOnClickListener(listener)
        contentView.findViewById<TextView>(R.id.tv4).setOnClickListener(listener)
        contentView.findViewById<ImageView>(R.id.which_bad_close_iv).setOnClickListener(listener)
    }

    val listener = EMCallStateChangeListener { callState, error ->
        Log.e("EM_CALL", "" + callState)
        when (callState) {
        //挂电话
            EMCallStateChangeListener.CallState.DISCONNECTED
            -> {
                mBinding.callTimer.stop()
//                mViewModel.netCancelMatching() //
                if (!mViewModel.isSwitch) {
                    MainActivity.startOpenCall(mActivity)
                } else {//切人
                    switchSubscribe?.dispose()
                    AnimationUtils.stopSwitchLoading()
//                      mBinding.ivSwitch.setImageResource(R.drawable.ic_switch)
                    mViewModel.netStoneIsEnough()
//                    MainActivity.startOutgoing(mActivity)
                }
//                    //关闭定时器
//                    mViewModel.mCompositeDisposable.clear()
                if ((activity as MainActivity).mFromUserId != null && (activity as MainActivity).mToUserId != null && mViewModel.getTalkId() != null) {
                    //通话结束
                    if ((activity as MainActivity).mToUserId == mDataRepository.getUserid() && unknownTalkId.isNotEmpty()) {
                        mViewModel.netReportTalkingStatus(talkId = unknownTalkId,
                                fromUserId = (activity as MainActivity).mFromUserId!!, toUserId = (activity as MainActivity).mToUserId!!,
                                reportType = "3",
                                reportValue = mViewModel.getEndTime().toString())
                    } else {
                        mViewModel.netReportTalkingStatus(talkId = mViewModel.getTalkId()!!,
                                fromUserId = (activity as MainActivity).mFromUserId!!, toUserId = (activity as MainActivity).mToUserId!!,
                                reportType = "3",
                                reportValue = mViewModel.getEndTime().toString())
                    }

                    //开始时间清零
                    getMainActivity()?.mViewModel?.restartTime()
                    EventBus.getDefault().post(
                            VoiceMessage(status = StatusTag.STATUS_CALL_IN, fromUserid = (activity as MainActivity).mFromUserId!!,
                                    type = StatusTag.TYPE_VOICE, toUserid = (activity as MainActivity).mToUserId!!))
                }
                //关闭定时器
//                  mViewModel.mCompositeDisposable.clear()
//                    clearNotify()
            }
            EMCallStateChangeListener.CallState.NETWORK_UNSTABLE -> {
//                mActivity.niceToast("网络不稳定")
            }
            EMCallStateChangeListener.CallState.NETWORK_DISCONNECTED -> {
                /*mBinding.callTimer.stop()

                MainActivity.startOpenCall(mActivity)
                //关闭定时器
                mViewModel.mCompositeDisposable.clear()
                //通话结束
                if ((activity as MainActivity).mToUserId == mDataRepository.getUserid() && unknownTalkId.isNotEmpty()) {
                    mViewModel.netReportTalkingStatus(talkId = unknownTalkId,
                            fromUserId = (activity as MainActivity).mFromUserId!!, toUserId = (activity as MainActivity).mToUserId!!,
                            reportType = "3",
                            reportValue = mViewModel.getEndTime().toString())
                } else {
                    mViewModel.netReportTalkingStatus(talkId = mViewModel.getTalkId()!!,
                            fromUserId = (activity as MainActivity).mFromUserId!!, toUserId = (activity as MainActivity).mToUserId!!,
                            reportType = "3",
                            reportValue = mViewModel.getEndTime().toString())
                }*/


            }
//                EMCallStateChangeListener.CallState.VIDEO_RESUME -> {
//                    //同意切换摄像头
//                    ChatApp.getInstance().setOnlineTime(mBinding.callTimer.base)
//                    MainActivity.startVideoOut(mActivity, mFromUserId!!, mToUserId!!,
//                            mViewModel.getTalkId()!!)
//                }
        }
    }

    private fun clearNotify() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(listener)
    }

    @NeedsPermission(android.Manifest.permission.CAMERA)
    fun switchVideo() {
        mViewModel.netRequestVideo(talkId = mViewModel.mTalkId!!, fromUserId = (activity as MainActivity).mFromUserId!!,
                toUserId = (activity as MainActivity).mToUserId!!)
    }

    private fun getMainActivity() = (mActivity as? MainActivity)


    override fun onDestroyView() {
        super.onDestroyView()
        clearNotify()
//    mCanClick = false
//    mViewModel.isSendVideo = false
//    mViewModel.isFriend = false
//    mViewModel.isSwitch = false
        onDestryEvenBus(this)
        RxTimerUtil.cancel()
        closeGuideControllers()
    }

    override fun onResume() {
        super.onResume()
            if (mViewModel.mTalkId == null) {//处理从好友语音聊天跳过来mTalkId为空的情况
                mBinding.rlSwitch.visibility = View.GONE
                mBinding.rlSwitch2.visibility = View.VISIBLE
            }else{
                mBinding.rlSwitch.visibility = View.VISIBLE
                mBinding.rlSwitch2.visibility = View.GONE
            }
    }

    override fun onStart() {
        super.onStart()
//    startTime()
        mViewModel.netNewMsg()
//        mViewModel.netDynamicsMsg()
    }

    @Subscribe
    fun onUgcVote(obj: UgcVoteResult) {
        if (obj.messageCount != "0") {
            mViewModel.netNewMsg()
        }
    }

    @Subscribe
    fun onUgcComment(obj: UgcCommentResult) {
        if (obj.messageCount != "0") {
            mViewModel.netNewMsg()
        }
    }

    @Subscribe
    fun onUgcTopic(obj: UgcTopicResult) {
        if (obj.messageCount != "0") {
            mViewModel.netDynamicsMsg()
        }
    }

    override fun onStop() {
        super.onStop()
        mReport = false
        mCanClick = false
        mViewModel.isSendVideo = false
        mViewModel.isFriend = false
        mViewModel.isSwitch = false
//    subscribe?.dispose()
        switchSubscribe?.dispose()
    }

    @Subscribe
    fun onNoticeMsg(obj: NoticeMsgResult) {
        mViewModel.netDynamicsMsg()
        mViewModel.netNewMsg()
    }

    @Subscribe
    fun onViewModel(vm: CallViewModel.StartMatchingVM) {
        if (vm.isSuccess) {//拨号
            vm.obj?.data?.matchingUserId?.let {
                getMainActivity()?.mViewModel?.setStartMatchingResult(vm.obj.data)
                initView()
            }
        }
    }

    /**
     * 匹配通话中响应切换视频
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun OnResponseSwitchVideoVM(obj: SwitchVideoResponse) {
//        if (obj.responseResult == "1") {
//            if (mViewModel.isSwitchVideoResponse(obj)) {
//                //同意切换摄像头
//                ChatApp.getInstance().setOnlineTime(mBinding.callTimer.base)
//                mViewModel.resumeVideoTransfer()
//                MainActivity.startVideoOut(mActivity, mFromUserId!!, mToUserId!!, mViewModel.getTalkId()!!)
//            } else {
//                ChatApp.getInstance().setOnlineTime(mBinding.callTimer.base)
//                MainActivity.startVideoOut(mActivity, mFromUserId!!, mToUserId!!, mViewModel.getTalkId()!!)
//            }
//        } else {
//            //不同意切换
//            Toast.makeText(ChatApp.mInstance?.applicationContext, "不同意切换视频", Toast.LENGTH_SHORT).show()
//            mViewModel.isSendVideo = true
//        }
//    }

    /**
     * 匹配通话中请求切换视频
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun OnRequestSwitchVideoVM(obj: SwitchVideoRequest) {
//        if (mViewModel.isSwitchVideoRequest(obj)) {
//            val videoRequestPop = VideoRequestPop(activity)
//            videoRequestPop.initData(obj.requestUserId!!, mDataRepository.getUserid()!!, obj.talkId!!,
//                    mBinding.nicknameTv.text.toString())
//            videoRequestPop.show(mBinding.root)
//        }
//
//    }

    /**
     * 友好度查询
     */
    @Subscribe
    fun onFriendExperienceVM(obj: CallViewModel.StoneIsEnough) {
        if (obj.isEnough) {
//            OpenCallFragment.isSHowStone = false
            MainActivity.startOutgoing(mActivity)
        } else {
            MainActivity.startOpenCall(mActivity)
            niceChatContext().niceToast("魔石不足，换人失败")
//            OpenCallFragment.isSHowStone = true
        }
    }

    /**
     * 友好度查询
     */
    @Subscribe
    fun onFriendExperienceVM(obj: CallViewModel.FriendExperienceVM) {
        if (obj.isSuccess) {
            obj.obj?.let {
                //        initProgress(obj = it)
            }

        }
    }

    /**
     * 获取标签
     */
    @Subscribe
    fun onSignsVM(obj: CallViewModel.SignsVM) = if (obj.success) {
//    mBinding.aflCotent.setAdapter(SignAdpater(activity, obj.obj?.tags));
        obj.obj?.tags?.forEachIndexed { index, sign ->
            if (index >= 5) {
                return@forEachIndexed
            } else {
                val itemView = LayoutInflater.from(niceChatContext()).inflate(R.layout.item_sign, null)
                val textview = itemView.findViewById<TextView>(R.id.txt_sign)
                textview.setSingleLine(true)
                textview.text = sign.tagName
                val params = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT)
//      params.order = -1
//      params.flexGrow = 2F
                mBinding.aflCotent.addView(itemView, params)
            }
        }
    } else {
        mBinding.aflCotent.visibility = View.GONE
    }

    fun setResume(booleanExtra: Boolean): CallFragment {
        this.mResume = booleanExtra
        return this@CallFragment
    }

//  fun insertDanmu(baseline: Int, data: DanmuResult.Data) {
//    data.barrage?.forEachIndexed { index, danmu -> addDanma(damaskContext, index, danmu, baseline) }
//  }

    /**
     * 获取弹幕
     */
//  @Subscribe
//  fun onDanmuVM(obj: CallViewModel.DanmuVM) = if (obj.success) {
//    insertDanmu(0, obj.obj!!)
//  } else {
//
//  }

//  @Subscribe
//  fun onDanmuPreparedVM(obj: CallViewModel.DanmuPrepared) {
//    mViewModel.netDanmu(
//        if (mDataRepository.getUserid() == mToUserId) mFromUserId!! else mToUserId!!)
//  }

    @Subscribe
    fun onSubscribe(obj: MainActivity.ChatReportVM) {//这个是由ActionPop里举报按钮发来的消息，从1.6版本去掉
        mViewModel.netReportReason()
    }

    @Subscribe
    fun onSubscribe(obj: MainActivity.ChatCloseVM) {
        if (mViewModel.mTalkId != null) //如果是从ChatActivity中开启一对一通话,就不弹出,because一对一通话会跑到这里执行通过过程
            showDialogChatHow(CLOSE)
        else
            mViewModel.endCall()
    }

    @Subscribe
    fun onReportReasonVM(obj: ChatViewModel.ReportReasonVM) {
        if (obj.isSuccess) {
            obj.obj?.data?.reportReason?.let {
                showReportReason(it)
            }
        } else {
            //niceToast("获取失败")
        }
    }

    /**
     * 举报
     */
    private fun showReportReason(list: List<ReportReasonResult.Data.ReportReason>) {
        val map = hashMapOf<String, String>()
        val names = arrayListOf<String>()
        list.forEach {
            val name = it.reportReasonName ?: return@forEach
            val id = it.reportReasonId ?: return@forEach
            map[name] = id
            names.add(name)
        }
        MaterialDialog.Builder(mActivity).items(names).itemsCallback { _, _, _, text ->
            val id = map[text]
            //if (mDataRepository.getUserid() == (activity as MainActivity).mToUserId) (activity as MainActivity).mFromUserId!! else (activity as MainActivity).mToUserId!!
            mViewModel.netReportUser(if (mDataRepository.getUserid() == (activity as MainActivity).mFromUserId) (activity as MainActivity).mToUserId!! else (activity as MainActivity).mFromUserId!!,
                    id!!,SystemClock.elapsedRealtime() - mBinding.callTimer.base)
        }.show()
    }

    /**
     * 举报
     */
    @Subscribe
    fun onReportUserVM(obj: ChatViewModel.ReportUserVM) {
        if (obj.isSuccess) {
//      niceToast("举报成功,我们正在处理")
            Toast.makeText(ChatApp.getInstance().applicationContext, "举报成功,我们正在处理",
                    Toast.LENGTH_SHORT).show()
        } else {
//      niceToast("网络等原因,举报失败请重试")
            Toast.makeText(ChatApp.getInstance().applicationContext, "网络等原因,举报失败请重试",
                    Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe
    fun onChangePerionResult(obj: ChangePersionResult) {
        Toast.makeText(ChatApp.getInstance().applicationContext, obj.message, Toast.LENGTH_SHORT).show()
        mViewModel.endCall()
        MainActivity.startOutgoing(mActivity)
    }

    @Subscribe
    fun onChangePerson(obj: OpenCallViewModel.ConsumeMatchingVM) {
        closeProgressDialog()
        if (obj.isSuccess) {
            AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this@CallFragment)
        } else {
            Toast.makeText(ChatApp.getInstance().applicationContext, "失败", Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe
    fun onExitChatVM(obj: ExitChatResult) {
        mViewModel.endCall()
    }

//  private fun addDanma(daemonContext: DanmakuContext, index: Int, danmu: DanmuResult.Data.Danmu,
//      baseline: Int) {
//    val item = daemonContext.mDanmakuFactory.createDanmaku(1, daemonContext) ?: return
//    if (!mBinding.sv2Danmaku.isPrepared) {
//      Log.e("danmu", "no-prepared")
//      return
//    }
//    val randomIndex = (Math.random() * 15).toInt()
//    val secTime = ((randomIndex + baseline) * 1000).toLong()
//    item.time = secTime
//    val randomColor = (Math.random() * 15).toInt()
//    val randomSize = (Math.random() * 6).toInt()
//    item.textSize = sizeList[randomSize] * (resources.displayMetrics.density)
//    item.textColor = colorList[randomColor]
//    item.textShadowColor = Color.BLACK
//    item.text = danmu.barrageContent
//    mBinding.sv2Danmaku.addDanmaku(item)
//  }

    val cacehStuffer = object : BaseCacheStuffer.Proxy() {
        override fun releaseResource(danmaku: BaseDanmaku?) {

        }

        override fun prepareDrawing(danmaku: BaseDanmaku?, fromWorkerThread: Boolean) {

        }

    }

    //  private var subscribe: Disposable? = null
    private var switchSubscribe: Disposable? = null

//  fun startTime() {
//    subscribe = Flowable.interval(15, 15, TimeUnit.SECONDS).observeOn(
//        AndroidSchedulers.mainThread()).subscribe({
//                                                    insertDanmu((it.toInt() + 1) * 15,
//                                                                mViewModel.danmuData!!)
//                                                  }, {})
//    CompositeDisposable().add(subscribe!!)
//  }

    private fun startSwitchTime() {
        mBinding.ivSwitch.setImageResource(R.drawable.ic_switch_load)
        AnimationUtils.startSwitchLoading(mBinding.ivSwitch)
        switchSubscribe = Flowable.interval(2, 2, TimeUnit.SECONDS).observeOn(
                AndroidSchedulers.mainThread()).subscribe({
            if (it.toInt() == 5) {
                mViewModel.isSwitch = false
                switchSubscribe?.dispose()
                AnimationUtils.stopSwitchLoading()
                mBinding.ivSwitch.setImageResource(R.drawable.ic_switch)
            }
        }, {
            Timber.tag("niushiqi-bengkui").i("startSwitchTime 崩溃")
        })
        CompositeDisposable().add(switchSubscribe!!)
    }

    /**
     * 动态未读消息
     */
    @Subscribe
    fun onUnReadMsg(obj: CircleViewModel.CircleUnReadMsgVM) {
        if (obj.success) {
            size3 = obj.obj.unReadCount!!
            sendCircleUnReadMsg()
            setMessageNumber()
            EventBus.getDefault().post(CircleViewModel.MyUnReadMsgVM(obj.success, obj.obj))
        }
    }

    @Subscribe
    fun onMainUnReadMsg(obj: CallViewModel.DynamicsMsgVM) {
        if (obj.success) {
            size4 = if (obj.obj?.count?:0 > 0) 1 else 0
            size4 = obj.obj?.count ?: 0
        } else {
            size4 = 0
        }
        sendCircleUnReadMsg()
        setMessageNumber()
    }

    /**
     * 广场未读消息
     */
    @Subscribe
    fun onSquareUnreadMessageVM(obj: MainViewModel.SquareUnreadMessageVM) {
        if (obj.success) {
            size5 = obj.obj
            //下拉页未读消息
            sendSquareUnReadMsg()
            //首页未读消息
            setMessageNumber()
            //广场页未读消息
            EventBus.getDefault().post(PlazaMainViewModel.SquareUnreadMessageVM(obj.success, obj.obj, obj.avatar))
        }
    }

    /**
     * 此处统一处理  好友发表的 以及自己动态收到的 点赞 评论 这些未读的总数
     */
    private fun sendCircleUnReadMsg() {
        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT, EventConstant.WHAT.WHAT_UNREAD_CIRCLE_COUNT, size3 + size4))
    }

    /**
     * 为了修改SystemMessageFragment页中的未读提示
     */
    private fun sendSquareUnReadMsg() {
        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT, EventConstant.WHAT.WHAT_UNREAD_SQUARE_COUNT, size5))
    }

    @Subscribe
    fun onUnreadMessageVM(obj: MainViewModel.UnreadMessageVM) {
        size1 = obj.list!!.size
        setMessageNumber()
    }

    @Subscribe
    fun onSubscribeNumVM(obj: MainViewModel.RefreshPeopleNumVM) {
        size2 = obj.nums
        setMessageNumber()
    }


    private fun setMessageNumber() {
        if (size1 > 0 || size2 > 0 || size3 > 0 || size4 > 0 || size5 > 0) {
            mBinding.topMessageFore.visibility = View.VISIBLE
            mBinding.txtTopMessageFore.text = "您有 ${size1 + size2 + size3 + size4 + size5} 条新消息"

        } else {
            mBinding.topMessageFore.visibility = View.GONE
        }
        try {
            val badgeCount = size1 + size2 + size3 + size4 + size5
            BadgeUtils.setBadgeCount(mActivity, badgeCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}