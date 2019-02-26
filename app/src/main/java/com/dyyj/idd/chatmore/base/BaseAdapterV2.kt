package com.dyyj.idd.chatmore.base

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.dyyj.idd.chatmore.app.ChatApp
import java.lang.ref.WeakReference

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/04/27
 * desc   : V2版本
 */
 abstract class BaseAdapterV2<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


  /**
   * 数据集
   */
  private var mList:ArrayList<T> = arrayListOf()

  /**
   * 滑动事件
   */
  val mScrollListener by lazy { ScrollListener() }

    var mRealScrollListener: RecyclerView.OnScrollListener? = null

  /**
   * 数据
   */
  val mDataRepository by lazy { ChatApp.getInstance().getDataRepository() }

  /**
   * 获取列表
   */
  fun getList() = mList


  /**
   * 初始化数据
   */
  fun initData(list: List<T>) {
    mList.clear()
    mList.addAll(list)
  }

    fun refreshData(list: List<T>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

  open fun moreData(list: List<T>) {
        val size = mList.size
        mList.addAll(list)
        notifyItemRangeInserted(size,list.count())
//        notifyDataSetChanged()
    }

//  /**
//   * 增加头部数据
//   */
//  fun addFirstList(list: List<T>) {
//    mList.addAll(0, list)
//  }

  /**
   * 软引用保存ImageView
   */
  fun getImageView(imageView: ImageView): ImageView?{
    val imageViewWeakReference = WeakReference(imageView)
    return imageViewWeakReference.get()
  }


  /**
   * 滑动事件
   */
  inner class ScrollListener : RecyclerView.OnScrollListener(){
      override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
          super.onScrolled(recyclerView, dx, dy)
          mRealScrollListener?.onScrolled(recyclerView, dx, dy)
      }

      override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        mRealScrollListener?.onScrollStateChanged(recyclerView, newState)
      //Glide监听RecyclerView滑动事件
      recyclerView?.context?.let {
        when (newState) {
          RecyclerView.SCROLL_STATE_IDLE -> {//滑动停止
            try {
              getGlide(it).resumeRequests()
            } catch (e: Exception) {
              e.printStackTrace()
            }

          }
          RecyclerView.SCROLL_STATE_DRAGGING->{//正在滚动
            try {
              getGlide(it).pauseRequests()
            } catch (e: Exception) {
              e.printStackTrace()
            }

          }
        }
      }
      super.onScrollStateChanged(recyclerView, newState)
    }

    /**
     * 获取Glide
     */
    fun getGlide(context: Context): RequestManager {
      return when (context) {
        is AppCompatActivity -> Glide.with(context)
        is Fragment -> Glide.with(context as Fragment)
        else -> Glide.with(context)
      }
    }
  }

  override fun getItemCount(): Int {
    return mList.size
  }
}