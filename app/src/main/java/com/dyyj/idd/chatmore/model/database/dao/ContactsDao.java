package com.dyyj.idd.chatmore.model.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.dyyj.idd.chatmore.model.database.entity.ContactsEntity;
import java.util.List;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/05
 * desc   :
 */
@Dao public interface ContactsDao {

  /**
   * 插入好友
   */
  @Insert void insert(ContactsEntity contactsEntity);

  /**
   * 更新通讯录
   */
  @Update int update(ContactsEntity contactsEntity);

  /**
   * 查询当前用户所有好友
   */
  @Query("select * from contactsentity where userId = (select userId from accountentity order by localLastLoginTime desc limit 1)")
  List<ContactsEntity> queryContactsEntity();

  /**
   * 查询当前帐号指定好友
   * @param friendid
   * @return
   */
  @Query("select * from contactsentity where userId = (SELECT max(userId) from accountentity order by localLastLoginTime desc limit 1) and friendUserId = :friendid group by userId  limit 1")
  ContactsEntity queryContactsEntityByFriendid(String friendid);
}
