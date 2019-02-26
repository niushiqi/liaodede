package com.dyyj.idd.chatmore.base

import android.Manifest
import android.app.ProgressDialog
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper
import com.afollestad.materialdialogs.MaterialDialog
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.ActivityBaseBinding
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.mqtt.result.*
import com.dyyj.idd.chatmore.ui.dialog.activity.*
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.main.fragment.CallFragment
import com.dyyj.idd.chatmore.ui.user.activity.LoginActivity
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import com.dyyj.idd.chatmore.viewmodel.CallOutgoingViewModel
import com.dyyj.idd.chatmore.viewmodel.VideoRequestPopViewModel
import com.dyyj.idd.chatmore.weiget.pop.IncomingRequestPop
import com.gt.common.gtchat.extension.getExtMap
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.othershe.nicedialog.NiceDialog
import com.othershe.nicedialog.ViewConvertListener
import com.umeng.analytics.MobclickAgent
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_base.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/02/05
 * desc   : Activity基类(绑定ViewDataBinding)
 */
@RuntimePermissions
abstract class BaseActivity<D : ViewDataBinding, out VM : ViewModel> : AppCompatActivity() {
    val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
    var mProgressDialog: ProgressDialog? = null
    lateinit var mBinding: D
    lateinit var mBaseBinding: ActivityBaseBinding
    val mViewModel: VM by lazy { onViewModel() }
    private val mViewModelTag: Int?  by lazy { onViewModelTag() }
    var mToolbar: Toolbar? = null
    private var mLastVisibleItemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG","simpleName="+javaClass.simpleName)
        ActManagerUtils.getAppManager().addActivity(this)
        initContentView()
        mToolbar = onToolbar()
        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
        }
        onBindViewModel(mViewModelTag, mViewModel)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

//    if (!LinphoneService.isReady()) {
//      finish()
//      startService(intent.setClass(this, LinphoneService::class.java))
//      return
//    }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
        ActManagerUtils.getAppManager().addActivityVisible(this)
        mViewModel.sendViewEventTrackingMessage("sw_" + this.javaClass.simpleName + "_in")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
        ActManagerUtils.getAppManager().finishActivityVisible(this)
        mViewModel.sendViewEventTrackingMessage("sw_" + this.javaClass.simpleName + "_out")
    }

    /**
     * 设置Activity @setContentView
     * 根布局套单个子Activity布局,使用统一状态
     * Loding/Error/Empty
     */
    private fun initContentView() {
        //根布局
        mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null,
                false)

        //child Activity传递上传的布局
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), onLayoutId(), null, false)
        // content
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mBinding.root.layoutParams = params
        mBaseBinding.root.container.addView(mBinding.root)
        window.setContentView(mBaseBinding.root)
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroy() {
        super.onDestroy()
        //翻译ViewModel资源
        mViewModel.destroy()
        closeProgressDialog()
        ActManagerUtils.getAppManager().finishActivity(this)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 绑定ViewModel
     */
    private fun onBindViewModel(tag: Int?, viewmodel: ViewModel) {
        if (tag != null) {
            mBinding.setVariable(tag, viewmodel)
        }
    }

    /**
     * 打开等待条
     */
    fun showProgressDialog() {
        showProgressDialog("Loading...")
    }

    /**
     * 打开等待条
     */
    fun showProgressDialog(message: String) {
        runOnUiThread {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                return@runOnUiThread
            }

            mProgressDialog = ProgressDialog(this)
            // 进度条为水平旋转
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            // 设置点击返回不能取消
            mProgressDialog!!.setCancelable(true)
            //设置触摸对话框以外的区域不会消失
            mProgressDialog!!.setCanceledOnTouchOutside(false)

            mProgressDialog!!.setMessage(message)
            mProgressDialog!!.show()
        }
    }

    /**
     * 关闭等待条
     */
    fun closeProgressDialog() {
        runOnUiThread {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
            }
        }
    }


    /**
     * 设置页面状态
     * 空页面
     * 能用异常页面
     * 加载中页面
     * 正常页面
     * 网络异常页面
     */
    fun setStatusUI(status: Status) {
        mBinding.root.visibility = View.GONE
        when (status) {
            BaseActivity.Status.Empty -> {
                mBaseBinding.root.findViewById<View>(R.id.ll_empty).visibility = View.VISIBLE
            }
            BaseActivity.Status.Error -> {
                mBaseBinding.root.findViewById<View>(R.id.ll_error_refresh).visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.layout_error_network).visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.ll_progress_bar).visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.ll_empty).visibility = View.GONE
            }
            BaseActivity.Status.Loading -> {
                mBaseBinding.root.findViewById<View>(R.id.ll_progress_bar).visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.ll_empty).visibility = View.GONE
                mBaseBinding.root.findViewById<View>(R.id.ll_error_refresh).visibility = View.GONE
                mBaseBinding.root.findViewById<View>(R.id.layout_error_network).visibility = View.GONE
            }

            BaseActivity.Status.Network -> {
                mBaseBinding.root.findViewById<View>(R.id.layout_error_network).visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.ll_progress_bar).visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.ll_empty).visibility = View.GONE
                mBaseBinding.root.findViewById<View>(R.id.ll_error_refresh).visibility = View.GONE
            }
            Status.Normal -> {
                mBinding.root.visibility = View.VISIBLE
                mBaseBinding.root.findViewById<View>(R.id.ll_empty).visibility = View.GONE
                mBaseBinding.root.findViewById<View>(R.id.ll_error_refresh).visibility = View.GONE
                mBaseBinding.root.findViewById<View>(R.id.ll_progress_bar).visibility = View.GONE
                mBaseBinding.root.findViewById<View>(R.id.layout_error_network).visibility = View.GONE
            }
        }
    }


    override fun onBackPressed() {
        if (onBackPressedSuper()) {
            super.onBackPressed()
        } else {
            BGAKeyboardUtil.closeKeyboard(this)
            this.finish()
            BGASwipeBackHelper.executeBackwardAnim(this)
        }
    }

    open fun onBackPressedSuper(): Boolean {
        return false
    }

    open fun onDrawerLayout(): DrawerLayout? = null

    fun onCreateEvenbus(any: Any) {
        if (!EventBus.getDefault().isRegistered(any)) {
            EventBus.getDefault().register(any)
        }
    }

    fun onDestryEvenbus(any: Any) {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(any)
        }
    }

    /**
     * 设置字体
     */
    fun setTextFont(textView: TextView) {
        val type = Typeface.createFromAsset(assets, "fonts/font1.ttf")
        textView.typeface = type
    }

    /**
     * Activity 布局文件
     */
    @LayoutRes
    abstract fun onLayoutId(): Int

    /**
     * 获取Toolbar
     */
    open fun onToolbar(): Toolbar? {
        return null
    }

    /**
     * 获取ViewModel类
     */
    abstract fun onViewModel(): VM

    /**
     * ViewModel类Tag
     */
    open fun onViewModelTag(): Int? {
        return null
    }

    /**
     * 界面状态
     */
    enum class Status {
        /**
         * 空页面
         */
        Empty,

        /**
         * 通用异常
         */
        Error,

        /**
         * 加载中
         */
        Loading,

        /**
         * 正常
         */
        Normal,

        /**
         * 网络异常
         */
        Network
    }

    /**
     * 固定奖励
     */
    @Subscribe
    fun onSubscribeResult(obj: CommonAwardResult) {

        if (isCureentActivity()) {
            CommonAwardActivity.start(this, obj.rewardCoin, obj.rewardStone, obj.rewardCash, obj.rewardId)
        }
    }

    /**
     * 首次聊天奖励
     */
    @Subscribe
    fun onSubscribeResult(obj: FirstChatResult) {

    }

    /**
     * 挂断后获取奖励
     */
    @Subscribe
    fun onSubscribeResult(obj: HangUpResult) {
        if (isCureentActivity()) {
            HangUpActivity.start(this, stone = obj.rewardStone, gold = obj.rewardCoin,
                    money = obj.rewardCash)
        }
    }

    /**
     * 等级提升奖励
     */
    @Subscribe
    fun onSubscribeResult(obj: LevelUpgradeResult) {
        if (isCureentActivity()) {
            val desc = if (obj.message!!.line3 == null) "" else obj.message!!.line3!!
            val value = if (obj.message!!.line2 == null) "" else obj.message!!.line2!!
            val title: String = if (obj.message!!.line1 == null) "" else obj.message!!.line1!!
            LevelUpgradeActivity.start(context = this, level = obj.level.toString(), title = title,
                    desc = desc, value = value)
        }
    }

    /**
     * 匹配失败
     */
    @Subscribe
    fun onSubscribeResult(obj: MatchingFailedResult) {
        if (isCureentActivity()) {
            MatchingFailedActivity.start(this)
        }
    }

    /**
     * 新手任务
     */
    @Subscribe
    fun onSubscribeResult(obj: NewbieTaskResult2) {
        if (isCureentActivity()) {
            NewbieTaskActivity.start(this, title = obj.title!!, gold = obj.rewardCoin,
                    stone = obj.rewardStone, money = obj.rewardCash,
                    rewardId = obj.rewardId)
        }
    }

    @Subscribe
    fun onSubscribeResult(obj: SignTaskResult) {
        if (isCureentActivity()) {
            SignTaskActivity.start(this, obj.rewardCoin, obj.rewardStone, obj.rewardCash, obj.rewardId)
        }
    }

    @Subscribe
    fun onSubscribeResult(obj: VideoRequestPopViewModel.VideoRequestOk) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchVideoWithPermissionCheck(obj)
        } else {
            switchVideo(obj)
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun switchVideo(obj: VideoRequestPopViewModel.VideoRequestOk) {
        val subscribe = mDataRepository.responseSwitchVideo(obj.talkId, obj.fromUserId, obj.toUserId,
                "1").subscribe({})
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    /**
     * 被举报
     */
    @Subscribe
    fun onUserReportResult(obj: UserReportResult) {
        if (isCureentActivity()) {
            //举报人和被举报人都会下发mqtt，需要判断
            if(mDataRepository.getUserid() != obj.fromUser) {
                MaterialDialog.Builder(this).title("提示").content("您的帐号被举报").positiveText("确定").show()
            }
        }
    }

    /**
     * 挂断电话
     */
    @Subscribe
    fun onTalkHangupResult(obj: TalkHangupResult) {
        if (isCureentActivity()) {
            mDataRepository.endCall()
            //记录系统消息
            mDataRepository.saveSystemMessage()
        }
    }

    /**
     * 强制注销
     */
    @Subscribe
    fun onOfflineResult(obj: OfflineResult) {
        if (isCureentActivity()) {
            mDataRepository.cleanAccountInfo()
            ActManagerUtils.getAppManager().finishActivity()
            ActManagerUtils.getAppManager().finishActivityVisible()
            LoginActivity.start(this)
        }
    }

    /**
     * 匹配通话中请求切换视频
     */
    @Subscribe
    fun onMatchingRequestSwitchVideoResult(obj: MatchingRequestSwitchVideoResult) {
        if (isCureentActivity()) {

        }
    }

    /**
     * 匹配通话中响应切换视频
     */
    @Subscribe
    fun onMatchingResponseSwitchVideoResult(obj: MatchingResponseSwitchVideoResult) {
        if (isCureentActivity()) {

        }
    }

    @Subscribe
    fun onAvatarErrorVM(obj: AvatarFailerVM) {
        if (isCureentActivity()) {
            UploadAvatarActivity.start(this@BaseActivity)
        }
    }

    /**
     * 主叫方，收到mqtt推送，开始匹配推送
     */
    @Subscribe
    fun onTalkMatchingResult(obj: TalkMatchingResult) {
        //if (isCureentActivity() && (obj.fromUserId == mDataRepository.getUserid())) {
        if (isCureentActivity()) {
            Timber.tag("niushiqi-pipei").i("主叫mqtt talkid" + obj.talkId
                    + " from:" + obj.fromUserId + " to:" + obj.toUserId)
            /*ChatApp.getInstance().niceToast("主叫mqtt talkid"+ obj.talkId
                    + " from:" + obj.fromUserId + " to:" + obj.toUserId)*/
            obj.talkId.let {
                //回传
                val subscribe = mDataRepository.postReportMatchingPush(it.toString()).filter {
                    if (it.errorCode != 200) {
                        MatchingFailedActivity.start(this)
                    }
                    it.errorCode == 200
                }.filter {
                    mDataRepository.getToUserid(obj.fromUserId, obj.toUserId) != null
                }.flatMap {
                    mDataRepository.getMatchingUserBaseInfo(mDataRepository.getToUserid(obj.fromUserId, obj.toUserId)!!)
                }.subscribe({
                    EventBus.getDefault().post(CallOutgoingViewModel.StartMatchingVM(it, obj.talkId.toString(), obj.fromUserId, obj.toUserId, it.errorCode == 200))
                }, {
                    EventBus.getDefault().post(CallOutgoingViewModel.StartMatchingVM(talkId = obj.talkId.toString(), fromid = obj.fromUserId, toid = obj.toUserId, isSuccess = true))
                    Timber.tag("MQTT").e(it.message)
                })
                mViewModel.mCompositeDisposable.add(subscribe)
                //设置talkid，和1对1通话共用一个talkid
                mDataRepository.setChatCallInfo(
                        if (mDataRepository.getUserid() == obj.fromUserId) obj.toUserId!! else obj.fromUserId!!,
                        it.toString(), StatusTag.ATTRIBUTE_CALL)
            }
        }/* else {
            Timber.tag("niushiqi-pipei").i("被叫拒绝mqtt talkid"+ obj.talkId
                    + " from:" + obj.fromUserId + " to:" + obj.toUserId)
            ChatApp.getInstance().niceToast("被叫拒绝mqtt talkid"+ obj.talkId
                    + " from:" + obj.fromUserId + " to:" + obj.toUserId)
        }*/
    }

    /**
     * 2019.1.17 匹配流程修改
     * 被叫方，收到环信呼叫，开始匹配推送
     */
    /*@Subscribe
    fun onTalkMatchingResultFromB(obj: TalkMatchingResultFromB) {
        Timber.tag("niushiqi-pipei").i("被叫假mqtt talkid"+ obj.talkId
                + " from:" + obj.fromUserId + " to:" + obj.toUserId)
        /*ChatApp.getInstance().niceToast("被叫假mqtt talkid"+ obj.talkId
                + " from:" + obj.fromUserId + " to:" + obj.toUserId)*/
        if (isCureentActivity()) {
            obj.talkId.let {
                //回传
                val subscribe = mDataRepository.postReportMatchingPush(it.toString()).filter {
                    if (it.errorCode != 200) {
                        MatchingFailedActivity.start(this)
                    }
                    it.errorCode == 200
                }.filter {
                    mDataRepository.getToUserid(obj.fromUserId, obj.toUserId) != null
                }.flatMap {
                    mDataRepository.getMatchingUserBaseInfo(mDataRepository.getToUserid(obj.fromUserId, obj.toUserId)!!)
                }.subscribe({
                    EventBus.getDefault().post(CallOutgoingViewModel.StartMatchingVM(it, obj.talkId.toString(), obj.fromUserId, obj.toUserId, it.errorCode == 200))
                }, {
                    EventBus.getDefault().post(CallOutgoingViewModel.StartMatchingVM(talkId = obj.talkId.toString(), fromid = obj.fromUserId, toid = obj.toUserId, isSuccess = true))
                    Timber.tag("MQTT").e(it.message)
                })
                mViewModel.mCompositeDisposable.add(subscribe)
                //设置talkid，和1对1通话共用一个talkid
                mDataRepository.setChatCallInfo(
                        if (mDataRepository.getUserid() == obj.fromUserId) obj.toUserId!! else obj.fromUserId!!,
                        it.toString(), StatusTag.ATTRIBUTE_CALL)
            }
        }
    }*/

    /**
     * 视频内容违章
     */
    @Subscribe
    fun onSlytherinFrameHangupResult(obj: SlytherinFrameHangupResult) {
        if (isCureentActivity()) {
            obj.talkId.let {
                Flowable.just("").map {
                    Timber.tag("mqtt").i("视频内容违章")
                    mDataRepository.endCall()
                }.subscribeOn(Schedulers.io()).subscribe({}, {})

            }
        }
    }

    /**
     * 环信透传
     */
    @Subscribe
    fun onMutableList(list: MutableList<EMMessage>) {

        Timber.tag("niushiqi-tonghua").i("重要bug打印： 进入的activity： ("+this.javaClass.simpleName+")")

        if (!isCureentActivityVisible()) {
            return
        }

        list.forEach {
            val fromUserid = it.getExtMap("from_userid")
            val toUserid = it.getExtMap("to_userid")
            val type = it.getExtMap("voice_type")
            val nickname = it.getExtMap("from_nickname")
            val avatar = it.getExtMap("from_avatar")
            val chat_time = it.getExtMap("chat_time")
            val fromType = it.getExtMap("fromType")

            (it.body as? EMCmdMessageBody)?.let { body ->
                when (body.action()) {
                    StatusTag.STATUS_CONNECTING -> {
                        Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 收到1对1语音请求")
                        //判断对方是否忙线
                        //if(ActManagerUtils.getAppManager().currentActivityVisible()!!::class.java == ConnetingActivity::class.java) {
                        //2018.12.4日过渡版本-通话建立成功直接开启树洞
                        if(ActManagerUtils.getAppManager().currentActivityVisible()!!::class.java == MainActivity::class.java) {
                            if((ActManagerUtils.getAppManager().currentActivityVisible() as MainActivity).mContent == CallFragment.instance()) {
                                //处于树洞匹配界面，即处于忙线
                                mViewModel.sendBusyMessage(fromUserid!!, type!!, avatar!!, nickname!!, fromType!!)
                                //显示未读提示
                                var message = mDataRepository.createVoiceCmdMessage(fromUserid!!, StatusTag.TYPE_VOICE, avatar!!,
                                        nickname!!, toContent = "忙线未接听", isCallingParty = false, fromType = fromType!!)
                                mViewModel.insertReceivedMessage(message)
                                return
                            }
                        }

                        runOnUiThread {
                            //打开来电界面
                            mViewModel.mPop = IncomingRequestPop(this)
                            mViewModel.mPop?.let {
                                it.initData(fromUserId = fromUserid!!, toUserId = toUserid!!,
                                        username = nickname!!, type = type!!, avatar = avatar!!, fromType = fromType!!)
                                it.initView()
                                it.show(mBinding.root)
                            }
                        }
                    }
                    StatusTag.STATUS_CANCEL -> {
                        //1对1通话：A/B取消，B/A的处理逻辑
                        Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方已经取消")
                        runOnUiThread {
                            if (mViewModel.mPop != null) {
                                mViewModel.mPop?.dismiss()
                                mViewModel.mPop = null
                            }
                        }
                        //添加未打开聊天框取消的特殊判断
                        //if (ActManagerUtils.getAppManager().currentActivityVisible()!!::class.java != ChatActivity::class.java) {
                        //显示未读提示
                        var message = mDataRepository.createVoiceCmdMessage(fromUserid!!, StatusTag.TYPE_VOICE, avatar!!,
                                nickname!!, toContent = "对方已取消", isCallingParty = false, fromType = fromType!!)
                        mViewModel.insertReceivedMessage(message)
                        //}
                    }
                    else -> {
                        Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 收到others："+body.action())
                    }
                }
            }
        }
    }

    /**
     * 备选池被叫(树洞中业务来电)
     */
    @Subscribe
    fun onTalkMatchingBackupResult(obj: TalkMatchingBackupResult) {
        niceToast("备选池")
    }

    /**
     * 接通呼叫奖励
     */
    @Subscribe
    fun onRewardTalkMatchingBackupResult(obj: RewardTalkMatchingBackupResult) {
        if (isCureentActivity()) {

//      CallIncomingDialogActivity.start(this, "10162", "10162")
            ToastConnectAwardActivity.start(this, obj.rewardTitle
                    ?: "接通呼叫奖励", obj.rewardCoin?.toDouble(),
                    obj.rewardStone?.toDouble(), obj.rewardCash?.toDouble())
        }
    }


    //var show: NiceDialog? = null

    /**
     * 长连接  连接超时
     */
    /*@Subscribe
    open fun onOfflineResult(obj: String) {
        Log.e("zhangwj", "BaseActiviity  obj.toString()="+obj.toString())
        if (obj.equals("BufferedSinkNullPointerException")) {
            niceToast("BufferedSink-空指针异常")
            reLogin()
        }else if (obj.equals("BufferedSinkIOException")) {//代表重新连接，去掉
            niceToast("BufferedSink-IO异常")
            reLogin()
        }else if (obj.equals("BufferedSinkIllegalStateException")) {//代表重新连接，去掉
            niceToast("BufferedSink-非法状态异常")
            reLogin()
        }else if (obj.equals("BufferedException")) {//代表重新连接，去掉
            niceToast("BufferedSink-最大异常")
            reLogin()
        }

        //EventTrackingUtils.joinPoint(EventBeans("sw_reregisterpage", ""))
        *//*else if (obj.equals("socket-send-success")) {//代表重新连接，去掉
            if (show != null) {
                show?.dismiss()
            }
        }*//*

    }*/

    fun reLogin() {
        NiceDialog.init()
                .setLayoutId(R.layout.let_user_log_out_layout)
                .setConvertListener(ViewConvertListener { viewHolder, baseNiceDialog ->
                    viewHolder.setOnClickListener(R.id.tv_iknow) {
                        baseNiceDialog.dismiss()
                        //niceToast("我知道啦")
                        if (isCureentActivity()) {
                            mDataRepository.cleanAccountInfo()
                            ActManagerUtils.getAppManager().finishActivity()
                            ActManagerUtils.getAppManager().finishActivityVisible()
                            LoginActivity.start(this)
                        }
                        //EventTrackingUtils.joinPoint(EventBeans("ck_reregister", ""))
                    }
                })
                .setOutCancel(false)
                .setWidth(250)
                .show(getSupportFragmentManager())
    }



    /**
     * 备选池状态重置
     */
//    @Subscribe
//    fun onMatchingBackupResetStatus(obj: MatchingBackupResetStatus) {
//        EasemobManager.isStartMatch = false
//    }

    /**
     * 当前是否
     */
    fun isCureentActivity() = ActManagerUtils.getAppManager().currentActivity() == this

    /**
     * 判断activity是否是当前显示的activity
     */
    fun isCureentActivityVisible() = ActManagerUtils.getAppManager().currentActivityVisible() == this

    class AvatarFailerVM()

    //class TalkMatchingResultFromB(val talkId:Int, val fromUserId:String, val toUserId:String)
}