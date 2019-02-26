package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : 登陆的帐号表
 * @author yuhang
 */
@Entity(indices = @Index(value = { "userId" }, unique = true)) public class AccountEntity {
  @PrimaryKey(autoGenerate = true) public int id;

  /**
   * 用户id
   */
  public String userId;

  /**
   * 本地登陆时间(判断当前登陆的帐号)
   */
  public long localLastLoginTime;

  /**
   * 创建AccountEntity
   */
  public static AccountEntity createAccountEntity(UserInfoEntity data) {
    AccountEntity entity = new AccountEntity();
    entity.localLastLoginTime = System.currentTimeMillis();
    entity.userId = data.userId;
    return entity;
  }


}
