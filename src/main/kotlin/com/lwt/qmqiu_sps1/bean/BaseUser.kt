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
        var lastLoginTime:Long = System.currentTimeMillis(),
        var creatTime:Long? = System.currentTimeMillis()

)