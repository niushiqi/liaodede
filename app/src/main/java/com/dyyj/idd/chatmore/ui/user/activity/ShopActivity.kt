package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityShopBinding
import com.dyyj.idd.chatmore.viewmodel.ShopViewModel

/**
 * 道具商城
 */
class ShopActivity: BaseActivity<ActivityShopBinding, ShopViewModel>() {

    companion object {

        const val WITCH_PAGE = "witch_page"

        fun start(context: Context, witchPage: Int) {
            val intent = Intent(context, ShopActivity::class.java)
            intent.putExtra(ShopActivity.WITCH_PAGE, witchPage)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_shop
    }

    override fun onViewModel(): ShopViewModel {
        return ShopViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "商店"
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.text = "支付记录"
        initListener()
        mBinding.vpInShop.adapter = mViewModel.getAdapter(supportFragmentManager)
        mBinding.tlInShop.setViewPager(mBinding.vpInShop)
        mBinding.vpInShop.offscreenPageLimit = 2
        mBinding.vpInShop.currentItem = intent.getIntExtra(WITCH_PAGE,0)
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        findViewById<View>(R.id.right_iv)?.setOnClickListener { PayOrderHistoryActivity.start(this) }

    }


}