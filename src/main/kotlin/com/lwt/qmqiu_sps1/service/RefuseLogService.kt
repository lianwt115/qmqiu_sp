package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.RefuseLog
import com.lwt.qmqiu_sps1.dao.RefuseLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.RefuseLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("refuseLogService")
class RefuseLogService:BaseDaoInterface<RefuseLog>, RefuseLogDaoInterface<RefuseLog> {



    @Autowired
    private lateinit var  refuseLogDao: RefuseLogDao


    override fun getAll(key: String, value: Any?): List<RefuseLog> {
        return refuseLogDao.getAll(key,value)
    }

    override fun insert(refuseLog: RefuseLog) {
        return refuseLogDao.insert(refuseLog)
    }

    override fun findByKey(key: String, value: Any): RefuseLog? {

        return refuseLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return refuseLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return refuseLogDao.delete(_id)
    }

    override fun getRefuseLogOne(from: String, to: String): RefuseLog? {
        return refuseLogDao.getRefuseLogOne(from,to)
    }

}