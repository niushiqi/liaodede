package com.dyyj.idd.chatmore.viewmodel

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.dyyj.idd.chatmore.BuildConfig
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseChatAdapterV2
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventGiftMessage
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.ImageMessage
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.network.result.*
import com.dyyj.idd.chatmore.ui.adapter.ChatAdapter
import com.dyyj.idd.chatmore.utils.GifsSizeFilter
import com.google.gson.Gson
import com.gt.common.gtchat.extension.getExtMap
import com.gt.common.gtchat.extension.niceString
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.CaptureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 聊天界面
 */
class ChatViewModel : ViewModel() {
  var mPageNum: Int = 0
  var mUsername: String? = null
  var mNickname: String? = null
  var mAvatar: String? = null
  var mFriendsAvatar: String? = null
  var mRewardId: String? = null
  var mUserInfo: UserDetailInfoResult.Data? = null
  var mFriendExperienceResult: FriendExperienceResult? = null
  private var mChatImage: ImageMessageResult.Data? = null
  private val mAdapter by lazy { ChatAdapter() }

  val userid by lazy { mDataRepository.getUserid() }
  /**
   * 获取Adapter
   */
  fun getAdapter() = mAdapter

  /**
   * 设置头像
   */
  fun setFriendsAvatar(avatar: String?, userId: String) {
    mFriendsAvatar = avatar
    getAdapter().setLeftAvatar(mFriendsAvatar).setLeftUid(userId).setRightAvatar(
        mDataRepository.getUserInfoEntity()?.avatar)
  }

  fun setUsername(username: String) {
    this.mUsername = username
  }

  fun setNickName(nickname: String) {
    this.mNickname = nickname
  }

  fun setAvatar(avatar: String?) {
    this.mAvatar = avatar
  }

  fun getPageNum() = mPageNum

  fun getOnlineStatus(friendUserId: String) {//http://api.liaodede.net:8083/v1/friendship/getFriendOnlineStatus
            val subscribe = mDataRepository.getFriendOnlineStatus(friendUserId)
            .subscribe({EventBus.getDefault().post(FriendOnlineStatusVM(it))}, {
      Timber.tag("getOnlineStatus").e("getOnlineStatus获取失败")
    })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取结束时间
   */
  fun getMessageEndTime(otherUserId: String){
    mDataRepository.getMessageEndTime(otherUserId).subscribe({
      EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.HWAT_MESSAGE_END_TIME,it.data?.endTimestamp))
    },{})
  }

  /**
   * 加载消息列表
   */
  fun loadMessageList() {
    mUsername ?: return
    mPageNum++
    val subscribe = mDataRepository.queryConversationEntityList(mUsername!!).subscribe({ list ->
                                                                                         EventBus.getDefault().post(MessageVM(list))
                                                                                       }, {
                                                                                         Timber.tag("chat").e("记录获取失败")
                                                                                       }, {
                                                                                         Timber.tag(
                                                                                             "chat").i(
                                                                                             "完成")

                                                                                       })
    mCompositeDisposable.add(subscribe)

  }

  /**
   * 文字匹配添加好友
   */
  fun addFriendFromTextMatch(toUserId: String){
    niceToast("已申请, 请等待通过! ")
    mDataRepository.addFriendActionFromTextMatch(toUserId).subscribe({},{})
    val subscribe = Observable.timer(10,TimeUnit.SECONDS).subscribe({
      val subscribe = mDataRepository.checkRelationApi(toUserId).subscribe({
        var isFriend = it.data?.isFriend == "1"
        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_IS_FRIEND,isFriend))
      },{})
    },{})

    mCompositeDisposable.add(subscribe)
  }

  /**
   * 文字匹配添加好友
   */
  fun addFriendFromSquare(toUserId: String){
    niceToast("已申请, 请等待通过! ")
    mDataRepository.addFriendActionFromSquare(toUserId).subscribe({

    }, {

    })
    /*val subscribe = Observable.timer(10,TimeUnit.SECONDS).subscribe({
      val subscribe = mDataRepository.checkRelationApi(toUserId).subscribe({
        var isFriend = it.data?.isFriend == "1"
        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_IS_FRIEND,isFriend))
      },{})
    },{})

    mCompositeDisposable.add(subscribe)*/
  }

  /**
   * 检查好友关系
   */
  fun netCheckRelation(toUserId: String) {
    val subscribe = mDataRepository.checkRelationApi(toUserId).subscribe({
      var isFriend = it.data?.isFriend == "1"
      EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_IS_FRIEND,isFriend))
      if (it.errorCode != 200) {
        niceToast(it.errorMsg)
      }
    }, {  })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 通知关闭
   */
  fun exitMessageMatching(toUserId: String) {
    mDataRepository.exitMessageMatching(toUserId).subscribe({},{})
  }

  /**
   * 发送消息
   */
  fun sendMessage(content: String,fromType: Int,isFriend: Boolean,matchTime: Long) {
    val message = mDataRepository.sendMessage(content, mUsername!!, mNickname!!, mAvatar!!, fromType, isFriend, matchTime)
//    if (getAdapter().getList().size == 0){
//      EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_INIT_RECYCLER, arrayListOf(message)))
//    }
    insertMessage(message)
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 发送消息
   */
  fun sendMessageBySquare(content: String) {
    val message = mDataRepository.sendMessageBySquare(content, mUsername!!, mNickname!!, mAvatar!!, null)
//    if (getAdapter().getList().size == 0){
//      EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_INIT_RECYCLER, arrayListOf(message)))
//    }
    insertMessage(message)
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 文字匹配成功发送默认消息
   */
  fun sendTextMatchDefaultMessage(matchTime: Long) {
    val message = mDataRepository.sendMessage("很高兴认识你，一起聊聊吧！", mUsername!!, mNickname!!, mAvatar!!, 2, false, matchTime)
    insertMessage(message)
  }

  /**
   * 广场聊天发送默认消息
   */
  fun sendSquareDefaultMessage(topics: List<String>) {
    //获取topic的标题
    val squareTopicTitle = "今天我们一起嗨吧"

    val subscribe = mDataRepository.getPlazaTopic(topics[0]).subscribe({
      it.data?.let {
        val message = mDataRepository.sendMessageBySquare("我也参加了%[#${it.squareTopicTitle}#]%，非常喜欢你的评论，一起聊聊吧！",
                mUsername!!, mNickname!!, mAvatar!!, topics[0])
        insertMessage(message)
        getAdapter().addChatBottom(message, true)
        Timber.tag("niushiqi-task").i("sendSquareDefaultMessage " + System.currentTimeMillis())
      }
    }, {
      it.printStackTrace()
    })
  }

  /**
   * 发送添加好友  发起语音聊天消息
   * 1好友 2语音
   */
  fun sendAddFriendAndStartVoiceMessage(type : Int){
    getAdapter().getList().add(BaseChatAdapterV2.Wrapper(CustomMessageEntity(StatusTag.CUSTOM_FRIEND_ADD_VIOCE, type.toString(),"",userid)))
    getAdapter().notifyItemInserted(getAdapter().getList().size - 1)
    getAdapter().scrollToBotton()
  }

  /**
   * 发送图片消息 - 发给环信服务器
   */
  /*fun sendImageMessage(imagePath: String): EMMessage? {
    if (TextUtils.isEmpty(mUsername)) {
      return null
    }
    return mDataRepository.sendImageMessage(imagePath, mUsername!!, mNickname!!, mAvatar!!)
  }*/

  /**
   * 发送图片URL地址 - 发给环信服务器
   */
  fun sendImageURLMessage(imageMessage: ImageMessage,fromType: Int,isFriend: Boolean,matchTime: Long) {
    val message = mDataRepository.sendImageURLMessage(imageMessage, mUsername!!, mNickname!!, mAvatar!!, fromType, isFriend, matchTime)
    insertMessage(message)
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 广场聊天发送图片URL地址 - 发给环信服务器
   */
  fun sendImageURLMessageBySquare(imageMessage: ImageMessage) {
    val message = mDataRepository.sendImageURLMessageBySquare(imageMessage, mUsername!!, mNickname!!, mAvatar!!, null)
    insertMessage(message)
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 发送礼物卡
   */
  fun sendGiftCardMessage(obj: EventGiftMessage, fromType: Int, isFriend: Boolean, matchTime: Long) {
    val message = mDataRepository.sendGiftCardMessage(obj, mUsername!!, mNickname!!, mAvatar!!, fromType, isFriend, matchTime)
    insertMessage(message)
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 广场聊天发送礼物卡
   */
  fun sendGiftCardMessageBySquare(obj: EventGiftMessage) {
    val message = mDataRepository.sendGiftCardMessageBySquare(obj, mUsername!!, mNickname!!, mAvatar!!, null)
    insertMessage(message)
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 发送图片 - 发给自己服务器 - niushiqifind
   */
  fun sendImageMessage(localUrl: String) {
    val subscribe = mDataRepository.postImageMessage(localUrl).subscribe({
      Timber.tag("niushiqi111").i("errorCode:"+it.errorCode+" errorMsg:"+it.errorMsg+" data:"+it.data)
      if (it.errorCode == 200) {
        var imageMessage: ImageMessage? = ImageMessage(it.data?.image!!, it.data?.imageFilename!!, localUrl)

        mChatImage = it.data

        Timber.tag("niushiqi").i("image: " + mChatImage?.image + " imageFilename: " + mChatImage?.imageFilename)
        EventBus.getDefault().post(ChatViewModel.AvatarVM(true, imageMessage))
      }
      if (it.errorCode != 200) {
        if (it.errorCode == 3030) {
          //增加图片鉴定失败的提示,错误码3030
          niceToast("图片违规，请重新选择")
        } else {
          niceToast(it.errorMsg)
        }
        EventBus.getDefault().post(ChatViewModel.AvatarVM(false))
      }
    }, {
      EventBus.getDefault().post(ChatViewModel.AvatarVM(false, null, niceString(R.string.error_network_http)))
    })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 接收消息
   */
  fun onMessageReceived(list: MutableList<EMMessage>?, toUserid:String) {

    EMClient.getInstance().chatManager().importMessages(list)
    list?.let {
      list.forEach {
        if (it.from == toUserid || it.to == toUserid) {
          val attribute = it.getExtMap(StatusTag.ATTRIBUTE_CALL)
          if (!attribute.isNullOrEmpty()) {
            //语音 or 视频
            sendCustomMessage(it)
            reject(it)
          } else {
              //普通消息
              getAdapter().addChatBottom(it, true)
          }
        }
      }
    }
  }

  /**
   * 处理对方拒绝事件
   */
  private fun reject(item: EMMessage) {
    val attribute = item.getExtMap(StatusTag.ATTRIBUTE_CALL)

    var voiceMessage: VoiceMessage? = null
    if (!attribute.isNullOrEmpty()) {
      voiceMessage = Gson().fromJson(attribute, VoiceMessage::class.java)
    }
    if (voiceMessage?.status == StatusTag.STATUS_REJEC) {
      EventBus.getDefault().post(voiceMessage)
    }

  }

  private fun sendCustomMessage(msg: EMMessage) {
    getAdapter().addChatBottom(msg, true)
  }

  /**
   * 发起请求
   */
  fun sendConnetingMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, fromType: String) {
    val message = mDataRepository.sendConnetingMessage(toUserid, type, toAvatar, toNickName, fromType)
//    getAdapter().addChatBottom(message, true)
  }

  /**
   * 接受语音/视频
   */
  fun sendAcceptMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, fromType: String) {
    val message = mDataRepository.sendCallAcceptMessage(toUserid, type, toAvatar = toAvatar,
            toNickName = toNickName, fromType = fromType)
  }

  /**
   * 拒绝接听
   */
  fun sendRejectMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, fromType: String) {
    Handler(Looper.getMainLooper()).post {
      val message = mDataRepository.sendCallRejectMessage(toUserid, type, toAvatar = toAvatar,
              toNickName = toNickName, fromType = fromType)
    }
  }

  /**
   * 取消呼叫
   */
  fun sendCancelMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, isCallingParty: String, fromType: String) {
    val message = mDataRepository.sendCallCancelMessage(toUserid, type, toAvatar = toAvatar,
                                                        toNickName = toNickName, isCallingParty = isCallingParty, fromType = fromType)
  }

  /**
   * 呼叫中
   */
  fun sendCallInMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, fromType: String) {
    val message = mDataRepository.sendCallInMessage(toUserid, type, toAvatar = toAvatar,
                                                    toNickName = toNickName, fromType = fromType)
  }


  /**
   * 呼叫超时
   */
  fun sendOuttimeMessage(toUserid: String, type: String, toAvatar: String, toNickName: String, fromType: String) {
    val message = mDataRepository.sendCallOutTimeMessage(toUserid, type, toAvatar = toAvatar,
                                                         toNickName = toNickName, fromType = fromType)
  }

  /**
   * 通话结束
   */
  fun sendDisconnetedMessage(fromUseid: String, toUserid: String, type: String, toAvatar: String, toNickName: String, time: String, fromType: String) {
    val message = mDataRepository.sendCallEndMessage(fromUseid, toUserid, type, toAvatar = toAvatar,
                                                     toNickName = toNickName, time = time, fromType = fromType)
  }

  /**
   * 1对1语音控制命令显示
   */
  fun showVoiceCmdMessage(toContent: String, toUserid: String, isCallingParty: Boolean, fromType: String) {
    val message: EMMessage
    if (isCallingParty) {
      message = mDataRepository.createVoiceCmdMessage(toUserid, StatusTag.TYPE_VOICE, mAvatar!!,
                 mNickname!!, toContent = toContent, isCallingParty = isCallingParty, fromType = fromType)
      insertMessage(message)
    } else {
      message = mDataRepository.createVoiceCmdMessage(toUserid, StatusTag.TYPE_VOICE, mAvatar!!,
              mNickname!!, toContent = toContent, isCallingParty = isCallingParty, fromType = fromType)
      insertReceivedMessage(message)
    }
    getAdapter().addChatBottom(message, true)
  }

  /**
   * 获取通话id
   */
  fun getTalkId() = mDataRepository.getTalkId()

  /**
   * 通话类型
   */
  fun getCallType() = mDataRepository.getCallType()

  /**
   * 解除好友关系并删除好友记录
   */
  fun netRevokeFriendship(revokeUserId: String?) {
    revokeUserId ?: return
    val subscribe = mDataRepository.postRevokeFriendship(revokeUserId).subscribe({
                                                                                   mDataRepository.deleteFriendConversation(
                                                                                       revokeUserId)
                                                                                   EventBus.getDefault().post(
                                                                                       RevokeFriendshipVM(
                                                                                           it.errorCode == 200))
                                                                                   if (it.errorCode != 200) {
                                                                                     niceToast(
                                                                                         it.errorMsg)
                                                                                   }
                                                                                 }, {
                                                                                   EventBus.getDefault().post(
                                                                                       RevokeFriendshipVM())
                                                                                 })
    mCompositeDisposable.add(subscribe)

  }

  /**
   * 举报
   */
  fun netReportReason() {
    val subscribe = mDataRepository.postReportReason().subscribe({
                                                                   EventBus.getDefault().post(
                                                                       ReportReasonVM(it,
                                                                                      isSuccess = it.errorCode == 200))
                                                                   if (it.errorCode != 200) {
                                                                     niceToast(it.errorMsg)
                                                                   }
                                                                 }, {
                                                                   EventBus.getDefault().post(
                                                                       ReportReasonVM())
                                                                 })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取个人信息
   */
  fun netUserinfo(userId: String) {
    val subscribe = mDataRepository.getUserDetailInfo(userId).subscribe({
                                                                          if (it.errorCode == 200) {
                                                                            mUserInfo = it.data
                                                                          }
                                                                          EventBus.getDefault().post(
                                                                              UserInfoDetailVM(
                                                                                  it.errorCode == 200))
                                                                        }, {
                                                                          EventBus.getDefault().post(
                                                                              UserInfoDetailVM(
                                                                                  false))
                                                                        })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取个人信息
   */
  fun netUserinfo2(userId: String) {
    val subscribe = mDataRepository.getUserDetailInfo(userId).subscribe({
                                                                          EventBus.getDefault().post(UserInfoDetailVM2(it.data))
                                                                        }, {
                                                                          niceToast(it.message)
                                                                        })
    mCompositeDisposable.add(subscribe)
  }



  /**
   * 举报用户
   */
  fun netReportUser(reportUserId: String, reportReasonId: String) {

    val subscribe = mDataRepository.postReportUser(reportUserId, reportReasonId).subscribe({
                                                                                             EventBus.getDefault().post(
                                                                                                 ReportUserVM(
                                                                                                     it.errorCode == 200))
                                                                                             if (it.errorCode != 200) {
                                                                                               niceToast(
                                                                                                   it.errorMsg)
                                                                                             }
                                                                                           }, {
                                                                                             EventBus.getDefault().post(
                                                                                                 ReportUserVM())
                                                                                           })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 友好度查询
   */
  fun timerFriendExperience(friendUserId: String) {
    val subscribe = Flowable.interval(0, 30, TimeUnit.SECONDS).take(9999).flatMap {
      return@flatMap mDataRepository.getFriendExperience(friendUserId)
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                                                                                         mFriendExperienceResult = it
                                                                                         EventBus.getDefault().post(
                                                                                             FriendExperienceVM(
                                                                                                 it,
                                                                                                 it.errorCode == 200))
                                                                                       }, {
                                                                                         EventBus.getDefault().post(
                                                                                             FriendExperienceVM(
                                                                                                 isSuccess = false))
                                                                                       })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 获取友好度
   */
  fun getFriendExperienceResult(friendUserId: String) = mDataRepository.getFriendExperienceResult(
      friendUserId)

  /**
   * 保存友好度
   */
  fun saveFriendExperienceResult(obj: FriendExperienceResult) {
    mDataRepository.saveFriendExperienceResult(obj)
  }

  /**
   * 领取奖励
   */
  fun getReward(rewardId: String) {
    val subscribe = mDataRepository.postGetReward(rewardId).subscribe({
                                                                        if (it.errorCode == 200) {
                                                                          Toast.makeText(
                                                                              ChatApp.mInstance?.applicationContext,
                                                                              "领取成功",
                                                                              Toast.LENGTH_SHORT).show()
                                                                        } else {
//                        Toast.makeText(ChatApp.mInstance?.applicationContext, "领取失败", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                        EventBus.getDefault().post(
                                                                            GetRewardVM(
                                                                                it.errorCode == 200))
                                                                      })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 清空未读消息
   */
  fun clearUnreadMessage(friendUserId: String) {
    EventBus.getDefault().post(ClearUnreadMessage())
    mDataRepository.clearUnreadMessage(friendUserId)
  }

  /**
   * 领取友好度等级奖励
   */
  fun getFriendExperienceReward(friendUserId: String, rewardLevel: String) {
    val subscribe = mDataRepository.getFriendExperienceReward(friendUserId, rewardLevel).subscribe(
        {
          Toast.makeText(
              ChatApp.mInstance?.applicationContext,
              "领取成功",
              Toast.LENGTH_SHORT).show()
        }, {})
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 打开相册
   */
  fun openPhoto(context: Activity, maxSelect: Int) {
    Matisse.from(context)
            .choose(MimeType.ofImage(), false)
            .countable(false)
            .capture(true)
            .maxSelectable(maxSelect)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .addFilter(GifsSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .captureStrategy(CaptureStrategy(true, BuildConfig.APPLICATION_ID+".FileProvider"))
            .gridExpectedSize(context.resources.getDimensionPixelSize(R.dimen.grid_expected_size))
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .forResult(1)
  }

  /**
   * 响应一对一好友通话
   */
  fun netFriendExperience(fromUserid: String, toUserid: String, type: Int, status: Int) {
    mDataRepository.postResponseFriendTalk(fromUserid, toUserid, type, status).subscribe({
                                                                                           Timber.i(
                                                                                               it.toString())
                                                                                         }, {
                                                                                           Timber.i(
                                                                                               it.toString())
                                                                                         })
  }

  /**
   * 广场-"1分钟内积极回复"任务
   * 上报开始通话时间
   */
  fun sendSquareMessageTaskStartTime(toUserId: String) {
    mDataRepository.startSquareMessage(userid!!, toUserId).subscribe({
      if(it.errorCode == 200) {
        if(it.data == null) {
          Timber.tag("niushiqi-square").i("1分钟内积极回复任务：发送失败，之前已经有相关记录")
        } else {
          Timber.tag("niushiqi-square").i("1分钟内积极回复任务：" + it.data.endTimestamp.toString())

        }
      }
    },{
      Timber.tag("niushiqi-square").i(it.toString())
    })
  }

  /**
   * 广场-"1分钟内积极回复"任务
   * 获取当前任务进行状态
   * A:任务发起方(不需要显示) B:任务执行方(需要显示任务进度)
   */
  fun getSquareMessageTaskStatus(toUserId: String) {
    mDataRepository.getSquareMessageStatus(userid!!, toUserId).subscribe({
      if(it.errorCode == 200) {
        if(it.data == null) {
          Timber.tag("niushiqi-square").i("1分钟内积极回复任务：任务不存在")
        } else {
          //判断是A还是B
          if(it.data.messageUserId == userid) {
            //如果当前任务处于进行中(结束时间大于当前系统时间),需要显示出来
            if(it.data.endTimestamp!!.toLong() > System.currentTimeMillis().toString().substring(0, 10).toLong()) {
              //改变数据刷新UI
              EventBus.getDefault().post(UpdateMessageVM(true))
            } else {
              Timber.tag("niushiqi-square").i("1分钟内积极回复任务：任务已完成／已失效")
            }
            Timber.tag("niushiqi-task").i("getSquareMessageTaskStatus now：" + System.currentTimeMillis().toString().substring(0, 10).toLong() + "end：" + it.data.endTimestamp!!.toLong())
          } else {
            Timber.tag("niushiqi-square").i("1分钟内积极回复任务：无需显示")
          }
        }
      }
    },{
      Timber.tag("niushiqi-square").i(it.toString())
    })
  }

  class GetRewardVM(val isSuccess: Boolean = false)

  class MessageVM(val list: List<EMMessage>? = null)

  class RevokeFriendshipVM(val isSuccess: Boolean = false)

  class ReportReasonVM(val obj: ReportReasonResult? = null, val isSuccess: Boolean = false)

  class ReportUserVM(val isSuccess: Boolean = false)

  class ClearUnreadMessage()

  /**
   * 友好度
   */
  class FriendExperienceVM(val obj: FriendExperienceResult? = null, val isSuccess: Boolean = false)
  /**
   * 好友当前在线状态
   */
  class FriendOnlineStatusVM(val onlineStatus: OnlineStatus? = null)

  class UserInfoVM(val userId: String)

  class ShowLargeImageVM(val imageUrl: String)

  class UserInfoDetailVM(val success: Boolean)

  class UserInfoDetailVM2(val obj: UserDetailInfoResult.Data? = null)

  class AvatarVM(val avatar: Boolean, val obj: ImageMessage? = null, val errorMsg: String? = "")

  class OpenConnetingActivityVM(val fromUseid:String, val toUserid: String, val type: String)

  class CloseConnetingActivityVM()

  class ShowVoiceCmdMessageVM(val Content: String, val isDelay: Boolean, val isCloseConWindow: Boolean,
                              val isCallingParty: Boolean, val fromType: String)

  class UpdateMessageVM(val isTasking: Boolean = false)

  class SendGiftCardMessage(val obj: EventGiftMessage)
}