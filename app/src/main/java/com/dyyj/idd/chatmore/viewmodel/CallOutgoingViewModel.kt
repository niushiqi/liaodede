package com.dyyj.idd.chatmore.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.easemob.EasemobManager
import com.dyyj.idd.chatmore.model.network.result.DanmuResult
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult
import com.dyyj.idd.chatmore.model.network.result.StatusResult
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/19
 * desc   : 匹配电话
 */
class CallOutgoingViewModel : ViewModel() {


  /**
   * 开始匹配
   */
  fun netStartMatching() {
    val subscribe = mDataRepository.startMatchingManual().subscribe({
                Timber.tag("viewmodel").i(it.toString())
                if (it.errorCode != 200) {            //匹配不成功的 3 种结果
                    if (it.errorCode == 3023) {//1.
                        EventBus.getDefault().post(MatchTimeOut(it.errorMsg!!))
                    }else if (it.errorCode == 3005) {//2.魔石不足
                        EventBus.getDefault().post(MatchingFailerBecauseNotEnoughMagicStone())
                    } else {//
                        EventBus.getDefault().post(MatchingFailer())//3.就是不成功
                    }
                } else {//进入匹配池
                    //EventBus.getDefault().post(MatchSuccess())
                    timerTalkingStatus()
                    //EventBus.getDefault().post(MatchTimeOut())
                }
            }, {
                Timber.tag("viewmodel").e(
                it.toString())
                // EventBus.getDefault().post(
                //StartMatchingVM(
                //isSuccess = false))
            })
    mCompositeDisposable.add(subscribe)
  }

    fun startTextMatching(){
        val subscribe = mDataRepository.startTextMatching().subscribe({
            Timber.d(it.toString())
            if (it.errorCode == 200){
                netGetMatchingUserBaseInfo(it.data?.matchUserId!!)
            } else if (it.errorCode == 37001) {
                EventBus.getDefault().post(TimeNotEnough(it.errorCode))
            } else {
                if (it.errorCode == 3005) {
                    EventBus.getDefault().post(MatchingFailerBecauseNotEnoughMagicStone())
                } else {
                    niceToast(it.errorMsg)
                    EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CALLOUTGOING_FRAGMENT, EventConstant.WHAT.WHAT_TXT_MATCH_FAIL, it))
                }
            }
        },{
            niceToast(it.message)
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取来电用户信息
     */
    fun netGetMatchingUserBaseInfo(username: String) {
        val subscribe = mDataRepository.getMatchingUserBaseInfo(username).subscribe({
            EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CALLOUTGOING_FRAGMENT, EventConstant.WHAT.WHAT_TEXT_MATCHING,it))
        }, {})
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

  var subscribe: Disposable? = null
  fun timerTalkingStatus() {
      Timber.tag("niushiqi-tonghua").i("开始60s计时")

      subscribe = Flowable.interval(5, 5, TimeUnit.SECONDS).observeOn(
              AndroidSchedulers.mainThread()).subscribe({
          if (it.toInt() == 12) {
              Timber.tag("niushiqi-tonghua").i("60s结束，失败")
              /*mDataRepository.endCall()
              niceToast("挂断,本次通话结束")*/
              ChatApp.getInstance().getDataRepository().endCall()
              EasemobManager.isStartMatch = false
              EventBus.getDefault().post(MatchingFailer())
              netRestoreMatchingStone()
          }
      }, { Timber.tag("niushiqi-bengkui").i("timerTalkingStatus 请求失败") })
      mCompositeDisposable.add(subscribe!!)
  }

  fun netRestoreMatchingStone() {
    val subscribe = mDataRepository.restoreMatchingStone().subscribe({
                                                                       if (it.errorCode != 200) {
                                                                         niceToast(it.errorMsg)
                                                                       }
                                                                     }, {Log.i("zhangwj","请求失败")})
    mCompositeDisposable.add(subscribe)
  }

  fun getUserid() = mDataRepository.getUserid()

  /**
   * 拨打电话
   */
  fun callOutgoing(username: String) {
    Log.e("hunluan", "bochu")
    mDataRepository.callVoice(username)
  }

  var danmuData: DanmuResult.Data? = null

  /**
   * 获取弹幕
   */
  fun netDanmu(uid: String) {
    val subscribe = mDataRepository.getDanmuRank(uid).subscribe({
                                                                  if (it.errorCode == 200) {
                                                                    danmuData = it.data
                                                                    EventBus.getDefault().post(
                                                                        DanmuCallVM(
                                                                            it.errorCode == 200,
                                                                            it.data))
                                                                  } else {
                                                                    EventBus.getDefault().post(
                                                                        DanmuCallVM(false))
                                                                  }
                                                                  if (it.errorCode != 200) {
                                                                    niceToast(it.errorMsg)
                                                                  }
                                                                }, {
                                                                  EventBus.getDefault().post(
                                                                      DanmuCallVM(false))
                                                                })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 备选池被叫用户回传通话结果
   */
  fun reportBackupMatchingResult(type: Int) {
    val subscribe = mDataRepository.reportBackupMatchingResult(type).subscribe({}, {})
  }

  /**
   * 是否超一天
   */
  fun isShow(): Boolean {
    return mDataRepository.getConvention()
  }

    /**
     * MQTT推送定时器 - 定时30秒
     */
    @SuppressLint("CheckResult")
    fun timber(activity: Activity) {
        //开启MQTT推送定时器后要关闭匹配定时器（60s）
        if (subscribe != null) {
            Timber.tag("niushiqi-tonghua").i("销毁60s定时器")
            subscribe?.dispose()
        }
        //开启MQTT推送定时器（30s）
        Timber.tag("niushiqi-tonghua").i("30s开始计时")
        val subscribe1 = Flowable.interval(30, 1, TimeUnit.SECONDS).take(1).subscribe {
            // 超時操作
            Timber.tag("niushiqi-tonghua").i("30s结束，失败")
            ChatApp.getInstance().getDataRepository().endCall()
            EasemobManager.isStartMatch = false
            EventBus.getDefault().post(CallOutgoingViewModel.MatchingFailer())
        }
        mCompositeDisposable.add(subscribe1)
    }

    fun matchingTimeOut(){
        mDataRepository.matchingTimeOut().subscribe({
            EventBus.getDefault().post(CallOutgoingViewModel.MatchingTimeOut(it))
        },{
            Log.i("tag","")
        })
    }

  class StartMatchingVM(val obj: StartMatchingResult? = null, val talkId: String,
      val fromid: String, val toid: String, val isSuccess: Boolean = true)

  class DanmuCallVM(val success: Boolean, val obj: DanmuResult.Data? = null)
    class MatchingTimeOut(val obj: StatusResult)
    class MatchingFailer()
  class OpenConvention()

    //次数不足弹窗
  class TimeNotEnough(val obj: Int?)

  class MatchingFailerBecauseNotEnoughMagicStone()
  class MatchTimeOut(val message: String)
  class MatchSuccess()
  //class ReceiveEasemobCallingForPiPeiVM(val fromUserId: String, val toUserId: String)

  fun reportDialUserId(fromid: String, toid: String, talkId: String) {
    val subscribe = mDataRepository.reportDialUserId(fromid, toid, talkId).subscribe({
                                                                                       Log.e("TAG-",
                                                                                             "上报通话成功 $it")
                                                                                     }, { })
    mCompositeDisposable.add(subscribe)
  }

    /**
     * 挂电话
     */
    fun endCall() {
        mDataRepository.endCall()
    }

    /**
     * 取消匹配
     */
    fun netCancelMatching() {
        val subscribe = mDataRepository.cancelMatching().subscribe({
            if (it.errorCode != 200) {
                niceChatContext().niceToast(it.errorMsg!!)
            }
        }, {
            niceChatContext().niceToast("网络异常啦")
        })
        mCompositeDisposable.add(subscribe)
    }

}