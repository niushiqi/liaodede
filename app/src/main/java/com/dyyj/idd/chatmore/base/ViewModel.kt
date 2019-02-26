package com.dyyj.idd.chatmore.base

import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.weiget.pop.IncomingRequestPop
import com.hyphenate.chat.EMMessage
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/04/27
 * desc   : ViewModel V2版本
 */
abstract class ViewModel {


  val mCompositeDisposable by lazy { CompositeDisposable() }
  val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
  var mPop: IncomingRequestPop? = null

  /**
   * Activity/Fragment进入onDestory时调用
   * 避免内存泄漏
   */
  fun destroy() {
    mCompositeDisposable.clear()
  }

  /**
   * 发送忙线
   */
  open fun sendBusyMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, fromType: String) {
    val message = mDataRepository.sendBusyMessage(toUserid, type, toAvatar = toAvatar,
            toNickName = toNickName, fromType = fromType)
  }

  /**
   * 保存 发送消息 到本地記錄
   * 注：发送消息要设置为已读
   */
   fun insertMessage(msg: EMMessage) {
    mDataRepository.insertEMMesage(msg, true)
  }

  /**
   * 保存 接收消息 到本地记录
   * 注：发送消息要设置为未读
   */
  fun insertReceivedMessage(msg: EMMessage) {
    mDataRepository.insertEMMesage(msg, false)
  }

  /**
   * 发送埋点
   */
  fun sendViewEventTrackingMessage(eventName: String) {
    Timber.tag("niushiqi-vieweventtracking").i(eventName)
    val subscribe = mDataRepository.postViewEventTrackMessage(eventName).subscribe({
      Timber.tag("niushiqi-vieweventtracking").i("errorCode:"+it.errorCode+" errorMsg:"+it.errorMsg)
      if (it.errorCode == 200) {

      }
      if (it.errorCode != 200) {

      }
    }, {

    })
    mCompositeDisposable.add(subscribe)
  }
}