package com.lwt.qmqiu_sps1.dao

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.BaseUserDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository("baseUserDao")
class BaseUserDao: BaseDaoInterface<BaseUser>,BaseUserDaoInterface<BaseUser> {


    @Autowired
    private  lateinit var  mongoTemplate: MongoTemplate

    override fun getAll(): List<BaseUser> {

        return mongoTemplate.findAll(BaseUser::class.java)
    }

    override fun insert(user: BaseUser) {

        return mongoTemplate.insert(user)
    }

    override fun findById(_id: String): BaseUser? {
        val query = Query(Criteria.where("_id").`is`(_id))

        return mongoTemplate.findOne(query,BaseUser::class.java)
    }

    override fun userExist(name: String): BaseUser? {
        val query = Query(Criteria.where("name").`is`(name))

        return mongoTemplate.findOne(query,BaseUser::class.java)
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

        return mongoTemplate.updateMulti(query, update!!, BaseUser::class.java)

    }

    override fun delete(_id: String): DeleteResult {
        val criteria = Criteria.where("_id").`is`(_id)
        val query = Query(criteria)
        return mongoTemplate.remove(query,BaseUser::class.java)
    }
}