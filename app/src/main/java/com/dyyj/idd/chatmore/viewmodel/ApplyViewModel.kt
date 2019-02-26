package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.ApplyFriendResult
import com.dyyj.idd.chatmore.ui.adapter.ApplyFriendAdapter
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   :
 */
class ApplyViewModel : ViewModel() {

  private val mAdapter by lazy { ApplyFriendAdapter() }

  /**
   * 获取Adapter
   */
  fun getAdapter() = mAdapter

  fun netApplyFriendList() {
    val subscribe = mDataRepository.getApplyFriendList().subscribe(
        { EventBus.getDefault().post(ApplyListVM(it.errorCode == 200, it.data))
          if (it.errorCode != 200) {
            niceToast(it.errorMsg)
          }},
        { EventBus.getDefault().post(ApplyListVM(false)) })
    mCompositeDisposable.add(subscribe)
  }

  class ApplyListVM(val error: Boolean, val data: ApplyFriendResult.Data? = null);

  class MessageRefreshVM
  class RefreshFriend

  class RefreshData
}