package com.dyyj.idd.chatmore.viewmodel

import android.app.Activity
import android.content.pm.ActivityInfo
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity
import com.dyyj.idd.chatmore.utils.GifsSizeFilter
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.CaptureStrategy

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/14
 * desc   :
 */
class UploadAvatarViewModel:ViewModel() {

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


  data class AvatarVM(val obj:List<String>)
}