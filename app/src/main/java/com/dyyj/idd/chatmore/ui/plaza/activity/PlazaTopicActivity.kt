package com.dyyj.idd.chatmore.ui.plaza.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPlazaTopicBinding
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaTopicFragment
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel

/**
 * 广场话题
 */
class PlazaTopicActivity : BaseActivity<ActivityPlazaTopicBinding, EmptyAcitivityViewModel>() {

    companion object {
        const val KEY_ID = "ID"

        fun start(context: Context, id: String?) {
            val intent = Intent(context, PlazaTopicActivity::class.java)
            intent.putExtra(KEY_ID, id)
            context.startActivity(intent)
        }

    }

    override fun onLayoutId(): Int {
        return R.layout.activity_plaza_topic
    }

    override fun onViewModel(): EmptyAcitivityViewModel {
        return EmptyAcitivityViewModel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        onCreateEvenbus(this)
        toFragment()
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onDestroy() {
        super.onDestroy()
//        onDestryEvenbus(this)
    }

    fun toFragment() {
        try {
            val topicID = intent.getStringExtra(KEY_ID)
            val create = PlazaTopicFragment.create(topicID)

            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(R.id.fragment_Container, create)
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}