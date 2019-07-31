package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "report_log")
data class ReportLog(
        @Id
        var _id:String?=null,

        //谁
        var from:String? = "",
        //举报谁
        var to:String? = "",
        //发生在那个房间
        var roomNumber:String? = "",
        //消息内容加密的
        var messageContent:String? = "",
        //消息id  即为消息日志中的时间
        var messageId:String? = "",
        //什么理由
        /**
         * 0.恋童癖,儿童色情
         * 1.垃圾,灌水.钓鱼信息
         * 2.骚扰.变态.攻击信息
         * 3.个人数据
         * 4.轻度色情内容
         * 5.色情,生殖器官
         */
        var why:Int? = 0,
        //交易时间
        var reportTime:Long = System.currentTimeMillis()

)