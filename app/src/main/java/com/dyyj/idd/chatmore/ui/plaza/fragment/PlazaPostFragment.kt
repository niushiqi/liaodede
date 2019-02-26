package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaPostedBinding
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.adapter.PlazaCardAdapter
import com.dyyj.idd.chatmore.ui.adapter.PlazaTopicsPostedAdapter
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicsPostedActivity
import com.dyyj.idd.chatmore.ui.user.activity.PicPreActivity
import com.dyyj.idd.chatmore.ui.user.activity.PicSelectActivity
import com.dyyj.idd.chatmore.ui.user.photo.Image
import com.dyyj.idd.chatmore.ui.user.photo.ImageAdapter
import com.dyyj.idd.chatmore.viewmodel.PlazaPostedViewModel
import com.dyyj.idd.chatmore.viewmodel.PublishViewModel
import com.gt.common.gtchat.extension.niceChatContext
import kotlinx.android.synthetic.main.activity_publish.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 广场发帖界面
 */
class PlazaPostFragment : BaseFragment<FragmentPlazaPostedBinding, PlazaPostedViewModel>() {

    companion object {
        const val KEY_ID = "ID"
        const val KEY_TITLE = "KEY_TITLE"


        fun create(id: String? = null, topicTile: String? = null): PlazaPostFragment {
            val fragment = PlazaPostFragment()
            val bundle = Bundle()
            bundle.putString(KEY_ID, id)
            bundle.putString(KEY_TITLE, topicTile)
            fragment.arguments = bundle
            return fragment
        }
    }

    var topicID: String? = ""
    var photoList: ArrayList<Image> = arrayListOf()

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_posted
    }

    override fun onViewModel(): PlazaPostedViewModel {
        return PlazaPostedViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initToobar()
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lazyLoad()
    }


    private fun initToobar() {
        val layoutToolbar = mBinding.layoutToolbar!!
        layoutToolbar.toolbarTitleTv.text = "发广场动态"
        layoutToolbar.toolbarBackIv.setOnClickListener {
            activity?.onBackPressed()
        }

        layoutToolbar.txtSubmit.setOnClickListener {
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

            val subscribe = mViewModel.netPublish(topicID, photoStrings, et_input.text.toString()).subscribe({
                closeProgressDialog()
                if (it.errorCode == 200) {
                    Toast.makeText(niceChatContext(), "发布成功", Toast.LENGTH_SHORT).show()
                    EventBus.getDefault().post(PlazaPostedViewModel.PublishSuccess())
                    PreferenceUtil.commitBoolean(PlazaCardAdapter.KEY_GUDIE, true)
                    activity?.finish()
                } else {
                    Toast.makeText(niceChatContext(), it.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }, {
                closeProgressDialog()
                Toast.makeText(niceChatContext(), it.message, Toast.LENGTH_SHORT).show()
            })
            mViewModel.mCompositeDisposable.add(subscribe)
        }

        val toolbar = layoutToolbar.toolbar
        mActivity.setSupportActionBar(layoutToolbar.toolbar)
    }

    fun initView() {
        //选话题
        topicID = arguments?.getString(KEY_ID)
        val title = arguments?.getString(KEY_TITLE)
        if (title != null) {
            mBinding.tvTopic.text = "#${title}"
        } else {
            mBinding.tvTopic.text = "#随便聊聊"
        }
//
//        if (usetID == null) {
//            mBinding.llTip.visibility = View.VISIBLE
//        }

        mBinding.tvSelect.setOnClickListener({
            PlazaTopicsPostedActivity.start(it.context)
        })
        //
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

    }

    private fun initSubmit() {
        if ((photoList?.size ?: 0 > 1) or (!mBinding.etInput.text.isNullOrBlank())) {
            mBinding.layoutToolbar!!.txtSubmit.setBackgroundResource(R.drawable.shape_round_red)
            mBinding.layoutToolbar!!.txtSubmit.isEnabled = true
        } else {
            mBinding.layoutToolbar!!.txtSubmit.setBackgroundResource(R.drawable.shape_round_gray)
            mBinding.layoutToolbar!!.txtSubmit.isEnabled = false
        }
    }

    private fun initRecyclerView(list: java.util.ArrayList<Image>) {
        mViewModel.getAdapter().initData(list)
        mBinding.rvList.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvList.layoutManager = GridLayoutManager(context, 3)
        mBinding.rvList.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().setTakePicListener(object : ImageAdapter.ITakePicListener {
            override fun onTake() {
                val tempList: ArrayList<Image> = arrayListOf()
                photoList?.forEach {
                    if (it.type != 1) {
                        tempList.add(it)
                    }
                }
                PicSelectActivity.start(context!!, tempList, true)
            }
        })
    }

    @Subscribe
    fun onSubscribeRefresh(obj: PicPreActivity.RefreshPubPic) {
        photoList.clear()
        photoList.addAll(obj.photos)

        if (photoList?.size ?: 0 < 9) {
            val image = Image("local", 0, "local", 0)
            image.type = 1
            photoList?.add(image)
        }
        initSubmit()
        mViewModel.getAdapter().refreshData(photoList!!)
    }

    @Subscribe
    fun onPicUploadResult(obj: DataRepository.PicUploadFaile) {
        Toast.makeText(niceChatContext(), obj.msg, Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun onSelectTopic(obj: PlazaTopicsPostedAdapter.SelectTopicVM) {
        topicID = obj.topicID
        if (obj.title != null) {
            mBinding.tvTopic.text = "#${obj.title}"
        }
    }

}