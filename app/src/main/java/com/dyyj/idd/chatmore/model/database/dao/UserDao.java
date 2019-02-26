package com.dyyj.idd.chatmore.model.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.dyyj.idd.chatmore.model.database.entity.AccountEntity;
import com.dyyj.idd.chatmore.model.database.entity.SystemMessageEntity;
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity;
import com.dyyj.idd.chatmore.model.database.entity.UserWalletEntity;
import io.reactivex.Flowable;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : 用户(钱包/用户信息/登陆帐号)
 *
 * @author yuhang
 */
@Dao public interface UserDao {

  /**
   * 插入用户信息
   */
  @Insert void insert(UserInfoEntity userInfoEntity);

  /**
   * 插入用户钱包
   */
  @Insert void insert(UserWalletEntity userWalletEntity);

  /**
   * 插入当前登陆用户
   */
  @Insert void insert(AccountEntity accountEntity);

  /**
   * 根据用户id查找用户
   */
  @Query("SELECT * FROM accountentity WHERE userId = :userid LIMIT 1 ")
  AccountEntity queryAccountEntity(String userid);

  /**
   * 获取最后一次登陆的用户数据
   */
  @Query("SELECT * FROM accountentity ORDER BY localLastLoginTime DESC LIMIT 1")
  AccountEntity queryAccountEntityLast();

  /**
   * 更新帐号信息
   */
  @Update void update(AccountEntity accountEntity);

  /**
   * 查询上一次登陆的用户信息
   */
  @Query("SELECT * FROM userinfoentity AS userinfo WHERE userId = (SELECT max(userId) FROM accountentity ORDER BY localLastLoginTime DESC LIMIT 1) LIMIT 1")
  Flowable<UserInfoEntity> queryUserInfoEntityListRx();

  /**
   * 查询上一次登陆的用户信息
   */
  @Query("SELECT * FROM userinfoentity AS userinfo WHERE userId = (SELECT max(userId) FROM accountentity ORDER BY localLastLoginTime DESC LIMIT 1) LIMIT 1")
  UserInfoEntity queryUserInfoEntityList();

  /**
   * 更新用户信息表
   */
  @Update int update(UserInfoEntity entity);

  /**
   * 查询我的钱包
   */
  @Query("SELECT * FROM userwalletentity WHERE userId = :userid LIMIT 1")
  UserWalletEntity queryUserWalletEntity(String userid);

  /**
   * 查询消耗的魔石(默认获取最后一条)
   */
  @Query("select * from userwalletentity where userId = :userid and consumeStoneId <> null order by timestamp desc limit 1")
  UserWalletEntity queryConsumeStone(String userid);

  /**
   * 更新我的钱包
   */
  @Update void udpate(UserInfoEntity entity);

  /**
   * 更新我的钱包
   */
  @Update void update(UserWalletEntity entity);

  /**
   * 删除我的钱包
   */
  @Query("DELETE FROM userwalletentity WHERE userId = :userid") void deleteUserWalletEntity(
      String userid);

  /**
   * 删除用户详情
   */
  @Query("DELETE FROM userinfoentity WHERE userId = :userid") void deleteUserInfoEntity(
      String userid);

  /**
   * 删除用户详情
   * @param entity
   */
  @Delete void delete(UserInfoEntity entity);

  /**
   * 删除我的钱包
   * @param entity
   */
  @Delete void delete(UserWalletEntity entity);

  /**
   * 删除帐号
   */
  @Query("DELETE FROM accountentity WHERE userId = :userid") void deleteAccountEntity(
      String userid);

  /**
   *  删除帐号
   * @param entity
   */
  @Delete void delete(AccountEntity entity);

  /**
   * 删除系统消息
   */
  @Query("DELETE FROM systemmessageentity WHERE userId = :userid") void deleteSystemMessageEntity(
      String userid);

  /**
   * 删除系统消息
   * @param entity
   */
  @Delete void delete(SystemMessageEntity entity);
  /**
   * 插入消息系统
   */
  @Insert void insert(SystemMessageEntity entity);

  /**
   * 查询系统消息
   */
  @Query("SELECT * FROM systemmessageentity WHERE userId = (SELECT max(userId) FROM accountentity ORDER BY localLastLoginTime DESC LIMIT 1)")
  SystemMessageEntity querySystemMessageEntity();

  /**
   * 更新系统消息
   */
  @Query("UPDATE systemmessageentity SET count = count +1 WHERE userId = (SELECT max(userId) FROM accountentity ORDER BY localLastLoginTime DESC LIMIT 1)")
  int updateSystemMessageEntity();
}
