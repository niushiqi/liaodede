package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityContactBinding
import com.dyyj.idd.chatmore.model.network.result.ContactUser
import com.dyyj.idd.chatmore.ui.adapter.ContactAdapter
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.viewmodel.ContactViewModel
import com.dyyj.idd.chatmore.weiget.address.SideBar
import com.dyyj.idd.chatmore.weiget.pop.ContactPop
import org.greenrobot.eventbus.Subscribe

class ContactActivity: BaseActivity<ActivityContactBinding, ContactViewModel>(), SideBar.OnChooseLetterChangedListener {

    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onChooseLetter(s: String?) {
        val i = mViewModel.getAdapter().getFirstPositionByChar(s?.get(0)!!)
        if (i == -1) {
            return
        }
        linearLayoutManager?.scrollToPositionWithOffset(i, 0)
    }

    override fun onNoChooseLetter() {
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ContactActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_contact
    }

    override fun onViewModel(): ContactViewModel {
        return ContactViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "悄悄跟ta表白"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        initView()
    }

    private fun initView() {
        mViewModel.netGetMatch(this)
//        mViewModel.getContactMessage(this@ContactActivity)
        mBinding.txtSend.setOnClickListener {
            if (mViewModel.UserSelect != null) {
                showProgressDialog()
                mViewModel.netSendMessage(mViewModel.UserSelect!!.phone)
            }
        }
    }

    @Subscribe
    fun onContactVM(obj: ContactViewModel.ContactVM) {
        initRecycleView(obj.obj)
        val phones = StringBuilder()
        obj.obj.forEachIndexed { index, contactUser ->
            phones.append(contactUser.phone.trim())
            if (index != (obj.obj.size - 1)) {
                phones.append(",")
            }
        }
        mViewModel.netUploadContact(phones = phones.toString())
    }

    @Subscribe
    fun onContactSendVM(obj: ContactViewModel.ContactSendVM) {
        closeProgressDialog()
        if (obj.success) {
            val list = mViewModel.getAdapter().getList()
            list.remove(mViewModel.UserSelect)
            mViewModel.mMatchCurrentUserData.add(mViewModel.UserSelect!!)
            headView?.findViewById<ConstraintLayout>(R.id.cl_like)?.visibility = View.VISIBLE
            headView?.findViewById<TextView>(R.id.textView43)?.text = "已选择的暗恋（${mViewModel.mMatchCurrentUserData.size}）"
            headView?.findViewById<TextView>(R.id.textView57)?.text = mViewModel.mMatchCurrentUserData[mViewModel.mMatchCurrentUserData.size - 1].name
//            val params = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dp2px(resources, if ((mViewModel.mMatchCurrentUserData == null) and (mViewModel.mMatchCurrentUserData?.size != 0)) 285f else 200f).toInt())
            val params = headView?.layoutParams
            params?.height = DeviceUtils.dp2px(resources, if ((mViewModel.mMatchCurrentUserData != null) and (mViewModel.mMatchCurrentUserData?.size != 0)) 285f else 200f).toInt()
            val paramsSpace = mBinding.spaecTop.layoutParams
            paramsSpace?.height = DeviceUtils.dp2px(resources, if ((mViewModel.mMatchCurrentUserData != null) and (mViewModel.mMatchCurrentUserData?.size != 0)) 285f else 200f).toInt()
            mViewModel.getAdapter().posSelect = -1
            mViewModel.getAdapter().notifyDataSetChanged()
//            headView?.layoutParams = params
            val contactPop = ContactPop(this@ContactActivity)
            contactPop.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
        }
    }

    private var headView: View? = null

    fun initRecycleView(list: List<ContactUser>) {
        //设置线
        mViewModel.getAdapter().initData(list)

        headView = LayoutInflater.from(this).inflate(R.layout.item_contact_head, null)
        if ((mViewModel.mMatchCurrentUserData != null) and (mViewModel.mMatchCurrentUserData?.size != 0)) {
            headView?.findViewById<ConstraintLayout>(R.id.cl_like)?.visibility = View.VISIBLE
            headView?.findViewById<TextView>(R.id.textView43)?.text = "已选择的暗恋（${mViewModel.mMatchCurrentUserData.size}）"
            headView?.findViewById<TextView>(R.id.textView57)?.text = mViewModel.mMatchCurrentUserData[mViewModel.mMatchCurrentUserData.size - 1].name
        } else {
            headView?.findViewById<ConstraintLayout>(R.id.cl_like)?.visibility = View.GONE
        }
        headView?.findViewById<ConstraintLayout>(R.id.cl_like)?.setOnClickListener {
            ContactLikeActivity.start(this, mViewModel.mMatchCurrentUserData)
        }
        val paramsSpace = mBinding.spaecTop.layoutParams
        paramsSpace?.height = DeviceUtils.dp2px(resources, if ((mViewModel.mMatchCurrentUserData != null) and (mViewModel.mMatchCurrentUserData?.size != 0)) 285f else 200f).toInt()
        val params = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dp2px(resources, if ((mViewModel.mMatchCurrentUserData != null) and (mViewModel.mMatchCurrentUserData?.size != 0)) 285f else 200f).toInt())
        headView?.layoutParams = params
        mBinding.rvContact.addHeaderView(headView)

        mBinding.rvContact.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        linearLayoutManager = LinearLayoutManager(this)
        mBinding.rvContact.layoutManager = linearLayoutManager
        mBinding.rvContact.adapter = mViewModel.getAdapter()

        mBinding.hitesidebar.setOnChooseLetterChangedListener(this)
    }

    @Subscribe
    fun onShowSendVM(obj: ContactAdapter.ShowSendVM) {
        mViewModel.UserSelect = obj.user
        mBinding.txtSend.visibility = View.VISIBLE
    }

    @Subscribe
    fun onFinishViewVM(obj: ContactPop.FinishViewVM) {
        finish()
    }
}