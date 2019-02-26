package com.dyyj.idd.chatmore.ui.plaza.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPlazaTopicsBinding
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaTopicsFocusFragment
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel

/**
 * 广场关注话题列表
 * */
class PlazaTopicsFocusActivity : BaseActivity<ActivityPlazaTopicsBinding, EmptyAcitivityViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PlazaTopicsFocusActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_plaza_topics
    }

    override fun onViewModel(): EmptyAcitivityViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        onCreateEvenbus(this)
        initToobar()
        toFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
//        onDestryEvenbus(this)
    }

    private fun initToobar() {
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.let {
            it.text = "我关注的"
        }
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
            onBackPressed()
        }

        mToolbar?.findViewById<View>(R.id.tv_right)?.visibility = View.GONE
        mToolbar?.findViewById<View>(R.id.tv_num)?.visibility = View.GONE
    }

    fun toFragment() {

        try {
            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(R.id.fragment_Container, PlazaTopicsFocusFragment())
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}