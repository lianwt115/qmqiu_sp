package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "login_log")
data class LoginLog(
        @Id
        var _id:String?=null,

        var name:String? ="lwt",
        //登录地点
        var loginWhere:String?="",
        //登录时的纬度
        var latitude:Double?=0.0,
        //登录时的经度
        var longitude:Double?=0.0,
        //登录时间
        var loginTime:Long = System.currentTimeMillis()


)