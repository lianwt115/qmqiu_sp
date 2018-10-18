package com.lwt.qmqiu_sps1.websocket

import org.springframework.stereotype.Component
import javax.websocket.server.ServerEndpoint
import java.io.IOException
import javax.websocket.OnMessage
import javax.websocket.OnClose
import javax.websocket.OnOpen
import java.util.concurrent.CopyOnWriteArraySet
import javax.websocket.Session
import javax.websocket.OnError


@ServerEndpoint(value = "/websocket")
@Component
class QMWebSocket {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    companion object {

        private var onlineCount = 0

        //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
        private val webSocketSet = CopyOnWriteArraySet<QMWebSocket>()
    }



    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private var session: Session? = null

    /**
     * 连接建立成功调用的方法 */
    @OnOpen
    fun onOpen(session: Session) {
        this.session = session
        webSocketSet.add(this)     //加入set中
        addOnlineCount()           //在线数加1
        println("有新连接加入！当前在线人数为" + getOnlineCount())
        try {
            sendMessage("你好")
        } catch (e: IOException) {
            println("IO异常")
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    fun onClose() {
        webSocketSet.remove(this)  //从set中删除
        subOnlineCount()           //在线数减1
        println("有一连接关闭！当前在线人数为" + getOnlineCount())
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    fun onMessage(message: String, session: Session) {

        println("来自客户端的消息(id:${session.id}):$message")
        //群发消息
        for (item in webSocketSet) {
            try {
                item.sendMessage(message)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    @OnError
    fun onError(session: Session, error: Throwable) {
        println("发生错误")
        error.printStackTrace()
    }


    @Throws(IOException::class)
    fun sendMessage(message: String) {
        println("发送给客户端的消息(id:${this.session?.id}):$message")
        this.session?.getBasicRemote()?.sendText(message.plus("来自客户端"))
        //this.session.getAsyncRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     */
    @Throws(IOException::class)
    fun sendInfo(message: String) {
        for (item in webSocketSet) {
            try {
                item.sendMessage(message)
            } catch (e: IOException) {
                continue
            }

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

}