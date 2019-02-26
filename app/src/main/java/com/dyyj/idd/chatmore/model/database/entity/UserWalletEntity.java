package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;
import com.dyyj.idd.chatmore.model.network.result.MainResult;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : 用户钱包
 * @author yuhang
 */
@Entity
public class UserWalletEntity {

  @PrimaryKey(autoGenerate = true)
  public int id;

  /**
   * 用户id
   */
  public String userId;

  /**
   * 金币
   */
  public String userCoin;

  /**
   * 冻结金币
   */
  public String userCoinFreeze;

  /**
   * 魔石
   */
  public String userStone;

  /**
   * 冻结魔石
   */
  public String userStoneFreeze;

  /**
   * 匹配开始以后消耗的魔石
   * 匹配失败需要返回
   */
  public String consumeStoneId;

  /**
   * 暂存金币
   */
  public String freezeCoin;

  /**
   * 现金
   */
  public String userCash;

  /**
   * 冻结现金
   */
  public String userCashFreeze;

  /**
   * 时间戳
   */
  public long timestamp;

  /**
   * 创建UserWalletEntity
   * @param data
   * @return
   */
  public static UserWalletEntity createUserWalletEntity(MainResult.Data data) {
    UserWalletEntity entity = new UserWalletEntity();
    if (!TextUtils.isEmpty(data.getUserWallet().getFreezeCoin())) {
      entity.freezeCoin = data.getUserWallet().getFreezeCoin();
    }
    if (!TextUtils.isEmpty(data.getUserWallet().getUserCoin())) {
      entity.userCoin = data.getUserWallet().getUserCoin();
    }
    if (!TextUtils.isEmpty(data.getUserWallet().getUserCoinFreeze())) {
      entity.userCoinFreeze = data.getUserWallet().getUserCoinFreeze();
    }
    if (!TextUtils.isEmpty(data.getUserWallet().getUserStone())) {
      entity.userStone = data.getUserWallet().getUserStone();
    }
    if (!TextUtils.isEmpty(data.getUserWallet().getUserStoneFreeze())) {
      entity.userStoneFreeze = data.getUserWallet().getUserStoneFreeze();
    }
    if (!TextUtils.isEmpty(data.getUserWallet().getUserCash())) {
      entity.userCash = data.getUserWallet().getUserCash();
    }
    if (!TextUtils.isEmpty(data.getUserWallet().getUserCashFreeze())) {
      entity.userCashFreeze = data.getUserWallet().getUserCashFreeze();
    }
    if (!TextUtils.isEmpty(data.getUser().getUserId())) {
      entity.userId = data.getUser().getUserId();
    }
    entity.timestamp = System.currentTimeMillis();
    return entity;
  }

  public UserWalletEntity createUserWalletEntity(UserWalletEntity entity) {
    if (!TextUtils.isEmpty(entity.freezeCoin)) {
      this.freezeCoin = entity.freezeCoin;
    }
    if (!TextUtils.isEmpty(entity.userCoin)) {
      this.userCoin = entity.userCoin;
    }
    if (!TextUtils.isEmpty(entity.userCoinFreeze)) {
      this.userCoinFreeze = entity.userCoinFreeze;
    }
    if (!TextUtils.isEmpty(entity.userStone)) {
      this.userStone = entity.userStone;
    }
    if (!TextUtils.isEmpty(entity.userStoneFreeze)) {
      this.userStoneFreeze = entity.userStoneFreeze;
    }
    if (!TextUtils.isEmpty(entity.userCash)) {
      this.userCash = entity.userCash;
    }
    if (!TextUtils.isEmpty(entity.userCashFreeze)) {
      this.userCashFreeze = entity.userCashFreeze;
    }
    if (entity.timestamp > 0) {
      this.timestamp = entity.timestamp;
    }
    if (!TextUtils.isEmpty(entity.userId)) {
      this.userId = entity.userId;
    }
    if (entity.id > 0) {
      this.id = entity.id;
    }
    return this;
  }
}
