package com.lwt.qmqiu_sps1.utils

import okhttp3.*
import org.apache.tomcat.util.ExceptionUtils
import org.slf4j.LoggerFactory

class OkHttpUtil {

    companion object {

        private val logger = LoggerFactory.getLogger(OkHttpUtil::class.java)

        /**
         * get
         * @param url     请求的url
         * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
         * @return
         */

        fun get( url:String, queries:Map<String, String>?):String {
            var responseBody = ""
            var sb = StringBuffer(url)

            if (queries != null && queries.keys.isNotEmpty()) {
                var firstFlag = true

                queries.forEach { key, value ->

                    if (firstFlag) {
                        sb.append("?$key=$value")
                        firstFlag = false;
                    } else {
                        sb.append("&$key=$value")
                    }


                }

            }
            var request =  Request.Builder()
                    .url(sb.toString())
                    .build()
            var response: Response? = null
            try {
                var okHttpClient =  OkHttpClient()
                response = okHttpClient.newCall(request).execute()
                var status = response.code()
                if (response.isSuccessful) {
                    return response.body()!!.string()
                }
            } catch ( e:Exception) {
                logger.error("okhttp3:${e.message}")
            } finally {
                response?.close()
            }
            return responseBody
        }

        /**
         * post
         *
         * @param url    请求的url
         * @param params post form 提交的参数
         * @return
         */
        fun  post( url:String, params:Map<String, String> ?):String {
            var responseBody = ""
            var builder = FormBody.Builder()
            //添加参数
            if (params != null && params.keys.size > 0) {

                params.forEach { key, value ->

                    builder.add(key, value)
                }
            }
            var request = Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build()
            var response:Response? = null
            try {
                var okHttpClient =  OkHttpClient()
                response = okHttpClient.newCall(request).execute()
                var status = response.code()
                if (response.isSuccessful) {
                    return response.body()!!.string()
                }
            } catch ( e:Exception) {
                logger.error("okhttp3 post:${e.message}")
            } finally {
                response?.close()
            }
            return responseBody;
        }

        /**
         * get
         * @param url     请求的url
         * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
         * @return
         */
        fun  getForHeader( url:String,queries: Map<String, String> ):String {
            var responseBody = ""
            var sb =  StringBuffer(url)
            if (queries != null && queries.keys.size > 0) {
                var firstFlag = true
                var iterator = queries.values.iterator()
                while (iterator.hasNext()) {
                    var entry =  iterator.next() as (Map.Entry<String, String>)
                    if (firstFlag) {
                        sb.append("?" + entry.key + "=" + entry.value)
                        firstFlag = false
                    } else {
                        sb.append("&" + entry.key + "=" + entry.value)
                    }
                }
            }
            var request =  Request.Builder()
                    .addHeader("key", "value")
                    .url(sb.toString())
                    .build()
            var response :Response?= null
            try {
                var okHttpClient =  OkHttpClient()
                response = okHttpClient.newCall(request).execute()
                var status = response.code()
                if (response.isSuccessful) {
                    return response.body()!!.string()
                }
            } catch (e:Exception ) {
                logger.error("okhttp3 put :${e.message}")
            } finally {
                response?.close()
            }
            return responseBody;
        }

        /**
         * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
         * 参数一：请求Url
         * 参数二：请求的JSON
         * 参数三：请求回调
         */
        fun  postJsonParams( url:String,  jsonParams:String):String {
            var responseBody = ""
            var requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams)
            var request =  Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()
            var response:Response? = null
            try {
                var okHttpClient =  OkHttpClient()
                response = okHttpClient.newCall(request).execute()
                var status = response.code()
                if (response.isSuccessful) {
                    return response.body()!!.string()
                }
            } catch (e:Exception ) {
                logger.error("okhttp3 post :${e.message}")
            } finally {
                response?.close()
            }
            return responseBody
        }

        /**
         * Post请求发送xml数据....
         * 参数一：请求Url
         * 参数二：请求的xmlString
         * 参数三：请求回调
         */
        fun  postXmlParams( url:String,  xml:String):String {
            var responseBody = ""
            var requestBody = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xml)
            var request =  Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()
            var response:Response? = null
            try {
                var okHttpClient =  OkHttpClient()
                response = okHttpClient.newCall(request).execute();
                var status = response.code()
                if (response.isSuccessful) {
                    return response.body()!!.string()
                }
            } catch (e:Exception ) {
                logger.error("okhttp3 post:${e.message}")
            } finally {
                response?.close()
            }
            return responseBody;
        }
    }
}