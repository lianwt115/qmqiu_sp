package com.lwt.qmqiu_sps1.websocket

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "message_log")
data class QMMessage(
        @Id
        var _id:String?=null,

        var from:String,
        var to:String,
        //0是房间消息 2是礼物通知 3 语音消息
        var type:Int,
        var colorIndex:Int,
        var imgPath:String,
        var message:String,
        var currentCount:Int,
        var time:Long = System.currentTimeMillis()) {

}