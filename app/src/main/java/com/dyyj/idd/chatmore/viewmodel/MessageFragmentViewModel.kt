package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.database.entity.UnreadMessageEntity
import com.dyyj.idd.chatmore.model.network.result.UserMessageResult
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaMainFragment
import com.dyyj.idd.chatmore.ui.task.fragment.ContactsFragment
import com.dyyj.idd.chatmore.ui.task.fragment.MessageFragment
import com.dyyj.idd.chatmore.ui.user.activity.CircleFragment
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class MessageFragmentViewModel : ViewModel() {

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager, tabSelectIndex: Int): FragmentPagerAdapter {
        val mTitles = if (tabSelectIndex == 0) arrayOf("广场", "好友圈") else arrayOf("好友", "消息")
        val fragments : Array<Fragment> = if (tabSelectIndex == 0) arrayOf(PlazaMainFragment(), CircleFragment()) else arrayOf(MessageFragment(), ContactsFragment())
        return PagerAdapterV2(fm, fragments, mTitles)
    }

    fun myMatchStatus(){
        mDataRepository.myMatchStatus().subscribe({
            var title: String? = null
            when(it.data.matchingEnable){
                "1" -> title = "允许匹配"
                "2" -> title = "仅允许广场匹配"
                "3" -> title = "仅允许文字匹配"
                "4" -> title = "拒绝匹配"
            }
            EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_MESSAGE_SYSTEM_FRAGMENT,EventConstant.WHAT.WHAT_MATCH_STATUS,title))
        },{})
    }

    fun setMatchStatus(status: Int){
        mDataRepository.changeMatchingStatus(status).subscribe({},{})
    }

    /**
     * 加载未读消息
     */
    fun loadUnreadMessage() {

        val subscribe = mDataRepository.queryUnreadMessageAll().subscribe({

            EventBus.getDefault().post(
                    UnreadMessageVM(
                            list = it))
        }, {
            EventBus.getDefault().post(
                    UnreadMessageVM(
                            isSuccess = false))
        })
        mCompositeDisposable.add(subscribe)
    }

    class RefreshPeopleNumVM(val nums: Int)
    class RefreshApplyNum(val nums: Int)
    class UnreadMessageVM(val list: List<UnreadMessageEntity>? = null, val isSuccess: Boolean = true)

    fun getApplyNum() {
        val subscribe = mDataRepository.getUnHandleFriendRequest()
                .subscribe({
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                        EventBus.getDefault().post(RefreshApplyNum(0))
                    } else {
                        EventBus.getDefault().post(RefreshApplyNum(it.data.size))
                    }
                }, {
                    EventBus.getDefault().post(RefreshApplyNum(0))
                })

        mCompositeDisposable.add(subscribe)
    }

    fun netNewMsg() {
        val subscribe = mDataRepository.getMyFriendMsg().subscribe {
            EventBus.getDefault().post(SmallUnReadMsgVM(it.errorCode == 200, it))
        }
        mCompositeDisposable.add(subscribe)
    }

    class SmallUnReadMsgVM(val success: Boolean, val obj: UserMessageResult)
    class ShowUserInfoVM(val userId: String)
}