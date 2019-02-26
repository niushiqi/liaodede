package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/28
 * desc   : 聊天记录
 * @author yuhang
 */
@Entity
public class ConversationEntity {
  @PrimaryKey(autoGenerate = true)
  public int id;

  /**
   * 消息ID
   */
  @ColumnInfo(name = "msg_id")
  public String msgId;

  /**
   * 消息发送时间
   */
  public long timestamp;

  /**
   * 方向呼入/呼出
   */
  public String direction;

  /**
   * 发送人username
   */
  public String from;

  /**
   * 接收人的username或者接收group的ID
   */
  public String to;

  /**
   * 用来判断单聊还是群聊。chat: 单聊；groupchat: 群聊
   */
  @ColumnInfo(name = "chat_type")
  public String chatType;

  @Ignore
  public PayloadEntity payload;



}
