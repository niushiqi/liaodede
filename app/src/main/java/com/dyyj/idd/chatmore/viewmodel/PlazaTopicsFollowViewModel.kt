package com.dyyj.idd.chatmore.viewmodel

import android.text.TextUtils
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.ui.adapter.PlazaTopicsFocusAdapter
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import io.reactivex.Single
import org.greenrobot.eventbus.EventBus

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 广场话题列表
 */
class PlazaTopicsFollowViewModel : ViewModel() {
    private val mAdapter by lazy { PlazaTopicsFocusAdapter() } //

    fun getAdapterFocus() = mAdapter

    /**
     * 关注话题列表
     */
    fun netTopicListByFocus(): Single<Int> {
        return mDataRepository.getMyFollowTopic()
                .map {
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                    return@map it
                }
                .flatMap {
                    Flowable.fromIterable(it.data)
                }
                .map {
                    it.follow = 1 //已关注
                    if (it.top == 1) {
                        it._FocusSort = 1
                    } else {
                        it._FocusSort = it.followTimestamp
                    }
                    return@map it
                }
                .sorted { o1, o2 ->
                    (o1._FocusSort - o2._FocusSort).toInt()
                }
                .toList()
                .map {
                    //未读数量
                    var result = 0;
                    it?.forEach {
                        result += it.newComments
                    }
                    EventBus.getDefault().post(PlazaMainViewModel.FollowNumVM(result))

                    //刷新view
                    it?.let { getAdapterFocus().initData(it) }
                    getAdapterFocus().notifyDataSetChanged()

                    return@map 0
                }
    }

    /**
     * 置顶话题
     */
    fun postTopTopic(topic: PlazaTopicListResult.Topic) {
        val subscribe = mDataRepository.postTopTopic(topic.squareTopicId)
                .filter({
                    if (it.errorCode != 200 && !TextUtils.isEmpty(it.errorMsg)) {
                        niceToast(it.errorMsg)
                    }

                    it.errorCode == 200
                })
                .flatMap {
                    topic.top = 1
                    topic._FocusSort = 0
                    Flowable.fromIterable(getAdapterFocus().getList())
                }
                .map {
                    if (it._FocusSort == 0L && it == topic) {
                        it._FocusSort = 1
                    }
                    return@map it
                }
                .sorted { o1, o2 ->
                    (o1._FocusSort - o2._FocusSort).toInt()
                }
                .toList()
                .subscribe({
                    it?.let { getAdapterFocus().initData(it) }
                    getAdapterFocus().notifyDataSetChanged()
                }, {
                    it.printStackTrace()
                })

        mCompositeDisposable.add(subscribe)

    }

    /**
     * 取消置顶话题
     */
    fun revokeTopTopic(topic: PlazaTopicListResult.Topic) {
        val subscribe = mDataRepository.revokeTopTopic(topic.squareTopicId)
                .filter({
                    if (it.errorCode != 200 && !TextUtils.isEmpty(it.errorMsg)) {
                        niceToast(it.errorMsg)
                    }

                    it.errorCode == 200
                })
                .flatMap {
                    topic.top = 0
                    topic._FocusSort = topic.followTimestamp
                    Flowable.fromIterable(getAdapterFocus().getList())
                }
                .sorted { o1, o2 ->
                    (o1._FocusSort - o2._FocusSort).toInt()
                }
                .toList()
                .subscribe({
                    it?.let { getAdapterFocus().initData(it) }
                    getAdapterFocus().notifyDataSetChanged()
                }, {
                    it.printStackTrace()
                })

        mCompositeDisposable.add(subscribe)

    }

    /**
     * 取消关注话题
     */
    fun unFollowTopic(topic: PlazaTopicListResult.Topic) {
        val subscribe = mDataRepository.postRevokeFollowTopic(topic.squareTopicId)
                .filter({
                    if (it.errorCode != 200 && !TextUtils.isEmpty(it.errorMsg)) {
                        niceToast(it.errorMsg)
                    }
                    topic.follow = 0 //未关注

                    it.errorCode == 200
                })
                .subscribe({
                    getAdapterFocus().getList().remove(topic)
                    getAdapterFocus().notifyDataSetChanged()
                }, {
                    it.printStackTrace()
                })

        mCompositeDisposable.add(subscribe)
    }

}