package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.model.network.result.UserMessageResult
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/13
 * desc   :
 */
class MeViewModel : ViewModel() {

    /**
     * 获取个人基本信息
     */
    fun netUserInfo() {
        val subscribe = mDataRepository.getUserDetailInfo().subscribe({
            EventBus.getDefault().post(
                    UserInfoVM(it.errorCode == 200,
                            it.data))
        }, {
            EventBus.getDefault().post(
                    UserInfoVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 使用过的邀请码
     */
    fun netUseInviteCode() {

        mDataRepository.postUseInviteCode().subscribe({
            EventBus.getDefault().post(
                    UseInviteCodeVM(it.data == null))
        }, {

            EventBus.getDefault().post(
                    UseInviteCodeVM(true))
        })
    }

    fun netNewMsg() {
        val subscribe = mDataRepository.getUserMessage(mDataRepository.getUserid()!!).subscribe ({
            EventBus.getDefault().post(MeUnReadMsgVM(it.errorCode == 200, it))
            val subscribe1 = mDataRepository.getSquareUnReadMessageNum(mDataRepository.getUserid()!!).subscribe({
                EventBus.getDefault().post(MeViewModel.SquareUnreadMessageVM(it.errorCode == 200,
                        it.data!!.unReadMessageNum!!.toInt(), it.data!!.lastMessageAvatar!!))
            }, {
                it.printStackTrace()
            })
            mCompositeDisposable.add(subscribe1)
        },{})
        mCompositeDisposable.add(subscribe)
    }

    class MeUnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

    class UserInfoVM(val success: Boolean, val obj: UserDetailInfoResult.Data? = null)

    class UseInviteCodeVM(val isSuccess: Boolean)

    class SquareUnreadMessageVM(val success: Boolean, val obj: Int, val avatar: String)


//    class UserUnMsgVM(val )

}