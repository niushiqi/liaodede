package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/11.
 */
data class OfflineSystemMessageResult(val errorCode: Int? = 0, val errorMsg: String? = "", val data: List<Message>? = arrayListOf()) {

    //data class Data(val messagesList: List<Message>?) {

    //data class Message(, val type: String?, val msg: String?, val ctime: String?, val router: Router?) {
    data class Message(val type: String? = "", val msg: String? = "", val router: Router? = Router(), val ctime: String? = "", val userId: String? = "") {

        data class Router(val activityName: String? = "", val flag: String? = "", val param: List<Field>? = arrayListOf()) {

            //data class Param(val fieldList: List<Field>?) {

            data class Field(val type: String? = "", val key: String? = "", val value: String? = "")
            //}
        }
    }
    //}
}