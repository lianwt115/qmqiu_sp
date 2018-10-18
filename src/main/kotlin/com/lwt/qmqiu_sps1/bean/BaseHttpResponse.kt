package com.lwt.qmqiu_sps1.bean

data class BaseHttpResponse<T>(var code:Int?=200,var message:String?="请求成功",var data:T?=null){


}













