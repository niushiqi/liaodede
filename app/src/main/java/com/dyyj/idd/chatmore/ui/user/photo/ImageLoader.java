package com.dyyj.idd.chatmore.ui.user.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.ui.user.photo.transformation.CircleBitmapTransformation;
import com.dyyj.idd.chatmore.ui.user.photo.transformation.RotateTransformation;
import com.dyyj.idd.chatmore.ui.user.photo.transformation.RoundBitmapTransformation;

import java.io.File;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Desc: 封装glide图片加载，借助ImageLoaderConfig进行设置
 * Author :zhangcx
 * Data: 2018/6/6 11:21
 */
public class ImageLoader {
    //默认配置
    public static ImageLoadConfig defConfig = new ImageLoadConfig.Builder().
            setErrorResId(R.drawable.ic_error_network1_normal).//加载失败
            setPlaceHolderResId(R.drawable.ic_error_network1_normal).//占位
            setCropType(ImageLoadConfig.CENTER_CROP).
            setAsBitmap(true).
            setDiskCacheStrategy(ImageLoadConfig.DiskCacheType.SOURCE).
            setPrioriy(ImageLoadConfig.LoadPriorityType.HIGH).build();


    public static void loadGif(ImageView imageView, String path) {
        Glide.with(imageView.getContext()).load(path).asGif().into(imageView);
    }

    public static void loadStringRes(ImageView view, String imageUrl, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, imageUrl, config, listener);
    }

    public static void loadFile(ImageView view, File file, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, file, config, listener);
    }

    public static void loadResId(ImageView view, Integer resourceId, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, resourceId, config, listener);
    }

    public static void loadUri(ImageView view, String uri, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, uri, config, listener);
    }

    public static void load(ImageView view, Object url) {
        load(view.getContext(), view, url, null, null);
    }

    public static void load(ImageView view, Object url, int place) {
        defConfig.setPlaceHolderResId(place);
        load(view.getContext(), view, url, null, null);
        defConfig.setPlaceHolderResId(R.drawable.ic_error_network1_normal);
    }

    /**
     * 加载图片，带监听
     */
    public static void load(ImageView view, Object url, LoaderListener listener) {
        load(view.getContext(), view, url, null, listener);
    }

    public static void loadPicCorner(ImageView view, Object url, int corner) {
        defConfig.setPlaceHolderResId(R.drawable.ic_error_network1_normal);
        defConfig.setRoundedCorners(true);
        defConfig.setRoundedCornerSize(corner);
        load(view.getContext(), view, url, null, null);
        defConfig.setRoundedCorners(false);
        defConfig.setRoundedCornerSize(0);
    }


    public static void loadPicCorner(ImageView view, Object url) {
        defConfig.setRoundedCorners(true);
        defConfig.setRoundedCornerSize(12);
        load(view.getContext(), view, url, null, null);
        defConfig.setRoundedCorners(false);
        defConfig.setRoundedCornerSize(0);
    }

    public static void loadPicCircle(ImageView view, Object url) {
        defConfig.setCropCircle(true);
        load(view.getContext(), view, url, null, null);
        defConfig.setCropCircle(false);
    }

    public static void loadHead(ImageView view, Object url) {
        defConfig.setPlaceHolderResId(R.drawable.ic_message_system_head);
        defConfig.setCropCircle(true);
        load(view.getContext(), view, url, null, null);
        defConfig.setCropCircle(false);
        defConfig.setPlaceHolderResId(R.drawable.ic_error_network1_normal);
    }

    /**
     * @param view  iamgeview
     * @param url   图片
     * @param place 默认站位图
     */
    public static void loadPicCircle(ImageView view, Object url, int place) {
        defConfig.setPlaceHolderResId(place);
        defConfig.setCropCircle(true);
        load(view.getContext(), view, url, null, null);
        defConfig.setCropCircle(false);
    }

    public static void loadGif(ImageView view, Object gifUrl, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, gifUrl, ImageLoadConfig.parseBuilder(config).setAsGif(true).build(), listener);
    }

    public static void loadTarget(Context context, Object objUrl, ImageLoadConfig config, final LoaderListener listener) {
        load(context, null, objUrl, config, listener);
    }

    /**
     * 加载图片
     *
     * @param objUrl   <br>加载String类型的资源
     *                 <br>SD卡资源："file://"+ Environment.getExternalStorageDirectory().getPath()+"/test.jpg"<p/>
     *                 <br>assets资源："file:///android_asset/f003.gif"<p/>
     *                 <br>raw资源："Android.resource://com.frank.glide/raw/raw_1"或"android.resource://com.frank.glide/raw/"+R.raw.raw_1<p/>
     *                 <br>drawable资源："android.resource://com.frank.glide/drawable/news"或load"android.resource://com.frank.glide/drawable/"+R.drawable.news<p/>
     *                 <br>ContentProvider资源："content://media/external/images/media/139469"<p/>
     *                 <br>http资源："http://img.my.csdn.net/uploads/201508/05/1438760757_3588.jpg"<p/>
     *                 <br>https资源："https://img.alicdn.com/tps/TB1uyhoMpXXXXcLXVXXXXXXXXXX-476-538.jpg_240x5000q50.jpg_.webp"<p/>
     *                 <br>
     * @param config   各种配置信息
     * @param listener
     */
    private static void load(Context context, ImageView view, Object objUrl, ImageLoadConfig config, final LoaderListener listener) {
//        objUrl = "http://img.tuku.cn/file_big/201503/55b7341dbe084fa6b995a33f2407c0e0.jpg";
        if (null == config) {
            config = defConfig;
        }
        if (null == objUrl) {
            objUrl = "";
        }

        try {
            GenericRequestBuilder builder = null;
            if (config.isAsGif()) {
                //gif类型
                GifRequestBuilder request = Glide.with(context).load(objUrl).asGif();
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    request.centerCrop();
                } else {
                    request.fitCenter();
                }
                builder = request;


            } else if (config.isAsBitmap()) {
                //bitmap 类型

                BitmapRequestBuilder request = Glide.with(context).load(objUrl).asBitmap();
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    request.centerCrop();
                } else {
                    request.fitCenter();
                }
                //transform bitmap
                if (config.isRoundedCorners()) {
                    request.transform(new RoundBitmapTransformation(context, config.getRoundedCornerSize()));
                } else if (config.isCropCircle()) {
                    request.transform(new CircleBitmapTransformation(context));
                } else if (config.isGrayscale()) {
                    request.transform(new GrayscaleTransformation(context));
                } else if (config.isRotate()) {
                    request.transform(new RotateTransformation(context, config.getRotateDegree()));
                }
                builder = request;

            } else if (config.isCrossFade()) {
                // 渐入渐出动画
                DrawableRequestBuilder request = Glide.with(context).load(objUrl).crossFade();
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    request.centerCrop();
                } else {
                    request.fitCenter();
                }
                builder = request;
            }


            //缓存设置
            builder.diskCacheStrategy(config.getDiskCacheStrategy().getStrategy())
                    .skipMemoryCache(config.isSkipMemoryCache())
                    .priority(config.getPrioriy().getPriority());

            builder.dontAnimate();

            //设置tag，默认是图片资源地址
            if (null != config.getTag()) {
                builder.signature(new StringSignature(config.getTag()));
            } else {
                builder.signature(new StringSignature(objUrl.toString()));
            }

            //指定数学动画
            if (null != config.getAnimator()) {
                builder.animate(config.getAnimator());
            } else if (null != config.getAnimResId()) {
                builder.animate(config.getAnimResId());
            }


            //指定缩略图的比例
            if (config.getThumbnailRadio() > 0.0f) {
                builder.thumbnail(config.getThumbnailRadio());
            }

            //指定加载错误显示的图片
            if (null != config.getErrorResId()) {
                builder.error(config.getErrorResId());
            }

            //指定默认未加载显示的图片
            if (null != config.getPlaceHolderResId()) {
                builder.placeholder(config.getPlaceHolderResId());
            }

            //指定图片的宽、高
            if (null != config.getSize()) {
                builder.override(config.getSize().getWidth(), config.getSize().getHeight());
            }
            //指定图片加载过程的监听器
            if (null != listener) {
                setListener(builder, listener);
            }
            //指定缩略图
            if (null != config.getThumbnailUrl()) {
                BitmapRequestBuilder thumbnailRequest = Glide.with(context).load(config.getThumbnailUrl()).asBitmap();
                builder.thumbnail(thumbnailRequest).into(view);
            } else {
                setTargetView(builder, config, view);
            }


        } catch (Exception e) {
//            view.setImageResource(config.getErrorResId());
        }
    }

    private static void setListener(GenericRequestBuilder request, final LoaderListener listener) {
        request.listener(new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                if (!e.getMessage().equals("divide by zero")) {
                    listener.onError();
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                listener.onSuccess();
                return false;
            }
        });
    }

    private static void setTargetView(GenericRequestBuilder request, ImageLoadConfig config, ImageView view) {
        //set targetView
        if (null != config.getSimpleTarget()) {
            request.into(config.getSimpleTarget());
        } else if (null != config.getViewTarget()) {
            request.into(config.getViewTarget());
        } else if (null != config.getNotificationTarget()) {
            request.into(config.getNotificationTarget());
        } else if (null != config.getAppWidgetTarget()) {
            request.into(config.getAppWidgetTarget());
        } else {
            request.into(view);
        }
    }


    /**
     * 取消所有正在下载或等待下载的任务。
     */
    public static void cancelAllTasks(Context context) {
        Glide.with(context).pauseRequests();
    }

    /**
     * 恢复所有任务
     */
    public static void resumeAllTasks(Context context) {
        Glide.with(context).resumeRequests();
    }

    /**
     * 清除磁盘缓存
     */
    public static void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

    /**
     * 清除所有缓存
     */
    public static void cleanAll(Context context) {
        clearDiskCache(context);
        Glide.get(context).clearMemory();
    }

    /**
     * 清除内存缓存
     */
    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }


    public static interface LoaderListener {

        void onSuccess();

        void onError();
    }

    public static interface BitmapLoadingListener {

        void onSuccess(Bitmap b);

        void onError();
    }

}