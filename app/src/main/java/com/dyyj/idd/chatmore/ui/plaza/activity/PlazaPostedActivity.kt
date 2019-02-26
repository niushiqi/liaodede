package com.dyyj.idd.chatmore.ui.plaza.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityPlazaPostedBinding
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaPostFragment
import com.dyyj.idd.chatmore.viewmodel.EmptyAcitivityViewModel
import com.dyyj.idd.chatmore.weiget.pop.PublishPop

/**
 * 广场发帖
 * */
class PlazaPostedActivity : BaseActivity<ActivityPlazaPostedBinding, EmptyAcitivityViewModel>() {

    companion object {
        const val KEY_ID = "ID"
        const val KEY_TITLE = "KEY_TITLE"

        fun start(context: Context, id: String? = null, topicTile: String? = null) {
            val intent = Intent(context, PlazaPostedActivity::class.java)
            id?.let {
                intent.putExtra(KEY_ID, id)
                intent.putExtra(KEY_TITLE, topicTile)
            }
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_plaza_posted
    }

    override fun onViewModel(): EmptyAcitivityViewModel {
        return EmptyAcitivityViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return null
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

    override fun onBackPressed() {
        val pop = PublishPop(this)
        pop.show(mBinding.root, object : PublishPop.IPublishListener {
            override fun onPublish(exit: Boolean) {
                if (exit) {
                    super@PlazaPostedActivity.onBackPressed()
                }
                pop.dismiss()
            }

        })
    }

    fun toFragment() {
        val topicID = intent.getStringExtra(KEY_ID)
        val title = intent.getStringExtra(KEY_TITLE)
        try {
            val beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.replace(R.id.fragment_Container, PlazaPostFragment.create(topicID, title))
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}