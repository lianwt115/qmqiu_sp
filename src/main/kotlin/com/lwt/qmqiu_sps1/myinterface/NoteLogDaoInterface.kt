package com.lwt.qmqiu_sps1.myinterface



interface NoteLogDaoInterface<T> {

    fun getNote(noteType:Int,seeType:Int,topic:String?):List<T>


}