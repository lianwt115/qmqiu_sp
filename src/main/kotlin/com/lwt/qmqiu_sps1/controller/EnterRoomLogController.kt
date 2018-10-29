package com.lwt.qmqiu_sps1.controller


import com.google.gson.Gson
import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.EnterRoomLog
import com.lwt.qmqiu_sps1.bean.IMChatRoom
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.EnterRoomLogService
import com.lwt.qmqiu_sps1.service.IMChatRoomService
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
@RequestMapping("/enterlog")
class EnterRoomLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(EnterRoomLogController::class.java)

    }

    enum class EnterRoomLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"用户不存在"),
        ROOM_NOTFIND(202,"房间不存在"),

    }

    @Autowired
    private lateinit var enterRoomLogService: EnterRoomLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @Autowired
    private lateinit var imChatRoomService: IMChatRoomService


    //只允许内部插入
    @PostMapping("/insertLog")
    fun insertEnterRoomLog(@RequestParam("name") name:String, @RequestParam("roomNumber") roomNumber:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        var room = imChatRoomService.findByKey("roomNumber",roomNumber)

        if (user != null){

            if (room != null){


                enterRoomLogService.insert(EnterRoomLog(null,name,roomNumber,System.currentTimeMillis()))

                baseR.data = true

            }else{

                baseR.code = EnterRoomLogErr.ROOM_NOTFIND.code
                baseR.message = EnterRoomLogErr.ROOM_NOTFIND.message

                baseR.data = false
            }


        }else{


            baseR.code = EnterRoomLogErr.USER_NOTFIND.code
            baseR.message = EnterRoomLogErr.USER_NOTFIND.message
            baseR.data = false

        }

        return baseR

    }

    //搜索
    @GetMapping("/getenterlog")
    fun getEnterRoomLog(@RequestParam("name") name:String): BaseHttpResponse<List<EnterRoomLog>> {

        var baseR= BaseHttpResponse<List<EnterRoomLog>>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            baseR.data = enterRoomLogService.getAll("name",name)

        }else{


            baseR.code = EnterRoomLogErr.USER_NOTFIND.code
            baseR.message = EnterRoomLogErr.USER_NOTFIND.message


        }

        if (baseR.data==null) {
            baseR.data = ArrayList<EnterRoomLog>()
        }


        return baseR
    }

}