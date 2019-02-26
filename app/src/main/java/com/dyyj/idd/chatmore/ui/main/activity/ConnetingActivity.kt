package com.dyyj.idd.chatmore.ui.main.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityConnetingBinding
import com.dyyj.idd.chatmore.ui.main.fragment.ConnetingFragment
import com.dyyj.idd.chatmore.utils.StatusBarUtilV2
import com.dyyj.idd.chatmore.viewmodel.ConnetingActViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/07
 * desc   : 呼叫中(语音/视频)
 */
class ConnetingActivity : BaseActivity<ActivityConnetingBinding, ConnetingActViewModel>() {
  private var mContent: Fragment? = null

  companion object {
    const val TYPE_FROM_USERID = "from_userid"
    const val TYPE_TO_USERID = "to_userid"
    const val TYPE = "type"
    const val TYPE_CALL = "is_calling"
    const val FROM_TYPE = "from_type"
    fun start(context: Context, fromUserid: String, toUserid: String, type: String, is_calling: String, fromType: String) {
      val intent = Intent(context, ConnetingActivity::class.java)
      intent.putExtra(TYPE_FROM_USERID, fromUserid)
      intent.putExtra(TYPE_TO_USERID, toUserid)
      intent.putExtra(TYPE, type)
      intent.putExtra(TYPE_CALL, is_calling)
      intent.putExtra(FROM_TYPE, fromType)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_conneting
  }

  override fun onViewModel(): ConnetingActViewModel {
    return ConnetingActViewModel()
  }


  private fun getFromUserid() = intent.getStringExtra(TYPE_FROM_USERID)

  private fun getToUserid() = intent.getStringExtra(TYPE_TO_USERID)

  private fun getType() = intent.getStringExtra(TYPE)

  fun getTypeCall() = intent.getStringExtra(TYPE_CALL)

  fun getFromType() = intent.getStringExtra(FROM_TYPE)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //状态栏透明和间距处理
    StatusBarUtilV2.immersive(this)
    addFragment(ConnetingFragment.instance().setFromUserid(getFromUserid()).setToUserid(
        getToUserid()).setType(getType()).setTypeCall(getTypeCall()).setFromType(getFromType()))
  }


  /**
   * 替换Fragment
   */
  private fun addFragment(fragment: Fragment) {
    if (mContent != fragment) {
      mContent?.onDestroy()
      mContent = fragment
      val transaction = supportFragmentManager.beginTransaction()
      transaction.replace(R.id.fragmentContainer, fragment, fragment::class.java.simpleName)
      try {
        transaction.commitAllowingStateLoss()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  override fun onBackPressed() {

//    super.onBackPressed()
  }

  @Subscribe
  open fun onCloseConnetingActivityVM(obj: ConnetingActViewModel.CloseConnetingActivityVM) {
    this@ConnetingActivity.finish()
  }
}