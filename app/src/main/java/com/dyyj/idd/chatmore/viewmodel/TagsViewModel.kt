package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.TagsResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class TagsViewModel : ViewModel() {
    val allTags = arrayListOf<TagsResult.Data.Tag>()
    fun netTags(userId: String, gender: String) {
        val subscribe = mDataRepository.getTagList(userId, gender).subscribe(
                {
                    if (it.errorCode == 200) {
                        allTags.addAll(it.data?.tags!!)
                        EventBus.getDefault().post(AllTagsVM(it.errorCode == 200, it.data))
                    } else {
                        EventBus.getDefault().post(AllTagsVM(false))
                    }
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                },
                {EventBus.getDefault().post(AllTagsVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netSaveTag(userId: String, tags: String) {
        val subscribe = mDataRepository.saveTag(userId, tags).subscribe(
                {EventBus.getDefault().post(SaveTagsVM(it.errorCode == 200))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                },
                {EventBus.getDefault().post(SaveTagsVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    class AllTagsVM(val success: Boolean, val obj: TagsResult.Data? = null)

    class SaveTagsVM(val success: Boolean)
}