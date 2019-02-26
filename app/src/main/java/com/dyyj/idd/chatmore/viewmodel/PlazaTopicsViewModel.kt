package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PlazaTopicsAdapter
import com.dyyj.idd.chatmore.ui.adapter.PlazaTopicsPostedAdapter
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 广场话题列表
 */
class PlazaTopicsViewModel : ViewModel() {
    private val mAdapter by lazy { PlazaTopicsAdapter() } //热门
    private val mAdapterPosted by lazy { PlazaTopicsPostedAdapter() } // 发帖

    fun getAdapter() = mAdapter
    fun getAdapterPosted() = mAdapterPosted

    /**
     * 热门话题列表
     */
    fun netTopicList(): Single<Int> {
        return mDataRepository.getPlazaListInPlaza()
                .map {
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                    return@map it
                }
                .flatMap {
                    Flowable.fromIterable(it.data)
                }
                .sorted { o1, o2 -> o1.squareTopicSort!! - o2.squareTopicSort!! }
                .toList()
                .map {
                    it?.let { getAdapter().initData(it) }
                    getAdapter().notifyDataSetChanged()
                    return@map 0
                }

    }

    /**
     * 发帖话题列表
     */
    fun netTopicListByPosted(): Single<Int> {
        return mDataRepository.getPlazaListInPlaza()
                .map {
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                    return@map it
                }
                .flatMap {
                    Flowable.fromIterable(it.data)
                }
                .sorted { o1, o2 -> o1.squareTopicSort!! - o2.squareTopicSort!! }
                .toList()
                .map {
                    it?.let { getAdapterPosted().initData(it) }
                    getAdapterPosted().notifyDataSetChanged()
                    return@map 0
                }

    }

}