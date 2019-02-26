package com.dyyj.idd.chatmore.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemMessageBinding
import com.dyyj.idd.chatmore.databinding.ItemMessageHeaderBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.listener.OnClickItemListener
import com.dyyj.idd.chatmore.model.database.entity.MessageEntity
import com.dyyj.idd.chatmore.ui.main.fragment.OpenCallFragment
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.ui.task.activity.SystemMessageActivity
import com.dyyj.idd.chatmore.utils.DateFormatter
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.MessageFragmentViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageSystemViewModel
import com.flyco.tablayout.utils.UnreadMsgUtils
import com.gt.common.gtchat.extension.getExtMap
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.exceptions.HyphenateException
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.EventBus

class MessageAdapter : BaseAdapterV2<MessageEntity>() {

  companion object {
    const val TYPE_HEADER = 0
    const val TYPE_CONTENT = 1
    const val TYPE_SYSTEM_MESSAGE = 2
    const val TYPE_SQUARE_CONTENT = 3
  }

  private var mOnClickItemListener: OnClickItemListener? = null

  var isHeader = false

  fun setListener(listener: OnClickItemListener) {
    mOnClickItemListener = listener
  }

  override fun getItemCount(): Int {
    return if (isHeader) getList().size + 1 else getList().size
  }

  /**
   * 区分消息的类型，跳转不同的信箱页／消息页
   * 系统消息（废弃）：未知
   * 系统消息：EMMessage的toUserId为2
   * 语音匹配消息：EMMessage的fromType为1
   * 文字匹配消息：EMMessage的fromType为2
   * 邀新好友消息：EMMessage的fromType为3
   * 广场消息：EMMessage的fromType为4
   *
   * editor by niushiqi 2019.1.5 12:18
   */
  override fun getItemViewType(position: Int): Int {
    var messageEntity: MessageEntity? = null
    if (getList().size > 0) {
      if (position >= getList().size) {
        messageEntity = getList().get(getList().size-1)
      } else {
        messageEntity = getList().get(position)
      }
    }
    //conversationEntityToEMMessage转换的EMMessage均为from自己ID
    //，to对方ID（不论接收或发送），此处只能通过to判断
    if (messageEntity != null) {
      val toUserId = messageEntity.message.to
      val fromType = messageEntity.message.getExtMap("fromType")
      if(toUserId == "2") {
        return TYPE_SYSTEM_MESSAGE
      }
      if(fromType == ChatActivity.FROM_TYPE_SQUARE.toString()) {
        return TYPE_SQUARE_CONTENT
      }
    }

    if (isHeader && position == 0)
      return TYPE_HEADER
    else
      return TYPE_CONTENT
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    if (viewType == TYPE_HEADER) {
      val holderHeader = ViewHolderHeader(parent)
      holderHeader.mOnClickItemListener = mOnClickItemListener
      return holderHeader
    } else if(viewType == TYPE_SYSTEM_MESSAGE) {
      return ViewHolderSystemMessage(parent)
    } else if(viewType == TYPE_SQUARE_CONTENT) {
      return ViewHolderSquareContent(parent)
    } else {
      return ViewHolder(parent)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is ViewHolder) {
      holder.onBindViewHolder(getList()[if (isHeader) position - 1 else position], position)
    } else if (holder is ViewHolderHeader) {
      if (getList() != null && getList().size > position) {
        holder.onBindViewHolder(getList()[position], position)
      }
    } else if (holder is ViewHolderSystemMessage) {
      if (getList() != null && getList().size > position) {
        holder.onBindViewHolder(getList()[position], position)
      }
    } else if (holder is ViewHolderSquareContent) {
      if (getList() != null && getList().size > position) {
        holder.onBindViewHolder(getList()[position], position)
      }
    }
  }

  /**
   * 接收到消息（环信） - 来自文字匹配／邀新／语音匹配
   */
  class ViewHolder(parent: ViewGroup?) : BaseViewHolder<MessageEntity, ItemMessageBinding>(parent,
                                                                                            R.layout.item_message) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: MessageEntity, position: Int) {
      super.onBindViewHolder(obj, position)
//            mBinding.model = obj

      val keyName: StringBuilder = StringBuilder()
      val keyAvatar: StringBuilder = StringBuilder()
      if (obj.message.direct().name == EMMessage.Direct.RECEIVE.name) {
        keyName.append("from_")
        keyAvatar.append("from_")
      } else {
        keyName.append("to_")
        keyAvatar.append("to_")
      }
      try {
        obj.message.getExtMap(keyAvatar.toString() + "avatar")?.let {
          Glide.with(mContext).load(
              obj.message.getExtMap(keyAvatar.toString() + "avatar")).asBitmap().transform(
              CropCircleTransformation(mContext)).crossFade().error(
              R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(
              mBinding.avatarIv)
//          val requestOption = RequestOptions().circleCrop()
//          requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//          Glide.with(mContext).load(obj.message.getExtMap(keyAvatar.toString() + "avatar")).apply(requestOption).into(mBinding.avatarIv)
        }
//        mBinding.nameTv.text = obj.lastMessage.getStringAttribute(keyName.toString() + "nickname",
//                                                                  "佚名")
        val nickname = obj.message.getExtMap(keyName.toString() + "nickname").toString()
        mBinding.nameTv.text = if (TextUtils.isEmpty(nickname)) "佚名" else nickname
      } catch (e: HyphenateException) {
        mBinding.avatarIv.setImageResource(R.drawable.ic_dialog_avatar_normal)
        mBinding.nameTv.text = "佚名"
        e.printStackTrace()
      }
      mBinding.timeTv.text = DateFormatter.LongFormatTime(obj.message.msgTime.toString())
      mBinding.messageTv.text = (obj.message.body as EMTextMessageBody).message
      if (obj.unreadMessageList.isNotEmpty()){
        mBinding.messageTipIv.visibility = View.VISIBLE
        UnreadMsgUtils.show(mBinding.messageTipIv, obj.unreadMessageList.size)
      } else mBinding.messageTipIv.visibility = View.INVISIBLE

      if (obj.message.to.equals("2")){
        mBinding.tvFromType.visibility = View.GONE
      }else if (obj.message.getExtMap("fromType").equals("2")){
        mBinding.tvFromType.visibility = View.VISIBLE
        mBinding.tvFromType.text = if (OpenCallFragment.matchButtonEntity == null) "文字匹配" else OpenCallFragment.matchButtonEntity!!.data.textTip.title
        mBinding.tvFromType.setBackgroundResource(R.drawable.rect_f2446d_text_bg)
      } else if (obj.message.getExtMap("fromType").equals("3")){
        mBinding.tvFromType.visibility = View.VISIBLE
        mBinding.tvFromType.text = "来自邀新"
        mBinding.tvFromType.setBackgroundResource(R.drawable.rect_8acc2c_voice_bg)
      } else {
        mBinding.tvFromType.visibility = View.VISIBLE
        mBinding.tvFromType.text = if (OpenCallFragment.matchButtonEntity == null) "语音匹配" else OpenCallFragment.matchButtonEntity!!.data.voiceTip.title
        mBinding.tvFromType.setBackgroundResource(R.drawable.rect_8acc2c_voice_bg)
      }

      mBinding.root.setOnClickListener {
        try {
          obj.message.conversationId()?.let {
            val nickname = obj.message.getExtMap(keyName.toString() + "nickname").toString()
            val avatar = obj.message.getExtMap(keyAvatar.toString() + "avatar").toString()

            val b = Bundle()

            b.putInt("fromType",if (TextUtils.isEmpty(obj.message.getExtMap("fromType"))) 1 else obj.message.getExtMap("fromType")!!.toInt())
            b.putBoolean("isFriend",if (!TextUtils.isEmpty(obj.message.getExtMap("isFriend")) && obj.message.getExtMap("isFriend")!!.toInt() == 1) true else false)
            ChatActivity.start(mContext, it, if (TextUtils.isEmpty(nickname)) "佚名" else nickname,
                    avatar,b)
            mBinding.messageTipIv.visibility = View.GONE
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_chatlist",it))
          }
        } catch (e: HyphenateException) {
          e.printStackTrace()
        }
      }
      mBinding.avatarIv.setOnClickListener {
        val uid = obj.message.to
        EventBus.getDefault().post(MessageFragmentViewModel.ShowUserInfoVM(uid))
      }
    }
  }

  /**
   * 已废弃
   */
  class ViewHolderHeader(parent: ViewGroup?) : BaseViewHolder<Any, ItemMessageHeaderBinding>(parent,
                                                                                             R.layout.item_message_header) {
    var mOnClickItemListener: OnClickItemListener? = null
    override fun onBindViewHolder(obj: Any, position: Int) {
      super.onBindViewHolder(obj, position)

      mBinding.root.setOnClickListener {
        mOnClickItemListener?.onClickItem(it, obj, position)
      }
    }
  }

  /**
   * 接收到消息（环信） - 来自广场
   */
  class ViewHolderSquareContent(parent: ViewGroup?) : BaseViewHolder<MessageEntity, ItemMessageBinding>(parent,
          R.layout.item_message) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: MessageEntity, position: Int) {
      super.onBindViewHolder(obj, position)

      val keyName: StringBuilder = StringBuilder()
      val keyAvatar: StringBuilder = StringBuilder()
      if (obj.message.direct().name == EMMessage.Direct.RECEIVE.name) {
        keyName.append("from_")
        keyAvatar.append("from_")
      } else {
        keyName.append("to_")
        keyAvatar.append("to_")
      }

      obj.message.getExtMap(keyAvatar.toString() + "avatar")?.let {
        Glide.with(mContext).load(obj.message.getExtMap(keyAvatar.toString() + "avatar"))
                .asBitmap().transform(CropCircleTransformation(mContext))
                .crossFade().error(R.drawable.bg_circle_black)
                .placeholder(R.drawable.bg_circle_black).into(mBinding.avatarIv)
      }
      val nickname = obj.message.getExtMap(keyName.toString() + "nickname").toString()
      mBinding.nameTv.text = if (TextUtils.isEmpty(nickname)) "佚名" else nickname

      mBinding.timeTv.text = DateFormatter.LongFormatTime(obj.message.msgTime.toString())
      if((obj.message.body as EMTextMessageBody).message!!.length > 15) {
        mBinding.messageTv.text = (obj.message.body as EMTextMessageBody).message.substring(0, 15) + "..."
      } else {
        mBinding.messageTv.text = (obj.message.body as EMTextMessageBody).message
      }
      if (obj.unreadMessageList.isNotEmpty()) {
        mBinding.messageTipIv.visibility = View.VISIBLE
        UnreadMsgUtils.show(mBinding.messageTipIv, obj.unreadMessageList.size)
      } else {
        mBinding.messageTipIv.visibility = View.INVISIBLE
      }

      mBinding.tvFromType.text = "来自广场"
      mBinding.tvFromType.setBackgroundResource(R.drawable.rect_f2446d_text_bg)

      mBinding.root.setOnClickListener {
        obj.message.conversationId()?.let {
          val nickname = obj.message.getExtMap(keyName.toString() + "nickname").toString()
          val avatar = obj.message.getExtMap(keyAvatar.toString() + "avatar").toString()

          ChatActivity.startBySquare(mContext, it, nickname, avatar, null)
          mBinding.messageTipIv.visibility = View.GONE
          EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_chatlist", it))
        }
      }
      mBinding.avatarIv.setOnClickListener {
        val uid = obj.message.to
        EventBus.getDefault().post(MessageFragmentViewModel.ShowUserInfoVM(uid))
      }
    }
  }

  /**
   * 接收到系统消息 - 来自聊得得服务器推送
   */
  class ViewHolderSystemMessage(parent: ViewGroup?) : BaseViewHolder<MessageEntity, ItemMessageBinding>(parent,
          R.layout.item_message) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(obj: MessageEntity, position: Int) {
      super.onBindViewHolder(obj, position)

      val type = obj.message.getExtMap("type")
      val msg = obj.message.getExtMap("msg")
      val url_type = obj.message.getExtMap("url_type")

      Glide.with(mContext).load(R.drawable.ic_message_system_head).asBitmap()
              .transform(CropCircleTransformation(mContext)).crossFade()
              .error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
              .into(mBinding.avatarIv)
      mBinding.nameTv.text = "系统消息"
      mBinding.timeTv.text = DateFormatter.LongFormatTime(obj.message.msgTime.toString())
      mBinding.tvFromType.visibility = View.GONE
      if(msg!!.length > 15) {
        mBinding.messageTv.text = msg.substring(0, 15) + "..."
      } else {
        mBinding.messageTv.text = msg
      }
      if (obj.unreadMessageList.isNotEmpty()) {
        mBinding.messageTipIv.visibility = View.VISIBLE
        UnreadMsgUtils.show(mBinding.messageTipIv, obj.unreadMessageList.size)
      } else mBinding.messageTipIv.visibility = View.INVISIBLE

      mBinding.root.setOnClickListener {
          SystemMessageActivity.start(mContext, type, msg, url_type)
          mBinding.messageTipIv.visibility = View.GONE
      }
    }
  }
}