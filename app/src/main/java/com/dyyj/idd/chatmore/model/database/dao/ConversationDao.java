package com.dyyj.idd.chatmore.model.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dyyj.idd.chatmore.model.database.entity.BodiesEntity;
import com.dyyj.idd.chatmore.model.database.entity.ConversationEntity;
import com.dyyj.idd.chatmore.model.database.entity.PayloadEntity;
import com.dyyj.idd.chatmore.model.database.entity.UnreadMessageEntity;

import java.util.List;

import io.reactivex.Flowable;

/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 会话列表
 *
 * @author yuhang
 */
@Dao public interface ConversationDao {

  /**
   * 插入消息
   */
  @Insert Long[] insert(ConversationEntity... conversationEntities);

  /**
   * 删除消息
   */
  @Delete void delete(ConversationEntity conversationEntity);

  /**
   * 插入消息类型
   */
  @Insert Long[] insert(BodiesEntity... bodiesEntity);

  /**
   * 消息主体
   */
  @Insert Long[] insert(PayloadEntity... payloadEntities);

  /**
   * 获取所有消息列表
   * toUserid:好友ID userid：自己ID
   */
  //@Query("SELECT * FROM conversationentity AS c WHERE (`from` = :toUserid AND `to` = :userid) OR (`to` = :toUserid AND `from` = :userid) ORDER BY c.timestamp ASC")
  @Query("SELECT * FROM conversationentity AS c WHERE (`direction`= 'RECEIVE' AND `from` = :toUserid AND `to` = :userid) OR (`direction` = 'SEND' AND `to` = :toUserid AND `from` = :userid) ORDER BY c.timestamp ASC")
  List<ConversationEntity> queryConversationEntityListByFromAndTo(final String toUserid,
      final String userid);

  /**
   * 查询最新联系人聊天记录(最新一条消息)
   * 备注：查询表中from或to为userid的记录
   */
  @Query("SELECT * FROM (SELECT `to` AS fid, * FROM ConversationEntity WHERE `from` = :userid AND `to` <> :userid UNION SELECT `from` AS fid, * FROM ConversationEntity WHERE `from` <> :userid AND `to` = :userid ) GROUP BY fid ORDER BY timestamp DESC")
  Flowable<List<ConversationEntity>> queryLastConversationEntityAllByFriend(final String userid);

  /**
   * 获取系统消息 V2.0
   * 备注：系统消息的fromUserId为2
   * @param userid
   * @return
   */
  @Query("SELECT * FROM conversationentity WHERE `from` = 2 AND `to` = :userid order by timestamp asc")
  List<ConversationEntity> querySystemMessageListByUserId(final String userid);

  /**
   * 获取系统消息
   * @param userid
   * @return
   */
  @Query("SELECT * FROM conversationentity WHERE `from` = 1 AND `to` = :userid order by timestamp desc limit 1")
  ConversationEntity querySystemMessage(final String userid);

  /**
   * 获取消息主体列表
   * conversationentity的id和payloadentity的conversationId相同时，返回payloadentity（EMMessage头部内容）
   */
  @Query(
      "SELECT * FROM payloadentity AS p INNER JOIN conversationentity AS c ON p.conversationId = c.id"
          + " WHERE c.id = :id LIMIT 1") PayloadEntity queryPayloadEntityList(final int id);

  /**
   * 获取消息类型
   * bodiesentity的payloadId和payloadentity的id相同，返回BodiesEntity（EMMessage消息内容）
   */
  @Query("SELECT * FROM bodiesentity AS b INNER JOIN payloadentity as p ON p.id = b.payloadId WHERE"
      + " p.conversationId = (SELECT c.id FROM conversationentity AS c WHERE c.id = :id)")
  List<BodiesEntity> queryBodiesEntityList(final int id);

  /**
   * 查询好友未读消息
   * 与好友发送或者接收的isRead为未读的消息
   */
  @Query("SELECT * FROM UnreadMessageEntity WHERE (`from` = :firendId OR `to` = :firendId) AND (`from` = :userid OR `to` = :userid) AND isRead = 0")
  List<UnreadMessageEntity> queryUnreadMessageEntityById(final String firendId,
      final String userid);

  /**
   * 查询系统未读消息
   * @param userid
   * @return
   */
  @Query("SELECT * FROM unreadmessageentity WHERE `from` = 1 AND `to` = :userid AND isRead = 0")
  List<UnreadMessageEntity> queryUnreadSystemMessageEntityByid(final String userid);

  /**
   * 获取所有未读消息
   */
  @Query("SELECT * FROM UnreadMessageEntity WHERE (`from` = :userid OR `to` = :userid) AND isRead = 0")
  Flowable<List<UnreadMessageEntity>> queryUnreadMessageEntityAll(String userid);

  /**
   * 获取所有未读消息
   */
  @Query("SELECT * FROM UnreadMessageEntity WHERE (`from` = :userid OR `to` = :userid) AND isRead = 0")
  List<UnreadMessageEntity> queryUnreadMessage(String userid);


  /**
   * 插入未读消息
   */
  @Insert Long insert(UnreadMessageEntity messageEntity);

  /**
   * 更新未读消息
   */
  @Query("UPDATE unreadmessageentity SET isRead = isRead + 1  WHERE `from` = :firendId OR `to` = :firendId")
  void update(final String firendId);

  /**
   * 删除好友消息记录
   */
  @Query("DELETE FROM conversationentity WHERE `from` = :id OR `to` = :id")
  void deleteConversationEntityById(final String id);
}
