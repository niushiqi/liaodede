package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityTagsBinding
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.utils.ToolbarUtils
import com.dyyj.idd.chatmore.viewmodel.TagsViewModel
import com.google.android.flexbox.FlexboxLayout
import com.gt.common.gtchat.extension.niceChatContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TagsActivity : BaseActivity<ActivityTagsBinding, TagsViewModel>() {

    companion object {
        const val FROM_REGISTER = "from_reigster"
        const val FROM_USERINFO = "from_userinfo"

        fun start(context: Context, from: String, mSignsList: ArrayList<String>? = null) {
            val intent = Intent(context, TagsActivity::class.java)
            intent.putExtra("from", from)
            if (mSignsList != null) {
                intent.putStringArrayListExtra("tags", mSignsList)
            }
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_tags
    }

    override fun onViewModel(): TagsViewModel {
        return TagsViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
    }

    private fun initView() {
        title = "请选择标签"
        ToolbarUtils.init(this)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = title
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }

        mBinding.commitBtn.text = if (intent.getStringExtra("from") == FROM_REGISTER) "进入聊得得" else "提交"
        mBinding.commitBtn.setOnClickListener {
            var nums = 0
            mViewModel.allTags.forEach {
                if (it.check) nums++
            }
            if (nums < 3) {
                Toast.makeText(niceChatContext(), "至少选择三个标签", Toast.LENGTH_SHORT).show()
            } else {
                val sb = StringBuilder()
                mViewModel.allTags.forEach {
                    if (it.check) {
                        sb.append(it.tagId).append(",")
                    }
                }
                val tags = sb.substring(0, sb.length - 1)
                showProgressDialog()
                mViewModel.netSaveTag(mDataRepository.getUserid()!!, tags)
            }
        }

//        insertTags()
        mViewModel.netTags(mDataRepository.getUserid()!!, mDataRepository.getUserInfoEntity()?.gender.toString())
    }

//    private fun insertTags() {
//        val tags = arrayOf("大叔范", "高冷", "小哥哥", "我是帅哥", "身高一米八", "八块腹肌",
//                "逗你开心", "宠你没商量", "陪你逛吃", "会做饭",
//                "上知天文下知地理", "五行缺女友", "看书就困",
//                "宅", "户外", "吃鸡", "开黑", "夜猫子", "666", "唱歌给你听",
//                "黑泡", "民谣", "篮球", "freestyle", "很燃")
//        tags.forEach {
//            tagList.add(TagResult(it, false))
//        }
//        insertTagView()
//    }

    private fun insertTagView() {
        mBinding.aflCotent.removeAllViews()
        mViewModel.allTags.forEach {
            val itemView = LayoutInflater.from(niceChatContext()).inflate(R.layout.item_tags2, null)
            val textview = itemView.findViewById<TextView>(R.id.txt_sign)
            textview.setSingleLine(true)
            textview.text = it.tagName
            if (it.check) {
                textview.setBackgroundResource(R.drawable.rect_round_solid_yellow)
            } else {
                textview.setBackgroundResource(R.drawable.rect_round_stroke_gray)
            }
            val params = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
            params.order = -1
            params.flexGrow = 2F
            mBinding.aflCotent.addView(itemView, params)

            itemView.setOnClickListener { view ->
                it.check = !it.check
                insertTagView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun OnSubscribe(obj: TagsViewModel.AllTagsVM) {
        if (intent.getStringExtra("from") == FROM_USERINFO) {
            val selectList = intent.getStringArrayListExtra("tags")
            if ((selectList != null) and (selectList.size != 0)) {
                selectList.forEach { outIt ->
                    mViewModel.allTags.forEach { inIt ->
                        if (outIt == inIt.tagName) {
                            inIt.check = true
                        }
                    }
                }
            }
        }
        insertTagView()
    }

    @Subscribe
    fun OnSubscribe(obj: TagsViewModel.SaveTagsVM) {
        closeProgressDialog()
        if (intent.getStringExtra("from") == FROM_REGISTER) {
            MainActivity.startOpenCall(this)
            mDataRepository.pubSocket()
            finish()
        } else {
            Toast.makeText(niceChatContext(), if (obj.success) "保存成功" else "保存失败", Toast.LENGTH_SHORT).show()
            if (obj.success) {
                finish()
            }
        }
    }

}