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
import com.dyyj.idd.chatmore.databinding.LayoutPlazaTopicHeadInputBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.gt.common.gtchat.extension.niceCustomTaost
import org.greenrobot.eventbus.EventBus
import java.net.URLEncoder

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题页 输入型
 */
class PlazaTopicHeadInputFragment : BaseFragment<LayoutPlazaTopicHeadInputBinding, ViewModel>() {
    var topic: PlazaTopicListResult.Topic? = null

    companion object {
        const val KEY_OBJ = "OBJ"

        fun create(obj: PlazaTopicListResult.Topic): PlazaTopicHeadInputFragment {
            val plazaTopicFragment = PlazaTopicHeadInputFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_OBJ, obj)
            plazaTopicFragment.arguments = bundle
            return plazaTopicFragment
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.layout_plaza_topic_head_input
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

        if (!TextUtils.isEmpty(topicObj.squareTopicImage)) {
            Glide.with(context).load(topicObj.squareTopicImage).asBitmap()
                    .centerCrop()
                    .into(head.bg)
        }

        if (topicObj.question.isEmpty()) {
            return
        }

        head.tvTitle.text = topicObj.squareTopicContent
        val arrayListOf = arrayListOf(head.et1, head.et2)

        var index = 0
        topicObj.question.forEach {
            if (index > 1) return

            var obj = it;

            val binding = arrayListOf[index]

            binding?.visibility = View.VISIBLE
            binding?.hint = obj.rules_title

            index++
        }

        head.btn.setOnClickListener {
            doTest()
        }
    }

    fun doTest() {
        if (topic == null) return

        val jsonArray = JsonArray()
        val arrayListOf = arrayListOf(mBinding.et1, mBinding.et2)

        var index = 0
        var ext: String? = null
        topic?.question?.forEach {
            if (index > 1) return

            val binding = arrayListOf[index]

            val toString = binding.text.toString()
            if (ext == null) {
                ext = toString
            }

            if (toString.isEmpty()) {
                context?.niceCustomTaost("请填写所有输入选项~！")
                return
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("rulesId", it.rules_id)
            jsonObject.addProperty("optionVal", URLEncoder.encode(toString, "UTF-8"))

            jsonArray.add(jsonObject)

            index++
        }

        val toString = jsonArray.toString()
        EventBus.getDefault().post(PlazaTopicFragment.OnSelectVM(toString, true, ext))
    }


}