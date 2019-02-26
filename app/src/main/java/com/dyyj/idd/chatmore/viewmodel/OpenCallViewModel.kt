package com.dyyj.idd.chatmore.viewmodel

import android.util.Log
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.network.result.*
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/20
 * desc   : 开启匹配界面
 */
class OpenCallViewModel : ViewModel() {

    val CURRENT_USER_ID = "currentUserId";

    private var mRedPacket: RedPackageResult.RedPackageEntry? = null
    val mCompositeDisposable2 by lazy { CompositeDisposable() }

    fun setRedPacket(redPacket: RedPackageResult.RedPackageEntry) {
        mRedPacket = redPacket
    }

    fun getRedPacket() = mRedPacket
    /**
     * 获取红包列表
     */
    fun getRedPackageList() {
        val subscribe = mDataRepository.getRedPackageList().subscribe(
                { EventBus.getDefault().post(RedPackageVM(it.errorCode == 200, it.data)) },
                { EventBus.getDefault().post(RedPackageVM(false)) })
        mCompositeDisposable.add(subscribe)
    }

    fun getBoxOpen(channelId: String) {
        val subscribe = mDataRepository.getBoxOpen(if(mDataRepository.getUserid() != null)mDataRepository.getUserid()!! else PreferenceUtil.getString(CURRENT_USER_ID,null), channelId).subscribe(
                { EventBus.getDefault().post(OpenBoxVM(it.data?.isOpen == "1")) },
                { EventBus.getDefault().post(OpenBoxVM(true)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun getGameOpen(channelId: String) {
        val subscribe = mDataRepository.getGameOpen(if(mDataRepository.getUserid() != null)mDataRepository.getUserid()!! else PreferenceUtil.getString(CURRENT_USER_ID,null), channelId).subscribe(
                { EventBus.getDefault().post(OpenGameVM(it.data?.isOpen == "1")) },
                { EventBus.getDefault().post(OpenGameVM(true)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取当前魔石和次数
     */
    fun netCurrentCanBeStart() {
        val subscribe = mDataRepository.netCanStart().subscribe({
            EventBus.getDefault().post(OpenCallViewModel.CanBeStartVM(it))
        }, {
            niceToast(it.message)
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 主页信息
     */
    fun netMainInfo() {
        val subscribe = mDataRepository.postMain().subscribe({
            mDataRepository.mCurrentStone = it.data?.userWallet?.userStone
            Log.i("aaaa","用户当前魔石mDataRepository.mCurrentStone="+mDataRepository.mCurrentStone)
            EventBus.getDefault().post(
                    OpenCallViewModel.MainVM(it,
                            it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().post(
                    OpenCallViewModel.MainVM(isSuccess = false))
        })
        mCompositeDisposable.add(subscribe)
    }



    var isHankOpen: Boolean = false
    /**
     * 银行开关
     */
    fun netHankOpen() {
        val subscribe = mDataRepository.getHankOpen().subscribe({
            if (it.errorCode == 200) {
                isHankOpen = (it.data?.bankIsOpen == 1)
            }
        }, {

        })
        mCompositeDisposable.add(subscribe)
    }

    var subscribe: Disposable? = null

    /**
     * 红包倒计时
     */
    fun countRedPacket(time: Long, total: Long, giftId: String) {
        var count: Long = Math.abs(total)
        mCompositeDisposable2.clear()

        subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS).take(
                count + 1).map { count - it }.subscribeOn(Schedulers.computation()).observeOn(
                AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({

                    val currentTime = System.currentTimeMillis() / 1000

                    if (currentTime >= time) {
                        EventBus.getDefault().post(
                                ReadPacketVM(content = "打开", progress = 100, count = count,
                                        giftId = giftId))
                        EventBus.getDefault().post(RedPacketOpenVM())
                    } else {
                        val centerTime = it
                        val sb = StringBuilder()
                        sb.append(
                                if (centerTime / 60 < 10) "0${centerTime / 60}" else "${centerTime / 60}")
                        sb.append(":")
                        sb.append(
                                if (centerTime % 60 < 10) "0${centerTime % 60}" else "${centerTime % 60}")
                        EventBus.getDefault().post(
                                ReadPacketVM(content = sb.toString(), progress = count - it,
                                        count = count, giftId = giftId))
                    }


                }, {

                }, {
                    EventBus.getDefault().post(
                            ReadPacketVM(content = "打开", progress = 100, count = count,
                                    giftId = giftId))
                    EventBus.getDefault().post(RedPacketOpenVM())
                })
        mCompositeDisposable2.add(subscribe!!)
    }


    /**
     * 保存引导页状态
     */
    fun saveGuideStatus() {
        mDataRepository.saveGuide()
    }

    /**
     * 获取引导页状态
     */
    fun getGuideStatus(): Boolean {
        return mDataRepository.getGuideStatus()
    }

    fun saveGuideUnReadStatus() {
        mDataRepository.saveUnReadGuide()
    }

    fun getGuideUnReadStatus(): Boolean {
        return mDataRepository.getUnReadGuideStatus()
    }


    /**
     * 消耗魔石
     */
    class ConsumeMatchingVM(val isSuccess: Boolean)

    /**
     * 领取红包
     */
    class RedPackageVM(val success: Boolean, val obj: List<RedPackageResult.RedPackageEntry>? = null)

    /**
     * 主页用户信息
     */
    class MainVM(val obj: MainResult? = null, val isSuccess: Boolean? = false)

    /**
     * 获取当前用户是否有资格进入匹配
     */
    class CanBeStartVM(val obj: GetCanBeStartResult?)

    /**
     * 红包计时
     */
    class ReadPacketVM(val content: String, val progress: Long, val count: Long = 0,
                       val giftId: String)

    /**
     * 红包打开
     */
    class RedPacketOpenVM()

    /**
     * 红包关闭
     */
    class RedPacketCloseVM()

    /**
     * 分享微信状态
     */
    class ShareStatusVM(val success: Boolean)

    /**
     * 领取三连开
     */
    class GetBoxListVM(val success: Boolean, val obj: GetGiftResult? = null)

    /**
     * 刷新主页信息
     */
    class RefreshMain(val success: Boolean)

    /**
     * 获取匹配文字状态
     */
    fun getMatchingButton(){
        val subscribe = mDataRepository.getMatchingButton().subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_OPENCALL_FRAGMENT,EventConstant.WHAT.WHAT_MATCHBUTTON,it))
                    }
                },
                {
                    niceToast(it.message)
                }
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取任务列表
     */
    fun getCurrentTaskList() {
        val subscribe = mDataRepository.getCurrentTaskList().subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(OpenCallViewModel.TaskListData(true, it))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netNewMsg() {
        val userId : String
        if(mDataRepository.getUserid() == null || mDataRepository.getUserid() == "" || mDataRepository.getUserid().equals(""))
            userId = PreferenceUtil.getString(CURRENT_USER_ID, null)
        else
            userId = mDataRepository.getUserid()!!
        val subscribe = mDataRepository.getUserMessage(userId).subscribe ({
            EventBus.getDefault().post(UnReadMsgVM(it.errorCode == 200, it))
        },{})
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 收到 好友发表动态 推送处理
     */
    fun netDynamicsMsg() {
        val subscribe = mDataRepository.getMainMsg().subscribe {
            EventBus.getDefault().post(DynamicsMsgVM(it.errorCode == 200, it.data))
        }
        mCompositeDisposable.add(subscribe)
    }



//    /**
//     * 开始匹配
//     */
//    fun netStartMatching() {
//        val subscribe = mDataRepository.startMatching().subscribe({
//            Timber.tag("viewmodel").i(it.toString())
//            if (it.errorCode != 200) {
//                if (it.errorCode == 3023) {
//                    EventBus.getDefault().post(MatchingResultVM(false, it.errorMsg))
//                } else {
//                    EventBus.getDefault().post(MatchingResultVM(false, it.errorMsg))
//                }
//            } else {
//                EventBus.getDefault().post(MatchingResultVM(true))
//            }
//        }, {
//            Timber.tag("viewmodel").e(it.toString())
//            EventBus.getDefault().post(MatchingResultVM(false, "网络异常"))
////            EventBus.getDefault().post(
////                    StartMatchingVM(
////                            isSuccess = false))
//        })
//        mCompositeDisposable.add(subscribe)
//    }
//
//    class MatchingResultVM(val success: Boolean, val msg: String? = null)

    class DynamicsMsgVM(val success: Boolean, val obj: MainMsgResult.Data? = null)

    class UnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

    class TaskListData(val success: Boolean, val obj: CurrentTaskListResult? = null)

    class OpenBoxVM(val isOpen: Boolean)
    class OpenGameVM(val isOpen: Boolean)

}