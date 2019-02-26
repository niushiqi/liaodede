package com.gt.common.gtchat.extension

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.DataRepository
import com.dyyj.idd.chatmore.model.database.ConvertersFactory
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.MD5
import com.dyyj.idd.chatmore.utils.RequestBodyUtils
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.ishumei.smantifraud.SmAntiFraud
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/10
 * desc   : 扩展
 */

fun ViewModel.niceToast(message: String? = "", length: Int = Toast.LENGTH_SHORT) {
  if ((message != null) and !TextUtils.isEmpty(message)) {
    Toast.makeText(ChatApp.niceChatContext(), message, length).show()
  }
}

/**
 * Toast提示
 */
fun Context.niceToast(message: String, length: Int = Toast.LENGTH_SHORT) {
  Toast.makeText(this, message, length).show()
//  niceCustomTaost(message)
}

fun Context.niceCustomTaost(message:String) {
  val toast = Toast(this);
  //设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移70个单位，
  toast.setGravity(Gravity.CENTER_HORIZONTAL and Gravity.BOTTOM, 0, 70)
  //设置显示时间
  toast.duration = Toast.LENGTH_SHORT
//使用布局加载器，将编写的toast_layout布局加载进来
  val view = LayoutInflater.from(this).inflate(R.layout.toast_layout_view1, null)
  view.findViewById<TextView>(R.id.content_tv)?.text = message
  toast.view = view
  toast.show()

}


fun Context.niceCustomToast2(message: String) {
  val toast = Toast(this)
  //设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移70个单位，
  toast.setGravity(Gravity.CENTER_HORIZONTAL and Gravity.BOTTOM, 0, 20)
  //设置显示时间
  toast.duration = Toast.LENGTH_SHORT
//使用布局加载器，将编写的toast_layout布局加载进来
  val view = LayoutInflater.from(this).inflate(R.layout.toast_layout_view2, null)
  view.findViewById<TextView>(R.id.content_tv)?.text = message
  toast.view = view
  toast.show()
}

/**
 * Toast提示
 */
fun Context.niceToast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
  Toast.makeText(this, message, length).show()
}

/**
 * 获取Glide
 */
fun Context.niceGlide(): RequestManager {
  return when (this) {
    is AppCompatActivity -> Glide.with(this)
    is Fragment -> Glide.with(this as Fragment)
    else -> Glide.with(this)
  }
}

fun Fragment.niceGlide(): RequestManager {
  return Glide.with(this)
}

/**
 * 软引用
 */
fun ImageView.get(): ImageView? {
  val imageViewWeakReference = WeakReference(this)
  return imageViewWeakReference.get()
}

/**
 * 获取Glide
 */
fun ImageView.niceGlide(): RequestManager {
  return when (context) {
    is AppCompatActivity -> Glide.with(context as AppCompatActivity)
    is Fragment -> Glide.with(context as Fragment)
    else -> Glide.with(context)
  }
}

/**
 * html形式变色
 */
fun String.fontColor(color: String, bold: Boolean = false): String {
  val boldypeface = if (bold) "<b>$this</b>" else this
  return "<font color='$color'>$boldypeface</font>"
}

fun String.fontColorLine(color: String, bold: Boolean = false): String {
  val boldypeface = if (bold) "<b>$this</b>" else this
  return "<font color='$color'><u>$boldypeface</u></font>"
}

/**
 * 加载Html处理过时
 */
fun TextView.niceHtml(content: String) {
  val result: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
    Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
  } else {
    Html.fromHtml(content)
  }
  this.text = result
}

/**
 * 获取String资源
 */
fun Any.niceString(@StringRes id: Int): String {
  return ChatApp.getInstance().getString(id)
}

/**
 * 上下文
 */
fun Any.niceChatContext() = ChatApp.getInstance()

/**
 * 转成RequestBody
 */
fun DataRepository.niceRequestBody(params: Any): RequestBody {
  val timestamp = System.currentTimeMillis().toString().substring(0, 10)
  val deviceId = DeviceUtils.getDeviceID(niceChatContext())
  val obj = JSONObject(Gson().toJson(params))
  obj.put("timestamp", timestamp)
  obj.put("deviceId", deviceId)
  val sb = StringBuilder()
  for (key in obj.keys()) {
//    if (!TextUtils.isEmpty(obj[key].toString())) {
    sb.append(MD5.MD5(obj[key].toString()))
//    }
  }
  obj.put("sign", MD5.MD5(sb.toString()))
  return RequestBodyUtils.getRequestBody(obj.toString())
}

/**
 * 转成RequestBody
 */
fun DataRepository.niceQueryMap(params: Any): Map<String, String> {
  val timestamp = System.currentTimeMillis().toString().substring(0, 10)
  val deviceId = DeviceUtils.getDeviceID(niceChatContext())
  val map = hashMapOf<String, String>()
  val obj = JSONObject(Gson().toJson(params))
  obj.put("timestamp", timestamp)
  obj.put("deviceId", deviceId)
  val sb = StringBuilder()
  for (key in obj.keys()) {
    val mD5 = MD5.MD5(obj[key].toString())
    sb.append(mD5)
    map[key] = obj[key].toString()
  }
  val mD5 = MD5.MD5(sb.toString())
  obj.put("sign", mD5)
  map["sign"] = mD5
  return map
}

/**
 * 转成RequestBody
 * deviceId换成数美ID，仅仅用于埋点特殊需求
 */
fun DataRepository.niceRequestBodyForEventTracking(params: Any): RequestBody {
  val timestamp = System.currentTimeMillis().toString().substring(0, 10)
  val deviceId = SmAntiFraud.getDeviceId()
  val obj = JSONObject(Gson().toJson(params))
  obj.put("timestamp", timestamp)
  obj.put("deviceId", deviceId)
  val sb = StringBuilder()
  for (key in obj.keys()) {
//    if (!TextUtils.isEmpty(obj[key].toString())) {
    sb.append(MD5.MD5(obj[key].toString()))
//    }
  }
  obj.put("sign", MD5.MD5(sb.toString()))
  return RequestBodyUtils.getRequestBody(obj.toString())
}

fun EMMessage.getExtMap(key: String): String? {
  val attribute = this.getStringAttribute(com.dyyj.idd.chatmore.model.easemob.EasemobManager.EXT)
  val map = ConvertersFactory.stringToMap(attribute)
  return map[key]
}



/**
 * 设置字体
 */
fun TextView.niceTextFont() {
  val type = Typeface.createFromAsset(context.assets, "fonts/font1.ttf")
  typeface = type
}