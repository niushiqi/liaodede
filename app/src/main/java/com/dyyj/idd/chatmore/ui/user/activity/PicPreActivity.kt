package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPicPreBinding
import com.dyyj.idd.chatmore.ui.adapter.ImagePagerAdapter
import com.dyyj.idd.chatmore.ui.adapter.PrePhotoAdapter
import com.dyyj.idd.chatmore.ui.user.photo.Image
import com.dyyj.idd.chatmore.viewmodel.PicPreViewModel
import org.greenrobot.eventbus.EventBus

class PicPreActivity : BaseActivity<ActivityPicPreBinding, PicPreViewModel>() {

    companion object {

        var fromPublish: Boolean = false

        var photoList: ArrayList<Image>? = null

        fun start(context: Context, list: ArrayList<Image>, fromPub: Boolean? = false) {
            photoList = list
            fromPublish = fromPub?:false
            context.startActivity(Intent(context, PicPreActivity::class.java))
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_pic_pre
    }

    override fun onViewModel(): PicPreViewModel {
        return PicPreViewModel()
    }

    private var resultList: ArrayList<Image> = arrayListOf()
    private var currentPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "预览"
        initViewPager()
        initRecycler(photoList!!)
        photoList?.forEach {
            resultList.add(it)
        }
        mBinding.txtOk.setText("完成(${resultList.size}/9)")
        mBinding.imageView26.setOnClickListener {
            if (resultList.contains(photoList?.get(currentPos)) ?: true) {
                resultList.remove(photoList?.get(currentPos))
                mBinding.imageView26.setImageResource(R.drawable.ic_photo_select2)
                mBinding.txtOk.setText("完成(${resultList.size}/9)")
            } else {
                resultList.add(photoList?.get(currentPos)!!)
                mBinding.imageView26.setImageResource(R.drawable.ic_photo_selected)
                mBinding.txtOk.setText("完成(${resultList.size}/9)")
            }
        }
        mBinding.txtOk.setOnClickListener {
            EventBus.getDefault().post(PicChangeVM(resultList))
            finish()
            if (fromPublish) {
                EventBus.getDefault().post(RefreshPubPic(resultList))
            } else {
                PublishActivity.start(this@PicPreActivity, resultList)
            }
        }
    }

    private fun initRecycler(list: ArrayList<Image>) {
        mViewModel.getAdapter().initData(list)
        mBinding.rvSmallList.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        val linearLayoutManager = LinearLayoutManager(this@PicPreActivity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rvSmallList.layoutManager = linearLayoutManager
        mBinding.rvSmallList.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().setSmallListener(object : PrePhotoAdapter.ISmallPhotoListener {
            override fun onSelect(position: Int) {
                mBinding.vpPre.currentItem = position
                imagePagerAdapter?.notifyDataSetChanged()
            }
        })
    }

    private var imagePagerAdapter: ImagePagerAdapter? = null
    private fun initViewPager() {
        imagePagerAdapter = ImagePagerAdapter(photoList!!)
        mBinding.vpPre.adapter = imagePagerAdapter
        mBinding.vpPre.offscreenPageLimit = photoList?.size!!
        mBinding.vpPre.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mViewModel.getAdapter().setCurrentSelect(position)
                currentPos = position
                if (resultList.contains(photoList?.get(currentPos)) ?: false) {
                    mBinding.imageView26.setImageResource(R.drawable.ic_photo_selected)
                } else {
                    mBinding.imageView26.setImageResource(R.drawable.ic_photo_select2)
                }
            }

        })
    }

    class PicChangeVM(val photos: ArrayList<Image>)
    class RefreshPubPic(val photos: ArrayList<Image>)
}