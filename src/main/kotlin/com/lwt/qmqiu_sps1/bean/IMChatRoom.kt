package com.lwt.qmqiu_sps1.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "im_room")
data class IMChatRoom(
        @Id
        var _id:String?=null,

        //开放价格  私人10青木  附近50青木  公共100青木
        var currentCount:Int = 0,
        var roomName:String="",
        var roomNumber:String="",
        //1 附近 2公共 3私人
        var roomType:Int= 0,
        var creatName:String="",
        var lastContent:String="" ,
        var latitude:Double=0.00,
        var longitude:Double=0.00 ,
        var creatTime:Long=System.currentTimeMillis(),
        var lastContentTime:Long=System.currentTimeMillis(),
        var status:Boolean = true) {
}