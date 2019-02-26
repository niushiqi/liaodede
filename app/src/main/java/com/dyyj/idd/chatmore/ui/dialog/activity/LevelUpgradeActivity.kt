package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogLevelUpgradeBinding
import com.dyyj.idd.chatmore.utils.RxTimerUtil
import com.dyyj.idd.chatmore.viewmodel.LevelUpgradeViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/16
 * desc   :  等级提升
 */
class LevelUpgradeActivity : BaseActivityV2<DialogLevelUpgradeBinding, LevelUpgradeViewModel>() {
    companion object {
        const val LEVEL = "level"
        const val DESC = "desc"
        const val VALUE = "value"
        const val TITLE = "title"
        fun start(context: Context, level: String, title: String, desc: String, value: String) {
            val intent = Intent(context, LevelUpgradeActivity::class.java)
            intent.putExtra(LEVEL, level)
            intent.putExtra(DESC, desc)
            intent.putExtra(VALUE, value)
            intent.putExtra(TITLE, title)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_level_upgrade
    }

    override fun onViewModel(): LevelUpgradeViewModel {
        return LevelUpgradeViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {

//    setTextFont(mBinding.levelTv1)
//    setTextFont(mBinding.levelTv2)
        mBinding.itemCl.setOnClickListener { onBackPressed() }
        mBinding.levelTv2.text = getLevel()
        mBinding.descContentTv.text = getDesc()
        mBinding.descValueTv.text = getValue()
        mBinding.descTitleTv.text = getTitleDes()

        if (TextUtils.isEmpty(getDesc() + getValue() + getTitleDes())) {
            mBinding.clTag.visibility = View.GONE
        }

        RxTimerUtil.timer(5000, { finish() })
    }

    private fun getTitleDes() = intent.getStringExtra(TITLE)

    private fun getLevel() = intent.getStringExtra(LEVEL)

    private fun getDesc() = intent.getStringExtra(DESC)

    private fun getValue() = intent.getStringExtra(VALUE)

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTimerUtil.cancel()
    }
}