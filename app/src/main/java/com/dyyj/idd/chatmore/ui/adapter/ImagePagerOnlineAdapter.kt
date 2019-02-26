package com.dyyj.idd.chatmore.ui.adapter

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.utils.DisplayUtils
import pl.droidsonroids.gif.GifDrawableBuilder
import pl.droidsonroids.gif.MultiCallback
import java.io.File


class ImagePagerOnlineAdapter(val activity: Activity, val photos: ArrayList<String>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return photos.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        return super.instantiateItem(container, position)
        val screenWidth: Int = DisplayUtils.getScreenWidthPixels(activity)
        val screenHeight: Int = DisplayUtils.getScreenHeightPixels(activity)

        val image = ImageView(container.context)
        image.setOnClickListener { activity.finish() }
        container.addView(image)

        val url = photos.get(position)

        if (url.isNotEmpty() && url.toLowerCase().endsWith(".gif")) {
            GetImageCacheTask(container.context, image).execute(url)
        } else {
            Glide.with(container.context).load(url)
                    .centerCrop().placeholder(R.drawable.ic_photo_default)
                    .error(R.drawable.ic_photo_default)
                    .into(object : SimpleTarget<GlideDrawable>() {
                        override fun onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation<in GlideDrawable>) {
                            //动态设置图片大小
                            val params: ViewGroup.LayoutParams = image.getLayoutParams();
                            if ((resource.intrinsicWidth / resource.intrinsicHeight) > (screenWidth / screenHeight)) {
                                params.height = screenHeight
                                params.width = screenHeight * resource.intrinsicWidth / resource.intrinsicHeight
                            } else {
                                params.width = screenWidth
                                params.height = screenWidth * resource.intrinsicHeight / resource.intrinsicWidth
                            }
                            image.setLayoutParams(params)
                            image.setImageDrawable(resource)
                        }
                    })
        }
        return image
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
    }

    class GetImageCacheTask(val context: Context, val view: ImageView) : AsyncTask<String, Void, File?>() {
        override fun doInBackground(vararg params: String?): File? {
            var imgUrl = params[0]
            try {
                return Glide.with(context).load(imgUrl).downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                        com.bumptech.glide.request.target.Target.SIZE_ORIGINAL).get()
            } catch (e: Exception) {
            }

            return null
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)

            try {
                if (null != result) {
                    val gifFromFile = GifDrawableBuilder().from(result).build()

                    gifFromFile.loopCount = Character.MAX_VALUE.toInt()

                    view.setImageDrawable(gifFromFile)

                    var multiCallback = MultiCallback()
                    multiCallback.addView(view)

                    gifFromFile.setCallback(multiCallback);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }

}