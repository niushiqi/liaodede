package com.dyyj.idd.chatmore.ui.plaza.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dyyj.idd.chatmore.BR
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivitySpaceBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity
import com.dyyj.idd.chatmore.ui.user.activity.ShopActivity
import com.dyyj.idd.chatmore.ui.user.fragment.MessageSquareFragment
import com.dyyj.idd.chatmore.utils.AnimationUtils
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.PlazaMainViewModel
import com.dyyj.idd.chatmore.viewmodel.PlazaSpaceViewModel
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation
import com.othershe.nicedialog.NiceDialog
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * 我的空间
 * */
class PlazaSpaceActivity : BaseActivity<ActivitySpaceBinding, PlazaSpaceViewModel>() {

    //临时发版，赶时间，匆忙中加这个字段
    var num: Int = 0

    companion object {
        const val KEY_ID = "ID"
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"

        fun start(context: Context, id: String?, topicID: String? = null) {
            val intent = Intent(context, PlazaSpaceActivity::class.java)
            intent.putExtra(KEY_ID, id ?: "")
            intent.putExtra(KEY_TOPIC_ID, topicID ?: "")
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_space
    }

    override fun onViewModel(): PlazaSpaceViewModel {
        return PlazaSpaceViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (true) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//白色
        }

        onCreateEvenbus(this)

        mViewModel.mUserId = intent.getStringExtra(KEY_ID)
        mViewModel.mTopicID = intent.getStringExtra(KEY_TOPIC_ID)

        initToobar()
        initView()

        mViewModel.getUserInfo(mBinding)
        mViewModel.getSignData(mBinding)
        checkChat()
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEvenbus(this)
    }

    private fun initToobar() {
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.let {
            it.text = mViewModel.getTitle()
        }
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener {
            onBackPressed()
        }

        mBinding.layoutToolbar?.setVariable(BR.vm, mViewModel)

        if (mViewModel.isSelf) {
            mViewModel.getSquareUnreadMessage()
        }

        mBinding.layoutToolbar?.txtMessage?.visibility = if (mViewModel.isSelf) View.VISIBLE else View.GONE
    }

    /**
     * 初始化动态
     */
    fun initView() {
        mBinding.vp.offscreenPageLimit = 0
        mBinding.vp.adapter = mViewModel.getAdapter(supportFragmentManager, mViewModel.mUserId)
        mBinding.layoutToolbar?.tl4?.setViewPager(mBinding.vp)
        mBinding.vp.offscreenPageLimit = 1
//        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//            }
//
//            override fun onPageSelected(position: Int) {
//
//            }
//
//        })
    }

    /**
     * 清空未读消息
     */
    @Subscribe
    fun onClearSquareUnMsg(obj: MessageSquareFragment.ClearSquareUnMsgVM) {
        if (obj.success) {
//            mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.GONE
            mBinding.layoutToolbar?.rlTip?.visibility = View.GONE
        }
    }

    /**
     * 未读消息
     */
    @Subscribe
    fun onSubscribeUnMsg(obj: PlazaMainViewModel.SquareUnreadMessageVM) {
        if (obj.success) {
            setUnRead(obj.obj ?: 0, obj.avatar ?: "")
        }
    }

    /**
     * 设置未读数量
     */
    fun setUnRead(num: Int, img: String?) {
        val layoutToolbar = mBinding.layoutToolbar

        if (num > 0) {
//                mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.VISIBLE
            layoutToolbar?.rlTip?.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(img)) {
                Glide.with(this).load(img).asBitmap()
                        .transform(CropCircleTransformation(layoutToolbar?.root?.context))
                        .crossFade().error(R.drawable.bg_circle_black)
                        .placeholder(R.drawable.bg_circle_black)
                        .into(layoutToolbar?.ivAvatar)
            }

            this.num = num
            layoutToolbar?.txtMsgNum?.text = "${num}条新消息"
        } else {
            layoutToolbar?.rlTip?.visibility = View.GONE
        }
    }

    /**
     * 好友未读消息
     */
    @Subscribe
    fun onNewMsg(obj: PlazaMainViewModel.MeUnReadMsgVM) {
        val layoutToolbar = mBinding.layoutToolbar

        if (obj.success) {
            if (obj.obj.unReadCount?:0 > 0) {
                layoutToolbar?.rlTip?.visibility = View.VISIBLE

                if (!TextUtils.isEmpty(obj.obj.firstInfo!!.avatar)) {
                    Glide.with(this).load(obj.obj.firstInfo!!.avatar).asBitmap()
                            .transform(CropCircleTransformation(layoutToolbar?.root?.context))
                            .crossFade().error(R.drawable.bg_circle_black)
                            .placeholder(R.drawable.bg_circle_black)
                            .into(layoutToolbar?.ivAvatar)
                }

                var numTotal = obj.obj.unReadCount!!.plus(this.num)
                layoutToolbar?.txtMsgNum?.text = "${numTotal}条新消息"
            }
        }
    }

    fun checkChat() {
        if (mViewModel.isSelf) {
            mBinding.tvChat.visibility = View.GONE
            return
        }

        mViewModel.getConsumeStoneData(mBinding)
        mViewModel.getMatchStatus(mBinding)
    }

    fun onClickChat(view: View) {
        val toUserid = mViewModel.mUserId
        mDataRepository.startSquareMessage(mDataRepository.getUserid()!!, toUserid)
                .subscribe({ result ->

                    if (mViewModel.userInfo == null) return@subscribe

                    if (result.errorCode == 200) {
                        //不是好友,消耗10魔石开始聊天
                        if (result.data!!.endTimestamp == "") {
                            //只有首次进入才消耗魔石
                            startChart(view.context, mViewModel.mTopicID)
                        } else {
                            AnimationUtils.start(view.context, mBinding.ivPersonMoshi, "xiahaomoshi10", 1, 40, 30, object : MyFrameAnimation.OnFrameAnimationListener {
                                override fun onStartFrameAnimation() {
                                    mBinding.ivPersonMoshi.setVisibility(View.VISIBLE)
                                }

                                override fun onEnd() {
                                    mBinding.ivPersonMoshi.setVisibility(View.GONE)
                                    startChart(view.context, mViewModel.mTopicID)
                                }
                            })
                        }
                    } else if (result.errorCode == 3017) {
                        //已经是好友
                        startChart(view.context)

                    } else if (result.errorCode == 3026) {
                        //不允许广场匹配
                        Toast.makeText(view.context, "对方设置了拒绝接收广场消息，开启聊天偶遇ta", Toast.LENGTH_SHORT).show()
                    } else if (result.errorCode == 3005) {
                        showDialog()
                    }
                }, { throwable -> })
        EventTrackingUtils.joinPoint(EventBeans("ck_publicsquare_privatechat1", toUserid))
    }

    fun showDialog() {
        //魔石不足弹窗
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_magic_stone_not_enough)
                .setConvertListener { holder, dialog ->
                    holder.setOnClickListener(R.id.store_btn) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_stonelack_toshop", ""))
                        dialog.dismiss()
                        ShopActivity.start(dialog.context!!, 0)
                    }
                    holder.setOnClickListener(R.id.gift_btn) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_stonelack_totask", ""))
                        dialog.dismiss()
                        MainActivity.startOpenCall(dialog.context!!)
                    }
                    holder.setOnClickListener(R.id.close_iv) {
                        EventTrackingUtils.joinPoint(EventBeans("ck_stonelack_close", ""))
                        dialog.dismiss()
                    }
                }
                .setDimAmount(0.8f)
                .setShowBottom(false)
                .setWidth(260)
                .setHeight(300)
                .setOutCancel(false)
                .show(supportFragmentManager)
    }

    fun startChart(context: Context, topicID: String? = null) {
        mBinding.ivPersonMoshi.setVisibility(View.GONE)

        val userBaseInfo = mViewModel.userInfo?.userBaseInfo

        val toUserid = userBaseInfo?.userId!!
        val nickname = userBaseInfo.nickname
        val avatar = userBaseInfo.avatar

        if (TextUtils.isEmpty(topicID)) {
            ChatActivity.start(context, toUserid, nickname, avatar, null)
        } else {
            ChatActivity.startBySquare(context, toUserid, nickname, avatar, ArrayList(Arrays.asList(topicID!!)))
        }

    }
}