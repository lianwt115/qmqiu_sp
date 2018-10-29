package com.lwt.qmqiu_sps1.myinterface

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.IMChatRoom


interface IMChatRoomDaoInterface<T> {

    fun getRoom(type:Int,latitude:Double,longitude:Double):List<IMChatRoom>
    fun getRoomOne(key:String,value:Any,latitude:Double,longitude:Double,check:Boolean):IMChatRoom?


}