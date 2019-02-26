package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityBigPicBinding
import com.dyyj.idd.chatmore.ui.adapter.ImagePagerOnlineAdapter
import com.dyyj.idd.chatmore.viewmodel.PicBigViewModel

class PicBigActivity : BaseActivity<ActivityBigPicBinding, PicBigViewModel>() {

    companion object {
        var photoList: ArrayList<String>? = null
        var firstPos: Int = 0

        fun start(context: Context, urls: ArrayList<String>, Pos: Int) {
            photoList = urls
            firstPos = Pos
            context.startActivity(Intent(context, PicBigActivity::class.java))
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_big_pic
    }

    override fun onViewModel(): PicBigViewModel {
        return PicBigViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "大图"

//        val requestOption = RequestOptions().fitCenter()
//        requestOption.error(R.drawable.ic_photo_default).placeholder(R.drawable.ic_photo_default)
//        Glide.with(this).load(imagePic).apply(requestOption).into(mBinding.ivPic)
        val imagePagerAdapter = ImagePagerOnlineAdapter(this@PicBigActivity, photoList!!)
        mBinding.ivPic.adapter = imagePagerAdapter
        mBinding.ivPic.offscreenPageLimit = photoList?.size!!
        mBinding.ivPic.currentItem = firstPos
    }
}
