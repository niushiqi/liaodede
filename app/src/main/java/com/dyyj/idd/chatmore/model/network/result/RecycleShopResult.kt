package com.dyyj.idd.chatmore.model.network.result



data class RecycleShopResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    var data: Data? = Data()
) {
    data class Data(
            val stone: List<Stone>? = listOf(),
            val deDou: List<Dedou>? = listOf(),
            val matchCard: List<MatchCard>? = listOf()
    ) {
        data class Stone(
                val goodsId: String? = "",
                val goodsName: String? = "",
                val goodsDesc: String? = "",
                val goodsImg: String? = "",
                val goodsPrice: String? = "",
                val goodsType: String? = "",
                val goodsPromotion: String? = "",
                val goodsNum: Int? = 0
        )
        data class Dedou(
                val goodsId: String? = "",
                val goodsName: String? = "",
                val goodsDesc: String? = "",
                val goodsImg: String? = "",
                val goodsPrice: String? = "",
                val goodsType: String? = "",
                val goodsPromotion: String? = "",
                val goodsNum: Int? = 0,
                var isSelected: Int?//另一个购买页复用时用到
        )
        data class MatchCard(
                val goodsId: String? = "",
                val goodsName: String? = "",
                val goodsDesc: String? = "",
                val goodsImg: String? = "",
                val goodsPrice: String? = "",
                val goodsType: String? = "",
                val goodsPromotion: String? = "",
                val goodsNum: Int? = 0,
                val goodsOncePrice: String? = "",
                val goodsEveryDayNum: Int? = 0
        )
    }
}

