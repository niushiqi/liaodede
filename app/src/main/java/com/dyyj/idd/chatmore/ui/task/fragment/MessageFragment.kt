package com.dyyj.idd.chatmore.ui.task.fragment

import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentMessageBinding
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.listener.OnClickItemListener
import com.dyyj.idd.chatmore.model.database.entity.MessageEntity
import com.dyyj.idd.chatmore.ui.user.activity.ApplyActivity
import com.dyyj.idd.chatmore.viewmodel.ApplyViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageViewModel
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 消息列表
 */
class MessageFragment : BaseFragment<FragmentMessageBinding, MessageViewModel>(), OnClickItemListener, EMMessageListener {
  override fun onLayoutId(): Int {
    return R.layout.fragment_message
  }

  override fun onViewModel(): MessageViewModel {
    return MessageViewModel()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    onCreateEvenBus(this)
//    mViewModel.loadConversationList(false)
    if (EMClient.getInstance().chatManager() != null) {
        EMClient.getInstance().chatManager().addMessageListener(this)
    }
    mBinding.tvApplyNum.setOnClickListener { ApplyActivity.start(mActivity) }
  }

  override fun onStart() {
    super.onStart()
  }

  override fun onResume() {
    super.onResume()
    mViewModel.getApplyNum()
    Handler().postDelayed({mViewModel.loadConversationList(false)},300)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    onDestryEvenBus(this)
    if (EMClient.getInstance().chatManager() != null) {
      EMClient.getInstance().chatManager().removeMessageListener(this)
    }
  }

  private fun initRecycleView(list: List<MessageEntity>) {
    if (mBinding.recyclerview.adapter == null) {
      //设置线
      val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
          ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
          R.dimen.item_decoration_2px).build()
        if (mViewModel.getNewMessage()) mViewModel.getAdapter().isHeader = true
        mViewModel.getAdapter().setListener(this)
        mViewModel.getAdapter().initData(list)
        mBinding.recyclerview.addItemDecoration(decoration)
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
    } else {
      mViewModel.getAdapter().initData(list)
      mViewModel.getAdapter().notifyDataSetChanged()
    }
  }

  override fun onClickItem(view: View, any: Any, position: Int) {
    mViewModel.celarNewMessage()
    mViewModel.getAdapter().isHeader = false
    mViewModel.getAdapter().notifyDataSetChanged()
  }

  @Subscribe
  fun onEventMessage(msg: EventMessage<Any>) {
/*    if (msg.tag.equals(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT)){
      when(msg.what){
        EventConstant.WHAT.WHAT_UNREAD_FRIEND_COUNT -> {
          var count: Int = msg.obj as Int
          if (count > 0){
            mBinding.tvApplyNum.text = "${count}条好友申请"
            mBinding.tvApplyNum.visibility = View.VISIBLE
          }else{
            mBinding.tvApplyNum.visibility = View.GONE
          }
        }
      }
    }*/
  }

  @Subscribe
  fun onMessageVM(obj: MessageViewModel.MessageVM) {
    obj?.let {
      initRecycleView(it.obj)
      if (obj.obj.isEmpty()) {
        mBinding.clEmpty.visibility = View.VISIBLE
      } else {
        mBinding.clEmpty.visibility = View.GONE
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onRefreshVM(obj: MessageViewModel.RefreshVM) {
    obj?.let {
      mViewModel.getAdapter().initData(obj.obj)
      mViewModel.getAdapter().notifyDataSetChanged()
      if (obj.obj.isEmpty()) {
        mBinding.clEmpty.visibility = View.VISIBLE
      } else {
        mBinding.clEmpty.visibility = View.GONE
      }
    }
  }

  @Subscribe
  fun onMessageRefreshVM(obj: ApplyViewModel.MessageRefreshVM) {
    mViewModel.loadConversationList(false)
  }

  override fun onMessageRecalled(p0: MutableList<EMMessage>?) {
  }

  override fun onMessageChanged(p0: EMMessage?, p1: Any?) {
  }

  override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {
  }

  override fun onMessageReceived(p0: MutableList<EMMessage>?) {
    mViewModel.loadConversationList(false)
  }

  override fun onMessageDelivered(p0: MutableList<EMMessage>?) {
  }

  override fun onMessageRead(p0: MutableList<EMMessage>?) {
  }
}