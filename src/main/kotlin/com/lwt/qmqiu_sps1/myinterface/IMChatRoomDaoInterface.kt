package com.lwt.qmqiu_sps1.myinterface

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.IMChatRoom


interface IMChatRoomDaoInterface<T> {

    fun getRoom(type:Int,latitude:Double,longitude:Double):List<IMChatRoom>
    fun getRoomOne(roomName:String,latitude:Double,longitude:Double):IMChatRoom?


}