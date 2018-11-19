package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "upload_log")
data class UploadLog(
        @Id
        var _id:String?=null,

        //谁上传
        var from:String? = "",
        //什么类型 0,语音 1 图片 2 文件
        var type:Int? = 0,
        //那个房间
        var where:String? = "",
        //文件位置
        var path:String? = "",
        //文件名称
        var name:String? = "",
        //文件大小
        var length:Int? = 0,
        //上传时间
        var creatTime:Long = System.currentTimeMillis()

)