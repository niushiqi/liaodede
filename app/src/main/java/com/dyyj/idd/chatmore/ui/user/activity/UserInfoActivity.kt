package com.dyyj.idd.chatmore.ui.user.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityUserInfoBinding
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.viewmodel.IdentityViewModel
import com.dyyj.idd.chatmore.viewmodel.TagsViewModel
import com.dyyj.idd.chatmore.viewmodel.UserInfoViewModel
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceGlide
import com.gt.common.gtchat.extension.niceToast
import com.zhihu.matisse.Matisse
import jp.wasabeef.glide.transformations.CropCircleTransformation
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
 * time   : 2018/05/21
 * desc   : 资料
 */
@RuntimePermissions
class UserInfoActivity : BaseActivity<ActivityUserInfoBinding, UserInfoViewModel>() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_user_info
    }

    override fun onViewModel(): UserInfoViewModel {
        return UserInfoViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    fun hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v = this.window.decorView;
            v.systemUiVisibility = View.GONE;
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView;
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.systemUiVisibility = uiOptions;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideBottomUIMenu()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "资料"
//        mBinding.schoolRightTv.setOnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus and !TextUtils.isEmpty(mBinding.schoolRightTv.text)) {
//                mViewModel.saveSchool(mBinding.schoolRightTv.text.toString())
//            }
//        }
        initListener()
        mViewModel.initData(this@UserInfoActivity)
        mViewModel.netGetTags()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.itemBirthdayCl?.setOnClickListener {
                        TPicker.show(true)
//            showSelectDate()
        }
        mBinding.itemAddressCl?.setOnClickListener {
            LPicker.setPicker(mViewModel.options1Items as List<Any>?, mViewModel.options2Items as List<MutableList<Any>>?, mViewModel.options3Items as List<MutableList<MutableList<Any>>>?)//三级选择器
            LPicker.show(true)
        }
        mBinding.itemProfessionCl?.setOnClickListener {
            PPicker.setPicker(mViewModel.optionsItems as List<Any>?)
            PPicker.show(true)
        }
//        mBinding.itemSchoolCl.setOnClickListener {
//            SPicker.setPicker(mViewModel.optionsSchool1Items as List<Any>?, mViewModel.optionsSchool2Items as List<MutableList<Any>>?)
//            SPicker.show(true)
//        }
        mBinding.schoolRightTv.clearFocus()
        mBinding.avatarIv?.setOnClickListener {
            //更换头像
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openCameraWithPermissionCheck()
            } else {
                openCamera()
            }
        }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.setOnClickListener {
            //完成按钮点击
            if (checkState()) {
                showProgressDialog()
                mViewModel.updateUserInfoApi(mBinding.editNicknameTv.text.toString(), mViewModel.getUserInfoEntity()?.gender
                        ?: 1, mBinding.birthdayRightTv.text.toString(), mViewModel.mAvatar?.avatarFilename
                        ?: mViewModel.userInfo?.avatar?.replace("http://api.ddaylove.com/avatar/", "")?:"",
                        mViewModel.areaId?:"0", mViewModel.mProfessionItem?.professionId ?: mViewModel.professionId ?: "0")
                if (!mBinding.schoolRightTv.text.equals(mViewModel.getUserInfoEntity()?.school)) {
                    mViewModel.saveSchool(mBinding.schoolRightTv.text.toString())
                }
            }
        }
        mBinding.itemNicknameEdit?.setOnClickListener {
            //修改昵称
            showEditNickname()
        }
        mBinding.itemSchoolCl.setOnClickListener { showEditSchool() }
        mBinding.itemApprove?.setOnClickListener {
            IdentityActivity.start(this@UserInfoActivity)
        }
        mBinding.itemTagCl?.setOnClickListener {
            TagsActivity.start(this@UserInfoActivity, TagsActivity.FROM_USERINFO, mViewModel.mSignsList)
        }
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
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    mBinding.birthdayRightTv.text = "$year-${month + 1}-$dayOfMonth"
                    val age = calculateAge("$year-${month + 1}-$dayOfMonth")
                    mBinding.ageRightTv.text = age.toString()
                },
                year, month, day).show()
    }

    /**
     * 检测是否可以上传
     */
    private fun checkState(): Boolean {
        val changeNickname: Boolean = !mBinding.accountRightTv.text.toString().equals(mViewModel.getUserInfoEntity()?.nickname)
        val changeAvatar: Boolean = mViewModel.mAvatar != null
        val changeBirthday: Boolean = !mBinding.birthdayRightTv.text.toString().equals(mViewModel.getUserInfoEntity()?.birthday)
        val changeProfession: Boolean = !mBinding.professionRightTv.text.toString().equals(mViewModel.getUserInfoEntity()?.professionName)
        val changeAddress: Boolean = !mBinding.addressRightTv.text.toString().equals(mViewModel.getUserInfoEntity()?.address)
        val changeSchool : Boolean = !mBinding.schoolRightTv.text.toString().equals(mViewModel.getUserInfoEntity()?.school)
        return (changeNickname or changeAvatar or changeBirthday or changeProfession or changeAddress or changeSchool)
    }

    val TPicker: TimePickerView by lazy {

        val calendarStart = Calendar.getInstance()
        calendarStart.set(1905,0,1)
        val calendarEnd: Calendar = Calendar.getInstance()
        calendarEnd.set(2015,0,1)
        var calendarSelect = Calendar.getInstance()
        calendarSelect.set(1995, 0, 1)
        if (mDataRepository.getUserInfoEntity()?.birthday?.isNotBlank() ?: false) {
            val sqls = mDataRepository.getUserInfoEntity()?.birthday?.split("-")
            if (sqls?.size == 3) {
                calendarSelect = Calendar.getInstance()
                calendarSelect.set(sqls[0].toInt(), sqls[1].toInt()-1, sqls.get(2).toInt())
            }
        }

        TimePickerBuilder(this@UserInfoActivity, OnTimeSelectListener { date, v ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd").format(date)
            mBinding.birthdayRightTv.text = dateStr

            val age = calculateAge(dateStr)
            mBinding.ageRightTv.text = age.toString()

        })

                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setDecorView(findViewById(R.id.rootview))
                .setDate(calendarSelect)
                .setRangDate(calendarStart, calendarEnd)
                .setLayoutRes(R.layout.layout_custom_time_picker, {
                    it.findViewById<TextView>(R.id.picker_title).text = "选择生日"
                    it.findViewById<TextView>(R.id.picker_cancel).setOnClickListener { TPicker.dismiss() }
                    it.findViewById<TextView>(R.id.picker_ok).setOnClickListener {
                        TPicker.returnData()
                        TPicker.dismiss()
                    }
                })
                .build()
    }

    /**
     * 计算年龄
     */
    private fun calculateAge(dateStr: String?): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val sDate = dateStr?.split("-")
        val sYear = sDate!!.get(0).toInt()
        val sMonth = sDate!!.get(1).toInt()
        val sDay = sDate!!.get(2).toInt()

        var age = year - sYear
        if (age <= 0)
            return 0
        else {
            if (month < sMonth) {
                return age - 1
            } else if ((month == sMonth) and (day < sDay)) {
                return age - 1
            } else {
                return age
            }
        }
    }

    val LPicker: OptionsPickerView<Any> by lazy {
        OptionsPickerBuilder(this@UserInfoActivity, OnOptionsSelectListener { options1, options2, options3, v ->
            //返回的分别是三个级别的选中位置
            val sb: StringBuilder = StringBuilder()
            sb.append(mViewModel.options1Items[options1].pickerViewText).append("-")
            sb.append(mViewModel.options2Items[options1][options2].pickerViewText).append("-")
            sb.append(mViewModel.options3Items[options1][options2][options3].pickerViewText)
            mBinding.addressRightTv.text = sb.toString()
            if (mViewModel.options3Items[options1][options2][options3] != null) {
                mViewModel.areaId = mViewModel.options3Items[options1][options2][options3].codeId;
            } else if (mViewModel.options2Items[options1][options2] != null) {
                mViewModel.areaId = mViewModel.options2Items[options1][options2].codeId;
            } else if (mViewModel.options1Items[options1] != null) {
                mViewModel.areaId = mViewModel.options1Items[options1].codeId
            } else {
                mViewModel.areaId = ""
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setDecorView(findViewById(R.id.rootview))
                .setLayoutRes(R.layout.layout_custom_option_picker, {
                    it.findViewById<TextView>(R.id.picker_title).text = "选择地址"
                    it.findViewById<TextView>(R.id.picker_cancel).setOnClickListener { LPicker.dismiss() }
                    it.findViewById<TextView>(R.id.picker_ok).setOnClickListener {
                        LPicker.returnData()
                        LPicker.dismiss()
                    }
                })
                .build<Any>()
    }
//    val SPicker: OptionsPickerView<Any> by lazy {
//        OptionsPickerBuilder(this@UserInfoActivity, OnOptionsSelectListener { options1, options2, options3, v ->
//            val sb = StringBuilder()
//            sb.append(mViewModel.optionsSchool1Items[options1].school).append("-")
//            sb.append(mViewModel.optionsSchool2Items[options1][options2].school)
//            mBinding.schoolRightTv.text = sb.toString()
//        })
//                .setTitleText("学校选择")
//                .setDividerColor(Color.BLACK)
//                .setTextColorCenter(Color.BLACK)
//                .setContentTextSize(20)
//                .setLayoutRes(R.layout.layout_custom_option_picker, {
//                    it.findViewById<TextView>(R.id.picker_title).text = "学校选择"
//                    it.findViewById<TextView>(R.id.picker_cancel).setOnClickListener { SPicker.dismiss() }
//                    it.findViewById<TextView>(R.id.picker_ok).setOnClickListener {
//                        SPicker.returnData()
//                        SPicker.dismiss()
//                    }
//                })
//                .build<Any>()
//    }
    val PPicker: OptionsPickerView<Any> by lazy {
        OptionsPickerBuilder(this@UserInfoActivity, OnOptionsSelectListener { options1, options2, options3, v ->
            //            mViewModel.mProfessionItem = mViewModel.mProfession?.professionList?.get(options1)
//            mBinding.professionRightTv.text = mViewModel.mProfessionItem?.professionName
            mBinding.professionRightTv.text = mViewModel.optionsItems.get(options1)
            mViewModel.mProfessionItem = mViewModel.mProfession?.professionList?.get(options1)
        })
                .setTitleText("职业")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setDecorView(findViewById(R.id.rootview))
                .setLayoutRes(R.layout.layout_custom_option_picker, {
                    it.findViewById<TextView>(R.id.picker_title).text = "选择职业"
                    it.findViewById<TextView>(R.id.picker_cancel).setOnClickListener { PPicker.dismiss() }
                    it.findViewById<TextView>(R.id.picker_ok).setOnClickListener {
                        PPicker.returnData()
                        PPicker.dismiss()
                    }
                })
                .build<Any>()
    }

    /**
     * 输入昵称
     */
    private fun showEditNickname() {
        MaterialDialog.Builder(this).title("提示").content("请输入你的昵称").inputType(
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS).inputRange(
                2, 16).positiveText("确定").input("", "", false, { dialog, input ->
            mBinding.editNicknameTv.text = input.toString().trim()
        }).show()
    }

    /**
     * 输入学校
     */
    private fun showEditSchool() {
        MaterialDialog.Builder(this).title("提示").content("请输入你的学校").inputType(
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS).inputRange(
                2, 50).positiveText("确定").input("", "", false, { dialog, input ->
            mBinding.schoolRightTv.setText(input.toString().trim())
        }).show()
    }

    /**
     * 打开拍照
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    fun openCamera() {
        mViewModel.openPhoto(this@UserInfoActivity, 1)

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
        if (requestCode == RegisterUserInfoActivity.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            val list = Matisse.obtainPathResult(data)
            showProgressDialog()
            mViewModel.netUploadAvatar(list)
        }
    }

    @Subscribe
    open fun onAvatarVM(obj: UserInfoViewModel.AvatarVM) {
        closeProgressDialog()
        if (obj.avatar && obj.obj != null) {
            mBinding.avatar = true
            niceGlide().load(obj.obj.avatar).asBitmap().transform(
                    CropCircleTransformation(this)).crossFade().error(R.drawable.bg_circle_black).placeholder(
                    R.drawable.bg_circle_black).into(mBinding.avatarIv)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(this).load(obj.obj.avatar).apply(requestOption).into(mBinding.avatarIv)
            niceToast("头像上传成功")
        }
//        else if ()
    }

    @Subscribe
    open fun onUserInfoVM(obj: UserInfoViewModel.UserInfoVM) {
        closeProgressDialog()
        if (obj.obj != null) {
            niceGlide().load(obj.obj.userBaseInfo.avatar).asBitmap().transform(
                    CropCircleTransformation(this)).crossFade().error(R.drawable.bg_circle_black).placeholder(
                    R.drawable.bg_circle_black).into(mBinding.avatarIv)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(this).load(obj.obj.userBaseInfo.avatar).apply(requestOption).into(mBinding.avatarIv)
            mBinding.levelTv.text = "LV " + obj.obj.userBaseInfo.userLevel
            mBinding.nicknameTv.text = obj.obj.userBaseInfo.nickname
            if (obj.obj.userBaseInfo.gender.equals("1")) mBinding.genderIv.setImageResource(R.drawable.ic_gender_main_normal) else mBinding.genderIv.setImageResource(R.drawable.ic_gender_woman_normal)
            mBinding.ageTv.text = obj.obj.userBaseInfo.age.toString()
            mBinding.editNicknameTv.text = obj.obj.userBaseInfo.nickname
            mBinding.accountRightTv.text = obj.obj.userBaseInfo.userId
            mBinding.ageRightTv.text = obj.obj.userBaseInfo.age.toString()
            mBinding.birthdayRightTv.text = obj.obj.userBaseInfo.birthday
            mBinding.professionRightTv.text = obj.obj.userExtraInfo.professionName
            mBinding.addressRightTv.text = obj.obj.userExtraInfo.area
            mBinding.approveTv1.text = if (obj.obj.realNameInfo.auth.equals("0")) "未认证" else "已认证"
            mBinding.presonApproveIv.visibility = if (obj.obj.realNameInfo.auth.equals("0")) View.GONE else View.VISIBLE
            mBinding.approveTv2.text = obj.obj.realNameInfo.name
            mBinding.schoolRightTv.setText(obj.obj.userExtraInfo.school)
            mBinding.schoolRightTv.clearFocus()
        }
    }

    @Subscribe
    fun onIdentityVM(obj: IdentityViewModel.IdetityVM) {
        closeProgressDialog()
        if (obj.verify) {
        } else {
        }
    }

    @Subscribe
    fun onEditUserVM(obj: UserInfoViewModel.EditUserVM) {
        closeProgressDialog()
        if (obj.editOk) {
            mBinding.nicknameTv.text = mBinding.editNicknameTv.text
            if (mViewModel.mAvatar != null) {
                mDataRepository.saveUserInfoEntity(UserInfoEntity.createUserInfoEntity(mViewModel.mAvatar))
            }
            niceToast("修改成功")
        } else {
            niceToast(obj.message!!)
        }
    }

    @Subscribe
    fun onSaveSchoolVM(obj: UserInfoViewModel.SaveSchoolVM) {
        if (!obj.success) {
            mBinding.schoolRightTv.setText("")
        } else {
            mViewModel.getUserInfoEntity()?.school = mBinding.schoolRightTv.text.toString()
        }
    }

    @Subscribe
    fun onSubscribe(obj: UserInfoViewModel.MyTags) {
        if (obj.success) {
            mViewModel.mSignsList.clear()
            mBinding.tagRightLl.removeAllViews()
            obj.obj?.tags?.forEachIndexed { index, sign ->
                mViewModel.mSignsList.add(sign.tagName!!)
                if (index < 3) {
                    val itemView = LayoutInflater.from(niceChatContext()).inflate(R.layout.item_tag, null)
                    val textview = itemView.findViewById<TextView>(R.id.txt_sign)
                    textview.setSingleLine(true)
                    textview.text = sign.tagName
                    mBinding.tagRightLl.addView(itemView)
                }
            }
        } else {

        }
    }

    @Subscribe
    fun OnSubscribe(obj: TagsViewModel.SaveTagsVM) {
        if (obj.success) {
            mViewModel.netGetTags()
        }
    }

    @Subscribe
    fun OnAvatarFailer(obj: UserInfoViewModel.AvatarFailerVM) {
        niceToast("头像审核失败，请上传清晰正面个人照", Toast.LENGTH_LONG)
    }
}