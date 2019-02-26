package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivityInviteNew2Binding
import com.dyyj.idd.chatmore.viewmodel.InviteNewViewModel2
import com.dyyj.idd.chatmore.weiget.pop.TishiPop
import com.othershe.nicedialog.NiceDialog

class InviteNewActivity2: BaseActivityV2<ActivityInviteNew2Binding, InviteNewViewModel2>() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, InviteNewActivity2::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_invite_new_2
    }

    override fun onViewModel(): InviteNewViewModel2 {
        return InviteNewViewModel2()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        mBinding.vpBottom.adapter = mViewModel.getAdapter(supportFragmentManager)
        mBinding.vpBottom.offscreenPageLimit = 2
        mBinding.vpBottom.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    mBinding.txtLeft.setBackgroundResource(R.drawable.iv_invite_left_selected)
                    mBinding.txtRight.setBackgroundResource(R.drawable.iv_invite_right_select)
                    mBinding.txtLeft.setTextColor(Color.parseColor("#FFFFFF"))
                    mBinding.txtRight.setTextColor(Color.parseColor("#ED445C"))
                } else {
                    mBinding.txtRight.setBackgroundResource(R.drawable.iv_invite_right_selected)
                    mBinding.txtLeft.setBackgroundResource(R.drawable.iv_invite_left_select)
                    mBinding.txtLeft.setTextColor(Color.parseColor("#ED445C"))
                    mBinding.txtRight.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }

        })
        mBinding.txtLeft.setOnClickListener { mBinding.vpBottom.setCurrentItem(0, true) }
        mBinding.txtRight.setOnClickListener { mBinding.vpBottom.setCurrentItem(1, true) }
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "邀请好友下载APP赚红包"

        mBinding.textView33.setOnClickListener {
            val tishi = TishiPop(this)
            tishi.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
        }

        mBinding.tvMakeMoneyTricks.setOnClickListener {
            showDialogMakeMoneyTricks() //赚钱小技巧弹窗
        }
    }

    private fun showDialogMakeMoneyTricks() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_make_money_tricks_layout)     //设置dialog布局文件
                .setConvertListener { holder, dialog ->
                    //进行相关View操作的回调
                    holder.setOnClickListener(R.id.ic_close) {
                        dialog.dismiss()
                    }
                }
                .setDimAmount(0.8f)     //调节灰色背景透明度[0-1]，默认0.5f
                .setWidth(280)     //dialog宽度（单位：dp），默认为屏幕宽度
                //.setHeight(300)     //dialog高度（单位：dp），默认为WRAP_CONTENT
                //                .setAnimStyle(R.style.EnterExitAnimation)//设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
                .show(supportFragmentManager)     //显示dialog
    }


}