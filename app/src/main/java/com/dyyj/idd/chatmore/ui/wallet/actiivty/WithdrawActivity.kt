package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityWithdrawBinding
import com.dyyj.idd.chatmore.ui.adapter.MyGridAdapter
import com.dyyj.idd.chatmore.ui.user.activity.AlipayNumberActivity
import com.dyyj.idd.chatmore.ui.user.activity.WithdrawRecordActivity
import com.dyyj.idd.chatmore.viewmodel.WithdrawfViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


@Suppress("UNREACHABLE_CODE")
/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/16
 * desc   : 提现
 */
class WithdrawActivity : BaseActivity<ActivityWithdrawBinding, WithdrawfViewModel>() {

    companion object {
        const val WIDTHDRAWTYPE = "WIDTHDRAWTYPE"
        fun start(context: Context, widthdrawType: String) {
            val intent = Intent(context, WithdrawActivity::class.java);
            intent.putExtra(WIDTHDRAWTYPE, widthdrawType)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_withdraw
    }

    override fun onViewModel(): WithdrawfViewModel {
        return WithdrawfViewModel()
    }

    override fun onResume() {
        super.onResume()
        mBinding.withdrawBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc4)
        mBinding.withdrawBtn.setTextColor(Color.parseColor("#C8C8C8"))
        mViewModel.netCash(intent.getStringExtra(WIDTHDRAWTYPE))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        initAdapter()
        initLinster()
//        mViewModel.netCash(intent.getStringExtra(WIDTHDRAWTYPE))
    }
    var num: String = ""
    var myAlipayNumber = ""
    var myAlipayName = ""
    private fun initLinster() {
        mBinding.withdrawBtn.setOnClickListener {
            //      WithdrawDialogActivity.start(this)
            if (isOk == 0) {
//                Toast.makeText(this, reason, Toast.LENGTH_SHORT).show()
                niceToast(reason)
            } else {
                Log.i("aaa","myAlipayName="+myAlipayName)
                AlipayNumberActivity.start(this, myAlipayNumber , num,myAlipayName,intent.getStringExtra(WIDTHDRAWTYPE))
            }
//      NoWorkPop(this@WithdrawActivity).showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @SuppressLint("WrongViewCast")
    private fun initView() {
        val widthdrawType = intent.getStringExtra(WIDTHDRAWTYPE);
        findViewById<View>(R.id.toolbar_back_iv).setOnClickListener { onBackPressed() }

        if (widthdrawType.equals("social")) {
            findViewById<TextView>(R.id.toolbar_title_tv).text = "交友提现"
            mBinding.tvAttention.text = "注意事项：\n1.交友得钱：储存社交行为带来现金收益：\n    开启语音聊天、给好友发消息、领定时红包等。\n2.满足登录天数和等级要求，就可以提现哦 \n3.提现申请将在1-3个工作日内审批，请随时关注提现记录\n4.根据国家法规要求，聊得得将为您代缴提现税款、提现手续费"

        } else if (widthdrawType.equals("invite")) {
            findViewById<TextView>(R.id.toolbar_title_tv).text = "邀新提现"
            mBinding.tvAttention.text = "注意事项：\n1.邀请得钱：储存邀请朋友带来的现金收益:\n    每次邀请成功1人，可获2元。累计邀请达到人数要求，还加送现金。\n2.余额足够就可以提现哦\n3.提现申请将在1-3个工作日内审批，请随时关注提现记录\n4.根据国家法规要求，聊得得将为您代缴提现税款、提现手续费"

        }

        findViewById<TextView>(R.id.right_iv).text = "提现记录"
        findViewById<View>(R.id.right_iv)?.setOnClickListener { WithdrawRecordActivity.start(this) }


        mBinding.withdrawBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc4)
        mBinding.withdrawBtn.setTextColor(Color.parseColor("#C8C8C8"))

    }


    private fun initAdapter() {


    }
    @Subscribe
    fun OnSubscribeVM(obj: WithdrawfViewModel.CashVM) {
        mBinding.priceValueTv.text = "${obj.obj?.balance}"
        mBinding.tvTip.text = "${obj.obj?.tip}"
        myAlipayNumber = "${obj.obj?.alipayAccount}"
        myAlipayName = "${obj.obj?.alipayRealName}"

        val adapter = MyGridAdapter(this, obj.obj?.withdrawUnit);
        var array = obj.obj?.withdrawUnit!!.asList()
        mBinding.gvGrideview.setAdapter(adapter)
        val widthdrawType = intent.getStringExtra(WIDTHDRAWTYPE);
        mBinding.gvGrideview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            adapter.setSeclection(position);
            adapter.notifyDataSetChanged();
            num = array.get(position)

            if (widthdrawType.equals("invite")) {
                mViewModel.netWithdrawCheck(widthdrawType, array.get(position))
            } else if (widthdrawType.equals("social")) {
                mViewModel.netWithdrawCheck(widthdrawType, array.get(position))
            }


        })
    }


    var isOk: Int = 0
    var reason: String = "请选择要提现的金额"
    @Subscribe
    fun OnWithdrawCheckVM(obj: WithdrawfViewModel.WithdrawCheckVM) {
        mBinding.tvTip.text = "${obj.obj?.tip}"
        isOk = obj.obj?.isOk!!
        reason = obj.obj?.reason!!
        if (obj.obj?.isOk == 1) {
            mBinding.withdrawBtn.setBackgroundResource(
                    R.drawable.rect_rounded_left_right_arc)
            mBinding.withdrawBtn.setTextColor(
                    Color.parseColor("#884D00"))
        }else{
            mBinding.withdrawBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc4)
            mBinding.withdrawBtn.setTextColor(Color.parseColor("#C8C8C8"))
        }

    }

}