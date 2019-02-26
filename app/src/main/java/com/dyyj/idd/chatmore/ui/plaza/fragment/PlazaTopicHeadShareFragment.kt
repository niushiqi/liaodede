package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadShareBinding
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页 输入型
 */
class PlazaTopicHeadShareFragment : BaseFragment<LayoutPlazaTopicHeadShareBinding, ViewModel>() {

    companion object {
        const val KEY_OBJ = "OBJ"
        const val KEY_EXT = "KEY_EXT"

        fun create(img: String, ext: String? = null): PlazaTopicHeadShareFragment {
            val plazaTopicFragment = PlazaTopicHeadShareFragment()
            val bundle = Bundle()
            bundle.putString(KEY_OBJ, img)
            bundle.putString(KEY_EXT, ext)
            plazaTopicFragment.arguments = bundle
            return plazaTopicFragment
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        if (enter) {
            return AnimationUtils.loadAnimation(context, R.anim.trans_fragment_in)
        } else {
            return AnimationUtils.loadAnimation(context, R.anim.trans_fragment_out)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.layout_plaza_topic_head_share
    }

    override fun onViewModel(): ViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()

        val imgUrl = arguments?.getString(KEY_OBJ)
        val ext = arguments?.getString(KEY_EXT)

        Glide.with(context).load(imgUrl).asBitmap()
                .centerCrop()
                .into(mBinding.bg)

        if (!TextUtils.isEmpty(ext)) {
            mBinding.tvExt.text = ext
            mBinding.tvExt.visibility = View.VISIBLE
        } else {
            mBinding.tvExt.visibility = View.INVISIBLE
        }


        mBinding.contentShare.isDrawingCacheEnabled = true
        mBinding.bg.isDrawingCacheEnabled = true

        mBinding.qq.setOnClickListener {
            val bitmap = mBinding.contentShare.getDrawingCache(true)
            ShareUtils.sharePicToQQ(activity, bitmap)
        }
        mBinding.weibo.setOnClickListener {
            val bitmap = mBinding.contentShare.getDrawingCache(true)
            ShareUtils.sharePicToWEIBO(activity, bitmap)
        }
        mBinding.weixin.setOnClickListener {
            val bitmap = mBinding.contentShare.getDrawingCache(true)
            ShareUtils.sharePicToWEIXIN(activity, bitmap)
        }
        mBinding.save.setOnClickListener {
            val bitmap = mBinding.contentShare.getDrawingCache(true)

            val subscribe = Observable.just(0)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .map {
                        saveImageToGallery(context!!, bitmap)
                        return@map it
                    }
                    .subscribe({
                        context?.niceToast("成功保存到相册！")
                    }, {
                        context?.niceToast("保存到相册失败！")
                        it.printStackTrace()
                    })
            mViewModel.mCompositeDisposable.add(subscribe)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
//        onDestryEvenBus(this)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    fun saveImageToGallery(context: Context, bmp: Bitmap): String {
        // 首先保存图片
        var appDir = File(Environment.getExternalStorageDirectory(), "LiaoDeDe");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        var fileName = "${System.currentTimeMillis()}.jpg"
        var file = File(appDir, fileName)

        try {
            var fos = FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.flush();
            fos.close();
        } catch (e: Exception) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 最后通知图库更新
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())))

        return file.absolutePath
    }

}