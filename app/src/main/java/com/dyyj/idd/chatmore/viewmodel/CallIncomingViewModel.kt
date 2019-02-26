package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult
import com.dyyj.idd.chatmore.ui.main.fragment.CallFragment
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/20
 * desc   : 来电界面
 */
class CallIncomingViewModel : ViewModel() {

  private var mStartMatchingResult: StartMatchingResult? = null
  /**
   * 接听电话
   */
  fun answerCall() {
    mDataRepository.answerCall()
  }

  /**
   * 挂断电话
   */
  fun rejectCall() {
    mDataRepository.rejectCall()
  }

  /**
   * 获取来电用户信息
   */
  fun netGetMatchingUserBaseInfo(username: String) {
    val subscribe = mDataRepository.getMatchingUserBaseInfo(username).subscribe({
                                                                                  mStartMatchingResult = it
                                                                                  netGetUnknownTalkId(
                                                                                      username)

                                                                                }, {
                                                                                  EventBus.getDefault().post(
                                                                                      CallIncomingViewModel.StartMatchingVM(
                                                                                          isSuccess = false))
                                                                                })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 备选池用户获取通话ID
   */
  fun netGetUnknownTalkId(from: String) {
    CallFragment.unknownTalkId = ""
    mDataRepository.getUnknownTalkId(from).subscribe({
                                                       it.data?.talkId?.let {
                                                         CallFragment.unknownTalkId = it
                                                         EventBus.getDefault().post(
                                                             CallIncomingViewModel.StartMatchingVM(
                                                                 mStartMatchingResult))
                                                       }
                                                     }, {})
  }

  /**
   * 备选池被叫用户回传通话结果
   */
  fun reportBackupMatchingResult(type: Int) {
    val subscribe = mDataRepository.reportBackupMatchingResult(type).subscribe({}, {})
  }

  /**
   * 定时关闭界面,结束呼叫任务
   */
  fun timing() {
    val subscribe = Flowable.interval(10, 1, TimeUnit.SECONDS).take(1).subscribe({}, {}, {
      EventBus.getDefault().post(Outtimer(true))
    })

    mCompositeDisposable.add(subscribe)
  }

  class StartMatchingVM(val obj: StartMatchingResult? = null, val isSuccess: Boolean = true)

  /**
   * 呼叫超时,界面关闭
   */
  class Outtimer(val isClose: Boolean)
}