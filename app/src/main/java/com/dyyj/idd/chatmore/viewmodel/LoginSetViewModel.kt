package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.model.network.result.LoginResult
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.utils.MD5
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class LoginSetViewModel : ViewModel() {

    var isPassShow = false
    val isEnterPhone = 0
    val isEnterPass = 1
    val isEnterVerify = 2
    var STATUES = isEnterPass

    private var mMobile: String? = null

    fun setMobile(mobile: String) {
        mMobile = mobile
    }

    /**
     * 发送验证码
     */
    fun netMobile() {

        if (mMobile == null || mMobile?.isEmpty()!!) {
            EventBus.getDefault().post(RegisterViewModel.MobileVM(false))
            niceChatContext().niceToast("请输入正确手机号")
            return
        }

        val subscribe = mDataRepository.getRegisterCode(mMobile!!).subscribe({
            EventBus.getDefault().post(
                    RegisterViewModel.MobileVM(
                            it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().post(
                    RegisterViewModel.MobileVM(
                            false))
        })
        mCompositeDisposable.add(subscribe)
    }

    fun netLogin(verifyCode: String) {
        val subscribe = mDataRepository.login(mMobile!!, if (STATUES == isEnterPass) "2" else "1",//登录时走1
                if (STATUES == isEnterPass) MD5.MD5(MD5.MD5(verifyCode)) else verifyCode).subscribe({
            if (it.errorCode == 200) {
                EventBus.getDefault().post(
                        LoginSetVM(
                                true,
                                obj = it.data,
                                codeMsg = it.errorCode))
            } else {
                EventBus.getDefault().post(
                        LoginSetVM(
                                false,
                                msg = it.errorMsg,
                                codeMsg = it.errorCode))
            }
        }, {
            EventBus.getDefault().post(
                    RegisterViewModel.VerifyVM(
                            false,
                            message = "登录失败"))
        })
        mCompositeDisposable.add(subscribe)
    }

    var userDetailInfo: UserDetailInfoResult.Data? = null

    fun netUserInfo(userid: String, token: String) {
        val subscribe = mDataRepository.getUserDetailInfo(userid).subscribe({

            userDetailInfo = it.data
            userDetailInfo?.user?.token = token


//        if (it.errorCode == 200) {
//            uploadChannel(userDetailInfo?.userBaseInfo?.userId!!, userDetailInfo?.userBaseInfo?.gender!!)
//        }

            mDataRepository.saveUserInfoEntity(
                    UserInfoEntity.createUserInfoEntity(
                            userDetailInfo))
            ChatApp.getInstance().userDetailInfo = userDetailInfo
            EventBus.getDefault().post(
                    LoginSetUserVM(
                            it.errorCode == 200))
        }, {
            EventBus.getDefault().post(
                    LoginSetUserVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

//    private fun uploadChannel(userId: String, gender: String) {
//        val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getInt("definechannel")
//
//        val subscribe = mDataRepository.uploadChannel(value.toString(), userId, gender).subscribe({ })
//        CompositeDisposable().add(subscribe)
//    }

    fun saveAccountInfo(obj: LoginResult.Data) {
        mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(obj))
    }

    class LoginSetVM(val success: Boolean, val msg: String? = "", val obj: LoginResult.Data? = null,
                     val codeMsg: Int?)

    class LoginSetUserVM(val success: Boolean, val msg: String? = "")

}