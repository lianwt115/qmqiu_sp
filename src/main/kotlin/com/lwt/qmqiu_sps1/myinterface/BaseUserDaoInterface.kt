package com.lwt.qmqiu_sps1.myinterface

import com.lwt.qmqiu_sps1.bean.BaseUser


interface BaseUserDaoInterface<T> {


    fun  userExist(key:String,value:String): BaseUser?

}