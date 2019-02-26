package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/28
 * desc   : 主体
 *
 * @author yuhang
 */
@Entity(foreignKeys = @ForeignKey(entity = ConversationEntity.class, parentColumns = "id",
    childColumns = "conversationId", onDelete = CASCADE))
public class PayloadEntity {
  @PrimaryKey(autoGenerate = true) public int id;

  @Ignore public List<BodiesEntity> bodiesEntity;

  /**
   * 发送人username
   */
  @ColumnInfo(name = "from") public String from;

  /**
   * 接收人的username或者接收group的ID
   */
  @ColumnInfo(name = "to") public String to;

  /**
   * 透传参数
   */
  @ColumnInfo(name = "ext") public String ext;

  /**
          * 消息ID
   */
  public int conversationId;


}
