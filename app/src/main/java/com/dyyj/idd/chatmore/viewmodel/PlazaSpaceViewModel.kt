package com.dyyj.idd.chatmore.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.ActivitySpaceBinding
import com.dyyj.idd.chatmore.model.mqtt.result.SignResult
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaSpaceCardsFragment
import com.dyyj.idd.chatmore.ui.user.activity.MyMsgActivity
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader
import com.dyyj.idd.chatmore.weiget.FlowAdapter
import org.greenrobot.eventbus.EventBus

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 我的空间
 */
class PlazaSpaceViewModel : ViewModel() {

    var isSelf = false
    var mUserId = ""
    var mTopicID = ""
    var userInfo: UserDetailInfoResult.Data? = null

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager, id: String): PagerAdapterV2 {
        val mTitles = arrayOf("动态")
        val mFragment: Array<Fragment> = arrayOf(PlazaSpaceCardsFragment.create(id))
        return PagerAdapterV2(fm, mFragment, mTitles)
    }

    fun getTitle(): String {
        if (mUserId == mDataRepository.getUserid()) {
            isSelf = true
            return "我的空间"
        } else {
            return "TA的空间"
        }
    }

    fun showMessage(view: View) {
        MyMsgActivity.start(view.context, MyMsgActivity.SELECT_SQUARE_PAGE)
    }

    fun showUnReadMessage(view: View) {
        MyMsgActivity.start(view.context, MyMsgActivity.SELECT_SQUARE_PAGE)
    }

    /**
     * 获取个人信息
     */
    fun getUserInfo(binding: ActivitySpaceBinding) {
        val subscribe = mDataRepository.getUserDetailInfo(mUserId)
                .subscribe({ result ->
                    if (result.errorCode == 200) {
                        userInfo = result.data

                        val head = binding.layoutToolbar?.head
                        val userInfo = result.data!!.userBaseInfo
                        val userExtraInfo = result.data.userExtraInfo
                        ImageLoader.loadHead(head?.ivPersonHead, userInfo.avatar)
                        head?.tvPersonName?.text = userInfo.nickname
                        head?.tvPersonAge?.text = "${userInfo.age}岁"

                        if (TextUtils.isEmpty(userExtraInfo.school)) {
                            head?.tvPersonProfession?.text = userExtraInfo.professionName
                        } else {
                            head?.tvPersonProfession?.text = userExtraInfo.school + "|" + userExtraInfo.professionName
                        }

                        head?.tvPersonRegion?.text = userExtraInfo.address

                        if (TextUtils.equals("2", userInfo.gender)) {
                            head?.ivPersonSex?.setImageResource(R.drawable.ic_sex_femal)
                        } else {
                            head?.ivPersonSex?.setImageResource(R.drawable.ic_gender_main_normal)
                        }
                    }
                }, { e -> e.printStackTrace() })

        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取标签
     */
    fun getSignData(binding: ActivitySpaceBinding) {
        val subscribe = mDataRepository.getSigns(mUserId)
                .subscribe({ result ->
                    if (result.errorCode == 200) {
                        val tags = result.data!!.tags
                        if (tags!!.isEmpty()) return@subscribe
                        val head = binding.layoutToolbar?.head
                        head?.aflPerson?.setAdapter(FlowAdapter2(tags, head.root.context))
                    }
                }, { throwable -> throwable.printStackTrace() })

        mCompositeDisposable.add(subscribe)
    }

    /**
     * 标签适配器
     */
    class FlowAdapter2(var tags: List<SignResult.Data.Sign>, var context: Context)
        : FlowAdapter<SignResult.Data.Sign>(tags) {
        override fun getView(position: Int): View {
            val item = View.inflate(context, R.layout.item_person_tag, null)
            val tvAttrTag = item.findViewById<TextView>(R.id.tv_item_tag)
            tvAttrTag.text = tags[position].tagName
            return item
        }
    }

    /**
     * 获取未读消息
     */
    fun getSquareUnreadMessage() {
        val subscribe = mDataRepository.getSquareUnReadMessageNum(mDataRepository.getUserid()!!).subscribe({
            EventBus.getDefault().post(PlazaMainViewModel.SquareUnreadMessageVM(it.errorCode == 200,
                    it.data!!.unReadMessageNum!!.toInt(), it.data!!.lastMessageAvatar!!))
            //获取好友圈消息
            val subscribe = mDataRepository.getUserMessage(mDataRepository.getUserid()!!).subscribe ({
                EventBus.getDefault().post(PlazaMainViewModel.MeUnReadMsgVM(it.errorCode == 200, it))
            },{})
            mCompositeDisposable.add(subscribe)
        }, {
            it.printStackTrace()
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取开始私聊消耗魔石数量
     */
    fun getConsumeStoneData(binding: ActivitySpaceBinding) {

        val subscribe = mDataRepository.getSquareMessageConsumeStone(mUserId)
                .subscribe({ result ->
                    if (result.errorCode == 200) {
                        val consumeStone = result.data!!.consumeStone
                        binding.tvChat.setText("立即聊天，消耗" + consumeStone!!.toString() + "魔石")
                    }
                }, { e -> e.printStackTrace() })
        mCompositeDisposable.add(subscribe)

    }

    /**
     * 获取是否允许广场匹配
     */
    fun getMatchStatus(binding: ActivitySpaceBinding) {
        val subscribe = mDataRepository.getUserMatchingStatus(mUserId)
                .subscribe({ result ->
                    if (result.getErrorCode() == 200) {
                        if (result.getData().getMatchingEnable() == "1" || result.getData().getMatchingEnable() == "2") {
                            //允许广场匹配
                            netCheckRelation(binding)
                        } else {
                            binding.tvChat.visibility = View.VISIBLE

                            //不允许广场匹配
                            binding.tvChat.setBackgroundResource(R.drawable.shape_bg_dark)
                            binding.tvChat.setTextColor(Color.parseColor("#828284"))
                            binding.tvChat.setOnClickListener(View.OnClickListener {
                                Toast.makeText(binding.root.context, "对方设置了拒绝接收广场消息哦", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }, { e -> e.printStackTrace() })

        mCompositeDisposable.add(subscribe)
    }

    /**
     * 获取是否允许广场匹配
     */
    @SuppressLint("CheckResult")
    private fun netCheckRelation(binding: ActivitySpaceBinding) {
        val subscribe = mDataRepository.checkRelationApi(mUserId)
                .subscribe({ result ->
                    if (result.errorCode == 200) {
                        binding.tvChat.visibility = View.VISIBLE

                        if (result.data!!.isFriend == "1") {
                            //是好友哦
                            binding.tvChat.setText("立即聊天")
                        }
                    }
                }, { e -> e.printStackTrace() })
        mCompositeDisposable.add(subscribe)
    }
}