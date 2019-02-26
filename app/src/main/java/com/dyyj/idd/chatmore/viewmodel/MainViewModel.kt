package com.dyyj.idd.chatmore.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.dyyj.idd.chatmore.BuildConfig
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.database.entity.UnreadMessageEntity
import com.dyyj.idd.chatmore.model.mqtt.result.RangAwardResult
import com.dyyj.idd.chatmore.model.network.result.MainResult
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult
import com.dyyj.idd.chatmore.model.network.result.UploadAvatarResult
import com.dyyj.idd.chatmore.model.network.result.UserCenterInfoResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.utils.*
import com.gt.common.gtchat.extension.niceString
import com.gt.common.gtchat.extension.niceToast
import com.gt.common.gtchat.model.network.NetURL
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.utils.AppUpdateUtils.getPackageInfo
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/13
 * desc   :
 */
class MainViewModel : ViewModel() {
    val CURRENT_USER_ID = "currentUserId";
    var isChating: Boolean = false
    private var mMainResult: MainResult? = null
    private var mStartMatchingResult: StartMatchingResult.Data? = null
    private var mRangAwardResult: RangAwardResult? = null
    var mStartTime: Int? = null
    val mCompositeDisposable2 by lazy { CompositeDisposable() }

    /**
     * 消耗魔石
     */
    fun netConsumeMatchingStore() {
        val subscribe = mDataRepository.consumeMatchingStore(3).subscribe({
            it.data?.consumeId?.let {
                mDataRepository.saveConsumeId(it)
            }
        }, {
        })
        mCompositeDisposable.add(subscribe)
    }


    fun getStartTime(): Int? {
        if (mStartTime == null) {
            mStartTime = DateUtils.getSecondTimestampTwo(Date())
        }
        return mStartTime
    }

    /**
     * 重置初始化时间
     */
    fun restartTime() {
        mStartTime = null
    }

    fun getMainResult() = mMainResult

    fun setMainResult(result: MainResult?) {
        mMainResult = result
    }

    fun getRangAwardResult() = mRangAwardResult

    fun setRangAwardResult(result: RangAwardResult) {
        mRangAwardResult = result
    }

    /**
     * 获取匹配信息
     */
    fun getStartMatchingResult() = mStartMatchingResult

    /**
     * 设置匹配信息
     */
    fun setStartMatchingResult(result: StartMatchingResult.Data?) {
        mStartMatchingResult = result
    }

    /**
     * 上传头像
     */
    fun netUploadAvatar(image: List<String>) {
        val subscribe = mDataRepository.postUploadAvatar(image).subscribe({
            EventBus.getDefault().post(
                    MainViewModel.AvatarVM(
                            it.errorCode == 200,
                            it.data, it.errorMsg))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().post(
                    MainViewModel.AvatarVM(
                            false, null, niceString(R.string.error_network_http)))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 审核失败重新上传头像
     */
    fun netReuploadAvatar(image: List<String>) {
        val subscribe = mDataRepository.postReuploadAvatar(image).subscribe({
            EventBus.getDefault().post(
                    MainViewModel.AvatarVM(
                            it.errorCode == 200,
                            it.data, it.errorMsg))
        }, {
            EventBus.getDefault().post(
                    MainViewModel.AvatarVM(
                            false, null, niceString(R.string.error_network_http)))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 主页信息
     */
    fun netMainInfo() {
        val subscribe = mDataRepository.postMain().subscribe({
            mMainResult = it
            EventBus.getDefault().post(MainVM(it, it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().post(
                    MainVM(isSuccess = false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 环信帐号登陆
     */
    fun netLoginEM(activity: Activity, username: String?, password: String?) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) return

        EMClient.getInstance().login(username, password, object : EMCallBack {
            //回调
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                Log.d("main", "登录聊天服务器成功！")
                activity.runOnUiThread {
                    if (BuildConfig.DEBUG) {
//            niceToast("环信登陆成功")
                    }

                }

                mDataRepository.initEasemobListener()
//        mDataRepository.registerCall()
            }

            override fun onProgress(progress: Int, status: String) {
                Log.d("main", "onProgress")
            }

            override fun onError(code: Int, message: String) {
                Log.d("main", "登录聊天服务器失Å败！")
                activity.runOnUiThread {
                    if (BuildConfig.DEBUG) {
//            niceToast("环信登陆失败")
                    }
                }

            }
        })
    }

    /**
     * 领取奖励
     */
    fun netGetReward(rewardId: String) {
        val subscribe = mDataRepository.postGetReward(rewardId).subscribe({
            EventBus.getDefault().post(
                    RewardVM(
                            it.errorCode == 200))
        }, {
            EventBus.getDefault().post(
                    RewardVM())
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 定时
     */
    fun timberFirstLogin() {
        Flowable.interval(2, 1, TimeUnit.SECONDS).take(1).observeOn(
                AndroidSchedulers.mainThread()).subscribe({
            netFirstLogin()
        }, {})
    }

    /**
     * 首次登陆
     */
    private fun netFirstLogin() {
        val subscribe = mDataRepository.postFirstLogin().subscribe({
            Timber.i("netFirstLogin")
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {})
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 保存消息到本地記錄
     */
//  fun insertMessage(msg: EMMessage) {
//    mDataRepository.insertEMMesage(msg)
//  }

    /**
     * 加载未读消息
     */
    fun loadUnreadMessage() {
        ThreadUtils.operate(object : ThreadRequest<List<UnreadMessageEntity>>() {
            override fun run(): List<UnreadMessageEntity>? {
                return mDataRepository.queryUnreadMessage()
            }
        }, object : ResultCallBack<List<UnreadMessageEntity>>() {
            override fun result(list: List<UnreadMessageEntity>?) {
                netNewMsg()//发送好友圈 未读消息
                getSquareUnreadMessage()
                EventBus.getDefault().post(UnreadMessageVM(list = list))
                EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT, EventConstant.WHAT.WHAT_UNREAD_MESSAGE_COUNT,list?.size))

            }
        })

//        val subscribe = mDataRepository.queryUnreadMessageAll().subscribe({
//
//            EventBus.getDefault().post(
//                    UnreadMessageVM(
//                            list = it))
//        }, {
//            EventBus.getDefault().post(
//                    UnreadMessageVM(
//                            isSuccess = false))
//        })
//        mCompositeDisposable.add(subscribe)
    }

    fun getApplyNum() {
        val subscribe = mDataRepository.getUnHandleFriendRequest()
                .subscribe({
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    } else {
                        EventBus.getDefault().post(RefreshPeopleNumVM(it.data.size))
                        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT, EventConstant.WHAT.WHAT_UNREAD_FRIEND_COUNT,it.data.size))

                    }
                }, {
                    EventBus.getDefault().post(RefreshPeopleNumVM(0))
                })

        mCompositeDisposable.add(subscribe)
    }

    fun netNewMsg() {
        val userid = PreferenceUtil.getString(CURRENT_USER_ID, null)
        val subscribe = mDataRepository.getUserMessage((if(mDataRepository.getUserid()!=null) mDataRepository.getUserid() else userid)!!).subscribe ({
            EventBus.getDefault().post(CircleViewModel.CircleUnReadMsgVM(it.errorCode == 200,it))//自己的动态 好友评论点赞动态数
        },{})
        mCompositeDisposable.add(subscribe)
    }

    class RefreshPeopleNumVM(val nums: Int)

    @SuppressLint("HardwareIds")
            /**
             * 检测版本升级
             */
    fun checkVersionUpdate(activity: Activity) {
        val params = HashMap<String, String>()

        params["appVersion"] = getPackageInfo(activity)?.versionName ?: ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            params["deviceId"] = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID)
        }
        UpdateAppManager.Builder()
                //当前Activity
                .setActivity(activity)
                //更新地址
                .setUpdateUrl(NetURL.VERSION_UPDATE)
                .setParams(params)
                //实现httpManager接口的对象
                .setHttpManager(UpdateAppHttpUtil()).build().update()
    }


    /**
     * 红包倒计时
     */
    fun countRedPacket(time: Long, total: Long, giftId: String) {
        var count: Long = Math.abs(total)
        mCompositeDisposable2.clear()

        val subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS).take(
                count + 1).map { count - it }.subscribeOn(Schedulers.computation()).observeOn(
                AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({

                    val currentTime = System.currentTimeMillis() / 1000

                    if (currentTime >= time) {
                        EventBus.getDefault().post(
                                OpenCallViewModel.ReadPacketVM(content = "打开", progress = 100,
                                        count = count, giftId = giftId))
                        EventBus.getDefault().post(OpenCallViewModel.RedPacketOpenVM())
                    } else {
                        val centerTime = it
                        val sb = StringBuilder()
                        sb.append(
                                if (centerTime / 60 < 10) "0${centerTime / 60}" else "${centerTime / 60}")
                        sb.append(":")
                        sb.append(
                                if (centerTime % 60 < 10) "0${centerTime % 60}" else "${centerTime % 60}")
                        EventBus.getDefault().post(
                                OpenCallViewModel.ReadPacketVM(content = sb.toString(),
                                        progress = count - it, count = count,
                                        giftId = giftId))
                    }


                }, {

                }, {
                    EventBus.getDefault().post(
                            OpenCallViewModel.ReadPacketVM(content = "打开", progress = 100,
                                    count = count, giftId = giftId))
                    EventBus.getDefault().post(OpenCallViewModel.RedPacketOpenVM())
                })
        mCompositeDisposable2.add(subscribe!!)
    }


    class UnreadMessageVM(val list: List<UnreadMessageEntity>? = null, val isSuccess: Boolean = true)

    /**
     * 固定奖励
     */
    class RewardVM(val isSuccess: Boolean = false)


    /**
     * 头像信息
     */
    class AvatarVM(val avatar: Boolean, val obj: UploadAvatarResult.Data? = null, val errorMsg: String? = "")

    /**
     * 主页用户信息
     */
    class MainVM(val obj: MainResult? = null, val isSuccess: Boolean? = false)

    /**
     * 获取任务列表
     */
    fun getUserData() {
        val subscribe = mDataRepository.getUserCenterApi().subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(UserData(true, it.data))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class UserData(val success: Boolean, val obj: UserCenterInfoResult.Data? = null)

    fun getTaskReward(taskId: String?) {
        val subscribe = mDataRepository.getTaskRewardData(taskId!!).subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(ThreeDayViewModel.TaskRewardData(true, it.data))
                    } else {
                        EventBus.getDefault().post(ThreeDayViewModel.TaskRewardData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 消耗魔石
     */
//  class ConsumeMatchingVM(val isSuccess: Boolean)

    /**
     * 获取离线期间系统未读通知
     */
    fun getOfflineSystemMessages() {
        val subscribe = mDataRepository.getOfflineSystemMessages()
                .subscribe({
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    } else {
                        var type = ""
                        var msg = ""
                        var activityName = ""
                        var param_value = ""
                        var ctime = ""
                        //从data中获取参数type/msg/activityName/param_value/ctime
                        if (it.data != null) {
                            it.data.forEach {
                                type = "";msg = "";activityName = "";param_value = "";ctime = ""
                                if(it.type != "" && it.msg != "" && it.ctime != "") {
                                    type = it.type!!
                                    msg = it.msg!!
                                    ctime = it.ctime!!
                                    if(it.router?.activityName != "") {
                                        activityName = it.router?.activityName!!
                                        it.router?.param?.forEach {
                                            if(it.value != "") {
                                                param_value = it.value!!
                                            }
                                        }
                                    }
                                }
                                EventBus.getDefault().post(SystemMessageResult(type, msg, activityName, param_value, ctime))
                            }
                        }else{
                            Log.i("it.data","系统未读通知 数据为空")
                        }
                    }
                }, {
                    niceToast("网络异常")
                })

        mCompositeDisposable.add(subscribe)
    }

    class SystemMessageResult(val type: String? = "", val msg: String? = "", val activityName: String? = "",
                              val param_value: String? = "", val ctime: String? = "")

    fun getSquareUnreadMessage() {
        val userid = PreferenceUtil.getString(CURRENT_USER_ID, null)
        val subscribe = mDataRepository.getSquareUnReadMessageNum((if(mDataRepository.getUserid()!=null) mDataRepository.getUserid() else userid)!!).subscribe ({
            EventBus.getDefault().post(MainViewModel.SquareUnreadMessageVM(it.errorCode == 200,
                    it.data!!.unReadMessageNum!!.toInt(), it.data!!.lastMessageAvatar!!))
        },{

        })
        mCompositeDisposable.add(subscribe)
    }

    class SquareUnreadMessageVM(val success: Boolean, val obj: Int, val avatar: String)

    fun getSquarePopConfig() {
        val subscribe = mDataRepository.getSquarePopConfig().subscribe({
            if(it.errorCode == 200) {
                //设置配置项
                if(it.data!!.isNeedDialog == 1) {
                    MainActivity.isSquarePop = true
                } else {
                    MainActivity.isSquarePop = false
                }
                MainActivity.squarePopNumber = it.data.popupNum!!
                MainActivity.squarePopImage = it.data.backgroundImg!!
                MainActivity.squarePopGotoTarget = it.data.target!!
            }
        }, {
            niceToast("网络异常")
        })
        mCompositeDisposable.add(subscribe)
    }
}