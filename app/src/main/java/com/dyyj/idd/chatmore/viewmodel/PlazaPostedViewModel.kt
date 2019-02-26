package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.StatusResult
import com.dyyj.idd.chatmore.ui.adapter.PlazaPublishAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 广场发帖
 */
class PlazaPostedViewModel : ViewModel() {
    private val mAdapter by lazy { PlazaPublishAdapter() }

    fun getAdapter() = mAdapter

    fun netPublish(squareTopicId: String?, photoStrings: List<String>, body: String): Flowable<StatusResult> {
        return mDataRepository.postUploadPicPlaza(photoStrings)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    val photoes = StringBuilder()
                    val length = it.size
                    it.forEachIndexed { index, picResult ->
                        picResult.data?.imageFilename?.let {
                            if (picResult.data.imageFilename.isNotBlank()) {
                                photoes.append(picResult.data?.imageFilename)
                                if (index != (length - 1)) {
                                    photoes.append(",")
                                }
                            }
                        }
                    }

                    mDataRepository.postTopicComment(mDataRepository.getUserid()!!, squareTopicId, photoes.toString(), body)
                }
                .observeOn(AndroidSchedulers.mainThread())

    }

    class PublishSuccess()
}