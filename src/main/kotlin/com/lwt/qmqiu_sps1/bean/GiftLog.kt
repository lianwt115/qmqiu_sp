package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "gift_log")
data class GiftLog(
        @Id
        var _id:String?=null,

        //0 购买 1赠送 2兑换
        var type:Int? = 0,
        //总花费
        var cash:Int? = 0,

        var giftCount:String ="0*0*0*0",

        var giftPrice:String ="18*38*68*88",
        //主动方
        var name:String?="",
        //从哪来
        var from:String?="",
        //到哪去
        var to:String?="",
        //交易时间
        var happenTime:Long = System.currentTimeMillis()

)