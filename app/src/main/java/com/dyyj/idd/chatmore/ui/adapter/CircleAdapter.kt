package com.dyyj.idd.chatmore.ui.adapter

import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseAdapterV2
import com.dyyj.idd.chatmore.base.BaseViewHolder
import com.dyyj.idd.chatmore.databinding.ItemCircleBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.UserTopicResult
import com.dyyj.idd.chatmore.ui.h5.H5Activity
import com.dyyj.idd.chatmore.ui.task.activity.TaskSystemActivity
import com.dyyj.idd.chatmore.ui.user.activity.RankingActivity
import com.dyyj.idd.chatmore.utils.DateUtils
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.utils.Utils
import com.dyyj.idd.chatmore.viewmodel.CircleViewModel
import com.gt.common.gtchat.extension.niceChatContext
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * by_zwj
 * desc: 消息里的好友圈
 * 上次更改:2019/1/3
 */

class CircleAdapter : BaseAdapterV2<UserTopicResult.UserTopic>() {

//    val commentAdapter = lazy {
//        CircleCommentAdapter()
//    }

    interface ICommentListener {
        fun onCommentClick()
    }

    private var commentListener: ICommentListener? = null

    fun setCommentListener(listener: ICommentListener) {
        this.commentListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.onBindViewHolder(getList()[position], position)
        }
    }

    inner class ViewHolder(
            viewGroup: ViewGroup?) : BaseViewHolder<UserTopicResult.UserTopic, ItemCircleBinding>(
            viewGroup, R.layout.item_circle) {
        override fun onBindViewHolder(obj: UserTopicResult.UserTopic, position: Int) {
            super.onBindViewHolder(obj, position)
            mBinding.clTop.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
//            val requestOption = RequestOptions().circleCrop()
//            requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//            Glide.with(mContext).load(obj.userInfo?.avatar).apply(requestOption).into(mBinding.ivAvatar)
            Glide.with(mContext).load(obj.userInfo?.avatar).asBitmap().transform(
                    CropCircleTransformation(mContext)).crossFade().error(
                    R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar)
            mBinding.ivAvatar.setOnClickListener {
                if(obj.topicType?.toInt() == 1 || obj.topicType?.toInt() == 4) {
                    //好友圈类型1为好友帖，类型4为升级帖
                    EventBus.getDefault().post(PersonVM(obj.userInfo?.userId))
                }
            }

            mBinding.textView21.text = obj.userInfo?.nickname
            mBinding.textView63.text = obj.userInfo?.userLevel
            mBinding.textView64.text = "${if (obj.userInfo?.gender == "1") "男" else "女"} / ${calculateAge(obj.userInfo?.birthday)}岁"
            mBinding.textView65.visibility = View.VISIBLE
            mBinding.textView65.text = obj.topicBody
            mBinding.txtTime.text = DateUtils.getStrTime("MM月dd日 HH:mm", obj.addTimeStamp);//yyyy/MM/dd HH:mm
//            commentAdapter.value.initData(arrayListOf())
//            mBinding.rvComment.layoutManager = MyLinearLayoutManager(mContext)
//            mBinding.rvComment.adapter = commentAdapter.value
            //三类集合的打印
            //Log.i("zhangwj","obj.userInfo?.nickname="+obj.userInfo?.nickname+""+obj.userInfo?.userId+""+obj.userInfo?.userLevel)
            //Log.i("zhangwj","obj.userId="+obj.userId)

            /*obj.userVoteList!!.forEachIndexed { index, userVote ->
                Log.i("zhangwj", "下标=${index}obj.userVote?.nickname=" + userVote.nickname + "obj.userVote?.replyVirtualName=" + userVote.replyVirtualName)
            }*/

            /*obj.userReplyList!!.forEachIndexed { index, userReply ->
                Log.i("zhangwj","下标=${index}obj.userReply?.nickname="+userReply.nickname+"obj.userReply?.replyVirtualName="+userReply.replyVirtualName)
            }*/

            /**
             * 赞信息
             */
            mBinding.txtLike.text = "+${obj.topicVoteCount}"
            mBinding.ivLike.setImageResource(if (obj.isVote != 0) R.drawable.ic_dynamic_like_ok else R.drawable.ic_dynamic_like)
            mBinding.txtLike.setTextColor(if (obj.isVote != 0) Color.parseColor("#FF715F") else Color.parseColor("#7F8D9C"))

            /**
             * 赞列表
             */
            if (obj.userVoteList?.size?:0 > 0) {
                mBinding.llLikes.visibility = View.VISIBLE
                obj.userVoteList?.let {
                    val sb = StringBuilder()
                    obj.userVoteList!!.forEachIndexed { index, userVote ->
                        if (obj.userId.equals(mDataRepository.getUserid())) {
                            sb.append(userVote.nickname)
                        }else{
                            sb.append(userVote.replyVirtualName)
                        }
                        if (index != obj.userVoteList!!.size - 1) {
                            sb.append("、")
                        }
                    }
                    mBinding.txtLikes.text = sb.toString()
                }
            } else {
                mBinding.llLikes.visibility = View.GONE
            }

            /**
             * 图片信息
             */
            if (obj.topicImgs?.size?:0 > 0) {
                mBinding.rvPhoto.visibility = View.VISIBLE
                mBinding.rvPhoto.layoutManager = GridLayoutManager(mContext, 3)
                mBinding.rvPhoto.adapter = PlazaPhotoAdapter(mContext, obj.topicImgs, obj.Id)
            } else {
                mBinding.rvPhoto.visibility = View.GONE
            }

            /**
             * 标签信息
             */
            mBinding.llTags.visibility = View.GONE
            obj.userInfo?.userTags?.let {
                mBinding.llTags.visibility = View.VISIBLE
                mBinding.llTags.removeAllViews()
                obj.userInfo.userTags.forEachIndexed { index, userTag ->
                    if (index < 3) {
                        userTag.tagName?.let {
                            insertTagTextView(userTag.tagName)
                        }
                    }
                }
            }

            mBinding.ivTag.visibility = if (obj.topicType == "7") View.VISIBLE else View.GONE
            updateUI(obj.topicType?.toInt()?:0, obj)
//            insertTagTextView("")

            mBinding.rvComment.removeAllViews()

            if (obj.topicReplyCount?.toInt()?:0 > 0) {//评论的数量大于 0 就显示评论
                mBinding.rvComment.visibility = View.VISIBLE
                if (obj.topicReplyCount?.toInt()?:0 > obj.showNums) {
                    mBinding.txtMore.visibility = View.VISIBLE
//                    obj.userReplyList?.subList(0, obj.showNums)?.let { commentAdapter.value.refreshData(obj.userReplyList?.subList(0, obj.showNums)) }
                    obj.userReplyList?.subList(0, obj.showNums)?.let {
                        obj.userReplyList.subList(0, obj.showNums).forEachIndexed { index, userReply ->
                            if (obj.userId.equals(mDataRepository.getUserid())) {
                                insertTextView(userReply,true)//如果是自己的动态,就显示真实的好友信息
                            }else{
                                insertTextView(userReply,false)//如果是自己的动态,就显示真实的好友信息
                            }
                        }
                    }
                } else {
                    mBinding.txtMore.visibility = View.GONE
//                    obj.userReplyList?.let { commentAdapter.value.refreshData(obj.userReplyList) }
                    obj.userReplyList?.let {
                        obj.userReplyList.forEachIndexed { index, userReply ->
                            if (obj.userId.equals(mDataRepository.getUserid())) {
                                insertTextView(userReply,true)//如果是自己的动态,就显示真实的好友信息
                            }else{
                                insertTextView(userReply,false)//如果是自己的动态,就显示真实的好友信息
                            }
                        }
                    }
                }
            } else {
                mBinding.rvComment.visibility = View.GONE
                mBinding.txtMore.visibility = View.GONE
            }

            mBinding.txtMore.setOnClickListener {
                if (obj.topicReplyCount?.toInt()?:0 > 0) {
                    obj.showNums = obj.topicReplyCount?.toInt()!!
                    notifyDataSetChanged()
                }
            }
            mBinding.ivComment.setOnClickListener {
                /*if (obj.userInfo?.userId == mDataRepository.getUserid()) {
                    Toast.makeText(mContext, "不能回复自己", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }*/
                obj.Id?.let {
                    EventBus.getDefault().post(CircleViewModel.CommentSendVM(obj.Id , obj.userId?:""))
//                    commentListener?.onCommentClick()
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_square_comment",obj.Id))
            }
            mBinding.ivLike.setOnClickListener {
                if (obj.isVote == 0) {
                    obj.Id?.let {
                        mDataRepository.voteTopic(mDataRepository.getUserid()!!, obj.Id).subscribe {
                            if (it.errorCode == 200) {
                                obj.isVote = 1
                                obj.topicVoteCount = (1 + (obj.topicVoteCount?.toInt()?:0)).toString()
                                mBinding.ivLike.setImageResource(R.drawable.ic_dynamic_like_ok)
                                mBinding.txtLike.text = "+${obj.topicVoteCount}"
                                mBinding.txtLike.setTextColor(Color.parseColor("#FF715F"))
                                mBinding.llLikes.visibility = View.VISIBLE
                                if ((obj.userVoteList == null) or (obj.userVoteList?.size == 0)) {
                                    val list = arrayListOf<UserTopicResult.UserVote>()
                                    val vote = UserTopicResult.UserVote()
                                    vote.nickname = it.replyVirtualName
                                    list.add(vote)
                                    obj.userVoteList = list
                                } else {
                                    val vote = UserTopicResult.UserVote()
                                    vote.nickname = it.replyVirtualName
                                    obj.userVoteList?.add(0, vote)
                                }
                                val sb = StringBuilder()
                                obj.userVoteList!!.forEachIndexed { index, userVote ->
                                    sb.append(userVote.nickname)
                                    if (index != obj.userVoteList!!.size - 1) {
                                        sb.append("、")
                                    }
                                }
                                mBinding.txtLikes.text = sb.toString()
                            } else {
                                Toast.makeText(niceChatContext(), it.errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                        EventTrackingUtils.joinPoint(EventBeans("ck_square_like",obj.Id))
                    }
                }
            }
        }

        private fun insertTextView(comment: UserTopicResult.UserReply , isMyCircle: Boolean) {
            val csView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null)
            val textView = csView.findViewById<TextView>(R.id.txt_content)
            if (isMyCircle) {//是自己发得动态,就显示朋友的真是昵称
                textView.text = Utils.getCText(comment.nickname + "：" + comment.replyMessage, "#5985B2", 0, comment.nickname?.length!!)
            }else{
                textView.text = Utils.getCText(comment.replyVirtualName + "：" + comment.replyMessage, "#5985B2", 0, comment.replyVirtualName?.length!!)
            }
            mBinding.rvComment.addView(csView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }

        private fun insertTagTextView(tag: String) {
            val csView = LayoutInflater.from(mContext).inflate(R.layout.item_dynamics_tag, null)
            val textView = csView.findViewById<TextView>(R.id.txt_tag)
            textView.text = tag
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = DeviceUtils.dp2px(mContext.resources, 5F).toInt()
            mBinding.llTags.addView(csView, params)
        }

        private fun updateUI(type: Int, obj: UserTopicResult.UserTopic) {
            mBinding.style2.visibility = if (type == 5) View.VISIBLE else View.GONE//任务完成数
            mBinding.style3.visibility = if (type == 6) View.VISIBLE else View.GONE//交友银行
            mBinding.style4.visibility = if (type == 7) View.VISIBLE else View.GONE//人气排行榜
            mBinding.style5.visibility = if (type == 4) View.VISIBLE else View.GONE//人员等级

            mBinding.style1.visibility = View.GONE

            if (type == 4) {
                mBinding.textView65.visibility = View.GONE
                mBinding.style5.text = Utils.getCText(obj.topicBody, "#F30950", 5, 6)
            } else if (type == 5) {
                mBinding.txtStyle.text = "完成了${obj.topicExt?.completeTaskNum}项，拿到${obj.topicExt?.rewardCoin}金币"
                mBinding.txtStyleTake.setOnClickListener {
                    TaskSystemActivity.start(mContext)
                    EventTrackingUtils.joinPoint(EventBeans("ck_messpage_gototask", ""))
                }
            } else if (type == 6) {
                mBinding.txtStyle3.text = "银行提升到 ${obj.topicExt?.bankLevel}级，累计赚取 ${obj.topicExt?.bankCoin}金币！"
                mBinding.txtStyleTake3.setOnClickListener {
                    val neturl = "http://www.liaodede.com/buddyBank/myBank.html?userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}&token=${mDataRepository.getLoginToken()!!}"
                    H5Activity.start(mContext, neturl, "社交银行", true)
                    EventTrackingUtils.joinPoint(EventBeans("ck_messpage_gotobank", ""))
                }
            } else if (type == 7) {
                mBinding.style4.setOnClickListener {
                    RankingActivity.start(mContext)
                    EventTrackingUtils.joinPoint(EventBeans("ck_messpage_gotorank", ""))
                }
                obj.topicExt?.msgList?.forEach {
                    if (it.rank == "1") {
//                        val requestOption = RequestOptions().circleCrop()
//                        requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//                        Glide.with(mContext).load(it.avatar).apply(requestOption).into(mBinding.ivAvatar1)
                        Glide.with(mContext).load(it.avatar).asBitmap().transform(
                                CropCircleTransformation(mContext)).crossFade().error(
                                R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar1)
                    } else if (it.rank == "2") {
//                        val requestOption = RequestOptions().circleCrop()
//                        requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//                        Glide.with(mContext).load(it.avatar).apply(requestOption).into(mBinding.ivAvatar2)
                        Glide.with(mContext).load(it.avatar).asBitmap().transform(
                                CropCircleTransformation(mContext)).crossFade().error(
                                R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar2)
                    } else if (it.rank == "3") {
//                        val requestOption = RequestOptions().circleCrop()
//                        requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//                        Glide.with(mContext).load(it.avatar).apply(requestOption).into(mBinding.ivAvatar3)
                        Glide.with(mContext).load(it.avatar).asBitmap().transform(
                                CropCircleTransformation(mContext)).crossFade().error(
                                R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivAvatar3)
                    }
                }
            }
        }
    }

    /**
     * 计算年龄
     */
    private fun calculateAge(dateStr: String?): Int {
        if (dateStr.isNullOrBlank()) {
            return 0;
        }
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val sDate = dateStr?.split("-")
        val sYear = sDate!!.get(0).toInt()
        val sMonth = sDate!!.get(1).toInt()
        val sDay = sDate!!.get(2).toInt()

        var age = year - sYear
        if (age <= 0)
            return 0
        else {
            if (month < sMonth) {
                return age - 1
            } else if ((month == sMonth) and (day < sDay)) {
                return age - 1
            } else {
                return age
            }
        }
    }

    class PersonVM(val userID: String?)

}