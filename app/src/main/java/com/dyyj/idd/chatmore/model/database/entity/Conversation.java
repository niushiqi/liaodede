package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 最近会话
 * @author yuhang
 */
@Entity public class Conversation {
  @PrimaryKey(autoGenerate = true) public Long id;
  @ColumnInfo(name = "target_id") public String targetId;
  @ColumnInfo(name = "received_time") public long receivedTime;
  @ColumnInfo(name = "latest_message_id") public Integer latestMessageId;
  @ColumnInfo(name = "avatar") public String avatar;
  @ColumnInfo(name = "nickname") public String nickname;
  @ColumnInfo(name = "message") public String message;
  @ColumnInfo(name = "extra") public String extra;

  public Long getId() {
    return id;
  }

  public Conversation setId(Long id) {
    this.id = id;
    return this;
  }

  public String getTargetId() {
    return targetId;
  }

  public Conversation setTargetId(String targetId) {
    this.targetId = targetId;
    return this;
  }

  public long getReceivedTime() {
    return receivedTime;
  }

  public Conversation setReceivedTime(long receivedTime) {
    this.receivedTime = receivedTime;
    return this;
  }

  public Integer getLatestMessageId() {
    return latestMessageId;
  }

  public Conversation setLatestMessageId(Integer latestMessageId) {
    this.latestMessageId = latestMessageId;
    return this;
  }

  public String getAvatar() {
    return avatar;
  }

  public Conversation setAvatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

  public String getNickname() {
    return nickname;
  }

  public Conversation setNickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public Conversation setMessage(String message) {
    this.message = message;
    return this;
  }

  public String getExtra() {
    return extra;
  }

  public Conversation setExtra(String extra) {
    this.extra = extra;
    return this;
  }
}
