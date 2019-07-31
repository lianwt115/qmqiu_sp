package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.GoodLog
import com.lwt.qmqiu_sps1.dao.GoodLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.GoodLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("goodLogService")
class GoodLogService:BaseDaoInterface<GoodLog>,GoodLogDaoInterface<GoodLog> {



    @Autowired
    private lateinit var  goodLogDao: GoodLogDao


    override fun getAll(key: String, value: Any?): List<GoodLog> {
        return goodLogDao.getAll(key,value)
    }

    override fun insert(goodLog: GoodLog) {
        return goodLogDao.insert(goodLog)
    }

    override fun findByKey(key: String, value: Any): GoodLog? {

        return goodLogDao.findByKey(key,value)
    }

    override fun checkGood(from: String, to: String, id: String): Boolean {

        return goodLogDao.checkGood(from,to,id)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return goodLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return goodLogDao.delete(_id)
    }

}