package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivityNewbieTaskBinding
import com.dyyj.idd.chatmore.viewmodel.NewbieTaskViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/16
 * desc   : 新手任务
 */
class NewbieTaskActivity : BaseActivityV2<ActivityNewbieTaskBinding, NewbieTaskViewModel>() {
    companion object {
        const val TYPE_GOLD = "gold"
        const val TYPE_STONE = "stone"
        const val TYPE_MONEY = "money"
        const val TYPE_TITLE = "title"
        const val TYPE_REWARDID = "rewardId"
        const val TYPE_AUTOCLOSE = "autoclose"
        fun start(context: Context, title: String, gold: Double?, stone: Double?, money: Double?, rewardId: Int, autoClose: Boolean = false) {
            val intent = Intent(context, NewbieTaskActivity::class.java)
            intent.putExtra(TYPE_GOLD, gold)
            intent.putExtra(TYPE_STONE, stone)
            intent.putExtra(TYPE_MONEY, money)
            intent.putExtra(TYPE_REWARDID, rewardId)
            intent.putExtra(TYPE_TITLE, title)
            intent.putExtra(TYPE_AUTOCLOSE, autoClose)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_newbie_task
    }

    override fun onViewModel(): NewbieTaskViewModel {
        return NewbieTaskViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEvenbus(this)
        initView()
    }

    /**
     * 金币
     */
    private fun getGold() = intent.getDoubleExtra(TYPE_GOLD, 0.0)

    /**
     * 魔石
     */
    private fun getStone() = intent.getDoubleExtra(TYPE_STONE, 0.0)

    /**
     * 现金
     */
    private fun getMoney() = intent.getDoubleExtra(TYPE_MONEY, 0.0)

    /**
     * 标题
     */
    private fun getDailogTitle() = intent.getStringExtra(TYPE_TITLE)

    /**
     * 任务id
     */
    private fun getRewardId() = intent.getIntExtra(TYPE_REWARDID, 0)

    /**
     * 初始化view
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBinding.closeIv.setOnClickListener { finish() }
        mBinding.okBtn.setOnClickListener { mViewModel.netGetReward(getRewardId().toString()) }
        mBinding.itemCl.setOnClickListener { finish() }

        mBinding.title = getDailogTitle()
        mBinding.gold = getGold()
        mBinding.stone = getStone()
        mBinding.money = getMoney()

        mBinding.goldTv.text = "+${getGold()}"
        mBinding.stoneTv.text = "+${getStone()}"
        mBinding.moneyTv.text = "+${getMoney()}"

        setTextFont(mBinding.goldTv)
        setTextFont(mBinding.stoneTv)
        setTextFont(mBinding.moneyTv)

        setTextFont(mBinding.goldTv2)
        setTextFont(mBinding.stoneTv2)
        setTextFont(mBinding.moneyTv2)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun finish() {
        onDestryEvenbus(this)
        super.finish()
        ChatApp.getInstance().runNestTaskQueue()
        EventBus.getDefault()
    }

    @Subscribe
    fun onRewardVM(obj: NewbieTaskViewModel.RewardVM) {
        onBackPressed()
    }
}