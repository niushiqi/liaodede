package com.dyyj.idd.chatmore.model.getui

import android.content.Context
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.ui.main.activity.GeTuiAcktivity
import com.igexin.sdk.PushManager

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/12
 * desc   : 个推管理
 */
object  GeTuiManaage {

  /**
   * 初始化个推
   */
  fun init(context: Context) {
    try {
      val  method = PushManager::class.java.getDeclaredMethod("registerPushActivity", Context::class.java,Class::class.java)
      method?.isAccessible = true
      method.invoke(PushManager.getInstance(), ChatApp.getInstance().applicationContext,
                    GeTuiAcktivity::class.java)
    } catch (e:Throwable ) {
      e.printStackTrace()
    }
    PushManager.getInstance().initialize<DemoPushService>(context.applicationContext, DemoPushService::class.java)
    // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
    // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
    // IntentService, 必须在 AndroidManifest 中声明)
    PushManager.getInstance().registerPushIntentService(context.applicationContext,
                                                        DemoIntentService::class.java)
  }

}