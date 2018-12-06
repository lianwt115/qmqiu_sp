package com.lwt.qmqiu_sps1.controller


import com.google.gson.Gson
import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
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
        ROOMUSER_NOTFIND(203,"用户不在房间内,或未发言"),

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


                enterRoomLogService.insert(EnterRoomLog(null,name,roomNumber,1,System.currentTimeMillis()))

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

    //搜索房间内活跃群组用户
    @GetMapping("/getactiveuser")
    fun getActiveUser(@RequestParam("name") name:String,@RequestParam("roomNumber") roomNumber:String): BaseHttpResponse<List<BaseUser>> {

        var baseR= BaseHttpResponse<List<BaseUser>>()

        //是否存在  name和roomNumber对应的房间,若果有则查找房间相关用户,按messageCount排序,返回排名靠前的10位用户

        var exist = enterRoomLogService.checkRoomUser(name,roomNumber)

        if (exist!=null){

            var list = enterRoomLogService.getActiveUser(roomNumber,10)

            if (list != null && list.isNotEmpty()){

                var userList = ArrayList<BaseUser>()

                list.forEach {

                   var user =  userService.findByKey("name",it.name!!)

                    if (user != null)
                        userList.add(user)

                }

                baseR.data = userList

            }

        }else{

            baseR.code = EnterRoomLogErr.ROOMUSER_NOTFIND.code
            baseR.message = EnterRoomLogErr.ROOMUSER_NOTFIND.message

        }
        return baseR
    }
    //退出并删除
    @GetMapping("/exitanddelete")
    fun exitAndDelete(@RequestParam("name") name:String,@RequestParam("roomNumber") roomNumber:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //1.房间存不存在 2.创建者是不是name

        var room = imChatRoomService.findByKey("roomNumber",roomNumber)

        if (room !=null){

            //看我是否发言
            var roomLog = enterRoomLogService.checkRoomUser(name,roomNumber)

            if (roomLog!=null)
                //删除我的列表
                enterRoomLogService.delete(roomLog._id!!).deletedCount


            //如果房主也是我,则将房间状态改为false
            if (room.creatName == name ){

                var hash = HashMap<String,Any>()

                hash["status"] = false

               imChatRoomService.updata(roomNumber,hash).modifiedCount

            }

            baseR.data = true

        }else{

            baseR.code = EnterRoomLogErr.ROOM_NOTFIND.code
            baseR.message = EnterRoomLogErr.ROOM_NOTFIND.message
            baseR.data = false

        }

        return baseR
    }

}