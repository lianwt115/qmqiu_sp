package com.lwt.qmqiu_sps1.websocket

import com.google.gson.Gson
import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.dao.BaseUserDao
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.utils.OkHttpUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import javax.websocket.server.ServerEndpoint
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import javax.websocket.OnMessage
import javax.websocket.OnClose
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.OnError
import javax.websocket.server.PathParam


@ServerEndpoint(value = "/websocket/{name}")
@Component
class QMWebSocket {


    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    companion object {

        private val baseUrl = "http://localhost:9898/api/"

        private var onlineCount = 0

        //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
        private val webSocketSet = ConcurrentHashMap<String,QMWebSocket>()

        private val idNameSet = ConcurrentHashMap<String,Session>()

        private val logger = LoggerFactory.getLogger(QMWebSocket::class.java)

    }

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private var session: Session? = null

    /**
     * 连接建立成功调用的方法 */
    @OnOpen
    fun onOpen(@PathParam(value = "name") name:String, session: Session) {
        this.session = session

        when (userExist(name)) {

            true -> {
                //二次登陆
                if (idNameSet.keys.contains(name)) {
                    //将上一次连接关闭
                    idNameSet[name]?.close()
                }
                logger.info(name.plus("连接了"))
                webSocketSet[name] = this     //加入set中
                idNameSet[name] = session     //加入set中
                addOnlineCount()           //在线数加1
                logger.info("有新连接加入！当前在线人数为:${getOnlineCount()}")

            }

            false -> {
                logger.info("非法连接:$name")
                this.session?.close()
            }

        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    fun onClose() {

        //删除集合
        var key = getKeyFromValue()

        if (idNameSet.keys.contains(key))
            idNameSet.remove(key)
        if (webSocketSet.keys.contains(key))
            webSocketSet.remove(key)

        subOnlineCount()
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

            logger.info("来自客户端的消息(name:${qmMessage.from}):$message")

            if (qmMessage.to == "public"){

                sendToPublic(gson.toJson(qmMessage))

            }else{
                sendToPrivate(gson.toJson(qmMessage),qmMessage.to)
            }


        }catch (e:Exception){

            logger.info("消息不合法(id:${idNameSet[session.id]}):$message")

        }

    }


    @OnError
    fun onError(session: Session, error: Throwable) {
        logger.error("error:${error.message}")
        error.printStackTrace()
    }


    @Throws(IOException::class)
    fun sendMessage(message: String) {
        logger.info("发送给客户端的消息(name:${getKeyFromValue()}):$message")
        //this.session?.basicRemote?.sendText(message)
        this.session?.asyncRemote?.sendText(message)
    }


    /**
     * 发送1-1(点对点发送)
     */
    @Throws(IOException::class)
    fun sendToPrivate(message: String,name:String) {

        if (webSocketSet.containsKey(name))
            webSocketSet[name]?.sendMessage(message)

    }
    /**
     * 发送1-n(1对多发送)  同一个房间
     */
    @Throws(IOException::class)
    fun sendToPublic(message: String) {

        webSocketSet.forEach { t: String, u: QMWebSocket ->

            u.sendMessage(message)
        }

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

    fun userExist(name: String): Boolean? {

        var hashMap = HashMap<String,String>()

        hashMap["name"] = name

        var response = OkHttpUtil.get(baseUrl.plus("user/findByName"),hashMap)

        var gson = Gson()

        var boolean = gson.fromJson<BaseHttpResponse<Boolean>>(response,BaseHttpResponse::class.java)

        return boolean.data

    }

    fun getKeyFromValue():String{

        var keyLocal = ""
        idNameSet.forEach { key: String, value: Session ->

            if (value == this.session){
                keyLocal = key
            }
                return@forEach

        }

        return  keyLocal

    }

}