package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "enterroom_log")
data class EnterRoomLog(
        @Id
        var _id:String?=null,

        var name:String? ="lwt",

        var roomNumber:String?="",

        //活跃度使用
        var messageCount:Int? = 0,

        var enterTime:Long = System.currentTimeMillis()


)