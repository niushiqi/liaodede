package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentMyCircleBinding
import com.dyyj.idd.chatmore.utils.KeyboardUtil
import com.dyyj.idd.chatmore.viewmodel.MessageCircleViewModel
import com.dyyj.idd.chatmore.viewmodel.MyCircleViewModel
import com.dyyj.idd.chatmore.viewmodel.PublishViewModel
import com.dyyj.idd.chatmore.weiget.pop.DynamicsCommentPop
import com.gt.common.gtchat.extension.niceToast
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.Subscribe

class MyCircleFragment : BaseFragment<FragmentMyCircleBinding, MyCircleViewModel>() {

    var lastVisibleItem = 0
    var PAGE = 1
    var loadMore = true
    var loading = false

    companion object {
        const val PAGE_SIZE = 10
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_my_circle
    }

    override fun onViewModel(): MyCircleViewModel {
        return MyCircleViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initRecycleView()
        mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, PAGE_SIZE)
        mBinding.swipeLayout.setOnRefreshListener {
            PAGE = 1
            mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, PAGE_SIZE)
        }
        mBinding.txtPub.setOnClickListener {
            PicSelectActivity.start(mActivity, arrayListOf())
        }
        mViewModel.netNewMsg()
        mBinding.rlTip.setOnClickListener {
            MyMsgActivity.start(mActivity, MyMsgActivity.SELECT_CIRCLE_PAGE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            mViewModel.netNewMsg()
            PAGE = 1
            mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, PAGE_SIZE)
        }
    }

    @Subscribe
    fun onClearUnMsg(obj: MessageCircleViewModel.ClearUnMsgVM) {
        if (obj.success) {
            mBinding.rlTip.visibility = View.GONE
        }
    }

    @Subscribe
    fun onSubscribeUnMsg(obj: MyCircleViewModel.MyUnReadMsgVM) {
        if (obj.success) {
            if (obj.obj.unReadCount ?: 0 > 0) {
                //mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.VISIBLE
                mBinding.rlTip.visibility = View.VISIBLE
                Glide.with(this).load(obj.obj.firstInfo?.avatar).asBitmap().transform(
                        CropCircleTransformation(mContext)).crossFade().error(R.drawable.bg_circle_black).placeholder(
                        R.drawable.bg_circle_black).into(mBinding.ivAvatar)
//                val requestOption = RequestOptions().circleCrop()
//                requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//                Glide.with(this).load(obj.obj.firstInfo?.avatar).apply(requestOption).into(mBinding.ivAvatar)
                mBinding.txtName.text = "${obj.obj.unReadCount}条新消息"
            }
        }
    }

    private fun initRecycleView() {
        //设置线
//        val decoration = HorizontalDividerItemDecoration.Builder(this).color(
//                ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
//                R.dimen.item_decoration_2px).build()
//        mViewModel.getAdapter().initData(list)
//        mBinding.recyclerview.addItemDecoration(decoration)
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(mActivity)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().mRealScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //最后一个可见的ITEM
                lastVisibleItem = (mBinding.recyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mViewModel.getAdapter().itemCount) {
                    if (loadMore and !loading) {
                        PAGE += 1
                        loading = true
                        mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, PAGE_SIZE)
                    }
                }
            }
        }
    }

    @Subscribe
    fun onSubscribeCircle(obj: MyCircleViewModel.MyCircleListVM) {
        loading = false
        mBinding.swipeLayout.isRefreshing = false
        if (obj.success) {
            if (obj.more) {
                mViewModel.getAdapter().moreData(obj?.vm!!)
                if (mViewModel.getAdapter().getList().size < PAGE * PAGE_SIZE) {
                    loadMore = false
                }
            } else {
                loadMore = true
                mViewModel.getAdapter().refreshData(obj?.vm!!)
            }
        } else {
            mActivity.niceToast(obj.message ?: "", Toast.LENGTH_SHORT)
        }
    }

    @Subscribe
    fun onSubscribeComment(obj: MyCircleViewModel.MyCommentSendVM) {
        mActivity.runOnUiThread {
            val commentPop = DynamicsCommentPop(mActivity)
            commentPop.setListener {
                mViewModel.netCommentSend(mDataRepository.getUserid()!!, it, obj.topicId)
            }
            commentPop.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            KeyboardUtil.showInputKeyboard(mActivity, commentPop.etInput)
        }
    }

    @Subscribe
    fun onSubscribeRefreshComment(obj: MyCircleViewModel.MyRefreshComment) {
        if (obj.success) {
            PAGE = 1
            mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, PAGE_SIZE)
        }
    }

    @Subscribe
    fun onSubscribePublishSuccessVM(obj: PublishViewModel.PublishSuccess) {
        PAGE = 1
        loading = true
        mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, PAGE_SIZE)
    }
}