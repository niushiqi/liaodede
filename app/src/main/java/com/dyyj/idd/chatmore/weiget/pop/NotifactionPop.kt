package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopNotifactionBinding
import com.dyyj.idd.chatmore.model.getui.NotificationHelper
import com.dyyj.idd.chatmore.viewmodel.NotifactionViewModel


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/30
 * desc   : 打开通知
 */
class NotifactionPop(context: Context) : BaseTipPop<PopNotifactionBinding, NotifactionViewModel>(
    context) {
  override fun onLayoutId(): Int {
    return R.layout.pop_notifaction
  }

  override fun onViewModel(): NotifactionViewModel {
    return NotifactionViewModel()
  }

  override fun onLayoutSet() {
    width = ViewGroup.LayoutParams.MATCH_PARENT
    height = ViewGroup.LayoutParams.MATCH_PARENT
  }

  override fun onInitListener() {
    super.onInitListener()
    mBinding.closeTv.setOnClickListener { dismiss() }
    mBinding.openBtn.setOnClickListener {
//      NotificationPageHelper.open(context)
      NotificationHelper(context).goToNotificationSettings()
      dismiss()
    }
  }

  override fun onInitView() {
    super.onInitView()
    setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.color.black_80))
    // 设置点击窗口外边窗口消失
    isOutsideTouchable = true
    // 设置此参数获得焦点，否则无法点击



    isFocusable = true
  }

  fun show(view: View, title: String, content: String, key:String):Boolean {
    if (mViewModel.getNotifactionStatus(key)) {
      return false
    }
    showAtLocation(view, Gravity.CENTER, 0, 0)
    mBinding.titleTv.text = title
    mBinding.contentTv.text = content
    return true
  }

  /**
   * 打开通知设置
   */
  private fun startSettingsActivity() {
//    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//      val intent = Intent()
//      intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
//      intent.putExtra("app_package", context?.packageName)
//      intent.putExtra("app_uid", context?.applicationInfo?.uid)
//      context?.startActivity(intent)
//    } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//      val intent = Intent()
//      intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//      intent.addCategory(Intent.CATEGORY_DEFAULT)
//      intent.data = Uri.parse("package:" + context?.packageName)
//      context?.startActivity(intent)
//    }

    val localIntent = Intent();
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (Build.VERSION.SDK_INT >= 9) {
      localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS";
      localIntent.data = Uri.fromParts("package", context?.packageName, null)
    } else if (Build.VERSION.SDK_INT <= 8) {
      localIntent.action = Intent.ACTION_VIEW
      localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
      localIntent.putExtra("com.android.settings.ApplicationPkgName", context?.packageName)
    }
    context?.startActivity(localIntent)

  }
}