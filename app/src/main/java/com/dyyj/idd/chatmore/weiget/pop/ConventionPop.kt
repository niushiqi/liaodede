
import android.content.Context

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/09/15
 * desc   : 公约
 */
class ConventionPop(context: Context?) /*: BaseTipPop<PopConventionBinding, ConventionViewModel>(
    context) */{//被谁干掉了？
  /*override fun onLayoutId(): Int {
    return R.layout.pop_convention
  }

  override fun onViewModel(): ConventionViewModel {
    return ConventionViewModel()
  }

  override fun onLayoutSet() {
    height = ViewGroup.LayoutParams.WRAP_CONTENT
    width = ViewGroup.LayoutParams.WRAP_CONTENT
  }

  override fun onInitListener() {
    super.onInitListener()
    mBinding.btnCancel.setOnClickListener { EventBus.getDefault().post(this@ConventionPop) }
  }

  override fun onInitView() {
    super.onInitView()
    setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.color.black_80))
    // 设置点击窗口外边窗口消失
    isOutsideTouchable = true
    // 设置此参数获得焦点，否则无法点击
    isFocusable = true

  }

  *//**
   * 显示
   *//*
  fun show(view: View) {
    showAtLocation(view, Gravity.CENTER, 0, 0)
    Flowable.interval(5, TimeUnit.SECONDS).subscribe({}, {}, { dismiss() })
  }*/

}