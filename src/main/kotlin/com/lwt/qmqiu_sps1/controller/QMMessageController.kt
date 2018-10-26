package com.lwt.qmqiu_sps1.controller


import com.google.gson.Gson
import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.MessageLogService
import com.lwt.qmqiu_sps1.utils.RSAUtils
import com.lwt.qmqiu_sps1.websocket.QMMessage
import com.lwt.qmqiu_sps1.websocket.QMWebSocket
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*
import java.lang.Exception


@RestController
@RequestMapping("/message")
class QMMessageController {

    companion object {

       private val logger = LoggerFactory.getLogger(QMMessageController::class.java)

    }

    enum class QMMessageErr(var code:Int,var message:String){

        Message_NOTFIND(201,"房间不存在"),
        Message_UNABLE(202,"无操作权限"),

    }

    @Autowired
    private lateinit var messageLogService: MessageLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @GetMapping("/getmessage")
    fun getRoomMessage(@RequestParam("name") name:String, @RequestParam("roomNumber") roomNumber:String): BaseHttpResponse<List<QMMessage>> {

        var baseR= BaseHttpResponse<List<QMMessage>>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            baseR.data = messageLogService.getAll("to",roomNumber)

        }else{

            baseR.code = QMMessageErr.Message_NOTFIND.code
            baseR.message = QMMessageErr.Message_NOTFIND.message

        }

        if (baseR.data==null) {
            baseR.data = ArrayList<QMMessage>()
        }

        return baseR
    }

    //只允许内部插入
    @PostMapping("/insert")
    fun addRoomMessage(@RequestParam("name") name:String, @RequestParam("message") message:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()



        if (name == "sys"){

            try {

                var gson = Gson()

                var qmMessage = gson.fromJson<QMMessage>(message,QMMessage::class.java)

                //加密信息
                qmMessage.message = Base64Utils.encodeToString( RSAUtils.encryptData(qmMessage.message.toByteArray(),RSAUtils.publucKey)!!)

                messageLogService.insert(qmMessage)

                baseR.data = true

            }catch (e: Exception){

                logger.info("插入消息不合法:$message")
                baseR.data = false
                return baseR
            }

        }else{

            baseR.code = QMMessageErr.Message_UNABLE.code
            baseR.message = QMMessageErr.Message_UNABLE.message
            baseR.data = false
        }



        return baseR
    }

}