package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadSelectBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.greenrobot.eventbus.EventBus

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页
 */
class PlazaTopicHeadSelectFragment : BaseFragment<LayoutPlazaTopicHeadSelectBinding, ViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null

    companion object {
        const val KEY_OBJ = "OBJ"

        fun create(obj: PlazaTopicListResult.Topic): PlazaTopicHeadSelectFragment {
            val plazaTopicFragment = PlazaTopicHeadSelectFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_OBJ, obj)
            plazaTopicFragment.arguments = bundle
            return plazaTopicFragment
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.layout_plaza_topic_head_select
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
//            head.tvDesc.niceHtml(topicObj.squareTopicContent!!)

        val arrayListOf = arrayListOf(head.option1, head.option2, head.option3, head.option4)
        val arrayListOf2 = arrayListOf(Color.parseColor("#F0FE7374")
                , Color.parseColor("#F032C4EF")
                , Color.parseColor("#F0FEA073")
                , Color.parseColor("#F08059A8"))

        var index = 0
        question.option.forEach {
            if (index > 3) return

            var obj = it;

            val binding = arrayListOf[index]
            val color = arrayListOf2[index]
            binding?.root?.visibility = View.VISIBLE
            binding?.txtOption?.text = obj.rulesTitle
            binding?.btn?.setBackgroundColor(color)
            binding?.btn?.setOnClickListener {
                val jsonObject = JsonObject()
                jsonObject.addProperty("rulesId", question.rules_id)
                jsonObject.addProperty("optionVal", obj.Id)
                val jsonArray = JsonArray()
                jsonArray.add(jsonObject)

                val toString = jsonArray.toString()
                EventBus.getDefault().post(PlazaTopicFragment.OnSelectVM(toString, true))
            }

            Glide.with(context).load(obj.rulesImage).asBitmap()
                    .centerCrop()
                    .into(binding?.bg)

            index++
        }
    }

}