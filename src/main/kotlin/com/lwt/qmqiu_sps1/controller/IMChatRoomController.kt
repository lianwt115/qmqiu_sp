package com.lwt.qmqiu_sps1.controller


import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.IMChatRoom
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.IMChatRoomService
import com.lwt.qmqiu_sps1.service.LoginLogService
import com.lwt.qmqiu_sps1.utils.RSAUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/chat")
class IMChatRoomController {

    companion object {

       private val logger = LoggerFactory.getLogger(IMChatRoomController::class.java)

    }

    enum class IMChatErr(var code:Int,var message:String){

        ROOM_NOTFIND(201,"房间不存在"),
        ROOM_CLOSE(202,"房间已关闭"),
        USER_NOTFIND(203,"非法用户"),
        USER_NO(204,"没有相关权限"),
        ROOM_NOTFIND_MOVE(205,"房间不存在或已偏离位置"),
        ROOM_EXITS(206,"房间名已存在,请更换重试"),

    }

    @Autowired
    private lateinit var imChatRoomService: IMChatRoomService

    @Autowired
    private lateinit var userService: BaseUserService

    @GetMapping("/getroom")
    fun getAllRoom(@RequestParam("name") name:String, @RequestParam("latitude") latitude:Double,@RequestParam("longitude") longitude:Double,@RequestParam("type") type:Int): BaseHttpResponse<List<IMChatRoom>> {

        var baseR= BaseHttpResponse<List<IMChatRoom>>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            when (type) {

                //附近
                1 -> {

                    if (latitude == 0.000000 && longitude == 0.000000)
                        baseR.data = imChatRoomService.getAll("roomType",type)
                    else
                        baseR.data =  imChatRoomService.getRoom(type,latitude,longitude)

                }

                //公共
                2 -> {

                    baseR.data = imChatRoomService.getAll("roomType",type)

                }

                //我的
                3 -> {

                }
            }
        }else{

            baseR.code = IMChatErr.USER_NOTFIND.code
            baseR.message = IMChatErr.USER_NOTFIND.message

        }

        if (baseR.data==null) {
            baseR.data = ArrayList<IMChatRoom>()
        }

        return baseR
    }

    @PostMapping("/creatroom")
    fun insert(@RequestParam("name") name:String, @RequestParam("roomname") roomname:String,@RequestParam("latitude") latitude:Double,@RequestParam("longitude") longitude:Double,@RequestParam("type") type:Int): BaseHttpResponse<IMChatRoom> {

        var baseR=BaseHttpResponse<IMChatRoom>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            //检查是否有房间名重名的
            var room = imChatRoomService.getRoomOne(roomname,0.0,0.0,false)


            if (room == null){

                when (type) {
                    //附近
                    1 -> {
                        var imChatRoom = IMChatRoom(
                                null,
                                roomname,
                                name.plus(System.currentTimeMillis()),
                                type,
                                name,
                                "",
                                latitude,
                                longitude,
                                System.currentTimeMillis(),
                                System.currentTimeMillis(),
                                true
                        )
                        baseR.data = imChatRoom

                        imChatRoomService.insert(imChatRoom)
                    }
                    //公共
                    2 -> {

                        if (name == "lwt520"){
                            var imChatRoom = IMChatRoom(
                                    null,
                                    roomname,
                                    name.plus(System.currentTimeMillis()),
                                    type,
                                    name,
                                    "",
                                    latitude,
                                    longitude,
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis(),
                                    true
                            )
                            baseR.data = imChatRoom
                            imChatRoomService.insert(imChatRoom)
                        }else{

                            baseR.code = IMChatErr.USER_NO.code
                            baseR.message = IMChatErr.USER_NO.message

                        }

                    }
                }
            }else{

                baseR.code = IMChatErr.ROOM_EXITS.code
                baseR.message = IMChatErr.ROOM_EXITS.message

            }

        }else{

            baseR.code = IMChatErr.USER_NOTFIND.code
            baseR.message = IMChatErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/enterroom")
    fun enterRoom(@RequestParam("name") name:String,@RequestParam("roomname") roomname:String, @RequestParam("latitude") latitude:Double,@RequestParam("longitude") longitude:Double,@RequestParam("type") type:Int): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            when (type) {

                //附近
                1 -> {

                    baseR.data =  imChatRoomService.getRoomOne(roomname,latitude,longitude,true) != null

                    if (!baseR.data!!){
                        baseR.code = IMChatErr.ROOM_NOTFIND_MOVE.code
                        baseR.message = IMChatErr.ROOM_NOTFIND_MOVE.message
                    }

                }

                //公共
                2 -> {

                    baseR.data = imChatRoomService.findByKey("roomname",roomname) != null

                    if (!baseR.data!!){
                        baseR.code = IMChatErr.ROOM_NOTFIND.code
                        baseR.message = IMChatErr.ROOM_NOTFIND.message
                    }
                }

                //我的
                3 -> {

                }
            }
        }else{

            baseR.code = IMChatErr.USER_NOTFIND.code
            baseR.message = IMChatErr.USER_NOTFIND.message

        }

        return baseR
    }



}