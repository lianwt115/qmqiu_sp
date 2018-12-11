package com.lwt.qmqiu_sps1.dao

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.IMChatRoom
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.BaseUserDaoInterface
import com.lwt.qmqiu_sps1.myinterface.IMChatRoomDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("imChatRoomDao")
class IMChatRoomDao: BaseDaoInterface<IMChatRoom>,IMChatRoomDaoInterface<IMChatRoom> {


    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate

    override fun getAll(key: String, value: Any?): List<IMChatRoom> {

        if (key == "" || value == null)
            return mongoTemplate.findAll(IMChatRoom::class.java)

        val query = Query(Criteria.where(key).`is`(value))

        query.addCriteria(Criteria.where("status").`is`(true))

        var list = mongoTemplate.find(query,IMChatRoom::class.java)

        if (list.size >20){

            list.shuffle()

            list= list.subList(0,20)
        }

        return list
    }

    override fun getRoom(type: Int, latitude: Double, longitude: Double): List<IMChatRoom> {

        var query = Query(Criteria.where("roomType").`is`(type))

        query.addCriteria(Criteria.where("status").`is`(true))

        query.addCriteria(Criteria.where("latitude").gte(latitude-0.006).lte(latitude+0.006))

        query.addCriteria(Criteria.where("longitude").gte(longitude-0.0025).lte(longitude+0.0025))

        return mongoTemplate.find(query,IMChatRoom::class.java)
    }

    override fun getRoomOne(key: String, value: Any, latitude: Double, longitude: Double, check: Boolean): IMChatRoom? {

        var query = Query(Criteria.where(key).`is`(value))

        query.addCriteria(Criteria.where("status").`is`(true))

        if (check) {
            query.addCriteria(Criteria.where("latitude").gte(latitude-0.006).lte(latitude+0.006))

            query.addCriteria(Criteria.where("longitude").gte(longitude-0.0025).lte(longitude+0.0025))
        }


        return mongoTemplate.findOne(query,IMChatRoom::class.java)
    }


    override fun insert(imChatRoom: IMChatRoom) {

        return mongoTemplate.insert(imChatRoom)
    }

    override fun findByKey(key: String, value: Any): IMChatRoom? {
        val query = Query(Criteria.where(key).`is`(value))

        return mongoTemplate.findOne(query,IMChatRoom::class.java)
    }

    override fun updata(roomNumber: String, data: HashMap<String, Any>): UpdateResult {

        val criteria = Criteria.where("roomNumber").`is`(roomNumber)
        val query = Query(criteria)

        var update :Update?=null

       data.forEach { key, value ->

           if (update === null)
                update = Update.update(key,value)
           else
               update!!.set(key,value)

       }

        return mongoTemplate.updateMulti(query, update!!, IMChatRoom::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,IMChatRoom::class.java)
    }
}