package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "videocall_log")
data class VideoCallLog(
        @Id
        var _id:String?=null,

        //呼叫方
        var callUser:String? = "",
        //接收方
        var acceptUser:String? = "",
        //通话时间  单位s
        var callTime:Int? = 0,
        //房间名称
        var roomName:String?="",
        //频道号
        var channelName:String?="",
        //交易时间
        var happenTime:Long = System.currentTimeMillis()

)