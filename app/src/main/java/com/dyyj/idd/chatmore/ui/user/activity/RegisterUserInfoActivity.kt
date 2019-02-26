package com.dyyj.idd.chatmore.ui.user.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityRegisterUserInfoBinding
import com.dyyj.idd.chatmore.viewmodel.RegisterUserInfoViewModel
import com.gt.common.gtchat.extension.niceGlide
import com.zhihu.matisse.Matisse
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_register_user_info.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   : 完善用户信息
 */
@RuntimePermissions
class RegisterUserInfoActivity : BaseActivity<ActivityRegisterUserInfoBinding, RegisterUserInfoViewModel>() {

    companion object {
        const val REQUEST_CODE_CHOOSE = 2
        fun start(context: Context) {
            context.startActivity(Intent(context, RegisterUserInfoActivity::class.java))
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_register_user_info
    }

    override fun onViewModel(): RegisterUserInfoViewModel {
        return RegisterUserInfoViewModel()
    }

//    override fun onToolbar(): Toolbar? {
//        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        initListenter()
    }

    private fun checkBtnStatus() {
        if ((mBinding.nicknameTv.text.isNotBlank()) and (mBinding.dateTv.text.isNotBlank()) and (mViewModel.getAvatar() != null)
                and (mBinding.sex != 0)) {
            mBinding.enterBtn.setTextColor(Color.parseColor("#884D00"))
            mBinding.enterBtn.isEnabled = true
            mBinding.enterBtn.setBackgroundResource(R.drawable.rect_round_solid_yellow)
        } else {
            mBinding.enterBtn.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.enterBtn.isEnabled = false
            mBinding.enterBtn.setBackgroundResource(R.drawable.rect_round_solid_gray)
        }
    }

//    private fun initBottomHeight(): Int {
//        val point = Point()
//        windowManager.defaultDisplay.getSize(point)
//
//        val display = windowManager.defaultDisplay
//        val dm = DisplayMetrics()
//        val clazz = Class.forName("android.view.Display")
//        val method = clazz.getMethod("getRealMetrics",DisplayMetrics::class.java)
//        method.invoke(display, dm)
//
//        return dm.heightPixels - point.y
//    }

    private fun initView() {
//        title = "完善资料"
//        ToolbarUtils.init(this)
        mBinding.avatar = false
        mBinding.sex = 0

//        val height = initBottomHeight()
//        if (height > 0) {
//            mBinding
//        }
    }

    private fun initListenter() {
        mBinding.avatarTv.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openCameraWithPermissionCheck()
            } else {
                openCamera()
            }
        }
        mBinding.avatarIv.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openCameraWithPermissionCheck()
            } else {
                openCamera()
            }
        }
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.clNick.setOnClickListener { showEditNickname() }
        mBinding.clDate.setOnClickListener { TPicker.show(true) }
        mBinding.sexManIv.setOnClickListener {
            mBinding.sex = 1
            mBinding.updateAvatarTv.visibility = View.VISIBLE
            mBinding.updateAvatarTv.text = "性别选定后，将不能更改哦~"
            mBinding.updateAvatarTv.setTextColor(Color.parseColor("#6CBE44"))
            checkBtnStatus()
        }
        mBinding.sexWomanIv.setOnClickListener {
            mBinding.sex = 2
            mBinding.updateAvatarTv.visibility = View.VISIBLE
            mBinding.updateAvatarTv.text = "性别选定后，将不能更改哦~"
            mBinding.updateAvatarTv.setTextColor(Color.parseColor("#FF7C91"))
            checkBtnStatus()
        }
        mBinding.enterBtn.setOnClickListener {
            //      val avatar = mViewModel.getAvatar()?.avatarFilename ?: return@setOnClickListener
//      if (mBinding.sex != 0) {
//      } else {
//        return@setOnClickListener
//      }
//      if (TextUtils.isEmpty(mBinding.nicknameTv.text.toString())) return@setOnClickListener
//      if (TextUtils.isEmpty(mBinding.dateTv.text.toString())) return@setOnClickListener
            showProgressDialog()
            mViewModel.netCommitUserInfo(nickname = mBinding.nicknameTv.text.toString(), gender = mBinding.sex!!,
                    birthday = mBinding.dateTv.text.toString())
        }
    }

    private val TPicker: TimePickerView by lazy {
        val calendarStart = Calendar.getInstance()
        calendarStart.set(1915, 0, 1)
        val calendarEnd: Calendar = Calendar.getInstance()
        calendarEnd.set(2015, 0, 1)
        val calendarSelect = Calendar.getInstance()
        calendarSelect.set(1995, 0, 1)
        TimePickerBuilder(this@RegisterUserInfoActivity, OnTimeSelectListener { date, v ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd").format(date)
            mBinding.dateTv.date_tv.text = dateStr
            checkBtnStatus()
        })
//            .isDialog(true)
                .setDecorView(findViewById(R.id.rootview))
                .setDate(calendarSelect)
                .setRangDate(calendarStart, calendarEnd)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setLayoutRes(R.layout.layout_custom_time_picker, {
                    it.findViewById<TextView>(R.id.picker_title).text = "选择生日"
                    it.findViewById<TextView>(R.id.picker_cancel).setOnClickListener { TPicker.dismiss() }
                    it.findViewById<TextView>(R.id.picker_ok).setOnClickListener {
                        TPicker.returnData()
                        TPicker.dismiss()
                    }
                })
//            .isDialog(true)
                .build()
    }

    /**
     * 输入昵称
     */
    private fun showEditNickname() {
        MaterialDialog.Builder(this).title("提示").content("请输入你的昵称").inputType(
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS).inputRange(
                2, 16).positiveText("确定").input("", "", false, { dialog, input ->
            mBinding.nicknameTv.text = input.toString().trim()
            mBinding.nicknameTv.setTextColor(Color.parseColor("#999999"))
            checkBtnStatus()
        }).show()
    }


    /**
     * 选择日期
     */
    @SuppressLint("SetTextI18n")
    private fun showSelectDate() {
        //获取Calendar对象，用于获取当前时间
        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth -> mBinding.dateTv.date_tv.text = "$year-${month + 1}-$dayOfMonth" },
                year, month, day).show()
    }

    /**
     * 打开拍照
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    fun openCamera() {
        mViewModel.openPhoto(this@RegisterUserInfoActivity, 1)

    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    fun showCameraDialog(request: PermissionRequest) {
        MaterialDialog.Builder(this).title(R.string.title_hint).content(
                R.string.permission_camera).positiveText(R.string.button_ok).show()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            val list = Matisse.obtainPathResult(data)
            showProgressDialog()
            mViewModel.netUploadAvatar(list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onNickNameVM(obj: RegisterUserInfoViewModel.NickNameErrorVM) {
        mBinding.imageView25.visibility = View.VISIBLE
        mBinding.textView19.visibility = View.VISIBLE
        mBinding.nicknameTv.setTextColor(Color.parseColor("#DC1D1D"))
    }

    @Subscribe
    fun onAvatarVM(obj: RegisterUserInfoViewModel.AvatarVM) {
        closeProgressDialog()
        if (obj.avatar != null && obj.obj != null) {
            mBinding.avatar = true
            niceGlide().load(obj.obj.avatar).asBitmap().transform(
                    CropCircleTransformation(this)).crossFade().error(R.drawable.bg_circle_black).placeholder(
                    R.drawable.bg_circle_black).into(mBinding.avatarIv)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(this).load(obj.obj.avatar).apply(requestOption).into(mBinding.avatarIv)
            mBinding.updateAvatarTv.text = "恭喜，头像上传成功！"
            mBinding.updateAvatarTv.visibility = View.VISIBLE
            mBinding.updateAvatarTv.setTextColor(Color.parseColor("#6CBE44"))
        } else {
            mBinding.avatar = false
            mBinding.avatarTv.setBackgroundResource(R.drawable.ic_avatar_errer_bg)
            mBinding.avatarTv.text = "重新上传"
            mBinding.avatarTv.setTextColor(Color.parseColor("#BD1F3D"))
            mBinding.updateAvatarTv.text = "上传失败了"
            mBinding.updateAvatarTv.visibility = View.VISIBLE
            mBinding.updateAvatarTv.setTextColor(Color.parseColor("#BD1F3D"))
        }
        checkBtnStatus()
//    else if (obj.msg != null && !TextUtils.isEmpty(obj.msg)) {
//      niceToast("头像审核失败，请上传清晰正面个人照", Toast.LENGTH_LONG)
//    }
    }

    @Subscribe
    fun onRegisterUserInfo(obj: RegisterUserInfoViewModel.RegisterUserInfoVM) {
        closeProgressDialog()
        if (obj.info) {
            val userinfo = mDataRepository.getUserInfoEntity()
            userinfo?.gender = mBinding.sex!!
            userinfo?.nickname = mBinding.nicknameTv.text.toString()
            userinfo?.avatar = mViewModel.getAvatar()?.avatar
            userinfo?.avatarFilename = mViewModel.getAvatar()?.avatarFilename
            mDataRepository.saveUserInfoEntity(userInfo = userinfo)
//      mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(mViewModel.getAvatar()))
            TagsActivity.start(this@RegisterUserInfoActivity, TagsActivity.FROM_REGISTER)
            finish()
        }
//    else {
//      niceToast(obj.msg!!)
//    }
    }

    @Subscribe
    fun onCheckNickNameVM(obj: RegisterUserInfoViewModel.CheckNickNameVM) {
        if (obj.msg == null) {

        } else {
            Toast.makeText(ChatApp.getInstance().applicationContext, obj.msg, Toast.LENGTH_SHORT).show()
        }
    }
}