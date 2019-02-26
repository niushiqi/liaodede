package com.gt.common.gtchat.model.network

import com.dyyj.idd.chatmore.BuildConfig

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/08
 * desc   : 所有地址集合
 */
object NetURL {

  val type = if (BuildConfig.BUILD_TYPE == "debug") NetType.DEVELOP else NetType.MASTER
//  val type = NetType.MASTER
//   const val HOST = "http://api.ddaylove.com/"

  private const val HOST_MASTER = "http://api.liaodede.net:8083/"
  private const val HOST_DEV = "http://api.ddaylove.com/"
  private const val MQTT_MASTER = "mqtt.liaodede.net:51883"
  private const val MQTT_DEV = "api.ddaylove.com"
  private const val MQTT_MASTER_PORT = "51883"
  private const val MQTT_DEV_PORT = "51883"
  private const val SOCKET_DEV = "api.ddaylove.com"
  private const val SOCKET_MASTER = "alive.liaodede.net"
  private const val SOCKET_DEV_PORT = 51900
  private const val SOCKET_MASTER_PORT = 51900
  private const val EASEMOB_APPKEY_DEV = "1124180803228057#liaodede"
  private const val EASEMOB_APPKEY_MASTER = "1110180622253005#ldd"

  /**
   * socket端口号
   */
  val SOCKET_HOST_PORT by lazy {
    when (type) {
      NetType.DEVELOP -> return@lazy SOCKET_DEV_PORT
      NetType.MASTER -> return@lazy SOCKET_MASTER_PORT
      else -> return@lazy SOCKET_DEV_PORT
    }
  }

  /**
   * socket域名
   */
  val SOCKET_HOST by lazy {
    when (type) {
      NetType.DEVELOP -> return@lazy SOCKET_DEV
      NetType.MASTER -> return@lazy SOCKET_MASTER
      else -> return@lazy SOCKET_DEV
    }
  }

  /**
   * mqtt端口
   */
  val MQTT_HOST_PORT by lazy {
    when (type) {
      NetType.DEVELOP -> return@lazy MQTT_DEV_PORT
      NetType.MASTER -> return@lazy MQTT_MASTER_PORT
      else -> return@lazy MQTT_DEV_PORT
    }
  }

  /**
   * api地址
   */
  val HOST by lazy {
    when (type) {
      NetType.DEVELOP -> return@lazy HOST_DEV
      NetType.MASTER -> return@lazy HOST_MASTER
      else -> return@lazy HOST_DEV
    }
  }

  /**
   * mqtt地址
   */
  val MQTT_HOST by lazy {
    when (type) {
      NetType.DEVELOP -> return@lazy MQTT_DEV
      NetType.MASTER -> return@lazy MQTT_MASTER
      else -> return@lazy MQTT_DEV
    }
  }

  //用户信息
  private const val HOST_ACCOUNT_DEVELOP = "https://account.dev.egtcp.com/"
  private const val HOST_ACCOUNT_MASTER = "https://account.egtcp.com/"
  private const val HOST_ACCOUNT_QA = "https://account.qa.egtcp.com/"
  private const val HOST_ACCOUNT_STAGE = "https://account.stage.egtcp.com/"
  //H5
  private const val HOST_STATIC_DEVELOP = "https://static.dev.egtcp.com/"
  private const val HOST_STATIC_MASTER = "https://static.egtcp.com/"
  private const val HOST_STATIC_QA = "https://static.qa.egtcp.com/"
  private const val HOST_STATIC_STAGE = "https://static.stage.egtcp.com/"
  //上传下载
  private const val HOST_BASIC_DEVELOP = "https://basic.dev.egtcp.com/"
  private const val HOST_BASIC_MASTER = "https://basic.egtcp.com/"
  private const val HOST_BASIC_QA = "https://basic.qa.egtcp.com/"
  private const val HOST_BASIC_STAGE = "https://basic.stage.egtcp.com/"
  //H5页面
  private const val HOST_H5_LOCAL_DEVELOP = "file:///android_asset/h5/index.html"
  private const val HOST_H5_LOCAL_MASTER = "file:///android_asset/h5/index.html"
  private const val HOST_H5_LOCAL_QA = "file:///android_asset/h5/index.html"
  private const val HOST_H5_LOCAL_STAGE = "file:///android_asset/h5/index.html"

  private const val HOST_H5_DEVELOP = "https://tradinh5.dev.egtcp.com/"
  private const val HOST_H5_MASTER = "https://tradinh5.egtcp.com/"
  private const val HOST_H5_QA = "https://tradinh5.qa.egtcp.com/"
  private const val HOST_H5_STAGE = "https://tradinh5.stage.egtcp.com/"

  //share页面后缀
  private const val SUFFIX_SHARE_DEVELOP = "?env=develop"
  private const val SUFFIX_SHARE_MASTER = ""
  private const val SUFFIX_SHARE_QA = "?env=qa"
  private const val SUFFIX_SHARE_STAGE = "?env=stage"


  /**
   * 用户协议
   */
  val USER_AGREEMENT = "http://www.liaodede.com:8083/userprotocol.html"

  /**
   * 隐私协议
   */
  val USER_PRIVACY = "http://www.liaodede.com:8083/privacy.html"

  /**
   * 邀请
   */
  val INVITE = HOST_H5_LOCAL_DEVELOP

  /**
   * 版本升级
   */
  val VERSION_UPDATE = "${HOST}v1/tools/checkVersion"

  /**
   * 系统消息信息
   */
  val SYSTEM_MESSAGE_INFO = "${HOST}v1/tools/getXjj"

}