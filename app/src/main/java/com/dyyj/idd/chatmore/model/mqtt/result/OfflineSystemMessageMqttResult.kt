package com.dyyj.idd.chatmore.model.mqtt.result

/**
 * Created by wangbin on 2018/12/12.
 */
//data class OfflineSystemMessageMqttResult(val errorCode: Int? = 0, val errorMsg: String? = "", val data: List<Message>? = arrayListOf()) {

data class OfflineSystemMessageMqttResult(val msg: String? = "", val type: String? = "", val userId: String? = "", val ctime: String? = "", val router: Router? = Router()) {

    data class Router(val activityName: String? = "", val flag: String? = "", val param: List<Field>? = arrayListOf()) {

        data class Field(val type: String? = "", val key: String? = "", val value: String? = "")
    }
}
//}