package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import com.gt.common.gtchat.extension.niceHtml

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页
 */
class PlazaTopicHeadTextFragment : BaseFragment<LayoutPlazaTopicHeadBinding, ViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null

    companion object {
        const val KEY_OBJ = "OBJ"

        fun create(obj: PlazaTopicListResult.Topic): PlazaTopicHeadTextFragment {
            val plazaTopicFragment = PlazaTopicHeadTextFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_OBJ, obj)
            plazaTopicFragment.arguments = bundle
            return plazaTopicFragment
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.layout_plaza_topic_head
    }

    override fun onViewModel(): ViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()

        this.topic = arguments?.getParcelable<PlazaTopicListResult.Topic>(KEY_OBJ)
        loadTopicInfo(topic)
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

    private fun loadTopicInfo(topicObj: PlazaTopicListResult.Topic?) {
        val head = mBinding
        if (head != null && topicObj != null) {

            if (!TextUtils.isEmpty(topicObj.squareTopicImage)) {
                Glide.with(context).load(topicObj.squareTopicImage).asBitmap()
                        .error(R.drawable.bg_plaza_topic_top).placeholder(R.drawable.bg_plaza_topic_top)
                        .centerCrop()
                        .into(head.bg)
            }

            head.tvTitle.text = topicObj.squareTopicTitle
            head.tvDesc.niceHtml(topicObj.squareTopicContent!!)
            head.tvPeopleNum.text = "${topicObj.squareTopicAgreeNum + topicObj.squareTopicCommentsNum}人正在参与"
            head.tvCommentNum.text = "${topicObj.squareTopicCommentsNum}"

            head.tvCommentNum.setOnClickListener({
                PlazaPostedActivity.start(context!!, topic?.squareTopicId, topic?.squareTopicTitle)
            })

        }
    }

}