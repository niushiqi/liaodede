package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;
import com.dyyj.idd.chatmore.model.network.result.ContactsResult;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/05
 * desc   : 好友列表
 */
@Entity public class ContactsEntity {
  @PrimaryKey(autoGenerate = true) public int id;

  /**
   * 当前用户id
   */
  public String userId;

  /**
   * 好友id
   */
  public String friendUserId;

  /**
   * 昵称
   */
  public String friendNickname;

  /**
   * 性别
   */
  public String friendGender;

  /**
   * 用户等级
   */
  public String friendUserLevel;

  /**
   * 是否vip
   */
  public String friendIsVip;

  /**
   * 头像
   */
  public String friendAvatar;

  /**
   * 友好度等级
   */
  public String friendshipLevel;

  /**
   * 年纪
   */
  public int friendAge;

  /**
   * 升级下一级百分比
   */
  public String friendshipExperience;

  /**
   * 奖励
   */
  public int newReward;

  /**
   * 通话状态
   */
  public int talkingStatus;

  public static ContactsEntity createContactsEntity(ContactsResult.Data.Friends result,
      String userid) {
    ContactsEntity entity = new ContactsEntity();
    entity.userId = userid;
    entity.friendUserId = result.getFriendUserId();
    entity.friendNickname = result.getFriendNickname();
    entity.friendGender = result.getFriendGender();
    entity.friendUserLevel = result.getFriendUserLevel();
    entity.friendIsVip = result.getFriendIsVip();
    entity.friendAvatar = result.getFriendAvatar();
    entity.friendshipLevel = result.getFriendshipLevel();
    entity.friendAge = result.getFriendAge();
    entity.friendshipExperience = result.getFriendshipExperience();
    entity.newReward = result.getNewReward();
    entity.talkingStatus = result.getTalkingStatus();
    return entity;
  }

  public ContactsEntity createContactsEntity(ContactsEntity entity) {

    if (!TextUtils.isEmpty(entity.friendUserId)) {
      this.friendUserId = entity.friendUserId;
    }
    if (!TextUtils.isEmpty(entity.friendNickname)) {
      this.friendNickname = entity.friendNickname;
    }
    if (!TextUtils.isEmpty(entity.friendGender)) {
      this.friendGender = entity.friendGender;
    }
    if (!TextUtils.isEmpty(entity.friendUserLevel)) {
      this.friendUserLevel = entity.friendUserLevel;
    }
    if (!TextUtils.isEmpty(entity.friendIsVip)) {
      this.friendIsVip= entity.friendIsVip;
    }
    if (!TextUtils.isEmpty(entity.friendAvatar)) {
      this.friendAvatar= entity.friendAvatar;
    }
    if (!TextUtils.isEmpty(entity.friendshipLevel)) {
      this.friendshipLevel= entity.friendshipLevel;
    }
    if (entity.friendAge > 0) {
      this.friendAge= entity.friendAge;
    }
    if (!TextUtils.isEmpty(entity.friendshipExperience)) {
      this.friendshipExperience= entity.friendshipExperience;
    }
    if (entity.newReward > 0) {
      this.newReward= entity.newReward;
    }
    if (entity.talkingStatus > 0) {
      this.talkingStatus= entity.talkingStatus;
    }
    return this;
  }
}
