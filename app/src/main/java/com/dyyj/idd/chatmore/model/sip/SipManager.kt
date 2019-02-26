//package com.dyyj.idd.chatmore.model.sip
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.text.TextUtils
//import android.view.Surface
//import com.dyyj.idd.chatmore.utils.ActManagerUtils
//import com.gt.common.gtchat.extension.niceToast
//import org.linphone.LinphoneManager
//import org.linphone.LinphonePreferences
//import org.linphone.LinphoneService
//import org.linphone.LinphoneUtils
//import org.linphone.core.*
//import org.linphone.mediastream.Log
//import timber.log.Timber
//
//
///**
// * author : yuhang
// * e-mail : 714610354@qq.com
// * time   : 2018/06/18
// * desc   : sip管理
// */
//class SipManager(
//    val context: Context) : LinphoneCoreListenerBase(), LinphoneAccountCreator.LinphoneAccountCreatorListener {
//
//
//  private val TAG = "SIP"
//
//
//  private var mAddress: LinphoneAddress? = null
//  private var accountCreated: Boolean = false
//  private val mPrefs: LinphonePreferences by lazy { LinphonePreferences.instance() }
//  private var mInit = false
//  /**
//   * 设置屏幕旋转
//   */
//  private fun initRotation() {
//    //屏幕旋转状态
//    val activity: Activity? = ActManagerUtils.getAppManager().currentActivity() ?: return
//    var rotation = activity?.windowManager?.defaultDisplay?.rotation ?: return
//    when (rotation) {
//      Surface.ROTATION_0 -> rotation = 0
//      Surface.ROTATION_90 -> rotation = 90
//      Surface.ROTATION_180 -> rotation = 180
//      Surface.ROTATION_270 -> rotation = 270
//    }
//
//    //设置设备旋转
////    if (LinphoneManager.isInstanciated()) {
////      LinphoneManager.getLc().setDeviceRotation(rotation)
////    }
//  }
//
//  /**
//   * 初始化
//   */
//  fun init() {
//
//    val context = this.context.applicationContext
//    //开启服务
////    if (!LinphoneService.isReady()) {
////      context.startService(
////          Intent(Intent.ACTION_MAIN).setClass(context, LinphoneService::class.java))
////    }
//
//
//  }
//
//  /**
//   * 注册监听事件
//   */
//  fun registerListener() {
//    if (mInit) return
//    mInit = true
//    /**
//     * 设置监听事件
//     */
////    val lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull()
////    if (lc != null) {
////      lc.addListener(this)
////
////      //重新注册
////      if (!LinphoneService.instance().displayServiceNotification()) {
////        lc.refreshRegisters()
////      }
////    }
//  }
//
//
//  /**
//   * 保存帐号
//   */
////  fun saveAccount(username: String, userid: String? = "", password: String,
////      displayname: String? = null, ha1: String? = null, prefix: String? = null, domain: String,
////      transport: LinphoneAddress.TransportType? = LinphoneAddress.TransportType.LinphoneTransportUdp) {
////    val username = LinphoneUtils.getDisplayableUsernameFromAddress(username)
////    val domain = LinphoneUtils.getDisplayableUsernameFromAddress(domain)
////
////    val identity = "sip:$username@$domain"
////    try {
////      mAddress = LinphoneCoreFactory.instance().createLinphoneAddress(identity)
////    } catch (e: LinphoneCoreException) {
////      Log.e(e)
////    }
////
////
////    val builder = LinphonePreferences.AccountBuilder(LinphoneManager.getLc()).setUsername(
////        username).setDomain(domain).setHa1(ha1).setUserId(userid).setDisplayName(
////        displayname).setPassword(password)
////
////    if (prefix != null) {
////      builder.setPrefix(prefix)
////    }
////
////    val forcedProxy = ""
////    if (!TextUtils.isEmpty(forcedProxy)) {
////      builder.setProxy(forcedProxy).setOutboundProxyEnabled(true).setAvpfRRInterval(5)
////    }
////    if (transport != null) {
////      builder.setTransport(transport)
////    }
////
////    try {
////      builder.saveNewAccount()
////      accountCreated = true
////    } catch (e: LinphoneCoreException) {
////      Log.e(e)
////    }
////
////  }
//
//
//  /**
//   * 加载已有帐户
//   */
//  private fun loadAccountCreator(cfg: LinphoneProxyConfig?): LinphoneAccountCreator {
//    initRotation()
//    val accountCreator = LinphoneCoreFactory.instance().createAccountCreator(
//        LinphoneManager.getLc(), LinphonePreferences.instance().xmlrpcUrl)
//    val cfgTab = LinphoneManager.getLc().proxyConfigList
//    accountCreator.setListener(this)
//    var n = -1
//    for (i in cfgTab.indices) {
//      if (cfgTab[i] == cfg) {
//        n = i
//        break
//      }
//    }
//    if (n >= 0) {
//      accountCreator.domain = mPrefs.getAccountDomain(n)
//      accountCreator.username = mPrefs.getAccountUsername(n)
//    }
//    return accountCreator
//  }
//
//  /******************************** 注册回调事件 **********************************************************/
//
//  /**
//   * 拨打电话
//   */
//  fun callOngoning(username: String, address: String) {
//    android.util.Log.i(TAG, "callOngoning")
//    LinphoneManager.getInstance().newOutgoingCall(username, address)
//  }
//
//  /**
//   * 消息接收
//   */
//  override fun messageReceived(lc: LinphoneCore?, cr: LinphoneChatRoom?,
//      message: LinphoneChatMessage?) {
//    super.messageReceived(lc, cr, message)
//    Timber.tag("SIP").i("messageReceived")
//  }
//
//  /**
//   * 注册状态
//   */
//  override fun registrationState(lc: LinphoneCore?, cfg: LinphoneProxyConfig?,
//      state: LinphoneCore.RegistrationState?, smessage: String?) {
//    super.registrationState(lc, cfg, state, smessage)
//    android.util.Log.i(TAG, "register:$state")
//    if (state === LinphoneCore.RegistrationState.RegistrationOk) {
//      if (LinphoneManager.getLc().defaultProxyConfig != null) {
//        loadAccountCreator(cfg).isAccountUsed
//      }
//    } else if (state === LinphoneCore.RegistrationState.RegistrationFailed) {
//      context.niceToast("注册失败")
//    } else if (!(state === LinphoneCore.RegistrationState.RegistrationProgress)) {
//      android.util.Log.i(TAG, "初始化中")
//    }
//  }
//
//  /**
//   * 通话状态
//   */
//  override fun callState(lc: LinphoneCore?, call: LinphoneCall?, state: LinphoneCall.State?,
//      message: String?) {
//    super.callState(lc, call, state, message)
//    android.util.Log.i("SIP", "callState:$state")
//
////    when (state) {
////      LinphoneCall.State.IncomingReceived ->{
////        CallIncomingDialogActivity.start(niceChatContext())
////      }
////    }
//  }
//
//  /******************************** 注册回调事件 **********************************************************/
//
////  override fun onReceive(context: Context?, intent: Intent?) {
////    if (!BluetoothManager.getInstance().isBluetoothHeadsetAvailable) {
////      if (intent != null && intent.hasExtra("state")) {
////        when (intent.getIntExtra("state", 0)) {
////          0//切换到扬声器
////          -> LinphoneManager.getInstance().routeAudioToSpeaker()
////
////          1//切换到耳机
////          -> {
////            LinphoneManager.getInstance().routeAudioToReceiver()
////
////          }
////        }
////      }
////    }
////  }
//
//  /******************************** 帐号回调事件 **********************************************************/
//  override fun onAccountCreatorIsAccountUsed(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorIsAccountUsed")
//  }
//
//  override fun onAccountCreatorAccountCreated(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorAccountCreated")
//  }
//
//  override fun onAccountCreatorAccountActivated(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorAccountActivated")
//  }
//
//  override fun onAccountCreatorAccountLinkedWithPhoneNumber(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorAccountLinkedWithPhoneNumber")
//  }
//
//  override fun onAccountCreatorPhoneNumberLinkActivated(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorPhoneNumberLinkActivated")
//  }
//
//  override fun onAccountCreatorIsAccountActivated(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorIsAccountActivated")
//  }
//
//  override fun onAccountCreatorPhoneAccountRecovered(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorPhoneAccountRecovered")
//  }
//
//  override fun onAccountCreatorIsAccountLinked(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorIsAccountLinked")
//  }
//
//  override fun onAccountCreatorIsPhoneNumberUsed(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorIsPhoneNumberUsed")
//  }
//
//  override fun onAccountCreatorPasswordUpdated(accountCreator: LinphoneAccountCreator?,
//      RequestStatus: LinphoneAccountCreator.RequestStatus?) {
//    Timber.tag("SIP").i("onAccountCreatorPasswordUpdated")
//  }
//
//}