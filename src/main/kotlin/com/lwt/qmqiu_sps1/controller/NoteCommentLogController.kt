package com.lwt.qmqiu_sps1.controller



import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.NoteCommentLog
import com.lwt.qmqiu_sps1.bean.ReportLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.NoteLogService
import com.lwt.qmqiu_sps1.service.NotecommentLogService
import com.lwt.qmqiu_sps1.service.ReportLogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/noteComment")
class NoteCommentLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(NoteCommentLogController::class.java)

    }

    enum class NoteCommentLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"用户不存在"),
        COMMENT_NOTFIND(202,"评论不存在"),
        COMMENT_ERR(203,"删除失败,权限不够"),
        Note_NOTFIND(204,"帖子不存在"),

    }

    @Autowired
    private lateinit var noteCommentLogService: NotecommentLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @Autowired
    private lateinit var noteLogService: NoteLogService

    @GetMapping("/getComment")
    fun getNoteCommentLog(@RequestParam("name") name:String, @RequestParam("id") id:String): BaseHttpResponse<List<NoteCommentLog>> {

        var baseR= BaseHttpResponse<List<NoteCommentLog>>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null ) {

            baseR.data = noteCommentLogService.getAll("whereId",id)

        }else{

            baseR.code = NoteCommentLogErr.USER_NOTFIND.code
            baseR.message = NoteCommentLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/deleteComment")
    fun deleteNoteCommentLog(@RequestParam("name") name:String, @RequestParam("id") id:String): BaseHttpResponse<Boolean> {

        var baseR= BaseHttpResponse<Boolean>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null ) {

            var log = noteCommentLogService.findByKey("_id",id)

            if (log!=null ){

                if (log.from == name){

                    noteCommentLogService.delete(id)

                    //相应的帖子评论 -1
                    var note = noteLogService.findByKey("_id",log.whereId!!)

                    if (note !=null){

                        note.commentNum --

                        var hash = HashMap<String,Any>()

                        hash["commentNum"] = note.commentNum

                        noteLogService.updata(note._id!!,hash)

                    }

                    baseR.data = true

                }else{

                    baseR.code = NoteCommentLogErr.COMMENT_ERR.code
                    baseR.message = NoteCommentLogErr.COMMENT_ERR.message


                }

            }else{

                baseR.code = NoteCommentLogErr.COMMENT_NOTFIND.code
                baseR.message = NoteCommentLogErr.COMMENT_NOTFIND.message


            }


        }else{

            baseR.code = NoteCommentLogErr.USER_NOTFIND.code
            baseR.message = NoteCommentLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/createComment")
    fun creatNoteCommentLog(@RequestParam("name") name:String, @RequestParam("id") id:String,@RequestParam("commentContent") commentContent:String): BaseHttpResponse<NoteCommentLog> {

        var baseR= BaseHttpResponse<NoteCommentLog>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",name)

        if (userFrom != null ) {

            //有没有帖子id
            var log = noteLogService.findByKey("_id",id)

            if (log!=null){

                var noteCommentLog = NoteCommentLog()

                noteCommentLog.commentContent = commentContent
                noteCommentLog.from = name
                noteCommentLog.fromImg = userFrom.imgPath
                noteCommentLog.to = log.name
                //评论id
                noteCommentLog.whereId = id
                noteCommentLog.type = 1

                noteCommentLogService.insert(noteCommentLog)

                //帖子评论数+1
                log.commentNum++

                var hash =HashMap<String,Any>()

                hash["commentNum"] = log.commentNum

                noteLogService.updata(log._id!!,hash)

                baseR.data = noteCommentLog

            }else{

                baseR.code = NoteCommentLogErr.Note_NOTFIND.code
                baseR.message = NoteCommentLogErr.Note_NOTFIND.message

            }

        }else{

            baseR.code = NoteCommentLogErr.USER_NOTFIND.code
            baseR.message = NoteCommentLogErr.USER_NOTFIND.message

        }

        return baseR
    }
}