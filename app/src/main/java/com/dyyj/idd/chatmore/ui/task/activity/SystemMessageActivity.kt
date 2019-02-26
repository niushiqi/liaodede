package com.dyyj.idd.chatmore.ui.task.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivitySystemMessageBinding
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.viewmodel.SystemMessageViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.hyphenate.chat.EMMessage
import org.greenrobot.eventbus.Subscribe

/**
 * Created by wangbin on 2018/12/9.
 */
class SystemMessageActivity : BaseActivity<ActivitySystemMessageBinding, SystemMessageViewModel>() {

    companion object {
        const val TYPE = "type"
        const val MSG = "msg"
        const val URL_TYPE = "url_type"

        fun start(context: Context, type: String?, msg: String?, url_type: String?) {
            val intent = Intent(context, SystemMessageActivity::class.java)
            intent.putExtra(TYPE, type)
            intent.putExtra(MSG, msg)
            intent.putExtra(URL_TYPE, url_type)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_system_message
    }

    override fun onViewModel(): SystemMessageViewModel {
        return SystemMessageViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEvenbus(this)
        initToobar()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEvenbus(this)
    }

    private fun initToobar() {
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.let {
            it.text = "系统消息"
        }
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initData() {
        mViewModel.loadSystemMessageList()
        mViewModel.clearUnreadMessage("2")
    }

    fun initRecycleView(list: List<EMMessage>) {
        val layoutManager = LinearLayoutManager(this)
        mViewModel.getAdapter().setLayoutManager(layoutManager)
        mViewModel.getAdapter().initChatList(list)
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = layoutManager
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().scrollToBotton()
    }

    @Subscribe
    fun onSystemMessageVM(obj: SystemMessageViewModel.SystemMessageVM) {
        obj.list?.let {
            initRecycleView(it)
        }
    }

    private var dialog2: Dialog? = null

    fun showLargeImageDialog(imageUrl: String) {
        val screenWidth: Int = DisplayUtils.getScreenWidthPixels(this@SystemMessageActivity)
        val screenHeight: Int = DisplayUtils.getScreenHeightPixels(this@SystemMessageActivity)
        if (dialog2 == null) {
            dialog2 = Dialog(this@SystemMessageActivity)
            dialog2?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog2?.setContentView(R.layout.dialog_large_photo)
            dialog2?.getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
            dialog2?.window!!.setBackgroundDrawable(
                    ColorDrawable(resources.getColor(android.R.color.black)))
        }
        var image: PhotoView? = dialog2?.findViewById<PhotoView>(R.id.large_image)
        image!!.setImageDrawable(null)
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(object: SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(resource: GlideDrawable , glideAnimation: GlideAnimation<in GlideDrawable>) {
                        //动态设置图片大小
                        var params: ViewGroup.LayoutParams = image!!.getLayoutParams();
                        if((resource.intrinsicWidth / resource.intrinsicHeight) > (screenWidth / screenHeight)) {
                            params.height = screenHeight
                            params.width = screenHeight * resource.intrinsicWidth / resource.intrinsicHeight
                        } else {
                            params.width = screenWidth
                            params.height = screenWidth * resource.intrinsicHeight / resource.intrinsicWidth
                        }
                        image!!.setLayoutParams(params)
                        image.setImageDrawable(resource)
                    }
                })
        dialog2?.show()
        image?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(paramView: View) {
                dialog2?.cancel()
            }
        })
    }

    /**
     * 点击显示大图
     */
    @Subscribe
    fun showUserInfoDetail(obj: SystemMessageViewModel.ShowLargeImageVM) {
        showLargeImageDialog(obj.imageUrl)
    }

}