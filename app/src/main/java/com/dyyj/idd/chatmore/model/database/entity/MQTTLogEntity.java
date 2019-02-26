package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : MQTT请求日志
 * @author yuhang
 */
@Entity
public class MQTTLogEntity {
  public MQTTLogEntity() {
  }

  public MQTTLogEntity(String receive, String status, long timestamp) {
    this.receive = receive;
    this.status = status;
    this.timestamp = timestamp;
  }

  @PrimaryKey(autoGenerate = true) public int id;

  /**
   * 推送接收的json
   */
  public String receive;

  /**
   * mqtt连接状态(成功/失败/丢失连接)
   */
  public String status;

  /**
   * 接收消息时的时间戳
   */
  public long timestamp;

}
