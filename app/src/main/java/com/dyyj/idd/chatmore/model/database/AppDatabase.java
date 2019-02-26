package com.dyyj.idd.chatmore.model.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import com.dyyj.idd.chatmore.model.database.dao.ContactsDao;
import com.dyyj.idd.chatmore.model.database.dao.ConversationDao;
import com.dyyj.idd.chatmore.model.database.dao.LogDao;
import com.dyyj.idd.chatmore.model.database.dao.UserDao;
import com.dyyj.idd.chatmore.model.database.entity.AccountEntity;
import com.dyyj.idd.chatmore.model.database.entity.BodiesEntity;
import com.dyyj.idd.chatmore.model.database.entity.ContactsEntity;
import com.dyyj.idd.chatmore.model.database.entity.ConversationEntity;
import com.dyyj.idd.chatmore.model.database.entity.HttpLogEntity;
import com.dyyj.idd.chatmore.model.database.entity.MQTTLogEntity;
import com.dyyj.idd.chatmore.model.database.entity.PayloadEntity;
import com.dyyj.idd.chatmore.model.database.entity.SocketLogEntity;
import com.dyyj.idd.chatmore.model.database.entity.SystemMessageEntity;
import com.dyyj.idd.chatmore.model.database.entity.UnreadMessageEntity;
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity;
import com.dyyj.idd.chatmore.model.database.entity.UserWalletEntity;

/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 数据库
 *
 * @author yuhang
 */
@Database(entities = {
    ConversationEntity.class, PayloadEntity.class, BodiesEntity.class, UnreadMessageEntity.class,
    HttpLogEntity.class, MQTTLogEntity.class, SocketLogEntity.class, UserWalletEntity.class,
    AccountEntity.class, UserInfoEntity.class, SystemMessageEntity.class, ContactsEntity.class
}, version = 24) @TypeConverters({ ConvertersFactory.class }) public abstract class AppDatabase
    extends RoomDatabase {
  /**
   * 数据库名称
   */
  private static final String DATABASE_NAME = "chatmore";

  private static AppDatabase INSTANCE;
  private static final Object sLock = new Object();

  public synchronized static AppDatabase getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE =
          Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
              .allowMainThreadQueries()
              .fallbackToDestructiveMigration()
              .build();
    }
    return INSTANCE;
  }

  /**
   * 最近会话列表Dao
   */
  public abstract ConversationDao conversationDao();

  /**
   * 日志记录Dao
   */
  public abstract LogDao logDao();

  /**
   * 用户信息dao
   */
  public abstract UserDao userDao();

  /**
   * 好友列表
   * @return
   */
  public abstract ContactsDao contactsDao();
}
