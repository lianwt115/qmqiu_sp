package com.lwt.qmqiu_sps1.dao


import com.lwt.qmqiu_sps1.bean.UploadLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("uploadLogDao")
class UploadLogDao: BaseDaoInterface<UploadLog> {



    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate


    override fun getAll(key: String, value: Any?): List<UploadLog> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(UploadLog::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,UploadLog::class.java)
    }

    override fun insert(log: UploadLog) {

        return mongoTemplate.insert(log)
    }

    override fun findByKey(key: String, value: Any): UploadLog? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,UploadLog::class.java)
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

        return mongoTemplate.updateMulti(query, update!!, UploadLog::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,UploadLog::class.java)
    }
}