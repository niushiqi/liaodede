package com.dyyj.idd.chatmore.model.database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;
import com.dyyj.idd.chatmore.model.database.entity.BodiesEntity;
import com.dyyj.idd.chatmore.model.database.entity.ConversationEntity;
import com.dyyj.idd.chatmore.model.database.entity.PayloadEntity;
import com.dyyj.idd.chatmore.model.easemob.EasemobManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 转换
 *
 * @author yuhang
 */
public class ConvertersFactory {
  @TypeConverter public static Date fromTimestamp(Long value) {
    return value == null ? null : new Date(value);
  }

  @TypeConverter public static Long dateToTimestamp(Date date) {
    return date == null ? null : date.getTime();
  }

  @TypeConverter public static String mapToString(Map<String, Object> map) {
    return (map == null || map.isEmpty()) ? null : new Gson().toJson(map);
  }

  @TypeConverter
  public static Map<String, String> stringToMap(String string) {
    if (TextUtils.isEmpty(string)) {
      return new HashMap<>();
    }
    Type type = new TypeToken<Map<String, String>>(){}.getType();
    return new Gson().fromJson(string, type);
  }



  @TypeConverter
  public static ConversationEntity EMMessageToconversationEntity(EMMessage msg) {
    ConversationEntity conversationEntity = new ConversationEntity();
    conversationEntity.chatType = msg.getChatType().name();
    conversationEntity.direction = msg.direct().name();
    conversationEntity.from = msg.getFrom();
    conversationEntity.to = msg.getTo();
    conversationEntity.msgId = msg.getMsgId();
    conversationEntity.timestamp = msg.getMsgTime();
    return conversationEntity;
  }

  public static EMMessage conversationEntityToEMMessage(ConversationEntity conversationEntity) {
    if (conversationEntity == null
        || conversationEntity.payload == null
        || conversationEntity.payload.bodiesEntity == null
        || conversationEntity.payload.bodiesEntity.isEmpty()) {
      return null;
    }
    String friend = conversationEntity.direction.equals(EMMessage.Direct.RECEIVE.name())?conversationEntity.from : conversationEntity.to;
    EMMessage message =
        EMMessage.createTxtSendMessage(conversationEntity.payload.bodiesEntity.get(0).msg, friend);
    Map<String, String> stringToMap = stringToMap(conversationEntity.payload.ext);
    Map<String, String> map = new HashMap<>(stringToMap.size());
    for (Map.Entry<String,String> entry : stringToMap.entrySet()){
      map.put(entry.getKey(), entry.getValue());
    }
    message.setAttribute(EasemobManager.EXT, new Gson().toJson(map));
    if (conversationEntity.direction.equals(EMMessage.Direct.RECEIVE.name())) {
      message.setDirection(EMMessage.Direct.RECEIVE);
    } else {
      message.setDirection(EMMessage.Direct.SEND);
    }
    message.setMsgTime(conversationEntity.timestamp);
    return message;
  }
  public static PayloadEntity EMMessageToPayloadEntity(EMMessage msg, int id) {
    PayloadEntity payloadEntity = new PayloadEntity();
    payloadEntity.from = msg.getFrom();
    payloadEntity.to = msg.getTo();
    payloadEntity.conversationId = id;
    try {
      payloadEntity.ext = msg.getStringAttribute(EasemobManager.EXT);
    } catch (HyphenateException e) {
      e.printStackTrace();
    }
    return payloadEntity;

  }

  public static BodiesEntity EMMessageToBodiesEntity(EMMessage msg, int id) {
    BodiesEntity bodiesEntity = new BodiesEntity();
    EMMessageBody body = msg.getBody();
    bodiesEntity.type = msg.getType().name();

    //文字
    if(body instanceof EMTextMessageBody) {
      EMTextMessageBody b = (EMTextMessageBody)body;
      bodiesEntity.msg = b.getMessage();
      bodiesEntity.payloadId = id;
      //透传参数
    } else if(body instanceof EMVoiceMessageBody) {
      EMVoiceMessageBody b = (EMVoiceMessageBody)body;
      bodiesEntity.filename = b.getFileName();
      bodiesEntity.secret = b.getSecret();
      bodiesEntity.url = b.getRemoteUrl();
      bodiesEntity.length = b.getLength();
    } else if(body instanceof EMVideoMessageBody) {
    } else if(body instanceof EMNormalFileMessageBody) {
    } else if(body instanceof EMLocationMessageBody) {
    } else if(body instanceof EMImageMessageBody) {
      //直接向环信发送图片实现
      //EMImageMessageBody b = (EMImageMessageBody)body;
      //bodiesEntity.filename = b.thumbnailLocalPath();
    } else if (body instanceof EMFileMessageBody) {

    }
    return bodiesEntity;
  }
}
