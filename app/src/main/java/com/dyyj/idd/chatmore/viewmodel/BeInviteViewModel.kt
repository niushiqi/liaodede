package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/25
 * desc   :邀请我的人
 */
class BeInviteViewModel : ViewModel() {

  /**
   * 使用邀请码
   */
  fun netRegVerifyCode(inviteCode: String) {
    mDataRepository.postRegVerifyCode(inviteCode).subscribe({
                                                              EventBus.getDefault().post(
                                                                  BeInviteVM(it.errorCode == 200))
                                                            }, {
                                                              EventBus.getDefault().post(
                                                                  BeInviteVM(false))
                                                            })
  }

  /**
   * 验证邀请码
   */
  class BeInviteVM(val isSuucees: Boolean)

  /**
   * 成功通知
   */
  class BeInviteSuccessVM()
}