package com.dyyj.idd.chatmore.ui.h5

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.Gravity
import android.view.View
import android.webkit.*
import android.widget.TextView
import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityH5Binding
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity2
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WalletActivity
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.H5ViewModel
import com.dyyj.idd.chatmore.weiget.pop.InviteFriendPop
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.hybrid.core.HybridConstant
import com.gt.common.hybrid.core.HybridJsInterface
import com.gt.common.hybrid.core.HybridWebChromeClient
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */
class H5Activity:BaseActivity<ActivityH5Binding, H5ViewModel>() {
  companion object {
    const val params = "url"
    const val TITLE = "title"
    const val HIDETITLE = "hidetitle"
    fun start(context: Context, url: String, title: String, hideTitle: Boolean? = false) {
      val intent = Intent(context, H5Activity::class.java)
      if (context== niceChatContext()){
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      intent.putExtra(HIDETITLE, hideTitle)
      intent.putExtra(params, url)
      intent.putExtra(TITLE, title);
      context.startActivity(intent)
    }
  }

  /**
   * 获取url
   */
  fun getURL(): String {
    return intent.getStringExtra(params)
  }

  fun getTopTitle(): String {
    return intent.getStringExtra(TITLE)
  }

  fun getHideTitle() : Boolean {
    return intent.getBooleanExtra(HIDETITLE, false)
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_h5
  }

  override fun onViewModel(): H5ViewModel {
    return H5ViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initConfig(mBinding.hybridWebview)
    initView()
  }

  /**
   * 初始化View
   */
  private fun initView() {
    findViewById<View>(R.id.error_network_refersh_btn).setOnClickListener {
      setStatusUI(Status.Loading)
      mBinding.hybridWebview.reload()
    }

    findViewById<View>(R.id.toolbar_back_iv).setOnClickListener {
      finish()
    }

    findViewById<TextView>(R.id.toolbar_title_tv).setText(getTopTitle(), null)
//    findViewById<TextView>(R.id.toolbar_title_tv).setOnClickListener { mBinding.hybridWebview.loadUrl("http://switch_android_show") }

    if (getHideTitle()) {
      mBinding.layoutBar?.visibility = View.GONE
    } else {
      mBinding.layoutBar?.visibility = View.VISIBLE
    }
  }

  /**
   * 需要设置webview的属性则重写此方法
   */
  private fun initConfig(webView: WebView) {
    var settings = webView.settings
    settings.textZoom = 100
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.allowUniversalAccessFromFileURLs = true
    settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
    settings.cacheMode = WebSettings.LOAD_NO_CACHE
    settings.setAppCacheEnabled(false)
    settings.databaseEnabled = false
    settings.userAgentString = settings.userAgentString + " hybrid_1.0.0 "
    webView.webViewClient = mWebViewClient(this)
    webView.webChromeClient = object : HybridWebChromeClient() {
      override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
        Timber.tag("niushiqi-webview").i(message + " --From line " + lineNumber + " of " + sourceID)
      }
    }
    settings.javaScriptCanOpenWindowsAutomatically = true
    settings.allowFileAccess = true
    webView.addJavascriptInterface(HybridJsInterface(webView), HybridConstant.JSINTERFACE)
    webView.loadUrl(getURL())
  }

  /**
   * 网络异常处理监听
   */
  class mWebViewClient(val activity: H5Activity) : WebViewClient() {
    private var loadError = false
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.toString()?.contains("switch_android_show")!!) {
            val subscribe = DataRepository(activity).getMyInvite().subscribe(
                    {
                        if (it.errorCode == 200) {
                            InviteFriendPop(activity).setCode(it.data?.inviteCode).showAtLocation(activity.mBinding.root, Gravity.CENTER, 0, 0)
                        }
                    },
                    { }
            )
            CompositeDisposable().add(subscribe)
            return true
        } else if (request?.toString()?.contains("switch_wallet_show")) {
          WalletActivity.start(activity)
          return true
        } else if (request?.toString()?.contains("switch_share_show")) {
//          activity.showProgressDialog()
//          activity.mViewModel.netShareMessage()
          InviteNewActivity2.start(activity)
          return true
        } else if (request?.toString()?.contains("switch_chat_show")) {
          val toUserId = request?.url.getQueryParameter("toUserId")
          val toName = request?.url.getQueryParameter("toName")
          val toAvatar = request?.url.getQueryParameter("toAvatar")
          ChatActivity.start(activity, toUserId, toName, toAvatar,null)
          return true
        } else if (request?.toString()?.contains("switch_back")) {
          activity.finish()
          return true
        }
      return super.shouldOverrideUrlLoading(view, request)
    }
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
      if (url?.contains("switch_android_show")!!) {
//        Toast.makeText(activity, "可以弹出弹窗", Toast.LENGTH_SHORT).show()
          val subscribe = DataRepository(activity).getMyInvite().subscribe(
                  {
                      if (it.errorCode == 200) {
                          InviteFriendPop(activity).setCode(it.data?.inviteCode).showAtLocation(activity.mBinding.root, Gravity.CENTER, 0, 0)
                      }
                  },
                  { }
          )
          CompositeDisposable().add(subscribe)
        return true
      } else if (url?.contains("switch_wallet_show")) {
        WalletActivity.start(activity)
        return true
      } else if (url?.contains("switch_share_show")) {
//        activity.showProgressDialog()
//        activity.mViewModel.netShareMessage()
        InviteNewActivity2.start(activity)
        return true
      } else if (url?.contains("switch_chat_show")) {
        val toUserId = Uri.parse(url).getQueryParameter("toUserId")
        val toName = Uri.parse(url).getQueryParameter("toName")
        val toAvatar = Uri.parse(url).getQueryParameter("toAvatar")
        ChatActivity.start(activity, toUserId, toName, toAvatar,null)
        return true
      } else if (url?.contains("switch_back")) {
        activity.finish()
        return true
      }
      return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
      loadError = false
      super.onPageStarted(view, url, favicon)
    }
    override fun onPageFinished(view: WebView?, url: String?) {
      super.onPageFinished(view, url)
      if (!loadError) {//当网页加载成功的时候判断是否加载成功
        activity.setStatusUI(Status.Normal)
      } else { //加载失败的话，初始化页面加载失败的图，然后替换正在加载的视图页面
        activity.setStatusUI(Status.Network)
      }
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?,
        failingUrl: String?) {
      super.onReceivedError(view, errorCode, description, failingUrl)
      loadError = true
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?,
        error: WebResourceError?) {
      super.onReceivedError(view, request, error)
      loadError = true
    }
  }

  override fun onBackPressed() {
    BGAKeyboardUtil.closeKeyboard(this)
    this.finish()
    BGASwipeBackHelper.executeBackwardAnim(this)
  }

  @Subscribe
  fun onShareMessageVM(obj: H5ViewModel.ShareMessageVM) {
    closeProgressDialog()
    if (obj.success) {
      ShareUtils.shareWebToWEIXIN(this, obj.inviteCode, obj.shareUrl, obj.title, obj.icon, false)
    }
  }
}