package com.dyyj.idd.chatmore.base

import android.support.v7.widget.RecyclerView
import com.dyyj.idd.chatmore.utils.DateFormatter
import com.hyphenate.chat.EMMessage
import java.util.*

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/01
 * desc   : 聊天
 */
abstract class BaseChatAdapterV2 : BaseAdapterV2<BaseChatAdapterV2.Wrapper>() {

  private var mLayoutManager: RecyclerView.LayoutManager? = null

  /**
   * 初始化聊天
   */
  fun initChatList(list: List<EMMessage>) {
    addChatTop(list, false)
  }

  /**
   * 增加聊天底部
   */
  fun addChatBottom(message: EMMessage, scroll: Boolean) {
    val isNewMessageToday = !isPreviousSameDate(getListLastIndex(), Date(message.msgTime))
    if (isNewMessageToday) {
      getList().add(getListLastIndex(), Wrapper(message.msgTime))
    }
    val element = Wrapper(message)
    getList().add(element)
    notifyItemRangeInserted(if (isNewMessageToday) getListLastIndex() - 1 else getListLastIndex(),
                            if (isNewMessageToday) 2 else 1)
    if (mLayoutManager != null && scroll) {
      mLayoutManager?.scrollToPosition(getListLastIndex())
    }

//    notifyDataSetChanged()
  }

  /**
   * 移动到底部
   */
  fun scrollToBotton() {
    if (mLayoutManager != null) {
      mLayoutManager?.scrollToPosition(getListLastIndex())
    }
  }

  private fun getListLastIndex() = if (getList().size == 0) 0 else getList().lastIndex

  /**
   * 增加聊天头部
   */
  fun addChatTop(messages: List<EMMessage>, reverse: Boolean) {
    if (reverse) Collections.reverse(messages)

    if (!getList().isEmpty()) {
      val lastItemPosition = getList().size - 1
      if (getList()[lastItemPosition].item is Date){
        val lastItem = getList()[lastItemPosition].item as Date
        if (DateFormatter.isSameDay(Date(messages[0].msgTime), lastItem)) {
          getList().removeAt(lastItemPosition)
          notifyItemRemoved(lastItemPosition)
        }
      }
    }

    val oldSize = getList().size
    generateDateHeaders(messages)
    notifyItemRangeInserted(oldSize, getList().size - oldSize)
  }

  fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
    this.mLayoutManager = layoutManager
  }

  /**
   * 是否与之前相同日期
   */
  private fun isPreviousSameDate(position: Int, dateToCompare: Date): Boolean {
    if (getList().size <= position) return false
    if (getList()[position].item is Date) {
      return true
    }
    return if (getList()[position].item is EMMessage) {
      val previousPositionDate = (getList()[position].item as EMMessage).msgTime
      DateFormatter.isSameDay(dateToCompare, Date(previousPositionDate))
    } else false
  }

  private fun generateDateHeaders(messages: List<EMMessage>) {
    for (i in messages.indices) {
      val message = messages[i]
      getList().add(Wrapper(message))
      if (messages.size > i + 1) {
        val nextMessage = messages[i + 1]
        if (!DateFormatter.isSameDay(Date(message.msgTime), Date(nextMessage.msgTime))) {
          getList().add(Wrapper(Date(message.msgTime)))
        }
      } else {
        getList().add(Wrapper(Date(message.msgTime)))
      }
    }
  }

  class Wrapper(var item: kotlin.Any)
}