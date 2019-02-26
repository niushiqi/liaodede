package com.dyyj.idd.chatmore.viewmodel

import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.model.network.result.LoginResult
import com.dyyj.idd.chatmore.utils.MD5
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */
class RegisterViewModel : ViewModel() {

    val isEnterPhone = 0
    val isEnterPass = 1
    val isEnterVerify = 2
    var STATUES = isEnterVerify

    var isPassShow: Boolean = false
    var isInviteCodeOk: Boolean = false

    private var mMobile: String? = null

    fun setMobile(mobile: String) {
        mMobile = mobile
    }

    /**
     * 发送验证码
     */
    fun netMobile() {

        if (mMobile == null) {
            EventBus.getDefault().post(MobileVM(false))
            niceChatContext().niceToast("请输入正确手机号")
            return
        }

        val subscribe = mDataRepository.getRegisterCode(mMobile!!).subscribe({
            EventBus.getDefault().post(
                    MobileVM(
                            it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
                EventBus.getDefault().post(
                        MobileVM(false))
            }
        }, {
            EventBus.getDefault().post(
                    MobileVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 验证邀请码
     */
    fun netCheckInviteCode(code: String) {
        val subscribe = mDataRepository.checkInviteCode(code).subscribe({
            EventBus.getDefault().post(
                    VerifyCodeVM(
                            it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().post(
                    VerifyCodeVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 注册
     */
    fun netRegister(code: String, password: String, inviteCode: String) {
//    if (!isInviteCodeOk) {
//      EventBus.getDefault().post(VerifyVM(false, message = "请填写正确邀请码"))
//      return
//    }
        if (mMobile == null || mMobile?.isEmpty()!!) {
            EventBus.getDefault().post(VerifyVM(false, message = "尚未填写手机号"))
            return
        }
        if (code == null || code.isEmpty()) {
            EventBus.getDefault().post(VerifyVM(false, message = "尚未填写验证码"))
            return
        }
        if (password == null || password.isEmpty()) {
            EventBus.getDefault().post(VerifyVM(false, message = "尚未填写密码"))
            return
        } else if (password.length < 6) {
            EventBus.getDefault().post(VerifyVM(false, message = "密码应为6位以上的数字字母组合"))
            return
        }

        val subscribe = mDataRepository.checkInviteCode(inviteCode).flatMap {
            if (it.errorCode == 200 || TextUtils.isEmpty(inviteCode)) {
                return@flatMap mDataRepository.register(mMobile!!, MD5.MD5(MD5.MD5(password)), code,
                        inviteCode)
            } else Flowable.just(it)
        }.subscribe({
            if (!TextUtils.isEmpty(inviteCode)) {
                EventBus.getDefault().post(VerifyCodeVM(it.errorCode == 200))
            }

            if (it.errorCode == 200) {
                netLogin(MD5.MD5(MD5.MD5(password)))
            } else {
                EventBus.getDefault().post(VerifyVM(false, message = it.errorMsg))
            }
        }, {
            EventBus.getDefault().post(VerifyVM(false, message = "注册失败"))
        })
        mCompositeDisposable.add(subscribe)
    }

    fun netLogin(verifyCode: String) {
        val subscribe = mDataRepository.login(mMobile!!, "2", verifyCode).subscribe({
            if (it.errorCode == 200) {
                EventBus.getDefault().post(
                        VerifyVM(
                                true,
                                obj = it.data))
            } else {
                EventBus.getDefault().post(
                        VerifyVM(
                                false,
                                message = it.errorMsg))
            }
        }, {
            EventBus.getDefault().post(
                    VerifyVM(
                            false,
                            message = "登录失败"))
        })
        mCompositeDisposable.add(subscribe)
    }

    fun netUserInfo(userId: String) {
        val subscribe = mDataRepository.getUserDetailInfo(userId).subscribe({
            mDataRepository.saveUserInfoEntity(
                    UserInfoEntity.createUserInfoEntity(
                            it.data!!))
            ChatApp.getInstance().userDetailInfo = it.data
        }, {            Log.i("chaogeww",it.message) })
        mCompositeDisposable.add(subscribe)
//      CompositeDisposable().add(subscribe)
    }

    fun netCheckVerifyCode(code: String) {
        val subscribe = mDataRepository.checkVerify(mMobile!!, code).subscribe(
                {
                    if (it.errorCode == 200) {
                        if (it.data?.rs == 0) {
                            EventBus.getDefault().post(VerifyOk(false, "验证码错误"))
                        } else {
                            EventBus.getDefault().post(VerifyOk(true, null))
                        }
                    } else {
                        EventBus.getDefault().post(VerifyOk(false, it.errorMsg))
                    }
                },
                { EventBus.getDefault().post(VerifyOk(false, "网络异常")) }
        )
    }

    /**
     * 保存帐号
     */
    fun saveAccountInfo(account: LoginResult.Data) {
        mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(account))
    }

    /**
     * 上报渠道号：数据分析需求，分析注册用户群渠道信息
     */
    fun uploadChannel(userId : String) {
        val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getInt("definechannel")

        val userId = userId
        val gender = "" //此处无法获取gender
        val subscribe = mDataRepository.uploadChannel(value.toString(), userId, gender).subscribe({
            Timber.tag("niushiqi").i("上传渠道号：发送成功")
        }, {
            Timber.tag("niushiqi").i("上传渠道号：网络异常")
        })
        mCompositeDisposable.add(subscribe)
    }

    class VerifyOk(val success: Boolean, val message: String?)

    class MobileVM(val mobile: Boolean)

    class VerifyVM(val verify: Boolean, val message: String? = null,
                   val obj: LoginResult.Data? = null)

    class VerifyCodeVM(val success: Boolean)
}