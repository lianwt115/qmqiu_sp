package com.lwt.qmqiu_sps1.controller


import com.lwt.qmqiu_sps1.bean.*
import com.lwt.qmqiu_sps1.service.*
import com.lwt.qmqiu_sps1.utils.RSAUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/chat")
class IMChatRoomController {

    companion object {

       private val logger = LoggerFactory.getLogger(IMChatRoomController::class.java)
       private val baseUrl = "http://localhost:9898/api/"
    }

    enum class IMChatErr(var code:Int,var message:String){

        ROOM_NOTFIND(201,"房间不存在"),
        ROOM_CLOSE(202,"房间已关闭"),
        USER_NOTFIND(203,"非法用户"),
        USER_NO(204,"没有相关权限"),
        ROOM_NOTFIND_MOVE(205,"房间不存在或已偏离位置"),
        ROOM_EXITS(206,"房间名已存在,请更换重试"),
        ROOM_FLY(207,"定位不在地球上"),
        ROOM_CASH(208,"货币不足"),
        ROOM_REFUSE(209,"抱歉,对方阻止了你"),

    }

    @Autowired
    private lateinit var imChatRoomService: IMChatRoomService

    @Autowired
    private lateinit var userService: BaseUserService

    @Autowired
    private lateinit var enterRoomLogService: EnterRoomLogService

    @Autowired
    private lateinit var coinLogService: CoinLogService

    @Autowired
    private lateinit var refuseLogService: RefuseLogService

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

                    //先查出参与了那些聊天

                    var data = enterRoomLogService.getAll("name",name)

                    if (data.isNotEmpty()){

                        var list = ArrayList<IMChatRoom>()

                        data.forEach { log ->

                            var room = imChatRoomService.getRoomOne("roomNumber",log.roomNumber!!,0.0,0.0,false)

                            //公共的给 ,附近的校验一波
                            when (room?.roomType) {

                                1 -> {

                                    if (latitude-0.006 <room.latitude && latitude+0.006 >room.latitude &&  longitude-0.0025 <room.longitude && longitude+0.0025 >room.longitude)
                                            list.add(room)

                                }

                                2,3-> {

                                        list.add(room)
                                }
                            }


                        }

                        baseR.data = list
                    }

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

    @GetMapping("/getmycreatroom")
    fun getMyRoom(@RequestParam("name") name:String,@RequestParam("sys") sys:String): BaseHttpResponse<List<IMChatRoom>> {

        var baseR= BaseHttpResponse<List<IMChatRoom>>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            if(sys == "sys"){

                baseR.data = imChatRoomService.getAll("creatName",name)

            }

            else{

                baseR.code = IMChatErr.USER_NO.code
                baseR.message = IMChatErr.USER_NO.message

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
            var room = imChatRoomService.getRoomOne("roomName",roomname,0.0,0.0,false)


            if (room == null){

                when (type) {
                    //附近
                    1 -> {
                        //附近对经纬度做判断

                        if ((0<latitude && latitude<90) &&  (0<longitude && longitude<180)){

                            val  time = System.currentTimeMillis()

                            var imChatRoom = IMChatRoom(
                                    null,
                                    0,
                                    roomname,
                                    name.plus(time),
                                    type,
                                    name,
                                    "",
                                    latitude,
                                    longitude,
                                    time,
                                    time,
                                    true
                            )

                            //扣钱
                            if (roomCash(type,user,time)) {

                                baseR.data = imChatRoom

                                imChatRoomService.insert(imChatRoom)

                            }else{
                                baseR.code = IMChatErr.ROOM_CASH.code
                                baseR.message = IMChatErr.ROOM_CASH.message
                            }

                        }else{

                            baseR.code = IMChatErr.ROOM_FLY.code
                            baseR.message = IMChatErr.ROOM_FLY.message


                        }


                    }
                    //公共
                    2,3 -> {

                        //查看是否阻止
                        if (type==3 && chenkRefuse(roomname)){

                            baseR.code = IMChatErr.ROOM_REFUSE.code
                            baseR.message = IMChatErr.ROOM_REFUSE.message

                        }else{

                            val  time = System.currentTimeMillis()
                            var imChatRoom = IMChatRoom(
                                    null,
                                    0,
                                    roomname,
                                    if (type == 2)name.plus(time) else roomname,
                                    type,
                                    name,
                                    "",
                                    latitude,
                                    longitude,
                                    time,
                                    time,
                                    true
                            )
                            //扣钱
                            if (roomCash(type,user,time)) {

                                baseR.data = imChatRoom
                                imChatRoomService.insert(imChatRoom)

                            }else{
                                baseR.code = IMChatErr.ROOM_CASH.code
                                baseR.message = IMChatErr.ROOM_CASH.message
                            }

                        }

                    }

                }

            }else{

                if (type==3){

                    if (chenkRefuse(room.roomNumber)) {
                        baseR.code = IMChatErr.ROOM_REFUSE.code
                        baseR.message = IMChatErr.ROOM_REFUSE.message
                    }else{
                        baseR.data = room
                        baseR.message = "free"
                    }

                }else{

                    baseR.code = IMChatErr.ROOM_EXITS.code
                    baseR.message = IMChatErr.ROOM_EXITS.message
                }

            }

        }else{

            baseR.code = IMChatErr.USER_NOTFIND.code
            baseR.message = IMChatErr.USER_NOTFIND.message

        }

        return baseR
    }

    private fun roomCash(type: Int, user: BaseUser,time:Long):Boolean {

        //写入消费日志
        var coinLog = CoinLog()

        coinLog.cashType = 1

        coinLog.name = user.name

        coinLog.happenTime = time
        //私人10青木  附近50青木  公共100青木
          when (type) {
            //附近
            1 -> {

                when {
                    user.coinbase>=50 -> {

                        user.coinbase-=50
                        coinLog.coinType = 0
                        coinLog.cash = 50
                    }
                    user.coin >=5 -> {
                        user.coin-=5
                        coinLog.coinType = 1
                        coinLog.cash = 5
                    }
                    else -> return false
                }

                coinLog.toType=  0

            }
            //公共
            2 -> {

                when {
                    user.coinbase>=100 -> {

                        user.coinbase-=100
                        coinLog.coinType = 0
                        coinLog.cash = 100
                    }
                    user.coin >=10 -> {
                        user.coin-=10
                        coinLog.coinType = 1
                        coinLog.cash = 10
                    }
                    else -> return false
                }


                coinLog.toType=1
            }
            //私人
            3 -> {
                when {
                    user.coinbase>=10 -> {

                        user.coinbase-=10
                        coinLog.coinType = 0
                        coinLog.cash = 10
                    }
                    user.coin >=1 -> {
                        user.coin-=1
                        coinLog.coinType = 1
                        coinLog.cash = 1
                    }
                    else -> return false
                }

                coinLog.toType=4

            }

        }
        //插入消费记录
        coinLogService.insert(coinLog)
        //修改用户数据
        var hash = HashMap<String,Any>()

        hash["coin"] = user.coin
        hash["coinbase"] = user.coinbase

        userService.updata(user._id!!,hash)

        return true
    }


    @PostMapping("/updata")
    fun enterRoom(@RequestParam("roomNumber") roomNumber:String,@RequestParam("currentCount") currentCount:Int, @RequestParam("lastContent") lastContent:String,@RequestParam("lastContentTime") lastContentTime:Long,@RequestParam("type") type:Int=1): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        var hash = HashMap<String,Any>()

        when (type) {

            1 -> {
                hash["currentCount"] = currentCount
            }
            2 -> {
                //加密信息
                hash["lastContent"] = Base64Utils.encodeToString( RSAUtils.encryptData(lastContent.toByteArray(),RSAUtils.publucKey)!!)
                hash["lastContentTime"] = lastContentTime
            }
            else -> {
                hash["currentCount"] = currentCount
                hash["lastContent"] = Base64Utils.encodeToString( RSAUtils.encryptData(lastContent.toByteArray(),RSAUtils.publucKey)!!)
                hash["lastContentTime"] = lastContentTime
            }
        }



        when (imChatRoomService.updata(roomNumber,hash).modifiedCount) {

            0L -> {
                baseR.data = false
            }

            else -> {
                baseR.data = true
            }
        }

        return baseR
    }

    @GetMapping("/check")
    fun checkRoom(@RequestParam("roomNumber") roomNumber:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        baseR.data = imChatRoomService.findByKey("roomNumber",roomNumber) !=null


        return baseR
    }

    private fun  chenkRefuse(roomNumber: String):Boolean{

            val info=roomNumber.split("ALWTA")

            if (info.size == 2){

                //检测是否阻止
                var log = refuseLogService.getRefuseLogOne(info[1],info[0])

                if (log != null && log.status!!)
                    return true

                return false

            }else{

                return false
            }

    }

}