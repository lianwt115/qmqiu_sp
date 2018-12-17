package com.lwt.qmqiu_sps1.myinterface

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.IMChatRoom
import com.lwt.qmqiu_sps1.bean.RefuseLog


interface ReportLogDaoInterface<T> {

    fun checkReport(from:String,to:String,id:String):Boolean


}