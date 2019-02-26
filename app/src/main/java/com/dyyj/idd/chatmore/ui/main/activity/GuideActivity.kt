package com.dyyj.idd.chatmore.ui.main.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityLaunchBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.model.getui.NotificationHelper
import com.dyyj.idd.chatmore.ui.user.activity.LoginActivity
import com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.GuideViewModel
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/12
 * desc   : 开屏页
 */
class GuideActivity :BaseActivity<ActivityLaunchBinding, GuideViewModel>(){
  override fun onLayoutId(): Int {
    return R.layout.activity_launch
  }

  override fun onViewModel(): GuideViewModel {
    return GuideViewModel()
  }

  companion object {
    fun start(context: Context) {
      context.startActivity(Intent(context, GuideActivity::class.java))
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    //设置透明状态栏
    window.requestFeature(Window.FEATURE_NO_TITLE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val window = window
      window.clearFlags(
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
      window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
      window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
      window.statusBarColor = Color.TRANSPARENT
      window.navigationBarColor = Color.TRANSPARENT
      super.onCreate(savedInstanceState)
    } else {
      super.onCreate(savedInstanceState)
    }
    onCreateEvenbus(this)
    timing()
//    notifacation()
    if(intent.getBooleanExtra("start_from_getui", false)) {
      Timber.tag("niushiqi-getui").i("from getui")
      EventTrackingUtils.joinPoint(EventBeans("ck_getuimessage", ""))
    }
  }

  /**
   * 通知
   */
  private fun notifacation() {
    val title = intent.getStringExtra("title")
    val content = intent.getStringExtra("content")
    val helper = NotificationHelper(this)
    val builder = helper.getNotification(title, content)
    helper.notify(1002, builder)
  }
  override fun onDestroy() {
    super.onDestroy()
    onDestryEvenbus(this)
  }

  /**
   * 定时
   */
  private fun timing() {
    Flowable.interval(2,4,TimeUnit.SECONDS)
        .take(1)
        .map {
          if ((mDataRepository.getUserInfoEntity() != null) and (mDataRepository.getLoginToken() != null) and (mDataRepository.getLoginToken() != null) and (!TextUtils.isEmpty(
                  mDataRepository.getLoginToken()))) {
            mViewModel.netLogin(mDataRepository.getLoginToken()!!)
          } else {
            LoginActivity.start(this)
            finish()
          }
        }
        .subscribe({},{})
  }

  @Subscribe
  fun OnSubscribVM(obj: GuideViewModel.LoginSetVM) {
    closeProgressDialog()
    if (obj.success) {
      ChatApp.getInstance().initMqtt()
      ChatApp.getInstance().initEM()
      mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(obj.obj!!))
      mViewModel.netUserInfo()
    } else {
     LoginActivity.start(this)
      finish()
    }
  }

  @Subscribe
  fun OnSubscribeVM(obj: GuideViewModel.LoginSetUserVM) {
    closeProgressDialog()
    if (obj.success) {
      if (mDataRepository.checkUserInfoNull(mViewModel.userInfo)) {
        RegisterUserInfoActivity.start(this)
      } else {
        MainActivity.startOpenCall(this)
      }
      finish()
    } else {
      niceToast("个人信息获取失败")
    }
  }
}