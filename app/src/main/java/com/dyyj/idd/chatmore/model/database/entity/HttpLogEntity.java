package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : http请求日志
 */
@Entity
public class HttpLogEntity {
  public HttpLogEntity() {
  }

  public HttpLogEntity(String url, String params, String result, long paramsTimestamp,
      long resultTimestamp) {
    this.url = url;
    this.params = params;
    this.result = result;
    this.paramsTimestamp = paramsTimestamp;
    this.resultTimestamp = resultTimestamp;
    this.intervalTime = String.valueOf((resultTimestamp - paramsTimestamp) % 60 / 60);
  }

  @PrimaryKey(autoGenerate = true) public int id;

  /**
   * 请求地址
   */
  public String url;

  /**
   * 请求参数
   */
  public String params;

  /**
   * 返回参数
   */
  public String result;

  /**
   * 请求时间戳
   */
  @ColumnInfo(name = "params_timestamp") public long paramsTimestamp;

  /**
   * 返回时间戳
   */
  @ColumnInfo(name = "result_timestamp") public long resultTimestamp;

  /**
   * 间距时间(秒)
   */
  @ColumnInfo(name = "interval_time") public String intervalTime;
}
