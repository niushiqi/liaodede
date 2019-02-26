package com.dyyj.idd.chatmore.ui.plaza.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPlazaTopicsBinding
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaTopicsFragment
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel

/**
 * 广场话题列表
 * */
class PlazaTopicsActivity : BaseActivity<ActivityPlazaTopicsBinding, EmptyAcitivityViewModel>() {

    companion object {
        fun start(context: Context, num: Int) {
            val intent = Intent(context, PlazaTopicsActivity::class.java)
            intent.putExtra("focusNum", num)
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

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onDestroy() {
        super.onDestroy()
//        onDestryEvenbus(this)
    }

    private fun initToobar() {
        var focusNum = intent.getIntExtra("focusNum", 0)
        val view = mToolbar?.findViewById<TextView>(R.id.tv_num)
        if (focusNum > 0) {
            view?.text = if (focusNum > 99) "${focusNum}+" else "${focusNum}"
            view?.visibility = View.VISIBLE
        } else {
            view?.visibility = View.GONE
        }

        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.let {
            it.text = "热门话题"
        }
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
            onBackPressed()
        }

        mToolbar?.findViewById<View>(R.id.tv_right)?.setOnClickListener {
            PlazaTopicsFocusActivity.start(this@PlazaTopicsActivity)
        }

    }

    fun toFragment() {

        try {
            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(R.id.fragment_Container, PlazaTopicsFragment())
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}