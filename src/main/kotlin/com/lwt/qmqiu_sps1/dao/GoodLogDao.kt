package com.lwt.qmqiu_sps1.dao



import com.lwt.qmqiu_sps1.bean.GoodLog
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.GoodLogDaoInterface
import com.lwt.qmqiu_sps1.myinterface.ReportLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("goodLogDao")
class GoodLogDao: BaseDaoInterface<GoodLog>,GoodLogDaoInterface<GoodLog> {


    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate


    override fun getAll(key: String, value: Any?): List<GoodLog> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(GoodLog::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,GoodLog::class.java)
    }

    override fun insert(log: GoodLog) {

        return mongoTemplate.insert(log)
    }

    override fun findByKey(key: String, value: Any): GoodLog? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,GoodLog::class.java)
    }

    override fun checkGood(from: String, to: String, id: String): Boolean {
        //检测是否是有效举报
        val query = Query(Criteria.where("from").`is`(from))

        query.addCriteria(Criteria.where("to").`is`(to))
        query.addCriteria(Criteria.where("whereId").`is`(id))

        return mongoTemplate.findOne(query,GoodLog::class.java)!=null

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

        return mongoTemplate.updateMulti(query, update!!, GoodLog::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,GoodLog::class.java)
    }
}