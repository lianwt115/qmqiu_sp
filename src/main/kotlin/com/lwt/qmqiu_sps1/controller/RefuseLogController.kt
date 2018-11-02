package com.lwt.qmqiu_sps1.controller



import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.RefuseLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.RefuseLogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/refuse")
class RefuseLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(RefuseLogController::class.java)

    }

    enum class RefuseLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"用户不存在"),
        STATUS_ERR(202,"状态错误"),
        DB_ERR(203,"数据库错误"),

    }

    @Autowired
    private lateinit var refuseLogService: RefuseLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @PostMapping("/refuseuser")
    fun refuseLog(@RequestParam("from") from:String, @RequestParam("to") to:String,@RequestParam("refuse") refuse:Boolean): BaseHttpResponse<RefuseLog> {

        var baseR= BaseHttpResponse<RefuseLog>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",from)
        var userTo = userService.findByKey("name",to)

        if (userFrom != null && userTo != null){

            //是否有日志
            var refuseLog = refuseLogService.getRefuseLogOne(from,to)

            if (refuseLog != null){

                //有记录,校验状态
                if (refuseLog.status == refuse){


                    baseR.code = RefuseLogErr.STATUS_ERR.code
                    baseR.message = RefuseLogErr.STATUS_ERR.message

                }else{

                    var hash = HashMap<String,Any>()

                    hash["status"] = refuse
                    hash["changeTime"] = System.currentTimeMillis()

                    when (refuseLogService.updata(refuseLog._id!!,hash).modifiedCount) {

                        0L -> {

                            baseR.code = RefuseLogErr.DB_ERR.code
                            baseR.message = RefuseLogErr.DB_ERR.message

                        }

                        else -> {

                            refuseLog.status =  hash["status"] as Boolean
                            refuseLog.changeTime = hash["changeTime"] as Long

                            baseR.data = refuseLog
                        }
                    }

                }

            }else{
                //插入新的记录
                var refuseLog = RefuseLog()

                refuseLog.from = from
                refuseLog.to = to
                refuseLog.status =  refuse

                refuseLogService.insert(refuseLog)

                baseR.data = refuseLog
            }

        }else{

            baseR.code = RefuseLogErr.USER_NOTFIND.code
            baseR.message = RefuseLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @GetMapping("/refusecheck")
    fun checkRefuseLog(@RequestParam("from") from:String, @RequestParam("to") to:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",from)
        var userTo = userService.findByKey("name",to)

        if (userFrom != null && userTo != null){

            //是否有日志
            var refuseLog = refuseLogService.getRefuseLogOne(from,to)

            if (refuseLog != null){

                baseR.data = refuseLog.status

            }else{

                baseR.data = false

            }

        }else{

            baseR.code = RefuseLogErr.USER_NOTFIND.code
            baseR.message = RefuseLogErr.USER_NOTFIND.message

        }

        return baseR
    }

}