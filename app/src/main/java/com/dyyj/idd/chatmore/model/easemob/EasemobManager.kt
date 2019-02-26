package com.dyyj.idd.chatmore.model.easemob

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.eventtracking.EventGiftMessage
import com.dyyj.idd.chatmore.model.easemob.message.ImageMessage
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.ui.main.activity.ConnetingActivity
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.main.fragment.CallOutgoingFragment
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import com.dyyj.idd.chatmore.viewmodel.ApplyViewModel
import com.dyyj.idd.chatmore.viewmodel.CallOutgoingViewModel
import com.dyyj.idd.chatmore.viewmodel.ChatViewModel
import com.dyyj.idd.chatmore.viewmodel.MainViewModel
import com.google.gson.Gson
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.EMCallBack
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.EMCallStateChangeListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMMessage.ChatType
import com.hyphenate.exceptions.EMNoActiveCallException
import com.hyphenate.exceptions.EMServiceNotReadyException
import com.hyphenate.util.EMLog
import com.hyphenate.util.NetUtils
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/23
 * desc   : 环信管理类
 */
class EasemobManager {

    val TAG = EasemobManager::class.java.simpleName
    var mCallReceiver: CallReceiver? = null
    val takePicturePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "output_videp_pic.jpg"
    companion object {
        var mAcceptUserid: String? = null
        var mTalkId: String? = null
        var mType: String = StatusTag.TYPE_VOICE
        /**
         * 是否匹配业务中接收
         */
        var isStartMatch = false
        var toUserid: String? = ""
        var from: String? = ""
        var type: String? = ""
        var isReceiveOk = false
        var isCallComing = false
        var isComing = false

        var isCalling = false
        var getCallingState = false
        const val EXT = "ext"
    }


    /**
     * 注册呼叫来电
     */
    fun registerCall(context: Context) {
        if (mCallReceiver != null) {
            context.applicationContext.unregisterReceiver(mCallReceiver)
            mCallReceiver = null
        }
        if (EMClient.getInstance() == null || EMClient.getInstance().callManager() == null) {
            return
        }
        mCallReceiver = CallReceiver()
        val callFilter = IntentFilter(EMClient.getInstance().callManager()?.incomingCallBroadcastAction)
        context.applicationContext.registerReceiver(mCallReceiver, callFilter)

        initCallListener()
    }

    /**
     * 视频截图
     */
    fun takePicture(activity: Activity): String {
        // 获取内置SD卡路径
        val sdCardPath = Environment.getExternalStorageDirectory().getPath();
// 图片文件路径
        val filePath = sdCardPath + File.separator + "screenshot.png"
        val dView = activity.window.decorView;
        dView.isDrawingCacheEnabled = true;
        dView.buildDrawingCache();
        val bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        if (bitmap != null) {
            try {


                val file = File(filePath);
                if (file.exists()) {
                    file.delete()
                }
                val os = FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Timber.tag("UploadVideoFrame").i("success")
            } catch (e: Exception) {
                Timber.tag("UploadVideoFrame").i("error:${e.message}")
            }
        }

//    val dirPath = ChatApp.getInstance().getExternalFilesDir("").absolutePath + "/videos/"
//    val dir = File(dirPath)
//    if (!dir.isDirectory) {
//      Timber.tag("UploadVideoFrame").i("dir.mkdirs")
//      dir.mkdirs()
//    }
//    val path = dirPath + "IMG_" + System.currentTimeMillis() + ".jpg"
//    val callHelper = EMClient.getInstance().callManager().videoCallHelper
//    Timber.tag("UploadVideoFrame").i("path=$path")
//    Timber.tag("UploadVideoFrame").i("takePicture=${callHelper.takePicture(path)}")

        return filePath
    }

    /**
     * 通话状态
     */
    fun initCallListener() {
        EMClient.getInstance().callManager().addCallStateChangeListener { callState, error ->


            Log.e("EM_CALL_MANAGER", "" + callState)
            Timber.tag("niushiqi-tonghua").i("callState: " + callState + " error：" + error)

            when (callState) {
                EMCallStateChangeListener.CallState.CONNECTING // 正在连接对方
                -> {
                }
                EMCallStateChangeListener.CallState.CONNECTED // 双方已经建立连接
                -> {

                }

                EMCallStateChangeListener.CallState.ACCEPTED // 电话接通成功
                -> {
                    if (!isCalling) {
                        isCalling = true
                        getCallingState = true
                        CallOutgoingFragment.talkId?.let {
                            ChatApp.getInstance().getDataRepository().reportSipServiceStatus(ChatApp.getInstance().getDataRepository().getUserid()!!, CallOutgoingFragment.talkId!!, "1")
                        }
                    }
                }
                EMCallStateChangeListener.CallState.DISCONNECTED // 电话断了
                -> {
                    isCalling = false
                    getCallingState = false
                    ChatApp.getInstance().niceToast("连接断开")
                    ChatApp.getInstance().getDataRepository().endCall()
                }
                EMCallStateChangeListener.CallState.NETWORK_UNSTABLE //网络不稳定
                -> {
                }
                EMCallStateChangeListener.CallState.NETWORK_DISCONNECTED -> {
                    CallOutgoingFragment.talkId?.let {
                        ChatApp.getInstance().niceToast("网络断开")
                        ChatApp.getInstance().getDataRepository().reportSipServiceStatus(ChatApp.getInstance().getDataRepository().getUserid()!!, CallOutgoingFragment.talkId!!, "2")
                    }
                }
                EMCallStateChangeListener.CallState.NETWORK_NORMAL //网络恢复正常
                -> {
                }
                else -> {
                }
            }
            when (error) {
                EMCallStateChangeListener.CallError.ERROR_BUSY -> {
                    CallOutgoingFragment.talkId?.let {
                        ChatApp.getInstance().getDataRepository().reportSipServiceStatus(ChatApp.getInstance().getDataRepository().getUserid()!!, CallOutgoingFragment.talkId!!, "3")
                    }
                }
                EMCallStateChangeListener.CallError.ERROR_TRANSPORT -> {
                    CallOutgoingFragment.talkId?.let {
                        ChatApp.getInstance().getDataRepository().reportSipServiceStatus(ChatApp.getInstance().getDataRepository().getUserid()!!, CallOutgoingFragment.talkId!!, "4")
                    }
                }
                EMCallStateChangeListener.CallError.ERROR_NORESPONSE -> {
                    CallOutgoingFragment.talkId?.let {
                        ChatApp.getInstance().getDataRepository().reportSipServiceStatus(ChatApp.getInstance().getDataRepository().getUserid()!!, CallOutgoingFragment.talkId!!, "4")
                    }
                }
                EMCallStateChangeListener.CallError.ERROR_UNAVAILABLE -> {
                    CallOutgoingFragment.talkId?.let {
                        ChatApp.getInstance().getDataRepository().reportSipServiceStatus(ChatApp.getInstance().getDataRepository().getUserid()!!, CallOutgoingFragment.talkId!!, "2")
                    }
                }
            }
        }
    }

    /**
     * 监听登陆状态
     */
    fun initListener() {
        EMClient.getInstance().addConnectionListener(object : EMConnectionListener {
            override fun onConnected() {
            }

            override fun onDisconnected(error: Int) {
                Handler(Looper.getMainLooper()).post {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        niceChatContext().niceToast("当前帐号不存在")
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        niceChatContext().niceToast("该帐号在其他设备登录")
                    } else {
                        if (NetUtils.hasNetwork(ChatApp.getInstance())) {
                            //连接不到聊天服务器
                            val userid = ChatApp.getInstance().getDataRepository().getUserid()
                            netLoginEM(userid, userid)
                            niceChatContext().niceToast("无法连接服务器")
                        } else {
                            //当前网络不可用，请检查网络设置
                            niceChatContext().niceToast("当前网络不可用，请检查网络设置")
                        }

                    }
                }
            }
        })
    }

    /**
     * 环信帐号登陆
     */
    fun netLoginEM(username: String?, password: String?) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) return

        EMClient.getInstance().login(username, password, object : EMCallBack {
            //回调
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                Log.d("main", "登录聊天服务器成功！")
//        ActManagerUtils.getAppManager().currentActivity()!!.runOnUiThread {
////          niceChatContext().niceToast("环信登陆成功")
//        }
            }

            override fun onProgress(progress: Int, status: String) {
                Log.d("main", "onProgress")
            }

            override fun onError(code: Int, message: String) {
                Log.d("main", "登录聊天服务器失Å败！")
//        ActManagerUtils.getAppManager().currentActivity().runOnUiThread {
////          niceChatContext().niceToast("环信登陆失败")
//        }
            }
        })
    }

    /**
     * 拨打语音通话
     */
    fun callVoice(username: String) {
        try {//单参数
            EMClient.getInstance().callManager().makeVoiceCall(username)
        } catch (e: EMServiceNotReadyException) {
            ChatApp.getInstance().niceToast("环信拨打语音拋异常")
//      EventBus.getDefault().post(e)
            e.printStackTrace()
        }
    }

    /**
     * 拨打语音通话 - 添加扩展内容，用于区分 1对1通话 和 匹配通话
     */
    fun callVoiceExt(username: String) {
        try {//多参数
            EMClient.getInstance().callManager().makeVoiceCall(username, "1v1")
        } catch (e: EMServiceNotReadyException) {
            //ChatApp.getInstance().niceToast("环信拨打语音Ext拋异常")
//      EventBus.getDefault().post(e)
            e.printStackTrace()
        }
    }

    /**
     * 拨打视频电话
     */
    fun callVideo(username: String) {
        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(username)
        } catch (e: EMServiceNotReadyException) {
            e.printStackTrace()
        }
    }

    /**
     * 接听通话
     */
    fun answerCall() {
        try {
            EMClient.getInstance().callManager().answerCall()
        } catch (e: EMNoActiveCallException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 拒绝接听
     */
    fun rejectCall() {
        try {
            EMClient.getInstance().callManager().rejectCall()
        } catch (e: EMNoActiveCallException) {
            e.printStackTrace()
        }
    }

    /**
     * 挂断通话
     */
    fun endCall() {
        try {
            EMClient.getInstance().callManager().endCall()
        } catch (e: EMNoActiveCallException) {
            e.printStackTrace()
        }
        // 关闭音频传输
        EMClient.getInstance().conferenceManager().closeVoiceTransfer()

        // 关闭视频传输
        EMClient.getInstance().conferenceManager().closeVideoTransfer()
    }

    /**
     * 发送消息
     */
    fun sendMessage(content: String, toChatUid: String, toNickName: String,
                    toAvatar: String,fromType: Int,isFriend: Boolean,matchTime: Long): EMMessage {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(content, toChatUid)

        val map = hashMapOf<String, String>()
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        map["fromType"] = fromType.toString()
        map["isFriend"] = if (isFriend) "1" else "0"
        map["time"] = matchTime.toString()
        message.setAttribute(EXT, Gson().toJson(map))
        //发送消息
        val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                content,if (ChatActivity.fromType == 2) 1 else 0).subscribe(
                {
                    if (it.errorCode == 200) {
//                Toast.makeText(niceChatContext(), "ok", Toast.LENGTH_SHORT).show()
                        EMClient.getInstance().chatManager().sendMessage(message)
                        message.setMessageStatusCallback(object : EMCallBack {
                            override fun onSuccess() {

                                Timber.tag("EMClient").i("onSuccess")
                                EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
                                EMClient.getInstance().chatManager().importMessages(listOf(message))

                            }

                            override fun onProgress(p0: Int, p1: String?) {
                                Timber.tag("EMClient").i("onProgress:$p0")
                            }

                            override fun onError(p0: Int, p1: String?) {
                                Timber.tag("EMClient").i("code:$p0 message:$p1")
                            }
                        })
                    } else if(it.errorCode == 3006) {
                        ChatApp.getInstance().niceToast(it.errorMsg!!)
                    }
                },{//onError
                    Log.i("zhangwj",it.message)
                })
        CompositeDisposable().add(subscribe)

        return message
    }

    /**
     * 通过广场发送消息
     */
    fun sendMessageBySquare(content: String, toChatUid: String, toNickName: String,
                            toAvatar: String, squareTopicId: String?): EMMessage {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(content, toChatUid)

        val map = hashMapOf<String, String>()
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        map["fromType"] = ChatActivity.FROM_TYPE_SQUARE.toString()   /* 广场 */
        squareTopicId?.let{
            map["square_topic_id"] = squareTopicId
        }
        message.setAttribute(EXT, Gson().toJson(map))
        //发送消息
        val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                content, 2).subscribe(
                {
                    if (it.errorCode == 200) {
//                Toast.makeText(niceChatContext(), "ok", Toast.LENGTH_SHORT).show()
                        EMClient.getInstance().chatManager().sendMessage(message)
                        message.setMessageStatusCallback(object : EMCallBack {
                            override fun onSuccess() {

                                Timber.tag("EMClient").i("onSuccess")
                                EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
                                EMClient.getInstance().chatManager().importMessages(listOf(message))

                            }

                            override fun onProgress(p0: Int, p1: String?) {
                                Timber.tag("EMClient").i("onProgress:$p0")
                            }

                            override fun onError(p0: Int, p1: String?) {
                                Timber.tag("EMClient").i("code:$p0 message:$p1")
                            }
                        })
                    } else if(it.errorCode == 3006) {
                        ChatApp.getInstance().niceToast(it.errorMsg!!)
                    }
                },{})
        CompositeDisposable().add(subscribe)

        return message
    }

    //直接向环信发送图片实现
    /*fun sendImageMessage(imagePath: String, toChatUid: String, toNickName: String,
                       toAvatar: String):EMMessage {
    var message = EMMessage.createImageSendMessage(imagePath, false, toChatUid);

    val map = hashMapOf<String, String>()
    map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
    map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
    map["to_avatar"] = toAvatar
    map["to_nickname"] = toNickName
    message.setAttribute(EXT, Gson().toJson(map))

    message.setMessageStatusCallback(object : EMCallBack {
      override fun onSuccess() {
        Log.i("niushiqi", "image onsuccess")
        Timber.tag("EMClient").i("onSuccess")
        *//*EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
        EMClient.getInstance().chatManager().importMessages(arrayListOf(message))
        val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                content).subscribe(
                {
                  if (it.errorCode == 200) {
    //                Toast.makeText(niceChatContext(), "ok", Toast.LENGTH_SHORT).show()
                  }
                })
        CompositeDisposable().add(subscribe)*//*
      }

      override fun onProgress(p0: Int, p1: String?) {
        Timber.tag("EMClient").i("onProgress:$p0")
        Log.i("niushiqi", "image onProgress")
      }

      override fun onError(p0: Int, p1: String?) {
        Timber.tag("EMClient").i("code:$p0 message:$p1")
        Log.i("niushiqi", "image onError")
      }
    })
    EMClient.getInstance().chatManager().sendMessage(message)
    Log.i("niushiqi", "test1")
    return message
    }*/

    /**
     * 发送图片URL消息
     */
    fun sendImageURLMessage(imageMessage: ImageMessage, toChatUid: String, toNickName: String,
                            toAvatar: String,fromType: Int,isFriend: Boolean,matchTime: Long):EMMessage {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(imageMessage.image, toChatUid)

        val map = hashMapOf<String, String>()
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        map["is_image_url"] = "yes"
        map["localUrl"] = imageMessage.localUrl
        map["fromType"] = fromType.toString()
        map["isFriend"] = if (isFriend) "1" else "0"
        map["time"] = matchTime.toString()
        message.setAttribute(EXT, Gson().toJson(map))
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
        Log.i("niushiqi", "sendImageURLMessage:" + imageMessage.image)
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {

                Timber.tag("EMClient").i("text onSuccess")
                Log.i("niushiqi", "onsuccess")
                EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
                EMClient.getInstance().chatManager().importMessages(arrayListOf(message))
                val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                        imageMessage.image, if (ChatActivity.fromType == 2) 1 else 0).subscribe(
                        {
                          if (it.errorCode == 200) {
        //                Toast.makeText(niceChatContext(), "ok", Toast.LENGTH_SHORT).show()
                          }
                        })
                CompositeDisposable().add(subscribe)
            }

            override fun onProgress(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("onProgress:$p0")
                Log.i("niushiqi", "text onProgress")
            }

            override fun onError(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("code:$p0 message:$p1")
                Log.i("niushiqi", "text onError")
            }
        })
        return message
    }

    /**
     * 广场聊天 发送图片URL消息
     */
    fun sendImageURLMessageBySquare(imageMessage: ImageMessage, toChatUid: String, toNickName: String,
                            toAvatar: String, squareTopicId: String?):EMMessage {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(imageMessage.image, toChatUid)

        val map = hashMapOf<String, String>()
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        map["fromType"] = ChatActivity.FROM_TYPE_SQUARE.toString()   /* 广场 */
        squareTopicId?.let{
            map["square_topic_id"] = squareTopicId
        }
        map["is_image_url"] = "yes"
        map["localUrl"] = imageMessage.localUrl
        message.setAttribute(EXT, Gson().toJson(map))
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
        Log.i("niushiqi", "sendImageURLMessage:" + imageMessage.image)
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {

                Timber.tag("EMClient").i("text onSuccess")
                Log.i("niushiqi", "onsuccess")
                EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
                EMClient.getInstance().chatManager().importMessages(arrayListOf(message))
                val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                        imageMessage.image, 2).subscribe(
                        {
                            if (it.errorCode == 200) {
                                //                Toast.makeText(niceChatContext(), "ok", Toast.LENGTH_SHORT).show()
                            }
                        })
                CompositeDisposable().add(subscribe)
            }

            override fun onProgress(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("onProgress:$p0")
                Log.i("niushiqi", "text onProgress")
            }

            override fun onError(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("code:$p0 message:$p1")
                Log.i("niushiqi", "text onError")
            }
        })
        return message
    }

    /**
     * 发送礼物卡消息
     */
    fun sendGiftCardMessage(obj: EventGiftMessage, toChatUid: String, toNickName: String,
                            toAvatar: String,fromType: Int,isFriend: Boolean,matchTime: Long):EMMessage {
        val message = EMMessage.createTxtSendMessage("送了你礼物：" + obj.giftName, toChatUid)

        val map = hashMapOf<String, String>()
        //好友相关
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        //礼物相关
        map["is_gift"] = "yes"
        map["gift_name"] = if(obj.giftName == null) "无效的礼物" else obj.giftName
        map["gift_icon"] = if(obj.giftIcon == null) "" else obj.giftIcon
        //来源相关
        map["fromType"] = fromType.toString()
        map["isFriend"] = if (isFriend) "1" else "0"
        map["time"] = matchTime.toString()
        message.setAttribute(EXT, Gson().toJson(map))
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                Timber.tag("EMClient").i("text onSuccess")
                EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
                EMClient.getInstance().chatManager().importMessages(arrayListOf(message))
                val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                        "送了你礼物：" + obj.giftName!!, if (ChatActivity.fromType == 2) 1 else 0).subscribe(
                        {
                            if (it.errorCode == 200) {
                            }
                        })
                CompositeDisposable().add(subscribe)
            }

            override fun onProgress(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("onProgress:$p0")
            }

            override fun onError(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("code:$p0 message:$p1")
            }
        })
        return message
    }

    /**
     * 广场聊天 发送礼物卡消息
     */
    fun sendGiftCardMessageBySquare(obj: EventGiftMessage, toChatUid: String, toNickName: String,
                                    toAvatar: String, squareTopicId: String?):EMMessage {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage("送了你礼物：" + obj.giftName, toChatUid)

        val map = hashMapOf<String, String>()
        //好友相关
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        //礼物相关
        map["is_gift"] = "yes"
        map["gift_name"] = if(obj.giftName == null) "无效的礼物" else obj.giftName
        map["gift_icon"] = if(obj.giftIcon == null) "" else obj.giftIcon
        //来源相关
        map["fromType"] = ChatActivity.FROM_TYPE_SQUARE.toString()   /* 广场 */
        squareTopicId?.let{
            map["square_topic_id"] = squareTopicId
        }
        message.setAttribute(EXT, Gson().toJson(map))
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                Timber.tag("EMClient").i("text onSuccess")
                EventBus.getDefault().post(ApplyViewModel.MessageRefreshVM())
                EMClient.getInstance().chatManager().importMessages(arrayListOf(message))
                val subscribe = ChatApp.getInstance().getDataRepository().sendFriendTextMessage(toChatUid,
                        "送了你礼物：" + obj.giftName!!, 2).subscribe(
                        {
                            if (it.errorCode == 200) {
                            }
                        })
                CompositeDisposable().add(subscribe)
            }

            override fun onProgress(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("onProgress:$p0")
            }

            override fun onError(p0: Int, p1: String?) {
                Timber.tag("EMClient").i("code:$p0 message:$p1")
            }
        })
        return message
    }

    /**
     * 发送群聊消息
     */
    fun sendGroupMessage(content: String, toChatUsername: String) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(content, toChatUsername)
        //如果是群聊，设置chattype，默认是单聊
        message.chatType = ChatType.GroupChat
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
    }

    /**
     * 发送透传消息
     */
    fun sendExtraMessage(msg: VoiceMessage, toChatUsername: String, toAvatar: String,
                         toNickName: String, extra: String, fromType: String): EMMessage {
        Timber.tag("niushiqi-tonghua").i("透传消息显示 VoiceMessageval status:"+msg.status
                +" fromUserid:"+msg.fromUserid
                +" type:"+msg.type
                +" toUserid"+msg.toUserid
                +" toChatUsername:"+toChatUsername
                +" toAvatar:"+toAvatar
                +" toNickName:"+toNickName)
        val cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD)

        val map = hashMapOf<String, String>()
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        map["from_userid"] = msg.fromUserid
        map["to_userid"] = msg.toUserid
        map["voice_type"] = msg.type
        map["extra"] = extra
        map["fromType"] = fromType
        cmdMsg.setAttribute(EXT, Gson().toJson(map))
        val cmdBody = EMCmdMessageBody(msg.status)
        cmdMsg.to = toChatUsername
        cmdMsg.addBody(cmdBody)
        EMClient.getInstance().chatManager().sendMessage(cmdMsg)
        return cmdMsg
    }

    /**
     * 发送语音/视频消息
     */
    fun sendCallMessage(msg: VoiceMessage, toChatUsername: String, toAvatar: String,
                        toNickName: String): EMMessage {

        val message = EMMessage.createTxtSendMessage(msg.type, toChatUsername)

        val gson = Gson()
        // 增加自己特定的属性
        val map = hashMapOf<String, String>()
        map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
        map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        map["to_avatar"] = toAvatar
        map["to_nickname"] = toNickName
        map[StatusTag.ATTRIBUTE_CALL] = gson.toJson(msg)
        message.setAttribute("ext", gson.toJson(map))
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                Timber.tag("")
            }

            override fun onProgress(p0: Int, p1: String?) {
                Timber.tag("")
            }

            override fun onError(p0: Int, p1: String?) {
                Timber.tag("")
            }
        })
        EMClient.getInstance().chatManager().sendMessage(message)
        return message
    }

    /**
     * 创建1对1语音控制命令-文字消息-不发送
     */
    fun createVoiceCmdMessage(msg: VoiceMessage, toChatUsername: String, toAvatar: String,
                              toNickName: String, toContent: String, fromType: String): EMMessage {
        val message = EMMessage.createTxtSendMessage(toContent, toChatUsername)

        val gson = Gson()
        // 增加自己特定的属性
        val map = hashMapOf<String, String>()
        if(ChatApp.getInstance().getDataRepository().getUserid() == msg.fromUserid) {
            //该用户是发起方
            map["from_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
            map["from_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
            map["to_avatar"] = toAvatar
            map["to_nickname"] = toNickName
        } else {
            map["from_avatar"] = toAvatar
            map["from_nickname"] = toNickName
            map["to_avatar"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.avatar ?: ""
            map["to_nickname"] = ChatApp.getInstance().getDataRepository().getUserInfoEntity()?.nickname ?: ""
        }
        //不知道为什么突然间好了，兼容克里的代码
        //if(fromType.equals("4")) {
            map["fromType"] = fromType
        //}
        map[StatusTag.ATTRIBUTE_CALL] = gson.toJson(msg)
        message.setAttribute("ext", gson.toJson(map))

        return message
    }

    /**
     * 创建系统消息EMMessage
     */
    fun createEMMessageBySystemMessage(systemMessage: MainViewModel.SystemMessageResult): EMMessage {
        val message = EMMessage.createTxtSendMessage("系统消息", ChatApp.getInstance().getDataRepository().getUserid())
        message.from = "2"
        message.to = ChatApp.getInstance().getDataRepository().getUserid()
        message.setDirection(EMMessage.Direct.RECEIVE)
        message.msgTime = systemMessage.ctime!!.toLong()

        val map = hashMapOf<String, String>()
        map["type"] = systemMessage.type!!
        map["msg"] = systemMessage.msg!!
        map["url_type"] = systemMessage.activityName!!
        map["url_param"] = systemMessage.param_value!!
        message.setAttribute(EXT, Gson().toJson(map))

        return message
    }

    /**
     * 聊天界面发起的语音视频信息
     */
    fun setChatCallInfo(userid: String, talkId: String, type: String) {
        mAcceptUserid = userid
        mTalkId = talkId
        mType = type
    }

    fun getAcceptedUserId() = mAcceptUserid

    fun getCallType() = mType

    /**
     * 获取通话id
     */
    fun getTalkId() = mTalkId

    fun setReceiveMsg(context: Context) {
        Timber.tag("niushiqi-pipei").i("设置开启环信接收 setReceiveMsg")
        /*ChatApp.getInstance().getDataRepository().answerCall()
        MainActivity.startCallIn(context, from!!, toUserid!!)
        EventBus.getDefault().post(CallOutgoingViewModel.MatchSuccess())
        isStartMatch = false*/
        isReceiveOk = true;
//    Log.e(ChatApp.LOGSTR, "talkmatchingresult_30")
        if (isCallComing) {
            ChatApp.getInstance()
//      Log.e(ChatApp.LOGSTR, "talkmatchingresult_31")
            //聊天界面呼叫.直接接听进入通话中界面
            ChatApp.getInstance().getDataRepository().answerCall()
            Log.e("hunluan", from + "-" + toUserid)
//            Toast.makeText(ChatApp.niceChatContext(), "007:" + from + "-" + toUserid, Toast.LENGTH_LONG).show()
            MainActivity.startCallIn(context, from!!, toUserid!!)
            EventBus.getDefault().post(CallOutgoingViewModel.MatchSuccess())
            isStartMatch = false
            isReceiveOk = false
            isCallComing = false
        }
    }

    class CallReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            // 拨打方username
            from = intent.getStringExtra("from")
            toUserid = intent.getStringExtra("to")
            type = intent.getStringExtra("type")
            EMLog.d("EMCallManager", "niushiqi-tonghua,接收到环信广播：from：" + from +" toUserid:"+ toUserid +" type: "+ type)
            isComing = true
            if ("voice" == type) {
                var callExt = EMClient.getInstance().callManager().currentCallSession.ext
                Timber.tag("niushiqi-tonghua").i("显示ext信息：from："+ from
                        +" toUserid:"+ toUserid
                        +" type: "+ type
                        +" ext: "+ callExt)
                if(callExt == "1v1") {
                    /**
                    * 一对一聊天界面
                    */
                    //mAcceptUserid = null
                    if (isCalling) {
                        //电话处于接通状态
                        EventBus.getDefault().post(
                                VoiceMessage(status = StatusTag.STATUS_CALL_IN, fromUserid = from!!,
                                        type = StatusTag.TYPE_VIDEO, toUserid = toUserid!!))
                    } else {
                        //电话处于挂断状态
                        Timber.tag("niushiqi-tonghua").i("环信CallReceiver，一对一聊天界面")
                        if (ActManagerUtils.getAppManager().currentActivity()!!::class.java == ConnetingActivity::class.java) {
                            Timber.tag("niushiqi-tonghua").i(ActManagerUtils.getAppManager().currentActivity().localClassName)
                            //聊天界面呼叫.直接接听进入通话中界面
                            ChatApp.getInstance().getDataRepository().answerCall()
                            //标志着通话成功
                            EventBus.getDefault().post(ChatViewModel.OpenConnetingActivityVM(toUserid!!, from!!, type!!))
                            //2018.12.4日过渡版本-通话建立成功直接开启树洞
                            MainActivity.startCallIn(context, from!!, toUserid!!)
                        } else {
                            //Toast.makeText(niceChatContext(), "聊天界面发出消息但此时不在聊天界面", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    /**
                     * 匹配界面
                     */
                    Timber.tag("niushiqi-pipei").i("环信CallReceiver，匹配界面 from:"+ from + " to:" + toUserid)
                    //ChatApp.getInstance().niceToast("收到环信呼叫 from:"+ from + " to:" + toUserid)
                    isCallComing = true
                    if (isReceiveOk) {
                        //聊天界面呼叫.直接接听进入通话中界面
                        ChatApp.getInstance().getDataRepository().answerCall()
                        Log.e("hunluan", from + "-" + toUserid)
                        MainActivity.startCallIn(context, from!!, toUserid!!)
                        EventBus.getDefault().post(CallOutgoingViewModel.MatchSuccess())
                        isStartMatch = false
                        isReceiveOk = false
                        isCallComing = false
                    }
                    //EventBus.getDefault().post(CallOutgoingViewModel.ReceiveEasemobCallingForPiPeiVM(from!!, toUserid!!))
                }
            } else {
                Toast.makeText(niceChatContext(), "开启视频分支", Toast.LENGTH_SHORT).show()
            }
        }
    }
}