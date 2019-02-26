package com.dyyj.idd.chatmore.ui.task.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActiivtyTaskSystemBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.dialog.activity.ToastConnectAwardActivity
import com.dyyj.idd.chatmore.ui.task.fragment.AchievementFragment
import com.dyyj.idd.chatmore.ui.task.fragment.EverydayFragment
import com.dyyj.idd.chatmore.ui.task.fragment.LevelFragment
import com.dyyj.idd.chatmore.ui.task.fragment.ThreeDayFragment
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/28
 * desc   : 任务系统
 */
class TaskSystemActivity : BaseActivity<ActiivtyTaskSystemBinding, TaskSystemViewModel>() {


    /**
     * 标题
     */
    private var mTitles = arrayOf("日常任务", "等级任务", "成就任务")

    /**
     * Fragments
     */
    private var mFragments: Array<Fragment> = arrayOf(EverydayFragment(), LevelFragment(),
            AchievementFragment())

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TaskSystemActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.actiivty_task_system
    }

    override fun onViewModel(): TaskSystemViewModel {
        return TaskSystemViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        initListener()
        mViewModel.netTask()
        mViewModel.getThreeDayTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun initView() {

        if (mViewModel.signIn == null) {
            mBinding.txtDes.text = ""
        } else {
            mBinding.txtDes.text = "${mViewModel.signIn?.nextReward?.rewardCoin}金币+${mViewModel.signIn?.nextReward?.rewardStone}魔石"
        }

        if (mViewModel.signIn == null) {
            mBinding.signOne.text = "0"
            mBinding.signTwo.text = "0"
            mBinding.signThree.text = "0"
            mBinding.signFour.text = "0"
            mBinding.signFive.text = "0"
            mBinding.signSix.text = "0"
            mBinding.signSeven.text = "0"

            mBinding.txtSignOne.text = "0"
            mBinding.txtSignTwo.text = "0"
            mBinding.txtSignThree.text = "0"
            mBinding.txtSignFour.text = "0"
            mBinding.txtSignFive.text = "0"
            mBinding.txtSignSix.text = "0"
            mBinding.txtSignSeven.text = "0"
        } else {
            mViewModel.signIn?.signInBar?.forEachIndexed { index, signInData ->
                if (index == 0) {
                    mBinding.txtSignOne.text = "${signInData.signInDays}天"
                    mBinding.signOne.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signOne) else setRewardNormal(mBinding.signOne)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signOne)
                } else if (index == 1) {
                    mBinding.txtSignTwo.text = "${signInData.signInDays}天"
                    mBinding.signTwo.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signTwo) else setRewardNormal(mBinding.signTwo)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signTwo)
                } else if (index == 2) {
                    mBinding.txtSignThree.text = "${signInData.signInDays}天"
                    mBinding.signThree.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signThree) else setRewardNormal(mBinding.signThree)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signThree)
                } else if (index == 3) {
                    mBinding.txtSignFour.text = "${signInData.signInDays}天"
                    mBinding.signFour.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signFour) else setRewardNormal(mBinding.signFour)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signFour)
                } else if (index == 4) {
                    mBinding.txtSignFive.text = "${signInData.signInDays}天"
                    mBinding.signFive.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signFive) else setRewardNormal(mBinding.signFive)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signFive)
                } else if (index == 5) {
                    mBinding.txtSignSix.text = "${signInData.signInDays}天"
                    mBinding.signSix.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signSix) else setRewardNormal(mBinding.signSix)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signSix)
                } else if (index == 6) {
                    mBinding.txtSignSeven.text = "${signInData.signInDays}天"
                    mBinding.signSeven.text = signInData.rewardCoin.toString()
                    if (signInData.attract?.toInt() == 1) setRewardSpecial(mBinding.signSeven) else setRewardNormal(mBinding.signSeven)
                    if (signInData.signInDays <= mViewModel.signIn?.signInTimes!!) setRewardYes(mBinding.signSeven)
                }
            }
        }

        if (mViewModel.signIn != null) {
            mBinding.txtBottomDes.text = "继续签到${mViewModel.signIn?.nextRewardAttract?.nextRewardAttractRemainDays}天!可拿${mViewModel.signIn?.nextRewardAttract?.nextRewardAttractCoin}金币!"
        } else {
            mBinding.txtBottomDes.text = ""
        }
    }

    private fun setRewardYes(textView: TextView) {
        textView.setBackgroundResource(R.drawable.bg_sign_take)
        textView.text = "已领"
        textView.setTextColor(Color.parseColor("#FA724D"))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
    }

    private fun setRewardNormal(textView: TextView) {
        textView.setBackgroundResource(R.drawable.bg_sign_untake)
//    textView.text = nums.toString()
        textView.setTextColor(Color.parseColor("#FA724D"))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
    }

    private fun setRewardSpecial(textView: TextView) {
        textView.setBackgroundResource(R.drawable.bg_sign_2take)
//    textView.text = nums.toString()
        textView.setTextColor(Color.parseColor("#FA724D"))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "任务系统"

        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                //判断滑动后选择的页面设置相应的RadioButton被选中
                if (position == 0 && mFragments.size == 4) {
                    mBinding.ivThreeDayTag?.visibility = View.VISIBLE
                    EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_new",""))
                } else {
                    mBinding.ivThreeDayTag?.visibility = View.GONE
                    if (position == 0) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_daily",""))
                    } else if (position == 1) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_level",""))
                    } else if (position == 2) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_taskpage_achieve",""))
                    }
                }
            }
        })
    }

    @Subscribe
    fun OnSubscribVM(obj: TaskSystemViewModel.TaskHeadVM) {
        if (obj.success) {
            initView()
        } else {
            Toast.makeText(ChatApp.getInstance().applicationContext, "签到信息未获取", Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe
    fun OnThreeDayTaskDataVM(obj: TaskSystemViewModel.TaskThreeDayVM) {
        if (obj.success && obj.obj!!.isNotEmpty()) {

            /**
             * 标题
             */
            mTitles = arrayOf("三日任务", "日常任务", "等级任务", "成就任务")

            /**
             * Fragments
             */
            mFragments = arrayOf(ThreeDayFragment(), EverydayFragment(), LevelFragment(),
                    AchievementFragment())

            mBinding.ivThreeDayTag.visibility = View.VISIBLE

        }


        mBinding.vp.offscreenPageLimit = 4
        mBinding.vp.adapter = PagerAdapterV2(supportFragmentManager, mFragments, mTitles)

        mBinding.tl4.setViewPager(mBinding.vp)

    }

    @Subscribe
    fun TaskThreeDayData(obj: ThreeDayViewModel.TaskThreeDayData) {
        if (obj.success) {
            // 三日任务
            (mFragments[0] as ThreeDayFragment).setTaskData(obj)
        }
    }


    @Subscribe
    fun TaskEverydayData(obj: EverydayViewModel.TaskEverydayData) {
        if (obj.success) {
            // 每日任务
            if (mFragments.size == 4) {
                (mFragments[1] as EverydayFragment).setTaskData(obj.obj!!)
            } else {
                (mFragments[0] as EverydayFragment).setTaskData(obj.obj!!)
            }
        }
    }

    @Subscribe
    fun TaskAchievementData(obj: AchievementViewModel.TaskAchievementData) {
        if (obj.success) {
            // 每日任务
            if (mFragments.size == 4) {
                (mFragments[3] as AchievementFragment).setTaskData(obj.obj!!)
            } else {
                (mFragments[2] as AchievementFragment).setTaskData(obj.obj!!)
            }
        }
    }

    @Subscribe
    fun TaskLevelData(obj: LevelViewModel.TaskLevelData) {
        if (obj.success) {
            // 每日任务
            if (mFragments.size == 4) {
                (mFragments[2] as LevelFragment).setTaskData(obj.obj!!)
            } else {
                (mFragments[1] as LevelFragment).setTaskData(obj.obj!!)
            }
        }
    }

    @Subscribe
    fun TaskRewardData(obj: ThreeDayViewModel.TaskRewardData) {
        if (obj.success) {
//            Toast.makeText(this, "金币 ： " + obj.obj!!.rewardCoin + " 现金" + obj.obj!!.rewardCash + " 宝石" + obj.obj!!.rewardStone, Toast.LENGTH_SHORT).show()

            ToastConnectAwardActivity.start(this, "奖励领取成功", obj.obj!!.rewardCoin?.toDouble(),
                    obj.obj!!.rewardStone?.toDouble(), obj.obj!!.rewardCash?.toDouble())
        }
    }


}