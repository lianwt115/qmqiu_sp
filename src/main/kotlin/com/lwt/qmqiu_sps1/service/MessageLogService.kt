package com.lwt.qmqiu_sps1.service

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.dao.BaseUserDao
import com.lwt.qmqiu_sps1.dao.LoginLogDao
import com.lwt.qmqiu_sps1.dao.MessageLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.BaseUserDaoInterface
import com.lwt.qmqiu_sps1.websocket.QMMessage
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("messageLogService")
class MessageLogService:BaseDaoInterface<QMMessage> {


    @Autowired
    private lateinit var  messageLogDao: MessageLogDao


    override fun getAll(key: String, value: Any?): List<QMMessage> {
        return messageLogDao.getAll(key,value)
    }

    override fun insert(message: QMMessage) {
        return messageLogDao.insert(message)
    }

    override fun findByKey(key: String, value: Any): QMMessage? {

        return messageLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return messageLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return messageLogDao.delete(_id)
    }

}