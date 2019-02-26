package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityInviteNewBinding
import com.dyyj.idd.chatmore.model.network.result.SystemMessages
import com.dyyj.idd.chatmore.viewmodel.InviteNewViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class InviteNewActivity : BaseActivity<ActivityInviteNewBinding, InviteNewViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, InviteNewActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_invite_new
    }

    override fun onViewModel(): InviteNewViewModel {
        return InviteNewViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
        mViewModel.getSystemMessages()
    }

    private fun initView() {
        initRecycleView(arrayListOf(0, 1))
    }

    private fun initText(arrays: List<SystemMessages.Data>) {
        var nickname:String? = ""
        if (arrays[0].name?.length?:0 <= 2) {
            nickname = arrays[0].name
        } else {
            nickname = "${arrays[0].name?.substring(0, 1)}***${arrays[0].name?.substring(arrays[0].name?.length!! - 1, arrays[0].name?.length!!)}"
        }
        mBinding.textView31.text = "${nickname}累计邀新${arrays[0].num}人,已赚取${2 * arrays[0].num!!}元"
        mBinding.textView30.text = ""
        val subscribeTime = Flowable.interval(0, 4, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe({
            mBinding.root.post {
                val temp = it % 2
                var nickname:String? = ""
                if (arrays[(it % arrays.size).toInt()].name?.length?:0 <= 2) {
                    nickname = arrays[(it % arrays.size).toInt()].name
                } else {
                    nickname = "${arrays[(it % arrays.size).toInt()].name?.substring(0, 1)}***${arrays[(it % arrays.size).toInt()].name?.substring(arrays[(it % arrays.size).toInt()].name?.length!! - 1, arrays[(it % arrays.size).toInt()].name?.length!!)}"
                }
                if (temp.toInt() == 0) {
                    //第二页
                    mBinding.textView31.text = "${nickname}刚邀请到1位好友,将赚取2元!"
                    mBinding.textView30.text = ""
                    com.dyyj.idd.chatmore.utils.AnimationUtils.startTextTransAnim(mBinding.rlText2, mBinding.rlText1)
                } else {
                    //第一页
                    mBinding.textView41.text = "${nickname}累计邀新${arrays[(it % arrays.size).toInt()].num}人,已赚取${2 * arrays[(it % arrays.size).toInt()].num!!}元,真棒！"
                    mBinding.textView40.text = ""
                    com.dyyj.idd.chatmore.utils.AnimationUtils.startTextTransAnim(mBinding.rlText1, mBinding.rlText2)
                }
            }
        })
        mViewModel.mCompositeDisposable.add(subscribeTime)
    }

    fun initRecycleView(list: List<Any>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(this@InviteNewActivity).color(
                ContextCompat.getColor(this@InviteNewActivity, R.color.divider_home)).sizeResId(
                R.dimen.item_home_decoration_15).build()
        mViewModel.getAdapter().initData(list)
        mBinding.rvInviteNotify.addItemDecoration(decoration)
        mBinding.rvInviteNotify.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvInviteNotify.layoutManager = LinearLayoutManager(this@InviteNewActivity)
        mBinding.rvInviteNotify.adapter = mViewModel.getAdapter()

    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "邀请活动"

    }

    @Subscribe
    fun onSystemMessageVM(obj: InviteNewViewModel.SystemMessageVM) {
        if (obj.success) {
            initText(obj.obj!!)
        }
    }

}