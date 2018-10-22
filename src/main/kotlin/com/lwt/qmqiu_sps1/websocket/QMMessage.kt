package com.lwt.qmqiu_sps1.websocket

data class QMMessage(var from:String,var to:String,var type:Int,var message:String,var time:Long = System.currentTimeMillis()) {

}