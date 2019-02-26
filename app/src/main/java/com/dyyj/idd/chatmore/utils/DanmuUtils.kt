package com.dyyj.idd.chatmore.utils

import android.app.Activity
import master.flame.danmaku.danmaku.loader.IllegalDataException
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.ui.widget.DanmakuView
import java.io.InputStream
import java.util.*

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/19
 * desc   : 弹幕
 */
class DanmuUtils {

  companion object {

    /**
     * 初始化字幕
     */
    fun init(context:Activity, danmukuContext: DanmakuContext, danmakView: DanmakuView, mCacheStufferAdapter: BaseCacheStuffer.Proxy?, stream: InputStream?) {
      var mContext = danmukuContext
      // 设置最大显示行数
      val maxLinesPair = HashMap<Int, Int>()
      maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 4 // 滚动弹幕最大显示5行
      // 设置是否禁止重叠
      val overlappingEnablePair = HashMap<Int, Boolean>()
      overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
      overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true

      mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3.0f).setDuplicateMergingEnabled(
              false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f).setCacheStuffer(
              SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
              //.setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter)  // 绘制背景使用BackgroundCacheStuffer
              .setMaximumLines(maxLinesPair).preventOverlapping(overlappingEnablePair).setDanmakuMargin(
                      40)

//      val mParser: BaseDanmakuParser = createParser(context.resources.openRawResource(R.raw.comments))
      val mParser: BaseDanmakuParser = createParser(stream)
      danmakView?.setCallback(object : master.flame.danmaku.controller.DrawHandler.Callback {
        override fun updateTimer(timer: DanmakuTimer) {}

        override fun drawingFinished() {
        }

        override fun danmakuShown(danmaku: BaseDanmaku) {
          //                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
        }

        override fun prepared() {
          danmakView.start()
//          EventBus.getDefault().post(CallViewModel.DanmuPrepared())
        }
      })

      danmakView?.prepare(mParser, mContext)
      danmakView?.showFPS(false)
      danmakView?.enableDanmakuDrawingCache(true)
    }

    /**
     * 解析流
     */
    private fun createParser(stream: InputStream?): BaseDanmakuParser {

      if (stream == null) {
        return object : BaseDanmakuParser() {

          override fun parse(): Danmakus {
            return Danmakus()
          }
        }
      }

      val loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)

      try {
        loader.load(stream)
      } catch (e: IllegalDataException) {
        e.printStackTrace()
      }

      val parser = BiliDanmukuParser()
      val dataSource = loader.dataSource
      parser.load(dataSource)
      return parser

    }
  }
}