package com.dyyj.idd.chatmore.model.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import com.dyyj.idd.chatmore.model.database.entity.HttpLogEntity;
import com.dyyj.idd.chatmore.model.database.entity.MQTTLogEntity;
import com.dyyj.idd.chatmore.model.database.entity.SocketLogEntity;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : 日志
 */
@Dao
public interface LogDao {

  /**
   * 插入http日志
   * @param logEntity
   */
  @Insert Long insert(HttpLogEntity logEntity);

  /**
   * 插入mqtt日志
   * @param logEntity
   */
  @Insert Long insert(MQTTLogEntity logEntity);

  /**
   * 插入socket日志
   * @param logEntity
   */
  @Insert Long insert(SocketLogEntity logEntity);
}
