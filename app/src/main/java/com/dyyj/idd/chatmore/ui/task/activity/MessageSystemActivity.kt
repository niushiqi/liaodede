package com.dyyj.idd.chatmore.ui.task.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMessageSystemBinding
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.utils.DataBindingUtils
import com.dyyj.idd.chatmore.viewmodel.MessageSystemViewModel
import com.google.android.flexbox.FlexboxLayout
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMMessage
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 消息任务
 */
class MessageSystemActivity : BaseActivity<ActivityMessageSystemBinding, MessageSystemViewModel>(), EMMessageListener {


  companion object {
    fun start(context: Context, index: Int? = 0) {
      val intent = Intent(context, MessageSystemActivity::class.java)
      intent.putExtra("index", index)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_message_system
  }

  override fun onViewModel(): MessageSystemViewModel {
    return MessageSystemViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateEvenbus(this)
    initView()
    initListener()
    mViewModel.loadUnreadMessage()
//    EMClient.getInstance().chatManager().loadAllConversations()
  }

  private fun initView() {
    mBinding.vp.offscreenPageLimit = 3
    mBinding.vp.adapter = mViewModel.getAdapter(supportFragmentManager)
    mBinding.tl4.setViewPager(mBinding.vp)
    mBinding.tl4.currentTab = intent.getIntExtra("index", 0)
  }

  private fun initListener() {
    mBinding.layoutToolbar?.findViewById<View>(
        R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "消息系统"
  }

  override fun onDestroy() {
    super.onDestroy()
    onDestryEvenbus(this)
  }

  @Subscribe
  fun onUnreadMessageVM(obj: MessageSystemViewModel.UnreadMessageVM) {
    if (obj.isSuccess) {
      obj.list?.let {
        if (it.isNotEmpty()) {
          mBinding.tl4.showMsg(0, it.size)
          mBinding.tl4.setMsgMargin(0, 45f, 8f)
          mViewModel.getAdapter(supportFragmentManager).getItem(0).onStart()
        } else {
          mBinding.tl4.hideMsg(0)
        }
      }
    }
  }

  override fun onMessageRecalled(p0: MutableList<EMMessage>?) {
  }

  override fun onMessageChanged(p0: EMMessage?, p1: Any?) {
  }

  override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {
  }

  override fun onMessageReceived(p0: MutableList<EMMessage>?) {
    mViewModel.loadUnreadMessage()
  }

  override fun onMessageDelivered(p0: MutableList<EMMessage>?) {
  }

  override fun onMessageRead(p0: MutableList<EMMessage>?) {
  }

  @Subscribe
  fun OnSubscribeVM(obj: MessageSystemViewModel.ShowUserInfoVM) {
    /**
     * 获取个人信息
     */
    val subscribe1 = mDataRepository.getUserDetailInfo(obj.userId).subscribe({
                                                                               showUserInfoDialog(
                                                                                   it.data!!)
                                                                             }, {})
    mViewModel.mCompositeDisposable.add(subscribe1)
  }

  private var dialog: Dialog? = null

  fun showUserInfoDialog(obj: UserDetailInfoResult.Data) {
    if (dialog == null) {
      dialog = Dialog(this@MessageSystemActivity)
      dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog?.setContentView(R.layout.dialog_userinfo)
      dialog?.getWindow()!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                                      WindowManager.LayoutParams.WRAP_CONTENT)
      dialog?.window!!.setBackgroundDrawable(
          ColorDrawable(resources.getColor(android.R.color.transparent)))
    }
    dialog?.findViewById<TextView>(R.id.textView14)?.text = obj.userBaseInfo.nickname
    dialog?.findViewById<ImageView>(R.id.imageView24)?.setImageResource(
        if (obj.userBaseInfo.gender == "1") R.drawable.ic_gender_main_normal else R.drawable.ic_gender_woman_normal)
    dialog?.findViewById<TextView>(R.id.level_tv)?.text = obj.userBaseInfo.userLevel
    DataBindingUtils.loadAvatar(dialog?.findViewById<ImageView>(R.id.imageView23),
                                obj.userBaseInfo.avatar)
    val sb = StringBuilder()
    sb.append(obj.userBaseInfo.age.toString())
    if (!TextUtils.isEmpty(obj.userExtraInfo.professionName)) {
      sb.append(" | ").append(obj.userExtraInfo.professionName)
    }
    if (!TextUtils.isEmpty(obj.userExtraInfo.address)) {
      sb.append(" | ").append(obj.userExtraInfo.address)
    }
    dialog?.findViewById<TextView>(R.id.txt_user_info)?.text = sb.toString()
    val subscribe = mDataRepository.getSigns(obj.userBaseInfo.userId).subscribe({
      if (it.errorCode != 200) {
        niceToast(it.errorMsg!!)
        return@subscribe
      }
                                                                                  dialog?.findViewById<FlexboxLayout>(
                                                                                      R.id.afl_cotent)?.removeAllViews()
                                                                                  it.data?.tags?.forEach {
                                                                                    val itemView = LayoutInflater.from(
                                                                                        niceChatContext()).inflate(
                                                                                        R.layout.item_tags3,
                                                                                        null)
                                                                                    val textview = itemView.findViewById<TextView>(
                                                                                        R.id.txt_sign)
                                                                                    textview.setSingleLine(
                                                                                        true)
                                                                                    textview.text = it.tagName
                                                                                    val params = FlexboxLayout.LayoutParams(
                                                                                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                                                                        FlexboxLayout.LayoutParams.WRAP_CONTENT)
                                                                                    params.order = -1
                                                                                    params.flexGrow = 2F
                                                                                    dialog?.findViewById<FlexboxLayout>(
                                                                                        R.id.afl_cotent)?.addView(
                                                                                        itemView,
                                                                                        params)
                                                                                  }
                                                                                  if (!this@MessageSystemActivity.isDestroyed && !this@MessageSystemActivity.isFinishing) {
                                                                                    dialog?.show()
                                                                                  }
                                                                                })
    CompositeDisposable().add(subscribe)
//    dialog.show()

  }
}