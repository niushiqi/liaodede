package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.databinding.PopInviteFriend2Binding
import com.dyyj.idd.chatmore.viewmodel.NoWorkViewModel
import com.gt.common.gtchat.extension.niceGlide
import jp.wasabeef.glide.transformations.CropCircleTransformation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//@RuntimePermissions
class InviteFriendPop : PopupWindow {
    val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
    var mBinding: PopInviteFriend2Binding? = null;
    val mViewModel by lazy { onViewModel() }
    var context: Context? = null
    var inviteCode: String? = null

    fun setCode(code: String?): InviteFriendPop {
        this.inviteCode = code
//        mBinding?.txtInviteCode?.niceHtml(("1、扫描上方二维码，下载“聊得得”<br>" +
//                "2、输入我的邀请码：${inviteCode?.fontColorLine("#43CFCF")}，注册账号<br>" +
//                "3、自动与我成为好友，玩转各种奖励<br>" +
//                "4、邀请码名额有限，先到先得送完为止"))
        mBinding?.txtInviteCode?.text = inviteCode
        return this@InviteFriendPop
    }

    constructor(context: Context?) : super(context) {
        this.context = context
        this.inviteCode = inviteCode
        width = context?.resources?.displayMetrics?.widthPixels!!
        height = context?.resources?.displayMetrics?.heightPixels!!
        isOutsideTouchable = true
        initContentView()
    }

    private fun initContentView() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), onLayoutId(), null, false)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mBinding?.root?.layoutParams = params
        setContentView(mBinding?.root)
        animationStyle = R.style.pop_anim_style
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(android.R.color.transparent)!!))
        mBinding?.root?.setOnTouchListener { v, event ->
            dismiss()
            return@setOnTouchListener true
        }
        mBinding?.ivAvatar?.niceGlide()?.load(mDataRepository.getUserInfoEntity()?.avatar)?.asBitmap()?.transform(
                CropCircleTransformation(context))?.crossFade()?.error(R.drawable.bg_circle_black)?.placeholder(
                R.drawable.bg_circle_black)?.into(mBinding?.ivAvatar)
//        val requestOption = RequestOptions().circleCrop()
//        requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//        Glide.with(context!!).load(mDataRepository.getUserInfoEntity()?.avatar).apply(requestOption).into(mBinding?.ivAvatar!!)
        mBinding?.ivSave?.setOnClickListener {
            mBinding?.llBottom?.visibility = View.VISIBLE
            mBinding?.ivSave?.visibility = View.GONE
            mBinding?.txtTip?.visibility = View.VISIBLE
//            saveBitmap(getViewBitmap()!!)
            saveImageToGallery(getViewBitmap()!!)
        }
        mBinding?.txtName?.text = mDataRepository.getUserInfoEntity()?.nickname
//        mBinding?.txtInviteDes?.text = "1、扫描上方二维码，下载“聊得得”\n2、输入我的邀请码：，注册账号\n3、自动与我成为好友，玩转各种奖励\n4、邀请码名额有限，先到先得送完为止"
    }

    @LayoutRes
    fun onLayoutId(): Int {
        return R.layout.pop_invite_friend2
    }

    fun onViewModel(): NoWorkViewModel {
        return NoWorkViewModel()
    }

    fun getViewBitmap(): Bitmap? {
        mBinding?.llWrap?.isDrawingCacheEnabled = true;
        mBinding?.llWrap?.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mBinding?.llWrap?.layout(0, 0, mBinding?.llWrap?.measuredWidth!!,
                mBinding?.llWrap?.measuredHeight!!);
        mBinding?.llWrap?.buildDrawingCache()
        return mBinding?.llWrap?.getDrawingCache(true)
    }

    //保存文件到指定路径
    fun saveImageToGallery(bmp: Bitmap) {
        // 首先保存图片
        val storePath = Environment.getExternalStorageDirectory ().absolutePath + File.separator + "dearxy";
        val appDir = File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        val fileName = "${System.currentTimeMillis()}.jpg";
        val file = File(appDir, fileName);
        try {
            val fos = FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
//            MediaStore.Images.Media.insertImage(context?.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            val uri = Uri . fromFile (file);
            context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Toast.makeText(ChatApp.getInstance().applicationContext, "保存成功", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace();
        }
    }

}