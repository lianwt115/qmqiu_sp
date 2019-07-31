package com.lwt.qmqiu_sps1.controller


import com.google.gson.Gson
import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.VideoCallLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.IMChatRoomService
import com.lwt.qmqiu_sps1.service.MessageLogService
import com.lwt.qmqiu_sps1.service.VideoCallLogService
import com.lwt.qmqiu_sps1.utils.RSAUtils
import com.lwt.qmqiu_sps1.websocket.QMMessage
import com.lwt.qmqiu_sps1.websocket.QMWebSocket
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
        Message_USERNOTFIND(203,"申请用户非法"),
        Message_STATUES(204,"房间状态异常"),
        Message_BUSY(205,"系统繁忙"),
        Message_NOTIFICATION(206,"用户不在线"),
        Message_VIDEOCALL(207,"视频通话记录不存在"),
        Message_VIDEOCALL_PARAMS(208,"视频通话时间异常"),

    }

    @Autowired
    private lateinit var messageLogService: MessageLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @Autowired
    private lateinit var imChatRoomService: IMChatRoomService

    @Autowired
    private lateinit var videoCallLogService: VideoCallLogService

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

    //视频申请消息
    @PostMapping("/sendVideoRequest")
    fun sendVideoMessage(@RequestParam("name") name:String, @RequestParam("to") to:String, @RequestParam("message") message:String): BaseHttpResponse<QMMessage> {

        var baseR= BaseHttpResponse<QMMessage>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null){

            //检测房间是否存在,状态是否正常,是否是私聊类型imChatRoomService.getRoomOne("roomName",roomname,0.0,0.0,false)
            var roomInfo = imChatRoomService.getRoomOne("roomNumber",to,0.0,0.0,false)

            if (roomInfo!=null){

                if (roomInfo.status && roomInfo.roomType == 3){

                    val nameList = to.split("ALWTA")

                    val sendName = if (name == nameList[0]) nameList[1] else nameList[0]

                    try {

                        var gson = Gson()

                        var qmMessage = gson.fromJson<QMMessage>(message,QMMessage::class.java)

                        //频道号
                        qmMessage.message = to.plus(System.currentTimeMillis()).plus("_ALWTA_videocall")

                        //加密
                        qmMessage.message = Base64Utils.encodeToString( RSAUtils.encryptData(qmMessage.message.toByteArray(), RSAUtils.publucKey)!!)
                        //插入日志
                        messageLogService.insert(qmMessage)
                        //修改房间最后一条信息
                        var hash = HashMap<String,Any>()

                        hash["lastContent"] = qmMessage.message
                        hash["lastContentTime"] = qmMessage.time

                        imChatRoomService.updata(to,hash)

                        //视频通话插入日志
                        var videoCallLog = VideoCallLog()

                        videoCallLog.callUser = name

                        videoCallLog.acceptUser = sendName

                        videoCallLog.roomName = to

                        videoCallLog.channelName = qmMessage.message

                        videoCallLogService.insert(videoCallLog)

                        //发送通知
                        if (QMWebSocket.sendNotification(gson.toJson(qmMessage),false,sendName)) {

                            qmMessage.from = sendName
                            //检测用户合法性
                            var sendUser = userService.findByKey("name",sendName)
                            if (sendUser!=null){
                                qmMessage.imgPath = sendUser.imgPath
                            }
                            baseR.data = qmMessage
                        }else{
                            baseR.code = QMMessageErr.Message_NOTIFICATION.code
                            baseR.message = QMMessageErr.Message_NOTIFICATION.message
                        }

                    }catch (e: Exception){

                        baseR.code = QMMessageErr.Message_BUSY.code
                        baseR.message = QMMessageErr.Message_BUSY.message
                        return baseR
                    }


                }else{

                    baseR.code = QMMessageErr.Message_STATUES.code
                    baseR.message = QMMessageErr.Message_STATUES.message

                }

            }else{

                baseR.code = QMMessageErr.Message_NOTFIND.code
                baseR.message = QMMessageErr.Message_NOTFIND.message

            }

        }else{

            baseR.code = QMMessageErr.Message_USERNOTFIND.code
            baseR.message = QMMessageErr.Message_USERNOTFIND.message

        }

        return baseR
    }

    //视频申请消息
    @PostMapping("/exitVideoRequest")
    fun exitVideoMessage(@RequestParam("channelName") channelName:String,@RequestParam("name") name:String, @RequestParam("time") time:Int): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var videoCallLog = videoCallLogService.findByKey("channelName",channelName)

        if (videoCallLog != null){

            if (time>=0){

                var gson = Gson()

                var  sendTo = if (name == videoCallLog.callUser) videoCallLog.acceptUser else videoCallLog.callUser

                //数量-单位-名称-动画名称
                var qmMessage = QMMessage(null,name,sendTo!!,7,0,"","",0)

                QMWebSocket.sendNotification(gson.toJson(qmMessage),false,sendTo)

                var hash = HashMap<String,Any>()

                hash["callTime"] = time

                videoCallLogService.updata(videoCallLog._id!!,hash)

                baseR.data =true

            }else{

                baseR.code = QMMessageErr.Message_VIDEOCALL_PARAMS.code
                baseR.message = QMMessageErr.Message_VIDEOCALL_PARAMS.message

            }


        }else{

            baseR.code = QMMessageErr.Message_VIDEOCALL.code
            baseR.message = QMMessageErr.Message_VIDEOCALL.message

        }

        return baseR
    }




}