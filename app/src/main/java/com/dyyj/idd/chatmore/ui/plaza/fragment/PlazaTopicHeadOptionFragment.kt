package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadOptionBinding
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
 * desc   : 话题页 选项测试
 */
class PlazaTopicHeadOptionFragment : BaseFragment<LayoutPlazaTopicHeadOptionBinding, ViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null

    var count = 0
    var index = 0

    var answers: HashMap<Int, String> = hashMapOf()

    companion object {
        const val KEY_OBJ = "OBJ"

        fun create(obj: PlazaTopicListResult.Topic): PlazaTopicHeadOptionFragment {
            val plazaTopicFragment = PlazaTopicHeadOptionFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_OBJ, obj)
            plazaTopicFragment.arguments = bundle
            return plazaTopicFragment
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.layout_plaza_topic_head_option
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

        head.option1?.root?.setOnClickListener {
            next(topicObj.question[index].option[0])
        }
        head.option2?.root?.setOnClickListener {
            next(topicObj.question[index].option[1])
        }
        head.option3?.root?.setOnClickListener {
            next(topicObj.question[index].option[2])
        }
        head.option4?.root?.setOnClickListener {
            next(topicObj.question[index].option[3])
        }

        head.btn.setOnClickListener {
            --index
            refresh(topicObj)
        }

        count = topicObj.question.size

        refresh(topicObj)
    }

    fun next(option: PlazaTopicListResult.Option) {
        if (index >= count) {
            //测试结束
            doTest()
            return
        }
        answers.put(index, option.Id)

        index++
        refresh(topic)
    }

    fun refresh(topicObj: PlazaTopicListResult.Topic?) {
        if (topicObj == null) {
            return
        }

        if (index > 0) {
            mBinding.btn.visibility = View.VISIBLE
        } else {
            mBinding.btn.visibility = View.INVISIBLE
        }

        if (index >= count) {
            //测试结束
            doTest()
            return
        }

        var question = topicObj.question[index]
        val answer = answers[index]

        mBinding.tvTitle.text = "(${index + 1}/${count}) ${question.rules_title}"


        val optionViews = arrayListOf(mBinding.option1, mBinding.option2, mBinding.option3, mBinding.option4)

        val size = question.option.size
        for (i in 0..3) {
            val binding = optionViews[i]
            if (i >= size) {
                binding?.root?.visibility = View.GONE
                continue
            }
            binding?.root?.visibility = View.VISIBLE

            val option = question.option[i]
            if (null != answer && answer.equals(option.Id)) {
                binding?.ivSelect?.visibility = View.VISIBLE
            } else {
                binding?.ivSelect?.visibility = View.INVISIBLE
            }

            Glide.with(context).load(option.rulesImage).asBitmap()
                    .centerCrop()
                    .into(binding?.bg)

        }

    }


    fun doTest() {
        if (topic == null) return

        val jsonArray = JsonArray()

        var index = 0
        topic?.question?.forEach {
            val jsonObject = JsonObject()
            jsonObject.addProperty("rulesId", it.rules_id)
            jsonObject.addProperty("optionVal", answers[index] ?: "")

            jsonArray.add(jsonObject)

            index++
        }

        val toString = jsonArray.toString()
        EventBus.getDefault().post(PlazaTopicFragment.OnSelectVM(toString, false))
    }

}