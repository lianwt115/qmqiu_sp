package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.RefuseLog
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.dao.RefuseLogDao
import com.lwt.qmqiu_sps1.dao.ReportLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.RefuseLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("reportLogService")
class ReportLogService:BaseDaoInterface<ReportLog> {



    @Autowired
    private lateinit var  reportLogDao: ReportLogDao


    override fun getAll(key: String, value: Any?): List<ReportLog> {
        return reportLogDao.getAll(key,value)
    }

    override fun insert(reportLog: ReportLog) {
        return reportLogDao.insert(reportLog)
    }

    override fun findByKey(key: String, value: Any): ReportLog? {

        return reportLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return reportLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return reportLogDao.delete(_id)
    }

}