package com.lwt.qmqiu_sps1.myinterface


interface NoteLogDaoInterface<T> {

    fun getNote(noteType:Int,latitude:Double,longitude:Double):List<T>


}