package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/02
 * desc   : 系统消息
 */
@Entity(foreignKeys = @ForeignKey(entity = AccountEntity.class, parentColumns = "userId", childColumns = "userId",onDelete = CASCADE))
public class SystemMessageEntity {
  @PrimaryKey(autoGenerate = true)
   public int id;

  /**
   * 用户id
   */
  public String userId;

  /**
   * 系统消息数量
   */
  public int count;
}
