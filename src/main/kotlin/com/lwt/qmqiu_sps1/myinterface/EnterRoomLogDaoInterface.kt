package com.lwt.qmqiu_sps1.myinterface

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.EnterRoomLog
import com.lwt.qmqiu_sps1.bean.IMChatRoom


interface EnterRoomLogDaoInterface<T> {

    fun checkRoomUser(name:String,roomNumber:String): EnterRoomLog?
    fun getActiveUser(roomNumber:String,limit:Int):List<T>?


}