package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/29
 * desc   : 未读消息
 */
@Entity
public class UnreadMessageEntity {
  @PrimaryKey(autoGenerate = true)
  public int id;

  /**
   * 发送方
   */
  public String from;

  /**
   * 接收方
   */
  public String to;

  /**
   * 好友消息本地id
   */
  public int conversationId;

  /**
   * 消息是否已读
   * 1:已读
   * 0:未读
   */
  public int isRead = 0;
}
