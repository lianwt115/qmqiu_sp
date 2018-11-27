package com.lwt.qmqiu_sps1.service


import com.lwt.qmqiu_sps1.bean.UploadLog
import com.lwt.qmqiu_sps1.bean.VideoCallLog
import com.lwt.qmqiu_sps1.dao.UploadLogDao
import com.lwt.qmqiu_sps1.dao.VideoCallLogDao
import com.lwt.qmqiu_sps1.myinterface.BaseDaoInterface
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service("videoCallLogService")
class VideoCallLogService:BaseDaoInterface<VideoCallLog> {



    @Autowired
    private lateinit var  videoCallLogDao: VideoCallLogDao


    override fun getAll(key: String, value: Any?): List<VideoCallLog> {
        return videoCallLogDao.getAll(key,value)
    }

    override fun insert(videoCallLog: VideoCallLog) {
        return videoCallLogDao.insert(videoCallLog)
    }

    override fun findByKey(key: String, value: Any): VideoCallLog? {

        return videoCallLogDao.findByKey(key,value)
    }

    override fun updata(_id: String, data: HashMap<String, Any>): UpdateResult {

        return videoCallLogDao.updata(_id,data)
    }

    override fun delete(_id: String): DeleteResult {
        return videoCallLogDao.delete(_id)
    }

}