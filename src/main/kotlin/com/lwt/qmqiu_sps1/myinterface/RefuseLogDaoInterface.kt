package com.lwt.qmqiu_sps1.myinterface

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.IMChatRoom
import com.lwt.qmqiu_sps1.bean.RefuseLog


interface RefuseLogDaoInterface<T> {

    fun getRefuseLogOne(from:String,to:String):RefuseLog?


}