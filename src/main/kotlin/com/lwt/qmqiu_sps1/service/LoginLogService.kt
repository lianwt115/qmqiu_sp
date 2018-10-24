package com.lwt.qmqiu_sps1.service

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.dao.BaseUserDao
import com.lwt.qmqiu_sps1.dao.LoginLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.BaseUserDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("loginLogService")
class LoginLogService:BaseDaoInterface<LoginLog> {


    @Autowired
    private lateinit var  loginLogDao: LoginLogDao


    override fun getAll(key: String, value: Any?): List<LoginLog> {
        return loginLogDao.getAll(key,value)
    }

    override fun insert(user: LoginLog) {
        return loginLogDao.insert(user)
    }

    override fun findByKey(key: String, value: Any): LoginLog? {

        return loginLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return loginLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return loginLogDao.delete(_id)
    }

}