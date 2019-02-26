package com.dyyj.idd.chatmore.ui.main.fragment

import android.os.Bundle
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentConnetingBinding
import com.dyyj.idd.chatmore.model.easemob.StatusTag
import com.dyyj.idd.chatmore.model.easemob.message.VoiceMessage
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult
import com.dyyj.idd.chatmore.utils.DataBindingUtils
import com.dyyj.idd.chatmore.viewmodel.ConnetingViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/07
 * desc   : 连接中(呼叫中)
 */
class ConnetingFragment : BaseFragment<FragmentConnetingBinding, ConnetingViewModel>() {

    private var mFromUserid: String? = null
    private var mToUserid: String? = null
    private var mType: String? = null

    companion object {
        var mInstance: ConnetingFragment? = null
        fun instance(): ConnetingFragment {
            if (mInstance == null) {
                return ConnetingFragment()
            }
            return mInstance!!
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_conneting
    }

    override fun onViewModel(): ConnetingViewModel {
        return ConnetingViewModel()
    }

    fun setFromUserid(userid: String): ConnetingFragment {
        mFromUserid = userid
        return this
    }

    fun setToUserid(userid: String): ConnetingFragment {
        mToUserid = userid
        return this
    }

    fun setType(type: String): ConnetingFragment {
        mType = type
        return this
    }

    fun setTypeCall(is_calling: String): ConnetingFragment{
        mViewModel.mTypeCall = is_calling
        return this
    }

    fun setFromType(fromType: String): ConnetingFragment{
        mViewModel.mfromType = fromType
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        mToUserid?.let {
            mViewModel.netGetMatchingUserBaseInfo(it)
        }

        initListener()

        //创建60s超时定时器 - 对方60s无应答:主叫方开启，被叫方不开启
        if(mViewModel.mTypeCall == "true") {
            mViewModel.callTimeoutTimer(mActivity, mFromUserid!!, mToUserid!!, mType!!,
                    "", "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    private fun initListener() {
        mBinding.hangUpLl.setOnClickListener {
            if (mViewModel.mCallSuccess) {
                //1对1通话：A/B挂断，A/B的处理逻辑
                mViewModel.callHangUpModel(mActivity, mFromUserid!!, mToUserid!!, mType!!,
                        mBinding.model!!.matchingUserAvatar!!, mBinding.model!!.matchingUserNickname!!)
            } else {
                //1对1通话：A/B取消，A/B的处理逻辑
                mViewModel.callCancelModel(mActivity, mFromUserid!!, mToUserid!!, mType!!,
                        mBinding.model!!.matchingUserAvatar!!, mBinding.model!!.matchingUserNickname!!, mViewModel.mTypeCall!!)
            }
        }
    }


    /**
     * 初始化view
     */
    private fun initView(model: StartMatchingResult.Data) {
        mBinding.model = model
        DataBindingUtils.loadAvatar(mBinding.avatarIv, model.matchingUserAvatar)
        mBinding.descTv.text = if (mType == StatusTag.TYPE_VOICE) "语音呼叫中..." else "视频呼叫中..."
    }

    @Subscribe
    fun onViewModel(vm: ConnetingViewModel.StartMatchingVM) {
        if (vm.isSuccess) {//拨号
            vm.obj?.data?.matchingUserId?.let {
                initView(vm.obj.data)
            }
        }
    }

    /**
     * 对方点击拒绝
     */
    @Subscribe
    fun onVoiceMessage(obj: VoiceMessage) {
        if (mFromUserid == null || mToUserid == null || mType == null) return
        if (obj.status == StatusTag.STATUS_REJEC) {
//      EventBus.getDefault().postSticky(mViewModel.getVoiceMessageReject(mFromUserid!!, mToUserid!!, mType!!))
            mActivity.finish()
        }
    }

    /**
     *  通话建立成功
     */
    @Subscribe
    fun onVoiceStartCallVM(obj: ConnetingViewModel.VoiceStartCallVM) {
        if (obj.isSuccess) {
            mViewModel.mCallSuccess = true
            //显示挂断图标,EventBus订阅和接收者在同一线程，只能在主线程中更新UI
            mActivity.runOnUiThread {
                mBinding.hangUpLl.text = "挂断"
            }
            //开始通话计时
            mViewModel.mCallStartTime = System.currentTimeMillis()
        }
    }

}