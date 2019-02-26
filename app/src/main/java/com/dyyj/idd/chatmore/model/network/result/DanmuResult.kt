package com.dyyj.idd.chatmore.model.network.result

data class DanmuResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val barrage: List<Danmu>? = listOf()
    ) {
        data class Danmu(
                val barrageContent: String? = ""
        )
    }
}

//{"errorCode":200,"errorMsg":"","data":{"barrage":[{"barrageContent":"BGM\u7231\u7684\u81ea\u6740\uff0c\u518d\u95ee\u4f9b\u517b"},{"barrageContent":"\u82b1\u74e3\u6fa1\uff0c\u54c8\u54c8\u54c8\u54c8\u54c8\u54c8"},{"barrageContent":"\u6559\u7ec3\uff0c\u6211\u60f3\u804a\u5929"},{"barrageContent":"\u6c42BMG"},{"barrageContent":"\u6211\u80d6\u864e\u4eca\u5929\u8981\u6253\u6b7b\u4f60"},{"barrageContent":"6666666"},{"barrageContent":"\u4e0b\u5df4\u6253\u5b57\u4ee5\u793a\u6e05\u767d"},{"barrageContent":"\u8bf4\u9ad8\u80fd\u7684\u51fa\u6765"},{"barrageContent":"\u4eca\u5929\u7684\u98ce\u513f\u5f88\u662f\u55a7\u56a3\u554a"},{"barrageContent":"2333333333"},{"barrageContent":"\u8033\u6735\u6000\u5b55\u4e86\uff01"},{"barrageContent":"\u6709\u7f51\u604b\u7684\u5c0f\u54e5\u54e5\u4e48"},{"barrageContent":"\u524d\u65b9\u9ad8\u80fd\u9884\u8b66\uff01\uff01"},{"barrageContent":"\u5173\u5173\u96ce\u9e20\uff0c\u5728\u6cb3\u4e4b\u6d32\uff0c\u7a88\u7a95\u6dd1\u5973\uff0cwhat are you QQ\uff1f"},{"barrageContent":"\u4e8c\u8425\u957f\uff0c\u4f60\u4ed6\u5a18\u7684\u610f\u5927\u5229\u70ae\u5462\uff1f"},{"barrageContent":"\u4f60\u51b7\u9177\u4f60\u65e0\u60c5\u4f60\u65e0\u7406\u53d6\u95f9"},{"barrageContent":"\u6211\u51d1\uff0c\u6211\u9501\u4e22\u4e86"},{"barrageContent":"\u6211\u4ece\u672a\u89c1\u8fc7\u5982\u6b64\u539a\u989c\u65e0\u803b\u4e4b\u4eba\uff01"},{"barrageContent":"\u5927\u5bb6\u597d\uff0c\u6211\u662f\u6e23\u6e23\u8f89"},{"barrageContent":"\u4e00\u672c\u6b63\u7ecf\u80e1\u8bf4\u516b\u9053"}]}}