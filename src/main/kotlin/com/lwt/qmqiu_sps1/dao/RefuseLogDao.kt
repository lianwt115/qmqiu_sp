package com.lwt.qmqiu_sps1.dao


import com.lwt.qmqiu_sps1.bean.RefuseLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.RefuseLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("refuseLogDao")
class RefuseLogDao: BaseDaoInterface<RefuseLog>,RefuseLogDaoInterface<RefuseLog> {



    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate


    override fun getAll(key: String, value: Any?): List<RefuseLog> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(RefuseLog::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,RefuseLog::class.java)
    }

    override fun insert(log: RefuseLog) {

        return mongoTemplate.insert(log)
    }

    override fun findByKey(key: String, value: Any): RefuseLog? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,RefuseLog::class.java)
    }

    override fun getRefuseLogOne(from: String, to: String): RefuseLog? {

        val query = Query(Criteria.where("from").`is`(from))

        query.addCriteria(Criteria.where("to").`is`(to))

        return mongoTemplate.findOne(query,RefuseLog::class.java)

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

        return mongoTemplate.updateMulti(query, update!!, RefuseLog::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,RefuseLog::class.java)
    }
}