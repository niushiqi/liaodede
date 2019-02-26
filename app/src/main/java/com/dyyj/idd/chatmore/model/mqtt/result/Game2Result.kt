package com.dyyj.idd.chatmore.model.mqtt.result

//“gameId”:xx,””aNum”:”10130,xxx”,”bNum”:”10131,xxx”,”rs”:0
//aNum:用户a得分
//bNum:用户b得分
//rs:本轮结果 2 a胜 3 b胜
data class Game2Result(
        val gameId: Int? = 0,
        val aNum: String? = "",
        val bNum: String? = ""
)