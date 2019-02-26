package com.dyyj.idd.chatmore.viewmodel

import android.widget.Toast
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.mqtt.result.SignResult
import com.dyyj.idd.chatmore.model.network.result.*
import com.dyyj.idd.chatmore.ui.adapter.GameHomeAdapter
import com.dyyj.idd.chatmore.ui.main.fragment.CallFragment
import com.dyyj.idd.chatmore.ui.main.fragment.CallOutgoingFragment
import com.dyyj.idd.chatmore.utils.DateUtils
import com.gt.common.gtchat.extension.niceToast
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
 * time   : 2018/06/19
 * desc   : 通话中
 */
class CallViewModel : ViewModel() {

  private val mAdapter by lazy { GameHomeAdapter() }

  /**
   * 获取Adapter
   */
  fun getAdapter() = mAdapter

  var mTalkId: String? = null
  var mEndTime: Int? = null
  var isFriend: Boolean = false
  var isSendVideo: Boolean = false
  var isSwitch: Boolean = false


  fun getEndTime(): Int? {
    mEndTime = DateUtils.getSecondTimestampTwo(Date())
    return mEndTime
  }

  /**
   * 挂断电话
   */
  fun endCall() {
    mDataRepository.endCall()
  }

  /**
   * 接听电话
   */
  fun answerCall() {
    mDataRepository.answerCall()
  }

  /**
   * 恢复视频（图像）数据传输
   */
  fun resumeVideoTransfer() {
    mDataRepository.resumeVideoTransfer()
  }

  /**
   * 拨打视频
   */
  fun callVideo(username: String?) {
    username ?: return
    mDataRepository.callVideo(username)
  }

  fun getTalkId() = mTalkId

//  /**
//   * 响应收到匹配通知
//   */
//  private fun netReportMatchingPush(talkid: String) {
//    val subscribe = mDataRepository.postReportMatchingPush(talkid).subscribe({
//
//                                                                               Timber.tag(
//                                                                                   "viewmodel").i(
//                                                                                   it.toString())
//                                                                             }, {
//
//                                                                               Timber.tag(
//                                                                                   "viewmodel").e(
//                                                                                   it.message)
//                                                                             })
//    mCompositeDisposable.add(subscribe)
//  }

  /**
   * 上传通话状态
   */
  fun netReportTalkingStatus(talkId: String = "0", fromUserId: String, toUserId: String,
      reportType: String, reportValue: String) {
    val subscribe = mDataRepository.postReportTalkingStatus(talkId = talkId, talkType = "1",
                                                            fromUserId = fromUserId,
                                                            toUserId = toUserId,
                                                            reportType = reportType,
                                                            reportValue = reportValue).subscribe({
                                                                                                   if (CallOutgoingFragment.talkId == null) {
                                                                                                     it.data?.talkId?.let {
                                                                                                       if (it.isNotEmpty() && it.toInt() > 0) {
                                                                                                         mTalkId = it
//                                                                                                       netReportMatchingPush(
//                                                                                                           it)
                                                                                                       }
                                                                                                     }
                                                                                                   }
                                                                                                   if (reportType == "3") {
                                                                                                     CallOutgoingFragment.talkId = null

                                                                                                   }
                                                                                                 },
                                                                                                 {
                                                                                                   Timber.tag(
                                                                                                       "")
                                                                                                 })
    if (reportType == "3") {
      CallFragment.unknownTalkId = ""
      CompositeDisposable().add(subscribe)
    } else {
      mCompositeDisposable.add(subscribe)
    }
  }

  /**
   * 检查好友关系
   */
  fun netCheckRelation(toUserId: String) {
//      niceToast("4:" + toUserId, Toast.LENGTH_LONG)
    val subscribe = mDataRepository.checkRelationApi(toUserId).subscribe({
                                                                           isFriend = it.data?.isFriend == "1"
                                                                           EventBus.getDefault().post(
                                                                               RelationVM(
                                                                                   it.errorCode == 200,
                                                                                   it.data?.isFriend == "1"))
                                                                           if (it.errorCode != 200) {
                                                                             niceToast(it.errorMsg)
                                                                           }
                                                                         }, {
                                                                           EventBus.getDefault().post(
                                                                               RelationVM(false))
                                                                         })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 定时上传通话状态
   */
  fun timerTalkingStatus(fromUserId: String, toUserId: String, startTime: Int) {
      val subscribe = Flowable.interval(5, 15, TimeUnit.SECONDS).observeOn(
              AndroidSchedulers.mainThread()).subscribe({
          if (toUserId == mDataRepository.getUserid() && CallFragment.unknownTalkId.isNotEmpty()) {
              netReportTalkingStatus(
                      talkId = CallFragment.unknownTalkId,
                      fromUserId = fromUserId,
                      toUserId = toUserId, reportType = "2",
                      reportValue = "${DateUtils.getSecondTimestampTwo(
                              Date()) - startTime}")
          } else {
              netReportTalkingStatus(talkId = getTalkId()!!,
                      fromUserId = fromUserId,
                      toUserId = toUserId,
                      reportType = "2",
                      reportValue = "${DateUtils.getSecondTimestampTwo(
                              Date()) - startTime}")
          }

      }, { Timber.tag("niushiqi-bengkui").i("timerTalkingStatus 崩溃") })
      mCompositeDisposable.add(subscribe)
  }

  fun timerReport(duration: Long) {
    val subscribe = mDataRepository.customMinuteCoin(mTalkId ?: "0", duration.toString()).subscribe(
        {
          if (it.errorCode == 200) {
            EventBus.getDefault().post(CustomCoin(it.data?.freezeCoin!!, it.data?.rewardCoin!!))
          }
        },{})
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取来电用户信息
   */
  fun netGetMatchingUserBaseInfo(username: String) {
    val subscribe = mDataRepository.getMatchingUserBaseInfo(username).subscribe({
                                                                                  EventBus.getDefault().post(
                                                                                      StartMatchingVM(
                                                                                          it))
//        niceToast("1:" + username + "-" + it.data?.matchingUserId)
                                                                                }, {
                                                                                  EventBus.getDefault().post(
                                                                                      StartMatchingVM(
                                                                                          isSuccess = false))
                                                                                })
    mCompositeDisposable.add(subscribe)
  }

    fun netTalkUserId() {
        val subscribe = mDataRepository.getLastTalkUserId().subscribe({
            EventBus.getDefault().post(
                    CallViewModel.TalkUserId(it.data?.userId!!))
        }, {})
        mCompositeDisposable.add(subscribe)
    }

  fun netRequestVideo(talkId: String, fromUserId: String, toUserId: String) {
    val subscribe = mDataRepository.requestSwitchVideo(talkId, fromUserId, toUserId).subscribe({
                                                                                                 if (it.errorCode == 200) {
                                                                                                   Toast.makeText(
                                                                                                       ChatApp.mInstance?.applicationContext,
                                                                                                       "请求已发送",
                                                                                                       Toast.LENGTH_SHORT).show()
//        isSendVideo = true
                                                                                                 }
                                                                                                 if (it.errorCode != 200) {
                                                                                                   niceToast(
                                                                                                       it.errorMsg)
                                                                                                 }
                                                                                               },
                                                                                               {})
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取标签
   */
  fun netSigns(uid: String) {
    val subscribe = mDataRepository.getSigns(uid).subscribe({
                                                              EventBus.getDefault().post(
                                                                  SignsVM(it.errorCode == 200,
                                                                          it.data))
                                                              if (it.errorCode != 200) {
                                                                niceToast(it.errorMsg)
                                                              }
                                                            }, {
                                                              EventBus.getDefault().post(
                                                                  SignsVM(false))
                                                            })
    mCompositeDisposable.add(subscribe)
  }

//    var danmuData: DanmuResult.Data? = null

  /**
   * 获取弹幕
   */
//    fun netDanmu(uid: String) {
//        val subscribe = mDataRepository.getDanmuRank(uid).subscribe({
//            if (it.errorCode == 200) {
//                danmuData = it.data
//                EventBus.getDefault().post(
//                        DanmuVM(it.errorCode == 200,
//                                it.data))
//            } else {
//                EventBus.getDefault().post(
//                        DanmuVM(false))
//            }
//            if (it.errorCode != 200) {
//                niceToast(it.errorMsg)
//            }
//        }, {
//            EventBus.getDefault().post(
//                    DanmuVM(false))
//        })
//        mCompositeDisposable.add(subscribe)
//    }

  /**
   * 友好度查询
   */
  fun timerFriendExperience(friendUserId: String) {
    val subscribe = Flowable.interval(0, 30, TimeUnit.SECONDS).take(9999).flatMap {
      return@flatMap mDataRepository.getFriendExperience(friendUserId)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                                                                                         EventBus.getDefault().post(
                                                                                             FriendExperienceVM(
                                                                                                 it,
                                                                                                 it.errorCode == 200))
                                                                                       }, {
                                                                                         EventBus.getDefault().post(
                                                                                             FriendExperienceVM(
                                                                                                 isSuccess = false))
                                                                                       })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取举报项
   */
  fun netReportReason() {
    val subscribe = mDataRepository.postReportReason().subscribe({
                                                                   EventBus.getDefault().post(
                                                                       ChatViewModel.ReportReasonVM(
                                                                           it,
                                                                           isSuccess = it.errorCode == 200))
                                                                   if (it.errorCode != 200) {
                                                                     niceToast(it.errorMsg)
                                                                   }
                                                                 }, {
                                                                   EventBus.getDefault().post(
                                                                       ChatViewModel.ReportReasonVM())
                                                                 })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 举报用户
   */
  fun netReportUser(reportUserId: String, reportReasonId: String, duration: Long) {

    val subscribe = mDataRepository.postReportUser(reportUserId, reportReasonId, mTalkId,
                                                   duration.toString()).subscribe({
                                                                                    EventBus.getDefault().post(
                                                                                        ChatViewModel.ReportUserVM(
                                                                                            it.errorCode == 200))
                                                                                    if (it.errorCode != 200) {
                                                                                      niceToast(
                                                                                          it.errorMsg)
                                                                                    }
                                                                                  }, {
                                                                                    EventBus.getDefault().post(
                                                                                        ChatViewModel.ReportUserVM())
                                                                                  })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 是否是被叫方
   */
//  fun isSwitchVideoRequest(obj: SwitchVideoRequest): Boolean {
//    return obj.requestUserId != mDataRepository.getUserInfoEntity()?.userId
//  }

  /**
   * 是否请求方
   */
//  fun isSwitchVideoResponse(obj: SwitchVideoResponse): Boolean {
//    return obj.fromUserId == mDataRepository.getUserid()
//  }


  /**
   * 获取友好度
   */
  fun getFriendExperienceResult(friendUserId: String) = mDataRepository.getFriendExperienceResult(
      friendUserId)

  /**
   * 保存友好度
   */
  fun saveFriendExperienceResult(obj: FriendExperienceResult) {
    mDataRepository.saveFriendExperienceResult(obj)
  }

  /**
   * 发送切人通知
   */
  fun netChangePerson() {
    val subscribe = mDataRepository.postSwitchPerson(mTalkId!!).subscribe({
                                                                            //                                                                        EventBus.getDefault().post(
//                                                                            OpenCallViewModel.ConsumeMatchingVM(
//                                                                                it.errorCode == 200))
                                                                            if (it.errorCode != 200) {
                                                                              niceToast(it.errorMsg)
                                                                            } else {

                                                                              EventBus.getDefault().post(
                                                                                  SwitchSendVM())
                                                                            }
                                                                          }, {
                                                                            //                                                                        EventBus.getDefault().post(
//                                                                            OpenCallViewModel.ConsumeMatchingVM(
//                                                                                false))
                                                                            niceToast("切人出现网络问题")
                                                                          })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 发送切人回执
   */
  fun netChangePersonReturn() {
    val subscribe = mDataRepository.postSwitchPersonReturn(mTalkId!!).subscribe({}, {})
    mCompositeDisposable.add(subscribe)
  }

  fun netMainInfo() {
    val subscribe = mDataRepository.postMain().subscribe({
                                                           EventBus.getDefault().post(
                                                               RefreshWallet(it.data!!))
                                                         }, {})
    mCompositeDisposable.add(subscribe)
  }

  fun getGuideStatus(): Boolean {
    return mDataRepository.getGameGuideStatus()
  }

    fun getGuideGame2Status(): Boolean {
        return mDataRepository.getGameGuideStatus2()
    }

    fun saveGuideGame2Status() {
        mDataRepository.saveGameGuide2()
    }

    fun getGuideGame1Status(): Boolean {
        return mDataRepository.getGameGuideStatus1()
    }

    fun saveGuideGame1Status() {
        mDataRepository.saveGameGuide1()
    }

  fun saveGuideStatus() {
    mDataRepository.saveGameGuide()
  }

    fun netNewMsg() {
        val subscribe = mDataRepository.getUserMessage(mDataRepository.getUserid()!!).subscribe ({
            EventBus.getDefault().post(UnReadMsgVM(it.errorCode == 200, it))
        },{})
        mCompositeDisposable.add(subscribe)
    }

    fun netDynamicsMsg() {
        val subscribe = mDataRepository.getMainMsg().subscribe {
            EventBus.getDefault().post(DynamicsMsgVM(it.errorCode == 200, it.data))
        }
        mCompositeDisposable.add(subscribe)
    }


    /**
     * 取消匹配
     */
    fun netCancelMatching() {
        val subscribe = mDataRepository.cancelMatching().subscribe({
            EventBus.getDefault().postSticky(CallMatchFailedVM(it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }

        }, {
            EventBus.getDefault().postSticky(CallMatchFailedVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 判断魔石数量
     */
    fun netStoneIsEnough() {
        val subscribe = mDataRepository.requestStoneNumber().subscribe({
            /*if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
            EventBus.getDefault().postSticky(CallMatchFailedVM(true))*/
            EventBus.getDefault().postSticky(StoneIsEnough(it.errorCode == 200))
        }, {
            EventBus.getDefault().postSticky(StoneIsEnough(false))
        })
        mCompositeDisposable.add(subscribe)

    }


    class StoneIsEnough(val isEnough:Boolean)

        //
    class CallMatchFailedVM(val isSuccess:Boolean)

    class DynamicsMsgVM(val success: Boolean, val obj: MainMsgResult.Data? = null)

    class UnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

  class StartMatchingVM(val obj: StartMatchingResult? = null, val isSuccess: Boolean = true)
  class RelationVM(val success: Boolean, var friend: Boolean = false)

  /**
   * 友好度
   */
  class FriendExperienceVM(val obj: FriendExperienceResult? = null, val isSuccess: Boolean = false)

  class SignsVM(val success: Boolean, val obj: SignResult.Data? = null)
  //    class DanmuVM(val success: Boolean, val obj: DanmuResult.Data? = null)
//    class DanmuPrepared()
  class SwitchSendVM()

  class CustomCoin(val freeCoin: String, val rewardCoin: String)
  class RefreshWallet(val obj: MainResult.Data)
  class RefreshWalletCallFragment()
    class TalkUserId(val userId: String)


}