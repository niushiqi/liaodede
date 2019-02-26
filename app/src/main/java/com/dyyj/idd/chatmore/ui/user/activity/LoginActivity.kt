package com.dyyj.idd.chatmore.ui.user.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityLoginBinding
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.KeyboardUtil
import com.dyyj.idd.chatmore.viewmodel.LoginViewModel
import com.gt.common.gtchat.model.network.NetURL
import com.othershe.nicedialog.NiceDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   : 登陆
 */
@RuntimePermissions
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    private val REQUEST_LOGIN: Int = 3

    var isPermissionDenied: Int = -2
    var isShow: Boolean = false

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onViewModel(): LoginViewModel {
        return LoginViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mBinding.mobileEt != null) {
//      mBinding.mobileEt.requestFocus()
            mBinding.mobileEt.postDelayed(Runnable {
                kotlin.run {
                    mBinding.mobileEt.setFocusable(true);
                    mBinding.mobileEt.setFocusableInTouchMode(true);
                    mBinding.mobileEt.requestFocus();
                    (mBinding.mobileEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(mBinding.mobileEt, 0);
                }
            }, 200)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //设置透明状态栏
        window.requestFeature(Window.FEATURE_NO_TITLE)
        ChatApp.getInstance().initMqtt()
        ChatApp.getInstance().initEM()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            super.onCreate(savedInstanceState)
            mBinding.loginBtn.setOnClickListener {
                loginWithPermissionCheck()
            }
            //      mBinding.registerBtn.setOnClickListener {
            //        registerWithPermissionCheck()
        } else {
            super.onCreate(savedInstanceState)
            mBinding.loginBtn.setOnClickListener { login() }
            //mBinding.registerBtn.setOnClickListener { register() }
        }
        mBinding.txtError.visibility = View.GONE
        mBinding.llMobile.visibility = View.VISIBLE
        mBinding.mobileEt.setText("")

        KeyboardUtil.showInputKeyboard(this, mBinding.mobileEt)
        mBinding.txtForget.visibility = View.INVISIBLE
        mBinding.loginBtn.text = "下一步"
        mBinding.loginBtn.visibility = View.VISIBLE
        mBinding.loginBtn.setBackgroundResource(R.drawable.rect_round_solid_gray_light)
        mBinding.loginBtn.setTextColor(Color.parseColor("#C4C4C4"))
        mBinding.loginBtn.isEnabled = false
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

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
        mBinding.llTip.setOnClickListener { H5Activity.start(this@LoginActivity, NetURL.USER_AGREEMENT, "用户协议") }
//
//    if ((mDataRepository.getUserInfoEntity() != null) and (mDataRepository.getLoginToken() != null) and (!TextUtils.isEmpty(
//            mDataRepository.getLoginToken()))) {
//      mViewModel.netLogin(mDataRepository.getLoginToken()!!)
//      return
//    }
    }
    /**
     *  拨打语音电话
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.USE_SIP,
            Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WAKE_LOCK)
    fun login() {
//    val info = mDataRepository.getUserInfoEntity()
//    if (info?.userId == null) {
//      LoginSetActivity.start(this)
//    } else {
//      MainActivity.startOpenCall(this)
//      finish()
//    }
        DeviceUtils.clearDeviceID()
        showProgressDialog()
        mViewModel.netSwitchApi(mBinding.mobileEt.text.toString())
    }

    @Subscribe
    fun onSwitchVM(obj: LoginViewModel.SwitchVM) {
        closeProgressDialog()
        if (obj.success) {
            LoginSetActivity.start(this, mBinding.mobileEt.text.toString())
        } else {
            RegisterActivity.start(this, mBinding.mobileEt.text.toString())
        }
        //finish()
    }

//  @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//                   Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                   Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.READ_PHONE_STATE,
//                   Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO,
//                   Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.WAKE_LOCK)
//  fun register() {
//    RegisterActivity.start(this)
//  }

    /*@SuppressLint("NoCorrespondingNeedsPermission")
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.USE_SIP,
            Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WAKE_LOCK)
    fun showLoginDialog(request: PermissionRequest) {
        //letPermissionEabled()
        *//*MaterialDialog.Builder(this).title(R.string.title_hint).content("应用需要相关权限才能开启功能").positiveText(
                R.string.button_ok).show()*//*
    }*/

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
        when (requestCode) {
            REQUEST_LOGIN -> {
                ++isPermissionDenied
                grantResults.forEachIndexed { index, i ->
                    if (i == PackageManager.PERMISSION_DENIED){
                        isShow = true
                    }
                }
                if (isPermissionDenied == 1 && isShow) {
                    letPermissionEabled()
                    --isPermissionDenied
                }
            }
        }
    }

    fun letPermissionEabled() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_permission_remind)     //设置dialog布局文件
                .setConvertListener { holder, dialog ->
                    holder.setOnClickListener(R.id.iv_close) {
                        dialog.dismiss()
                    }
                    holder.setOnClickListener(R.id.let_enable) {
                        dialog.dismiss()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:" + packageName)
                            startActivity(intent)
                        }
                    }
                }
                .setDimAmount(0.8f)     //调节灰色背景透明度[0-1]，默认0.5f
                .setShowBottom(false)     //是否在底部显示dialog，默认flase
                .setWidth(260)     //dialog宽度（单位：dp），默认为屏幕宽度
                .setHeight(300)     //dialog高度（单位：dp），默认为WRAP_CONTENT
                .setOutCancel(false)     //点击dialog外是否可取消，默认true
                //                .setAnimStyle(R.style.EnterExitAnimation)//设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
                .show(supportFragmentManager)     //显示dialog
    }

//  @Subscribe
//  fun OnSubscribVM(obj: LoginViewModel.LoginSetVM) {
//    closeProgressDialog()
//    if (obj.success) {
//      mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(obj.obj!!))
//      mViewModel.netUserInfo()
//    } else {
//      //帐号不存在
//      if (obj.codeMsg == 3008) {
//        RegisterActivity.start(this)
//        niceToast("帐号不存在,正在为你跳转注册界面")
//      } else if (obj.codeMsg == 1301) {
//        niceToast("验证过期，请重新登录")
//        LoginSetActivity.start(this@LoginActivity)
//      } else {
//        niceToast(obj.msg!!)
//      }
//    }
//  }
//
//  @Subscribe
//  fun OnSubscribeVM(obj: LoginViewModel.LoginSetUserVM) {
//    closeProgressDialog()
//    if (obj.success) {
//      if (mDataRepository.checkUserInfoNull(mViewModel.userInfo)) {
//        RegisterUserInfoActivity.start(this@LoginActivity)
//      } else {
//        MainActivity.startOpenCall(this@LoginActivity)
//      }
//      finish()
//    } else {
//      niceToast("个人信息获取失败")
//    }
//  }

//  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//    if ((requestCode == RegisterActivity.REQUEST_REGISTER) or (requestCode == LoginSetActivity.REQUEST_LOGIN) and (resultCode == Activity.RESULT_OK)) {
//      finish()
//    }
//  }
}