package com.dyyj.idd.chatmore.ui.task.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.base.BaseChatAdapterV2
import com.dyyj.idd.chatmore.databinding.ActivityChatBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventGiftMessage
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.listener.AcceptListener
import com.dyyj.idd.chatmore.listener.RejectListener
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.ImageMessage
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.mqtt.result.FriendExperienceResult2
import com.dyyj.idd.chatmore.model.mqtt.result.FriendshipRewardResult
import com.dyyj.idd.chatmore.model.mqtt.result.RangAwardResult
import com.dyyj.idd.chatmore.model.mqtt.result.SquareMessageTaskRewardResult
import com.dyyj.idd.chatmore.model.network.result.*
import com.dyyj.idd.chatmore.ui.adapter.EmotionGridViewAdapter
import com.dyyj.idd.chatmore.ui.adapter.EmotionPagerAdapter
import com.dyyj.idd.chatmore.ui.dialog.activity.NewbieTaskActivity
import com.dyyj.idd.chatmore.ui.dialog.activity.RangAwardActivityV2
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.main.activity.ConnetingActivity
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.model.BoxUi
import com.dyyj.idd.chatmore.ui.user.fragment.SendGiftFragmentInChat
import com.dyyj.idd.chatmore.ui.wallet.fragment.BuyProudPeasFragment
import com.dyyj.idd.chatmore.utils.*
import com.dyyj.idd.chatmore.viewmodel.*
import com.dyyj.idd.chatmore.weiget.dialog.ChatEndDialog
import com.dyyj.idd.chatmore.weiget.dialog.ChatExitDialog
import com.dyyj.idd.chatmore.weiget.dialog.DontLikeDialog
import com.dyyj.idd.chatmore.weiget.dialog.TimeOutDialog
import com.example.zhouwei.library.CustomPopWindow
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.flexbox.FlexboxLayout
import com.gt.common.gtchat.extension.getExtMap
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.zhihu.matisse.Matisse
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 聊天界面
 */
class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>(), TextWatcher, EMMessageListener, AcceptListener, RejectListener, View.OnClickListener {

    var mForContent: Fragment? = null

    private var mPopWindow: CustomPopWindow? = null

    private var nowCountDownTime: Long = -1L
    private val boxList1 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENED), BoxUi(this, BoxUi.BOX_PREOPEN),
            BoxUi(this, BoxUi.BOX_UNOPEN), BoxUi(this, BoxUi.BOX_UNOPEN))
    private val boxChangeList1 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENING),
            BoxUi(this, BoxUi.BOX_UNOPEN),
            BoxUi(this, BoxUi.BOX_UNOPEN))

    private val boxList2 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENED), BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_PREOPEN), BoxUi(this, BoxUi.BOX_UNOPEN))

    private val boxChangeList2 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENING),
            BoxUi(this, BoxUi.BOX_UNOPEN))

    private val boxList3 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENED), BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENED), BoxUi(this, BoxUi.BOX_PREOPEN))

    private val boxChangeList3 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENED),
            BoxUi(this, BoxUi.BOX_OPENING))
    private val boxList4 = arrayListOf(BoxUi(this, BoxUi.BOX_PREOPEN), BoxUi(this, BoxUi.BOX_UNOPEN),
            BoxUi(this, BoxUi.BOX_UNOPEN), BoxUi(this, BoxUi.BOX_UNOPEN))

    private val boxChangeList4 = arrayListOf(BoxUi(this, BoxUi.BOX_OPENING),
            BoxUi(this, BoxUi.BOX_UNOPEN),
            BoxUi(this, BoxUi.BOX_UNOPEN),
            BoxUi(this, BoxUi.BOX_UNOPEN))
    private val matchTime: Long = System.currentTimeMillis()

    companion object {
        const val USERID = "userid"
        const val NICKNAME = "nickname"
        const val AVATAR = "avatar"
        const val BUNDLE = "bundle"
        const val FROM_TYPE_VOICE = 1
        const val FROM_TYPE_TEXT = 2
        const val FROM_TYPE_INVITE = 3
        const val FROM_TYPE_SQUARE = 4

        var mBundle : Bundle? = null
        //1:语音 2:文字 3:广场
        var fromType : Int = 1
        var isFriend : Boolean = true
        var isFromMatch: Boolean = false
        var countDownTime: Long = -1L

        var squareTopics: List<String>? = listOf()

        fun start(context: Context, toUserid: String, nickname: String?, avatar: String?, bundle: Bundle?) {
            val intent = Intent(context, ChatActivity::class.java)
            mBundle = bundle
            if (mBundle != null) {
                fromType = mBundle!!.getInt("fromType",1)
                isFromMatch = mBundle!!.getBoolean("isFromMatch",false)
                isFriend = mBundle!!.getBoolean("isFriend",false)
            }
            intent.putExtra(USERID, toUserid)
            intent.putExtra(NICKNAME, nickname)
            intent.putExtra(AVATAR, avatar)
            intent.putExtra(BUNDLE, bundle)
            context.startActivity(intent)
        }

        //由广场页面开启的聊天
        fun startBySquare(context: Context, toUserid: String, nickname: String?, avatar: String?, topics: List<String>?) {
            val intent = Intent(context, ChatActivity::class.java)
            fromType = FROM_TYPE_SQUARE
            squareTopics = topics
            intent.putExtra(USERID, toUserid)
            intent.putExtra(NICKNAME, nickname)
            intent.putExtra(AVATAR, avatar)
            context.startActivity(intent)
            Timber.tag("niushiqi-task").i("startBySquare " + System.currentTimeMillis())
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun onViewModel(): ChatViewModel {
        return ChatViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    private fun getUserid() = intent.getStringExtra(USERID)

    private fun getToNickName() = intent.getStringExtra(NICKNAME)

    private fun getToAvatar() = intent.getStringExtra(AVATAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEvenbus(this)
        initListener()
        initToobar()
        initData()
        initSystemMessage()
        initEmotion()
    }

    /**
     * 初始化系统消息
     */
    private fun initSystemMessage() {
        if (getToUserid() == "1") {
            findViewById<View>(R.id.layout_progress_box0).visibility = View.GONE
            findViewById<View>(R.id.chat_add_iv).visibility = View.GONE
            findViewById<View>(R.id.right_iv).visibility = View.GONE
        }
    }

    override fun onDestroy() {
        mViewModel.exitMessageMatching(getToUserid())
        super.onDestroy()
        onDestryEvenbus(this)

        EMClient.getInstance().chatManager().removeMessageListener(this)
    }


    @SuppressLint("SetTextI18n")
    private fun initProgress(obj: FriendExperienceResult) {
        val data = obj.data ?: return
        val scheduleTv = findViewById<TextView>(R.id.schedule_tv)
        val levelTv = findViewById<TextView>(R.id.level_tv)
        val boxRl1 = findViewById<RelativeLayout>(R.id.box_rl1)
        val boxRl2 = findViewById<RelativeLayout>(R.id.box_rl2)
        val boxRl3 = findViewById<RelativeLayout>(R.id.box_rl3)
        val boxRl4 = findViewById<RelativeLayout>(R.id.box_rl4)
        val progress = findViewById<LottieAnimationView>(R.id.loading_lav)

        val boxView = arrayListOf<RelativeLayout>(boxRl1, boxRl2, boxRl3, boxRl4)
//    data.friendshipBox = 3
//    data.friendshipExperience = "70.5"

//    data.friendshipExperience = "66.0"
        val p = calculateProgress(data.friendshipBox?.toFloat(), data.friendshipExperience?.toFloat(),
                4)
        Timber.tag("OkHttp").i("$p")
        val df = DecimalFormat("0.00")
        val min = df.format(p!! - 0.01f)
        val max = df.format(p + 0.01f)
        progress.setMinAndMaxProgress(min.toFloat(), max.toFloat())

        progress.setMinProgress(p)

        val oldObj = mViewModel.getFriendExperienceResult(data.friendUserId!!)

        val subscribe0 = Flowable.interval(0, 3, TimeUnit.SECONDS).take(1).observeOn(
                AndroidSchedulers.mainThread()).subscribe({ count ->
            data.friendshipBox?.let {
                //一对一聊天奖励
                data.friendshipLastRewardLevel?.let { friendshipLastRewardLevel ->
                    data.friendshipLevel?.let { friendshipLevel ->
                        if (friendshipLevel.isNotEmpty() && friendshipLastRewardLevel.isNotEmpty() && friendshipLevel != friendshipLastRewardLevel) {
                            data.friendshipReward?.let {
                                onFriendshipRewardResult(it)
                            }
                        }
                    }
                }
            }
        })
        mViewModel.mCompositeDisposable.add(subscribe0)

        val subscribe = Flowable.interval(0, 3, TimeUnit.SECONDS).take(2).observeOn(
                AndroidSchedulers.mainThread()).subscribe({ count ->
            data.friendshipBox?.let {

                //进度条奖励
                val boxCount = if (count == 0.toLong()) oldObj?.data?.friendshipBox else it
                Timber.tag("")
                when (boxCount) {
                    1 -> {
                        if (oldObj?.data?.friendshipBox == it || oldObj?.data?.friendshipBox == null) {
                            initBoxView(boxView, boxList1)
                        } else {
                            initBoxView(boxView, boxChangeList1)
                        }
                        mViewModel.saveFriendExperienceResult(obj)
                    }
                    2 -> {
                        if (oldObj?.data?.friendshipBox == it || oldObj?.data?.friendshipBox == null) {
                            initBoxView(boxView, boxList2)
                        } else {
                            initBoxView(boxView, boxChangeList2)
                        }
                        mViewModel.saveFriendExperienceResult(obj)
                    }
                    3 -> {
                        if (oldObj?.data?.friendshipBox == it || oldObj?.data?.friendshipBox == null) {
                            initBoxView(boxView, boxList3)
                        } else {
                            initBoxView(boxView, boxChangeList3)
                        }
                        mViewModel.saveFriendExperienceResult(obj)
                    }
//                                                            4 -> {
//                                                              if (oldObj?.data?.friendshipBox == it || oldObj?.data?.friendshipBox == null) {
//                                                                initBoxView(boxView, boxList4)
//                                                              } else {
//                                                                initBoxView(boxView, boxChangeList4)
//                                                              }
//                                                              mViewModel.saveFriendExperienceResult(
//                                                                  obj)
//                                                            }

                }

            }
        }, {})

        mViewModel.mCompositeDisposable.add(subscribe)

        scheduleTv.text = "x${obj.data?.friendshipRewardAddition}"
        levelTv.text = "F${obj.data?.friendshipLevel}"
        setTextFont(scheduleTv)
        setTextFont(levelTv)
    }

    /**
     * 计算
     */
    private fun calculateProgress(friendshipBox: Float?, friendshipExperience: Float?,
                                  total: Int): Float? {
        friendshipBox ?: return null
        friendshipExperience ?: return null
        val df = DecimalFormat("0.00")
        val experience = friendshipExperience / 100
        val tmp = (100 / (total - 1)).toFloat() / 100
        val progress = tmp * experience + tmp * ((friendshipBox % total) - 1)
        return if (progress < 0) 0.01f else progress
    }

    fun div(v1: Double, v2: Double, scale: Int): Double {
        if (scale < 0) {
            throw IllegalArgumentException("The scale must be a positive integer or zero")
        }
        val b1 = BigDecimal(java.lang.Double.toString(v1))
        val b2 = BigDecimal(java.lang.Double.toString(v2))
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    private fun initBoxView(list: List<RelativeLayout>, boxs: List<BoxUi>) {
        (0 until list.size).forEach { index ->
            list[index].removeAllViews()
            list[index].addView(boxs[index].getViewFromType())
        }
    }

    private fun initData() {
        when(fromType) {
            FROM_TYPE_SQUARE -> {
                //广场信箱
                mViewModel.getOnlineStatus(getToUserid())
                mViewModel.setFriendsAvatar(intent.getStringExtra(AVATAR), intent.getStringExtra(USERID))
                mViewModel.setUsername(getToUserid())
                mViewModel.setNickName(getToNickName())
                mViewModel.setAvatar(getToAvatar())
                if (getToUserid() != "1") {
                    mViewModel.timerFriendExperience(getToUserid())
                }
                mViewModel.clearUnreadMessage(intent.getStringExtra(USERID))
                mViewModel.netCheckRelation(getToUserid())

                mViewModel.loadMessageList()

                //非好友第一次广场进入执行
                squareTopics?.let {
                    //给对方发话题问候
                    mViewModel.sendSquareDefaultMessage(it)
                    //开始"1分钟内积极回复"任务
                    //mViewModel.sendSquareMessageTaskStartTime(getToUserid())
                    //initRecycleView(arrayListOf())
                }

                //获取 "1分钟内积极回复"任务-进度状态
                mViewModel.getSquareMessageTaskStatus(getToUserid())
            }
            else -> {
                //语音／文字匹配信箱
                mViewModel.getOnlineStatus(getToUserid())
                mViewModel.setFriendsAvatar(intent.getStringExtra(AVATAR), intent.getStringExtra(USERID))
                mViewModel.setUsername(getToUserid())
                mViewModel.setNickName(getToNickName())
                mViewModel.setAvatar(getToAvatar())
                if (getToUserid() != "1") {
                    mViewModel.timerFriendExperience(getToUserid())
                }
                mViewModel.clearUnreadMessage(intent.getStringExtra(USERID))
                mViewModel.netCheckRelation(getToUserid())

                //陌生人首次聊天给对方发一句话
                if (fromType == 2 && isFromMatch) {
                    mViewModel.sendTextMatchDefaultMessage(matchTime)
                }
                mViewModel.loadMessageList()
                if (!isFriend) {//是好友
                    mViewModel.getMessageEndTime(getToUserid())
                }
                if (!isFromMatch && isFriend) {
                    initRecycleView(arrayListOf())
                }
            }
        }
    }


    private fun initToobar() {
        when(fromType) {
            FROM_TYPE_SQUARE -> {
                //广场信箱
                mToolbar?.findViewById<ImageView>(R.id.right_iv)?.let {
                    it.setImageResource(R.drawable.btn_toobar_more_normal)
                    it.setOnClickListener(this)
                }
                if (isFriend) {
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_stranger_toolbar)?.visibility = View.GONE
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_friend_title)?.visibility = View.VISIBLE
                    mBinding.llStartVoiceAddFriend.visibility = View.GONE
                    mBinding.clFriendLevel.visibility = View.VISIBLE
                } else {
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_stranger_toolbar)?.visibility = View.VISIBLE
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_friend_title)?.visibility = View.GONE
                    mBinding.clFriendLevel.visibility = View.GONE
                    mBinding.llStartVoiceAddFriend.visibility = View.VISIBLE
                }
            }
            else -> {
                //语音／文字匹配信箱
                mToolbar?.findViewById<ImageView>(R.id.right_iv)?.let {
                    it.setImageResource(R.drawable.btn_toobar_more_normal)
                    it.setOnClickListener(this)
                }
                if (isFriend) {
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_stranger_toolbar)?.visibility = View.GONE
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_friend_title)?.visibility = View.VISIBLE
                    mBinding.llStartVoiceAddFriend.visibility = View.GONE
                    mBinding.clFriendLevel.visibility = View.VISIBLE
                    if (countDownSubscribe != null){
                        countDownSubscribe!!.dispose()
                    }
                } else {
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_stranger_toolbar)?.visibility = View.VISIBLE
                    mToolbar?.findViewById<RelativeLayout>(R.id.rl_friend_title)?.visibility = View.GONE
                    mBinding.clFriendLevel.visibility = View.GONE
                    mBinding.llStartVoiceAddFriend.visibility = View.VISIBLE
                }
            }
        }
    }
    private var countDownSubscribe: Disposable? = null
    private fun countDownTime(){
        if (fromType == 2 && !isFriend) {
            if (countDownTime == 0L) {
                mToolbar?.findViewById<TextView>(R.id.tv_toolbar_count_down)?.text = "0秒"
                return
            }
            countDownSubscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)
                    .take(countDownTime + 1)
                    .map{t -> countDownTime - t }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        nowCountDownTime = it
                        mToolbar?.findViewById<TextView>(R.id.tv_toolbar_count_down)?.text = "${nowCountDownTime}秒"
                        if (it == 100L) mViewModel.sendAddFriendAndStartVoiceMessage(1)
                        if (it == 60L) mViewModel.sendAddFriendAndStartVoiceMessage(2)
                        if (it == 15L) mViewModel.sendAddFriendAndStartVoiceMessage(3)
                        if (it == 20L) mViewModel.netCheckRelation(getToUserid())
                    },{},{
                        var dialog : ChatEndDialog = ChatEndDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener { finish() }})
            mViewModel.mCompositeDisposable.add(countDownSubscribe!!)
        }
    }

    /**
     * 替换功能模块的Fragment
     */
    fun addFragment(fragment: Fragment) {
        if (mForContent != fragment) {
            mForContent?.onDestroy()
            mForContent = fragment
            val transaction = supportFragmentManager.beginTransaction()
            /*transaction.setCustomAnimations(
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out,
                    R.anim.fragment_silde_up_in,
                    R.anim.fragment_silde_down_out)*/
            transaction.replace(R.id.more_fun_fragment_container, fragment, fragment::class.java.simpleName)
            try {
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

    /**
     * 初始化事件
     */
    private fun initListener() {
        when(fromType) {
            FROM_TYPE_SQUARE -> {
                //广场信箱
                //环信接收消息回调
                if (EMClient.getInstance().chatManager() != null) {
                    EMClient.getInstance().chatManager().addMessageListener(this)
                }
                //公共title
                mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
                    onBackPressed()
                }
                //好友title
                mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = intent.getStringExtra(NICKNAME)
                mToolbar?.findViewById<View>(R.id.friend_bank_entrance)?.setOnClickListener {
                    val neturl = "http://www.liaodede.com/buddyBank/friendBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&fid=${getToUserid()}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                    H5Activity.start(this, neturl, "社交银行", true)
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_bank",""))
                }

                //非好友title
                mToolbar?.findViewById<TextView>(R.id.toolbar_stranger_title_tv)?.text = intent.getStringExtra(NICKNAME)
                mToolbar?.findViewById<TextView>(R.id.tv_report)?.setOnClickListener{
                    DontLikeDialog(this@ChatActivity, getToUserid()).show()
                    EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_report",getToUserid()))
                }
                //加好友图标／好友等级图标
                /*if (isFriend) {
                    mBinding.llStartVoiceAddFriend.visibility = View.GONE
                } else {
                    mBinding.clFriendLevel.visibility = View.GONE
                }*/
                //好友度规则说明（点击好友度旁边问号）
                mBinding.imgFriendlinessQuestion.setOnClickListener(View.OnClickListener {ruleDescription()})
                //非好友语音通话／加好友按钮
                mBinding.btnStartVoice.setOnClickListener {
                    startVoiceTalk()
                    if(fromType == 1) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_voice",getToUserid()))
                    } else if(fromType == 2){
                        EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_voice_go1", getToUserid()))
                    }
                }
                mBinding.btnAddFriend.setOnClickListener {
                    mViewModel.addFriendFromSquare(getToUserid())
                    EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_addfriend1", getToUserid()))
                }

                var handler: Handler = Handler()
                mBinding.chatAddIv.setOnClickListener {
                    /*if ((countDownTime == 0L || nowCountDownTime == 0L) && !isFriend) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            mDataRepository.deleteFriendConversation(getToUserid())
                            finish()
                        }
                        return@setOnClickListener
                    }*/
                    if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                        if(mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                            mBinding.chatMoreEmotion.visibility = View.GONE
                        } else {
                            hideChatMoreView()
                        }
                    } else {
                        KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
                        handler.postDelayed(Runnable {
                            mBinding.chatMoreLl.visibility = View.VISIBLE
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                            mBinding.chatMoreEmotion.visibility = View.GONE
                        }, 50)
                    }
                    //送礼物页面可见时，关闭送礼物
                    mBinding.moreFunFragmentContainer.visibility = View.GONE
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_moreact",getToUserid()))
                }

                mBinding.chatEmotionOpenIv.setOnClickListener {
                    if (mBinding.chatMoreLl.visibility == View.GONE) {
                        if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                            mBinding.chatMoreEmotion.visibility = View.GONE
                            //关闭表情显示软件键盘
                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                            mBinding.chatContentEt.requestFocus()

                        } else {
                            KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
                            handler.postDelayed(Runnable {
                                mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_close)
                                mBinding.chatMoreEmotion.visibility = View.VISIBLE
                            }, 50)
                        }
                    } else {
                        if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            //关闭表情
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                            mBinding.chatMoreEmotion.visibility = View.GONE
                            mBinding.chatMoreLl.visibility = View.GONE
                        } else {
                            //打开表情
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_close)
                            mBinding.chatMoreEmotion.visibility = View.VISIBLE
                            mBinding.chatMoreLl.visibility = View.GONE
                        }
                    }
                    //送礼物页面可见时，关闭送礼物
                    mBinding.moreFunFragmentContainer.visibility = View.GONE
                }

                mBinding.chatContentEt.addTextChangedListener(this)
                mBinding.chatContentEt.setOnClickListener {
                    if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                        mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                        mBinding.chatMoreEmotion.visibility = View.GONE
                    }
                    if (mBinding.chatMoreLl.visibility == View.VISIBLE) {//隐藏功能
                        mBinding.chatMoreLl.visibility = View.GONE
                    }
                    if (mBinding.llSomeFunc.visibility == View.VISIBLE) {
                        mBinding.llSomeFunc.visibility = View.GONE
                    }
                    //送礼物页面可见时，关闭送礼物
                    mBinding.moreFunFragmentContainer.visibility = View.GONE
                }
                //mBinding.chatContentEt.requestFocus()
                //输入框获取焦点时隐藏
                mBinding.chatContentEt.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        mBinding.moreFunFragmentContainer.visibility = View.GONE
                        hideChatMoreView()
                        hideEmotionView()
                        if (mBinding.llSomeFunc.visibility == View.VISIBLE) {
                            mBinding.llSomeFunc.visibility = View.GONE
                        }
                    }
                }

                mBinding.chatSendBtn.setOnClickListener {
                    /*if ((countDownTime == 0L || nowCountDownTime == 0L) && !isFriend) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            mDataRepository.deleteFriendConversation(getToUserid())
                            finish()
                        }
                        return@setOnClickListener
                    }*/
                    mBinding.chatContentEt.text?.toString()?.let {

                        mViewModel.sendMessageBySquare(it)
                        mBinding.chatContentEt.setText("", null)
                    }
                }

                //关闭奖励
                mBinding.getTv.setOnClickListener {

                    mViewModel.mRewardId?.let {
                        //        mViewModel.getReward(it)
                        mViewModel.mFriendExperienceResult?.data?.let {
                            mViewModel.getFriendExperienceReward(it.friendUserId!!, it.friendshipLastRewardLevel!!)
                        }
                        closeAnim()
                    }
                }

                //语音
                mBinding.voiceBtn.setOnClickListener {
                    startVoiceTalk()
                    if(fromType == 1) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_voice",getToUserid()))
                    } else if(fromType == 2){
                        EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_voice_go", getToUserid()))
                    }
                }

//        //视频
//        mBinding.videoBtn.setOnClickListener {
//            mViewModel.sendConnetingMessage(getUserid(), StatusTag.TYPE_VIDEO,
//                    intent.getStringExtra(AVATAR),
//                    intent.getStringExtra(NICKNAME))
//            hideChatMoreView()
//            ConnetingActivity.start(this@ChatActivity, mViewModel.userid!!, getToUserid(),
//                    StatusTag.TYPE_VIDEO)
//        }

                //语音
                mBinding.voiceBtn.setOnClickListener {
                    startVoiceTalk()
                    if(fromType == 1) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_voice",getToUserid()))
                    } else if(fromType == 2){
                        EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_voice_go", getToUserid()))
                    }
                }

                mBinding.chatAddIv.setOnClickListener {
                    //隐藏更多按钮，显示送碎片
                    mBinding.chatAddIv.visibility = View.GONE
                    mBinding.debrisBtn.visibility = View.VISIBLE
                    mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                    if ((countDownTime == 0L || nowCountDownTime == 0L) && !isFriend) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            mDataRepository.deleteFriendConversation(getToUserid())
                            finish()
                        }
                        return@setOnClickListener
                    }
                    if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                        if(mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            mBinding.chatMoreEmotion.visibility = View.GONE
                        } else {
                            hideChatMoreView()
                        }
                    } else {
                        KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
                        handler.postDelayed(Runnable {
                            mBinding.chatMoreLl.visibility = View.VISIBLE
                            mBinding.chatMoreEmotion.visibility = View.GONE
                        }, 50)
                    }
                    //送礼物页面可见时，关闭送礼物
                    if (mBinding.moreFunFragmentContainer.visibility == View.VISIBLE) {
                        mBinding.moreFunFragmentContainer.visibility = View.GONE
                    }
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_moreact",getToUserid()))
                }

                //发图片
                mBinding.pictureBtn.setOnClickListener {
                    //发送消息
                    mViewModel.openPhoto(this@ChatActivity, 9)
                }

                //送礼物
                mBinding.giftBtn.setOnClickListener({
                    reSetAboutGiftStatus(true)
                    addFragment(SendGiftFragmentInChat.instance(getToUserid()))
                })

                //送碎片
                mBinding.debrisBtn.setOnClickListener({ Toast.makeText(this, "即将上线，敬请期待", Toast.LENGTH_SHORT).show() })

                //玩筛子
                mBinding.shaiziBtn.setOnClickListener({ Toast.makeText(this, "即将上线，敬请期待", Toast.LENGTH_SHORT).show() })

                //看电影
                mBinding.movieBtn.setOnClickListener({ Toast.makeText(this, "即将上线，敬请期待", Toast.LENGTH_SHORT).show() })

                //收起
                mBinding.putAwayBtn.setOnClickListener {
                    //隐藏送碎片，显示更多按钮
                    mBinding.chatAddIv.visibility = View.VISIBLE
                    mBinding.debrisBtn.visibility = View.GONE
                    mBinding.chatMoreLl.visibility = View.GONE
                }




            }
            else -> {   //语音／文字匹配信箱
                if (fromType == 2) {
                    if (isFriend) mBinding.llStartVoiceAddFriend.visibility = View.GONE else mBinding.clFriendLevel.visibility = View.GONE
                } else {
                    mBinding.llStartVoiceAddFriend.visibility = View.GONE
                }

                var handler: Handler = Handler()

                if (EMClient.getInstance().chatManager() != null) {
                    EMClient.getInstance().chatManager().addMessageListener(this)
                }
                mBinding.chatContentEt.addTextChangedListener(this)
                mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
                    if (isFriend) {onBackPressed()}
                    if (nowCountDownTime == 0L || countDownTime == 0L) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            if (mToolbar?.findViewById<TextView>(R.id.tv_toolbar_count_down)?.text!!.equals("0秒")){
                                mDataRepository.deleteFriendConversation(getToUserid())
                            }
                            finish()
                        }
                        return@setOnClickListener
                    }
                    if (fromType == 2) {
                        if (isFromMatch || !isFriend || nowCountDownTime > 0) {
                            var dialog = ChatExitDialog(this, getToUserid())
                            dialog.show()
                            return@setOnClickListener
                        }
                    }
                    onBackPressed()
                }
                mToolbar?.findViewById<View>(R.id.friend_bank_entrance)?.setOnClickListener {
                    //银行入口
                    //http://www.liaodede.com/buddyBank/friendBank.html?userId=1000000001&fid=1000020242&timestamp=11111&deviceId=aaaa&token=1234
//            val neturl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                    val neturl = "http://www.liaodede.com/buddyBank/friendBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&fid=${getToUserid()}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                    H5Activity.start(this, neturl, "社交银行", true)
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_bank",""))
                }
                mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = intent.getStringExtra(NICKNAME)
                mToolbar?.findViewById<TextView>(R.id.toolbar_stranger_title_tv)?.text = intent.getStringExtra(NICKNAME)
                mToolbar?.findViewById<TextView>(R.id.tv_report)?.setOnClickListener{
                    DontLikeDialog(this@ChatActivity, getToUserid()).show()
                    EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_report",getToUserid()))
                }



                mBinding.chatEmotionOpenIv.setOnClickListener {
                    if (mBinding.chatMoreLl.visibility == View.GONE) {
                        if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                            mBinding.chatMoreEmotion.visibility = View.GONE
                            //关闭表情显示软件键盘
                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                            mBinding.chatContentEt.requestFocus()

                        } else {
                            KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
                            handler.postDelayed(Runnable {
                                mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_close)
                                mBinding.chatMoreEmotion.visibility = View.VISIBLE
                            }, 50)
                        }
                    } else {
                        if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            //关闭表情
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                            mBinding.chatMoreEmotion.visibility = View.GONE
                            mBinding.chatMoreLl.visibility = View.GONE
                        } else {
                            //打开表情
                            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_close)
                            mBinding.chatMoreEmotion.visibility = View.VISIBLE
                            mBinding.chatMoreLl.visibility = View.GONE
                        }
                    }
                    //送礼物页面可见时，关闭送礼物
                    mBinding.moreFunFragmentContainer.visibility = View.GONE
                }

                mBinding.chatContentEt.setOnClickListener {//点击输入框
                    if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                        mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                        mBinding.chatMoreEmotion.visibility = View.GONE
                    }
                    if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                        mBinding.chatMoreLl.visibility = View.GONE
                    }
                    //送礼物页面可见时，关闭送礼物
                    mBinding.moreFunFragmentContainer.visibility = View.GONE
                }
//        mBinding.chatContentEt.requestFocus()
                //输入框获取焦点时隐藏
                mBinding.chatContentEt.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        mBinding.moreFunFragmentContainer.visibility = View.GONE
                        hideChatMoreView()
                        hideEmotionView()
                    }
                }

                //发送按钮
                mBinding.chatSendBtn.setOnClickListener {
                    mBinding.chatContentEt.text?.toString()?.let {

                        mViewModel.sendMessage(it, fromType, isFriend,matchTime)
                        mBinding.chatContentEt.setText("", null)
                    }
                    if ((countDownTime == 0L || nowCountDownTime == 0L) && !isFriend) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            mDataRepository.deleteFriendConversation(getToUserid())
                            finish()
                        }
                        return@setOnClickListener
                    }
                }

                //关闭奖励
                mBinding.getTv.setOnClickListener {

                    mViewModel.mRewardId?.let {
                        //        mViewModel.getReward(it)
                        mViewModel.mFriendExperienceResult?.data?.let {
                            mViewModel.getFriendExperienceReward(it.friendUserId!!, it.friendshipLastRewardLevel!!)
                        }
                        closeAnim()
                    }

                }

                    ////视频
                    //mBinding.videoBtn.setOnClickListener {
                    //    mViewModel.sendConnetingMessage(getUserid(), StatusTag.TYPE_VIDEO,
                    //            intent.getStringExtra(AVATAR),
                    //            intent.getStringExtra(NICKNAME))
                    //    hideChatMoreView()
                    //    ConnetingActivity.start(this@ChatActivity, mViewModel.userid!!, getToUserid(),
                    //            StatusTag.TYPE_VIDEO)
                    //}

                        //监听通话状态,接通成功打开通话界面
                        EMClient.getInstance().callManager().addCallStateChangeListener { callState, _ ->
                            /*when (callState) {
                                EMCallStateChangeListener.CallState.ACCEPTED -> {
                                    Timber.tag("niushiqi-linshi").i("chatactivity:accepted")
                                    if (mViewModel.userid != null && getToUserid() != null) {

                                        if (StatusTag.TYPE_VOICE == mViewModel.getCallType()) {
                                            MainActivity.startCallOut(this@ChatActivity, mViewModel.userid?:"0", getToUserid())
                                        }
                             //    else if (StatusTag.TYPE_VIDEO == mViewModel.getCallType()) {
                             //        MainActivity.startVideoOut(this@ChatActivity, mViewModel.userid!!, getToUserid(),
                             //                mViewModel.getTalkId()!!)
                             //    }

                                    }
                                }
                            }*/
                        }
                        //好友度规则说明（点击好友度旁边问号）
                        mBinding.imgFriendlinessQuestion.setOnClickListener(View.OnClickListener {ruleDescription()})

                        mBinding.btnAddFriend.setOnClickListener {
                            mViewModel.addFriendFromTextMatch(getToUserid())
                            EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_addfriend1", getToUserid()))
                        }

                        mBinding.btnStartVoice.setOnClickListener {
                            startVoiceTalk()
                            if(fromType == 1) {
                                EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_voice",getToUserid()))
                            } else if(fromType == 2){
                                EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_voice_go1", getToUserid()))
                            }
                        }


                //语音
                mBinding.voiceBtn.setOnClickListener {
                    startVoiceTalk()
                    if(fromType == 1) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_voice",getToUserid()))
                    } else if(fromType == 2){
                        EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_voice_go", getToUserid()))
                    }
                }

                mBinding.chatAddIv.setOnClickListener {
                    //隐藏更多按钮，显示送碎片
                    mBinding.chatAddIv.visibility = View.GONE
                    mBinding.debrisBtn.visibility = View.VISIBLE
                    mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                    if ((countDownTime == 0L || nowCountDownTime == 0L) && !isFriend) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            mDataRepository.deleteFriendConversation(getToUserid())
                            finish()
                        }
                        return@setOnClickListener
                    }
                    if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                        if(mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                            mBinding.chatMoreEmotion.visibility = View.GONE
                        } else {
                            hideChatMoreView()
                        }
                    } else {
                        KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
                        handler.postDelayed(Runnable {
                            mBinding.chatMoreLl.visibility = View.VISIBLE
                            mBinding.chatMoreEmotion.visibility = View.GONE
                        }, 50)
                    }
                    //送礼物页面可见时，关闭送礼物
                    if (mBinding.moreFunFragmentContainer.visibility == View.VISIBLE) {
                        mBinding.moreFunFragmentContainer.visibility = View.GONE
                    }
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_moreact",getToUserid()))
                }

                //发图片
                mBinding.pictureBtn.setOnClickListener {
                    //发送消息
                    mViewModel.openPhoto(this@ChatActivity, 9)
                }

                //送礼物
                mBinding.giftBtn.setOnClickListener({
                    reSetAboutGiftStatus(true)
                    addFragment(SendGiftFragmentInChat.instance(getToUserid()))
                })

                //送碎片
                mBinding.debrisBtn.setOnClickListener({ Toast.makeText(this, "即将上线，敬请期待", Toast.LENGTH_SHORT).show() })

                //玩筛子
                mBinding.shaiziBtn.setOnClickListener({ Toast.makeText(this, "即将上线，敬请期待", Toast.LENGTH_SHORT).show() })

                //看电影
                mBinding.movieBtn.setOnClickListener({ Toast.makeText(this, "即将上线，敬请期待", Toast.LENGTH_SHORT).show() })

                //收起
                mBinding.putAwayBtn.setOnClickListener {
                    //隐藏送碎片，显示更多按钮
                    mBinding.chatAddIv.visibility = View.VISIBLE
                    mBinding.debrisBtn.visibility = View.GONE
                    mBinding.chatMoreLl.visibility = View.GONE
                }

            }//非来自广场
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
            //隐藏其他功能模块
            mBinding.chatMoreLl.visibility = View.GONE
            KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)//关闭输入键盘
            mBinding.moreFunFragmentContainer.visibility = View.VISIBLE
        }else{
            mBinding.moreFunFragmentContainer.visibility = View.GONE
        }
    }


    /*override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            KeyboardUtil.closeInputKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }*/

    fun startVoiceTalk(){
        var isHasReceiveMsg: Boolean = false
        val list = mViewModel.getAdapter().getList()
        for (obj in list) {
            if (obj.item is EMMessage) {
                if ((obj.item as EMMessage).direct() == EMMessage.Direct.RECEIVE) {
                    isHasReceiveMsg = true
                    break
                }
            }
        }
        if (!isFriend && !isHasReceiveMsg) {
            niceToast("需要对方回复一条消息,才能发起语音哦~")
            return
        }

        mViewModel.sendConnetingMessage(getUserid(), StatusTag.TYPE_VOICE,
                intent.getStringExtra(AVATAR),
                intent.getStringExtra(NICKNAME),
                fromType.toString())
        hideChatMoreView()
        ConnetingActivity.start(this@ChatActivity, mViewModel.userid!!, getToUserid(),
                StatusTag.TYPE_VOICE, "true", fromType.toString())
        Timber.tag("niushiqi-tonghua").i("主角方开启信息：fromUserid："+mViewModel.userid
                +" toUserid"+getToUserid()
                +" type"+StatusTag.TYPE_VOICE)
    }

    //好友度规则说明
    fun ruleDescription() {
        //获取好友的等级
        val b = getIntent().getBundleExtra(BUNDLE)

        val builder = AlertDialog.Builder(this)
        val inflate = View.inflate(this, R.layout.friendliness_question_layout, null)
        val iKnow = inflate.findViewById<TextView>(R.id.i_know)
        val friendGrade = inflate.findViewById<TextView>(R.id.tv_friend_grade)
        if (b != null) {
            //Log.e("aaa",b.getString("friendUserLevel"))
            friendGrade.text = "当前好友等级 F"+b.getString("friendshipLevel")
        }else{
            friendGrade.text = "当前好友等级 F?"
        }
        builder.setView(inflate)
        val show = builder.show()
        iKnow.setOnClickListener { show.dismiss() }
        //给AlertDialog设置4个圆角
        show.window!!.setBackgroundDrawableResource(R.drawable.bg_white_radius)

        val wm = this
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width
        val height = wm.defaultDisplay.height

        val layoutParams = WindowManager.LayoutParams()
        //设置宽高，高度默认是自适应的，宽度根据屏幕宽度比例设置
        layoutParams.width = width / 10 * 6
        layoutParams.height = (height / 10 * 4)
        show.window!!.attributes = layoutParams
    }


    /**
     * 获取对方个人信息，再添加卡片
     */
    @Subscribe
    fun showUserInfoDetail2(obj: ChatViewModel.UserInfoDetailVM2) {
        mViewModel.getAdapter().getList().add(0, BaseChatAdapterV2.Wrapper(MessageHeadEntity(fromType, getToAvatar(), getToNickName(), obj.obj?.userBaseInfo?.age, isFriend)))
        mViewModel.getAdapter().notifyDataSetChanged()
    }

    private fun initRecycleView(list: List<EMMessage>) = when(fromType) {
        FROM_TYPE_SQUARE -> {
            //广场信箱
            val layoutManager = LinearLayoutManager(this)
            //设置线
            mViewModel.getAdapter().setLayoutManager(layoutManager)
            mViewModel.getAdapter().initChatList(list)
            //mViewModel.getAdapter().getList().add(0, BaseChatAdapterV2.Wrapper(SquareMessageHeadEntity(fromType, getToAvatar(), getToNickName(), false)))
            EventBus.getDefault().post(ChatViewModel.UpdateMessageVM(false))
            mViewModel.getAdapter().setListener(this)
            mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
            mBinding.recyclerview.layoutManager = layoutManager
            mBinding.recyclerview.adapter = mViewModel.getAdapter()
            /*mBinding.recyclerview.setOnClickListener {
                if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                    mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                    mBinding.chatMoreEmotion.visibility = View.GONE
                }
                if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                    mBinding.chatMoreLl.visibility = View.GONE
                }
            }*/
            mViewModel.getAdapter().scrollToBotton()
            Timber.tag("niushiqi-task").i("initRecycleView " + System.currentTimeMillis())
        }
        else -> {
            //语音／文字匹配信箱
            val layoutManager = LinearLayoutManager(this)
            //设置线
            mViewModel.getAdapter().setLayoutManager(layoutManager)
            mViewModel.getAdapter().initChatList(list)
            //获取对方个人信息
            mViewModel.netUserinfo2(getToUserid())
            //mViewModel.getAdapter().getList().add(0, BaseChatAdapterV2.Wrapper(MessageHeadEntity(fromType, getToAvatar(), getToNickName(), isFriend)))
            mViewModel.getAdapter().setListener(this)
            mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
            mBinding.recyclerview.layoutManager = layoutManager
            mBinding.recyclerview.adapter = mViewModel.getAdapter()
            /*mBinding.recyclerview.setOnClickListener {
                if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
                    mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
                    mBinding.chatMoreEmotion.visibility = View.GONE
                }
                if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                    mBinding.chatMoreLl.visibility = View.GONE
                }
            }*/
            mViewModel.getAdapter().scrollToBotton()
        }
    }

    //初始化聊天表情
    private var emotionPagerAdapter : EmotionPagerAdapter? = null
    private fun initEmotion() {
        var screenMargin: Int = 2 * 2
        val screenWidth: Int = (DisplayUtils.getScreenWidthPixels(this@ChatActivity)
                - DisplayUtils.dp2px(this, screenMargin.toFloat()))
        val spacing: Int = DisplayUtils.dp2px(this, 5.toFloat())
        val itemWidth: Int = DisplayUtils.dp2px(this, 51.toFloat())
        val gvHeight: Int = itemWidth * 3 + spacing * 2
        val marginTop: Int = DisplayUtils.dp2px(this, 15.toFloat())

        var emotionViews: MutableList<GridView> = ArrayList()
        var emotionNames: MutableList<String> = ArrayList()
        for (emotionname: String in EmotionUtils.getEmojiMap(1).keys) {
            emotionNames.add(emotionname)
            if(emotionNames.size == 21) {
                var gv: GridView = createEmotionGridView(emotionNames,
                        screenWidth, spacing, itemWidth, gvHeight)
                emotionViews.add(gv)
                emotionNames = ArrayList()
            }
        }

        mBinding.llPointGroup.initIndicator(emotionViews.size)
        emotionPagerAdapter = EmotionPagerAdapter(emotionViews)
        mBinding.vpComplateEmotionLayout.setAdapter(emotionPagerAdapter)
        var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(screenWidth, gvHeight)
        params.topMargin = marginTop
        mBinding.vpComplateEmotionLayout.setLayoutParams(params)
        mBinding.vpComplateEmotionLayout.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            internal var oldPagerPos = 0
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mBinding.llPointGroup.playByStartPointToNext(oldPagerPos, position)
                oldPagerPos = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun createEmotionGridView(emotionNames: List<String>, gvWidth: Int, padding: Int, itemWidth: Int, gvHeight: Int): GridView {
        // 创建GridView
        val gv = GridView(this@ChatActivity)
        //设置点击背景透明
        //gv.setSelector(android.R.color.transparent)
        //设置7列
        gv.numColumns = 7
        //gv.setPadding(padding, padding, padding, padding)
        gv.horizontalSpacing = 0
        gv.verticalSpacing = padding
        //设置GridView的宽高
        val params = ViewGroup.LayoutParams(gvWidth, gvHeight)
        gv.layoutParams = params
        // 给GridView设置表情图片
        val adapter = EmotionGridViewAdapter(this@ChatActivity, emotionNames, itemWidth, 1)
        gv.adapter = adapter
        //设置全局点击事件
        gv.setOnItemClickListener ({
            parent, view : Any , position:Int, id:Any ->
            val itemAdapter = parent.adapter

                // 如果点击了表情,则添加到输入框中
                val emotionName = itemAdapter.getItem(position).toString()

                // 获取当前光标位置,在指定位置上添加表情图片文本
                val curPosition = mBinding.chatContentEt.getSelectionStart()
                val sb = StringBuilder(mBinding.chatContentEt.getText().toString())
                sb.insert(curPosition, emotionName)

                // 特殊文字处理,将表情等转换一下
                mBinding.chatContentEt.setText(SpanStringUtils.getEmotionContent(1,
                        this, mBinding.chatContentEt, sb.toString()))

                // 将光标设置到新增完表情的右侧
                mBinding.chatContentEt.setSelection(curPosition + emotionName.length)
        })
        //解决滑动gridview重新排列闪退问题
        gv.setOnTouchListener ({
            v : View, event : MotionEvent ->
            if(MotionEvent.ACTION_MOVE == event.getAction()) true else false
        })

        return gv

    }

    /**
     * 定时任务
     */
    private fun timberTime() {
        val subscribe = Flowable.interval(2, TimeUnit.SECONDS).take(1).observeOn(
                AndroidSchedulers.mainThread()).subscribe({ startAnim() }, {})
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    /**
     * 打开动画
     */
    private fun startAnim() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.award_in)
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
                mBinding.awardCl.visibility = View.VISIBLE
            }
        })
        mBinding.awardCl.startAnimation(animation)
    }

    /**
     * 关闭动画
     */
    private fun closeAnim() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.award_out)
        animation.fillAfter = false
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                mBinding.awardCl.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        mBinding.awardCl.startAnimation(animation)
    }

    public fun getToUserid() = intent.getStringExtra(USERID)

    override fun onBackPressed() {
        when(fromType) {
            FROM_TYPE_SQUARE -> {
                if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                    hideChatMoreView()
                } else {
                    mViewModel.clearUnreadMessage(intent.getStringExtra(USERID))
                    super.onBackPressed()
                }
            }
            else -> {
                if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
                    hideChatMoreView()
                } else {
                    mViewModel.clearUnreadMessage(intent.getStringExtra(USERID))
                    if (isFriend) {
                        finish()
                        return
                    }
                    if (nowCountDownTime == 0L || countDownTime == 0L) {
                        var dialog = TimeOutDialog(this@ChatActivity, getToUserid())
                        dialog.show()
                        dialog.setOnDismissListener {
                            if (mToolbar?.findViewById<TextView>(R.id.tv_toolbar_count_down)?.text!!.equals("0秒")) {
                                mDataRepository.deleteFriendConversation(getToUserid())
                            }
                            finish()
                        }
                        return
                    }
                    if (fromType == 2) {
                        if (isFromMatch || !isFriend || nowCountDownTime > 0) {
                            var dialog = ChatExitDialog(this, getToUserid())
                            dialog.show()
                            return
                        }
                    }
                    super.onBackPressed()
                }
            }
        }
    }

    /**
     * 隐藏更多
     */
    private fun hideChatMoreView() {
        if (mBinding.chatMoreLl.visibility == View.VISIBLE) {
            mBinding.chatMoreLl.visibility = View.GONE
            KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
        }
    }

    private fun hideEmotionView() {
        if (mBinding.chatMoreEmotion.visibility == View.VISIBLE) {
            mBinding.chatMoreEmotion.visibility = View.GONE
            mBinding.chatEmotionOpenIv.setImageResource(R.drawable.ic_chat_emotion_open)
            KeyboardUtil.closeInputKeyboard(this, mBinding.chatMoreLl)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.right_iv -> showMeunMore()
        }
        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_1by1_list", getToUserid()))
    }

    /**
     * 举报
     * 解除好友
     */
    private fun showMeunMore() {
        MaterialDialog.Builder(this).items("举报", "解除好友关系").itemsCallback { _, _, position, _ ->
            when (position) {
            //解除好友
                1 -> mViewModel.netRevokeFriendship(getToUserid())
            //举报
                0 -> mViewModel.netReportReason()
            }
        }.show()
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
        MaterialDialog.Builder(this).items(names).itemsCallback { _, _, _, text ->
            val id = map[text]
            mViewModel.netReportUser(getToUserid(), id!!)
        }.show()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        hideChatMoreView()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //内容消息变化,点亮发送按钮
        s?.let {
            mBinding.chatSendBtn.isEnabled = it.isNotEmpty()

        }
    }

    @Subscribe
    fun onEventMessage(obj : EventMessage<Any>){
        if (obj.tag == EventConstant.TAG.TAG_CHAT_ACTIVITY){
            when(obj.what){
                EventConstant.WHAT.WHAT_CHAT_EXIT -> {
                    if (nowCountDownTime <= 0) {
                        mDataRepository.deleteFriendConversation(getToUserid())
                    }
                    finish()
                }
                EventConstant.WHAT.WHAT_REPORT -> {
                    mDataRepository.deleteFriendConversation(getToUserid())
                    mDataRepository.postFeedBack(getUserid(), "", "2", obj.obj as String).subscribe({
                        if (it.errorCode != 200) {
                            niceToast(it.errorMsg)
                        } else {
                            niceToast("举报成功")
                        }
                    }, {
                        niceToast("网络错误")
                    })
                    finish()
                }
                EventConstant.WHAT.WHAT_REPORT_LIKE_OR_NO -> {
                    mDataRepository.deleteFriendConversation(getToUserid())
                    mDataRepository.postFeedBack(getUserid(), "", obj.obj as String, "").subscribe({
                        if (it.errorCode != 200) {
                            niceToast(it.errorMsg)
                        } else {
                            niceToast("评价成功")
                        }
                    }, {
                        niceToast("网络错误")
                    })
                    finish()
                }
                EventConstant.WHAT.WHAT_FRIEND_ADD_VOICE_CLICK -> {
                    if (obj.obj.toString().equals("1")){//语音
                        startVoiceTalk()
                    }else{//好友
                        Timber.d("addfriend")
                        mViewModel.addFriendFromTextMatch(getToUserid())
                    }
                }
                EventConstant.WHAT.WHAT_IS_FRIEND -> {
                    isFriend = obj.obj as Boolean
                    initToobar()
                }
                EventConstant.WHAT.WHAT_IS_SHOW_ADDFRIEND -> {
                    isFriend = obj.obj as Boolean

                }
                EventConstant.WHAT.HWAT_MESSAGE_END_TIME -> {
                    var messageEndTime = obj.obj as Long
                    if (messageEndTime == 0L) {
                        countDownTime = 180L
                    } else {
                        var nowTime: Long = System.currentTimeMillis()/1000
                        if (nowTime <= messageEndTime) {
                            countDownTime = (messageEndTime - nowTime) as Long
                        }else{
                            countDownTime = 0L
                        }
                    }
                    countDownTime()
                }
                /*EventConstant.WHAT.WHAT_INIT_RECYCLER -> {
                    if (isFriend && !isFromMatch) {
                        initRecycleView(obj.obj as List<EMMessage>)
                    }
                }*/
            }
        }
    }

    @Subscribe
    fun onMessageVM(obj: ChatViewModel.MessageVM) {
        when(fromType) {
            FROM_TYPE_SQUARE -> {
                //广场信箱
                //语音／文字匹配信箱
                //不是从文字匹配来的好友且有消息
                /*if (obj.list != null && isFriend && !isFromMatch) {
                    mViewModel.getAdapter().addChatTop(obj.list, false)
                    return
                }*/
                //其他情况
                if (obj.list != null && obj.list.size > 0) {
                    if (mViewModel.getPageNum() == 1) {
                        initRecycleView(obj.list)
                        Timber.tag("niushiqi-task").i("initRecycleView1 ")
                    } else {
                        mViewModel.getAdapter().addChatTop(obj.list, false)
                    }
                } else {
                    initRecycleView(arrayListOf())
                    Timber.tag("niushiqi-task").i("initRecycleView2 ")
                }
            }
            else -> {
                //语音／文字匹配信箱
                if (obj.list != null && isFriend && !isFromMatch) {
                    mViewModel.getAdapter().addChatTop(obj.list, false)
                    return
                }
                if (obj.list != null && obj.list.size > 0) {
                    if (mViewModel.getPageNum() == 1) {
                        initRecycleView(obj.list)
                    } else {
                        mViewModel.getAdapter().addChatTop(obj.list, false)
                    }
                }
            }
        }
    }

    /**
     * 别的界面进来,刷新语音/视频状态
     */
    @Subscribe
    fun onVoiceMessage(message: VoiceMessage) {
        when (message.status) {
        //通话超时
            StatusTag.STATUS_OUT_TIME -> mViewModel.sendOuttimeMessage(message.toUserid, message.type,
                    intent.getStringExtra(AVATAR),
                    intent.getStringExtra(NICKNAME),
                    fromType.toString())
//    //通话结束
//      StatusTag.STATUS_DISCONNECTED -> mViewModel.sendDisconnetedMessage(message.toUserid,
//                                                                         message.type,
//                                                                         intent.getStringExtra(
//                                                                             AVATAR),
//                                                                         intent.getStringExtra(
//                                                                             NICKNAME))
        //呼叫取消
            StatusTag.STATUS_CANCEL -> mViewModel.sendCancelMessage(message.toUserid, message.type,
                    intent.getStringExtra(AVATAR),
                    intent.getStringExtra(NICKNAME), "true", fromType.toString())

        //通话中
            StatusTag.STATUS_CALL_IN -> mViewModel.sendCallInMessage(message.toUserid, message.type,
                    intent.getStringExtra(AVATAR),
                    intent.getStringExtra(NICKNAME),
                    fromType.toString())
        }
    }

    @Subscribe
    fun onGetRewardVM(obj: MessageTipPopViewModel.GetRewardVM) {

    }


    /**
     * 一对一奖励
     */
    fun onFriendshipRewardResult(obj: FriendshipRewardResult) {
        mBinding.rewardStoneTv.text = obj.rewardStone.toString()
        mBinding.rewardCashTv.text = obj.rewardCash.toString()
        mBinding.rewardCoinTv.text = obj.rewardCoin.toString()
        setTextFont(mBinding.rewardCoinTv)
        setTextFont(mBinding.rewardStoneTv)
        setTextFont(mBinding.rewardCashTv)

        setVisibleView(obj.rewardStone == null || obj.rewardStone == 0, mBinding.rewardStoneTv)
        setVisibleView(obj.rewardCash == null || obj.rewardCash == 0.0, mBinding.rewardCashTv)
        setVisibleView(obj.rewardCoin == null || obj.rewardCoin == 0, mBinding.rewardCoinTv)

        mViewModel.mRewardId = obj.rewardId.toString()
        startAnim()
//    timberTime()
    }

    private fun setVisibleView(isVisible: Boolean, view: View) {
        if (!isVisible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    /**
     * 获取好友在线状态
     */
    @Subscribe
    fun getFriendOnlineStatus(obj: ChatViewModel.FriendOnlineStatusVM) {
        val online = obj.onlineStatus?.data?.online
        if (online == "1") {
            mToolbar?.findViewById<TextView>(R.id.is_on_line_text)?.text = "当前在线"
        }else{
            mToolbar?.findViewById<TextView>(R.id.is_on_line_text)?.text = DateFormatter.LongFormatTime2(obj.onlineStatus?.data?.lastActivityTime)+" 在线"
            mToolbar?.findViewById<TextView>(R.id.is_on_line_text)?.setTextColor(Color.parseColor("#B9B9B9"))
        }
    }

    /**
     * 随机奖励
     */
    @Subscribe
    fun onSubscribeResult(obj: RangAwardResult) {
        if (isCureentActivity()) {
            RangAwardActivityV2.start(this, obj)
        }
    }

    /**
     * 解除好友关系
     */
    @Subscribe
    fun onRevokeFriendshipVM(obj: ChatViewModel.RevokeFriendshipVM) {
        if (obj.isSuccess) {
            niceToast("解除成功")
            onBackPressed()
        } else {
            niceToast("解除失败")
        }
    }

    @Subscribe
    fun onReportReasonVM(obj: ChatViewModel.ReportReasonVM) {
        if (obj.isSuccess) {
            obj.obj?.data?.reportReason?.let {
                showReportReason(it)
            }

        } else {
            niceToast("获取失败")
        }
    }

    /**
     * 举报
     */
    @Subscribe
    fun onReportUserVM(obj: ChatViewModel.ReportUserVM) {
        if (obj.isSuccess) {
            niceToast("举报成功,我们正在处理")
        } else {
            niceToast("网络等原因,举报失败请重试")
        }
    }

    /**
     * 友好度查询
     */
    @Subscribe
    fun onFriendExperienceVM(obj: ChatViewModel.FriendExperienceVM) {
        if (obj.isSuccess) {
            obj.obj?.let {
                initProgress(obj = it)
            }

        }
    }

    override fun onMessageRecalled(p0: MutableList<EMMessage>?) {
        //消息被撤回
        Timber.tag("EMClient").i("消息被撤回")
    }

    override fun onMessageChanged(p0: EMMessage?, p1: Any?) {
        //消息状态变动
        Timber.tag("EMClient").i("消息状态变动")
    }

    override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {
        Timber.tag("EMClient").i("收到透传消息")
        //收到透传消息

        p0?.forEach {
            val fromUserid = it.getExtMap("from_userid")
            val toUserid = it.getExtMap("to_userid")
            val type = it.getExtMap("voice_type")
            val nickname = it.getExtMap("from_nickname")
            val avatar = it.getExtMap("from_avatar")
            val extra = it.getExtMap("extra")
            val fromType = it.getExtMap("fromType")

            (it.body as? EMCmdMessageBody)?.let { body ->
                    when(body.action()) {
                        StatusTag.STATUS_REJEC -> {
                            //1对1通话：B拒绝，A的处理逻辑
                            runOnUiThread {
                                niceToast("对方已拒绝，请稍后再试")
                            }
                            Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方已拒绝，请稍后再试")
                            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("对方已拒绝",
                                    true, true, true, fromType!!))
                        }
                        StatusTag.STATUS_ACCEPTED -> {
                            //1对1通话：B接收，A的处理逻辑
                            Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方已接收，开始聊天吧")
                        }
                        StatusTag.STATUS_DISCONNECTED -> {
                            //1对1通话：A/B挂断，B/A的处理逻辑
                            mDataRepository.endCall()
                            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("聊天时长:"+extra,
                                    true, true, true, fromType!!))
                            Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方已经挂断")
                        }
                        StatusTag.STATUS_CALL_IN -> {
                            //1对1通话：B发送通话建立成功，A的处理逻辑
                            EventBus.getDefault().post(ConnetingViewModel.VoiceStartCallVM(true))
                            //2018.12.4日过渡版本-通话建立成功直接开启树洞
                            MainActivity.startCallIn(this@ChatActivity, fromUserid!!, toUserid!!)
                            Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方建立连接成功")
                        }
                        /*StatusTag.STATUS_CANCEL -> {
                            //1对1通话：A/B取消，B/A的处理逻辑
                            //2018.12.4日过渡版本-通话建立成功直接开启树洞
                            var isCallingParty = extra
                            if(isCallingParty == "true") {
                                EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("对方已取消", true, true, false))
                            } else {
                                EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("对方已取消", true, true, true))
                            }
                            Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方已经取消")
                        }*/
                        StatusTag.STATUS_BUSY -> {
                            runOnUiThread {
                                niceToast("对方忙线中，请稍后再试")
                            }
                            EventBus.getDefault().post(ChatViewModel.ShowVoiceCmdMessageVM("对方忙线中",
                                    true, true, true, fromType!!))
                            Timber.tag("niushiqi-tonghua").i("activity("+this.javaClass.simpleName+") 对方忙线中")
                        }
                        else -> {

                        }
                    }
            }
        }
    }

    override fun onMessageReceived(p0: MutableList<EMMessage>?) {
        Timber.tag("EMClient").i("onSuccess")
        //收到消息
        runOnUiThread { mViewModel.onMessageReceived(p0, getToUserid()) }
        EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
    }

    override fun onMessageDelivered(p0: MutableList<EMMessage>?) {
        //收到已送达回执
        Timber.tag("EMClient").i("收到已送达回执")
    }

    override fun onMessageRead(p0: MutableList<EMMessage>?) {
        //收到已读回执
        Timber.tag("EMClient").i("收到已读回执")
    }


    /**
     * 发送礼物成功信箱显示礼物卡
     * 原理：环信发送礼物，adapter显示礼物卡
     */
    @Subscribe
    fun onSendGiftCardMessage(obj: ChatViewModel.SendGiftCardMessage) {
        when(fromType) {
            FROM_TYPE_SQUARE -> {

                var obj: EventGiftMessage? = obj.obj
                if (obj != null) {
                    mViewModel.sendGiftCardMessageBySquare(obj)
                }
            }
            else -> {

                var obj: EventGiftMessage? = obj.obj
                if (obj != null) {
                    mViewModel.sendGiftCardMessage(obj, fromType, isFriend,matchTime)
                }
            }
        }
    }

    /**
     * 个人信息界面
     */
    @Subscribe
    fun onUserInfoDialog(obj: ChatViewModel.UserInfoVM) {
        if (mViewModel.mUserInfo != null) {
            showUserInfoDialog(mViewModel.mUserInfo!!)
        } else {
            mViewModel.netUserinfo(obj.userId)
        }
    }

    /**
     * 展示个人信息界面
     */
    @Subscribe
    fun showUserInfoDetail(obj: ChatViewModel.UserInfoDetailVM) {
        if (obj.success) {
            showUserInfoDialog(mViewModel.mUserInfo!!)
        } else {
            niceToast("获取个人信息失败")
        }
    }

    /**
     * 点击显示大图
     */
    @Subscribe
    fun showUserInfoDetail(obj: ChatViewModel.ShowLargeImageVM) {
        showLargeImageDialog(obj.imageUrl)
    }

    /**
     * 点击接收
     * recyclerview会刷新，只适合记录显示数据，不适合处理数据
     */
    override fun onAccept(fromUserid: String, toUserid: String, type: String) {
//    val callStatus = if (type.contains(StatusTag.TYPE_VOICE)) 1 else 2
//    mViewModel.sendAcceptMessage(fromUserid, type, intent.getStringExtra(AVATAR),
//                                 intent.getStringExtra(NICKNAME))
//    mViewModel.netFriendExperience(fromUserid = fromUserid, toUserid = toUserid, type = callStatus,
//                                   status = 1)
    }

    /**
     * 点击拒绝
     * recyclerview会刷新，只适合记录显示数据，不适合处理数据
     */
    override fun onReject(fromUserid: String, toUserid: String, type: String) {
//    val callStatus = if (type == StatusTag.TYPE_VOICE) 1 else 2
//    mViewModel.sendRejectMessage(fromUserid, type, intent.getStringExtra(AVATAR),
//                                 intent.getStringExtra(NICKNAME))
//    mViewModel.netFriendExperience(fromUserid = fromUserid, toUserid = toUserid, type = callStatus,
//                                   status = 2)
    }

    private var dialog: Dialog? = null

    fun showUserInfoDialog(obj: UserDetailInfoResult.Data) {
        if (dialog == null) {
            dialog = Dialog(this@ChatActivity)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.dialog_userinfo)
            dialog?.getWindow()!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            dialog?.window!!.setBackgroundDrawable(
                    ColorDrawable(resources.getColor(android.R.color.transparent)))
        }
        dialog?.findViewById<TextView>(R.id.textView14)?.text = obj.userBaseInfo.nickname
        dialog?.findViewById<ImageView>(R.id.imageView24)?.setImageResource(
                if (obj.userBaseInfo.gender == "1") R.drawable.ic_gender_main_normal else R.drawable.ic_gender_woman_normal)
        dialog?.findViewById<TextView>(R.id.level_tv)?.text = obj.userBaseInfo.userLevel
        DataBindingUtils.loadAvatar(dialog?.findViewById<ImageView>(R.id.imageView23),
                obj.userBaseInfo.avatar)
        val sb = StringBuilder()
        sb.append(obj.userBaseInfo.age.toString())
        if (!TextUtils.isEmpty(obj.userExtraInfo.professionName)) {
            sb.append(" | ").append(obj.userExtraInfo.professionName)
        }
        if (!TextUtils.isEmpty(obj.userExtraInfo.address)) {
            sb.append(" | ").append(obj.userExtraInfo.address)
        }
        dialog?.findViewById<TextView>(R.id.txt_user_info)?.text = sb.toString()
        val subscribe = mDataRepository.getSigns(obj.userBaseInfo.userId).subscribe({
            if (it.errorCode != 200) {
                niceToast(
                        it.errorMsg!!)
                return@subscribe
            }
            dialog?.findViewById<FlexboxLayout>(
                    R.id.afl_cotent)?.removeAllViews()
            it.data?.tags?.forEach {
                val itemView = LayoutInflater.from(
                        niceChatContext()).inflate(
                        R.layout.item_tags3,
                        null)
                val textview = itemView.findViewById<TextView>(
                        R.id.txt_sign)
                textview.setSingleLine(
                        true)
                textview.text = it.tagName
                val params = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT)
                params.order = -1
                params.flexGrow = 2F
                dialog?.findViewById<FlexboxLayout>(
                        R.id.afl_cotent)?.addView(
                        itemView,
                        params)
            }
            dialog?.show()
        })
        CompositeDisposable().add(subscribe)
//    dialog.show()
    }

    private var dialog2: Dialog? = null

    fun showLargeImageDialog(imageUrl: String) {
        val screenWidth: Int = DisplayUtils.getScreenWidthPixels(this@ChatActivity)
        val screenHeight: Int = DisplayUtils.getScreenHeightPixels(this@ChatActivity)
        if (dialog2 == null) {
            dialog2 = Dialog(this@ChatActivity)
            dialog2?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog2?.setContentView(R.layout.dialog_large_photo)
            dialog2?.getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
            dialog2?.window!!.setBackgroundDrawable(
                    ColorDrawable(resources.getColor(android.R.color.black)))
        }
        var image: PhotoView? = dialog2?.findViewById<PhotoView>(R.id.large_image)
        image!!.setImageDrawable(null)
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(object: SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(resource: GlideDrawable , glideAnimation: GlideAnimation <in GlideDrawable>) {
                        //动态设置图片大小
                        var params: ViewGroup.LayoutParams = image!!.getLayoutParams();
                        if((resource.intrinsicWidth / resource.intrinsicHeight) > (screenWidth / screenHeight)) {
                            params.height = screenHeight
                            params.width = screenHeight * resource.intrinsicWidth / resource.intrinsicHeight
                        } else {
                            params.width = screenWidth
                            params.height = screenWidth * resource.intrinsicHeight / resource.intrinsicWidth
                        }
                        image!!.setLayoutParams(params)
                        image.setImageDrawable(resource)
                    }
                })
        dialog2?.show()
        image?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(paramView: View) {
                dialog2?.cancel()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val list = Matisse.obtainPathResult(data)
            showProgressDialog()           
            //发送HOST服务器请求图片
            list.forEach {
                mViewModel.sendImageMessage(it)
            }
        }
    }

    @Subscribe
    open fun onAvatarVM(obj: ChatViewModel.AvatarVM) {
        when(fromType) {
            FROM_TYPE_SQUARE -> {
                closeProgressDialog()

                var obj: ImageMessage? = obj.obj
                if (obj != null) {
                    mViewModel.sendImageURLMessageBySquare(obj)
                }
            }
            else -> {
                closeProgressDialog()

                var obj: ImageMessage? = obj.obj
                if (obj != null) {
                    mViewModel.sendImageURLMessage(obj, fromType, isFriend,matchTime)
                }
            }
        }
    }

    @Subscribe
    open fun onOpenConnetingActivityVM(obj: ChatViewModel.OpenConnetingActivityVM) {
        //B建立通话成功，需要告诉A
        EventBus.getDefault().post(ConnetingViewModel.VideoSuccessVM(obj.fromUseid, obj.toUserid,
                obj.type, getToAvatar(), getToNickName(), fromType.toString()))
        EventBus.getDefault().postSticky(ConnetingViewModel.VoiceStartCallVM(true))
    }

    @Subscribe
    open fun onShowVoiceCmdMessage(obj: ChatViewModel.ShowVoiceCmdMessageVM) {
        Timber.tag("niushiqi-tonghua").i("向窗口打印信息："+obj.Content)
        if(obj.isDelay) {
            //部分显示需要延时2s
            val subscribe = Flowable.interval(2, 0, TimeUnit.SECONDS).take(1).observeOn(
                    AndroidSchedulers.mainThread()).subscribe({ count ->
                obj.Content?.let {
                    mViewModel.showVoiceCmdMessage(it, getToUserid(), obj.isCallingParty, obj.fromType)
                }
                if(obj.isCloseConWindow) {
                    EventBus.getDefault().post(ConnetingActViewModel.CloseConnetingActivityVM())
                }
            })
            mViewModel.mCompositeDisposable.add(subscribe)
        } else {
            mViewModel.showVoiceCmdMessage(obj.Content, getToUserid(), obj.isCallingParty, obj.fromType)
        }



    }

    @Subscribe
    fun onFriendExperienceResult2(obj: FriendExperienceResult2) {

        if (obj.talkType == "1") {//语音
            if (obj.fromUserId == mDataRepository.getUserid()) {
                //语音发起方
                //发版 - 临时上报通话状态
                val subscribe = mDataRepository.postReportTalkingStatus(obj.talkId!!, "1",
                        obj.fromUserId!!, obj.toUserId!!, "1",
                        DateUtils.getSecondTimestampTwo(Date()).toString()).subscribe({
                    if(it.errorCode == 200) {
                        mDataRepository.setChatCallInfo(obj.toUserId, obj.talkId, StatusTag.TYPE_VOICE)
                    }
                }, {})

                obj.toUserId?.let {
                    Timber.tag("niushiqi-tonghua").i("环信开启发起通话")
                    //匹配通话独立版本
                    mDataRepository.callVoiceExt(it)
                    if (ActManagerUtils.getAppManager().currentActivity()!!.javaClass == ConnetingActivity.javaClass) {
                        onBackPressed()
                    }
                }
            } else {
                //语音接收方
                //发版 - 临时上报通话状态
                val subscribe = mDataRepository.postReportTalkingStatus(obj.talkId!!, "1",
                        obj.fromUserId!!, obj.toUserId!!, "1",
                        DateUtils.getSecondTimestampTwo(Date()).toString()).subscribe({
                    if(it.errorCode == 200) {
                        mDataRepository.setChatCallInfo(obj.fromUserId, obj.talkId, StatusTag.TYPE_VOICE)
                    }
                }, {})

                //mActivity.niceToast("B方建立通话")
                //mDataRepository.setEasemobStatus(mActivity)
                //ChatApp.getInstance().getDataRepository().answerCall()
                Timber.tag("niushiqi-tonghua").i("环信接收发起通话")
            }

        } else {//视频
            mDataRepository.setChatCallInfo(obj.toUserId, obj.talkId, StatusTag.TYPE_VIDEO)
            if (obj.fromUserId == mDataRepository.getUserid()) {
                obj.toUserId?.let {
                    mDataRepository.callVideo(it)
                    if (ActManagerUtils.getAppManager().currentActivity()!!.javaClass == ConnetingActivity.javaClass) {
                        onBackPressed()
                    }
//            MainActivity.startVideoOut(this, fromUserid = obj.fromUserId!!, toUserid = it, talkId = obj.talkId!!)
                }
            }
        }

    }

    /**
     * 点击同意来电
     */
    @Subscribe
    fun onVideoRequestAcceptVM(obj: IncomingRequestViewModel.VideoRequestAcceptVM) {
        val callStatus = if (obj.type.contains(StatusTag.TYPE_VOICE)) 1 else 2
        ConnetingActivity.start(this@ChatActivity, obj.toUserId, obj.fromUserId, obj.type, "false", fromType = obj.fromType)
        mViewModel.sendAcceptMessage(obj.fromUserId, obj.type, obj.avatar, obj.nickname, obj.fromType)
        mViewModel.netFriendExperience(fromUserid = obj.fromUserId, toUserid = obj.toUserId,
                type = callStatus, status = 1)
    }

    /**
     * 拒绝来电
     */
    @Subscribe
    fun onVideoRequestRejectVM(obj: IncomingRequestViewModel.VideoRequestRejectVM) {
        val callStatus = if (obj.type == StatusTag.TYPE_VOICE) 1 else 2
        mViewModel.sendRejectMessage(obj.fromUserId, obj.type, obj.avatar, obj.nickname, obj.fromType)
        mViewModel.netFriendExperience(fromUserid = obj.fromUserId, toUserid = obj.toUserId,
                type = callStatus, status = 2)
    }

    /**
     * 聊天建立成功
     */
    @Subscribe
    fun onVideoSuccessVM(obj: ConnetingViewModel.VideoSuccessVM) {
        val callStatus = if (obj.type == StatusTag.TYPE_VOICE) 1 else 2
        mViewModel.sendCallInMessage(obj.toUserId, obj.type, obj.avatar, obj.nickname, obj.fromType)
    }

    /**
     * 关闭送礼物
     */
    @Subscribe
    fun onCloseGiftFrg(obj: SendGiftFragmentInChat.CloseGiftFrg) {
        reSetAboutGiftStatus(false)
        removeFragment(SendGiftFragmentInChat.instance(null))
    }

    /**
     * 展示购买得豆页面
     */
    @Subscribe
    fun onShowBuyPeaFrg(obj: SendGiftFragmentInChat.showBuyPeaFrg) {
        reSetAboutGiftStatus(true)
        addFragment(BuyProudPeasFragment.instance())
    }

    /**
     * 关闭购买得豆页面
     */
    @Subscribe
    fun onCloseBuyPeaFrg(obj: BuyProudPeasFragment.CloseBuyPeaFrg) {
        reSetAboutGiftStatus(true)
        addFragment(SendGiftFragmentInChat.instance(getToUserid()))
    }

    /**
     * 聊天取消
     */
    @Subscribe
    fun onVideoCancelVM(obj: ConnetingViewModel.VideoCancelVM) {
        val callStatus = if (obj.type == StatusTag.TYPE_VOICE) 1 else 2
        mViewModel.sendCancelMessage(obj.toUserId, obj.type, obj.avatar, obj.nickname, obj.isCallingParty, obj.fromType)
    }

    /**
     * 聊天结束挂断
     */
    @Subscribe
    fun onVideoDisconnetedVM(obj: ConnetingViewModel.VideoDisconnetedVM) {
        val callStatus = if (obj.type == StatusTag.TYPE_VOICE) 1 else 2
        mViewModel.sendDisconnetedMessage(obj.fromUserId, obj.toUserId, obj.type, obj.avatar, obj.nickname, obj.time, obj.fromType)
    }

    /**
     * 广场-"1分钟内积极回复"任务
     * 更新recyclerview头部item的UI
     */
    @Subscribe
    fun onUpdateMessageVM(obj: ChatViewModel.UpdateMessageVM) {
        //判断当前SquareMessageHeadEntity是否存在，决定是否刷新／显示
        if(mViewModel.getAdapter().getList().size == 0) {
            mViewModel.getAdapter().getList().add(0,
                    BaseChatAdapterV2.Wrapper(SquareMessageHeadEntity(fromType, getToAvatar(), getToNickName(), obj.isTasking)))
        } else if(mViewModel.getAdapter().getList()[0].item is SquareMessageHeadEntity) {
            mViewModel.getAdapter().getList().set(0,
                    BaseChatAdapterV2.Wrapper(SquareMessageHeadEntity(fromType, getToAvatar(), getToNickName(), obj.isTasking)))
        } else {
            mViewModel.getAdapter().getList().add(0,
                    BaseChatAdapterV2.Wrapper(SquareMessageHeadEntity(fromType, getToAvatar(), getToNickName(), obj.isTasking)))
        }
        mViewModel.getAdapter().notifyDataSetChanged()
        Timber.tag("niushiqi-task").i("onUpdateMessageVM " + System.currentTimeMillis())
    }

    /**
     * 广场-"1分钟内积极回复"任务
     * 任务完成下发奖励弹窗
     */
    @Subscribe
    fun onSquareMessageTaskRewardResult(obj: SquareMessageTaskRewardResult) {
//        ReceiveTaskActivity.start(this, title = "广场对话奖励", gold = obj.rewardCoin!!.toDouble(),
//                stone = obj.rewardStone!!.toDouble(), money = obj.rewardCash!!.toDouble(),
//                rewardId = obj.rewardId!!, autoClose = false)
        NewbieTaskActivity.start(this, title = "广场对话奖励", gold = obj.rewardCoin!!.toDouble(),
                stone = obj.rewardStone!!.toDouble(), money = obj.rewardCash!!.toDouble(),
                rewardId = obj.rewardId!!.toInt())
        EventBus.getDefault().post(ChatViewModel.UpdateMessageVM(false))
    }
}