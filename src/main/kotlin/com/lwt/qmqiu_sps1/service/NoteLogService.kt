package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.NoteLog
import com.lwt.qmqiu_sps1.dao.NoteLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.NoteLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("noteLogService")
class NoteLogService:BaseDaoInterface<NoteLog>,NoteLogDaoInterface<NoteLog> {



    @Autowired
    private lateinit var  noteLogDao: NoteLogDao


    override fun getAll(key: String, value: Any?): List<NoteLog> {
        return noteLogDao.getAll(key,value)
    }

    override fun getNote(noteType: Int, seeType: Int, topic: String?): List<NoteLog> {
        return noteLogDao.getNote(noteType,seeType,topic)
    }

    override fun insert(reportLog: NoteLog) {
        return noteLogDao.insert(reportLog)
    }

    override fun findByKey(key: String, value: Any): NoteLog? {

        return noteLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return noteLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return noteLogDao.delete(_id)
    }

}