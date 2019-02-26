package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.PlazaFlowCardResult
import com.dyyj.idd.chatmore.model.network.result.UserMessageResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.ui.adapter.PlazaFlowCardAdapter
import com.dyyj.idd.chatmore.ui.adapter.PlazaMainHotAdapter
import com.dyyj.idd.chatmore.ui.plaza.ApiTransformer
import com.dyyj.idd.chatmore.utils.DateFormatter
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   :
 */
class PlazaMainViewModel : ViewModel() {
    private val mHotAdapter by lazy { PlazaMainHotAdapter() }
    private val mAdapter by lazy { PlazaFlowCardAdapter() }

    var isLoadingCard = false;
    //var cardSet = hashSetOf<String>()

    /**
     * 获取Adapter
     */
    fun getHotAdapter() = mHotAdapter

    fun getAdapter() = mAdapter

    /**
     * 热门话题列表
     */
    fun netTopicList() {
        mDataRepository.getPlazaListInPlaza()
                .map {
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                        EventBus.getDefault().post(TopicListVM(false))
                    }
                    return@map it
                }
                .flatMap {
                    Flowable.fromIterable(it.data)
                }
                .sorted { o1, o2 -> o1.squareTopicSort!! - o2.squareTopicSort!! }
                .filter { it.squareTopicIsHot == 1 }//热门话题
                .toList()
                .subscribe({
                    it?.let { getHotAdapter().initData(it) }
                    EventBus.getDefault().post(TopicListVM(true))
                }, {
                    EventBus.getDefault().post(TopicListVM(false))
                })
    }

    /**
     * 随便看看
     */
    fun netCircleList(page: Int, pageSize: Int) {
        if (isLoadingCard) return
        isLoadingCard = true

//        if (page == 1) {
//            cardSet.clear()
//        }

        var count = -1;
        val subscribe = Flowable.range(0, 4)
                .flatMap { return@flatMap Flowable.timer(it * 1L, TimeUnit.SECONDS) }
                .flatMap {
                    return@flatMap mDataRepository.getPlazaCardList(page.toString(), pageSize.toString())
                }
                .compose(ApiTransformer<PlazaFlowCardResult>())
                .map {
                    count++
                    val arrayListOf = arrayListOf<Any>()

                    val comments = it.data?.comments
                    if (null == comments) return@map arrayListOf

                    arrayListOf.addAll(comments!!)

//                    comments.forEach {
//                        val add = cardSet.add(it.squareTopicCommentId)
//                        if (add) arrayListOf.add(it)
//                    }

                    if (it.data?.topic != null) {
                        arrayListOf.add(it.data?.topic)
                    }

                    return@map arrayListOf
                }
                .subscribe({
                    EventBus.getDefault().post(CardListVM(true, !(page == 1 && count == 0), null, it))
                }, {
                    isLoadingCard = false;
                    EventBus.getDefault().post(CardListVM(false, !(page == 1 && count == 0), null, null))

                    if (it is ApiTransformer.NetError) {
                        niceToast(it.message)
                    } else {
                        niceToast("网络异常，请重试！")
                    }

                    it.printStackTrace()
                }, {
                    isLoadingCard = false;
                })
        mCompositeDisposable.add(subscribe)
    }

    fun getSquareUnreadMessage() {
        val subscribe = mDataRepository.getSquareUnReadMessageNum(mDataRepository.getUserid()!!).subscribe({
            EventBus.getDefault().post(SquareUnreadMessageVM(it.errorCode == 200,
                    it.data!!.unReadMessageNum!!.toInt(), it.data!!.lastMessageAvatar!!))
        }, {
            it.printStackTrace()
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 关注话题未读消息数量
     */
    fun netFocusNum() {
        val subscribe = mDataRepository.getMyFollowTopic()
                .subscribe({
                    if (it.errorCode == 200) {
                        var result = 0;
                        it.data?.forEach {
                            result += it.newComments
                        }
                        EventBus.getDefault().post(FollowNumVM(result))
                    }
                }, {
                    it.printStackTrace()
                })

        mCompositeDisposable.add(subscribe)
    }

    fun squarePopHandle(isSquarePop: Boolean, squarePopNumber: Int, squarePopImage: String, squarePopGotoTarget: String) {
        var squarePopNumToday: Int = 0
        var squarePopTime: Long = 0

        //是否弹窗
        if(isSquarePop) {
            //获取今天的次数
            squarePopNumToday = PreferenceUtil.getInt("squarePopNumber", 0)
            squarePopTime = PreferenceUtil.getLong("squarePopTime", 0)

            if(!DateFormatter.isSameDay(squarePopTime, System.currentTimeMillis())) {
                //不是同一天，更新今日次数为0
                squarePopNumToday = 0
            }
            //是否够次数
            if(squarePopNumToday < squarePopNumber) {
                //开启弹窗
                EventBus.getDefault().post(PlazaMainViewModel.squarePopVM(squarePopImage, squarePopGotoTarget))
                //记录次数+1
                squarePopNumToday += 1
                //记录此次弹窗信息
                PreferenceUtil.commitInt("squarePopNumber", squarePopNumToday)
                PreferenceUtil.commitLong("squarePopTime", System.currentTimeMillis())
            }
        }
    }

    class SquareUnreadMessageVM(val success: Boolean, val obj: Int, val avatar: String)

    class CardListVM(val success: Boolean, val more: Boolean, val message: String? = "", val vm: List<Any>? = null)

    class TopicListVM(val isSuccess: Boolean = false)

    class FollowNumVM(var num: Int)

    class FlowNumVM(var num: Int)

    class squarePopVM(val squarePopImage: String, val squarePopGotoTarget: String)

    class OpenSquareVM()

    class MeUnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

}