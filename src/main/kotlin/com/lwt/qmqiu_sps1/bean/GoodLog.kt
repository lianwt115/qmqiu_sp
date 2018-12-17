package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "good_log")
data class GoodLog(
        @Id
        var _id:String?=null,

        //谁
        var from:String? = "",
        //点赞谁
        var to:String? = "",
        //1 帖子点赞
        var type:Int = 0,
        //帖子id
        var whereId:String? = "",
        //点赞时间
        var goodTime:Long = System.currentTimeMillis()

)