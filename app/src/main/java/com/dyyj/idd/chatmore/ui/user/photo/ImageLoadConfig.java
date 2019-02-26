/**
  * desc
  * @author  zhangcx
  * 2018/12/25 9:19 PM
  */
package com.dyyj.idd.chatmore.ui.user.photo;

import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * Desc: 图片加载的一些配置
 * Author :zhangcx
 * Data: 2018/6/6 11:20
 */
public class ImageLoadConfig {
    /**
     * 剪切图片，显示图片中心区域在ImageView
     */
    public static final int CENTER_CROP = 0;
    public static final int FIT_CENTER = 1;
    /**
     * 裁剪类型,默认为中部裁剪
     */
    private int cropType = CENTER_CROP;

    /**
     * 默认ImageView显示的图片资源id
     */
    private Integer placeHolderResId;
    /**
     * 图片加载错误时显示的资源id
     */
    private Integer errorResId;

    /**
     * 是否淡入淡出动画
     */
    private boolean crossFade;
    /**
     * 淡入淡出动画持续的时间
     */
    private int crossDuration;

    /**
     * 设置加载的图片的宽、高尺寸
     */
    private OverrideSize size;

    /**
     * 是否当作gif方式显示，为true时表示强制显示的是gif动画,如果url不是gif的资源,那么会回调error()
     */
    private boolean asGif;
    /**
     * 是否当作bitmap方式显示，Glide 也可以加载 Gif 或 video，
     * 为true时表示强制显示为常规图片,如果是gif资源,则加载第一帧作为图片
     */
    private boolean asBitmap;

    /**
     * 是否跳过内存进行缓存，true时表示跳过，默认false
     */
    private boolean skipMemoryCache = false;
    /**
     * 硬盘缓存方式,默认为all类型
     */
    private DiskCacheType diskCacheStorage = DiskCacheType.ALL;

    /**
     * 加载优先级的方式
     */
    private LoadPriorityType priority;

    /**
     * 设置缩略图的缩放比例，范围是0.0f-1.0f
     */
    private float thumbnailRadio;
    /**
     * 设置缩略图的url地址,如果缩略图比全尺寸图先加载完，就显示缩略图，否则就不显示
     */
    private String thumbnailUrl;

    /**
     * 指定simpleTarget对象,可以在Target回调方法中处理bitmap,同时该构造方法中还可以指定宽、高
     */
    private SimpleTarget<Bitmap> simpleTarget;
    /**
     * 指定ViewTarget对象,可以是自定义View,该构造方法传入的View和通配符的view是同一个
     */
    private ViewTarget<? extends View, GlideDrawable> viewTarget;
    /**
     * 设置通知栏加载大图片的target
     */
    private NotificationTarget notificationTarget;
    /**
     * 设置加载小部件图片的target
     */
    private AppWidgetTarget appWidgetTarget;//

    /**
     * 图片加载完后的动画效果,在异步加载资源完成时会执行该动画。
     */
    private Integer animResId;
    /**
     * 在异步加载资源完成时会执行该动画。可以接受一个Animator对象
     */
    private ViewPropertyAnimation.Animator animator;

    /**
     * 是否做圆形裁剪
     */
    private boolean cropCircle;
    /**
     * 是否进行圆角处理
     */
    private boolean roundedCorners;
    /**
     * 圆角大小
     */
    private int roundedCornerSize;

    /**
     * 是否做灰度处理
     */
    private boolean grayscale;
    /**
     * 是否进行高斯模糊
     */
    private boolean blur;

    /**
     * 是否需要旋转图片
     */
    private boolean rotate;
    /**
     * 图片旋转的角度
     */
    private int rotateDegree;

    private String tag; // 唯一标识

    public void setCrossFade(boolean crossFade) {
        this.crossFade = crossFade;
    }

    public void setRoundedCornerSize(int roundedCornerSize) {
        this.roundedCornerSize = roundedCornerSize;
    }

    public void setRoundedCorners(boolean roundedCorners) {
        this.roundedCorners = roundedCorners;
    }

    public void setCropCircle(boolean cropCircle) {
        this.cropCircle = cropCircle;
    }

    public void setPlaceHolderResId(int placeHolderResId) {
        this.placeHolderResId = placeHolderResId;
        this.errorResId = placeHolderResId;
    }


    /**
     * 硬盘缓存方式
     */
    public enum DiskCacheType {
        NONE(DiskCacheStrategy.NONE), // 不做硬盘缓存
        SOURCE(DiskCacheStrategy.SOURCE), // 缓存原始分辨率的图片
        RESULT(DiskCacheStrategy.RESULT), // 缓存最终分辨率的图像(降低分辨率后的或者其他转换后的)
        ALL(DiskCacheStrategy.ALL);// 默认值，缓存源资源和转换后的资源
        private DiskCacheStrategy strategy;

        DiskCacheType(DiskCacheStrategy strategy) {
            this.strategy = strategy;
        }

        public DiskCacheStrategy getStrategy() {
            return strategy;
        }
    }

    /**
     * 加载优先级的方式
     */
    public enum LoadPriorityType {
        LOW(Priority.LOW), //低优先级
        NORMAL(Priority.NORMAL),
        HIGH(Priority.HIGH), //高优先级
        IMMEDIATE(Priority.IMMEDIATE);//立即加载
        Priority priority;

        LoadPriorityType(Priority priority) {
            this.priority = priority;
        }

        public Priority getPriority() {
            return priority;
        }
    }

    /**
     * 跳过builder构建config
     *
     * @param builder
     */
    private ImageLoadConfig(Builder builder) {
        this.placeHolderResId = builder.placeHolderResId;
        this.errorResId = builder.errorResId;
        this.crossFade = builder.crossFade;
        this.crossDuration = builder.crossDuration;
        this.size = builder.size;
        this.cropType = builder.cropType;
        this.asGif = builder.asGif;
        this.asBitmap = builder.asBitmap;
        this.skipMemoryCache = builder.skipMemoryCache;
        this.diskCacheStorage = builder.diskCacheStrategy;
        this.thumbnailRadio = builder.thumbnail;
        this.thumbnailUrl = builder.thumbnailUrl;
        this.simpleTarget = builder.simpleTarget;
        this.viewTarget = builder.viewTarget;
        this.notificationTarget = builder.notificationTarget;
        this.appWidgetTarget = builder.appWidgetTarget;
        this.animResId = builder.animResId;
        this.animator = builder.animator;
        this.priority = builder.prioriy;
        this.blur = builder.blur;
        this.cropCircle = builder.cropCircle;
        this.roundedCorners = builder.roundedCorners;
        this.roundedCornerSize = builder.roundedCornerSize;
        this.grayscale = builder.grayscale;
        this.rotate = builder.rotate;
        this.rotateDegree = builder.rotateDegree;
        this.tag = tag;
    }

    /**
     * ImageLoadConfig的Builder，相关属性设置跟ImageLoadConfig的一一对应
     */
    public static class Builder {
        private Integer placeHolderResId;
        private Integer errorResId;

        /**
         * 设置是否使用淡入淡出动画
         */
        private boolean crossFade;
        private int crossDuration;
        private int cropType = CENTER_CROP;

        private OverrideSize size;

        private boolean asGif;
        private boolean asBitmap;

        private boolean skipMemoryCache;
        private DiskCacheType diskCacheStrategy = DiskCacheType.ALL;

        private LoadPriorityType prioriy = LoadPriorityType.HIGH;

        private float thumbnail;
        private String thumbnailUrl;

        private SimpleTarget<Bitmap> simpleTarget;
        private ViewTarget<? extends View, GlideDrawable> viewTarget;
        private NotificationTarget notificationTarget;
        private AppWidgetTarget appWidgetTarget;

        private Integer animResId;
        private ViewPropertyAnimation.Animator animator;

        private boolean cropCircle;
        private boolean roundedCorners;
        private int roundedCornerSize;

        private boolean grayscale;
        private boolean blur;

        private boolean rotate;
        /**
         * 默认选择角度是90度
         */
        private int rotateDegree = 90;

        private String tag;

        /**
         * 设置目标View要显示的默认图片资源id
         */
        public Builder setPlaceHolderResId(Integer placeHolderResId) {
            this.placeHolderResId = placeHolderResId;
            return this;
        }

        /**
         * 设置图片加载错误时显示的资源id
         *
         * @param errorResId
         * @return
         */
        public Builder setErrorResId(Integer errorResId) {
            this.errorResId = errorResId;
            return this;
        }

        /**
         * 设置是否使用淡入淡出动画
         *
         * @param crossFade
         * @return
         */
        public Builder setCrossFade(boolean crossFade) {
            this.crossFade = crossFade;
            return this;
        }

        /**
         * 淡入淡出动画持续的时间
         *
         * @param crossDuration
         * @return
         */
        public Builder setCrossDuration(int crossDuration) {
            this.crossDuration = crossDuration;
            return this;
        }

        /**
         * 设置加载的图片的宽高尺寸
         *
         * @param size
         * @return
         */
        public Builder setSize(OverrideSize size) {
            this.size = size;
            return this;
        }

        /**
         * 设置裁剪类型,默认为中部裁剪
         *
         * @param cropType
         * @return
         */
        public Builder setCropType(int cropType) {
            cropType = cropType;
            return this;
        }

        /**
         * 是否当作gif方式显示，为true时表示强制显示的是gif动画, <br>
         * 如果url不是gif的资源,那么会回调error()
         *
         * @param asGif
         * @return
         */
        public Builder setAsGif(boolean asGif) {
            this.asGif = asGif;
            return this;
        }

        /**
         * 是否当作bitmap方式显示，Glide 也可以加载 Gif 或 video，
         * 为true时表示强制显示为常规图片,如果是gif资源,则加载第一帧作为图片
         *
         * @param asBitmap
         * @return
         */
        public Builder setAsBitmap(boolean asBitmap) {
            this.asBitmap = asBitmap;
            return this;
        }


        /**
         * 是否跳过内存进行缓存，true时表示跳过，默认false
         *
         * @param skipMemoryCache
         * @return
         */
        public Builder setSkipMemoryCache(boolean skipMemoryCache) {
            this.skipMemoryCache = skipMemoryCache;
            return this;
        }

        /**
         * 硬盘缓存方式,默认为all类型
         *
         * @param diskCacheStrategy
         * @return
         */
        public Builder setDiskCacheStrategy(DiskCacheType diskCacheStrategy) {
            this.diskCacheStrategy = diskCacheStrategy;
            return this;
        }

        /**
         * 加载优先级的方式
         *
         * @param prioriy
         * @return
         */
        public Builder setPrioriy(LoadPriorityType prioriy) {
            this.prioriy = prioriy;
            return this;
        }

        /**
         * 设置缩略图的缩放比例，范围是0.0f-1.0f
         *
         * @param thumbnail
         * @return
         */
        public Builder setThumbnail(float thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        /**
         * 设置缩略图的url地址,如果缩略图比全尺寸图先加载完，就显示缩略图，否则就不显示
         *
         * @param thumbnailUrl
         * @return
         */
        public Builder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }


        /**
         * 指定simpleTarget对象,可以在Target回调方法中处理bitmap,同时该构造方法中还可以指定宽、高
         *
         * @param simpleTarget
         * @return
         */
        public Builder setSimpleTarget(SimpleTarget<Bitmap> simpleTarget) {
            this.simpleTarget = simpleTarget;
            return this;
        }

        /**
         * 指定ViewTarget对象,可以是自定义View,该构造方法传入的View和通配符的view是同一个
         *
         * @param viewTarget
         * @return
         */
        public Builder setViewTarget(ViewTarget<? extends View, GlideDrawable> viewTarget) {
            this.viewTarget = viewTarget;
            return this;
        }

        /**
         * 设置通知栏加载大图片的target
         *
         * @param notificationTarget
         * @return
         */
        public Builder setNotificationTarget(NotificationTarget notificationTarget) {
            this.notificationTarget = notificationTarget;
            return this;
        }

        /**
         * 设置加载小部件图片的targe
         *
         * @param appWidgetTarget
         * @return
         */
        public Builder setAppWidgetTarget(AppWidgetTarget appWidgetTarget) {
            this.appWidgetTarget = appWidgetTarget;
            return this;
        }


        /**
         * 图片加载完后的动画效果,在异步加载资源完成时会执行该动画
         *
         * @param animResId
         * @return
         */
        public Builder setAnimResId(Integer animResId) {
            this.animResId = animResId;
            return this;
        }

        /**
         * 在异步加载资源完成时会执行该动画。可以接受一个Animator对象
         *
         * @param animator
         * @return
         */
        public Builder setAnimator(ViewPropertyAnimation.Animator animator) {
            this.animator = animator;
            return this;
        }

        /**
         * 设置是否做圆形裁剪
         *
         * @param cropCircle
         * @return
         */
        public Builder setCropCircle(boolean cropCircle) {
            this.cropCircle = cropCircle;
            return this;
        }

        /**
         * 是否进行圆角处理
         *
         * @param roundedCorners
         * @return
         */
        public Builder setRoundedCorners(boolean roundedCorners) {
            this.roundedCorners = roundedCorners;
            return this;
        }

        /**
         * 设置圆角大小
         *
         * @param roundedCornerSize
         * @return
         */
        public Builder setRoundedCornerSize(int roundedCornerSize) {
            this.roundedCornerSize = roundedCornerSize;
            return this;
        }

        /**
         * 是否做灰度处理
         *
         * @param grayscale
         * @return
         */
        public Builder setGrayscale(boolean grayscale) {
            this.grayscale = grayscale;
            return this;
        }

        /**
         * 是否进行高斯模糊
         *
         * @param blur
         * @return
         */
        public Builder setBlur(boolean blur) {
            this.blur = blur;
            return this;
        }

        /**
         * 是否需要旋转图片
         *
         * @param rotate
         * @return
         */
        public Builder setRotate(boolean rotate) {
            this.rotate = rotate;
            return this;
        }

        /**
         * 图片旋转的角度
         *
         * @param rotateDegree
         * @return
         */
        public Builder setRotateDegree(int rotateDegree) {
            this.rotateDegree = rotateDegree;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * build生成ImageLoadConfig
         *
         * @return
         */
        public ImageLoadConfig build() {
            return new ImageLoadConfig(this);
        }
    }

    public static Builder parseBuilder(ImageLoadConfig config) {
        Builder builder = new Builder();
        builder.placeHolderResId = config.placeHolderResId;
        builder.errorResId = config.errorResId;
        builder.crossFade = config.crossFade;
        builder.crossDuration = config.crossDuration;
        builder.size = config.size;
        builder.cropType = config.cropType;
        builder.asGif = config.asGif;
        builder.asBitmap = config.asBitmap;
        builder.skipMemoryCache = config.skipMemoryCache;
        builder.diskCacheStrategy = config.diskCacheStorage;
        builder.thumbnail = config.thumbnailRadio;
        builder.thumbnailUrl = config.thumbnailUrl;
        builder.simpleTarget = config.simpleTarget;
        builder.viewTarget = config.viewTarget;
        builder.notificationTarget = config.notificationTarget;
        builder.appWidgetTarget = config.appWidgetTarget;
        builder.animResId = config.animResId;
        builder.animator = config.animator;
        builder.prioriy = config.priority;
        builder.cropCircle = config.cropCircle;
        builder.roundedCorners = config.roundedCorners;
        builder.grayscale = config.grayscale;
        builder.blur = config.blur;
        builder.rotate = config.rotate;
        builder.rotateDegree = config.rotateDegree;
        builder.tag = config.tag;
        return builder;
    }

    public Integer getPlaceHolderResId() {
        return placeHolderResId;
    }

    public Integer getErrorResId() {
        return errorResId;
    }

    public boolean isCrossFade() {
        return crossFade;
    }

    public int getCrossDuration() {
        return crossDuration;
    }

    public OverrideSize getSize() {
        return size;
    }

    public int getCropType() {
        return cropType;
    }

    public boolean isAsGif() {
        return asGif;
    }

    public boolean isAsBitmap() {
        return asBitmap;
    }

    public boolean isSkipMemoryCache() {
        return skipMemoryCache;
    }

    public DiskCacheType getDiskCacheStrategy() {
        return diskCacheStorage;
    }

    public LoadPriorityType getPrioriy() {
        return priority;
    }

    public float getThumbnailRadio() {
        return thumbnailRadio;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public SimpleTarget<Bitmap> getSimpleTarget() {
        return simpleTarget;
    }

    public ViewTarget<? extends View, GlideDrawable> getViewTarget() {
        return viewTarget;
    }

    public NotificationTarget getNotificationTarget() {
        return notificationTarget;
    }

    public AppWidgetTarget getAppWidgetTarget() {
        return appWidgetTarget;
    }

    public Integer getAnimResId() {
        return animResId;
    }

    public ViewPropertyAnimation.Animator getAnimator() {
        return animator;
    }

    public boolean isCropCircle() {
        return cropCircle;
    }

    public boolean isRoundedCorners() {
        return roundedCorners;
    }

    public int getRoundedCornerSize() {
        return roundedCornerSize;
    }

    public boolean isGrayscale() {
        return grayscale;
    }

    public boolean isBlur() {
        return blur;
    }

    public boolean isRotate() {
        return rotate;
    }

    public int getRotateDegree() {
        return rotateDegree;
    }

    public String getTag() {
        return tag;
    }

    /**
     * 图片最终显示在ImageView上的宽高像素
     *
     * @author zhouyong
     * @date 2016年8月15日
     */
    public static class OverrideSize {
        private final int width;
        private final int height;

        public OverrideSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

}