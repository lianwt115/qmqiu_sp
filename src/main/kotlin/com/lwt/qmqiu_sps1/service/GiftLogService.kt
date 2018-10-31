package com.lwt.qmqiu_sps1.service

import com.lwt.qmqiu_sps1.bean.GiftLog
import com.lwt.qmqiu_sps1.dao.GiftLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("giftLogService")
class GiftLogService:BaseDaoInterface<GiftLog> {


    @Autowired
    private lateinit var  giftLogDao: GiftLogDao


    override fun getAll(key: String, value: Any?): List<GiftLog> {
        return giftLogDao.getAll(key,value)
    }

    override fun insert(giftLog: GiftLog) {
        return giftLogDao.insert(giftLog)
    }

    override fun findByKey(key: String, value: Any): GiftLog? {

        return giftLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return giftLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return giftLogDao.delete(_id)
    }

}