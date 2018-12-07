package com.lwt.qmqiu_sps1.controller


import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.UploadLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.UploadLogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestMapping
import java.io.*
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


@RestController
@RequestMapping("/upload")
class UploadLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(UploadLogController::class.java)

       private val formatter = SimpleDateFormat("yyyyMMdd")
    }

    enum class UploadLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"上传失败,用户不存在"),
        TYPE_ERR(202,"文件类型失败,类型错误"),
        DB_ERR(203,"上传失败,数据库错误"),
        FILE_ERR(204,"上传失败,文件错误"),
        FILE_NOTFIND(205,"文件不存在"),

    }

    @Autowired
    private lateinit var uploadLogService: UploadLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @PostMapping("/uploadfile")
    fun uploadFile(@RequestParam("file")  file: MultipartFile, @RequestParam("from") from:String, @RequestParam("type") type:Int, @RequestParam("where") where:String,@RequestParam("length") length:Int = 0): BaseHttpResponse<UploadLog> {

        var baseR= BaseHttpResponse<UploadLog>()

        //检测用户合法性
        var userFrom = userService.findByKey("name",from)


        if (userFrom != null) {

            if (file.isEmpty) {

                baseR.code = UploadLogErr.FILE_ERR.code
                baseR.message = UploadLogErr.FILE_ERR.message

            }else{

                if (type in 0..2){


                    // 获取文件名
                    val fileName = file.originalFilename
                    logger.info("上传的文件名为：$fileName")

                    //获取跟目录
                    var path = File(ResourceUtils.getURL("classpath:").path)
                    if(!path.exists())
                        path = File("")
                    logger.info("path:"+path.absolutePath)

                    var timeDirs ="static/${formatter.format(Date())}"

                    var child = when (type) {

                        0 -> {
                            "$timeDirs/voice/upload/"
                        }
                        1 -> {
                            "$timeDirs/images/upload/"
                        }
                        2 -> {
                            "$timeDirs/video/upload/"
                        }
                        else->{
                            "$timeDirs/file/upload/"
                        }
                    }

                    //如果上传目录为/static/images/upload/，则可以如下获取：
                    var upload =  File(path.absolutePath,child)
                    if(!upload.exists())
                        upload.mkdirs()

                    logger.info("upload url:"+upload.absolutePath)

                    val dest = File(upload,fileName)

                    // 检测是否存在目录
                    if (!dest.parentFile.exists()) {
                        dest.parentFile.mkdirs()
                    }

                    try {
                        file.transferTo(dest)

                        var uploadLog = UploadLog()
                        uploadLog.from = from
                        uploadLog.type = type
                        uploadLog.where = where
                        uploadLog.path = dest.absolutePath
                        uploadLog.name = fileName

                        uploadLog.length=  length

                        baseR.data = uploadLog

                        uploadLogService.insert(uploadLog)

                    } catch (e: Exception) {

                        baseR.code = UploadLogErr.DB_ERR.code
                        baseR.message = UploadLogErr.DB_ERR.message

                        e.printStackTrace()
                    }

                }else{

                    baseR.code = UploadLogErr.TYPE_ERR.code
                    baseR.message = UploadLogErr.TYPE_ERR.message

                }

            }

        }else{

            baseR.code = UploadLogErr.USER_NOTFIND.code
            baseR.message = UploadLogErr.USER_NOTFIND.message

        }

        return baseR
    }


    @RequestMapping("download")
    fun downFile(id: String, response: HttpServletResponse) {

        var baseR = BaseHttpResponse<Boolean>()
        var fileDown = uploadLogService.findByKey("_id", id)

        if (fileDown != null) {

            var fileName = URLEncoder.encode(fileDown.name, "utf-8")  // 设置文件名，根据业务需要替换成要下载的文件名

            if (fileName != null) {

                var file = File(fileDown.path)
                if (file.exists()) {
                    response.contentType = "application/force-download"// 设置强制下载不打开
                    response.setHeader("Content-Disposition", "attachment; filename=\"$fileName\"")// 设置文件名
                    response.setHeader("Content-Length", "${file.length()}")// 设置文件的长度
                    response.contentType = "multipart/form-data;charset=UTF-8"

                    var buffer = ByteArray(1024)
                    var fis: FileInputStream? = null
                    var bis: BufferedInputStream? = null

                    try {
                        fis =  FileInputStream(file)
                        bis =  BufferedInputStream(fis)
                        var os = response.outputStream
                        var i = bis.read(buffer)
                        while (i != -1) {
                            os.write(buffer, 0, i)
                            i = bis.read(buffer)
                        }
                        baseR.data = true
                    } catch ( e:Exception) {
                        e.printStackTrace()
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close()
                            } catch ( e:IOException) {
                                e.printStackTrace()
                            }
                        }
                        if (fis != null) {
                            try {
                                fis.close()
                            } catch ( e:IOException) {
                                e.printStackTrace()
                            }
                        }
                    }


                }else{

                    baseR.code = UploadLogErr.FILE_NOTFIND.code
                    baseR.message = UploadLogErr.FILE_NOTFIND.message
                }


            } else {

                baseR.code = UploadLogErr.FILE_NOTFIND.code
                baseR.message = UploadLogErr.FILE_NOTFIND.message


            }

        }else{

            baseR.code = UploadLogErr.FILE_NOTFIND.code
            baseR.message = UploadLogErr.FILE_NOTFIND.message
        }
        logger.info("${baseR.data}:$fileDown")
    }
}