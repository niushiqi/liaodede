package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityInviteGameBinding
import com.dyyj.idd.chatmore.ui.adapter.InviteGameAdapter
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.InviteGameViewModel
import com.dyyj.idd.chatmore.weiget.pop.SocialSharePop
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

class InviteGameActivity : BaseActivity<ActivityInviteGameBinding, InviteGameViewModel>() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, InviteGameActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_invite_game
    }

    override fun onViewModel(): InviteGameViewModel {
        return InviteGameViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arrays = arrayListOf(1, 2, 3, 4);
        initRecycleView(arrays)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "小游戏"
//        mBinding.likewan.setOnClickListener {
//            showProgressDialog()
//            mViewModel.netShareMessage()
//        }
    }

    fun initRecycleView(list: List<Any>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(this@InviteGameActivity).color(
                ContextCompat.getColor(this@InviteGameActivity, R.color.white_transparent)).sizeResId(
                R.dimen.item_decoration_2px).build()
        mViewModel.getAdapter().initData(list)
        mBinding.rvGame.addItemDecoration(decoration)
        mBinding.rvGame.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvGame.layoutManager = LinearLayoutManager(this@InviteGameActivity)
        mBinding.rvGame.adapter = mViewModel.getAdapter()

    }

    @Subscribe
    fun onShareGameVM(obj: InviteGameAdapter.ShareGameVM) {
        showProgressDialog()
        mViewModel.netShareMessage()
    }

    @Subscribe
    fun onShareMessageVM(obj: InviteGameViewModel.ShareMessageVM) {
        closeProgressDialog()
        if (obj.success) {
            var mPop = SocialSharePop(this@InviteGameActivity)
            mPop.initMinListener(this@InviteGameActivity, obj.inviteCode, obj.title, resources)
            mPop.show(mBinding.root)
            //ShareUtils.shareSmall(this@InviteGameActivity, obj.inviteCode, obj.title, "聊得得小游戏", "/pages/index/index", "gh_4ab55bfec127", BitmapFactory.decodeResource(resources, R.drawable.ic_game_icon))
        }
    }
}