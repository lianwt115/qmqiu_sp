package com.lwt.qmqiu_sps1.dao


import com.lwt.qmqiu_sps1.bean.NoteLog
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.NoteLogDaoInterface
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

    private val MAP_PATH_LATITUDE = 0.006
    private val MAP_PATH_LONGITUDE = 0.0025

    override fun getAll(key: String, value: Any?): List<NoteLog> {

        val query = Query(Criteria.where("deleteStatus").`is`(false))

        if (key == "" || value == null)
            return mongoTemplate.find(query,NoteLog::class.java)

        query.addCriteria(Criteria.where(key).`is`(value))

        return mongoTemplate.find(query,NoteLog::class.java)
    }

    //暂时获取全部
    override fun getNote(noteType: Int, latitude: Double, longitude: Double): List<NoteLog> {

        val query1 = Query(Criteria.where("noteType").`is`(noteType))

        //所有公共帖子
        query1.addCriteria(Criteria.where("seeType").`is`(2))
        query1.addCriteria(Criteria.where("deleteStatus").`is`(false))

        var public = mongoTemplate.find(query1,NoteLog::class.java)

        val query2 = Query(Criteria.where("noteType").`is`(noteType))
        query2.addCriteria(Criteria.where("deleteStatus").`is`(false))
        //所有附近帖子
        query2.addCriteria(Criteria.where("seeType").`is`(1))

        query2.addCriteria(Criteria.where("latitude").gte(latitude-MAP_PATH_LATITUDE).lte(latitude+MAP_PATH_LATITUDE))

        query2.addCriteria(Criteria.where("longitude").gte(longitude-MAP_PATH_LONGITUDE).lte(longitude+MAP_PATH_LONGITUDE))

        public.addAll(0,mongoTemplate.find(query2,NoteLog::class.java))

        return public

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