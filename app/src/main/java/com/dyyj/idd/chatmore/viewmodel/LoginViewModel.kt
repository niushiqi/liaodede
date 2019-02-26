package com.dyyj.idd.chatmore.viewmodel

import android.content.pm.PackageManager
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */
class LoginViewModel : ViewModel() {

    fun netSwitchApi(phone: String) {
        val subscribe = mDataRepository.checkRegister(phone).subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(SwitchVM(it.data?.rs == 1))
                    } else {
                        EventBus.getDefault().post(SwitchVM(false))
                    }
                },
                {EventBus.getDefault().post(SwitchVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 上报渠道号：数据分析需求，分析注册用户群渠道信息
     */
    fun uploadChannel() {
        val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getInt("definechannel")

        val userId = "" //此处无法获取userId
        val gender = "" //此处无法获取gender
        val subscribe = mDataRepository.uploadChannel(value.toString(), userId, gender).subscribe({
            Timber.tag("niushiqi").i("上传渠道号：发送成功")
        }, {
            Timber.tag("niushiqi").i("上传渠道号：网络异常")
        })
        mCompositeDisposable.add(subscribe)
    }

    class SwitchVM(val success: Boolean)

//  fun netLogin(token: String) {
//    val subscribe = mDataRepository.login(mDataRepository.getUserInfoEntity()?.mobile!!, "3",
//                                          token).subscribe({
//                                                             if (it.errorCode == 200) {
//                                                               EventBus.getDefault().post(
//                                                                   LoginSetVM(true, obj = it.data,
//                                                                              codeMsg = it.errorCode))
//                                                             } else {
//                                                               EventBus.getDefault().post(
//                                                                   LoginSetVM(false,
//                                                                              msg = it.errorMsg,
//                                                                              codeMsg = it.errorCode))
//                                                             }
//                                                           }, {
//                                                             EventBus.getDefault().post(
//                                                                 RegisterViewModel.VerifyVM(false,
//                                                                                            message = "登陆失败"))
//                                                           })
//    mCompositeDisposable.add(subscribe)
//  }
//
//  var userInfo: UserDetailInfoResult.Data? = null
//
//  fun netUserInfo() {
//    val subscribe = mDataRepository.getUserDetailInfo().subscribe({
//                                                                    userInfo = it.data
////        if (it.errorCode == 200) {
////            uploadChannel(userInfo?.userBaseInfo?.userId!!, userInfo?.userBaseInfo?.gender!!)
////        }
//////                    mDataRepository.saveUserWalletEntity(it.data!!)
////        if (it.errorCode == 200) {
////            uploadChannel(userInfo?.userBaseInfo?.userId!!, userInfo?.userBaseInfo?.gender!!)
////        }
//                                                                    EventBus.getDefault().post(
//                                                                        LoginSetViewModel.LoginSetUserVM(
//                                                                            it.errorCode == 200))
//                                                                  }, {
//                                                                    EventBus.getDefault().post(
//                                                                        LoginSetViewModel.LoginSetUserVM(
//                                                                            false))
//                                                                  })
//    mCompositeDisposable.add(subscribe)
//  }
//
////    private fun uploadChannel(userId: String, gender: String) {
////        val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getInt("definechannel")
////
////        val subscribe = mDataRepository.uploadChannel(value.toString(), userId, gender).subscribe({ })
////        CompositeDisposable().add(subscribe)
////    }
//
//  class LoginSetVM(val success: Boolean, val msg: String? = "", val obj: LoginResult.Data? = null,
//      val codeMsg: Int?)
//
//  class LoginSetUserVM(val success: Boolean, val msg: String? = "")

}