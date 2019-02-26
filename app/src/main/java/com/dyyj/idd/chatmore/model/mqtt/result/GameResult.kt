package com.dyyj.idd.chatmore.model.mqtt.result

data class GameResult(
//        “gameId”:xx,””aNum”:”10130,xxx”,”bNum”:”10131,xxx”,”rs”:0
        var gameId: Int? = 0,
        var aNum: String? = "",
        var bNum: String? = "",
        var rs: Int? = 0
)