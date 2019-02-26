package com.dyyj.idd.chatmore.extension;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/15
 * desc   :
 */
public class GitUtils {
//  public static void loadOneTimeGif(Context context, String model, final ImageView imageView, final GifListener gifListener) {
//    Glide.with(context).load(model).asGif().listener(new RequestListener<String, GifDrawable>() {
//
//      @Override public boolean onException(Exception e, String model, Target<GifDrawable> target,
//          boolean isFirstResource) {
//        return false;
//      }
//
//      @Override
//      public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target,
//          boolean isFromMemoryCache, boolean isFirstResource) {
//        try {
//          Field gifStateField = GifDrawable.class.getDeclaredField("state");
//          gifStateField.setAccessible(true);
//          Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
//          Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
//          gifFrameLoaderField.setAccessible(true);
//          Class gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
//          Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
//          gifDecoderField.setAccessible(true);
//          Class gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
//          Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)));
//          Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
//          getDelayMethod.setAccessible(true);
//          //设置只播放一次
//          resource.setLoopCount(1);
//          //获得总帧数
//          int count = resource.getFrameCount();
//          int delay = 0;
//          for (int i = 0; i < count; i++) {
//            //计算每一帧所需要的时间进行累加
//            delay += (int) getDelayMethod.invoke(gifDecoder, i);
//          }
//          imageView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//              if (gifListener != null) {
//                gifListener.gifPlayComplete();
//              }
//            }
//          }, delay);
//        } catch (NoSuchFieldException e) {
//          e.printStackTrace();
//        }catch (ClassNotFoundException e) {
//          e.printStackTrace();
//        } catch (IllegalAccessException e) {
//          e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//          e.printStackTrace();
//        } catch (InvocationTargetException e) {
//          e.printStackTrace();
//        }
//        return false;
//
//      }
//    }).into(imageView);
//  }

}
