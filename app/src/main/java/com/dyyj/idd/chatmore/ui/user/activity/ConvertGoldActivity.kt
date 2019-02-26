package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityConvertGoldBinding
import com.dyyj.idd.chatmore.utils.ToolbarUtils
import com.dyyj.idd.chatmore.viewmodel.CovertGoldViewModel
import com.dyyj.idd.chatmore.viewmodel.GoldViewModel
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ConvertGoldActivity : BaseActivity<ActivityConvertGoldBinding, CovertGoldViewModel>(), TextWatcher {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ConvertGoldActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_convert_gold
    }

    override fun onViewModel(): CovertGoldViewModel {
        return CovertGoldViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        title = "兑换"
        ToolbarUtils.init(this)
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "兑换"
        mViewModel.netConvertGold(0, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun OnSubscribeVM(obj: CovertGoldViewModel.ConvertGoldVM) {
        if (obj.first) {
            if (obj.success) {
                initView(obj)
            }
//            else {
//                finish()
//            }
        } else {
            mBinding.editText2.setText("¥" + obj.obj?.exchangeCash)
            if (obj.obj?.exchangeCash == 0.0) {
                mBinding.btnOk.isEnabled = false
                mBinding.btnOk.setBackgroundResource(R.drawable.rect_round_solid_gray)
                mBinding.btnOk.setTextColor(Color.parseColor("#C8C8C8"))
            } else {
                mBinding.btnOk.isEnabled = true
                mBinding.btnOk.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
                mBinding.btnOk.setTextColor(Color.parseColor("#884D00"))
            }
        }
    }

    @Subscribe
    fun OnSubscribVM(obj: CovertGoldViewModel.ConvertVM) {
        closeProgressDialog()
        if (obj.success) {
            Toast.makeText(ChatApp.mInstance?.applicationContext, "兑换成功，现金已入账", Toast.LENGTH_SHORT).show()
            mBinding.editText.setText("")
            mBinding.editText2.setText("¥")
            mViewModel.netConvertGold(0, true)
            EventBus.getDefault().post(GoldViewModel.RefreshGoldVM())
        } else {
            Toast.makeText(ChatApp.mInstance?.applicationContext, "兑换失败，未扣减您的金币，请重试", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView(obj: CovertGoldViewModel.ConvertGoldVM) {
        mBinding.txtCoinTotal.text = obj.obj?.coin
        mBinding.txtConvertDes.text = ("当前级别为" + "N" + "级，金币兑换率" + obj.obj?.exchangeRate)
        mBinding.btnOk.setOnClickListener {
            if (obj.obj?.isFirstTimeChange == 1) {
                //第一次兑换
                if ((mBinding.editText.text.toString().toDouble() % 50) != 0.0) {
                    Toast.makeText(ChatApp.getInstance().applicationContext, "请输入50金币的整数倍", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                //非第一次兑换
                if (mBinding.editText.text.toString().toDouble() < 2000) {
                    Toast.makeText(ChatApp.getInstance().applicationContext, "非首次充值，金币数量不得低于2000", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if ((mBinding.editText.text.toString().toDouble() % 1000 != 0.0)) {
                    Toast.makeText(ChatApp.getInstance().applicationContext, "请输入1000金币的整数倍", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            showProgressDialog()
            mViewModel.netConvert(mBinding.editText.text.toString().toInt())
        }
        mBinding.editText.addTextChangedListener(this)
      mBinding.btnGetGold.setOnClickListener { niceToast("功能尚未开放，敬请期待") }
    }

    override fun afterTextChanged(s: Editable?) {
        if (TextUtils.isEmpty(s.toString())) {
            mBinding.editText2.setText("¥" + s.toString())

            mBinding.editText.setBackgroundResource(R.drawable.bg_rect_round_stroke_gray)
            mBinding.editText.setTextColor(Color.parseColor("#3E3E3E"))
            val nums = if (TextUtils.isEmpty(s)) 0 else s.toString().toInt()
            mViewModel.netConvertGold(nums, false)

            mBinding.btnOk.isEnabled = false
            mBinding.btnOk.setBackgroundResource(R.drawable.rect_round_solid_gray)
            mBinding.btnOk.setTextColor(Color.parseColor("#C8C8C8"))
        } else {
            if (s.toString().toInt() > mViewModel.data?.coin?.toInt() ?: 0) {
                Toast.makeText(niceChatContext(), "金币兑换额度不足", Toast.LENGTH_SHORT).show()

                mBinding.editText.setBackgroundResource(R.drawable.bg_rect_round_red_solid)
                mBinding.editText.setTextColor(Color.parseColor("#F41515"))

                mBinding.btnOk.isEnabled = false
                mBinding.btnOk.setBackgroundResource(R.drawable.rect_round_solid_gray)
                mBinding.btnOk.setTextColor(Color.parseColor("#C8C8C8"))
            } else {
                mBinding.editText.setBackgroundResource(R.drawable.bg_rect_round_stroke_gray)
                mBinding.editText.setTextColor(Color.parseColor("#3E3E3E"))
                mViewModel.netConvertGold(s.toString().toInt(), false)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}