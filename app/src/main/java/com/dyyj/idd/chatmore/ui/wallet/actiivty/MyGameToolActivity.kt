package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMyGameToolsBinding
import com.dyyj.idd.chatmore.ui.wallet.fragment.GameToolsItemFragment
import com.dyyj.idd.chatmore.viewmodel.MyGameToolViewModel
import com.flyco.tablayout.listener.CustomTabEntity
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * time   : 2019/01/22
 * desc   : 我的碎片
 */

class MyGameToolActivity : BaseActivity<ActivityMyGameToolsBinding, MyGameToolViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MyGameToolActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_my_game_tools
    }

    override fun onViewModel(): MyGameToolViewModel {
        return MyGameToolViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
        mViewModel.netIndex()
    }

    private fun initView() {
    }

    fun initRecycleView(list: List<Any>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(this@MyGameToolActivity).color(
                ContextCompat.getColor(this@MyGameToolActivity, R.color.divider_home)).sizeResId(
                R.dimen.item_home_decoration_15).build()
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的碎片"
    }


    @Subscribe
    fun onGameToolsVM(obj: MyGameToolViewModel.GameToolsVM) {
        if (obj.isSuccess) {
            obj.obj?.let {
                val list = it.props?.map { mViewModel.getItem(it) }?.toList()
                val items = it.props?.filter { it.props != null }?.map {
                    GameToolsItemFragment().setData(it.props!!)
                }?.toList()
                val titles = ArrayList<CustomTabEntity>()
                val fragments = arrayListOf<Fragment>()
                titles.addAll(list!!)
                fragments.addAll(items!!)
                mBinding.tl4.setTabData(titles,this,  R.id.fl_change, fragments)
            }
        }
    }

}