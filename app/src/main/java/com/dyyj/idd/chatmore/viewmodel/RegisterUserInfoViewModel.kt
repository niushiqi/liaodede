package com.dyyj.idd.chatmore.viewmodel

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.UploadAvatarResult
import com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity
import com.dyyj.idd.chatmore.utils.GifsSizeFilter
import com.gt.common.gtchat.extension.niceString
import com.gt.common.gtchat.extension.niceToast
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.CaptureStrategy
import org.greenrobot.eventbus.EventBus


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */
class RegisterUserInfoViewModel : ViewModel() {
  private var mAvatar: UploadAvatarResult.Data? = null

  fun getAvatar() = mAvatar
  /**
   * 打开相册
   */
  fun openPhoto(context: Activity, maxSelect: Int) {
    Matisse.from(context).choose(MimeType.ofImage(), false).countable(true).capture(
        false).maxSelectable(maxSelect).restrictOrientation(
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED).addFilter(
        GifsSizeFilter(320, 320, 5 * Filter.K * Filter.K)).captureStrategy(
        CaptureStrategy(true, "com.gt.common.gtchat.FileProvider")).gridExpectedSize(
        context.resources.getDimensionPixelSize(R.dimen.grid_expected_size)).thumbnailScale(
        0.85f).imageEngine(GlideEngine()).forResult(RegisterUserInfoActivity.REQUEST_CODE_CHOOSE)
  }

  /**
   * 上传头像
   */
  fun netUploadAvatar(image: List<String>) {
    val subscribe = mDataRepository.postUploadAvatar(image).subscribe({
                                                                        if (it.errorCode == 200) {
                                                                          mAvatar = it.data
                                                                          EventBus.getDefault().post(
                                                                              AvatarVM(true,
                                                                                       it.data))
                                                                        }
//                    else if (it.errorCode == 3030) {
//                        mAvatar = null
//                        EventBus.getDefault().post(AvatarVM(false, null, "头像审核失败"))
//                    }
                                                                        if (it.errorCode != 200) {
                                                                          niceToast(it.errorMsg)
                                                                          EventBus.getDefault().post(
                                                                              AvatarVM(false))
                                                                        }
                                                                      }, {
                                                                        EventBus.getDefault().post(
                                                                            AvatarVM(false, null,
                                                                                     niceString(
                                                                                         R.string.error_network_http)))
                                                                      })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 检查昵称
   */
  fun checkNickName(nickname: String, gender: Int, birthday: String) {
    val subscribe = mDataRepository.getUserDetailInfo().subscribe({
                                                                    if (it.errorCode == 200) {
                                                                      mDataRepository.postRegisterUserInfo(
                                                                          nickname = nickname,
                                                                          gender = gender,
                                                                          birthday = birthday,
                                                                          avatar = getAvatar()?.avatarFilename!!,
                                                                          areaCodeId = "",
                                                                          professionId = "").subscribe(
                                                                          {
                                                                            EventBus.getDefault().post(
                                                                                RegisterUserInfoVM(
                                                                                    it.errorCode == 200))
                                                                          }, {
                                                                            EventBus.getDefault().post(
                                                                                RegisterUserInfoVM(
                                                                                    false))
                                                                          })
                                                                      if (it.errorCode != 200) {
                                                                        niceToast(it.errorMsg)
                                                                      }
                                                                    } else {
                                                                      EventBus.getDefault().post(
                                                                          CheckNickNameVM(false))
                                                                    }
                                                                  }, {
                                                                    EventBus.getDefault().post(
                                                                        CheckNickNameVM(false,
                                                                                        "网络异常"))
                                                                  })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 完成用户信息
   */
  fun netCommitUserInfo(nickname: String, gender: Int, birthday: String) {

    if (TextUtils.isEmpty(nickname)) {
      EventBus.getDefault().post(RegisterUserInfoVM(false, "尚未填写昵称"))
      return
    }
    if (TextUtils.isEmpty(birthday)) {
      EventBus.getDefault().post(RegisterUserInfoVM(false, "尚未填写生日"))
      return
    }
    if ((getAvatar() == null) or TextUtils.isEmpty(getAvatar()?.avatar)) {
      EventBus.getDefault().post(RegisterUserInfoVM(false, "尚未选择头像"))
      return
    }
    if (gender == 0) {
      EventBus.getDefault().post(RegisterUserInfoVM(false, "尚未选择性别"))
      return
    }
//    checkNickName(nickname, gender, birthday)
    val subscribe = mDataRepository.postRegisterUserInfo(nickname = nickname, gender = gender,
                                                         birthday = birthday,
                                                         avatar = getAvatar()?.avatarFilename!!,
                                                         areaCodeId = "",
                                                         professionId = "").subscribe({
                                                                                        EventBus.getDefault().post(
                                                                                            RegisterUserInfoVM(
                                                                                                it.errorCode == 200))
                                                                                        if (it.errorCode != 200) {
                                                                                          if (it.errorCode == 3031) {
                                                                                            EventBus.getDefault().post(
                                                                                                NickNameErrorVM(false))
                                                                                          } else {
                                                                                            niceToast(
                                                                                                it.errorMsg)
                                                                                          }
                                                                                        } else {
                                                                                          /*uploadChannel(
                                                                                              mDataRepository.getUserid()!!,
                                                                                              gender = gender.toString())*/
                                                                                        }
                                                                                      }, {
                                                                                        EventBus.getDefault().post(
                                                                                            RegisterUserInfoVM(
                                                                                                false))
                                                                                      })
    mCompositeDisposable.add(subscribe)
  }

    private fun uploadChannel(userId: String, gender: String) {
        val value = ChatApp.getInstance().packageManager.getApplicationInfo(ChatApp.getInstance().packageName, PackageManager.GET_META_DATA).metaData.getInt("definechannel")

        val subscribe = mDataRepository.uploadChannel(value.toString(), userId, gender).subscribe({})
        mCompositeDisposable.add(subscribe)
    }

  class AvatarVM(val avatar: Boolean, val obj: UploadAvatarResult.Data? = null,
      val msg: String? = "")

  class RegisterUserInfoVM(val info: Boolean, val msg: String? = "")

  class CheckNickNameVM(val success: Boolean, val msg: String? = null)

  class NickNameErrorVM(val success: Boolean)
}