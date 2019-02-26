package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.database.entity.UnreadMessageEntity
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.task.fragment.ApplyFragment
import com.dyyj.idd.chatmore.ui.task.fragment.ContactsFragment
import com.dyyj.idd.chatmore.ui.task.fragment.MessageFragment
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 消息任务
 */
class MessageSystemViewModel : ViewModel() {
  /**
   * 标题
   */
  private val mTitles = arrayOf("消息", "好友", "申请")

  /**
   * Fragments
   */
  private val mFragment: Array<Fragment> = arrayOf(MessageFragment(), ContactsFragment(),
                                                   ApplyFragment())

  /**
   * 获取Adapter
   */
  fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)

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

  class UnreadMessageVM(val list: List<UnreadMessageEntity>? = null, val isSuccess: Boolean = true)
  class ShowUserInfoVM(val userId: String)
}