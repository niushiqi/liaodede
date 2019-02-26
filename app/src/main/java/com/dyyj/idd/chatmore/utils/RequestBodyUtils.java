package com.dyyj.idd.chatmore.utils;

import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/03/10
 * desc   :
 */
public class RequestBodyUtils {
  private final static Gson mGson = new Gson();
  private final static String TYPE_JSON = "application/json";

  public static RequestBody getRequestBody(Object obj) {
    return RequestBody.create(MediaType.parse(TYPE_JSON), mGson.toJson(obj));
  }

  public static RequestBody getRequestBody(String obj) {
    return RequestBody.create(MediaType.parse(TYPE_JSON), obj);
  }

  /**
   * 获取多个文件
   * @param files
   * @return
   */
  public static List<MultipartBody.Part> getMultiparBodyListFormString(List<String> files) {

    List<MultipartBody.Part> mlist = new ArrayList<>();
    if (files == null || files.isEmpty()) {
      return mlist;
    }
    for (int i = 0 ; i< files.size(); i++) {
      String str = files.get(i);
      File file = new File(str);
      RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

      MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
      mlist.add(body);
    }
    return mlist;
  }

  public static List<MultipartBody.Part> getMultiparBodyListFormFile(List<File> files) {
    List<MultipartBody.Part> mlist = new ArrayList<>();
    if (files == null || files.isEmpty()) {
      return mlist;
    }
    for (int i = 0 ; i< files.size(); i++) {
      File file = files.get(i);
      RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

      MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
      mlist.add(body);
    }
    return mlist;
  }

  public static List<MultipartBody.Part> getPicMultiparBodyListFormFile(List<File> files) {
    List<MultipartBody.Part> mlist = new ArrayList<>();
    if (files == null || files.isEmpty()) {
      return mlist;
    }
    for (int i = 0 ; i< files.size(); i++) {
      File file = files.get(i);
      RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

      MultipartBody.Part body = MultipartBody.Part.createFormData("imgs", file.getName(), requestBody);
      mlist.add(body);
    }
    return mlist;
  }

  public static List<MultipartBody.Part> getPicMultiparBodyListFormFile(String name, List<File> files) {
    List<MultipartBody.Part> mlist = new ArrayList<>();
    if (files == null || files.isEmpty()) {
      return mlist;
    }
    for (int i = 0 ; i< files.size(); i++) {
      File file = files.get(i);
      RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

      MultipartBody.Part body = MultipartBody.Part.createFormData(name, file.getName(), requestBody);
      mlist.add(body);
    }
    return mlist;
  }

  public static List<MultipartBody.Part> getMultiparBodyListFormVideo(List<File> files) {
    List<MultipartBody.Part> mlist = new ArrayList<>();
    if (files == null || files.isEmpty()) {
      return mlist;
    }
    for (int i = 0 ; i< files.size(); i++) {
      File file = files.get(i);
      RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

      MultipartBody.Part body = MultipartBody.Part.createFormData("frame", file.getName(), requestBody);
      mlist.add(body);
    }
    return mlist;
  }
}
