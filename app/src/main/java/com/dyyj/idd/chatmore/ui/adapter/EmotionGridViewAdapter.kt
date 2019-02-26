package com.dyyj.idd.chatmore.ui.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.LayoutParams
import android.widget.BaseAdapter
import android.widget.GridLayout
import android.widget.ImageView
import com.dyyj.idd.chatmore.utils.EmotionUtils

/**
 * Created by wangbin on 2018/11/11.
 */
class EmotionGridViewAdapter(var context: Context, var emotionNames: List<String>,
                             var itemWidth: Int, var emotion_map_type: Int) : BaseAdapter() {

    override fun getCount(): Int {
        return emotionNames.size + 1
    }

    override fun getItem(position: Int): Any {
        return emotionNames.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var iv_emotion: ImageView = ImageView(context)
        var params: LayoutParams = LayoutParams(itemWidth, itemWidth)
        iv_emotion.setLayoutParams(params)
        val emotionName = emotionNames[position]
        iv_emotion.setImageResource(EmotionUtils.getImgByName(emotion_map_type, emotionName))

        return iv_emotion
    }
}