package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "base_user")
data class BaseUser(
        @Id
        var _id:String?=null,
        var name:String? ="lwt",
        var password:String? ="***",
        var imgPath:String? ="***",
        var colorIndex:Int? = 10,
        var privateKey: String?="",
        var publicKey: String?="",
        var status: Boolean?=true,
        var lastLoginTime:Long = System.currentTimeMillis(),
        var lastLoginOutTime:Long = System.currentTimeMillis(),
        var creatTime:Long = System.currentTimeMillis(),
        var male:Boolean = true,
        var age:Int = 18,
        //货币名为青木,用途为开房
        var coinbase:Int = 0,
        //货币名为青木球,用途为购买礼物
        var coin:Int = 0,
        var gift:String ="0*0*0*0"


)