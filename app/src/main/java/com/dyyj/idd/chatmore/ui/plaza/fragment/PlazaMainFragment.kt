package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaMainBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.adapter.PlazaFlowCardAdapter
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicsActivity
import com.dyyj.idd.chatmore.ui.task.activity.TaskSystemActivity
import com.dyyj.idd.chatmore.ui.user.activity.MyMsgActivity
import com.dyyj.idd.chatmore.ui.user.fragment.MessageSquareFragment
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.PlazaMainViewModel
import com.dyyj.idd.chatmore.viewmodel.PlazaPostedViewModel
import com.uuch.adlibrary.AdConstant
import com.uuch.adlibrary.AdManager
import com.uuch.adlibrary.bean.AdInfo
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 广场首页
 */
class PlazaMainFragment : BaseFragment<FragmentPlazaMainBinding, PlazaMainViewModel>() {
    companion object {
        var mInstance: PlazaMainFragment? = null
        /**
         * 单例
         */
        fun instance(): PlazaMainFragment {
            if (mInstance == null) {
                return PlazaMainFragment()
            }
            return mInstance!!
        }
    }

    var focusNum = 0

    var lastVisibleItem = 0
    var PAGE = 1
    var PAGE_SIZE = 20

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_main
    }

    override fun onViewModel(): PlazaMainViewModel {
        return PlazaMainViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()

        mBinding.rlTip.setOnClickListener {
            MyMsgActivity.start(mActivity, MyMsgActivity.SELECT_SQUARE_PAGE)
        }
        initHotView()
        initRecycleView()
        initListener()
        mViewModel.netTopicList()
        mViewModel.getSquareUnreadMessage()
        mViewModel.netFocusNum()
        //开始广场时进行弹窗判断
        mViewModel.squarePopHandle(MainActivity.isSquarePop, MainActivity.squarePopNumber,
                MainActivity.squarePopImage, MainActivity.squarePopGotoTarget)
    }

    private fun initListener() {
        mBinding.tvPostTopic.setOnClickListener {
            PlazaPostedActivity.start(context!!)
            EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_post1", ""))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        if (!PreferenceUtil.getBoolean(PlazaFlowCardAdapter.KEY_GUDIE, false)) {
            guide(mBinding.tvTip)
        }

        //开始广场时进行弹窗判断
        //mViewModel.squarePopHandle(MainActivity.isSquarePop, MainActivity.squarePopNumber,
        //        MainActivity.squarePopImage, MainActivity.squarePopGotoTarget)
    }

    fun initHotView() {
        mBinding.contentHot?.root?.setOnClickListener {
            PlazaTopicsActivity.start(context!!, focusNum)
            EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_seemore", ""))
        }
        mBinding.contentHot?.rvList?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.contentHot?.rvList?.adapter = mViewModel.getHotAdapter()
    }

    private fun initRecycleView() {
        mBinding.swipeLayout.setOnRefreshListener {
            PAGE = 1
            mViewModel.isLoadingCard = false
            mViewModel.netCircleList(PAGE, PAGE_SIZE)
        }

        mViewModel.netCircleList(PAGE, PAGE_SIZE)
        mBinding.content.layoutManager = LinearLayoutManager(activity)

        val adapter = mViewModel.getAdapter()
        mBinding.content.adapter = adapter

        mBinding.content.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.content.setHasFixedSize(true)
        mViewModel.getAdapter().mRealScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //最后一个可见的ITEM
                lastVisibleItem = (mBinding.content.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && !mViewModel.isLoadingCard
                        && lastVisibleItem + 5 >= mViewModel.getAdapter().itemCount) {
                    PAGE += 1
                    mViewModel.netCircleList(PAGE, PAGE_SIZE)
                }
            }
        }
    }

    /**
     * 显示引导
     */
    private fun guide(view: View) {
        view.visibility = View.VISIBLE

        //动画
        val toFloat = DisplayUtils.dp2px(mBinding.root.context, 10f).toFloat()
        var objectAnimator = ObjectAnimator.ofFloat(view, "translationY", toFloat)
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            objectAnimator.setAutoCancel(true)
        }
        objectAnimator.start()
    }

    /**
     * 设置未读数量
     */
    fun setUnRead(num: Int, img: String?) {
        if (num > 0) {
//                mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.VISIBLE
            mBinding.rlTip.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(img)) {
                Glide.with(this).load(img).asBitmap().transform(
                        CropCircleTransformation(mActivity)).crossFade().error(
                        R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar)
            }

            mBinding.txtName.text = "${num}条新消息"
        } else {
            mBinding.rlTip.visibility = View.GONE
        }
    }

    @Subscribe
    fun OnTopicList(vm: PlazaMainViewModel.TopicListVM) {
        mViewModel.getHotAdapter().notifyDataSetChanged()
    }

    @Subscribe
    fun OnFollowNumVM(vm: PlazaMainViewModel.FollowNumVM) {
        focusNum = vm.num
    }

    @Subscribe
    fun onSubscribeCardList(obj: PlazaMainViewModel.CardListVM) {
        mBinding.swipeLayout.isRefreshing = false

        if (obj.success) {
            val adapter = mViewModel.getAdapter()
            val list = obj.vm!!

            if (obj.more) {
                adapter.moreData(list)
            } else {
                adapter.isLoadMore = true
                EventBus.getDefault().post(PlazaMainViewModel.FlowNumVM(list.size))
                adapter.refreshData(list)
            }

        }
    }

    /**
     * 清空未读消息
     */
    @Subscribe
    fun onClearSquareUnMsg(obj: MessageSquareFragment.ClearSquareUnMsgVM) {
        if (obj.success) {
//            mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.GONE
            mBinding.rlTip.visibility = View.GONE
        }
    }

    /**
     * 未读消息
     */
    @Subscribe
    fun onSubscribeUnMsg(obj: PlazaMainViewModel.SquareUnreadMessageVM) {
        if (obj.success) {
            setUnRead(obj.obj ?: 0, obj.avatar ?: "")
        }
    }

    /**
     * 发帖成功
     */
    @Subscribe
    fun onPubishSuccess(vm: PlazaPostedViewModel.PublishSuccess) {
        //重新请求列表
        mViewModel.netTopicList()
    }

    /*
     * 开启广场，调出弹窗
     */
    @Subscribe
    fun onOpenSquare(vm: PlazaMainViewModel.OpenSquareVM) {
        //开始广场时进行弹窗判断
        mViewModel.squarePopHandle(MainActivity.isSquarePop, MainActivity.squarePopNumber,
                MainActivity.squarePopImage, MainActivity.squarePopGotoTarget)
    }

    /*
     * 广场开屏弹窗 - 类似广告弹窗
     */
    @Subscribe
    fun onSquarePopVM(vm: PlazaMainViewModel.squarePopVM) {
        var advList : ArrayList<AdInfo> = arrayListOf()
        var adInfo = AdInfo()
        adInfo.activityImg = vm.squarePopImage
        //adInfo.activityImg = Uri.parse("res://" + getPackageName() + "/" + R.drawable.bg_square_tanchuang).toString()
        advList.add(adInfo)

        var adManager = AdManager(mActivity, advList)
        adManager.setOverScreen(true)
                .setDialogCloseable(true)
                .setWidthPerHeight(0.86f)
                .setOnImageClickListener(object : AdManager.OnImageClickListener {
                    override fun onImageClick(view: View?, advInfo: AdInfo?) {
                        if(vm.squarePopGotoTarget.contains("square")) {
                            //跳转话题页 - 类型为square#1
                            try {
                                var topicId = vm.squarePopGotoTarget.substring(vm.squarePopGotoTarget.indexOf("#") + 1)
                                if (!(topicId.equals("") || topicId == null)) {
                                    PlazaTopicActivity.start(mActivity, topicId)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if(vm.squarePopGotoTarget.equals("invite")) {
                            //跳转邀新页
                            InviteNewActivity.start(mActivity)
                        } else if(vm.squarePopGotoTarget.equals("task")) {
                            //跳转任务页
                            TaskSystemActivity.start(mActivity)
                        } else {
                            //do nothing
                        }
                        adManager.dismissAdDialog()
                        //(activity as MainActivity).hideFragment2()
                    }
                })
                .showAdDialog(AdConstant.ANIM_DOWN_TO_UP)
    }
}