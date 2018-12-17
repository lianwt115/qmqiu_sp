package com.lwt.qmqiu_sps1.controller



import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.GoodLog
import com.lwt.qmqiu_sps1.bean.NoteLog
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.GoodLogService
import com.lwt.qmqiu_sps1.service.NoteLogService
import com.lwt.qmqiu_sps1.service.ReportLogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/note")
class NoteLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(NoteLogController::class.java)

       private val DELETE_NUM = 20

    }

    enum class NoteLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"发表失败,用户不存在"),
        DELETE_ERR(202,"帖子不存在,删除失败"),
        REPORT_ERR(203,"举报失败,重复举报"),
        REPORT_ERR1(204,"帖子举报过多已被删除"),


    }

    @Autowired
    private lateinit var noteLogService: NoteLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @Autowired
    private lateinit var reportService: ReportLogService

    @Autowired
    private lateinit var goodService: GoodLogService

    //发表帖子
    @PostMapping("/createNote")
    fun createNoteLog(@RequestParam("name") name:String, @RequestParam("noteType") noteType:Int,@RequestParam("seeType") seeType:Int,@RequestParam("topic") topic:String,@RequestParam("textContent") textContent:String,@RequestParam("imgList") imgList:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null) {

            var noteLog = NoteLog()

            noteLog.name = name
            noteLog.nameImg = userFrom.imgPath
            noteLog.imgList = imgList
            noteLog.noteType = noteType
            noteLog.seeType = seeType
            noteLog.textContent =textContent
            noteLog.topic = topic

            noteLogService.insert(noteLog)

            baseR.data = true

        }else{

            baseR.code = NoteLogErr.USER_NOTFIND.code
            baseR.message = NoteLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    //获取帖子
    @PostMapping("/getNote")
    fun getNoteLog(@RequestParam("name") name:String, @RequestParam("noteType") noteType:Int,@RequestParam("seeType") seeType:Int,@RequestParam("topic") topic:String?=null): BaseHttpResponse<List<NoteLog>> {

        var baseR= BaseHttpResponse<List<NoteLog>>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null) {

            baseR.data = noteLogService.getNote(noteType,seeType,topic)

        }else{

            baseR.code = NoteLogErr.USER_NOTFIND.code
            baseR.message = NoteLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/deleteNote")
    fun deleteNoteLog(@RequestParam("name") name:String, @RequestParam("id") id:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null) {

            var log = noteLogService.findByKey("_id",id)

            if (log!=null && !log.deleteStatus && log.name == name ){

                noteLogService.delete(id)

                baseR.data = true

            }else{

                baseR.code = NoteLogErr.DELETE_ERR.code
                baseR.message = NoteLogErr.DELETE_ERR.message
            }

        }else{

            baseR.code = NoteLogErr.USER_NOTFIND.code
            baseR.message = NoteLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/report")
    fun reportNoteLog(@RequestParam("name") name:String, @RequestParam("id") id:String,@RequestParam("why") why:Int): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null) {

            var log = noteLogService.findByKey("_id",id)

            if (log!=null && !log.deleteStatus ){

                        //检测该人举报过没有,如果已经举报过则
                        if (!reportService.checkReport(name,log.name, log._id!!)) {

                            var reportLog = ReportLog()

                            reportLog.from = name
                            reportLog.to = log.name
                            reportLog.messageId = log._id
                            reportLog.why = why


                            reportService.insert(reportLog)

                            log.reportNum++

                            var hash =HashMap<String,Any>()

                            hash["reportNum"] = log.reportNum
                            //超过次数就隐藏
                            if (log.reportNum>=DELETE_NUM){

                                hash["deleteStatus"] = true
                                hash["deleteTime"] = System.currentTimeMillis()

                            }

                            noteLogService.updata(log._id!!,hash)


                        }

                baseR.data =true

            }else{

                baseR.code = NoteLogErr.REPORT_ERR1.code
                baseR.message = NoteLogErr.REPORT_ERR1.message
            }

        }else{

            baseR.code = NoteLogErr.USER_NOTFIND.code
            baseR.message = NoteLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    //点赞
    @PostMapping("/good")
    fun reportNoteLog(@RequestParam("name") name:String, @RequestParam("id") id:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null) {

            var log = noteLogService.findByKey("_id",id)

            if (log!=null && !log.deleteStatus ){

                        if (!goodService.checkGood(name,log.name,log._id!!)){

                            var goodLog = GoodLog()

                            goodLog.from = name
                            goodLog.to = log.name
                            goodLog.type = 1
                            goodLog.whereId = log._id

                            goodService.insert(goodLog)


                            log.goodNum++

                            var hash =HashMap<String,Any>()

                            hash["goodNum"] = log.goodNum

                            noteLogService.updata(log._id!!,hash)

                        }

                    baseR.data =true

            }else{

                baseR.code = NoteLogErr.REPORT_ERR1.code
                baseR.message = NoteLogErr.REPORT_ERR1.message
            }

        }else{

            baseR.code = NoteLogErr.USER_NOTFIND.code
            baseR.message = NoteLogErr.USER_NOTFIND.message

        }

        return baseR
    }

}