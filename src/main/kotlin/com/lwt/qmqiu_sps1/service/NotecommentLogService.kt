package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.GoodLog
import com.lwt.qmqiu_sps1.bean.NoteCommentLog
import com.lwt.qmqiu_sps1.dao.GoodLogDao
import com.lwt.qmqiu_sps1.dao.NoteCommentLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.GoodLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("noteCommentLogService")
class NotecommentLogService:BaseDaoInterface<NoteCommentLog>{


    @Autowired
    private lateinit var  noteCommentLogDao: NoteCommentLogDao


    override fun getAll(key: String, value: Any?): List<NoteCommentLog> {
        return noteCommentLogDao.getAll(key,value)
    }

    override fun insert(log: NoteCommentLog) {
        return noteCommentLogDao.insert(log)
    }

    override fun findByKey(key: String, value: Any): NoteCommentLog? {

        return noteCommentLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return noteCommentLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return noteCommentLogDao.delete(_id)
    }

}