package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPublishBinding
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.ui.user.photo.Image
import com.dyyj.idd.chatmore.ui.user.photo.ImageAdapter
import com.dyyj.idd.chatmore.viewmodel.PublishViewModel
import com.dyyj.idd.chatmore.weiget.pop.PublishPop
import com.gt.common.gtchat.extension.niceChatContext
import kotlinx.android.synthetic.main.activity_publish.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class PublishActivity : BaseActivity<ActivityPublishBinding, PublishViewModel>() {

    companion object {

        var photoList: ArrayList<Image>? = null

        fun start(context: Context, list: ArrayList<Image>) {
            photoList = list
            val intent = Intent(context, PublishActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        if (photoList?.size?:0 < 9) {
//            val image = Image("local", 0, "local", 0)
//            image.type = 1
//            photoList?.add(image)
//        }
//        mViewModel.getAdapter().refreshData(photoList!!)
//    }

    @Subscribe
    fun onSubscribeRefresh(obj: PicPreActivity.RefreshPubPic) {
        photoList = obj.photos
        if (photoList?.size ?: 0 < 9) {
            val image = Image("local", 0, "local", 0)
            image.type = 1
            photoList?.add(image)
        }
        initSubmit()
        mViewModel.getAdapter().refreshData(photoList!!)
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_publish
    }

    override fun onViewModel(): PublishViewModel {
        return PublishViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "发好友动态"

        if (photoList?.size ?: 0 < 9) {
            val image = Image("local", 0, "local", 0)
            image.type = 1
            photoList?.add(image)
        }
        initSubmit()
        initRecyclerView(photoList!!)

        mBinding.etInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                initSubmit()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        mBinding.txtSubmit.setOnClickListener {
            val photoStrings = arrayListOf<String>()
            photoList?.forEach {
                if ((it.path.isNotBlank()) and (it.path != "local")) {
                    photoStrings.add(it.path)
                }
            }
            if (mBinding.etInput.text.isBlank() and (photoStrings.size == 0)) {
                return@setOnClickListener
            }
            showProgressDialog()
            val subscribe = mDataRepository.postUploadPic(photoStrings).subscribe {
//                Toast.makeText(niceChatContext(), it.size.toString(), Toast.LENGTH_SHORT).show()
                val photoes = StringBuilder()
                val length = it.size
                it.forEachIndexed { index, picResult ->
                    picResult.data?.imgFilename?.let {
                        if (picResult.data.imgFilename.isNotBlank()) {
                            photoes.append(picResult.data?.imgFilename)
                            if (index != (length - 1)) {
                                photoes.append(",")
                            }
                        }
                    }
                }
                val subscribe2 = mDataRepository.publishTopic(mDataRepository.getUserid()!!, photoes.toString(), et_input.text.toString()).subscribe({
                    closeProgressDialog()
                    if (it.errorCode == 200) {
                        Toast.makeText(niceChatContext(), "发布成功", Toast.LENGTH_SHORT).show()
                        EventBus.getDefault().post(PublishViewModel.PublishSuccess())
                        finish()
                    } else {
                        Toast.makeText(niceChatContext(), it.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }, {
                    closeProgressDialog()
                    Toast.makeText(niceChatContext(), it.message, Toast.LENGTH_SHORT).show()
                })
                mViewModel.mCompositeDisposable.add(subscribe2)
            }
            mViewModel.mCompositeDisposable.add(subscribe)
        }
    }

    @Subscribe
    fun onPicUploadResult(obj: DataRepository.PicUploadFaile) {
        Toast.makeText(niceChatContext(), obj.msg, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val pop = PublishPop(this)
        pop.show(mBinding.root, object : PublishPop.IPublishListener {
            override fun onPublish(exit: Boolean) {
                if (exit) {
                    finish()
                }
                pop.dismiss()
            }

        })
    }

    private fun initSubmit() {
        if ((photoList?.size?:0 > 1) or (!mBinding.etInput.text.isNullOrBlank())) {
            mBinding.txtSubmit.setBackgroundResource(R.drawable.shape_round_red)
            mBinding.txtSubmit.isEnabled = true
        } else {
            mBinding.txtSubmit.setBackgroundResource(R.drawable.shape_round_gray)
            mBinding.txtSubmit.isEnabled = false
        }
    }

    private fun initRecyclerView(list: java.util.ArrayList<Image>) {
        mViewModel.getAdapter().initData(list)
        mBinding.rvList.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvList.layoutManager = GridLayoutManager(this@PublishActivity, 3)
        mBinding.rvList.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().setTakePicListener(object : ImageAdapter.ITakePicListener {
            override fun onTake() {
                val tempList: ArrayList<Image> = arrayListOf()
                photoList?.forEach {
                    if (it.type != 1) {
                        tempList.add(it)
                    }
                }
                PicSelectActivity.start(this@PublishActivity, tempList, true)
            }
        })
    }
}