package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentStoneShopBinding
import com.dyyj.idd.chatmore.ui.adapter.StoneShopAdapter
import com.dyyj.idd.chatmore.ui.user.activity.PayActionActivity
import com.dyyj.idd.chatmore.viewmodel.StoneShopViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/14
 * desc   : 魔石商城
 */

class StoneShopFragment : BaseFragment<FragmentStoneShopBinding, StoneShopViewModel>() {
  override fun onLayoutId(): Int {
    return R.layout.fragment_stone_shop
  }

  override fun onViewModel(): StoneShopViewModel {
    return StoneShopViewModel()
  }

  override fun lazyLoad() {
    super.lazyLoad()
    mViewModel.netRecycleView()
    initRecycleView()
  }

  private fun initRecycleView() {
    //设置线
    val decoration = HorizontalDividerItemDecoration.Builder(mActivity).color(
            ContextCompat.getColor(mActivity, R.color.divider_home)).sizeResId(
            R.dimen.item_decoration_2px).build()

    mBinding.rvStoneShop.layoutManager = GridLayoutManager(mActivity, 2)
  }

  @Subscribe
  fun OnSubscribeVM(obj: StoneShopViewModel.StoneListVM) {
    val data = obj.obj
    val myadapter = StoneShopAdapter(mActivity, data)

    mBinding.rvStoneShop.adapter = myadapter

    myadapter.setOnItemClickListener{
      baseQuickAdapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, i: Int ->
      PayActionActivity.start(mActivity, data!!.get(i).goodsId!!,data.get(i).goodsPrice!!,data.get(i).goodsDesc!! )
    }
  }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    onCreateEvenBus(this)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onDestroyView() {
    onDestryEvenBus(this)
    super.onDestroyView()
  }

}