package com.dyyj.idd.chatmore.model.mqtt.params


data class MatchingBackupCancel(var messageType: String? = "", var data: Data? = Data()) {

  data class Data(var title: String? = "", var content: String? = "")
}