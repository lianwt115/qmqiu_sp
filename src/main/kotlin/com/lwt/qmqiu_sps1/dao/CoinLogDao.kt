package com.lwt.qmqiu_sps1.dao


import com.lwt.qmqiu_sps1.bean.CoinLog
import com.lwt.qmqiu_sps1.bean.GiftLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.websocket.QMMessage
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("coinLogDao")
class CoinLogDao: BaseDaoInterface<CoinLog> {


    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate


    override fun getAll(key: String, value: Any?): List<CoinLog> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(CoinLog::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,CoinLog::class.java)
    }

    override fun insert(log: CoinLog) {

        return mongoTemplate.insert(log)
    }

    override fun findByKey(key: String, value: Any): CoinLog? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,CoinLog::class.java)
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

        return mongoTemplate.updateMulti(query, update!!, CoinLog::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,CoinLog::class.java)
    }
}