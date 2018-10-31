package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "coin_log")
data class CoinLog(
        @Id
        var _id:String?=null,

        //0 青木 1青木球
        var coinType:Int? = 0,
        //0 充值 1消费
        var cashType:Int? = 0,
        //交易额
        var cash:Int? = 0,
        //主动方
        var name:String?="",
        //如果是消费 则记录用途 0 开附近房 1开公共房 2购买礼物 3视频聊天
        var toType:Int?=0,
        //交易时间
        var happenTime:Long = System.currentTimeMillis()

)