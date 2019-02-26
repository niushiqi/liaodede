package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityTixianAlipayBinding
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WithdrawDialogActivity
import com.dyyj.idd.chatmore.utils.MobileOrEmailUtils
import com.dyyj.idd.chatmore.viewmodel.AlipayNumberViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : malibo
 * time   : 2018/10/01
 * desc   :
 */
class AlipayNumberActivity: BaseActivity<ActivityTixianAlipayBinding, AlipayNumberViewModel>() {

    companion object {
        const val ALIPAYNUM = "ALIPAYNUM"
        const val ALIPAYNUMINT = "ALIPAYNUMINT"
        const val ALIPAYNAME = "ALIPAYNAME"
        const val WITHDRAWTYPE = "WITHDRAWTYPE"

        fun start(context: Context,myAlipayNum: String,widthdrawNumber: String,widthdrawName: String,widthdrawType: String) {
            val intent = Intent(context, AlipayNumberActivity::class.java)
            intent.putExtra(ALIPAYNUM, myAlipayNum)
            intent.putExtra(ALIPAYNUMINT, widthdrawNumber)
            intent.putExtra(ALIPAYNAME, widthdrawName)
            intent.putExtra(WITHDRAWTYPE, widthdrawType)
            context.startActivity(intent)
        }
    }
    override fun onViewModel(): AlipayNumberViewModel {
        return AlipayNumberViewModel()
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_tixian_alipay
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initLinster()
    }

    private fun initView() {
        val myAlipayNum = intent.getStringExtra(ALIPAYNUM)
        val myAlipayName = intent.getStringExtra(ALIPAYNAME)
        Log.i("myAlipayName","myAlipayName="+myAlipayName)
        if (myAlipayNum.length > 0 && !myAlipayName.equals("null")){
            editHidden()
            mBinding.alipayNum.text = myAlipayNum
            mBinding.textView67.text = myAlipayName
        }else{//第一次进入
            editShow()
        }
    }
    var identNameEt = ""
    var identNameEtName = ""
    private fun initLinster() {

        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
            if (type.equals("editShow")){
                onBackPressed()
            }else if(type.equals("editHidden")){
                onBackPressed()
            }
        }

        mBinding.btnEditor?.setOnClickListener{
            editShow()
        }

        mBinding.btnDone?.setOnClickListener{
            if (type.equals("editShow")){//还没提过现时

                //绑定/修改支付宝账号
                identNameEt = mBinding.identNameEt.text.toString()
                identNameEtName = mBinding.identNameEtName.text.toString()

                    if (TextUtils.isEmpty(identNameEtName)) {//判断输入的姓名是否为空
                        niceToast("请输入姓名")
                        return@setOnClickListener
                    }

                    if (MobileOrEmailUtils.isNumeric(identNameEt)){//手机号的判断
                        if (MobileOrEmailUtils.isMobileNO(identNameEt)){
                            mViewModel.netUpdateAliNum(identNameEt,identNameEtName)
                        }else{
                            niceToast("请输入正确的手机号")
                        }
                    }else{
                        if (MobileOrEmailUtils.isEmail(identNameEt)){
                            mViewModel.netUpdateAliNum(identNameEt,identNameEtName)
                        }else{
                            niceToast("请输入正确的邮箱")
                        }
                    }

            }else if(type.equals("editHidden")){
                //提现提交审核
                mViewModel.netsummitWithdrow(mBinding.alipayNum.text.toString(), mBinding.textView67.text.toString(),intent.getStringExtra(ALIPAYNUMINT),intent.getStringExtra(WITHDRAWTYPE))
            }
        }


    }

    var type = ""
    private fun editShow(){
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "编辑提现账户"
        mBinding.constraintLayout6?.visibility = View.VISIBLE
        mBinding.constraintLayout5?.visibility = View.GONE
        mBinding.tixianValue?.visibility = View.GONE
         type = "editShow"
    }
    private fun editHidden(){
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "确认提现"
        mBinding.constraintLayout6?.visibility = View.GONE
        mBinding.constraintLayout5?.visibility = View.VISIBLE
        mBinding.tixianValue?.visibility = View.VISIBLE
        mBinding.tixianValue?.text = "提现金额："+intent.getStringExtra(ALIPAYNUMINT)+"元"
        mBinding.alipayNum?.text = intent.getStringExtra(ALIPAYNUM)
         type = "editHidden"
    }

    @Subscribe
    fun OnSubscribeVM(obj: AlipayNumberViewModel.UpdateAliNumVM) {
        if(obj.obj?.errorCode == 200){
            editHidden()
            mBinding.alipayNum?.text = identNameEt
            mBinding.textView67?.text = identNameEtName
        }
    }

    @Subscribe
    fun OnSummitVM(obj: AlipayNumberViewModel.SummitWithdrow) {
        if(obj.obj?.errorCode == 200){
            WithdrawDialogActivity.start(this)
            finish()
        }
    }


}