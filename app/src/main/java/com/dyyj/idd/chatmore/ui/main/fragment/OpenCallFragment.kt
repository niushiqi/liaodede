package com.dyyj.idd.chatmore.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.app.hubert.guide.NewbieGuide
import com.app.hubert.guide.core.Controller
import com.app.hubert.guide.model.GuidePage
import com.app.hubert.guide.model.HighLight
import com.app.hubert.guide.model.HighlightOptions
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentOpenCallBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.easemob.EasemobManager
import com.dyyj.idd.chatmore.model.getui.NotificationHelper
import com.dyyj.idd.chatmore.model.mqtt.result.*
import com.dyyj.idd.chatmore.model.network.result.CurrentTaskListResult
import com.dyyj.idd.chatmore.model.network.result.MatchingButtonEntity
import com.dyyj.idd.chatmore.ui.dialog.activity.RedPacketActivity
import com.dyyj.idd.chatmore.ui.dialog.fragment.SlidingViewPagerFragment
import com.dyyj.idd.chatmore.ui.event.*
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.task.activity.TaskSystemActivity
import com.dyyj.idd.chatmore.ui.user.activity.MeActivity
import com.dyyj.idd.chatmore.ui.user.activity.ShopActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteGameActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WalletActivity
import com.dyyj.idd.chatmore.utils.*
import com.dyyj.idd.chatmore.viewmodel.*
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import com.dyyj.idd.chatmore.weiget.NoticeView
import com.dyyj.idd.chatmore.weiget.pop.RedPacketPop2
import com.dyyj.idd.chatmore.weiget.pop.ShareRedPacketPop
import com.example.zhouwei.library.CustomPopWindow
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceCustomTaost
import com.gt.common.gtchat.extension.niceToast
import com.othershe.nicedialog.NiceDialog
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.autosize.utils.AutoSizeUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/20
 * desc   : 开启匹配界面
 */
@RuntimePermissions
class OpenCallFragment : BaseFragment<FragmentOpenCallBinding, OpenCallViewModel>(), MyFrameAnimation.OnFrameAnimationListener {
    private var size1 = 0
    private var size2 = 0
    private var size3 = 0
    private var size4 = 0
    private var size5 = 0   //广场未读
    var isInitRedBigOpen = false
    var aa : String ? = null

    private var mPopWindow: CustomPopWindow? = null

    private var isForeground = true
    var data: List<CurrentTaskListResult.Data>? = arrayListOf()

    companion object {
        var isBoxClose = true
        var isGameClose = true
        var mInstance: OpenCallFragment? = null
        const val TAG = "SIP"
        var mGoldTake: Int = 0
        var startTime: Long = 0
        var endTime: Long = 0
        var isRegister = false
        var isShareSuccess = false
        var isGuide3 = false
        var timeOutTip: String = ""
        var textMatchTimes: Int = 1
        var matchButtonEntity: MatchingButtonEntity? = null

        var isSHowStone = false // 初始化是否显示魔石不足对话框

        /**
         * 单例
         */
        fun instance(): OpenCallFragment {
            if (mInstance == null) {
                return OpenCallFragment()
            }
            return mInstance!!
        }
    }

    @Subscribe
    fun OnOpenBoxVM(obj: OpenCallViewModel.OpenBoxVM) {
        isBoxClose = !obj.isOpen
        if (isBoxClose) {
            mBinding.getRedPacketCl.visibility = View.INVISIBLE
            mBinding.redPacketCl.visibility = View.INVISIBLE
        } else {
            mBinding.getRedPacketCl.visibility = View.VISIBLE
            if (isInitRedBigOpen) {
                mBinding.redPacketCl.visibility = View.VISIBLE
            }
        }
        isInitRedBigOpen = true
        //引导页
        if (!mViewModel.getGuideStatus()) {
            guide()
            mViewModel.saveGuideStatus()
        }
    }

    @Subscribe
    fun OnOpenGameVm(obj: OpenCallViewModel.OpenGameVM) {
        isGameClose = !obj.isOpen
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_open_call
    }

    override fun onViewModel(): OpenCallViewModel {
        return OpenCallViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//    (mActivity as MainActivity).mBinding.rlRedTake.visibility = View.VISIBLE
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        val channelValue = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getInt("definechannel")
//        Log.i("aaa","channelValue=${channelValue}")
        mViewModel.getBoxOpen(channelValue.toString())
        mViewModel.getGameOpen(channelValue.toString())
        mViewModel.getMatchingButton()

        initListenter()
        //    mViewModel.netMainInfo()

        //每次进入开始匹配,初始化备选池状态
        CallFragment.unknownTalkId = ""
        mViewModel.getRedPackageList()

        mViewModel.getCurrentTaskList()


        val subscribe = Observable.interval(30 * 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    if (isForeground) {
                        mViewModel.getCurrentTaskList()
                    }
                }

        val subscribe1 = Observable.interval(30 * 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    if (isForeground && data!!.isNotEmpty()) {
                        nextIndex++
                        if (data!!.size - 1 > nextIndex) {
                            currentTask = data!![nextIndex]
                        } else {
                            nextIndex = 0
                        }
                        setTaskData()
                    }
                }
        mViewModel.mCompositeDisposable.add(subscribe)
        mViewModel.mCompositeDisposable.add(subscribe1)

//        if(isSHowStone){
//            isSHowStone = false
//            magicStoneNotEnough()
////            MatchingFailedActivity.start(mActivity)
//        }

//        MainActivity.startCallOut(mActivity, "1000004582", "1000004582")
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
        mViewModel.getMatchingButton()
    }

    override fun onPause() {
        super.onPause()
        isForeground = false
    }

    /**
     * 引导页蒙层
     */
    private fun guide() {
        var controller: Controller? = null

        val options = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
            val paint = Paint()
            paint.color = Color.parseColor("#FFEC1C")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.pathEffect = DashPathEffect(floatArrayOf(24f, 20f), 0f)
            var space = AutoSizeUtils.dp2px(activity, 23f).toFloat()
            var newRect = RectF(rectF.left - space, rectF.top - space, rectF.right + space, rectF.bottom + space)
            canvas.drawRoundRect(newRect, AutoSizeUtils.dp2px(activity, 52f).toFloat(), AutoSizeUtils.dp2px(activity, 52f).toFloat(), paint)
        }.build()

        val page = GuidePage.newInstance().addHighLightWithOptions(mBinding.rlStartVoiceChat,
                HighLight.Shape.ROUND_RECTANGLE,
                options).setLayoutRes(
                R.layout.layout_guide1).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))

        val options2 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
            val paint = Paint()
            paint.color = Color.parseColor("#FFEC1C")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
            var space = AutoSizeUtils.dp2px(activity, 23f).toFloat()
            var newRect = RectF(rectF.left - space, rectF.top - space, rectF.right + space, rectF.bottom + space)
            canvas.drawRoundRect(newRect, AutoSizeUtils.dp2px(activity, 52f).toFloat(), AutoSizeUtils.dp2px(activity, 52f).toFloat(), paint)
        }.build()

        val page2 = GuidePage.newInstance().addHighLightWithOptions(mBinding.rlStartTextChat,
                HighLight.Shape.ROUND_RECTANGLE,
                options2).setLayoutRes(
                R.layout.layout_guide2).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))

        controller = NewbieGuide.with(this)
                .setLabel("guide2")
                .alwaysShow(true).addGuidePage(page).addGuidePage(page2).show()
    }

    /**
     * 第一次红包成熟,引导页
     */
    fun guide2() {
        if (isBoxClose) {
            mBinding.redPacketCl.visibility = View.INVISIBLE
        } else {
            mBinding.redPacketCl.visibility = View.VISIBLE
        }
        var controller: Controller? = null

        val options1 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
            //      val paint = Paint()
//      paint.color = Color.parseColor("#FFEC1C")
//      paint.style = Paint.Style.STROKE
//      paint.strokeWidth = 6f
//      paint.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
//      canvas.drawRect(rectF, paint)
        }.setOnClickListener {
            mViewModel.getRedPacket()?.let {
                getRedPacketPop(it.giftId.toString(), mBinding.model2?.openedNum?.toInt())
            }
            controller?.remove()
            isGuide3 = true
        }.build()
        val page1 = GuidePage.newInstance().addHighLightWithOptions(mBinding.redPacketCl,
                HighLight.Shape.ROUND_RECTANGLE,
                options1).setLayoutRes(
                R.layout.layout_guide7).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_10))

        controller = NewbieGuide.with(this)//传入fragment
                .setLabel("guide3")//设置引导层标示，必传！否则报错
                .alwaysShow(true).addGuidePage(page1).show()//直接显示引导层
    }

    /**
     * 引导页3
     */
    fun guide3() {
        var controller: Controller? = null

        val options5 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
        }.setOnClickListener {
            controller?.remove()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callOngoingWithPermissionCheck()
            } else {
                callOngoing()
            }

        }.build()

        val page5 = GuidePage.newInstance().addHighLightWithOptions(mBinding.rlStartVoiceChat,
                HighLight.Shape.ROUND_RECTANGLE,
                options5).setLayoutRes(
                R.layout.layout_guide5).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))


        val options6 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
        }.setOnClickListener {
            controller?.remove()
            mViewModel.getRedPacket()?.let {
                getRedPacketPop(it.giftId.toString(), mBinding.model2?.openedNum?.toInt())
            }

        }.build()
        controller = NewbieGuide.with(this)//传入fragment
                .setLabel("guide2")//设置引导层标示，必传！否则报错
                .alwaysShow(true).addGuidePage(page5).show()//直接显示引导层
    }

    fun guide5() {
        var controller: Controller? = null

        val options6 = HighlightOptions.Builder().setOnHighlightDrewListener { canvas, rectF ->
        }.setOnClickListener {
            controller?.remove()
        }.build()

        val page6 = GuidePage.newInstance().addHighLightWithOptions(mBinding.txtMessageTop,
                HighLight.Shape.ROUND_RECTANGLE,
                options6).setLayoutRes(
                R.layout.layout_guide_8).setBackgroundColor(
                ContextCompat.getColor(mActivity, R.color.black_80))


        controller = NewbieGuide.with(this)//传入fragment
                .setLabel("guide8")//设置引导层标示，必传！否则报错
                .alwaysShow(true).addGuidePage(page6).show()//直接显示引导层
    }

    private fun progressTime(time: Long, currentTime: Long): Float {
        return (100.toFloat() / time.toFloat() * currentTime)
    }

    override fun onStart() {
        super.onStart()
        mViewModel.netMainInfo()
//        mViewModel.loadUnreadMessage()
//        mViewModel.getApplyNum()
        (mActivity as MainActivity).mViewModel.getApplyNum()
        (mActivity as MainActivity).mViewModel.loadUnreadMessage()
        mViewModel.netHankOpen()
        mViewModel.netNewMsg()
//        mViewModel.netDynamicsMsg()
    }

    @Subscribe
    fun onEventMessage(obj : EventMessage<Any>){
        if (obj.tag == EventConstant.TAG.TAG_OPENCALL_FRAGMENT){
            when(obj.what){
                EventConstant.WHAT.WHAT_MATCHBUTTON -> {
                    matchButtonEntity = obj.obj as MatchingButtonEntity
                    if(matchButtonEntity!!.data.textEnable.equals("1")) {
                        mBinding.rlStartTextChat!!.visibility = View.VISIBLE
                        mBinding.rlStartTextChat!!.findViewById<TextView>(R.id.tv_text_title).text = matchButtonEntity!!.data.textTip.title
                        var mvTextSub = mBinding.rlStartTextChat!!.findViewById<NoticeView>(R.id.mv_text_sub)
                        mvTextSub.start(arrayListOf(matchButtonEntity!!.data.textTip.line1,matchButtonEntity!!.data.textTip.line2))
                    } else mBinding.rlStartTextChat!!.visibility = View.GONE
                    if(matchButtonEntity!!.data.voiceEnable.equals("1")) {
                        mBinding.rlStartVoiceChat!!.visibility = View.VISIBLE
                        mBinding.rlStartVoiceChat!!.findViewById<TextView>(R.id.tv_voice_title).text = matchButtonEntity!!.data.voiceTip.title
                        var mvVoiceSub = mBinding.rlStartVoiceChat!!.findViewById<NoticeView>(R.id.mv_voice_sub)
                        mvVoiceSub.start(arrayListOf(matchButtonEntity!!.data.voiceTip.line3,matchButtonEntity!!.data.voiceTip.line4))
                    } else mBinding.rlStartVoiceChat!!.visibility = View.GONE
                    //timeOutTip = matchButtonEntity!!.data.voiceTip.timeOutTip
                    textMatchTimes = matchButtonEntity!!.data.textTip.remainTimes
                }
            }
        }
    }

    @Subscribe
    fun onUgcVote(obj: UgcVoteResult) {
        if (obj.messageCount != "0") {
            mViewModel.netNewMsg()
//            mViewModel.netDynamicsMsg()
        }
    }

    @Subscribe
    fun onUgcComment(obj: UgcCommentResult) {
        if (obj.messageCount != "0") {
            mViewModel.netNewMsg()
//            mViewModel.netDynamicsMsg()
        }
    }

    @Subscribe
    fun onUgcTopic(obj: UgcTopicResult) {
        if (obj.messageCount != "0") {
            mViewModel.netDynamicsMsg()
        }
    }

    private var scrollY: Float = 0F
    private var downY: Float = 0F

    /**
     * 初始化View
     */
    private fun initView() {
        val model = (mActivity as? MainActivity)?.mViewModel?.getMainResult()?.data
        mBinding.model = model
        (mActivity as MainActivity).mBinding.model = model
        DataBindingUtils.loadAvatar(/*mBinding.avatarIv*/(mActivity as MainActivity).mBinding.avatarIv, model?.userBaseInfo?.avatar)
        initStarAnim()
//    mViewModel.getRedPackageList()
/*        if (mGoldTake > 0) {
            AnimationUtils.startGoldBtn(mBinding.ivGoldCircle, mBinding.ivGoldCircle2, false, false,
                    mBinding.rlGoldNum, mBinding.txtGoldNum)
        }*/

        mBinding.txtMessageTop.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
            EventTrackingUtils.joinPoint(EventBeans("ck_home_messagepage",""))
        }

        scrollY = DeviceUtils.dp2px(context?.resources, 10f)
        mBinding.root.setOnTouchListener { v, event ->
            val yM = event.y
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    downY = yM
                    return@setOnTouchListener true
                }
                event.action == MotionEvent.ACTION_MOVE -> {
                    Log.e("pop", (yM - downY).toString())
                    if ((yM - downY > 0) and (Math.abs(yM - downY).toInt() > scrollY.toInt())) {
                        (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
                    }
                }
                event.action == MotionEvent.ACTION_UP -> {
                    downY = 0F
                }
            }
            return@setOnTouchListener false
        }
        var freezeCoin = getMainActivity()!!.mViewModel.getMainResult()?.data?.userWallet?.freezeCoin
        if (!TextUtils.isEmpty(freezeCoin) && freezeCoin!!.toInt() > 0) {
            mGoldTake = freezeCoin!!.toInt()
            mBinding.rlStartVoiceChat?.findViewById<RelativeLayout>(R.id.rl_user_coin)?.visibility = View.VISIBLE
            mBinding.rlStartVoiceChat?.findViewById<TextView>(R.id.tv_user_coin)?.text = "${mGoldTake}枚"
        } else {
            mBinding.rlStartVoiceChat?.findViewById<RelativeLayout>(R.id.rl_user_coin)?.visibility = View.GONE
        }
    }

    /**
     * 暂存奖金
     */
    @Subscribe
    fun onSaveCoin(obj: RedPacketPopViewModel.GetGiftVM) {
//    if (obj.isSuccess) {
//      val first = mGoldTake == 0
//      mGoldTake = obj.obj?.freezeCoin?.toInt()?:0
//      AnimationUtils.startGolds(mBinding.txtGold, mBinding.ivGoldCircle, mBinding.ivGoldCircle2, true, first, mBinding.rlGoldNum, mBinding.txtGoldNum)
//    }
    }

    /**
     * 消耗奖金
     */
    @Subscribe
    fun onCustomCoin(obj: CallViewModel.CustomCoin) {
        mGoldTake = obj.freeCoin.toInt()
        AnimationUtils.startGoldBtn(mBinding.ivGoldCircle, mBinding.ivGoldCircle2, false, false,
                mBinding.rlGoldNum, mBinding.txtGoldNum)
    }

    /**
     * 执行奖励动画
     */
    @Subscribe
    fun runGiftAnimVM(obj: RedPacketPopViewModel2.GetGiftOkVM) {

        if (obj != null && obj.obj != null) {
            val first = (mGoldTake == 0)
            mGoldTake = obj.obj?.freezeCoin?.toInt() ?: 0
            mBinding.txtGold.text = "+金币 ${obj.obj?.addCoin}枚"
            AnimationUtils.startGolds(mBinding.txtGold, mBinding.ivGoldCircle, mBinding.ivGoldCircle2,
                    true, first, mBinding.rlGoldNum, mBinding.txtGoldNum)

            val startLocation = IntArray(2)
            mBinding.ivMoshi.getLocationInWindow(startLocation)
            val endLocation = IntArray(2)
            (activity as MainActivity).mBinding.tipIv.getLocationInWindow(endLocation)
            AnimationUtils.startPathAnim(mActivity, obj.obj, mBinding.clWrap, startLocation[0].toFloat(),
                    startLocation[1].toFloat(), /*mBinding.cashLl*/(mActivity as MainActivity).mBinding.cashLl, (mActivity as MainActivity).mBinding.coinLl,
                    (mActivity as MainActivity).mBinding.stoneLl, (mActivity as MainActivity).mBinding.viewWallet, endLocation[0].toFloat(),
                    endLocation[1].toFloat())//设置奖励的动画效果，物品跟随控件的位置飘移

            val subscribe = Flowable.interval(2, 0, TimeUnit.SECONDS).take(1).subscribeOn(
                    Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                mActivity.niceCustomTaost(
                        "第${obj.obj.openedNum}个红包\n" + "领取成功")
            }, {})
            mViewModel.mCompositeDisposable.add(subscribe)

            //红包领取成功刷新主页信息
            mViewModel.netMainInfo()


            /**
             * 领取完普通红包,当前是指定倍数.弹出分享微信窗口
             */
            if (obj.obj.isBigPackage == 1) {
                val subscribe2 = Flowable.interval(3, 0, TimeUnit.SECONDS).take(1).subscribeOn(
                        Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    startRedPakcets()
                }, {})
                mViewModel.mCompositeDisposable.add(subscribe2)

            } else {
                //普通红包,接着刷新界面
                mViewModel.getRedPackageList()
            }

            if (isGuide3) {
                isGuide3 = false
                Flowable.interval(4, TimeUnit.SECONDS).take(1).subscribe({
                    guide3()
                })

            }
        }

    }

    /**
     * 红包三连开
     */
    private fun startRedPakcets() {
        ShareRedPacketPop(mActivity).show(mActivity, mBinding.readPacketContentTv)
    }

    @Subscribe
    fun onRefreshGiftVM(obj: SlidingViewPagerFragment.RefreshGiftVM) {
//    mViewModel.getRedPackageList()
    }


    /**
     * 刷新主页信息
     */
    @Subscribe
    fun onRefreshMainInfo(obj: OpenCallViewModel.RefreshMain) {
        mViewModel.netMainInfo()
    }


    /**
     * 刷新数据数据
     */
    @Subscribe
    fun onMainVM(obj: OpenCallViewModel.MainVM) {
        getMainActivity()?.mViewModel?.setMainResult(obj.obj)
        initView()
    }

    private fun initStarAnim() {
        val alpha = android.view.animation.AnimationUtils.loadAnimation(activity, R.anim.start_alpha);
        mBinding.imageView6.startAnimation(alpha)
        mBinding.imageView14.startAnimation(alpha)
        mBinding.imageView7.startAnimation(alpha)
        mBinding.imageView9.startAnimation(alpha)
        mBinding.imageView11.startAnimation(alpha)
        mBinding.imageView13.startAnimation(alpha)
        mBinding.imageView16.startAnimation(alpha)
        mBinding.imageView18.startAnimation(alpha)
        mBinding.imageView8.startAnimation(alpha)
        mBinding.imageView15.startAnimation(alpha)
        mBinding.imageView12.startAnimation(alpha)
        mBinding.imageView17.startAnimation(alpha)
        mBinding.imageView10.startAnimation(alpha)
    }

    /**
     * 初始化点击事件
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initListenter() {
        (mActivity as MainActivity).mBinding.avatarIv.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            MeActivity.start(mActivity)
//      MainActivity.startCallIn(mActivity, "10019", "10037")
//      MainActivity.startCallIn(mActivity, "10037", "10019")
            EventTrackingUtils.joinPoint(EventBeans("ck_home_head", ""))
        }
        mBinding.rlStartVoiceChat!!.setOnClickListener {//开启语音聊天
            if (!DeviceUtils.isConn(mActivity)) {
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            MainActivity.isVoiceCall = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callOngoingWithPermissionCheck()
            } else {
                callOngoing()
            }
            EventTrackingUtils.joinPoint(EventBeans("ck_home_startmatch", ""))
        }
        mBinding.rlStartTextChat!!.setOnClickListener {//开启文字聊天
            if (!DeviceUtils.isConn(mActivity)) {
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            MainActivity.isVoiceCall = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callOngoingWithPermissionCheck()
            } else {
                callOngoing()
            }
            EventTrackingUtils.joinPoint(EventBeans("ck_home_startmatch_message", ""))
        }
        mBinding.tipRl.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            it.visibility = View.GONE }

        //打开钱包
//    mBinding.walletTv.setOnClickListener { WalletActivity.start(mActivity) }
        (mActivity as MainActivity).mBinding.viewWallet.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            //                  WalletActivity.start(mActivity)
//            val neturl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
//            H5Activity.start(mActivity, neturl, "社交银行", true)
//            mBinding.xiaoxiRl.visibility = View.GONE
            if (mViewModel.isHankOpen) {
                val neturl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserid()}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                H5Activity.start(mActivity, neturl, "社交银行", true)
                mBinding.xiaoxiRl.visibility = View.GONE
                (mActivity as MainActivity).mBinding.imgNewCoin.setVisibility(View.GONE)
            } else {
                WalletActivity.start(mActivity)
            }
            EventTrackingUtils.joinPoint(EventBeans("ck_home_bank",""))
        }

        //显示或者隐藏红包
        mBinding.getRedPacketCl.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            if (mBinding.redPacketCl.visibility == View.VISIBLE) {
                mBinding.redPacketCl.visibility = View.INVISIBLE
            } else {
                if (isBoxClose) {
                    mBinding.redPacketCl.visibility = View.INVISIBLE
                } else {
                    mBinding.redPacketCl.visibility = View.VISIBLE
                }
            }
        }


//    mBinding.readPacketContentTv.setOnClickListener {
//      startRedPakcets()
//    }

        mBinding.ivTakeMoney.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            InviteNewActivity.start(activity!!)
//            PicSelectActivity.start(activity!!, arrayListOf())
            EventTrackingUtils.joinPoint(EventBeans("ck_home_invite",""))
        }
        mBinding.renwuCl.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            TaskSystemActivity.start(mActivity)
            EventTrackingUtils.joinPoint(EventBeans("ck_home_task",""))
        }
//        mBinding.xiaoxiCl.setOnClickListener {
//            //      MessageSystemActivity.start(activity!!)
//            (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
//        }
        mBinding.xiaoxiRl.setOnClickListener {
            if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                mActivity.niceToast("当前无可用网络")
                return@setOnClickListener
            }
            if (mViewModel.isHankOpen) {
                val neturl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                H5Activity.start(mActivity, neturl, "社交银行", true)
                mBinding.xiaoxiRl.visibility = View.GONE
            } else {
                WalletActivity.start(mActivity)
            }
        }
//        mViewModel.netHankOpen()
    }

    /**
     * 领取普通红包
     */
    private fun getRedPacketPop(giftId: String, count: Int?) {
        if (!NotificationManagerCompat.from(mActivity).areNotificationsEnabled() && (count == 4 || (count?.rem(10)) == 0)) {
            /*val show = NotifactionPop(mActivity).show(mBinding.getRedPacketCl, "红包可领时，我们将立刻通知你",//1.4版本废弃
                    "不错过每一个红包", "sliding")
            if (show) return*/
            showPopNotificationsEnable()//1.6新加
        }
        RedPacketPop2(activity).show(getMainActivity()?.mBinding?.root!!, giftId)
    }

    /**
     * 领取三连开红包
     */
    private fun getRedPacketsPop(giftId: String) {
//    if (!NotificationManagerCompat.from(mActivity).areNotificationsEnabled()) {
//      val show = NotifactionPop(mActivity).show(mBinding.getRedPacketCl, "红包可领时，我们将立刻通知你",
//                                                "不错过每一个红包", "sliding")
//      if (show) return
//    }

        RedPacketActivity.start(mActivity, giftId)
//    RedPacketPop3(activity).show(mActivity, (mActivity as MainActivity).mBinding.root)
    }

    override fun onDestroyView() {
        (mActivity as MainActivity).mBinding.rlRedTake.visibility = View.GONE
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun startVoiceCall() {
        MainActivity.isVoiceCall = true
        if (getMainActivity()?.mViewModel?.getMainResult() != null) {//主页返回数据判断
            if (getMainActivity()?.mViewModel?.getMainResult()?.data?.userWallet?.userStone!!.toInt() >= 3) {
                AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
            } else {
                magicStoneNotEnough()
            }
        } else {
            mActivity.niceToast("网络错误,魔石获取失败")
        }
    }

    /**
     *  拨打语音电话
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO)
    fun callOngoing() {
        //获取当前魔石和次数
        if (MainActivity.isVoiceCall) {
            startVoiceCall()
        } else {
            mViewModel.netCurrentCanBeStart()
        }
    }
            //        mBinding.pbSmall.visibility = View.VISIBLE//进度条消失
        /*if (MainActivity.isVoiceCall){//开启聊天语音匹配
            if (mDataRepository.getCurrentStone() == null) {
                Log.i("zhangwj","预计崩溃点 魔石为空: mDataRepository.getCurrentStone() == null")
                if (getMainActivity()?.mViewModel?.getMainResult() != null) {//主页返回数据判断
                    if (getMainActivity()?.mViewModel?.getMainResult()?.data?.userWallet?.userStone!!.toInt() >= 3) {
                        AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
                    }else{
                        magicStoneNotEnough()
                    }
                }else{
                    mActivity.niceToast("网络错误,魔石获取失败")
                }
            }else{
                if (mDataRepository.getCurrentStone()!!.toInt() >= 3) {
                    AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
                }else{
                    magicStoneNotEnough()
                }
            }
        } */

    //魔石不足弹窗
    private fun magicStoneNotEnough() {
        EventTrackingUtils.joinPoint(EventBeans("sw_stonelack",""))
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_magic_stone_not_enough)     //设置dialog布局文件
                .setConvertListener { holder, dialog ->
                    //进行相关View操作的回调
                    holder.setOnClickListener(R.id.store_btn) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_stonelack_toshop", ""))
                        dialog.dismiss()
                        MainActivity.startOpenCall(mActivity)
                        ShopActivity.start(mActivity,0)
                    }
                    holder.setOnClickListener(R.id.gift_btn) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_stonelack_totask", ""))
                        dialog.dismiss()
                        MainActivity.startOpenCall(mActivity)
                    }
                    holder.setOnClickListener(R.id.close_iv) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_stonelack_close", ""))
                        dialog.dismiss()
                        MainActivity.startOpenCall(mActivity)
                    }
                }
                .setDimAmount(0.8f)     //调节灰色背景透明度[0-1]，默认0.5f
                .setShowBottom(false)     //是否在底部显示dialog，默认flase
                .setWidth(260)     //dialog宽度（单位：dp），默认为屏幕宽度
                .setHeight(310)     //dialog高度（单位：dp），默认为WRAP_CONTENT
                .setOutCancel(false)     //点击dialog外是否可取消，默认true
                //                .setAnimStyle(R.style.EnterExitAnimation)//设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
                .show(mActivity.getSupportFragmentManager())     //显示dialog
    }

    //文字匹配次数不足弹窗
    private fun TextTimeNotEnough() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_time_not_enough)     //设置dialog布局文件
                .setConvertListener { holder, dialog ->
                    //进行相关View操作的回调
                    holder.setOnClickListener(R.id.start_voice_btn) {
                        //开启语音聊天
                        startVoiceCall()
                        dialog.dismiss()
                    }
                    holder.setOnClickListener(R.id.to_buy_btn) {
                        ShopActivity.start(mActivity,2)
                        dialog.dismiss()
                    }
                    holder.setOnClickListener(R.id.close_iv) {
                        dialog.dismiss()
                    }
                }
                .setDimAmount(0.8f)
                .setShowBottom(false)
                .setWidth(260)
                .setHeight(285)
                .setOutCancel(false)
                .show(mActivity.getSupportFragmentManager())     //显示dialog
    }

     //文字匹配次数用尽弹窗
    private fun TextTimeReallyNotEnough() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_time_really_not_enough)
                .setConvertListener { holder, dialog ->
                    //进行相关View操作的回调
                    holder.setOnClickListener(R.id.store_btn) {
                        dialog.dismiss()
                        ShopActivity.start(mActivity,2)
                    }
                    holder.setOnClickListener(R.id.start_voice_btn) {
                        dialog.dismiss()
                        //获取当前魔石和次数
                        //开启语音聊天
                        startVoiceCall()
                    }
                    holder.setOnClickListener(R.id.close_iv) {
                        dialog.dismiss()
                    }
                }
                .setDimAmount(0.8f)
                .setShowBottom(false)
                .setWidth(260)
                .setHeight(285)
                .setOutCancel(false)
                //                .setAnimStyle(R.style.EnterExitAnimation)//设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
                .show(mActivity.getSupportFragmentManager())     //显示dialog
    }



//    @Subscribe
//    fun onMatchingOkVM(obj: OpenCallViewModel.MatchingResultVM) {
//        mBinding.pbSmall.visibility = View.GONE
//        if (obj.success) {
//            AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
//        } else {
//            Toast.makeText(niceChatContext(), obj.msg, Toast.LENGTH_SHORT).show()
//        }
//    }

    @Subscribe
    fun onConsumeMatschingVM(obj: OpenCallViewModel.CanBeStartVM) {//判断魔石和次数是否足够
        if (obj.obj?.errorCode == 200) {
            AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi10", 1, 40, 30, this)
        }else if (obj.obj?.errorCode == 3005) {//魔石不足1080*1920
            magicStoneNotEnough()
        }else if (obj.obj?.errorCode == 3700) {//文字匹配6次用尽
            TextTimeReallyNotEnough()
        }else if (obj.obj?.errorCode == 37001) {//文字匹配次数不足
            TextTimeNotEnough()
        }
    }

    @SuppressLint("NoCorrespondingNeedsPermission")
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO)
    fun showCameraDialog(request: PermissionRequest) {
        MaterialDialog.Builder(mActivity).title(R.string.title_hint).content(
                "应用需要相关权限才能开启功能").positiveText(R.string.button_ok).show()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @Subscribe
    fun onConsumeMatchingVM(obj: OpenCallViewModel.ConsumeMatchingVM) {
        closeProgressDialog()
        if (obj.isSuccess) {
            AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
        }
    }

    @Subscribe
    fun onBanknewVM(obj: BankNewResult) {
        if (mViewModel.isHankOpen) {
            mBinding.xiaoxiRl.visibility = View.VISIBLE
        } else {
            mBinding.xiaoxiRl.visibility = View.INVISIBLE
        }
    }

    fun goToNotificationSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val i = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            i.putExtra(Settings.EXTRA_APP_PACKAGE, mActivity.getPackageName())
            i.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationHelper.PRIMARY_CHANNEL)
            startActivity(i)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + mActivity.getPackageName())
            context.startActivity(intent)
        }
    }

    override fun onStartFrameAnimation() {
        mBinding.moshiIv.visibility = View.VISIBLE
    }

    /**
     * 魔石动画播放成功
     */
    override fun onEnd() {
        //魔石动画播放完成,打开匹配界面
        mBinding.moshiIv.visibility = View.GONE
//    (mActivity as? MainActivity)?.addFragment(CallOutgoingFragment.instance())
        MainActivity.startOutgoing(mActivity)
    }

    private fun getMainActivity() = (mActivity as? MainActivity)

    /**
     * 取消匹配
     */
    @Subscribe
    fun onCancelMatchingVM(obj: CancelMatchingViewModel.CancelMatchingVM) {
        /*if (obj.isSuccess) {
            MainActivity.startOpenCall(mActivity)
//      getMainActivity()?.supportFragmentManager?.popBackStack()
        } else {
            mActivity.niceToast("匹配失败")
        }*/
        if (obj.isSuccess) {
            EasemobManager.isStartMatch = false
            ChatApp.getInstance().getDataRepository().endCall()
//      onBackPressed()
        } else {
//      closeProgressDialog()
            MainActivity.startOpenCall(mActivity)
            ChatApp.getInstance().niceToast("匹配失败")
        }
    }

    /**
     * 匹配失败返回魔石
     */
    @Subscribe
    fun onRestoreMatchingStoneVM(obj: MatchingFailedViewModel.RestoreMatchingStoneVM) {
        if (obj.isSuccess) {
            MainActivity.startOpenCall(mActivity)
        }
    }

    /**
     * 更新用户信息
     */
    @Subscribe
    fun onUserInfoVM(obj: UserInfoViewModel.EditUserVM) {
        DataBindingUtils.loadAvatar((mActivity as MainActivity).mBinding.avatarIv, mDataRepository.getUserInfoEntity()?.avatar)
        (mActivity as MainActivity).mBinding.nicknameTv.text = mDataRepository.getUserInfoEntity()?.nickname
    }

    fun newCustomPopWindow(contentView: View?) {
        mPopWindow = CustomPopWindow.PopupWindowBuilder(mActivity)
                .setView(contentView)
                .setOutsideTouchable(true)
                .enableOutsideTouchableDissmiss(false)
                .setAnimationStyle(R.style.DialogChatHowEnterExitAnimation)
                .size(DeviceUtils.getScreenWidth(mActivity) - DisplayUtils.dp2px(mActivity, 30f), DisplayUtils.dp2px(mActivity, 300F))
                .create()
                .showAsDropDown(mBinding.ivRenwuTitle, DisplayUtils.dp2px(mActivity, 10f), DisplayUtils.dp2px(mActivity, 10F))
    }

    fun showPopNotificationsEnable() {//提示用户开启通知
        val contentView = View.inflate(mActivity, R.layout.pop_notifaction, null)
        handleNotifyLogic(contentView)//点击事件
        newCustomPopWindow(contentView)
    }

    private fun handleNotifyLogic(contentView: View?) {
        val listener = View.OnClickListener { v ->
            if (mPopWindow != null) {
                mPopWindow!!.dissmiss()
            }
            when (v.id) {
                R.id.open_btn -> {
                    goToNotificationSettings(mActivity)
                }
            }
        }
        contentView?.findViewById<AppCompatButton>(R.id.open_btn)?.setOnClickListener(listener)
        contentView?.findViewById<ImageView>(R.id.close_tv)?.setOnClickListener(listener)
    }

    /**
     * 红包倒计时
     */
    @Subscribe
    fun onReadPacketVM(obj: OpenCallViewModel.ReadPacketVM) {
        if (obj.progress == 100.toLong()) {
            mBinding.readPacketContentTv.text = obj.content
            mBinding.readPacketContentTv.textSize = 16f
            mBinding.progressBarCcpb.progress = 100f
            mBinding.getRedPacketTv.visibility = View.VISIBLE
            mBinding.getRedPacketTv.visibility = View.VISIBLE
            //打开红包
            mBinding.readPacketContentTv.setOnClickListener {
                if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                    mActivity.niceToast("当前无可用网络")
                    return@setOnClickListener
                }
                mViewModel.getRedPacket()?.let {
                    getRedPacketPop(it.giftId.toString(), mBinding.model2?.openedNum?.toInt())
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_home_openpacket",""))
            }
        } else {
            mBinding.readPacketContentTv.text = obj.content
            mBinding.readPacketContentTv.textSize = 14f
            mBinding.progressBarCcpb.progress = progressTime(obj.count, obj.progress)
            mBinding.getRedPacketTv.visibility = View.INVISIBLE
            mBinding.readPacketContentTv.setOnClickListener {
                if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                    mActivity.niceToast("当前无可用网络")
                    return@setOnClickListener
                }
            }
            Timber.tag("Progress").i(
                    "count=${obj.count} progress=${obj.progress} ${progressTime(obj.count, obj.progress)}")
        }
    }

    /**
     * 首次登录,新手福利.界面关闭
     */
    @Subscribe
    fun onNewbieTaskResult2(obj: NewbieTaskResult2) {
        mViewModel.getRedPackageList()
    }

    /**
     * 从微信分享成功回调
     */
    @Subscribe
    fun onShareStatusVM(obj: OpenCallViewModel.ShareStatusVM) {
        if (obj.success) {
            isShareSuccess = true
        }
        mViewModel.getRedPackageList()
    }

    /**
     * 三连开红包领取
     */
    @Subscribe
    fun onGetGiftOkVM(obj: RedPacketViewModel3.GetGiftOkVM) {

        //刷新红包列表
        mViewModel.getRedPackageList()
    }

    /**
     * 刷新红包列表
     */
    @Subscribe
    fun onRedSubscribeVM(obj: OpenCallViewModel.RedPackageVM) {
        //初始化进度条
        mBinding.progressBarCcpb.progress = 0f

        obj.obj?.forEach {
            if (it.availableTime.toLong() > 0) {

                if (it.isBigPackage == 1 && isShareSuccess) {//三连开红包
                    isShareSuccess = false
                    mViewModel.mCompositeDisposable2.clear()
                    if (isBoxClose) {
                        mBinding.redPacketShareTv.visibility = View.GONE
                        mBinding.redPacketCl.visibility = View.INVISIBLE
                    } else {
                        mBinding.redPacketShareTv.visibility = View.VISIBLE
                        mBinding.redPacketCl.visibility = View.VISIBLE
                    }
                    mBinding.readPacketContentTv.text = "打开"
                    mBinding.progressBarCcpb.progress = 100f
                    mBinding.model2 = it
                    (mActivity as MainActivity).mBinding.model2 = it
                    mViewModel.setRedPacket(it)
                    mBinding.readPacketContentTv.setOnClickListener { view ->
                        if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                            mActivity.niceToast("当前无可用网络")
                            return@setOnClickListener
                        }
                        getRedPacketsPop(it.giftId.toString())
                    }
                } else {//普通红包,进行倒计时
                    val availableTime = it.availableTime.toLong()
                    mBinding.redPacketShareTv.visibility = View.INVISIBLE
                    mBinding.model2 = it
                    (mActivity as MainActivity).mBinding.model2 = it
                    mViewModel.setRedPacket(it)
                    mViewModel.mCompositeDisposable2.clear()
                    //倒计时
                    getMainActivity()?.mViewModel?.countRedPacket(availableTime,
                            System.currentTimeMillis() / 1000 - availableTime,
                            it.giftId.toString())

                }
            }
        }


//        //引导页
//        if (!mViewModel.getGuideStatus()) {
//            guide()
//            mViewModel.saveGuideStatus()
//        }

    }

    @Subscribe
    fun onRedPacketOpenVM(obj: OpenCallViewModel.RedPacketOpenVM) {
        if (!isInitRedBigOpen) {
            isInitRedBigOpen = !isInitRedBigOpen
        } else {
            if (mBinding.redPacketCl.visibility == View.INVISIBLE) {
                if (isBoxClose) {
                    mBinding.redPacketCl.visibility = View.INVISIBLE
                } else {
                    mBinding.redPacketCl.visibility = View.VISIBLE
                }
            }
        }
    }

    @Subscribe
    fun onRedPacketCloseVM(obj: OpenCallViewModel.RedPacketCloseVM) {
        if (mBinding.redPacketCl.visibility == View.VISIBLE) {
            mBinding.redPacketCl.visibility = View.INVISIBLE
        }
    }

    @Subscribe
    fun onNoticeMsg(obj: NoticeMsgResult) {
        mViewModel.netDynamicsMsg()
        mViewModel.netNewMsg()
    }

    /**
     * 动态未读消息  (好友圈 未读消息(自己的动态由好友评论点赞de未读消息)同步)
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

    /**
     * 动态未读消息
     */
    @Subscribe
    fun onUnReadMsg(obj: OpenCallViewModel.UnReadMsgVM) {
        if (obj.success) {
//            size3 = obj.obj.unReadCount?:0
            if (obj.obj.unReadCount != 0) {
                (mActivity as MainActivity).mBinding.ivUnMsg.visibility = View.VISIBLE
            } else {
                (mActivity as MainActivity).mBinding.ivUnMsg.visibility = View.GONE
            }
//            setMessageNumber()
        } else {
            (mActivity as MainActivity).mBinding.ivUnMsg.visibility = View.GONE
        }
    }

    @Subscribe
    fun onMainUnReadMsg(obj: OpenCallViewModel.DynamicsMsgVM) {
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


    @Subscribe
    fun startChat(obj: TaskStartChatEvent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            callOngoingWithPermissionCheck()
        } else {
            callOngoing()
        }

    }

    @Subscribe
    fun startTextChat(obj: TaskStartTextChatEvent) {
        mActivity.runOnUiThread {
            MainActivity.isVoiceCall = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callOngoingWithPermissionCheck()
            } else {
                callOngoing()
            }
        }
    }

    @Subscribe
    fun openRedBox(obj: TaskBoxEvent) {
        if (isBoxClose) {
            mBinding.redPacketCl.visibility = View.INVISIBLE
        } else {
            mBinding.redPacketCl.visibility = View.VISIBLE
        }
    }

    @Subscribe
    fun goChat(obj: TaskChatEvent) {
        (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
    }

    @Subscribe
    fun goChat(obj: TaskChat2Event) {
        (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
        //EventBus.getDefault().post(TaskStartSquareEvent())
    }


    /**
     * 首页任务列表
     */
    @Subscribe
    fun onTaskList(obj: OpenCallViewModel.TaskListData) {
        data = obj.obj!!.data!!
        if (currentTask == null) {
            currentTask = data!![0]
        } else {
            if (data!!.size - 1 > nextIndex) {
                currentTask = data!![nextIndex]
            } else {
                nextIndex = 0
            }
        }
        setTaskData()
    }

    fun setTaskData() {
        if (currentTask != null) {
            var stringBuilder = StringBuffer()



            when {
                currentTask!!.taskStatus == 0 -> {
                    // 未完成
                    mBinding.tvTaskStatus.text = "去完成"
                }
                currentTask!!.taskStatus == 1 -> {
                    // 已完成
                    mBinding.tvTaskStatus.text = "可领取"
                }
                else -> {
                    // 已完成
                    mBinding.tvTaskStatus.text = "已入账"
                }
            }


            stringBuilder.append(currentTask!!.taskContent + "\n奖励：")
            if (java.lang.Double.parseDouble(currentTask!!.taskCash) > 0) {
                stringBuilder.append(currentTask!!.taskCash + "现金 ")
            }
            if (java.lang.Double.parseDouble(currentTask!!.taskCoin) > 0) {
                stringBuilder.append(currentTask!!.taskCoin + "金币 ")
            }
            if (java.lang.Double.parseDouble(currentTask!!.taskStone) > 0) {
                stringBuilder.append(currentTask!!.taskStone + "魔石 ")
            }
            mBinding.tvTaskText.text = stringBuilder.toString()

            mBinding.tvTaskStatus.visibility = View.VISIBLE
            mBinding.tvTaskStatus.setOnClickListener {
                if (!DeviceUtils.isConn(mActivity)) {//没有报错信息，直接崩溃，干脆直接加判断无网络状态下，不可点击
                    mActivity.niceToast("当前无可用网络")
                    return@setOnClickListener
                }
                if (currentTask!!.taskStatus == 0) {
                    goTask(currentTask!!.clickTarget)
                } else if (currentTask!!.taskStatus == 1) {
                    EventBus.getDefault().post(ReceiveTaskEvent(currentTask!!.taskId))
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_home_task_goto",currentTask!!.taskId.toString()))
            }
        }
    }


    private fun goTask(clickTarget: String?) {
        when (clickTarget) {
            TaskClickType.NONE -> {
//                Toast.makeText(activity, TaskClickType.NONE + " 无", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.MATCHING -> {
                EventBus.getDefault().post(TaskStartChatEvent())
            }
            TaskClickType.BOX -> {
                EventBus.getDefault().post(TaskBoxEvent())
            }
            TaskClickType.BANK -> {
                val netUrl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                H5Activity.start(mActivity, netUrl, "社交银行", true)
            }
            TaskClickType.MAIL -> {
                EventBus.getDefault().post(TaskChatEvent())
//                Toast.makeText(activity, TaskClickType.MAIL + " 一对一", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.WALLET -> {
                // 进入我的钱包
                WalletActivity.start(this!!.activity!!)
            }
            TaskClickType.INVITE_PINK -> {
                InviteNewActivity2.start(activity!!)
            }
            TaskClickType.SCENE -> {
            }
            TaskClickType.INVITE_GAME -> {
                // 小游戏
                InviteGameActivity.start(this!!.activity!!)
            }
        }
    }

    private var currentTask: CurrentTaskListResult.Data? = null
    private var nextIndex = 0
}