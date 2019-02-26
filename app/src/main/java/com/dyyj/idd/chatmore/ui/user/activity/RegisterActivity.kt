package com.dyyj.idd.chatmore.ui.user.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES.KITKAT_WATCH
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityRegisterBinding
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.utils.KeyboardUtil
import com.dyyj.idd.chatmore.viewmodel.RegisterViewModel
import com.gt.common.gtchat.model.network.NetURL
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   : 注册
 */
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>() {

    val CURRENT_USER_ID = "currentUserId";
    companion object {
        const val REQUEST_REGISTER = 101;
        const val PHONE = "phone"
        fun start(context: Context, phone: String) {
            val intent = Intent(context, RegisterActivity::class.java)
            intent.putExtra(PHONE, phone)
            (context as Activity).startActivityForResult(intent, REQUEST_REGISTER)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun onViewModel(): RegisterViewModel {
        return RegisterViewModel()
    }

  override fun onToolbar(): Toolbar? {
    return mBinding.layoutToolbar?.findViewById<Toolbar>(R.id.toolbar)
  }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        //设置透明状态栏
        window.requestFeature(Window.FEATURE_NO_TITLE)
        val window = window
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (android.os.Build.VERSION.SDK_INT >= KITKAT_WATCH) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        mViewModel.setMobile(intent.getStringExtra("phone"))
        mViewModel.netMobile()//进入页面直接获取验证码
    }

    private fun initView() {
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的"
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }

//    title = "手机注册"
//    ToolbarUtils.init(this)

//    val agreement = "聊得得用户协议".fontColor("#5B94FF", true)
//    mBinding.verifyTv.setOnClickListener {
//      showProgressDialog()
//      mViewModel.netMobile(mBinding.mobileEt.text.toString())
//    }
//    mBinding.commitBtn.setOnClickListener {
//      showProgressDialog()
//      mViewModel.setMobile(mBinding.mobileEt.text.toString())
//      mViewModel.netRegister(mBinding.passwordEt.text.toString(), mBinding.setpassEt.text.toString(), mBinding.inviteEt.text.toString().trim())
//    }
        //用户协议
        mBinding.llTip.setOnClickListener {
            H5Activity.start(this@RegisterActivity, NetURL.USER_AGREEMENT, "用户协议")
        }
//    //隐私协议
//    mBinding.agreementTv4.setOnClickListener {
//      H5Activity.start(this@RegisterActivity, NetURL.USER_PRIVACY, "隐私协议")
//    }
//    mBinding.layoutToolbar?.findViewById<View>(
//        R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
//
//    initPassEdt()
//    mBinding.seeIv.setOnClickListener {
//      mViewModel.isPassShow = !mViewModel.isPassShow
//      initPassEdt()
//    }
        refreshInput()
        mBinding.mobileEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                if ((s != null) and (Util.isMobile(s.toString()))) {
                if ((s != null) and (!TextUtils.isEmpty(s))) {
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#884D00"))
                    mBinding.loginBtn.isEnabled = true
                } else {
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
                    mBinding.loginBtn.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mBinding.verifyTv.setOnClickListener {
            showProgressDialog()
            mViewModel.netMobile()
        }
        mBinding.setpassEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                if ((s != null) and (s?.length ?: 0 > 5)) {
                if ((s != null) and (!TextUtils.isEmpty(s))) {
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#884D00"))
                    mBinding.loginBtn.isEnabled = true
                } else {
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
                    mBinding.loginBtn.isEnabled = false
                    mBinding.loginBtn.text = "登录"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mBinding.verifyEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if ((s != null) and (!TextUtils.isEmpty(s))) {
//          subscribe.let {
//            subscribe?.dispose()
//            mBinding.verifyTv.isEnabled = true
//            mBinding.verifyTv.text = "获取验证码"
//          }
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#884D00"))
                    mBinding.loginBtn.isEnabled = true
                    mBinding.loginBtn.text = "确定"
                } else {
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
                    mBinding.loginBtn.isEnabled = false
                    mBinding.loginBtn.text = "确定"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mBinding.loginBtn.setOnClickListener {
            if (mViewModel.STATUES == mViewModel.isEnterPhone) {
                mViewModel.STATUES = mViewModel.isEnterVerify
                mViewModel.setMobile(mBinding.mobileEt.text.toString())
                refreshInput()
            } else if (mViewModel.STATUES == mViewModel.isEnterPass) {
                showProgressDialog()
                mBinding.txtError.visibility = View.GONE
                mViewModel.netRegister(mBinding.verifyEt.text.toString(), mBinding.setpassEt.text.toString(), "")
            } else if (mViewModel.STATUES == mViewModel.isEnterVerify) {
//        showProgressDialog()
//        mViewModel.netMobile()
                showProgressDialog()
                mViewModel.netCheckVerifyCode(mBinding.verifyEt.text.toString())
            }
        }
    }

    private fun refreshInput() {
        if (mViewModel.STATUES == mViewModel.isEnterPhone) {
            mBinding.txtError.visibility = View.GONE
            mBinding.llMobile.visibility = View.VISIBLE
            mBinding.clPassword.visibility = View.GONE
            mBinding.llVerify.visibility = View.GONE
            mBinding.mobileEt.setText("")
            mBinding.txtForget.visibility = View.INVISIBLE
            mBinding.loginBtn.text = "确定"
            mBinding.loginBtn.visibility = View.VISIBLE
            mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
            mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.loginBtn.isEnabled = false
        } else if (mViewModel.STATUES == mViewModel.isEnterPass) {
            mBinding.txtError.visibility = View.GONE
            mBinding.llMobile.visibility = View.GONE
            mBinding.clPassword.visibility = View.VISIBLE
            mBinding.llVerify.visibility = View.GONE
            mBinding.setpassEt.setText("")
            mBinding.setpassEt.requestFocus()
            KeyboardUtil.showInputKeyboard(this, mBinding.setpassEt)
            mBinding.titleTv.text = "请输入密码:"
            mBinding.loginBtn.text = "登录"
            mBinding.txtForget.visibility = View.INVISIBLE
            mBinding.loginBtn.visibility = View.VISIBLE
            mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
            mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.loginBtn.isEnabled = false
        } else if (mViewModel.STATUES == mViewModel.isEnterVerify) {
            mBinding.txtError.visibility = View.GONE
            mBinding.llMobile.visibility = View.GONE
            mBinding.clPassword.visibility = View.GONE
            mBinding.llVerify.visibility = View.VISIBLE
            mBinding.txtForget.visibility = View.INVISIBLE
            mBinding.verifyEt.setText("")
            mBinding.verifyTv.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
            mBinding.verifyTv.setTextColor(Color.parseColor("#884D00"))
//      mBinding.verifyTv.setBackgroundResource(R.drawable.rect_round_yellow_stroke)
//      mBinding.verifyTv.setTextColor(Color.parseColor("#FDCB00"))
//      mBinding.verifyTv.isEnabled = true
            mBinding.loginBtn.text = "确定"
            mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
            mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.loginBtn.isEnabled = false
            mBinding.loginBtn.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    var subscribe: Disposable? = null
    private fun startTime() {
        var count: Long = 30
        subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take(count + 1) //设置循环11次
                .map { count - it }.doOnSubscribe {}
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({
                    mBinding.verifyTv.text = "${it}秒后重新获取"
//                     mBinding.verifyTv.isEnabled = false
                    mBinding.verifyTv.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    mBinding.verifyTv.setTextColor(Color.parseColor("#C0C0C0"))
                }, {}, {
                    mBinding.verifyTv.setBackgroundResource(R.drawable.rect_round_yellow_stroke)
                    mBinding.verifyTv.setTextColor(Color.parseColor("#FDCB00"))
//                     mBinding.verifyTv.isEnabled = true
                    mBinding.verifyTv.text = "重新获取验证码"
                })
        mViewModel.mCompositeDisposable.add(subscribe!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInviteCodeVM(obj: RegisterViewModel.VerifyCodeVM) {
        closeProgressDialog()
        if (obj.success) {
            mViewModel.isInviteCodeOk = true
            //mBinding.verifyTv.isEnabled = false
        }
    }

    @Subscribe
    fun onMobileVM(obj: RegisterViewModel.MobileVM) {
        closeProgressDialog()
        if (obj.mobile) {
            if (subscribe == null || subscribe!!.isDisposed) {
                startTime()
            }
        }

    }

    @Subscribe
    fun onVerifyOk(obj: RegisterViewModel.VerifyOk) {//验证码是否验证成功
        closeProgressDialog()
        if (obj.success) {
            mViewModel.STATUES = mViewModel.isEnterPass
            refreshInput()
        } else {
            mBinding.txtError.visibility = View.VISIBLE
            mBinding.txtError.text = obj.message
        }
    }

    @Subscribe
    fun onVerifyVM(obj: RegisterViewModel.VerifyVM) {
        closeProgressDialog()

        if (obj.verify) {
            mViewModel.saveAccountInfo(obj.obj!!)
            mDataRepository.saveLoginToken(obj.obj?.token!!)
            Log.i("chaogeww","obj.obj.userId!!="+obj.obj.userId)
            PreferenceUtil.commitString(CURRENT_USER_ID, obj.obj.userId)//保存用户本次登陆的的userId
            mViewModel.netUserInfo(obj.obj?.userId!!)
            mViewModel.uploadChannel(obj.obj?.userId!!)
            RegisterUserInfoActivity.start(this)
            finish()

        } else {
            obj.message?.let {
                //        niceToast(it)
                mBinding.txtError.visibility = View.VISIBLE
                mBinding.txtError.text = obj.message
            }

        }


    }

    override fun onBackPressedSuper(): Boolean {
        return true
    }

    override fun onBackPressed() {
        if (mViewModel.STATUES == mViewModel.isEnterPass) {
            mViewModel.STATUES = mViewModel.isEnterVerify
            refreshInput()
        }
//    else if (mViewModel.STATUES == mViewModel.isEnterVerify) {
//      mViewModel.STATUES = mViewModel.isEnterPhone
//      refreshInput()
//    }
        else {
            super.onBackPressed()
        }
    }
}

