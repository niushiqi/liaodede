package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivitySignTaskBinding
import com.dyyj.idd.chatmore.viewmodel.SignTaskViewModel

class SignTaskActivity : BaseActivityV2<ActivitySignTaskBinding, SignTaskViewModel>() {
    companion object {
        const val TYPE_GOLD = "gold"
        const val TYPE_STONE = "stone"
        const val TYPE_MONEY = "money"
        const val TYPE_REWARDID = "rewardId"
        fun start(context: Context, gold:Double?, stone:Double?, money:Double?, rewardId:Int) {
            val intent = Intent(context, SignTaskActivity::class.java)
            intent.putExtra(TYPE_GOLD, gold)
            intent.putExtra(TYPE_STONE, stone)
            intent.putExtra(TYPE_MONEY, money)
            intent.putExtra(TYPE_REWARDID, rewardId)
            context.startActivity(intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_sign_task
    }

    override fun onViewModel(): SignTaskViewModel {
        return SignTaskViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    /**
     * 金币
     */
    private fun getGold() = intent.getDoubleExtra(TYPE_GOLD,0.0)

    /**
     * 魔石
     */
    private fun getStone() = intent.getDoubleExtra(TYPE_STONE,0.0)

    /**
     * 现金
     */
    private fun getMoney() = intent.getDoubleExtra(TYPE_MONEY,0.0)

    /**
     * 标题
     */
    private fun getDailogTitle() = "自动签到成功"

    /**
     * 任务id
     */
    private fun getRewardId() = intent.getIntExtra(TYPE_REWARDID,0)

    /**
     * 初始化view
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBinding.closeIv.setOnClickListener { finish() }
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

        val sb = StringBuffer()
        if (getGold() != 0.0) {
            sb.append(getGold()).append("金币").append("+")
        }
        if (getStone() != 0.0) {
            sb.append(getStone()).append("魔石").append("+")
        }
        if (getMoney() != 0.0) {
            sb.append(getMoney()).append("现金").append("+")
        }
        mBinding.textView3.text = "获得" + sb.substring(0, sb.length - 1)
    }

    override fun onBackPressed() {
        finish()
    }

}