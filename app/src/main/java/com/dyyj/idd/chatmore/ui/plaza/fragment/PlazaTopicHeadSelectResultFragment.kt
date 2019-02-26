package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadSelectResult2Binding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicSubmitResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import java.util.*

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页
 */
class PlazaTopicHeadSelectResultFragment : BaseFragment<LayoutPlazaTopicHeadSelectResult2Binding, ViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null
    var result: ArrayList<PlazaTopicSubmitResult.TotalVote>? = null

    companion object {
        const val KEY_OBJ = "OBJ"
        const val KEY_OBJ2 = "KEY_OBJ2"

        fun create(obj: ArrayList<PlazaTopicSubmitResult.TotalVote>?, topic: PlazaTopicListResult.Topic): PlazaTopicHeadSelectResultFragment {
            val plazaTopicFragment = PlazaTopicHeadSelectResultFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_OBJ, topic)
            bundle.putParcelableArrayList(KEY_OBJ2, obj)
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
        return R.layout.layout_plaza_topic_head_select_result_2
    }

    override fun onViewModel(): ViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()

        this.result = arguments?.getParcelableArrayList(KEY_OBJ2)
        this.topic = arguments?.getParcelable(KEY_OBJ)
        loadTopicInfo()
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

    private fun loadTopicInfo() {
        val head = mBinding
        val topicObj = topic
        if (topicObj == null) {
            return
        }
        head.tvTitle.text = topicObj.squareTopicTitle

        head.tvPeopleNum.text = "${topicObj.participateInNum}人正在参与"
        head.tvCommentNum.text = "${topicObj.squareTopicCommentsNum}"

        head.tvCommentNum.setOnClickListener({
            PlazaPostedActivity.start(context!!, topic?.squareTopicId, topic?.squareTopicTitle)
        })

        if (topicObj.question.isEmpty()) {
            return
        }
        var question = topicObj.question[0]
        head.tvTitle.text = question.rules_title

        if (null == result || result!!.isEmpty() || result!!.size < 2) {
            return
        }

        val totalVote = result!![0]
        val totalVote2 = result!![1]
        val count = totalVote.num + totalVote2.num

        setOption(totalVote, count, head.option?.txtOption, head.option?.txtBg, head.option?.txtNum)
        setOption(totalVote2, count, head.option?.txtOption2, head.option?.txtBg2, head.option?.txtNum2)
    }

    private fun setOption(totalVote2: PlazaTopicSubmitResult.TotalVote, count: Int
                          , textView: TextView?, textBG: TextView?, textNum: TextView?) {
        textView?.text = totalVote2.rules_title
        textNum?.text = totalVote2.num.toString()

        val layoutParams2 = textBG?.layoutParams as LinearLayout.LayoutParams
        var weight = if (count != 0) {
            totalVote2.num * 1f / count
        } else {
            0.5f
        }

        if (weight < 0.2f) weight = 0.2f

        layoutParams2.weight = weight

        textBG.layoutParams = layoutParams2
    }

}