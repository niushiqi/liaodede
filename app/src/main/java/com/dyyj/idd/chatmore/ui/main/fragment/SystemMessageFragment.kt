package com.dyyj.idd.chatmore.ui.main.fragment

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentSystemMessageBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult
import com.dyyj.idd.chatmore.ui.event.TaskStartSquareEvent
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaSpaceActivity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicsFocusActivity
import com.dyyj.idd.chatmore.ui.user.activity.ApplyActivity
import com.dyyj.idd.chatmore.ui.user.activity.CircleFragment
import com.dyyj.idd.chatmore.utils.DataBindingUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.MessageCircleViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageFragmentViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageSystemViewModel
import com.dyyj.idd.chatmore.viewmodel.PlazaMainViewModel
import com.google.android.flexbox.FlexboxLayout
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMMessage
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.glide.transformations.CropCircleTransformation
import me.jessyan.autosize.utils.AutoSizeUtils
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber


class SystemMessageFragment : BaseFragment<FragmentSystemMessageBinding, MessageFragmentViewModel>(), EMMessageListener {
    var tabBarSelectIndex: Int = 1
    var contentTabSelectIndex: Int = 0
    var friendCount: Int = 0
    override fun onMessageRecalled(p0: MutableList<EMMessage>?) {

    }

    override fun onMessageChanged(p0: EMMessage?, p1: Any?) {

    }

    override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {

    }

    override fun onMessageReceived(p0: MutableList<EMMessage>?) {
        mViewModel.loadUnreadMessage()
    }

    override fun onMessageDelivered(p0: MutableList<EMMessage>?) {

    }

    override fun onMessageRead(p0: MutableList<EMMessage>?) {

    }

    companion object {
        var mInstance: SystemMessageFragment? = null
        var matchStatusTitle: String? = null
        /**
         * 单例
         */
        fun instance(): SystemMessageFragment {
            if (mInstance == null) {
                return SystemMessageFragment()
            }
            return mInstance!!
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_system_message
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    override fun onViewModel(): MessageFragmentViewModel {
        return MessageFragmentViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        mViewModel.myMatchStatus()
        initBottomTab()
        initTab(0)
        mBinding.viewpagerCircle.adapter = mViewModel.getAdapter(childFragmentManager, 0)
        mBinding.viewpager.adapter = mViewModel.getAdapter(childFragmentManager, 1)
        mBinding.viewpagerCircle.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                initTab(position)
                if (position == 0) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare", ""))
                } else if (position == 1) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_Circle", ""))
                }
            }

        })
        mBinding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                initTab(position)
                if (position == 0) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_mymessage", ""))
                } else if (position == 1) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_myfriends", ""))
                }
            }

        })
        mBinding.txtMessageLeft.setOnClickListener {
            if (tabBarSelectIndex == 0) {
                if (mBinding.viewpagerCircle.currentItem != 0) {
                    initTab(0)
                    mBinding.viewpagerCircle.setCurrentItem(0, true)
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare", ""))
            } else {
                if (mBinding.viewpager.currentItem != 0) {
                    initTab(0)
                    mBinding.viewpager.setCurrentItem(0, true)
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_mymessage", ""))
            }
        }
        mBinding.txtMessageRight.setOnClickListener {
            if (tabBarSelectIndex == 0) {
                if (mBinding.viewpagerCircle.currentItem != 1) {
                    initTab(1)
                    mBinding.viewpagerCircle.setCurrentItem(1, true)
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_Circle", ""))
            } else {
                if (mBinding.viewpager.currentItem != 1) {
                    initTab(1)
                    mBinding.viewpager.setCurrentItem(1, true)
                }
                EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_myfriends", ""))
            }
        }
        mBinding.rlBack.setOnClickListener {
            //显示首页头部信息
            //            mActivity.onBackPressed()
            (activity as MainActivity).hideFragment2()
            EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_off", ""))
        }
        mBinding.ivBack.setOnClickListener {
            //            mActivity.onBackPressed()
            (activity as MainActivity).hideFragment2()
            EventTrackingUtils.joinPoint(EventBeans("ck_messagepage_off", ""))
        }

        mBinding.llFriend.setOnClickListener {
            //            Toast.makeText(mActivity, "即将上线，敬请期待", Toast.LENGTH_SHORT).show()
            CircleFragment.start(mActivity)
            mBinding.ivUnAvatar.visibility = View.GONE
            mBinding.ivUnNew.visibility = View.GONE
        }

        mBinding.rlSquare.setOnClickListener {
            tabBarSelectIndex = 0
            initBottomTab()
            initTab(0)
            mBinding.viewpagerCircle.setCurrentItem(0, true)
        }
        mBinding.rlChat.setOnClickListener {
            tabBarSelectIndex = 1
            initBottomTab()
            initTab(0)
            mBinding.viewpager.setCurrentItem(0, true)
        }
        mBinding.textView71.setOnClickListener {
            if (tabBarSelectIndex == 0) {
                //todo 我的关注
                PlazaTopicsFocusActivity.start(context!!)
                EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_myconcern", ""))
            } else {
                if (contentTabSelectIndex == 0) {
                    //允许匹配
                    showMatchSettingPop()
                } else {
                    //好友申请
                    ApplyActivity.start(mActivity)
                }
            }
        }
    }

    fun showMatchSettingPop() {
        var contentView = LayoutInflater.from(mActivity).inflate(R.layout.layout_match_switch_pop, null, false)
        var pop = PopupWindow(contentView, AutoSizeUtils.dp2px(mActivity, 130F), AutoSizeUtils.dp2px(mActivity, 140F), true)
        pop.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        pop.isOutsideTouchable = true
        pop.isTouchable = true
        pop.showAsDropDown(mBinding.textView71)
        contentView.findViewById<TextView>(R.id.tv_allow_match).setOnClickListener {
            mViewModel.setMatchStatus(1)
            matchStatusTitle = "允许匹配"
            pop.dismiss()
        }
        contentView.findViewById<TextView>(R.id.tv_only_square).setOnClickListener {
            mViewModel.setMatchStatus(2)
            matchStatusTitle = "仅允许广场匹配"
            pop.dismiss()
        }
        contentView.findViewById<TextView>(R.id.tv_onle_text).setOnClickListener {
            mViewModel.setMatchStatus(3)
            matchStatusTitle = "仅允许文字匹配"
            pop.dismiss()
        }
        contentView.findViewById<TextView>(R.id.tv_refuse_match).setOnClickListener {
            mViewModel.setMatchStatus(4)
            matchStatusTitle = "拒绝匹配"
            pop.dismiss()
        }
        pop.setOnDismissListener { mBinding.textView71.text = matchStatusTitle }
    }

    fun initTab(position: Int) {
        contentTabSelectIndex = position
        if (position == 0) {
            mBinding.txtMessageLeft.setTextColor(Color.parseColor("#A86902"))
            mBinding.txtMessageRight.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.txtMessageLeft.setBackgroundResource(R.drawable.tab_indicator)
            mBinding.txtMessageRight.setBackgroundResource(0)
        } else {
            mBinding.txtMessageLeft.setTextColor(Color.parseColor("#C4C4C4"))
            mBinding.txtMessageRight.setTextColor(Color.parseColor("#A86902"))
            mBinding.txtMessageLeft.setBackgroundResource(0)
            mBinding.txtMessageRight.setBackgroundResource(R.drawable.tab_indicator)
        }
        if (tabBarSelectIndex == 0) {
            mBinding.textView71.setText("我的关注")
            mBinding.ivLaba.setImageResource(R.drawable.icon_message_my_collect)
        } else {
            if (position == 0) {
                mBinding.textView71.setText("")
                mViewModel.myMatchStatus()
            } else {
                mBinding.textView71.setText("好友申请")
                mBinding.ivLaba.setImageResource(R.drawable.ic_laba)
            }
        }
    }

    fun initBottomTab() {
        if (MainActivity.unReadMessageCount + MainActivity.unReadFriendCount == 0) {
            mBinding.tvUnreadChatCount.visibility = View.GONE
        } else {
            mBinding.tvUnreadChatCount.visibility = View.VISIBLE
            mBinding.tvUnreadChatCount.text = (MainActivity.unReadMessageCount + MainActivity.unReadFriendCount).toString()
        }
        if (MainActivity.unReadCircleCount + MainActivity.unReadSquareCount == 0) {
            mBinding.tvUnreadSquareCount.visibility = View.GONE
        } else {
            mBinding.tvUnreadSquareCount.visibility = View.VISIBLE
            mBinding.tvUnreadSquareCount.setText((MainActivity.unReadCircleCount + MainActivity.unReadSquareCount).toString())
        }

        if (tabBarSelectIndex == 0) {
            mBinding.ivSquare.setImageResource(R.drawable.icon_message_square_select)
            mBinding.tvSquare.setTextColor(Color.parseColor("#FFEC1C"))
            mBinding.ivChat.setImageResource(R.drawable.icon_message_chat)
            mBinding.tvChat.setTextColor(Color.parseColor("#FFFFFF"))
            mBinding.txtMessageLeft.setText("广场")
            mBinding.txtMessageRight.setText("好友圈")
            mBinding.txtUnreadRight.visibility = View.GONE
            if (MainActivity.unReadCircleCount == 0) {
                mBinding.txtUnreadRight.visibility = View.GONE
            } else {
                mBinding.txtUnreadRight.visibility = View.VISIBLE
                mBinding.txtUnreadRight.setText(MainActivity.unReadCircleCount.toString())
            }
            if (MainActivity.unReadSquareCount == 0) {
                mBinding.txtUnreadLeft.visibility = View.GONE
            } else {
                mBinding.txtUnreadLeft.visibility = View.VISIBLE
                mBinding.txtUnreadLeft.setText(MainActivity.unReadSquareCount.toString())
            }
            mBinding.viewpagerCircle.visibility = View.VISIBLE
            mBinding.viewpager.visibility = View.GONE

        } else {
            mBinding.ivSquare.setImageResource(R.drawable.icon_message_square)
            mBinding.tvSquare.setTextColor(Color.parseColor("#FFFFFF"))
            mBinding.ivChat.setImageResource(R.drawable.icon_message_chat_select)
            mBinding.tvChat.setTextColor(Color.parseColor("#FFEC1C"))
            mBinding.txtMessageLeft.setText("消息")
            if (friendCount > 0) {
                mBinding.txtMessageRight.setText("好友 (${friendCount}人)")
            } else {
                mBinding.txtMessageRight.setText("好友")
            }
            if (MainActivity.unReadFriendCount == 0) {
                mBinding.txtUnreadRight.visibility = View.GONE
            } else {
                mBinding.txtUnreadRight.visibility = View.VISIBLE
                mBinding.txtUnreadRight.setText(MainActivity.unReadFriendCount.toString())
            }
            if (MainActivity.unReadMessageCount == 0) {
                mBinding.txtUnreadLeft.visibility = View.GONE
            } else {
                mBinding.txtUnreadLeft.visibility = View.VISIBLE
                mBinding.txtUnreadLeft.setText(MainActivity.unReadMessageCount.toString())
            }
            mBinding.viewpagerCircle.visibility = View.GONE
            mBinding.viewpager.visibility = View.VISIBLE
        }
    }

    @Subscribe
    fun onEventMessage(msg: EventMessage<Any>) {
        if (msg.tag.equals(EventConstant.TAG.TAG_MESSAGE_SYSTEM_FRAGMENT)) {
            when (msg.what) {
                EventConstant.WHAT.WHAT_MATCH_STATUS -> {
                    matchStatusTitle = msg.obj as String?
                    mBinding.textView71.text = matchStatusTitle
                }
            }
        }
        if (msg.tag.equals(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT)) {
            when (msg.what) {
                EventConstant.WHAT.WHAT_UNREAD_FRIEND_COUNT -> {
                    MainActivity.unReadFriendCount = msg.obj as Int
                }
                EventConstant.WHAT.WHAT_UNREAD_MESSAGE_COUNT -> {
                    MainActivity.unReadMessageCount = msg.obj as Int
                }
                EventConstant.WHAT.WHAT_UNREAD_CIRCLE_COUNT -> {
                    MainActivity.unReadCircleCount = msg.obj as Int
                }
            }
            initBottomTab()
        }
    }

    @Subscribe
    fun onSubscribeNumVM(obj: MessageFragmentViewModel.RefreshPeopleNumVM) {
        Timber.tag("" + System.currentTimeMillis().toString().substring(0, 10))
        if (tabBarSelectIndex == 1) {
            friendCount = obj.nums
            if (friendCount > 0) {
                mBinding.txtMessageRight.setText("好友 (${friendCount}人)")
            } else {
                mBinding.txtMessageRight.setText("好友")
            }
        }
    }

    @Subscribe
    fun onUnreadMessageVM(obj: MessageSystemViewModel.UnreadMessageVM) {

        //todo
        if (obj.isSuccess) {
            obj.list?.let {
                if (it.isNotEmpty()) {
//                    mBinding.txtMessageLeft.visibility = View.VISIBLE
                    mBinding.txtMessageLeft.text = "${it.size}"
                } else {
//                    mBinding.txtMessageLeft.visibility = View.GONE
                    mBinding.txtMessageLeft.text = "0"
                }
            }
        }
    }

    fun refresUnMsg() {
        mViewModel.netNewMsg()
    }

    @Subscribe
    fun onClearUnMsg(obj: MessageCircleViewModel.ClearUnMsgVM) {
        if (obj.success) {
            mBinding.ivUnAvatar.visibility = View.GONE
            mBinding.ivUnNew.visibility = View.GONE
//            mBinding.llFriend.visibility = View.GONE
        }
    }

    @Subscribe
    fun onRefreshUmMsg(obj: MessageFragmentViewModel.SmallUnReadMsgVM) {
        if (obj.success) {
            if (obj.obj.unReadCount != 0) {
//                mBinding.llFriend.visibility = View.VISIBLE
                mBinding.ivUnAvatar.visibility = View.VISIBLE
//                val requestOption = RequestOptions().circleCrop()
//                requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//                Glide.with(mContext).load(obj.obj.firstInfo?.avatar).apply(requestOption).into(mBinding.ivUnAvatar)
                Glide.with(mActivity).load(obj.obj.firstInfo?.avatar).asBitmap().transform(
                        CropCircleTransformation(mActivity)).crossFade().error(
                        R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black).into(mBinding.ivUnAvatar)
                mBinding.ivUnNew.visibility = View.VISIBLE
            } else {
//                mBinding.llFriend.visibility = View.GONE
                mBinding.ivUnAvatar.visibility = View.GONE
                mBinding.ivUnNew.visibility = View.GONE
            }
        } else {
//            mBinding.llFriend.visibility = View.GONE
            mBinding.ivUnAvatar.visibility = View.GONE
            mBinding.ivUnNew.visibility = View.GONE
        }
    }

    @Subscribe
    fun onRefreshFocusNumMsg(obj: PlazaMainViewModel.FollowNumVM) {
        var focusNum = obj.num
        if (focusNum > 0) {
            mBinding.txtUnreadFocus.text = if (focusNum > 99) "${focusNum}+" else "${focusNum}"
            mBinding.txtUnreadFocus.visibility = View.VISIBLE
        } else {
            mBinding.txtUnreadFocus.visibility = View.GONE
        }
    }

    @Subscribe
    fun onRefreshFlowNumMsg(obj: PlazaMainViewModel.FlowNumVM) {
        var focusNum = obj.num
        if (focusNum > 0) {
//            val content = "<font color='#FFFFFF' size='13'>根据您的喜好，为您推荐了</font> <font color='#FFEC1C' size='13'>${focusNum}条动态</font>"
//            mBinding.tvPlazaTip.niceHtml(content)
            mBinding.tvPlazaTip.visibility = View.VISIBLE
            mBinding.tvPlazaTip.alpha = 1f

            val ofFloat = ObjectAnimator.ofFloat(mBinding.tvPlazaTip, "alpha", 1f, 0f)
            ofFloat.startDelay = 3000
            ofFloat.duration = 500
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ofFloat.setAutoCancel(true)
            }
            ofFloat.start()

        } else {
            mBinding.tvPlazaTip.visibility = View.GONE
        }
    }

    @Subscribe
    fun OnSubscribeVM(obj: MessageFragmentViewModel.ShowUserInfoVM) {
        /**
         * 获取个人信息
         */
        PlazaSpaceActivity.start(context!!, obj.userId, null)

        /*val subscribe1 = mDataRepository.getUserDetailInfo(obj.userId).subscribe({
            showUserInfoDialog(
                    it.data!!)
        }, {})
        mViewModel.mCompositeDisposable.add(subscribe1)*/
    }

    private var dialog: Dialog? = null

    fun showUserInfoDialog(obj: UserDetailInfoResult.Data) {
        if (dialog == null) {
            dialog = Dialog(mActivity)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.dialog_userinfo)
            dialog?.getWindow()!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            dialog?.window!!.setBackgroundDrawable(
                    ColorDrawable(resources.getColor(android.R.color.transparent)))
        }
        dialog?.findViewById<TextView>(R.id.textView14)?.text = obj.userBaseInfo.nickname
        dialog?.findViewById<ImageView>(R.id.imageView24)?.setImageResource(
                if (obj.userBaseInfo.gender == "1") R.drawable.ic_gender_main_normal else R.drawable.ic_gender_woman_normal)
        dialog?.findViewById<TextView>(R.id.level_tv)?.text = obj.userBaseInfo.userLevel
        DataBindingUtils.loadAvatar(dialog?.findViewById<ImageView>(R.id.imageView23),
                obj.userBaseInfo.avatar)
        val sb = StringBuilder()
        sb.append(obj.userBaseInfo.age.toString())
        if (!TextUtils.isEmpty(obj.userExtraInfo.professionName)) {
            sb.append(" | ").append(obj.userExtraInfo.professionName)
        }
        if (!TextUtils.isEmpty(obj.userExtraInfo.address)) {
            sb.append(" | ").append(obj.userExtraInfo.address)
        }
        dialog?.findViewById<TextView>(R.id.txt_user_info)?.text = sb.toString()
        val subscribe = mDataRepository.getSigns(obj.userBaseInfo.userId).subscribe({
            if (it.errorCode != 200) {
                mActivity.niceToast(it.errorMsg!!)
                return@subscribe
            }
            dialog?.findViewById<FlexboxLayout>(
                    R.id.afl_cotent)?.removeAllViews()
            it.data?.tags?.forEach {
                val itemView = LayoutInflater.from(
                        niceChatContext()).inflate(
                        R.layout.item_tags3,
                        null)
                val textview = itemView.findViewById<TextView>(
                        R.id.txt_sign)
                textview.setSingleLine(
                        true)
                textview.text = it.tagName
                val params = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT)
                params.order = -1
                params.flexGrow = 2F
                dialog?.findViewById<FlexboxLayout>(
                        R.id.afl_cotent)?.addView(
                        itemView,
                        params)
            }
            if (!mActivity.isDestroyed && !mActivity.isFinishing) {
                dialog?.show()
            }
        })
        CompositeDisposable().add(subscribe)
//    dialog.show()

    }

    /**
     * 打开广场主页面
     */
    @Subscribe
    fun OnTaskStartSquareEvent(obj: TaskStartSquareEvent) {
        mActivity.runOnUiThread {
            tabBarSelectIndex = 0
            initBottomTab()
            initTab(0)
            mBinding.viewpagerCircle.setCurrentItem(0, true)
        }
    }
}