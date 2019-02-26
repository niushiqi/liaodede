package com.dyyj.idd.chatmore.viewmodel

import android.content.pm.PackageManager
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.LoginResult
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/12
 * desc   : 开屏页
 */
class GuideViewModel : ViewModel() {
    val CURRENT_USER_ID = "currentUserId";

    var userInfo: UserDetailInfoResult.Data? = null

  fun netLogin(token: String) {
    val subscribe = mDataRepository.login(mDataRepository.getUserInfoEntity()?.mobile!!, "3",
                                          token).subscribe({
                                                             if (it.errorCode == 200) {
                                                                 //Log.i("zhangwj","saveId="+it.data?.userId)
                                                                 PreferenceUtil.commitString(CURRENT_USER_ID, it.data?.userId)//保存用户本次登陆的的userId
                                                                 mDataRepository.saveLoginToken(
                                                                   it.data?.token!!)
                                                               EventBus.getDefault().post(
                                                                   LoginSetVM(true,
                                                                                             obj = it.data,
                                                                                             codeMsg = it.errorCode))
                                                             } else {
                                                               EventBus.getDefault().post(
                                                                   LoginSetVM(false,
                                                                                             msg = it.errorMsg,
                                                                                             codeMsg = it.errorCode))
                                                             }
                                                           }, {
                                                             EventBus.getDefault().post(
                                                                 LoginSetVM(false,msg = "登录失败"))
                                                           })
    mCompositeDisposable.add(subscribe)
  }

    private fun uploadChannel(userId: String, gender: String) {
        if (PreferenceUtil.getBoolean("uploadchannel", true)) {
            PreferenceUtil.commitBoolean("uploadchannel", false)
            val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getString("definechannel")

            val subscribe = mDataRepository.uploadChannel(value, userId, gender).subscribe({
                Timber.tag("niushiqi").i("上传渠道号：发送成功")
            }, {
                Timber.tag("niushiqi").i("上传渠道号：网络异常")
            })
            CompositeDisposable().add(subscribe)
        }
    }

    fun netUserInfo() {
    val subscribe = mDataRepository.getUserDetailInfo().subscribe({
                                                                    userInfo = it.data
//                    mDataRepository.saveUserWalletEntity(it.data!!)
//        if (it.errorCode == 200) {
//            uploadChannel(userInfo?.userBaseInfo?.userId!!, userInfo?.userBaseInfo?.gender!!)
//        }
                                                                    EventBus.getDefault().post(
                                                                        LoginSetUserVM(
                                                                            it.errorCode == 200))
                                                                  }, {
                                                                    EventBus.getDefault().post(
                                                                        LoginSetUserVM(
                                                                            false))
                                                                  })
    mCompositeDisposable.add(subscribe)
  }

  class LoginSetVM(val success: Boolean, val msg: String? = "", val obj: LoginResult.Data? = null,
      val codeMsg: Int? =0)

  class LoginSetUserVM(val success: Boolean, val msg: String? = "")
}