package com.dyyj.idd.chatmore.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.network.result.StatusResult;
import com.dyyj.idd.chatmore.viewmodel.OpenCallViewModel;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMWeb;
import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ShareUtils {

    public static void shareSmall(Activity context, String inviteCode, String title, String des, String path, String name, Bitmap icon) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMMin umMin = new UMMin("http://www.liaodede.com:8083");
        //兼容低版本的网页链接
        UMImage image = new UMImage(context, icon);
        umMin.setThumb(image);
        // 小程序消息封面图片
        umMin.setTitle(title);
        // 小程序消息title
        umMin.setDescription(des);
        // 小程序消息描述
//        umMin.setPath("pages/page10007/xxxxxx");
        umMin.setPath(path);
        //小程序页面路径
//        umMin.setUserName("gh_xxxxxxxxxxxx");
        umMin.setUserName(name);
        // 小程序原始id,在微信平台查询
        new ShareAction(context)
                .withMedia(umMin)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消分享", Toast.LENGTH_LONG).show();
                    }
                }).share();
    }

    public static void sharePicToWEIXIN(Activity context, Bitmap bitmap) {
        UMImage thumb = new UMImage(context, bitmap);
        UMImage thumb2 = new UMImage(context, bitmap);
        thumb.setThumb(thumb2);
        new ShareAction(context)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .withText("邀请码")
                .withMedia(thumb)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                }).share();
    }

    public static void sharePicToQQ(Activity context, Bitmap bitmap) {
        UMImage thumb = new UMImage(context, bitmap);
        UMImage thumb2 = new UMImage(context, bitmap);
        thumb.setThumb(thumb2);
        new ShareAction(context)
                .setPlatform(SHARE_MEDIA.QQ)
                .withText("邀请码")
                .withMedia(thumb)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                }).share();
    }

    public static void sharePicToWEIBO(Activity context, Bitmap bitmap) {
        UMImage thumb = new UMImage(context, bitmap);
        UMImage thumb2 = new UMImage(context, bitmap);
        thumb.setThumb(thumb2);
        new ShareAction(context)
                .setPlatform(SHARE_MEDIA.SINA)
                .withText("#聊得得#跟我一起来玩赚聊得得！新人注册就送红包！聊天打游戏做任务，就能领红包！可以提现！")
                .withMedia(thumb)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                }).share();
    }

    public static void shareWebToWEIXIN(Activity context, String inviteCode, String webUrl, String title, String icon, boolean isCircle) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
//        UMImage thumb = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wx_icon));
        UMImage thumb = new UMImage(context, icon);
        web.setThumb(thumb);  //缩略图
        web.setDescription("聊得得");//描述
        new ShareAction(context)
                .setPlatform(isCircle ? SHARE_MEDIA.WEIXIN_CIRCLE : SHARE_MEDIA.WEIXIN)//传入平台
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                })//回调监听器
                .share();
    }

    public static void shareWebToQQ(Activity context, String inviteCode, String webUrl, String title, String icon, boolean isCircle) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
//        UMImage thumb = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wx_icon));
        UMImage thumb = new UMImage(context, icon);
        web.setThumb(thumb);  //缩略图
        web.setDescription("聊得得");//描述
        new ShareAction(context)
                .setPlatform(isCircle ? SHARE_MEDIA.QZONE : SHARE_MEDIA.QQ)//传入平台
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                })//回调监听器
                .share();
    }

    public static void shareWebToWEIBO(Activity context, String inviteCode, String webUrl, String title, String icon, boolean isCircle) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
//        UMImage thumb = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wx_icon));
        UMImage thumb = new UMImage(context, icon);
        web.setThumb(thumb);  //缩略图
        web.setDescription("#聊得得#跟我一起来玩赚聊得得！新人注册就送红包！聊天打游戏做任务，就能领红包！可以提现！");//描述
        new ShareAction(context)
                .setPlatform(isCircle ? SHARE_MEDIA.SINA : SHARE_MEDIA.SINA)//传入平台
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                })//回调监听器
                .share();
    }

    public static void shareWeb3ToWEIXIN(Activity context, String inviteCode, String webUrl, String title, String icon, boolean isCircle) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
        //        UMImage thumb = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.i3c_wx_icon));
        UMImage thumb = new UMImage(context, icon);
        web.setThumb(thumb);  //缩略图
        web.setDescription("聊得得");//描述
        new ShareAction(context)
                .setPlatform(isCircle ? SHARE_MEDIA.WEIXIN_CIRCLE : SHARE_MEDIA.WEIXIN)//传入平台
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        EventBus.getDefault().post(new OpenCallViewModel.ShareStatusVM(true));
                        //  Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        EventBus.getDefault().post(new OpenCallViewModel.ShareStatusVM(false));
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                })//回调监听器
                .share();
    }

    public static void shareWeb3ToQQ(Activity context, String inviteCode, String webUrl, String title, String icon, boolean isCircle) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
        //        UMImage thumb = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.i3c_wx_icon));
        UMImage thumb = new UMImage(context, icon);
        web.setThumb(thumb);  //缩略图
        web.setDescription("聊得得");//描述
        new ShareAction(context)
                .setPlatform(isCircle ? SHARE_MEDIA.QZONE : SHARE_MEDIA.QQ)//传入平台
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        EventBus.getDefault().post(new OpenCallViewModel.ShareStatusVM(true));
                        //  Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        EventBus.getDefault().post(new OpenCallViewModel.ShareStatusVM(false));
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                })//回调监听器
                .share();
    }

    public static void shareWeb3ToWEIBO(Activity context, String inviteCode, String webUrl, String title, String icon, boolean isCircle) {
        Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().uploadShare(inviteCode).subscribe(new Consumer<StatusResult>() {
            @Override
            public void accept(StatusResult statusResult) throws Exception {

            }
        });
        new CompositeDisposable().add(disposable);
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
        //        UMImage thumb = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.i3c_wx_icon));
        UMImage thumb = new UMImage(context, icon);
        web.setThumb(thumb);  //缩略图
        web.setDescription("#聊得得#跟我一起来玩赚聊得得！新人注册就送红包！聊天打游戏做任务，就能领红包！可以提现！");//描述
        new ShareAction(context)
                .setPlatform(isCircle ? SHARE_MEDIA.SINA : SHARE_MEDIA.SINA)//传入平台
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    /**
                     * @descrption 分享开始的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    /**
                     * @descrption 分享成功的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        EventBus.getDefault().post(new OpenCallViewModel.ShareStatusVM(true));
                        //  Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享成功", Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享失败的回调
                     * @param platform 平台类型
                     * @param t 错误原因
                     */
                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        EventBus.getDefault().post(new OpenCallViewModel.ShareStatusVM(false));
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    /**
                     * @descrption 分享取消的回调
                     * @param platform 平台类型
                     */
                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(ChatApp.Companion.getInstance().getApplicationContext(), "取消了", Toast.LENGTH_LONG).show();
                    }
                })//回调监听器
                .share();
    }

    public static void init(Activity context, String webUrl, String title, String icon){
        UMWeb  web = new UMWeb(webUrl);
        UMImage thumb = new UMImage(context, icon);
        web.setTitle(title);//标题
        web.setThumb(thumb);  //缩略图
        new ShareAction(context)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .withMedia(web)
                .share();
    }


}
