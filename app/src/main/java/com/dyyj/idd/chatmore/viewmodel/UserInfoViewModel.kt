package com.dyyj.idd.chatmore.viewmodel

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.model.mqtt.result.SignResult
import com.dyyj.idd.chatmore.model.network.result.ProfessionResult
import com.dyyj.idd.chatmore.model.network.result.UploadAvatarResult
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity
import com.dyyj.idd.chatmore.ui.user.picker.GetJsonDataUtil
import com.dyyj.idd.chatmore.ui.user.picker.JsonBean
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
 * time   : 2018/05/21
 * desc   :
 */
class UserInfoViewModel : ViewModel() {

    var userInfo: UserDetailInfoResult.DetailUserBaseInfo? = null

    var context: Context? = null

    var areaId: String? = ""
    var professionId: String? = ""

    var mProfession: ProfessionResult.Data? = null
    var mProfessionItem: ProfessionResult.Data.Profession? = null
    var mAvatar: UploadAvatarResult.Data? = null
    val mSignsList = arrayListOf<String>()

    val options1Items: ArrayList<JsonBean> = ArrayList<JsonBean>()
    val options2Items = ArrayList<ArrayList<JsonBean.CityBean>>()
    val options3Items = ArrayList<ArrayList<ArrayList<JsonBean.AreaBean>>>()

    val optionsItems: ArrayList<String> = ArrayList<String>()//职业


    fun initData(context: Context) {
        this.context = context
        netUserInfo()
        netProfessionInfo()
        GetJsonDataUtil().initJsonData(context, options1Items, options2Items, options3Items)
    }

    /**
     * 获取职业列表
     */
    private fun netProfessionInfo() {
        val subscribe = mDataRepository.getProfessionList().subscribe(
                {
                    mProfession = it.data
                    for (i in 0..(mProfession?.professionList?.size!! - 1)) {
                        optionsItems.add(mProfession?.professionList!![i]?.professionName!!)
                    }
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }
        )
        mCompositeDisposable.add(subscribe)
    }

  fun getUserInfoEntity() = mDataRepository.getUserInfoEntity()

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
        val subscribe = mDataRepository.postUploadAvatar(image).subscribe(
                {
                    mAvatar = it.data
                    EventBus.getDefault().post(AvatarVM(it.errorCode == 200, it.data))
//                    if (it.errorCode == 3030) {
//                        EventBus.getDefault().post(AvatarFailerVM())
//                    }
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                },
                { EventBus.getDefault().post(AvatarVM(false, null, niceString(R.string.error_network_http))) })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取个人基本信息
     */
    fun netUserInfo() {
        val subscribe = mDataRepository.getUserDetailInfo().subscribe(
                {
                    if (it.errorCode == 200) {
                        userInfo = it.data?.userBaseInfo
                        areaId = it.data?.userExtraInfo?.areaId
                        professionId = it.data?.userExtraInfo?.professionId
                        mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(it.data!!))
                        EventBus.getDefault().post(UserInfoVM(it.errorCode == 200, it.data))
                    }
                },
                { EventBus.getDefault().post(UserInfoVM(false)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 上传用户修改的信息
     */
    fun updateUserInfoApi(nickname: String, gender: Int, birthday: String, avatar: String, areaCodeId: String, professionId: String) {

        val subscribe = mDataRepository.postRegisterUserInfo(nickname = nickname, gender = gender, birthday = birthday, avatar = avatar, areaCodeId = areaCodeId, professionId = professionId).subscribe({
            if (it.errorCode == 200) {
                val accout = mDataRepository.getUserInfoEntity()
                accout?.username = nickname
              accout?.nickname = nickname
              accout?.avatar = "http://api.ddaylove.com/avatar/" + avatar
              accout?.birthday = birthday
              accout?.gender = gender
                mDataRepository.saveUserInfoEntity(accout)

                EventBus.getDefault().post(EditUserVM(it.errorCode == 200))
            } else {
                EventBus.getDefault().post(EditUserVM(false, it.errorMsg))
            }
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().post(EditUserVM(false, "网络异常"))
        })
        mCompositeDisposable.add(subscribe)

    }

    /**
     * 保存学校
     */
    fun saveSchool(name: String) {
        val subscribe = mDataRepository.saveSchool(name).subscribe(
                {EventBus.getDefault().post(SaveSchoolVM(it.errorCode == 200))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }},
                {EventBus.getDefault().post(SaveSchoolVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取个人标签
     */
    fun netGetTags() {
        val subscribe = mDataRepository.getSigns(mDataRepository.getUserid()!!).subscribe(
                {EventBus.getDefault().post(MyTags(it.errorCode == 200, it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }},
                {EventBus.getDefault().post(MyTags(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    class AvatarVM(val avatar: Boolean, val obj: UploadAvatarResult.Data? = null, val errorMsg: String? = "")
    class AvatarFailerVM()
    class UserInfoVM(val requestState: Boolean, val obj: UserDetailInfoResult.Data? = null)
    class EditUserVM(val editOk: Boolean, val message: String? = "")
    class SaveSchoolVM(val success: Boolean)
    class MyTags(val success: Boolean, val obj: SignResult.Data? = null)

}