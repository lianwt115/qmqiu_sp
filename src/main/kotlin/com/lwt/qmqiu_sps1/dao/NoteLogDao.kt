package com.lwt.qmqiu_sps1.dao


import com.lwt.qmqiu_sps1.bean.NoteLog
import com.lwt.qmqiu_sps1.bean.RefuseLog
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.NoteLogDaoInterface
import com.lwt.qmqiu_sps1.myinterface.RefuseLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("noteLogDao")
class NoteLogDao: BaseDaoInterface<NoteLog>,NoteLogDaoInterface<NoteLog> {



    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate


    override fun getAll(key: String, value: Any?): List<NoteLog> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(NoteLog::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,NoteLog::class.java)
    }

    //暂时获取全部
    override fun getNote(noteType: Int, seeType: Int, topic: String?): List<NoteLog> {

        val query = Query(Criteria.where("noteType").`is`(noteType))

        query.addCriteria(Criteria.where("seeType").`is`(seeType))
        if (topic!=null)
            query.addCriteria(Criteria.where("topic").`is`(topic))

        return mongoTemplate.find(query,NoteLog::class.java)

    }

    override fun insert(log: NoteLog) {

        return mongoTemplate.insert(log)
    }

    override fun findByKey(key: String, value: Any): NoteLog? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,NoteLog::class.java)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)

        var update :Update?=null

       data.forEach { key, value ->

           if (update === null)
                update = Update.update(key,value)
           else
               update!!.set(key,value)

       }

        return mongoTemplate.updateMulti(query, update!!, NoteLog::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,NoteLog::class.java)
    }
}