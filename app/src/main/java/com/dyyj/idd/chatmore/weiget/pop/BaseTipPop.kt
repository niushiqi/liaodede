package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.utils.DeviceUtils
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

abstract class BaseTipPop<D : ViewDataBinding, out VM : ViewModel> : PopupWindow {
    val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }
    lateinit var mBinding: D
    val mViewModel: VM by lazy { onViewModel() }
    var context: Context? = null

    constructor(context: Context?) : super(context) {
        this.context = context
        initContentView()
    }

    var downY: Float = 0f
    var scrollY = 0f
    private fun initContentView() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), onLayoutId(), null, false)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mBinding.root.layoutParams = params
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(R.color.black_80)!!))
        onLayoutSet()
        setContentView(mBinding.root)
        animationStyle = R.style.pop_anim_style

        scrollY = DeviceUtils.dp2px(context?.resources, 10f)
        setTouchInterceptor { v, event ->
            val yM = event.y
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    downY = yM
                }
                event.action == MotionEvent.ACTION_MOVE -> {
                    Log.e("pop", (yM - downY).toString())
                    if ((yM - downY < 0) and (Math.abs(yM - downY).toInt() > scrollY.toInt())) {
                        dismiss()
                    }
                }
            }
            return@setTouchInterceptor false
        }
        onInitView()
        onInitListener()
        onInitData()
    }

    @LayoutRes
    abstract fun onLayoutId(): Int

    abstract fun onViewModel(): VM

    /**
     * 兼容5.0以下系统，需要根据各自的布局文件，设置setHeight 和 setWidth
     */

    abstract fun onLayoutSet(): Unit

    /**
     * 初始化数据
     */
    open fun onInitData() {

    }

    /**
     * 初始化点击事件
     */
    open fun onInitListener() {

    }

    /**
     * 初始化view
     */
    open fun onInitView() {

    }

    fun startTime() {
        var count: Long = 5
        val subscribe = Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take(count + 1) //设置循环11次
                .map { count - it }.doOnSubscribe {}

                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe({}, {}, {
                    dismiss()
                })
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    fun onCreateEvenbus(any: Any) {
        if (!EventBus.getDefault().isRegistered(any)) {
            EventBus.getDefault().register(any)
        }
    }

    fun onDestryEvenbus(any: Any) {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(any)
        }
    }

    override fun dismiss() {
        mViewModel.destroy()
        super.dismiss()
    }

    /**
     * 设置字体
     */
    fun setTextFont(textView: TextView) {
        val type = Typeface.createFromAsset(context?.assets, "fonts/font1.ttf")
        textView.typeface = type
    }
}