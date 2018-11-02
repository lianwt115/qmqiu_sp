package com.lwt.qmqiu_sps1.dao

import com.lwt.qmqiu_sps1.bean.LoginLog
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

@Repository("messageLogDao")
class MessageLogDao: BaseDaoInterface<QMMessage> {


    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate


    //返回近一个小时的聊天信息  房间即to
    override fun getAll(key: String, value: Any?): List<QMMessage> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(QMMessage::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        var currentTime = System.currentTimeMillis()

        if ("to" == key && !(value as String).contains("ALWTA"))
            //24小时内
            query.addCriteria(Criteria.where("time").gte(currentTime-1000*60*60L*24))

        return mongoTemplate.find(query,QMMessage::class.java)
    }

    override fun insert(log: QMMessage) {

        return mongoTemplate.insert(log)
    }

    override fun findByKey(key: String, value: Any): QMMessage? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,QMMessage::class.java)
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

        return mongoTemplate.updateMulti(query, update!!, QMMessage::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,QMMessage::class.java)
    }
}