package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "refuse_log")
data class RefuseLog(
        @Id
        var _id:String?=null,

        //谁
        var from:String? = "",
        //阻止谁   即拒绝产生私人聊天
        var to:String? = "",
        //是否阻止
        var status:Boolean? = false,
        //交易时间
        var creatTime:Long = System.currentTimeMillis(),
        var changeTime:Long = System.currentTimeMillis()

)