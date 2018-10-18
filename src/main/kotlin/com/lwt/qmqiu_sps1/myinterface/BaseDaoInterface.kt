package com.lwt.qmqiu_sps1.myinterface

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult

interface BaseDaoInterface<T> {

    fun  getAll():List<T>
    fun insert(user:T)
    fun  findById(_id:String):T?
    fun updata(_id:String,data:HashMap<String,Any>): UpdateResult
    fun delete(_id:String): DeleteResult
}