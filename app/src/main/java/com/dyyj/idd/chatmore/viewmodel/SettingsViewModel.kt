package com.dyyj.idd.chatmore.viewmodel

import android.app.Activity
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.mqtt.MqttService
import com.hyphenate.chat.EMClient

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/21
 * desc   :
 */
class SettingsViewModel:ViewModel() {

  /**
   * 清空帐号
   */
  fun cleanAccount(activity: Activity) {
    mDataRepository.cleanAccountInfo()
//    mDataRepository.saveLoginToken("")
    ChatApp.getInstance().getMQTT().disConnectMqtt()
    ChatApp.getInstance().getMQTT().disconnect()

    MqttService.stop(activity)
    EMClient.getInstance().logout(true)
  }
}