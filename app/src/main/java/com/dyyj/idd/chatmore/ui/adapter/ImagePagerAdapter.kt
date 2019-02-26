package com.dyyj.idd.chatmore.ui.adapter

import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.ui.user.photo.Image
import java.io.File

class ImagePagerAdapter(val photos: ArrayList<Image>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return photos.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        return super.instantiateItem(container, position)
        val image = ImageView(container.context)
        image.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val url = Uri.decode(Uri.fromFile(File(photos.get(position).path)).toString())
        Glide.with(container.context).load(url).centerCrop().placeholder(R.drawable.ic_photo_default).error(R.drawable.ic_photo_default).into(image)
//        val requestOption = RequestOptions().centerCrop()
//        requestOption.error(R.drawable.ic_photo_default).placeholder(R.drawable.ic_photo_default)
//        Glide.with(container.context).load(url).apply(requestOption).into(image)
        container.addView(image)
        return image
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
    }

}