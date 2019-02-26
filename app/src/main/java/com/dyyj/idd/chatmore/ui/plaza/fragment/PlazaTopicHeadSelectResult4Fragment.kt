package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadSelectResultBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicSubmitResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import me.jessyan.autosize.utils.AutoSizeUtils
import java.util.*
import android.support.v4.view.ViewCompat
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.animation.Animation
import android.view.animation.AnimationUtils


/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页
 */
class PlazaTopicHeadSelectResult4Fragment : BaseFragment<LayoutPlazaTopicHeadSelectResultBinding, ViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null
    var result: ArrayList<PlazaTopicSubmitResult.TotalVote>? = null

    companion object {
        const val KEY_OBJ = "OBJ"
        const val KEY_OBJ2 = "KEY_OBJ2"

        fun create(obj: ArrayList<PlazaTopicSubmitResult.TotalVote>?, topic: PlazaTopicListResult.Topic): PlazaTopicHeadSelectResult4Fragment {
            val plazaTopicFragment = PlazaTopicHeadSelectResult4Fragment()
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
        return R.layout.layout_plaza_topic_head_select_result
    }

    override fun onViewModel(): ViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()

        this.result = arguments?.getParcelableArrayList(KEY_OBJ2)
        this.topic = arguments?.getParcelable(KEY_OBJ)
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
        if (topicObj == null) {
            return
        }


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


        if (result == null || result!!.isEmpty()) {
            return
        }

        var count = 0;
        val options = arrayListOf(head.option1, head.option2, head.option3, head.option4)
        val colors = arrayListOf(Color.parseColor("#F0FE7374")
                , Color.parseColor("#F032C4EF")
                , Color.parseColor("#F0FEA073")
                , Color.parseColor("#F08059A8"))

        result!!.forEach {
            count += it.num
        }

        val dp2px = AutoSizeUtils.dp2px(head.root.context, 280f)
        var index = 0;
        result!!.forEach {
            count += it.num

            val color = colors[index]
            val binding = options[index]
            binding?.root?.visibility = View.VISIBLE
            binding?.txtOption?.text = it.rules_title
            binding?.btn?.text = it.num.toString()
            val precent = it.num * 1f / count
            binding?.btn?.width = (precent * dp2px).toInt()


            ViewCompat.setBackgroundTintList((binding?.btn as View), ColorStateList.valueOf(color))
            ViewCompat.setBackgroundTintMode((binding?.btn as View), PorterDuff.Mode.DARKEN)
            index++
        }

    }

}