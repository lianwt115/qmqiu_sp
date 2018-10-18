package com.lwt.qmqiu_sps1.service

import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.dao.BaseUserDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.BaseUserDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("baseUserService")
class BaseUserService:BaseDaoInterface<BaseUser>, BaseUserDaoInterface<BaseUser> {

    @Autowired
    private lateinit var  baseUserDao: BaseUserDao


    override fun getAll(): List<BaseUser> {
        return baseUserDao.getAll()
    }

    override fun insert(user: BaseUser) {
        return baseUserDao.insert(user)
    }

    override fun findById(_id: String): BaseUser? {
        return baseUserDao.findById(_id)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return baseUserDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return baseUserDao.delete(_id)
    }

    override fun userExist(name: String): BaseUser? {

        return baseUserDao.userExist(name)
    }


}