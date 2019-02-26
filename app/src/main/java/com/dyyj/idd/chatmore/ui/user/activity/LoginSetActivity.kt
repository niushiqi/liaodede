package com.dyyj.idd.chatmore.ui.user.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityLoginSetBinding
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.main.fragment.OpenCallFragment
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import com.dyyj.idd.chatmore.utils.KeyboardUtil
import com.dyyj.idd.chatmore.utils.ToolbarUtils
import com.dyyj.idd.chatmore.viewmodel.LoginSetViewModel
import com.dyyj.idd.chatmore.viewmodel.RegisterViewModel
import com.gt.common.gtchat.extension.niceToast
import com.gt.common.gtchat.model.network.NetURL
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class LoginSetActivity : BaseActivity<ActivityLoginSetBinding, LoginSetViewModel>() {
    val CURRENT_USER_ID = "currentUserId";
    companion object {
        const val REQUEST_LOGIN = 100
        const val PHONE = "phone"
        fun start(context: Context, phone: String) {
            val intent = Intent(context, LoginSetActivity::class.java)
            intent.putExtra(PHONE, phone)
            (context as Activity).startActivityForResult(intent, REQUEST_LOGIN)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_login_set
    }

    override fun onViewModel(): LoginSetViewModel {
        return LoginSetViewModel()
    }

//  override fun onToolbar(): Toolbar? {
//    return mBinding.layoutToolbar?.findViewById<Toolbar>(R.id.toolbar)
//  }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        mViewModel.setMobile(intent.getStringExtra(PHONE))
        initView()
        mBinding.llTip.setOnClickListener { H5Activity.start(this@LoginSetActivity, NetURL.USER_AGREEMENT, "用户协议") }
    }

    private fun refreshInput() {
        Log.i("mytag","mViewModel.STATUES="+mViewModel.STATUES)
        if (mViewModel.STATUES == mViewModel.isEnterPhone) {
            mBinding.txtError.visibility = View.GONE
            mBinding.llMobile.visibility = View.VISIBLE
            mBinding.clPassword.visibility = View.GONE
            mBinding.llVerify.visibility = View.GONE
            mBinding.mobileEt.setText("")
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
            mBinding.txtVerify.visibility = View.GONE
            mBinding.mobileEt.setText("")
            mBinding.setpassEt.requestFocus()
            KeyboardUtil.showInputKeyboard(this, mBinding.setpassEt)
            mBinding.txtSwitch.visibility = View.VISIBLE
            mBinding.userPhone.text = "请输入密码:"
            mBinding.txtSwitch.text = "验证码登录"
            mBinding.loginBtn.visibility = View.VISIBLE
            mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
            mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.loginBtn.isEnabled = false
        } else if (mViewModel.STATUES == mViewModel.isEnterVerify) {
            mBinding.txtError.visibility = View.GONE
            mBinding.llMobile.visibility = View.GONE
            mBinding.clPassword.visibility = View.GONE
            mBinding.llVerify.visibility = View.VISIBLE
            mBinding.txtVerify.visibility = View.VISIBLE
            mBinding.txtVerify.setTextColor(Color.parseColor("#884D00"))
            mBinding.txtVerify.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
            mBinding.txtVerify.isEnabled = true
            mBinding.txtSwitch.visibility = View.VISIBLE
            mBinding.txtSwitch.text = "密码登录"
            mBinding.userPhone.text = "请输入验证码:"
            mBinding.verifyEt.setText("")
            mBinding.loginBtn.visibility = View.VISIBLE
            mBinding.loginBtn.isEnabled = false
        }
    }

    private fun initView() {
        title = "返回"
        ToolbarUtils.init(this)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = title
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }

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
                    mBinding.loginBtn.setBackgroundResource(R.drawable.rect_rounded_left_right_arc)
                    mBinding.loginBtn.setTextColor(Color.parseColor("#884D00"))
                    mBinding.loginBtn.isEnabled = true
//                    subscribe.let { subscribe?.dispose() }
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
        mBinding.loginBtn.setOnClickListener {
            if (mViewModel.STATUES == mViewModel.isEnterPhone) {
                mViewModel.STATUES = mViewModel.isEnterPass
                mViewModel.setMobile(mBinding.mobileEt.text.toString())
                refreshInput()
            } else {//输入密码时走的是这个
                showProgressDialog()
                mBinding.txtError.visibility = View.GONE
                mViewModel.netLogin(if (mViewModel.STATUES == mViewModel.isEnterPass) mBinding.setpassEt.text.toString() else mBinding.verifyEt.text.toString())
            }
//            else if (mViewModel.STATUES == mViewModel.isEnterVerify) {
//                showProgressDialog()
//                mViewModel.netMobile()
//            }
        }
        mBinding.txtVerify.setOnClickListener {
            mBinding.userPhone.text = "验证码已发送:"
            showProgressDialog()
            mViewModel.netMobile()
        }
        mBinding.txtSwitch.setOnClickListener {
            if (mViewModel.STATUES == mViewModel.isEnterPass) {
                mViewModel.STATUES = mViewModel.isEnterVerify
                refreshInput()
            } else if (mViewModel.STATUES == mViewModel.isEnterVerify) {
                mViewModel.STATUES = mViewModel.isEnterPass
                refreshInput()
            }
        }

        initPassEdt()
        mBinding.seeIv.setOnClickListener {
            mViewModel.isPassShow = !mViewModel.isPassShow
            initPassEdt()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    fun initPassEdt() {
        if (mViewModel.isPassShow) {
            mBinding.seeIv.setImageResource(R.drawable.icon_see_yes)
            mBinding.setpassEt.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            mBinding.seeIv.setImageResource(R.drawable.icon_see_no)
            mBinding.setpassEt.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }
    }

    private var subscribe: Disposable? = null

    private fun startTime() {
        var count: Long = 30
        subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take(count + 1) //设置循环11次
                .map { count - it }.doOnSubscribe {}

                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({
                    mBinding.txtVerify.isEnabled = false
                    mBinding.txtVerify.text = "${it}秒后重新获取"
                    mBinding.txtVerify.setTextColor(Color.parseColor("#C0C0C0"))
                    mBinding.txtVerify.setBackgroundColor(resources.getColor(android.R.color.transparent))
                }, {}, {
                    mBinding.txtVerify.setOnClickListener {
                        showProgressDialog()
                        mViewModel.netMobile()
                    }
                    mBinding.txtVerify.text = "重新获取验证码"
                    mBinding.txtVerify.setTextColor(Color.parseColor("#FDCB00"))
                    mBinding.txtVerify.setBackgroundResource(R.drawable.rect_round_yellow_stroke)
                    mBinding.txtVerify.isEnabled = true
                })
    }

    @Subscribe
    fun onMobileVM(obj: RegisterViewModel.MobileVM) {
        closeProgressDialog()
        if (obj.mobile) {
            startTime()
        }

    }

    @Subscribe
    fun onLoginSetVM(obj: LoginSetViewModel.LoginSetVM) {
        closeProgressDialog()
        if (obj.success) {
            mDataRepository.saveLoginToken(obj.obj?.token!!)
            obj.obj?.userId?.let {//10432
                //Log.i("saveId","saveId="+it+"obj.obj.userId="+obj.obj.userId)
                PreferenceUtil.commitString(CURRENT_USER_ID, it)//保存用户本次登陆的的userId
                mViewModel.netUserInfo(it, obj.obj.token!!)
            }
//            MainActivity.startOpenCall(this@LoginSetActivity)
//            finish()
        } else {
            //帐号不存在
            if (obj.codeMsg == 3008) {
                RegisterActivity.start(this, intent.getStringExtra("phone"))
                niceToast("帐号不存在,正在为你跳转注册界面")
            } else {
//                niceToast(obj.msg!!)
                mBinding.txtError.visibility = View.VISIBLE
                mBinding.txtError.text = obj.msg
            }
        }
    }

    @Subscribe
    fun onLoginUsrVM(obj: LoginSetViewModel.LoginSetUserVM) {
        mBinding.root.postDelayed({
            closeProgressDialog()
            if (obj.success) {
                if (mDataRepository.checkUserInfoNull(mViewModel.userDetailInfo)) {
                    RegisterUserInfoActivity.start(this)
                } else {
                    OpenCallFragment.isRegister = true
                    ActManagerUtils.getAppManager().finishAllActivity()
                    ActManagerUtils.getAppManager().finishAllActivityVisible()
                    MainActivity.startOpenCall(this@LoginSetActivity)

                }
                setResult(Activity.RESULT_OK)
                //finish()//之崩溃是因为这里多调用了这个，造成的假崩溃
            } else {
                niceToast("个人信息获取失败")
            }
        }, 1000)
    }

    override fun onBackPressedSuper(): Boolean {
        return true
    }

    override fun onBackPressed() {
//        if (mViewModel.STATUES == mViewModel.isEnterPass) {
//            mViewModel.STATUES = mViewModel.isEnterPhone
//            refreshInput()
//        } else {
//            super.onBackPressed()
//        }
        super.onBackPressed()
    }
}