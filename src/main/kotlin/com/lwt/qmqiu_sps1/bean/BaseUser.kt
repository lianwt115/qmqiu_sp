package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import sun.security.util.Password
import java.security.PrivateKey

@Document(collection = "base_user")
data class BaseUser(
        @Id
        var _id:String?=null,
        var name:String? ="lwt",
        var password:String? ="***",
        var privateKey: String?="",
        var publicKey: String?="",
        var age:Int= 18,
        var male:Boolean?=true

)