package com.dyyj.idd.chatmore.model.network.result

import com.contrarywind.interfaces.IPickerViewData

data class AreaResult(
        val errorCode: Int?= 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val areaList: List<Area>? = listOf()
    ) {
        data class Area(
                val areaCodeId: String? = "",
                val areaName: String? = ""
        ) : IPickerViewData {
            override fun getPickerViewText(): String {
                return areaName!!
            }

        }
    }
}