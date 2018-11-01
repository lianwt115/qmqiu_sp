package com.lwt.qmqiu_sps1.service

import com.lwt.qmqiu_sps1.bean.CoinLog
import com.lwt.qmqiu_sps1.bean.GiftLog
import com.lwt.qmqiu_sps1.dao.CoinLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("coinLogService")
class CoinLogService:BaseDaoInterface<CoinLog> {


    @Autowired
    private lateinit var  coinLogDao: CoinLogDao


    override fun getAll(key: String, value: Any?): List<CoinLog> {
        return coinLogDao.getAll(key,value)
    }

    override fun insert(coinLog: CoinLog) {
        return coinLogDao.insert(coinLog)
    }

    override fun findByKey(key: String, value: Any): CoinLog? {

        return coinLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return coinLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return coinLogDao.delete(_id)
    }

}