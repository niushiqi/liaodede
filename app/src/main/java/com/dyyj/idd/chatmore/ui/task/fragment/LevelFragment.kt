package com.dyyj.idd.chatmore.ui.task.fragment

import android.support.v7.widget.LinearLayoutManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentLevelBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.LevelTaskResult
import com.dyyj.idd.chatmore.ui.adapter.LevelTaskAdapter
import com.dyyj.idd.chatmore.ui.event.TaskBoxEvent
import com.dyyj.idd.chatmore.ui.event.TaskChatEvent
import com.dyyj.idd.chatmore.ui.event.TaskStartChatEvent
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteGameActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WalletActivity
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.utils.TaskClickType
import com.dyyj.idd.chatmore.viewmodel.LevelViewModel
import com.gt.common.gtchat.extension.niceChatContext
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 等级任务
 */
class LevelFragment : BaseFragment<FragmentLevelBinding, LevelViewModel>() {

    private val mTaskData = arrayListOf<LevelTaskResult.Data.LevelTaskData>()

    override fun onLayoutId(): Int {
        return R.layout.fragment_level
    }

    override fun onViewModel(): LevelViewModel {
        return LevelViewModel()
    }


    override fun lazyLoad() {
        super.lazyLoad()
        initView()
        mViewModel.getTaskData()
        initListener()
    }

    private fun initView() {
        mBinding.recyclerview.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerview.adapter = LevelTaskAdapter(mTaskData)
    }

    private fun initListener() {
        (mBinding.recyclerview.adapter as LevelTaskAdapter).setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tv_task_status) {
                when (mTaskData[position].taskCondition!!.taskStatus) {
                    0 -> {
                        //待完成 去完成
                        goTask(mTaskData[position].clickTarget)
                        EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_level_goto", mTaskData[position].level.toString()))
                    }
                }
            }
        }
    }

    private fun goTask(clickTarget: String?) {
        when (clickTarget) {
            TaskClickType.NONE -> {
//                Toast.makeText(activity, TaskClickType.NONE + " 无", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.MATCHING -> {
                EventBus.getDefault().post(TaskStartChatEvent())
                activity!!.finish()
//                Toast.makeText(activity, TaskClickType.MATCHING + " 匹配", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.BOX -> {
                EventBus.getDefault().post(TaskBoxEvent())
                activity!!.finish()
//                Toast.makeText(activity, TaskClickType.BOX + " 定时红包", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.BANK -> {
                val netUrl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                H5Activity.start(mActivity, netUrl, "社交银行", true)
//                Toast.makeText(activity, TaskClickType.BANK + " 银行", Toast.LENGTH_SHORT).show()

            }
            TaskClickType.MAIL -> {
                EventBus.getDefault().post(TaskChatEvent())
                activity!!.finish()
//                Toast.makeText(activity, TaskClickType.MAIL + " 一对一", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.WALLET -> {
                // 进入我的钱包
                WalletActivity.start(this!!.activity!!)
            }
            TaskClickType.INVITE_PINK -> {
                InviteNewActivity2.start(activity!!)
//                Toast.makeText(activity, TaskClickType.INVITE_PINK + " 邀请粉色", Toast.LENGTH_SHORT).show()
            }
            TaskClickType.SCENE -> {
                activity!!.finish()
            }
            TaskClickType.INVITE_GAME -> {
                // 小游戏
                InviteGameActivity.start(this!!.activity!!)
            }
        }
    }

    fun setTaskData(objects: List<LevelTaskResult.Data.LevelTaskData>) {
        mTaskData.clear()
        mTaskData.addAll(objects)
        mBinding.recyclerview.adapter.notifyDataSetChanged()
    }

}