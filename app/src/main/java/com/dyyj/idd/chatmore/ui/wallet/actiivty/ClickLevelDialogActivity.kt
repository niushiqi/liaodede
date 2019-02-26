package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.DialogClickLevelBinding
import com.dyyj.idd.chatmore.ui.event.TaskBoxEvent
import com.dyyj.idd.chatmore.ui.event.TaskChatEvent
import com.dyyj.idd.chatmore.ui.event.TaskStartChatEvent
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.TaskClickType
import com.dyyj.idd.chatmore.viewmodel.DialogTixianViewModel
import com.dyyj.idd.chatmore.weiget.pop.ContactPop
import com.gt.common.gtchat.extension.niceChatContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ClickLevelDialogActivity : BaseActivity<DialogClickLevelBinding, DialogTixianViewModel>() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, ClickLevelDialogActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_click_level
    }

    override fun onViewModel(): DialogTixianViewModel {
        return DialogTixianViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        mViewModel.getTaskData()
    }


    @SuppressLint("WrongViewCast")
    private fun initView() {
        mBinding.itemCl.setOnClickListener { finish() }
        mBinding.okBtn.setOnClickListener {
            EventBus.getDefault().post(ContactPop.FinishViewVM())
            goTask(clickType)
            finish()
        }

    }

    private fun goTask(clickTarget: String?) {
        when (clickTarget) {
            TaskClickType.NONE -> {
//                Toast.makeText(activity, TaskClickType.NONE + " 无", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.MATCHING -> {
                EventBus.getDefault().post(TaskStartChatEvent())
//                Toast.makeText(activity, TaskClickType.MATCHING + " 匹配", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.BOX -> {
                EventBus.getDefault().post(TaskBoxEvent())
//                Toast.makeText(activity, TaskClickType.BOX + " 定时红包", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.BANK -> {
                val netUrl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                H5Activity.start(this, netUrl, "社交银行", true)
//                Toast.makeText(activity, TaskClickType.BANK + " 银行", Toast.LENGTH_SHORT).show()

            }
            TaskClickType.MAIL -> {
                EventBus.getDefault().post(TaskChatEvent())
//                Toast.makeText(activity, TaskClickType.MAIL + " 一对一", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.WALLET -> {
                // 进入我的钱包
                WalletActivity.start(this!!)
            }
            TaskClickType.INVITE_PINK -> {
                InviteNewActivity2.start(this!!)
//                Toast.makeText(activity, TaskClickType.INVITE_PINK + " 邀请粉色", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.SCENE -> {

            }
            TaskClickType.INVITE_GAME -> {
                // 小游戏
                InviteGameActivity.start(this!!)
            }
        }
    }

    @Subscribe
    fun TaskLevelData(obj: DialogTixianViewModel.TaskLevelData) {
        if (obj.success) {
            mBinding.descTv.text = obj.obj!![0].taskCondition!!.taskName

            clickType = obj.obj[0].clickTarget
        }
    }

    private var clickType: String? = ""
}