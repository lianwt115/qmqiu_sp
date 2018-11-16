package com.lwt.qmqiu_sps1.websocket

import com.google.gson.Gson
import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.dao.BaseUserDao
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.utils.OkHttpUtil
import com.lwt.qmqiu_sps1.utils.RSAUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import javax.websocket.server.ServerEndpoint
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import javax.websocket.OnMessage
import javax.websocket.OnClose
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.OnError
import javax.websocket.server.PathParam


@ServerEndpoint(value = "/websocket/{number}")
@Component
class QMWebSocket {

    //TODO  查表任务都应异步执行

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    companion object {

        private val baseUrl = "http://localhost:9898/api/"
        private val NOTIFICATION = "notification"

        private var onlineCount = 0

        //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。 房间 - wsList
        private val webSocketSet = ConcurrentHashMap<String,ArrayList<QMWebSocket>>()

        //通知ws集合
        private val webSocketSetNotification = ConcurrentHashMap<String,QMWebSocket>()

        private val idWSSet = ConcurrentHashMap<Session,QMWebSocket>()

        private val idNameSet = ConcurrentHashMap<Session,String>()

        private val logger = LoggerFactory.getLogger(QMWebSocket::class.java)

        fun sendNotification(qmMessage: String,all:Boolean=true,to:String = "SYS"){

            if (all){

                webSocketSetNotification.forEach { t: String, u: QMWebSocket ->

                    u.sendMessage(qmMessage)

                }

            }else{

                if (webSocketSetNotification.containsKey(NOTIFICATION.plus(to)))
                    webSocketSetNotification[NOTIFICATION.plus(to)]!!.sendMessage(qmMessage)
                else
                    logger.error(to.plus("不在线"))

            }

        }

    }

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private var session: Session? = null
    private var number = ""

    /**
     * 连接建立成功调用的方法 */
    @OnOpen
    fun onOpen(@PathParam(value = "number") number:String, session: Session) {
        this.session = session

        this.number = number

        //验证房间号存不存在

        if (number.startsWith(NOTIFICATION)){

            webSocketSetNotification[number] = this@QMWebSocket

        }else if (roomExist(number)){

            logger.info(number.plus("连接了"))

            if (webSocketSet.keys.contains(number)){

                webSocketSet[number]!!.add(this@QMWebSocket)

            }else{

                var list =  ArrayList<QMWebSocket>()

                list.add(this@QMWebSocket)

                webSocketSet[number] = list

            }

            idWSSet[this.session!!] = this
            idNameSet[this.session!!] = number
            //总在线数加1
            addOnlineCount()

            logger.info("有新连接加入！当前在线人数为:${getOnlineCount()}")

            //只更新房间人数
            updataRoom(number,webSocketSet[number]!!.size,1)

        }else{

            logger.info("非法连接:$number")
            this.session?.close()

        }


    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    fun onClose() {

        if (this.number.startsWith(NOTIFICATION)){

            webSocketSetNotification.remove(this.number)

            return
        }

        try {

            var name =idNameSet[this.session]

            if (name != null && webSocketSet.keys.contains(name)){

                var list =webSocketSet[name]

                list?.remove(idWSSet[this.session])

                //只更新房间人数
                updataRoom(name!!,webSocketSet[name]!!.size,1)
            }

            idWSSet.remove(this.session)

            //总在线减1
            subOnlineCount()

        }catch (e:Exception){

            logger.info("onClose(:${e.message}")

        }


    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    fun onMessage(message: String, session: Session) {

        try {

            var gson = Gson()

            var qmMessage = gson.fromJson<QMMessage>(message,QMMessage::class.java)

            qmMessage.time = System.currentTimeMillis()

            //当前房间数
            qmMessage.currentCount = webSocketSet[qmMessage.to]?.size?:23

            logger.info("来自客户端的消息(name:${qmMessage.from}):$message")

            //更新最后一条信息和时间
            updataRoom(qmMessage.to,0,2,qmMessage.time,qmMessage.message)

            //更新房间绑定
            insertEnterRoomLog(qmMessage.from,qmMessage.to)

            //将message加密
            //加密信息
            qmMessage.message = Base64Utils.encodeToString( RSAUtils.encryptData(qmMessage.message.toByteArray(), RSAUtils.publucKey)!!)

            sendToPrivate(gson.toJson(qmMessage),qmMessage.to)

        }catch (e:Exception){

            logger.info("消息不合法(id:${idNameSet[session]}):$message")

        }

    }


    @OnError
    fun onError(session: Session, error: Throwable) {
        logger.error("error:${error.message}")
        error.printStackTrace()
    }


    @Throws(IOException::class)
    fun sendMessage(message: String) {

        //this.session?.basicRemote?.sendText(message)
        this.session?.asyncRemote?.sendText(message)
    }


    /**
     * 发送1-1(点对点发送)  是一对多的特例
     */
    @Throws(IOException::class)
    fun sendToPrivate(message: String,name:String) {

        //name为房间number

        webSocketSet[name]?.forEach {

            it.sendMessage(message)

        }

        //加密后速度有点慢  需异步执行
        storeMessage(message)

    }


    @Synchronized
    fun getOnlineCount(): Int {
        return onlineCount
    }

    @Synchronized
    fun addOnlineCount() {
        QMWebSocket.onlineCount++
    }

    @Synchronized
    fun subOnlineCount() {
        QMWebSocket.onlineCount--
    }

    private fun roomExist(roomNumber: String): Boolean {

        var hashMap = HashMap<String,String>()

        hashMap["roomNumber"] = roomNumber

        var response = OkHttpUtil.get(baseUrl.plus("chat/check"),hashMap)

        var gson = Gson()

        var boolean = gson.fromJson<BaseHttpResponse<Boolean>>(response,BaseHttpResponse::class.java)

        return boolean.data!!

    }


    private fun storeMessage(message: String) {

        var hashMap = HashMap<String,String>()

        hashMap["name"] = "sys"
        hashMap["message"] = message

        var response = OkHttpUtil.post(baseUrl.plus("message/insert"),hashMap)

        var gson = Gson()

        var boolean = gson.fromJson<BaseHttpResponse<Boolean>>(response,BaseHttpResponse::class.java)

        logger.error("消息插入:${boolean.data}")

    }

    //更新房间信息
    private fun updataRoom(roomNumber: String,count:Int=0,type:Int = 1,lastTime:Long=0,lastContent:String = ""){

        var hashMap = HashMap<String,String>()

        hashMap["roomNumber"] = roomNumber
        hashMap["currentCount"] = count.toString()
        hashMap["lastContent"] = lastContent
        hashMap["lastContentTime"] = lastTime.toString()
        hashMap["type"] = type.toString()

        var response = OkHttpUtil.post(baseUrl.plus("chat/updata"),hashMap)

        var gson = Gson()

        var boolean = gson.fromJson<BaseHttpResponse<Boolean>>(response,BaseHttpResponse::class.java)

        logger.error("${roomNumber}房间信息更新:${boolean.data}")

    }

    private fun insertEnterRoomLog(name: String,roomNumber: String){

        //判断是否是私人房消息
        var menList = roomNumber.split("ALWTA")

        if (menList.size == 2){

            var hashMap = HashMap<String,String>()

            hashMap["name"] = if (menList[0] == name) menList[1] else menList[0]
            hashMap["roomNumber"] = roomNumber

            OkHttpUtil.post(baseUrl.plus("enterlog/insertLog"),hashMap)

        }

        var hashMap = HashMap<String,String>()

        hashMap["name"] = name
        hashMap["roomNumber"] = roomNumber

        OkHttpUtil.post(baseUrl.plus("enterlog/insertLog"),hashMap)

    }



}