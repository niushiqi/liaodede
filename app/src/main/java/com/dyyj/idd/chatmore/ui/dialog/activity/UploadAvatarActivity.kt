package com.dyyj.idd.chatmore.ui.dialog.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogUploadAvatarBinding
import com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity
import com.dyyj.idd.chatmore.viewmodel.UploadAvatarViewModel
import com.gt.common.gtchat.extension.fontColor
import com.gt.common.gtchat.extension.niceGlide
import com.gt.common.gtchat.extension.niceHtml
import com.gt.common.gtchat.extension.niceToast
import com.zhihu.matisse.Matisse
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/14
 * desc   : 上传头像提示
 */
@RuntimePermissions
class UploadAvatarActivity:BaseActivityV2<DialogUploadAvatarBinding, UploadAvatarViewModel>() {
  private var mList  = arrayListOf<String>()
  companion object {
    fun start(context: Context) {
      val intent = Intent(context, UploadAvatarActivity::class.java)
      context.startActivity(intent)
    }
  }
  override fun onLayoutId(): Int {
    return R.layout.dialog_upload_avatar
  }

  override fun onViewModel(): UploadAvatarViewModel {
    return UploadAvatarViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    initView()
    initListenter()
  }

  private fun initView() {
    mBinding.avatarUnapproveTv.niceHtml("头像${"未审核通过".fontColor("#FF2525")}")
  }

  private fun initListenter() {
    mBinding.uploadAvatarBtn.setOnClickListener {
      if (mList.isEmpty()){
        this@UploadAvatarActivity.niceToast("未选择头像")
        return@setOnClickListener
      }
      EventBus.getDefault().postSticky(UploadAvatarViewModel.AvatarVM(mList))
      finish()
    }
    mBinding.closeIv.setOnClickListener { finish() }
    mBinding.avatarIv.setOnClickListener {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        openCameraWithPermissionCheck()
      } else {
        openCamera()
      }
    }
    mBinding.avatarShowIv.setOnClickListener {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        openCameraWithPermissionCheck()
      } else {
        openCamera()
      }
    }
  }

  /**
   * 打开拍照
   */
  @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                   Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
  fun openCamera() {
    mViewModel.openPhoto(this@UploadAvatarActivity, 1)

  }

  @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE,
                   Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
  fun showCameraDialog(request: PermissionRequest) {
    MaterialDialog.Builder(this).title(R.string.title_hint).content(
        R.string.permission_camera).positiveText(R.string.button_ok).show()
  }

  @SuppressLint("NeedOnRequestPermissionsResult")
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    onRequestPermissionsResult(requestCode, grantResults)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == RegisterUserInfoActivity.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
      val list = Matisse.obtainPathResult(data)
      list?:return
      mList.addAll(list)
      mBinding.avatarIv.visibility = View.INVISIBLE
      niceGlide().load(list[0]).asBitmap().transform(
          CropCircleTransformation(this)).crossFade().error(R.drawable.bg_circle_black).placeholder(
          R.drawable.bg_circle_black).into(mBinding.avatarShowIv)

//      val requestOption = RequestOptions().optionalCenterCrop()
//      requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//      Glide.with(mContext).load(list[0]).apply(requestOption).into(mBinding.avatarShowIv)

    }
  }
}