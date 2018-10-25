package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.IMChatRoom
import com.lwt.qmqiu_sps1.dao.IMChatRoomDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.lwt.qmqiu_sps1.myinterface.IMChatRoomDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("imChatRoomService")
class IMChatRoomService:BaseDaoInterface<IMChatRoom> , IMChatRoomDaoInterface<IMChatRoom> {

    @Autowired
    private lateinit var  imChatRoomDao: IMChatRoomDao


    override fun getAll(key: String, value: Any?): List<IMChatRoom> {
        return imChatRoomDao.getAll(key,value)
    }

    override fun insert(user: IMChatRoom) {
        return imChatRoomDao.insert(user)
    }

    override fun findByKey(key: String, value: Any): IMChatRoom? {

        return imChatRoomDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return imChatRoomDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return imChatRoomDao.delete(_id)
    }

    override fun getRoom(type: Int, latitude: Double, longitude: Double): List<IMChatRoom> {
        return imChatRoomDao.getRoom(type,latitude,longitude)
    }

    override fun getRoomOne(roomName: String, latitude: Double, longitude: Double, check: Boolean): IMChatRoom? {
        return imChatRoomDao.getRoomOne(roomName,latitude,longitude,check)
    }

}