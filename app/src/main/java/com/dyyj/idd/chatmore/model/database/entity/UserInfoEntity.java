package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;
import com.dyyj.idd.chatmore.model.network.result.LoginResult;
import com.dyyj.idd.chatmore.model.network.result.MainResult;
import com.dyyj.idd.chatmore.model.network.result.UploadAvatarResult;
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/31
 * desc   : 用户信息表
 *
 * @author yuhang
 */
@Entity
public class UserInfoEntity {
  @PrimaryKey(autoGenerate = true) public int id;

  /**
   * 用户id
   */
  public String userId;

  /**
   * 手机号
   */
  public String mobile;

  /**
   * 注册时间
   */
  public String registerTime;

  /**
   * 最后登陆时间(服务器)
   */
  public String lastLoginTime;

  /**
   * 是否首次登陆
   */
  public int firstLogin;

  /**
   * 昵称
   */
  public String nickname;

  /**
   * 性别
   */
  public int gender;

  /**
   * 用户等级
   */
  public String userLevel;

  /**
   * 是否vip
   */
  public String isVip;

  /**
   * 生日
   */
  public String birthday;

  /**
   * 年纪
   */
  public String age;

  /**
   * 头像
   */
  public String avatar;

  /**
   * 头像文件地址
   */
  public String avatarFilename;

  /**
   * 实名姓名
   */
  public String name;

  /**
   * 实名认证状态
   */
  public String auth;

  /**
   * 职业
   */
  public String professionName;

  /**
   * 地区
   */
  public String area;

  /**
   * 详情地址
   */
  public String address;

  /**
   * 学校
   */
  public String school;

  /**
   * 用户名称
   */
  public String username;

  /**
   * token
   */
  public String token;

  public static UserInfoEntity createUserInfoEntity(MainResult.Data data) {
    UserInfoEntity entity = new UserInfoEntity();
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getNickname())) {
      entity.nickname = data.getUserBaseInfo().getNickname();
    }
    if (data.getUserBaseInfo().getGender() > 0) {
      entity.gender = data.getUserBaseInfo().getGender();
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getBirthday())) {
      entity.birthday = data.getUserBaseInfo().getBirthday();
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getAvatar())) {
      entity.avatar = data.getUserBaseInfo().getAvatar();
    }
    if (!TextUtils.isEmpty(data.getUser().getUserId())) {
      entity.userId = data.getUser().getUserId();
    }
    if (!TextUtils.isEmpty(data.getUser().getMobile())) {
      entity.mobile = data.getUser().getMobile();
    }
    if (!TextUtils.isEmpty(data.getUser().getRegisterTime())) {
      entity.registerTime = data.getUser().getRegisterTime();
    }
    if (!TextUtils.isEmpty(data.getUser().getLastLoginTime())) {
      entity.lastLoginTime = data.getUser().getLastLoginTime();
    }
    if (data.getUser().getFirstLogin() > 0) {
      entity.firstLogin = data.getUser().getFirstLogin();
    }
    return entity;
  }

  public static UserInfoEntity createUserInfoEntity(UserDetailInfoResult.Data data) {
    UserInfoEntity entity = new UserInfoEntity();
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getNickname())) {
      entity.nickname = data.getUserBaseInfo().getNickname();
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getUserId())) {
      entity.userId = data.getUserBaseInfo().getUserId();
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getGender())) {
      entity.gender = Integer.valueOf(data.getUserBaseInfo().getGender());
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getUserLevel())) {
      entity.userLevel = data.getUserBaseInfo().getUserLevel();
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().isVip())) {
      entity.isVip = data.getUserBaseInfo().isVip();
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getBirthday())) {
      entity.birthday = data.getUserBaseInfo().getBirthday();
    }
    if (data.getUserBaseInfo().getAge() > 0) {
      entity.age = String.valueOf(data.getUserBaseInfo().getAge());
    }
    if (!TextUtils.isEmpty(data.getUserBaseInfo().getAvatar())) {
      entity.avatar = data.getUserBaseInfo().getAvatar();
    }
    if (!TextUtils.isEmpty(data.getUser().getMobile())) {
      entity.mobile = data.getUser().getMobile();
    }
    if (!TextUtils.isEmpty(data.getUser().getRegisterTime())) {
      entity.registerTime = data.getUser().getRegisterTime();
    }
    if (!TextUtils.isEmpty(data.getUser().getLastLoginTime())) {
      entity.lastLoginTime = data.getUser().getLastLoginTime();
    }
    if (!TextUtils.isEmpty(data.getUser().getFirstLogin())) {
      entity.firstLogin = Integer.valueOf(data.getUser().getFirstLogin());
    }

    if (!TextUtils.isEmpty(data.getRealNameInfo().getName())) {
      entity.name = data.getRealNameInfo().getName();
    }
    if (!TextUtils.isEmpty(data.getRealNameInfo().getAuth())) {
      entity.auth = data.getRealNameInfo().getAuth();
    }

    if (!TextUtils.isEmpty(data.getUserExtraInfo().getProfessionName())) {
      entity.professionName = data.getUserExtraInfo().getProfessionName();
    }
    if (!TextUtils.isEmpty(data.getUserExtraInfo().getArea())) {
      entity.area = data.getUserExtraInfo().getArea();
    }
    if (!TextUtils.isEmpty(data.getUserExtraInfo().getAddress())) {
      entity.address = data.getUserExtraInfo().getAddress();
    }
    if (!TextUtils.isEmpty(data.getUserExtraInfo().getSchool())) {
      entity.school = data.getUserExtraInfo().getSchool();
    }
    if (!TextUtils.isEmpty(data.getUser().getToken())) {
      entity.token = data.getUser().getToken();
    }
    return entity;
  }

  public static UserInfoEntity createUserInfoEntity(UploadAvatarResult.Data data) {
    UserInfoEntity entity = new UserInfoEntity();
    if (!TextUtils.isEmpty(data.getAvatarFilename())) {
      entity.avatarFilename = data.getAvatarFilename();
    }
    return entity;
  }

  public static UserInfoEntity createUserInfoEntity(LoginResult.Data data) {
    UserInfoEntity entity = new UserInfoEntity();
    if (!TextUtils.isEmpty(data.getUsername())) {
      entity.username = data.getUsername();
    }
    if (!TextUtils.isEmpty(data.getUserId())) {
      entity.userId = data.getUserId();
    }
    if (!TextUtils.isEmpty(data.getMobile())) {
      entity.mobile = data.getMobile();
    }
    if (!TextUtils.isEmpty(data.getLastLoginTime())) {
      entity.lastLoginTime = data.getLastLoginTime();
    }
    if (!TextUtils.isEmpty(data.getFirstLogin())) {
      entity.firstLogin = Integer.valueOf(data.getFirstLogin());
    }
    if (!TextUtils.isEmpty(data.getToken())) {
      entity.token = data.getToken();
    }
    return entity;
  }

  public UserInfoEntity updateUserInfoEntity(UserInfoEntity entity) {

    if (!TextUtils.isEmpty(entity.nickname)) {
      this.nickname = entity.nickname;
    }
    if (!TextUtils.isEmpty(entity.userId)) {
      this.userId = entity.userId;
    }
    if (entity.gender > 0) {
      this.gender = entity.gender;
    }
    if (!TextUtils.isEmpty(entity.userLevel)) {
      this.userLevel = entity.userLevel;
    }
    if (!TextUtils.isEmpty(entity.isVip)) {
      this.isVip = entity.isVip;
    }
    if (!TextUtils.isEmpty(entity.birthday)) {
      this.birthday = entity.birthday;
    }
    if (!TextUtils.isEmpty(entity.age)) {
      this.age = entity.age;
    }
    if (!TextUtils.isEmpty(entity.avatar)) {
      this.avatar = entity.avatar;
    }

    if (!TextUtils.isEmpty(entity.mobile)) {
      this.mobile = entity.mobile;
    }
    if (!TextUtils.isEmpty(entity.registerTime)) {
      this.registerTime = entity.registerTime;
    }
    if (!TextUtils.isEmpty(entity.lastLoginTime)) {
      this.lastLoginTime = entity.lastLoginTime;
    }
    if (entity.firstLogin > 0) {
      this.firstLogin = entity.firstLogin;
    }

    if (!TextUtils.isEmpty(entity.name)) {
      this.name = entity.name;
    }
    if (!TextUtils.isEmpty(entity.auth)) {
      this.auth = entity.auth;
    }

    if (!TextUtils.isEmpty(entity.professionName)) {
      this.professionName = entity.professionName;
    }
    if (!TextUtils.isEmpty(entity.area)) {
      this.area = entity.area;
    }
    if (!TextUtils.isEmpty(entity.address)) {
      this.address = entity.address;
    }
    if (!TextUtils.isEmpty(entity.school)) {
      this.school = entity.school;
    }
    if (!TextUtils.isEmpty(entity.token)) {
      this.token = entity.token;
    }
    return this;
  }
}
