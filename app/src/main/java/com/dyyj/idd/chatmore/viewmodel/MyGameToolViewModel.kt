package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.GameToolsResult
import com.dyyj.idd.chatmore.weiget.TabEntity
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class MyGameToolViewModel : ViewModel() {


    private val mFragmens = arrayListOf<Fragment>()

    /**
     * 获取道具数据
     */
    fun netIndex() {
        val subscribe = mDataRepository.postIndex().subscribe({
            EventBus.getDefault().post(
                    GameToolsVM(it.errorCode == 200,
                            it.data))
        }, {
            niceToast(it.message)
        })
        mCompositeDisposable.add(subscribe)
    }

    fun getItem(obj: GameToolsResult.Data.Prop): TabEntity {

        when (obj.id) {
        //葫芦娃系列
            "1"->{
                return TabEntity("", R.drawable.ic_huluwa_selected, R.drawable.ic_huluwa_normal)
            }
            "2"->{
                return TabEntity("", R.drawable.ic_huluwa_selected, R.drawable.ic_huluwa_normal)
            }
        //同福客栈系列
            "3"->{
                return TabEntity("", R.drawable.ic_tongfukezhan_selected, R.drawable.ic_tongfukezhan_normal)
            }
        //西游记系列
            "4"->{return TabEntity("", R.drawable.ic_xiyouji_selected, R.drawable.ic_xiyouji_normal)
            }
        //神奇动物系列
            "5"->{
                return TabEntity("", R.drawable.ic_shengqidongwu_selected, R.drawable.ic_shengqidongwu_normal)
            }
        //魔法系列
            else ->{
                return TabEntity("", R.drawable.ic_halibote_selected, R.drawable.ic_halibote_normal)
            }
        }
    }

    class GameToolsVM(val isSuccess: Boolean, val obj: GameToolsResult.Data? = null)
}