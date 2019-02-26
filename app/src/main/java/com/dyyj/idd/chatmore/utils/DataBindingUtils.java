package com.dyyj.idd.chatmore.utils;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/04/28
 * desc   : 图片加载
 */
public class DataBindingUtils {

  /**
   * 图片加载
   */
  @BindingAdapter({ "app:imageUrl" }) public static void loadImage(ImageView view,
      String imageUrl) {
    Glide.with(view.getContext())
        .load(imageUrl)
        .placeholder(R.color.picBackground)
        .centerCrop()
        .crossFade()
        .into(view);
//    RequestOptions requestOption = new RequestOptions().centerCrop();
//    requestOption.error(R.color.picBackground).placeholder(R.color.picBackground);
//    Glide.with(view.getContext()).load(imageUrl).apply(requestOption).into(view);
  }

  /**
   * 加载头像
   */
  @BindingAdapter({"app:loadAvatar"})
  public static void loadAvatar(ImageView view,
      String imageUrl) {
    if (view == null || TextUtils.isEmpty(imageUrl)){
      return;
    }
    //Log.e("AvatarDataBindUt","Avatar="+ imageUrl);
    Glide.with(ChatApp.Companion.getInstance())
        .load(imageUrl)
        .asBitmap()
        .transform(new CropCircleTransformation(view.getContext()))
        .crossFade()
        .error(R.drawable.bg_circle_black)
        .placeholder(R.drawable.bg_circle_black)
        .into(view);
//    RequestOptions requestOption = new RequestOptions().circleCrop();
//    requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black);
//    Glide.with(ChatApp.Companion.getInstance()).load(imageUrl).apply(requestOption).into(view);
  }

  /**
   * 加载国旗
   * @param view
   * @param data
   */
  public static void loadCountry(ImageView view, byte[] data) {
    //加载头像旁边的国旗
      Glide.with(view.getContext()).load(data).crossFade().fitCenter().into(view);
//    RequestOptions requestOption = new RequestOptions().fitCenter();
//    Glide.with(view.getContext()).load(data).apply(requestOption).into(view);
  }
}
