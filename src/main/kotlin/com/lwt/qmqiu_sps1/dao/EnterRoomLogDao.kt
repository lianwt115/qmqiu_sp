package com.lwt.qmqiu_sps1.dao

import com.lwt.qmqiu_sps1.bean.EnterRoomLog
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.EnterRoomLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("enterRoomLogDao")
class EnterRoomLogDao: BaseDaoInterface<EnterRoomLog>,EnterRoomLogDaoInterface<EnterRoomLog> {


    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate

    override fun getAll(key: String, value: Any?): List<EnterRoomLog> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(EnterRoomLog::class.java)
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,EnterRoomLog::class.java)
    }

    override fun insert(log: EnterRoomLog) {

        val query = Query(Criteria.where("name").`is`(log.name))

        query.addCriteria(Criteria.where("roomNumber").`is`(log.roomNumber))

        var enterlog =  mongoTemplate.findOne(query,EnterRoomLog::class.java)

        if (enterlog !=null){
            var hash =HashMap<String,Any>()
            hash["enterTime"] = log.enterTime
            hash["messageCount"] = log.messageCount!!+1

            updata(enterlog._id!!,hash)

        }else{

            return mongoTemplate.insert(log)
        }

    }

    override fun findByKey(key: String, value: Any): EnterRoomLog? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,EnterRoomLog::class.java)
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

        return mongoTemplate.updateMulti(query, update!!, EnterRoomLog::class.java)

    }

    override fun checkRoomUser(name: String, roomNumber: String): EnterRoomLog? {
        val query = Query(Criteria.where("name").`is`(name))

        query.addCriteria(Criteria.where("roomNumber").`is`(roomNumber))

        var enterlog =  mongoTemplate.findOne(query,EnterRoomLog::class.java)

        return enterlog
    }

    override fun getActiveUser(roomNumber: String, limit: Int): List<EnterRoomLog>? {


        val query = Query(Criteria.where("roomNumber").`is`(roomNumber))

        query.with(Sort(Sort.DEFAULT_DIRECTION,"messageCount"))

        query.limit(if (limit<0)10 else limit)

        return mongoTemplate.find(query,EnterRoomLog::class.java)

    }



    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,EnterRoomLog::class.java)
    }
}