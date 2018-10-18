package com.lwt.qmqiu_sps1

import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.WebErr
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class ExceptionHandle {


    private val logger = LoggerFactory.getLogger(ExceptionHandle::class.java)

    /**
     * 系统异常处理，比如：404,500
     * @param req
     * @param resp
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception::class)
    @ResponseBody
    @Throws(Exception::class)
    fun defaultErrorHandler(req: HttpServletRequest, e: Exception): BaseHttpResponse<WebErr> {
        logger.error("QM", e)
        val r = BaseHttpResponse<WebErr>()
        r.message = e.message
        if (e is org.springframework.web.servlet.NoHandlerFoundException) {
            //接口地址不存在
            r.code = 404
        } else {
            //其他所有异常均为500,并非只有状态码为500的时候
            r.code = 500
        }
        r.data=WebErr(req.method,req.requestURI)
        return r
    }
}