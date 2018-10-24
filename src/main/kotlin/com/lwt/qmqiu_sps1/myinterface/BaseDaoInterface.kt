package com.lwt.qmqiu_sps1.myinterface

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult

interface BaseDaoInterface<T> {

    fun getAll(key:String = "",value:Any?= null):List<T>
    fun insert(user:T)
    fun findByKey(key:String,value: Any):T?
    fun updata(_id:String,data:HashMap<String,Any>): UpdateResult
    fun delete(_id:String): DeleteResult
}