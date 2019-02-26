package com.dyyj.idd.chatmore.ui.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseChatAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.*
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.listener.AcceptListener
import com.dyyj.idd.chatmore.listener.RejectListener
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.network.result.CustomMessageEntity
import com.dyyj.idd.chatmore.model.network.result.MessageHeadEntity
import com.dyyj.idd.chatmore.model.network.result.SquareMessageHeadEntity
import com.dyyj.idd.chatmore.ui.main.fragment.OpenCallFragment
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicActivity
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.utils.*
import com.dyyj.idd.chatmore.utils.SpanStringUtils.getEmotionContent
import com.dyyj.idd.chatmore.viewmodel.ChatViewModel
import com.google.gson.Gson
import com.gt.common.gtchat.extension.getExtMap
import com.gt.common.gtchat.extension.niceChatContext
import com.hyphenate.chat.EMImageMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/01
 * desc   : 聊天
 */
class ChatAdapter : BaseChatAdapterV2() {
  private var mLeftAvatar: String? = null
  private var mRightAvatar: String? = null
  private var mListener: Any? = null
  private var mLeftUid: String? = null
  private var mHeaderView : View? = null

  companion object {
    const val TYPE_TIME = 1
    const val TYPE_LEFT = 2
    const val TYPE_RIGHT = 3

    //连接中左
    const val TYPE_CONNECTING_LEFT = 4

    //同意通话左边
    const val TYPE_ACCEPTED_LEFT = 5

    //挂断左边
    const val TYPE_DISCONNECTED_LEFT = 6

    //拒绝左边
    const val TYPE_REJEC_LEFT = 7

    //超时左边
    const val TYPE_OUT_TIME_LEFT = 8

    //取消左边
    const val TYPE_CANCEL_LEFT = 9

    //连接中右
    const val TYPE_CONNECTING_RIGHT = 10

    //取消右边
    const val TYPE_CANCEL_RIGHT = 11

    //同意通话右边
    const val TYPE_ACCEPTED_RIGHT = 12

    //拒绝右边
    const val TYPE_REJEC_RIGHT = 13

    //超时右边
    const val TYPE_OUT_TIME_RIGHT = 14

    //挂断左边
    const val TYPE_DISCONNECTED_RIGHT = 15

    //通话中左边
    const val TYPE_CALL_IN_LEFT = 16

    //通话中右边
    const val TYPE_CALL_IN_RIGHT = 17

    //图片设置左边
    const val TYPE_PICTURE_LEFT = 18

    //图片设置右边
    const val TYPE_PICTURE_RIGHT = 19

    //头部
    const val TYPE_HEAD = 20

    //自定义消息
    const val TYPE_CUSTOM_MESSAGE = 21

    //添加好友 发起语音消息
    const val TYPE_FRIEND_ADD_VOICE = 22

    //广场头部
    const val TYPE_SQUARE_HEAD = 23

    //广场话题跳转页左边
    const val TYPE_SQUARE_TOPIC_LEFT = 24

    //广场话题页跳转右边
    const val TYPE_SQUARE_TOPIC_RIGHT = 25

    //礼物卡左边
    const val TYPE_GIFT_CARD_LEFT = 26

    //礼物卡右边
    const val TYPE_GIFT_CARD_RIGHT = 27
  }

  fun setLeftAvatar(avatar: String?): ChatAdapter {
    mLeftAvatar = avatar
    return this
  }

  fun setRightAvatar(avatar: String?): ChatAdapter {
    mRightAvatar = avatar
    return this
  }

  fun setListener(listener: Any?) {
    mListener = listener
  }

  override fun getItemViewType(position: Int): Int {
    val item = getList()[position].item
    return when (item) {
      is Long -> TYPE_TIME
      is Date -> TYPE_TIME
      is MessageHeadEntity -> TYPE_HEAD
      is SquareMessageHeadEntity -> TYPE_SQUARE_HEAD
      is CustomMessageEntity -> {
        when(item.type){
          StatusTag.CUSTOM_LEFT_TEXT -> TYPE_CUSTOM_MESSAGE
          StatusTag.CUSTOM_FRIEND_ADD_VIOCE -> TYPE_FRIEND_ADD_VOICE
          else -> {
            TYPE_LEFT
          }
        }
      }
      is EMMessage -> {

        val attribute = item.getExtMap(StatusTag.ATTRIBUTE_CALL)
        val attribute1 = item.getExtMap("is_image_url")
        val attribute2 = item.getExtMap("square_topic_id")
        val attribute3 = item.getExtMap("is_gift")

        var voiceMessage: VoiceMessage? = null
        if (!attribute.isNullOrEmpty()) {
          voiceMessage = Gson().fromJson(attribute, VoiceMessage::class.java)
        }
        if (item.direct() == EMMessage.Direct.RECEIVE) {
          when (voiceMessage?.status) {
            StatusTag.STATUS_CONNECTING -> TYPE_CONNECTING_LEFT
            StatusTag.STATUS_CANCEL -> TYPE_CANCEL_LEFT
            StatusTag.STATUS_REJEC -> TYPE_REJEC_LEFT
            StatusTag.STATUS_OUT_TIME -> TYPE_OUT_TIME_LEFT
            StatusTag.STATUS_DISCONNECTED -> TYPE_DISCONNECTED_LEFT
            StatusTag.STATUS_ACCEPTED -> TYPE_ACCEPTED_LEFT
            StatusTag.STATUS_CALL_IN -> TYPE_CALL_IN_LEFT
            else -> {
              //备注，when-else的东西没有用，满足when要求
              if(!attribute1.isNullOrEmpty()) {
                when(item.body) {
                  is EMTextMessageBody -> TYPE_PICTURE_LEFT
                  else -> TYPE_LEFT
                }
              } else if(!attribute2.isNullOrEmpty()) {
                  when(item.body) {
                    is EMTextMessageBody -> TYPE_SQUARE_TOPIC_LEFT
                    else -> TYPE_LEFT
                  }
              } else if(!attribute3.isNullOrEmpty()) {
                when(item.body) {
                  is EMTextMessageBody -> TYPE_GIFT_CARD_LEFT
                  else -> TYPE_LEFT
                }
              } else {
                when (item.body) {
                  is EMImageMessageBody -> TYPE_PICTURE_LEFT
                  else -> TYPE_LEFT
                }
              }
            }
          }
        } else {
          when (voiceMessage?.status) {
            StatusTag.STATUS_CONNECTING -> TYPE_CONNECTING_RIGHT
            StatusTag.STATUS_CANCEL -> TYPE_CANCEL_RIGHT
            StatusTag.STATUS_REJEC -> TYPE_REJEC_RIGHT
            StatusTag.STATUS_OUT_TIME -> TYPE_OUT_TIME_RIGHT
            StatusTag.STATUS_DISCONNECTED -> TYPE_DISCONNECTED_RIGHT
            StatusTag.STATUS_ACCEPTED -> TYPE_ACCEPTED_RIGHT
            StatusTag.STATUS_CALL_IN -> TYPE_CALL_IN_RIGHT
            else -> {
              if(!attribute1.isNullOrEmpty()) {
                when(item.body) {
                  is EMTextMessageBody -> TYPE_PICTURE_RIGHT
                  else -> TYPE_RIGHT
                }
              } else if(!attribute2.isNullOrEmpty()) {
                when(item.body) {
                  is EMTextMessageBody -> TYPE_SQUARE_TOPIC_RIGHT
                  else -> TYPE_RIGHT
                }
              } else if(!attribute3.isNullOrEmpty()) {
                when(item.body) {
                  is EMTextMessageBody -> TYPE_GIFT_CARD_RIGHT
                  else -> TYPE_LEFT
                }
              } else {
                when (item.body) {
                  is EMImageMessageBody -> TYPE_PICTURE_RIGHT
                  else -> TYPE_RIGHT
                }
              }
            }
          }
        }
      }
      else -> TYPE_LEFT
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      TYPE_HEAD -> ViewHolderHead(parent)
      TYPE_CUSTOM_MESSAGE -> ViewHolderCustomLeft(parent)
      TYPE_FRIEND_ADD_VOICE -> ViewHolderFriendAddVoice(parent)
      TYPE_SQUARE_HEAD -> ViewHolderSquareHead(parent)
      TYPE_TIME -> ViewHolderTime(parent)
      TYPE_LEFT -> ViewHolderLeft(parent, mLeftAvatar, mLeftUid)
      TYPE_RIGHT -> ViewHolderRight(parent, mRightAvatar)
      TYPE_CONNECTING_LEFT -> ViewHolderLeftConnecting(parent, mLeftAvatar).setListener(mListener)
      TYPE_CONNECTING_RIGHT -> ViewHolderRightConnecting(parent, mRightAvatar)
      TYPE_CANCEL_LEFT -> ViewHolderLeftCancel(parent, mLeftAvatar)
      TYPE_CANCEL_RIGHT -> ViewHolderRightCancel(parent, mRightAvatar)
      TYPE_REJEC_LEFT -> ViewHolderLeftRejec(parent, mLeftAvatar)
      TYPE_REJEC_RIGHT -> ViewHolderRightReject(parent, mRightAvatar)
      TYPE_OUT_TIME_LEFT -> ViewHolderLeftOutTime(parent, mLeftAvatar)
      TYPE_OUT_TIME_RIGHT -> ViewHolderRightOutTime(parent, mRightAvatar)
      TYPE_ACCEPTED_LEFT -> ViewHolderLeftAccept(parent, mLeftAvatar)
      TYPE_ACCEPTED_RIGHT -> ViewHolderRightAccept(parent, mRightAvatar)
      TYPE_DISCONNECTED_LEFT -> ViewHolderLeftDisconnet(parent, mLeftAvatar)
      TYPE_DISCONNECTED_RIGHT -> ViewHolderRightDisconnet(parent, mRightAvatar)
      TYPE_CALL_IN_LEFT -> ViewHolderLeftCallIn(parent, mLeftAvatar)
      TYPE_CALL_IN_RIGHT -> ViewHolderRightCallIn(parent, mRightAvatar)
      TYPE_PICTURE_LEFT -> ViewHolderLeftPicture(parent, mLeftAvatar, mLeftUid)
      TYPE_PICTURE_RIGHT -> ViewHolderRightPicture(parent, mRightAvatar)
      TYPE_SQUARE_TOPIC_LEFT -> ViewHolderLeftTopic(parent, mLeftAvatar, mLeftUid)
      TYPE_SQUARE_TOPIC_RIGHT -> ViewHolderRightTopic(parent, mRightAvatar)
      TYPE_GIFT_CARD_LEFT -> ViewHolderLeftGiftCard(parent, mRightAvatar, mLeftUid)
      TYPE_GIFT_CARD_RIGHT -> ViewHolderRightGiftCard(parent, mRightAvatar)
      else -> ViewHolderRight(parent, mLeftAvatar)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = getList()[position].item
    when (holder) {
      is ViewHolderTime -> {
        if (item is Date) {
          holder.onBindViewHolder(item, position)
        } else if (item is Long) {
          holder.onBindViewHolder(Date(item), position)
        }
      }
      is ViewHolderFriendAddVoice -> holder.onBindViewHolder(item as CustomMessageEntity,position)
      is ViewHolderCustomLeft -> holder.onBindViewHolder(item as CustomMessageEntity,position)
      is ViewHolderHead -> holder.onBindViewHolder(item as MessageHeadEntity,position)
      is ViewHolderSquareHead -> holder.onBindViewHolder(item as SquareMessageHeadEntity, position )
      is ViewHolderLeft -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRight -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftConnecting -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightConnecting -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftCancel -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightCancel -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftRejec -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightReject -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftOutTime -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightOutTime -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftAccept -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightAccept -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftDisconnet -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightDisconnet -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftPicture -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightPicture -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftTopic -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightTopic -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderLeftGiftCard -> holder.onBindViewHolder(item as EMMessage, position)
      is ViewHolderRightGiftCard -> holder.onBindViewHolder(item as EMMessage, position)
    }
  }

  fun setLeftUid(userId: String): ChatAdapter {
    mLeftUid = userId
    return this
  }

  /**
   * 左边item
   */
  class ViewHolderLeft(parent: ViewGroup?,
      val avatar: String?, val userId: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftBinding>(parent,
                                                                                   R.layout.item_chat_message_left) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
        //将聊天表情解析显示出来
        mBinding.messageBtv.text = getEmotionContent(1,
                mContext, mBinding.messageBtv, (obj.body as EMTextMessageBody).message)
        mBinding.avatarIv.setOnClickListener{
          EventBus.getDefault().post(ChatViewModel.UserInfoVM(userId!!))
          EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_head", (mContext as ChatActivity).getToUserid()))
        }
    }
  }

  /**
   * 自定义左边消息
   */
  class ViewHolderCustomLeft(parent: ViewGroup?) : BaseViewHolder<CustomMessageEntity, ItemChatMessageLeftBinding>(parent,
          R.layout.item_chat_message_left) {

    override fun onBindViewHolder(obj: CustomMessageEntity, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, obj.img)
      //将聊天表情解析显示出来
      mBinding.messageBtv.text = obj.content
      mBinding.avatarIv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.UserInfoVM(obj.userId!!))
        EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_head", (mContext as ChatActivity).getToUserid()))
      }
    }
  }

  /**
   * 左边广场topic item
   */
  class ViewHolderLeftTopic(parent: ViewGroup?,
                       val avatar: String?, val userId: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftBinding>(parent,
          R.layout.item_chat_message_left) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
      //将超链接解析显示出来
      mBinding.messageBtv.text = SpanStringUtils.getTextWithURLContent((obj.body as EMTextMessageBody).message, object : ClickableSpan() {
        override fun onClick(widget: View?) {
          //跳转话题页
          PlazaTopicActivity.start(mContext as ChatActivity, obj.getExtMap("square_topic_id"))
          //Toast.makeText(niceChatContext(), obj.getExtMap("square_topic_id"), Toast.LENGTH_SHORT).show()
        }

        override fun updateDrawState(ds: TextPaint?) {
          ds?.let {
            it.color = Color.RED
          }
        }
      })
      mBinding.messageBtv.movementMethod = LinkMovementMethod.getInstance()
      mBinding.avatarIv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.UserInfoVM(userId!!))
        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_head", (mContext as ChatActivity).getToUserid()))
      }
    }
  }

  /**
   * 右边广场topic item
   */
  class ViewHolderRightTopic(parent: ViewGroup?,
                       val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightBinding>(parent,
          R.layout.item_chat_message_right) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
      //将超链接解析显示出来
      mBinding.messageBtv.text = SpanStringUtils.getTextWithURLContent((obj.body as EMTextMessageBody).message, object : ClickableSpan() {
        override fun onClick(widget: View?) {
          //跳转话题页
          PlazaTopicActivity.start(mContext as ChatActivity, obj.getExtMap("square_topic_id"))
          //Toast.makeText(niceChatContext(), obj.getExtMap("square_topic_id"), Toast.LENGTH_SHORT).show()
        }

        override fun updateDrawState(ds: TextPaint?) {
          ds?.let {
            it.color = Color.RED
          }
        }
      })
      mBinding.messageBtv.movementMethod = LinkMovementMethod.getInstance()
    }
  }

  /**
   * 右边item
   */
  class ViewHolderRight(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightBinding>(parent,
                                                                                    R.layout.item_chat_message_right) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)

      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
        //普通消息
        //将聊天表情解析显示出来
        mBinding.messageBtv.text = getEmotionContent(1,
                mContext, mBinding.messageBtv, (obj.body as EMTextMessageBody).message)
    }
  }

  /**
   * 左边picture item
   */
  class ViewHolderLeftPicture(parent: ViewGroup?,
                       val avatar: String?, val userId: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftPictureBinding>(parent,
          R.layout.item_chat_message_left_picture) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)

      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)

      var emImageMessage = (obj.body as EMTextMessageBody)
      Timber.tag("sendimage").i("左 " + position.toString() + " 图片 URI : " + emImageMessage.message)
      //判断
      val tag = mBinding.messageBtv.getTag()
      //解决recyclerview viewholder复用问题
      if (tag != null && tag as Int != position) {
        mBinding.messageBtv.setImageDrawable(null)
        var params: ViewGroup.LayoutParams = mBinding.messageBtv.getLayoutParams()
        params.height = DisplayUtils.dp2px(mContext, 160.toFloat())
        params.width = DisplayUtils.dp2px(mContext, 160.toFloat())
      }
      if(!(mContext as ChatActivity).isDestroyed) {
        Glide.with(mContext as ChatActivity)
                .load(emImageMessage.message)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .centerCrop()
                .into(object : SimpleTarget<GlideDrawable>() {
                  override fun onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation<in GlideDrawable>) {
                    //动态设置图片大小
                    var params: ViewGroup.LayoutParams = mBinding.messageBtv.getLayoutParams();
                    if (resource.intrinsicHeight > resource.intrinsicWidth) {
                      params.height = DisplayUtils.dp2px(mContext, 160.toFloat())
                      params.width = params.height * resource.intrinsicWidth / resource.intrinsicHeight
                    } else {
                      params.width = DisplayUtils.dp2px(mContext, 160.toFloat())
                      params.height = params.width * resource.intrinsicHeight / resource.intrinsicWidth
                    }
                    Timber.tag("sendimage").i("左 " + position.toString() + " height : " + resource.getIntrinsicHeight() + " width : " + resource.getIntrinsicWidth())
                    mBinding.messageBtv.setImageDrawable(resource)
                    mBinding.messageBtv.setTag(position)
                  }
                })
      } else {
        //niceChatContext().niceToast("glide error 图片未加载完")
        Log.i("zhangwj","mContext isDestroyed")
      }

      mBinding.avatarIv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.UserInfoVM(userId!!))
        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_head", (mContext as ChatActivity).getToUserid()))
      }
      //点击放大图片
      mBinding.messageBtv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.ShowLargeImageVM(emImageMessage.message!!))
      }

      //直接向环信发送图片实现
      /*DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
      var filePath: String? = (obj.body as? EMImageMessageBody)?.localUrl
      var thumbPath: String? = (obj.body as? EMImageMessageBody)?.thumbnailLocalPath()
      val bitmap:Bitmap? = EaseImageCache.getInstance().get(thumbPath)
      mBinding.messageBtv.setImageBitmap(bitmap)

      if (bitmap != null) {
        // thumbnail image is already loaded, reuse the drawable
        mBinding.messageBtv.setImageBitmap(bitmap)
      } else {
        var file: File = File(thumbPath)
        if(file.exists()) {
          Timber.tag("niushiqi").i("exists")
          var bitmap = EaseImageUtils.decodeScaleImage(thumbPath, 160, 160)
          mBinding.messageBtv.setImageBitmap(bitmap)
          EaseImageCache.getInstance().put(thumbPath, bitmap)
        } else {
          Timber.tag("niushiqi").i("not exists")
        }
      }

      mBinding.avatarIv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.UserInfoVM(userId!!))
      }*/
    }
  }

  /**
   * 右边picture item
   */
  class ViewHolderRightPicture(parent: ViewGroup?,
                              val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightPictureBinding>(parent,
          R.layout.item_chat_message_right_picture) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)

      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)

      Timber.tag("sendimage").i("右 " + position.toString() + " 图片 URI : " + obj.getExtMap("localUrl"))
      //判断
      val tag = mBinding.messageBtv.getTag()
      //解决recyclerview viewholder复用问题
      if (tag != null && tag as Int != position) {
        mBinding.messageBtv.setImageDrawable(null)
        var params: ViewGroup.LayoutParams = mBinding.messageBtv.getLayoutParams()
        params.height = DisplayUtils.dp2px(mContext, 160.toFloat())
        params.width = DisplayUtils.dp2px(mContext, 160.toFloat())
      }
      //图片压缩
      Luban.with(niceChatContext())
              .load(obj.getExtMap("localUrl"))
              .setCompressListener(object: OnCompressListener {
                override fun onStart() {
                }

                override fun onSuccess(newFile: File) {
                  val f = File(obj.getExtMap("localUrl"))
                  Timber.tag("sendimage").i("右 " + position.toString() + " 压缩结果为："+f.length()/1024+"KB -> "+newFile.length()/1024+"KB");
                  if(!(mContext as ChatActivity).isDestroyed) {
                    Glide.with(mContext as ChatActivity)
                            .load(newFile)
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.ic_default_avatar)
                            .centerCrop()
                            .into(object : SimpleTarget<GlideDrawable>() {
                              override fun onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation<in GlideDrawable>) {
                                //动态设置图片大小
                                var params: ViewGroup.LayoutParams = mBinding.messageBtv.getLayoutParams();
                                if (resource.intrinsicHeight > resource.intrinsicWidth) {
                                  params.height = DisplayUtils.dp2px(mContext, 160.toFloat())
                                  params.width = params.height * resource.intrinsicWidth / resource.intrinsicHeight
                                } else {
                                  params.width = DisplayUtils.dp2px(mContext, 160.toFloat())
                                  params.height = params.width * resource.intrinsicHeight / resource.intrinsicWidth
                                }
                                Timber.tag("sendimage").i("右 " + position.toString() + " height : " + resource.getIntrinsicHeight() + " width : " + resource.getIntrinsicWidth())
                                mBinding.messageBtv.setImageDrawable(resource);
                                mBinding.messageBtv.setTag(position)
                              }
                            })
                  } else {
                    //niceChatContext().niceToast("glide error 图片未加载完")
                    Log.i("zhangwj","mContext为空")
                  }
                }

                override fun onError(e: Throwable) {
                  Timber.tag("sendimage").i("图片压缩失败");
                }
              }).launch()
      //点击放大图片
      mBinding.messageBtv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.ShowLargeImageVM(obj.getExtMap("localUrl")!!))
      }
    }
  }

  /**
   * 左边gift card item
   */
  class ViewHolderLeftGiftCard(parent: ViewGroup?,
                              val avatar: String?, val userId: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftGiftCardBinding>(parent,
          R.layout.item_chat_message_left_gift_card) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)

      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)

      mBinding.avatarIv.setOnClickListener{
        EventBus.getDefault().post(ChatViewModel.UserInfoVM(userId!!))
        EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_head", (mContext as ChatActivity).getToUserid()))
      }

      var giftName = obj.getExtMap("gift_name")
      var giftIcon = obj.getExtMap("gift_icon")
      var toUserName = obj.getExtMap("to_nickname")

      mBinding.nameTv.text = "送给" + toUserName
      mBinding.giftTv.text = giftName + " ×1"
      Glide.with(mContext).load(giftIcon).asBitmap().crossFade().placeholder(
              R.drawable.ic_gifts_placeholder).into(mBinding.giftImageIv)
    }
  }

  var mCurrentIndex: Int? = 0
  //接收当前下标
  @Subscribe
  fun onIndexChanged(currentIndex: Int) {
    mCurrentIndex = currentIndex
  }

  /**
   * 右边gift card item
   */
  class ViewHolderRightGiftCard(parent: ViewGroup?,
                               val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightGiftCardBinding>(parent,
          R.layout.item_chat_message_right_gift_card) {

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)

      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)

      var giftName = obj.getExtMap("gift_name")
      var giftIcon = obj.getExtMap("gift_icon")
      var toUserName = obj.getExtMap("to_nickname")

      mBinding.nameTv.text = "送给" + toUserName
      mBinding.giftTv.text = giftName + " ×1"
      Glide.with(mContext).load(giftIcon).asBitmap().crossFade().placeholder(
              R.drawable.ic_gifts_placeholder).into(mBinding.giftImageIv)
    }
  }

  //非广场聊天界面
  class ViewHolderHead(parent: ViewGroup?) : BaseViewHolder<MessageHeadEntity, LayoutTextChatFromInfoBinding>(
          parent, R.layout.layout_text_chat_from_info) {
    override fun onBindViewHolder(obj: MessageHeadEntity, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, obj.headImg)
      mBinding.tvName.text = obj.name
      mBinding.tvAge.text = "${obj.age}岁"
      when(obj.type){
        1 -> {
          mBinding.tvFromType.text = if (OpenCallFragment.matchButtonEntity == null) "语音匹配" else OpenCallFragment.matchButtonEntity!!.data.voiceTip.title
          mBinding.tvDescribe.text = "我们聊得很开心，互加为好友"
        }
        2 -> {
          mBinding.tvFromType.text = if (OpenCallFragment.matchButtonEntity == null) "文字匹配" else OpenCallFragment.matchButtonEntity!!.data.textTip.title
          mBinding.tvDescribe.text = Html.fromHtml("我是聊得得小姐姐为你匹配的小伙伴\n <font color='#5D90EF'>3分钟</font><font color='#A9A9AB'>内不加好友，我就消失了哦</font>")
        }
        3 -> {
          mBinding.tvFromType.text = "来自邀请"
          mBinding.tvDescribe.text = Html.fromHtml("老友相聚，一起聊天升等级，拿金币！金币可提现哦~")
        }
      }
    }
  }

  //广场
  class ViewHolderSquareHead(parent: ViewGroup?) : BaseViewHolder<SquareMessageHeadEntity, ItemChatMessageSquareHeadBinding>(
          parent, R.layout.item_chat_message_square_head) {
    override fun onBindViewHolder(obj: SquareMessageHeadEntity, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, obj.headImg)
      mBinding.tvName.text = obj.name
      mBinding.tvFromType.text = "广场"
      obj.isTask?.let {
        if(it == true) {
          mBinding.taskIv.visibility = View.VISIBLE
        } else {
          mBinding.taskIv.visibility = View.INVISIBLE
        }
      }
      mBinding.tvDescribe.text = Html.fromHtml("回复来自广场的消息，获得<font color='#935A04'>20金币</font> <font color='#A9A9AB'>奖\n励！6小时内无回复，金币就消失了哦~</font>")
    }
  }

  class ViewHolderFriendAddVoice(parent: ViewGroup?) : BaseViewHolder<CustomMessageEntity, LayoutFriendAddVoiceBinding>(
          parent, R.layout.layout_friend_add_voice) {
    override fun onBindViewHolder(obj: CustomMessageEntity, position: Int) {
      super.onBindViewHolder(obj, position)
        if (obj.content.equals("1")){//语音
          mBinding.tvContent.text = "开启语音，立即解锁好玩小游戏！还能无限畅聊！"
          mBinding.btnFriendVoice.text = "开启语音"
          mBinding.btnFriendVoice.setBackgroundResource(R.drawable.bg_chat_voice_bg)
        }
        if (obj.content.equals("2")) {//60秒
          mBinding.tvContent.text = "还剩60秒的机会了，快加好友别错过！"
          mBinding.btnFriendVoice.text = "加好友"
          mBinding.btnFriendVoice.setBackgroundResource(R.drawable.bg_chat_addfriend)
        }
        if (obj.content.equals("3")) {
          mBinding.tvContent.text = "只剩下15秒了，申请加好友留下他/她！"
          mBinding.btnFriendVoice.text = "加好友"
          mBinding.btnFriendVoice.setBackgroundResource(R.drawable.bg_chat_addfriend)
        }

      mBinding.btnFriendVoice.setOnClickListener{
        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_FRIEND_ADD_VOICE_CLICK,obj.content))
        if(obj.content.equals("1")) {
          EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_voice_go2", (mContext as ChatActivity).getToUserid()))
        } else if(obj.content.equals("2")) {
          EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_addfriend2", (mContext as ChatActivity).getToUserid()))
        } else if(obj.content.equals("3")) {
          EventTrackingUtils.joinPoint(EventBeans("ck_textmessage_addfriend3", (mContext as ChatActivity).getToUserid()))
        }
      }
    }
  }

  /**
   * 时间
   */
  class ViewHolderTime(parent: ViewGroup?) : BaseViewHolder<Date, ItemChatMessageTimeBinding>(
      parent, R.layout.item_chat_message_time) {
    override fun onBindViewHolder(obj: Date, position: Int) {
      super.onBindViewHolder(obj, position)
      mBinding.dateTv.text = DateFormatter.LongFormatTime(obj.time.toString())
    }
  }


  /**
   * 呼叫中左边
   */
  class ViewHolderLeftConnecting(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftConnectingBinding>(parent,
                                                                                             R.layout.item_chat_message_left_connecting) {
    private var mAcceptListener: AcceptListener? = null
    private var mRejectListener: RejectListener? = null

    fun setListener(listener: Any?): ViewHolderLeftConnecting {
      if (listener is AcceptListener) {
        mAcceptListener = listener
      }
      if (listener is RejectListener) {
        mRejectListener = listener
      }
      return this
    }

    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      /*mBinding.acceptBtn.setOnClickListener {
        mAcceptListener?.onAccept(obj.from, obj.to, obj.body.toString())
      }
      mBinding.rejectBtn.setOnClickListener {
        mRejectListener?.onReject(obj.from, obj.to, obj.body.toString())
      }*/
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
      mBinding.titleTv.text = (obj.body as EMTextMessageBody).message
    }
  }

  /**
   * 呼叫中右边
   */
  class ViewHolderRightConnecting(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightConnectingBinding>(
      parent, R.layout.item_chat_message_right_connecting) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
      mBinding.titleTv.text = (obj.body as EMTextMessageBody).message
    }
  }

  /**
   * 呼叫取消左边
   */
  class ViewHolderLeftCancel(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftCancelBinding>(parent,
                                                                                         R.layout.item_chat_message_left_cancel) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 呼叫取消右边
   */
  class ViewHolderRightCancel(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightCancelBinding>(parent,
                                                                                          R.layout.item_chat_message_right_cancel) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 拒绝左边
   */
  class ViewHolderLeftRejec(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftRejecBinding>(parent,
                                                                                        R.layout.item_chat_message_left_rejec) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)

    }
  }

  /**
   * 拒绝右边
   */
  class ViewHolderRightReject(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightRejectBinding>(parent,
                                                                                          R.layout.item_chat_message_right_reject) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 超时左边
   */
  class ViewHolderLeftOutTime(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftOutTimeBinding>(parent,
                                                                                          R.layout.item_chat_message_left_out_time) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 超时右边
   */
  class ViewHolderRightOutTime(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightOutTimeBinding>(parent,
                                                                                           R.layout.item_chat_message_right_out_time) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 通话中左边
   */
  class ViewHolderLeftAccept(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftAcceptBinding>(parent,
                                                                                         R.layout.item_chat_message_left_accept) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 通话中右边
   */
  class ViewHolderRightAccept(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightAcceptBinding>(parent,
                                                                                          R.layout.item_chat_message_right_accept) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 结束通话左边
   */
  class ViewHolderLeftDisconnet(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftDisconnetBinding>(parent,
                                                                                            R.layout.item_chat_message_left_disconnet) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 结束通话右边
   */
  class ViewHolderRightDisconnet(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightDisconnetBinding>(parent,
                                                                                             R.layout.item_chat_message_right_disconnet) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 通话中左边
   */
  class ViewHolderLeftCallIn(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageLeftCallInBinding>(parent,
                                                                                         R.layout.item_chat_message_left_call_in) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

  /**
   * 通话中右边
   */
  class ViewHolderRightCallIn(parent: ViewGroup?,
      val avatar: String?) : BaseViewHolder<EMMessage, ItemChatMessageRightCallInBinding>(parent,
                                                                                          R.layout.item_chat_message_right_call_in) {
    override fun onBindViewHolder(obj: EMMessage, position: Int) {
      super.onBindViewHolder(obj, position)
      DataBindingUtils.loadAvatar(mBinding.avatarIv, avatar)
    }
  }

}