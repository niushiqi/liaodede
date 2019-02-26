package com.dyyj.idd.chatmore.ui.user.activity

/**
 * Created by wangbin on 2018/12/16.
 */
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityLevelUpgradeTutorialBinding
import com.dyyj.idd.chatmore.viewmodel.LevelUpgradeTutorialViewModel

/**
 * Created by wangbin on 2018/12/12.
 */
class LevelUpgradeTutorialActivity : BaseActivity<ActivityLevelUpgradeTutorialBinding, LevelUpgradeTutorialViewModel>() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LevelUpgradeTutorialActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_level_upgrade_tutorial
    }

    override fun onViewModel(): LevelUpgradeTutorialViewModel {
        return LevelUpgradeTutorialViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "聊得得怎么升等级"
    }
}