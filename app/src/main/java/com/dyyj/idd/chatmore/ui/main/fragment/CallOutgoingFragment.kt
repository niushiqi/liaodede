package com.dyyj.idd.chatmore.ui.main.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentCallOutgoingBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.easemob.EasemobManager
import com.dyyj.idd.chatmore.model.mqtt.MqttTag
import com.dyyj.idd.chatmore.model.mqtt.params.MatchingBackupCancel
import com.dyyj.idd.chatmore.model.mqtt.result.MatchingFailedResult2
import com.dyyj.idd.chatmore.model.network.result.DanmuResult
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult
import com.dyyj.idd.chatmore.ui.adapter.CallOutgoingBannerAdapter
import com.dyyj.idd.chatmore.ui.dialog.activity.MatchingFailedActivity
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.ui.user.activity.ShopActivity
import com.dyyj.idd.chatmore.utils.AnimationUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.CallOutgoingViewModel
import com.dyyj.idd.chatmore.viewmodel.CancelMatchingViewModel
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import com.google.gson.Gson
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.chat.EMCallStateChangeListener
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.EMServiceNotReadyException
import com.othershe.nicedialog.NiceDialog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.util.IOUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

@SuppressLint("ValidFragment")
/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/19
 * desc   : 匹配中界面(匹配中)
 */
class CallOutgoingFragment : BaseFragment<FragmentCallOutgoingBinding, CallOutgoingViewModel>(), MyFrameAnimation.OnFrameAnimationListener {
    override fun onEnd() {
        mBinding.moshiIv.visibility = View.GONE
    }

    override fun onStartFrameAnimation() {
        mBinding.moshiIv.visibility = View.VISIBLE
    }

    var mDanmakuView: IDanmakuView? = null
    //  private var mCall: LinphoneCall? = null
    var touserid: String? = null
    var fromuserid: String? = null
    var player: MediaPlayer? = null

    private val colorList = arrayListOf(Color.WHITE, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY,
            Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.RED,
            Color.WHITE, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY,
            Color.GREEN)
    private val colorSize = arrayListOf(10, 11, 12, 13, 14, 15)

    private val damaskContext = DanmakuContext.create()
    /*inner class MyHandler(holder: CallOutgoingFragment) : Handler(Looper.getMainLooper()) {
        private val mHolder: WeakReference<CallOutgoingFragment> = WeakReference(holder)

        override fun handleMessage(message: Message) {
            val holder = mHolder.get()
            if (holder != null && message.what == 1) {
                if (!EasemobManager.getCallingState) {
                    mActivity.niceToast("对方无接听")
                    // 超時操作
                }else{
                    mActivity.niceToast("对方有接听")
                }
                mActivity.niceToast("AAA")
                ChatApp.getInstance().getDataRepository().endCall()
//                ?EventBus.getDefault().post(CallOutgoingViewModel.MatchingFailer())
                mActivity.niceToast("Window")
                MatchingFailedActivity.start(mActivity)

            } else if (holder != null && message.what == 2) {
                mActivity.niceToast("对方无呼叫")
                ChatApp.getInstance().getDataRepository().endCall()
                MatchingFailedActivity.start(mActivity)
//                EventBus.getDefault().post(CallOutgoingViewModel.MatchingFailer())
            }
        }
    }*/


    companion object {
        var mInstance: CallOutgoingFragment? = null
        var talkId: String? = null

        /**
         * 单例
         */
        fun instance(): CallOutgoingFragment {
            if (mInstance == null) {
                return CallOutgoingFragment()
            }
            return mInstance!!
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_call_outgoing
    }

    override fun onViewModel(): CallOutgoingViewModel {
        return CallOutgoingViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateEvenBus(this)
        //DanmuUtils.init(mActivity, damaskContext, mBinding.svDanmaku, mCacheStufferAdapter, null)要你何用？
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        mInstance = this
        initListener()
        startMatching()
        //mViewModel.netDanmu(mDataRepository.getUserid()!!)//要你何用？
        mBinding.wvLoading.loadUrl("file:///android_asset/matching_anim.html")
        var webSettings = mBinding.wvLoading.getSettings()
        webSettings.setJavaScriptEnabled(true)
        Handler().postDelayed({ mBinding.wvLoading.visibility = View.VISIBLE }, 1000)
        mBinding.bannerLayout.setAdapter(CallOutgoingBannerAdapter(MainActivity.isVoiceCall))
        mBinding.bannerLayout.setAutoPlaying(true)
        if (OpenCallFragment.matchButtonEntity != null) {
            if (!TextUtils.isEmpty(OpenCallFragment.matchButtonEntity!!.data.textTip.timeOutButton)) {
                mBinding.btnStartTextChat.text =  OpenCallFragment.matchButtonEntity!!.data.textTip.timeOutButton
            }
            if (!TextUtils.isEmpty(OpenCallFragment.matchButtonEntity!!.data.voiceTip.timeOutButton)) {
                mBinding.btnContinueVoice.text = OpenCallFragment.matchButtonEntity!!.data.voiceTip.timeOutButton
            }
        }
    }

    fun startPlayMusic() {
        try {
            if (player != null) {
                player!!.release()
                player = null
            }
            //player = MediaPlayer.create(mActivity, R.raw.call_music)//文件删除后，要注释，否则空引用导致编译失败
            player!!.isLooping = true
            player!!.setOnPreparedListener {
                player!!.start()
            }
            player!!.prepareAsync()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startMatching() {
        mBinding.tvCollectStatus2.visibility = View.GONE
        mBinding.clConnectFail.visibility = View.GONE
        mBinding.tvCollectStatus.visibility = View.VISIBLE
//        mBinding.svDanmaku.visibility = View.VISIBLE
        mBinding.tvMatchFail.visibility = View.GONE
        mBinding.bannerLayout.visibility = View.VISIBLE
        mBinding.wvLoading.visibility = View.VISIBLE
        //startPlayMusic()
        if (MainActivity.isVoiceCall) {
            mViewModel.netStartMatching()
        } else {
            mViewModel.startTextMatching()
        }
    }

    @Subscribe
    fun onRestartMatchVM(obj: MatchingFailedActivity.MatchRestartVM) {
        mViewModel.netStartMatching()
    }

    private fun getMainActivity() = (mActivity as? MainActivity)


    /**
     * 初始化点击事件
     */
    private fun initListener() {
        mBinding.ivClose.setOnClickListener {
            //调用取消匹配的接口来取消后端匹配池
//            mViewModel.netCancelMatching()
            if (mBinding.clConnectFail.visibility == View.VISIBLE || mBinding.bannerLayout.visibility != View.VISIBLE) {

                MainActivity.startOpenCall(mActivity)
                return@setOnClickListener
            } else {
                MatchingFailedActivity.start(mActivity)

                //推送mqtt,匹配取消事件
                ChatApp.getInstance().getMQTT().pubMsg("/ldd/message/$touserid", Gson().toJson(
                        MatchingBackupCancel().apply {
                            messageType = MqttTag.SUBSCRIBE_MATCHING_BACKUP_CANCEL
                            data = MatchingBackupCancel.Data().apply {
                                content = "真可惜，ta被抢走了~\n跟其他的小哥哥/小姐姐 开启 聊天"
                            }
                        }), MqttTag.MQTT_QOS)

                EventTrackingUtils.joinPoint(EventBeans("ck_treepage_cancel", ""))
                return@setOnClickListener
            }
            MainActivity.startOpenCall(mActivity)
        }

        EMClient.getInstance().callManager().addCallStateChangeListener { callState, _ ->
            when (callState) {
                EMCallStateChangeListener.CallState.ACCEPTED -> {
//                    mActivity.niceToast("1: " + fromuserid + "/" + touserid)
                    MainActivity.startCallOut(mActivity, fromuserid ?: "0", touserid ?: "0")
                    Timber.tag("niushiqi-tonghua").i("进入call页面 fromuserid："+fromuserid+" touserid:"+touserid)
                }
            }
        }
        mBinding.btnStartTextChat.setOnClickListener {
            MainActivity.isVoiceCall = false
            startMatching()
            AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi10", 1, 40, 30, this)
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_timeout_rematchtext", ""))
        }
        mBinding.btnContinueVoice.setOnClickListener {
            MainActivity.isVoiceCall = true
            startMatching()
            AnimationUtils.start(mActivity, mBinding.moshiIv, "xiahaomoshi", 1, 40, 30, this)
            EventTrackingUtils.joinPoint(EventBeans("ck_treepage_timeout_rematchvoice", ""))
        }
    }//initListener

    private fun createSpannable(drawable: Drawable): SpannableStringBuilder {
        val text = "bitmap"
        val spannableStringBuilder = SpannableStringBuilder(text)
        val span = ImageSpan(drawable)//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.append("图文混排")
        spannableStringBuilder.setSpan(BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0,
                spannableStringBuilder.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return spannableStringBuilder
    }

    override fun onPause() {
        super.onPause()
        //暂停
        mDanmakuView?.let {
            if (it.isPrepared) {
                it.pause()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //继续
        mDanmakuView?.let {
            if (it.isPrepared && it.isPaused) {
                it.resume()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //startTime()
    }

    override fun onStop() {
        super.onStop()
        subscribe ?: subscribe?.dispose()
    }

    override fun onDestroyView() {
        Timber.tag("niushiqi-tonghua").i("calloutgoing页面关闭")
        super.onDestroyView()
        if (mViewModel.subscribe != null) {
            Timber.tag("niushiqi-tonghua").i("销毁60s定时器")
            mViewModel.subscribe?.dispose()
        }
        onDestryEvenBus(this)
        if (mDanmakuView != null) {
            // dont forget release!CustomPopWindow
            mDanmakuView?.release()
            mDanmakuView = null
        }
        if (player != null) {
            player!!.release()
        }
//        mHandler.removeCallbacksAndMessages(null)
    }

    @Subscribe
    fun onEventMessage(msg: EventMessage<Any>) {
        if (msg.tag.equals(EventConstant.TAG.TAG_CALLOUTGOING_FRAGMENT)) {
            when (msg.what) {
                EventConstant.WHAT.WHAT_TEXT_MATCHING -> {
                    val obj = msg.obj as StartMatchingResult
                    var subscribe = Observable.timer(5, TimeUnit.SECONDS).subscribe({
                        MainActivity.startOpenCall(mActivity)
                        (mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
                        val b = Bundle()
                        b.putString("friendshipLevel", obj.data!!.matchingUserLevel.toString())
                        b.putInt("fromType", 2)
                        b.putBoolean("isFromMatch", true)
                        ChatActivity.start(mActivity, obj.data!!.matchingUserId!!, obj.data.matchingUserNickname, obj.data.matchingUserAvatar, b)
                    })
                    mViewModel.mCompositeDisposable.add(subscribe)
                }
                EventConstant.WHAT.WHAT_TXT_MATCH_FAIL -> MainActivity.startOpenCall(mActivity)
            }
        }

    }

    /**
     * 环信手打电话异常,进行超时处理
     */
    @Subscribe
    fun onEasemobError(error: EMServiceNotReadyException) {
        mViewModel.reportBackupMatchingResult(3)
    }


    @Subscribe
    fun onCancelMatchingVM(obj: CancelMatchingViewModel.CancelMatchingVM) {
        if (obj.isSuccess) {
//      decline()
            MainActivity.startOpenCall(mActivity)
        }
    }

    var mMatchsubscribe: Disposable? = null


//    private val mHandler = MyHandler(this)
//
//    private inner class MyHandler(holder: CallOutgoingFragment) : Handler() {
//        private val mHolder: WeakReference<CallOutgoingFragment> = WeakReference(holder)
//
//        override fun handleMessage(message: Message) {
//            val holder = mHolder.get()
//            if (holder != null && message.what == 1) {
//                if (!EasemobManager.getCallingState) {
//                    // 超時操作
//                    ChatApp.getInstance().getDataRepository().endCall()
//                    startOpenCall(mActivity)
//                    MatchingFailedActivity.start(mActivity)
//                }
//            } else if (holder != null && message.what == 2) {
//                if (!EasemobManager.isComing) {
//                    ChatApp.getInstance().getDataRepository().endCall()
//                    startOpenCall(mActivity)
//                    MatchingFailedActivity.start(mActivity)
//                }
//            }
//        }
//    }

    @Subscribe
    fun onViewModel(vm: CallOutgoingViewModel.StartMatchingVM) {
        Timber.tag("niushiqi-pipei").i("处理匹配流程 talkid"+ vm.talkId
                + " from:" + vm.fromid + " to:" + vm.toid + " success:" + vm.isSuccess)
        if (vm.isSuccess and !EasemobManager.isStartMatch) {//拨号
            Log.e(ChatApp.LOGSTR, "talkmatchingresult_11")
            vm.obj?.data?.matchingUserId?.let {
                getMainActivity()?.mViewModel?.setStartMatchingResult(vm.obj.data)
                touserid = vm.toid
                talkId = vm.talkId
                fromuserid = vm.fromid
                EasemobManager.isStartMatch = true

                //开始定时
                mViewModel.timber(mActivity)
                if (vm.fromid == mDataRepository.getUserid()) {
                    Log.e(ChatApp.LOGSTR, "talkmatchingresult_13")
                    mActivity.niceToast("建立通话中")
                    Log.e("hun", "开始拨号")
                    //拨号
                    mViewModel.callOutgoing(vm.toid)
                    //匹配成功
                    EventBus.getDefault().post(CallOutgoingViewModel.MatchSuccess())//消耗魔石
                    mMatchsubscribe?.dispose()
                    //mViewModel.mCompositeDisposable.add(subscribe!!)
                } else {
                    //mActivity.niceToast("B方建立通话")
                    mDataRepository.setEasemobStatus(mActivity)
                    mMatchsubscribe?.dispose()
                    //mViewModel.mCompositeDisposable.add(subscribe!!)
                }
                return
            }
        }
        //匹配失败
        Log.e(ChatApp.LOGSTR, "vm.isSuccess=" + vm.isSuccess + "EasemobManager.isStartMatch=" + EasemobManager.isStartMatch)
        ChatApp.getInstance().getDataRepository().reportMatchingFailed(ChatApp.getInstance().getDataRepository().getUserid()!!, talkId!!)
    }

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
                .setHeight(300)     //dialog高度（单位：dp），默认为WRAP_CONTENT
                .setOutCancel(false)     //点击dialog外是否可取消，默认true
                //                .setAnimStyle(R.style.EnterExitAnimation)//设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
                .show(mActivity.getSupportFragmentManager())     //显示dialog
    }

    @Subscribe
    fun onConsumeMatschingVM(obj: CallOutgoingViewModel.TimeNotEnough?) {//判断魔石和次数是否足够
        if (obj?.obj == 37001) {
            mActivity.niceToast("今日随缘匹配次数已用尽，试试开启聊天吧")
            MainActivity.startOpenCall(mActivity)
        }
    }

    /**
     * 从新加入匹配池
     */
    fun joinPipei() {
        EasemobManager.isStartMatch = false
        MainActivity.startOutgoing(activity!!.applicationContext!!)
        EventBus.getDefault().post(MatchingFailedActivity.MatchRestartVM())
    }

    @Subscribe
    fun onMatchingBackFailed(vm: MatchingFailedResult2) {
        if (vm.talkId == mDataRepository.getTalkId()) {
            MatchingFailedActivity.start(mActivity)
        }
    }

    /**
     * 匹配失败
     */
    @Subscribe
    fun onViewModel(vm: CallOutgoingViewModel.MatchingFailer) {
        mViewModel.sendViewEventTrackingMessage("sw_treepage_nomatche")
        mViewModel.matchingTimeOut()
        matchFaildViewSet()
    }

    fun matchFaildViewSet(){
        mActivity.runOnUiThread {
            mBinding.clConnectFail.visibility = View.VISIBLE
            mBinding.rlTip.visibility = View.VISIBLE
            if (OpenCallFragment.textMatchTimes > 0) {
                mBinding.btnStartTextChat.visibility = View.VISIBLE
                mBinding.btnStartTextChat.isClickable = true
                mBinding.tvTextCantMatch.visibility = View.GONE
            } else {
                mBinding.btnStartTextChat.visibility = View.INVISIBLE
                mBinding.btnStartTextChat.isClickable = false
                mBinding.tvTextCantMatch.visibility = View.VISIBLE
            }
            // TODO 换成另一个控件
            mBinding.tvCollectStatus.visibility = View.GONE
            mBinding.tvCollectStatus2.visibility = View.VISIBLE
//        mBinding.svDanmaku.visibility = View.INVISIBLE
            mBinding.tvMatchFail.visibility = View.VISIBLE
            mBinding.bannerLayout.visibility = View.GONE
            mBinding.wvLoading.visibility = View.INVISIBLE
            //if (!TextUtils.isEmpty(OpenCallFragment.timeOutTip)) mBinding.tvTimeoutTip.text = OpenCallFragment.timeOutTip
            //if (player != null && player!!.isPlaying) player!!.stop()//暂时注掉
        }
    }

    /**
     * 匹配超时
     */
    @Subscribe
    fun onMatchingTimeOut(obj: CallOutgoingViewModel.MatchingTimeOut) {
        //mViewModel.matchingTimeOut()//godie
        OpenCallFragment.textMatchTimes = obj.obj.data!!.remainTimes!!.toInt()
        mBinding.tvTimeoutTip.text = obj.obj.data.tip
        //调用取消匹配的接口来取消后端匹配池
        mViewModel.netCancelMatching()
        mViewModel.endCall()
        matchFaildViewSet()
    }

    /**
     * 魔石不足
     */
    @Subscribe
    fun onViewModel(vm: CallOutgoingViewModel.MatchingFailerBecauseNotEnoughMagicStone) {
        //Log.i("aaa","拉起魔石不足弹窗")
        mActivity.niceToast("魔石不足了，快去领红包拿魔石！")
        MainActivity.startOpenCall(mActivity)
        //magicStoneNotEnough()
        /*handler.postDelayed(Runnable {
            magicStoneNotEnough()
        }, 500)*/
    }

    fun insertDanmu(baseline: Int, data: DanmuResult.Data) {
        data.barrage?.forEachIndexed { index, danmu -> addDanma(damaskContext, index, danmu, baseline) }
    }

    /**
     * 获取弹幕
     */
    /*@Subscribe
    fun onDanmuCallVM(obj: CallOutgoingViewModel.DanmuCallVM) = if (obj.success) {
        insertDanmu(0, obj.obj!!)
    } else {

    }*/

    private val mCacheStufferAdapter = object : BaseCacheStuffer.Proxy() {

        private var mDrawable: Drawable? = null

        override fun prepareDrawing(danmaku: BaseDanmaku, fromWorkerThread: Boolean) {
            if (danmaku.text is Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                object : Thread() {

                    override fun run() {
                        val url = "http://www.bilibili.com/favicon.ico"
                        var inputStream: InputStream? = null
                        var drawable = mDrawable
                        if (drawable == null) {
                            try {
                                val urlConnection = URL(url).openConnection()
                                inputStream = urlConnection.getInputStream()
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap")
                                mDrawable = drawable
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                IOUtils.closeQuietly(inputStream)
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100)
                            val spannable = createSpannable(drawable)
                            danmaku.text = spannable
                            if (mDanmakuView != null) {
                                mDanmakuView?.invalidateDanmaku(danmaku, false)
                            }
                            return
                        }
                    }
                }.start()
            }
        }

        override fun releaseResource(danmaku: BaseDanmaku) {
        }
    }

    private fun addDanma(daemonContext: DanmakuContext, index: Int, danmu: DanmuResult.Data.Danmu,
                         baseline: Int) {
        val item = daemonContext.mDanmakuFactory.createDanmaku(1, daemonContext) ?: return
        if (!mBinding.svDanmaku.isPrepared) {
            Log.e("danmu", "no-prepared")
            return
        }
        val randomIndex = (Math.random() * 15).toInt()
        val randomColor = (Math.random() * 15).toInt()
        val randomSize = (Math.random() * 6).toInt()
        val secTime = ((randomIndex + baseline) * 1000).toLong()
        item.time = secTime
        item.textSize = colorSize[randomSize] * (resources.displayMetrics.density)
        item.textColor = colorList[randomColor]
        item.textShadowColor = Color.BLACK
        item.text = danmu.barrageContent
        mBinding.svDanmaku.addDanmaku(item)
    }

    private var subscribe: Disposable? = null

    /*fun startTime() {
        subscribe = Flowable.interval(15, 15, TimeUnit.SECONDS).observeOn(
                AndroidSchedulers.mainThread()).subscribe({
            insertDanmu((it.toInt() + 1) * 15,
                    mViewModel.danmuData!!)
        }, {})
        CompositeDisposable().add(subscribe!!)
    }*/

    /**
     * 2019.1.17 匹配流程修改
     * 被叫方接收到环信呼叫后开启原MQTT流程
     */
    /*@Subscribe
    fun onReceiveEasemobCallingForPiPeiVM(obj: CallOutgoingViewModel.ReceiveEasemobCallingForPiPeiVM) {
        Timber.tag("niushiqi-pipei").i("开始请求talkid from:"+ obj.fromUserId + " to:" + obj.toUserId)
        mDataRepository.getUnknownTalkId(obj.fromUserId).subscribe({
            it.data?.talkId?.let {
                EventBus.getDefault().post(BaseActivity.TalkMatchingResultFromB(it.toInt(), obj.fromUserId, obj.toUserId))
            }
        }, {})
    }*/
}