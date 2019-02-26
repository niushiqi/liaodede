package com.dyyj.idd.chatmore.utils;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.dyyj.idd.chatmore.R;
import org.jetbrains.annotations.NotNull;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/02/08
 * desc   : Toolbar统一处理
 * @author yuhang
 */
public class ToolbarUtils {

  public static void init(AppCompatActivity activity) {
    ActionBar supportActionBar = activity.getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayShowTitleEnabled(true);
      TextView titleView = activity.findViewById(R.id.toolbar_title_tv);
      titleView.setText(activity.getTitle());
      supportActionBar.setDisplayHomeAsUpEnabled(false);
    }
  }

  public static void init(AppCompatActivity activity, String title) {
    ActionBar supportActionBar = activity.getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayShowTitleEnabled(true);
      supportActionBar.setDisplayHomeAsUpEnabled(false);
      TextView titleView = activity.findViewById(R.id.toolbar_title_tv);
      View backView = activity.findViewById(R.id.toolbar_back_iv);
      if (backView != null) {
        backView.setVisibility(View.VISIBLE);
      }
      titleView.setText(title);
    }
  }

  /**
   * Fragment 初始化Toobar
   *
   * @param fragment 当前Fragment
   * @param toolbarId toobar id
   * @param title 标题
   */
  public static void init(@NotNull Fragment fragment, @NotNull @IdRes int toolbarId,
      @NotNull @StringRes int title) {
    AppCompatActivity mAppCompatActivity = (AppCompatActivity) fragment.getActivity();
    assert mAppCompatActivity != null;
    Toolbar toolbar = fragment.getView().findViewById(toolbarId);
    if (toolbar != null) {
      mAppCompatActivity.setSupportActionBar(toolbar);
      ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
      if (actionBar != null) {
        TextView titleView = toolbar.findViewById(R.id.toolbar_title_tv);
        titleView.setText(title);
        actionBar.setDisplayShowTitleEnabled(false);
      }
    }
  }

}
