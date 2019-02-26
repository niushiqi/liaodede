package com.dyyj.idd.chatmore.model.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/28
 * desc   : 消息具体类型
 */
@Entity(foreignKeys = @ForeignKey(entity = PayloadEntity.class, parentColumns = "id", childColumns = "payloadId", onDelete = CASCADE))
public class BodiesEntity {
  @PrimaryKey(autoGenerate = true)
  public int id;

  /**
   * 消息内容
   */

  public String msg;

  /**
   * Payload外键
   */

  public int payloadId;

  /**
   * 消息类型:
   * txt = 文本消息类型
   * loc = 位置消息类型
   * img = 图片消息类型
   * audio = 语音消息类型
   * video = 视频消息类型
   * file = 文件消息类型
   */

  public String type;

  /**
   * 图片附件大小（单位：字节）
   * 语音附件大小（单位：字节）
   * 视频附件大小（单位：字节）
   * 文件附件大小（单位：字节）
   */
  @ColumnInfo(name = "file_length")
  public long fileLength;

  /**
   * 图片名称
   * 语音名称
   * 视频文件名称
   * 视频文件名称
   */

  public String filename;

  /**
   * secret在上传图片后会返回，只有含有secret才能够下载此图片
   * secret在上传文件后会返回
   * secret在上传文件后会返回
   * secret在上传文件后会返回
   */

  public String secret;

  /**
   * 图片尺寸
   * 视频缩略图尺寸
   */

  public String size;

  /**
   * 上传图片消息地址，在上传图片成功后会返回UUID
   * 上传语音远程地址，在上传语音后会返回UUID
   * 上传视频远程地址，在上传视频后会返回UUID
   * 上传文件远程地址，在上传文件后会返回UUID
   */

  public String url;

  /**
   * 要发送的地址
   */

  public String addr;

  /**
   * 纬度
   */

  public double lat;

  /**
   * 经度
   */

  public double lng;

  /**
   * 语音时间（单位：秒）
   * 视频播放长度
   */

  public long length;

  /**
   * 上传视频缩略图远程地址，在上传视频缩略图后会返回UUID
   */

  public String thumb;

  /**
   * secret在上传缩略图后会返回
   */
  @ColumnInfo(name = "thumb_secret")
  public String thumbSecret;


}
