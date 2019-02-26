package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.DialogPayResultBinding
import com.dyyj.idd.chatmore.ui.user.activity.PayActionActivity
import com.dyyj.idd.chatmore.viewmodel.DialogPayResultViewModel
import org.greenrobot.eventbus.EventBus


@Suppress("UNREACHABLE_CODE")
/**
 * author : malibo
 * time   : 2018/10/04
 * desc   : 支付成功后的dialog
 */
class PayResultDialogActivity : BaseActivity<DialogPayResultBinding, DialogPayResultViewModel>() {
    val TYPE_STONE: Int? = 1
    val TYPE_PEA: Int? = 2
    companion object {

        const val INDEX = "INDEX"
        const val GOODSID = "GOODSID"
        const val GOODSPRICE = "GOODSPRICE"
        const val GOODSDETAIL = "GOODSDETAIL"
        fun start(context: Context,index:Int,goodsID: String, goodsPrice: String, goodsDetail: String) {
            val intent = Intent(context, PayResultDialogActivity::class.java);
            intent.putExtra(INDEX,index)
            intent.putExtra(GOODSID, goodsID)
            intent.putExtra(GOODSPRICE, goodsPrice)
            intent.putExtra(GOODSDETAIL, goodsDetail)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_pay_result

    }

    override fun onViewModel(): DialogPayResultViewModel {
        return DialogPayResultViewModel()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @SuppressLint("WrongViewCast")
    private fun initView() {
        if (intent.getStringExtra(PayActionActivity.GOODSDETAIL).contains("得得豆")) {
            mBinding.titleTv.setImageResource(R.drawable.ic_pea_dialog)
            mBinding.tvMoshiValue.text = intent.getStringExtra(GOODSDETAIL).replace("得得豆X","+")
            mBinding.tvMoshiValue.setTextColor(Color.parseColor("#744f00"))
        }else if (intent.getStringExtra(PayActionActivity.GOODSDETAIL).contains("魔石")) {
            //mBinding.tvMoshiValue.setTextColor(Color.parseColor("#864EB4"))
            //mBinding.titleTv.setImageResource(R.drawable.img_moshi)
            mBinding.tvMoshiValue.text = intent.getStringExtra(GOODSDETAIL).replace("魔石X","+")
        }else{
            mBinding.titleTv.setImageResource(R.drawable.ic_match_card)
            mBinding.tvMoshiValue.text = intent.getStringExtra(GOODSDETAIL).replace("匹配卡X","+")
        }

        if (intent.getIntExtra(INDEX,0) == 0){
            mBinding.closeIv.visibility = View.GONE
            mBinding.okBtn.setOnClickListener{finish()}
        }else{
            mBinding.titleTv.setImageResource(R.drawable.img_moshi_hui)
            mBinding.tvMoshiValue.text = "再次购买"
            mBinding.tvMoshiValue.setTextColor(Color.parseColor("#3E3E3E"))
            mBinding.closeIv.visibility = View.VISIBLE
            mBinding.closeIv.setOnClickListener{finish()}
            mBinding.okBtn.setOnClickListener{
                PayActionActivity.start(this, intent.getStringExtra(GOODSID),intent.getStringExtra(GOODSPRICE),intent.getStringExtra(GOODSDETAIL))
            }
        }

    }

}