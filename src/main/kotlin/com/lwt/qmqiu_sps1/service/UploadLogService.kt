package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.RefuseLog
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.bean.UploadLog
import com.lwt.qmqiu_sps1.dao.RefuseLogDao
import com.lwt.qmqiu_sps1.dao.ReportLogDao
import com.lwt.qmqiu_sps1.dao.UploadLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.RefuseLogDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("uploadLogService")
class UploadLogService:BaseDaoInterface<UploadLog> {



    @Autowired
    private lateinit var  uploadLogDao: UploadLogDao


    override fun getAll(key: String, value: Any?): List<UploadLog> {
        return uploadLogDao.getAll(key,value)
    }

    override fun insert(uploadLog: UploadLog) {
        return uploadLogDao.insert(uploadLog)
    }

    override fun findByKey(key: String, value: Any): UploadLog? {

        return uploadLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return uploadLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return uploadLogDao.delete(_id)
    }

}