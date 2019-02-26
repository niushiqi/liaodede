package com.dyyj.idd.chatmore.ui.task.fragment

import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentEverydayBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.EverydayTaskResult
import com.dyyj.idd.chatmore.ui.adapter.EverydayTaskAdapter
import com.dyyj.idd.chatmore.ui.event.*
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.main.fragment.SystemMessageFragment
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaPostedActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteGameActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WalletActivity
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.utils.TaskClickType
import com.dyyj.idd.chatmore.viewmodel.EverydayViewModel
import com.gt.common.gtchat.extension.niceChatContext
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

class EverydayFragment : BaseFragment<FragmentEverydayBinding, EverydayViewModel>() {

    private val mTaskData = arrayListOf<EverydayTaskResult.Data.EverydayTaskData>()


    override fun onLayoutId(): Int {
        return R.layout.fragment_everyday
    }

    override fun onViewModel(): EverydayViewModel {
        return EverydayViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initView()
        mViewModel.getTaskData()
        initListener()
    }

    private fun initView() {

        mBinding.recyclerview.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerview.adapter = EverydayTaskAdapter(mTaskData)

//        //设置线
//        val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
//                ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
//                R.dimen.item_decoration_2px).build()
//        mBinding.recyclerview.addItemDecoration(decoration)

    }

    private fun initListener() {
        (mBinding.recyclerview.adapter as EverydayTaskAdapter).setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tv_task_status) {
                when (mTaskData[position].taskStatus) {
                    0 -> {
                        //待完成 去完成
                        goTask(mTaskData[position].clickTarget)
                        EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_goto", mTaskData[position].taskId))
                    }
                    1 -> {
                        // 可领取，领取
                        mViewModel.getTaskReward(mTaskData[position])
                        EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_get", mTaskData[position].taskId))
                    }
                    2 -> {
                        //已完成已领取， 已入账
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
                Log.i("zhangwj","预计崩溃点 空指针: mDataRepository.getLoginToken() == null"+{mDataRepository.getLoginToken()})
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
            TaskClickType.SQUARE -> {
                // 广场
                PlazaPostedActivity.start(this!!.activity!!)
            }
            TaskClickType.SQUARE_MAIN -> {
                // 广场主页
                EventBus.getDefault().post(TaskChat2Event())
                Flowable.interval(1, TimeUnit.SECONDS).take(1).subscribe({
                    EventBus.getDefault().post(TaskStartSquareEvent())
                })
                activity!!.finish()
                //(mActivity as MainActivity).addFragment2(SystemMessageFragment.instance())
            }
            TaskClickType.MATCHING_TEXT -> {
                // 随缘匹配
                Flowable.interval(1, TimeUnit.SECONDS).take(1).subscribe({
                    EventBus.getDefault().post(TaskStartTextChatEvent())
                })
                activity!!.finish()
            }
        }
    }

    fun setTaskData(objects: List<EverydayTaskResult.Data.EverydayTaskData>) {
        mTaskData.clear()
        mTaskData.addAll(objects)
        mBinding.recyclerview.adapter.notifyDataSetChanged()
    }

}