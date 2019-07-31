package com.lwt.qmqiu_sps1.controller



import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.ReportLogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/report")
class ReportLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(ReportLogController::class.java)

    }

    enum class ReportLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"举报失败,用户不存在"),
        TYPE_ERR(202,"举报失败,类型错误"),
        DB_ERR(203,"举报失败,数据库错误"),
        REPORT_ERR(204,"举报失败,重复举报"),

    }

    @Autowired
    private lateinit var reportLogService: ReportLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @PostMapping("/reportuser")
    fun refuseLog(@RequestParam("from") from:String, @RequestParam("to") to:String,@RequestParam("why") why:Int,@RequestParam("roomNumber") roomNumber:String,@RequestParam("messageContent") messageContent:String,@RequestParam("messageId") messageId:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",from)
        var userTo = userService.findByKey("name",to)

        if (userFrom != null && userTo != null) {

            if (why in 0..5){

                if (!reportLogService.checkReport(from,to,messageId)) {

                    var reportLog = ReportLog()

                    reportLog.from = from
                    reportLog.to = to
                    reportLog.why = why
                    reportLog.roomNumber = roomNumber
                    reportLog.messageContent = messageContent
                    reportLog.messageId = messageId



                    var hash =HashMap<String,Any>()

                    hash["reported"] = userTo.reported+1

                    when (userService.updata(userTo._id!!,hash).modifiedCount) {

                        0L -> {

                        }
                        else -> {
                            reportLogService.insert(reportLog)

                        }
                    }

                }

                baseR.data = true

            }else{

                baseR.code = ReportLogErr.TYPE_ERR.code
                baseR.message = ReportLogErr.TYPE_ERR.message

            }

        }else{

            baseR.code = ReportLogErr.USER_NOTFIND.code
            baseR.message = ReportLogErr.USER_NOTFIND.message

        }

        baseR.data = baseR.data== true

        return baseR
    }
}