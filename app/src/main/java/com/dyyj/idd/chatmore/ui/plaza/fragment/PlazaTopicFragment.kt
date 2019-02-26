package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaTopicBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.PlazaPostedViewModel
import com.dyyj.idd.chatmore.viewmodel.PlazaTopicViewModel
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页
 */
class PlazaTopicFragment : BaseFragment<FragmentPlazaTopicBinding, PlazaTopicViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null
    var topicID: String? = null

    companion object {
        const val KEY = "ID"

        fun create(id: String): PlazaTopicFragment {
            val plazaTopicFragment = PlazaTopicFragment()
            val bundle = Bundle()
            bundle.putString(KEY, id)
            plazaTopicFragment.arguments = bundle
            return plazaTopicFragment
        }

    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_topic
    }

    override fun onViewModel(): PlazaTopicViewModel {
        return PlazaTopicViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()

        this.topicID = arguments?.getString(KEY)
        netTopicInfo(topicID!!)

        initToobar()
        initView()
        initListener()
    }

    private fun initListener() {
        mBinding.tvPosted.setOnClickListener {
            val topicObj = topic
            if (topicObj != null) {
                PlazaPostedActivity.start(context!!, topicObj.squareTopicId, topicObj.squareTopicTitle)
                EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_post2", topicObj.squareTopicId))
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lazyLoad()
    }

    private fun initToobar() {
        val layoutToolbar = mBinding.toolbar!!

        layoutToolbar.toolbarTitleTv?.text = "话题"
        layoutToolbar.toolbarBackIv.setOnClickListener {
            activity?.onBackPressed()
        }

        layoutToolbar.txtSubmit.setOnClickListener {
            if (topic == null) return@setOnClickListener

            var topicObj = topic!!
            if (topicObj.follow == 1) {
                unFollowTopic(topicObj)
            } else {
                followTopic(topicObj)
            }
        }

        mActivity.setSupportActionBar(layoutToolbar.toolbar)
    }

    fun initView() {
        mBinding.vp.offscreenPageLimit = 0
        mBinding.vp.adapter = mViewModel.getAdapter(childFragmentManager, this.topicID!!)
        mBinding.tl4.setViewPager(mBinding.vp)
        mBinding.vp.offscreenPageLimit = 1
        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_talktheme_popular", topicID))
                } else if (position == 1) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_talktheme_new", topicID))
                }
            }

        })
    }

    private fun toggleFocused(bool: Boolean) {
        if (bool) {
            mBinding.toolbar?.txtSubmit?.text = "取消关注"
        } else {
            mBinding.toolbar?.txtSubmit?.text = "关注"
        }

    }

    /**
     * 关注话题
     */
    fun followTopic(topic: PlazaTopicListResult.Topic) {
        val subscribe = mDataRepository.postFollowTopic(topic.squareTopicId)
                .subscribe({
                    if (it.errorCode != 200 && !TextUtils.isEmpty(it.errorMsg)) {
                        mViewModel.niceToast(it.errorMsg)
                        return@subscribe
                    }

                    topic.follow = 1
                    toggleFocused(true)
                }, {
                    it.printStackTrace()
                    mViewModel.niceToast(getString(R.string.error_network_content))
                })

        mViewModel.mCompositeDisposable.add(subscribe)
    }

    /**
     * 取消关注话题
     */
    fun unFollowTopic(topic: PlazaTopicListResult.Topic) {
        val subscribe = mDataRepository.postRevokeFollowTopic(topic.squareTopicId)
                .subscribe({
                    if (it.errorCode != 200 && !TextUtils.isEmpty(it.errorMsg)) {
                        mViewModel.niceToast(it.errorMsg)
                        return@subscribe
                    }
                    topic.follow = 0
                    toggleFocused(false)
                }, {
                    it.printStackTrace()
                    mViewModel.niceToast(getString(R.string.error_network_content))
                })

        mViewModel.mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取话题信息
     */
    fun netTopicInfo(id: String) {
        val subscribe = mDataRepository.getPlazaTopic(id)
                .subscribe({
                    if (null == it.data) return@subscribe
                    topic = it.data
                    var obj = topic!!

                    if (null != topic) {
                        mBinding.toolbar?.toolbarTitleTv?.text = obj.squareTopicTitle
                        toggleFocused(obj.follow == 1)

                        if (!TextUtils.isEmpty(obj.shareImage)) {
                            replaceFragment(PlazaTopicHeadShareFragment.create(obj.shareImage!!, obj.inputValue))
                        } else if (obj.squareTopicType == 0) {
                            replaceFragment(PlazaTopicHeadTextFragment.create(obj))
                        } else if (obj.squareTopicType == 2) {
                            replaceFragment(PlazaTopicHeadSelectFragment.create(obj))
                        } else if (obj.squareTopicType == 1 && obj.question.isNotEmpty() && obj.question[0].rules_type == 2) {
                            replaceFragment(PlazaTopicHeadInputFragment.create(obj))
                        } else if (obj.squareTopicType == 1 && obj.question.isNotEmpty() && obj.question[0].rules_type == 1) {
                            replaceFragment(PlazaTopicHeadOptionFragment.create(obj))
                        }

                    }

                }, {
                    it.printStackTrace()
                })
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    fun replaceFragment(fragment: Fragment) {
        try {
            val beginTransaction = childFragmentManager.beginTransaction()
            beginTransaction.replace(R.id.head, fragment)
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Subscribe
    fun onPubishSuccess(vm: PlazaPostedViewModel.PublishSuccess) {
        //重新请求列表
        if (null != topicID) {
            netTopicInfo(topicID!!)
        }
    }

    @Subscribe
    fun onSelect(vm: OnSelectVM) {
        if (topic == null) return
        val obj = topic!!

        showProgressDialog("正在获取结果")
        val squareTopicId = obj.squareTopicId
        val squareTopicType = obj.squareTopicType.toString()
        val subscribe = mDataRepository.postQuestionOption(squareTopicId, squareTopicType, vm.answer)
                .subscribe({

                    if (obj.squareTopicType == 2) {
                        if (vm.isTwo) {
                            replaceFragment(PlazaTopicHeadSelectResultFragment.create(it.totalVote, obj))
                        } else {
                            replaceFragment(PlazaTopicHeadSelectResult4Fragment.create(it.totalVote, obj))
                        }

                        val img = it.shareImage ?: ""
                        if (img.isEmpty()) {
                            return@subscribe
                        }

                        val subscribe = Observable.timer(3000, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    replaceFragment(PlazaTopicHeadShareFragment.create(img))
                                }, {})
                        mViewModel.mCompositeDisposable.add(subscribe)
                    }

                    if (obj.squareTopicType == 1) {
                        val img = it.shareImage ?: ""
                        if (img.isEmpty()) {
                            return@subscribe
                        }
                        replaceFragment(PlazaTopicHeadShareFragment.create(img, vm.ext))
                    }

                    closeProgressDialog()

                }, {
                    it.printStackTrace()
                    mViewModel.niceToast("获取结果失败~！")
                    closeProgressDialog()
                })
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    class OnSelectVM(var answer: String, var isTwo: Boolean, var ext: String? = null)
}
