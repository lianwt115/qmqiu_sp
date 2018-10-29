package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.EnterRoomLog
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.dao.EnterRoomLogDao
import com.lwt.qmqiu_sps1.dao.LoginLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("enterRoomLogService")
class EnterRoomLogService:BaseDaoInterface<EnterRoomLog> {


    @Autowired
    private lateinit var  enterRoomLogDao: EnterRoomLogDao


    override fun getAll(key: String, value: Any?): List<EnterRoomLog> {
        return enterRoomLogDao.getAll(key,value)
    }

    override fun insert(user: EnterRoomLog) {
        return enterRoomLogDao.insert(user)
    }

    override fun findByKey(key: String, value: Any): EnterRoomLog? {

        return enterRoomLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return enterRoomLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return enterRoomLogDao.delete(_id)
    }

}