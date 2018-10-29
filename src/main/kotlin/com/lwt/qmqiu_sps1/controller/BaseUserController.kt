package com.lwt.qmqiu_sps1.controller


import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.LoginLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.LoginLogService
import com.lwt.qmqiu_sps1.utils.RSAUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/user")
class BaseUserController {

    companion object {

       private val logger = LoggerFactory.getLogger(BaseUserController::class.java)
       private val publicPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMMN4pDoYaBWEpa2Iy/WiAGBdIYnXOrsvboF0dwW/zXTFL2GRQzb1BTIkvrzY5qm+QzE7JgLLpFLfAOLMP6hF/OdmxWqxwLeUCw9NumY0Cs8nmtmvscFhvhfOretNTLxxX80vxBUv/TqpNnnAQludihMma8Hy/BYJvnYvLouQZwVAgMBAAECgYBOY2wkVF+3sh+yVex6Mzthb4dGytb6yr3M3r3iN5PFK9lv+WAStN3cpGb9V4c2BdidGx8CU6wZVD64pd3A1zjqR+XCaETH4VxzGmfqGzK4w254JO8eDqQ80xBW0Xkk1gQTS6Mf5ibkNbQrh3926Tbla1YcvJcVKvo3isN6q3QwQQJBAP2baB4Le41OsuV2ZSl6upJ2sLaZO8882WxSIVWeKJZuNo2zJKjD559qTM3HGSedxIW0NjmfQ4MqNfjrNj3La30CQQDE5Qrv8M+hXabrmYQ6zMe2s4EJOd4zuHwSQNgbrXOIfYI8QsOiHvs6i70FNpxFi3ESRKq4K6+hdX/YetH/6GZ5AkBzdaZQT2//pH3EBEQIP2zjs4++gkL9lblzHG06upfF7QV/O7kL8KzqIg43fVaRd716FdK+JykodTY/Tm7ScWNNAkEApQ1D3+PEigbR2Io2WHw1pqhPMQa7iCvMhhipkHoUcYSU2iM1j//cpjVh3K7szTeZL7E0U3L7paOz6ir7Q0T0MQJAVYaVgDAoSp9pqPDkN5D7TpXwocqFadmVn3m/zdeGG5CQJtLNGmBwt+NGxlx7dE6baELyNwB1rT9cU4dWLSDJDQ=="
        private var imgArray = listOf<String>(
                "qmqiuimg/ab.jpg","qmqiuimg/aj.jpg","qmqiuimg/bghqg.jpg","qmqiuimg/bmyw.jpg",
                "qmqiuimg/cjl.jpg","qmqiuimg/ck.jpg","qmqiuimg/clxzlmc.jpg","qmqiuimg/dbf.jpg",
                "qmqiuimg/dcq.jpg","qmqiuimg/deb.jpg","qmqiuimg/dfbb.jpg","qmqiuimg/dq.jpg",
                "qmqiuimg/dxhys.jpg","qmqiuimg/dy.jpg","qmqiuimg/dyq.jpg","qmqiuimg/emzzr.jpg",
                "qmqiuimg/gj.jpg","qmqiuimg/gsz.jpg","qmqiuimg/gx.jpg","qmqiuimg/hd.jpg",
                "qmqiuimg/hr.jpg","qmqiuimg/hsyls.jpg","qmqiuimg/hz.jpg","qmqiuimg/jl.jpg",
                "qmqiuimg/jlfw.jpg","qmqiuimg/jmsw.jpg","qmqiuimg/jmz.jpg","qmqiuimg/lfh.jpg",
                "qmqiuimg/lhc.jpg","qmqiuimg/lnhys.jpg","qmqiuimg/lqs.jpg","qmqiuimg/lwt.jpg",
                "qmqiuimg/lzw.jpg","qmqiuimg/mcf.jpg","qmqiuimg/mg.jpg","qmqiuimg/mlc.jpg",
                "qmqiuimg/mrb.jpg","qmqiuimg/mrf.jpg","qmqiuimg/mwq.jpg","qmqiuimg/mwq1.jpg",
                "qmqiuimg/nddzx.jpg","qmqiuimg/oyk.jpg","qmqiuimg/qf.jpg","qmqiuimg/qq.jpg",
                "qmqiuimg/qqr.jpg","qmqiuimg/rwx.jpg","qmqiuimg/ryy.jpg","qmqiuimg/se.jpg",
                "qmqiuimg/sg.jpg","qmqiuimg/shlxz.jpg","qmqiuimg/sqs.jpg","qmqiuimg/tbg.jpg",
                "qmqiuimg/tstl.jpg","qmqiuimg/wxb.jpg","qmqiuimg/wyy.jpg","qmqiuimg/wyz.jpg",
                "qmqiuimg/xdoyf.jpg","qmqiuimg/xln.jpg","qmqiuimg/xln1.jpg","qmqiuimg/xys.jpg",
                "qmqiuimg/xz.jpg","qmqiuimg/yd.jpg","qmqiuimg/yen.jpg","qmqiuimg/yg.jpg",
                "qmqiuimg/yg1.jpg","qmqiuimg/yk.jpg","qmqiuimg/yl.jpg","qmqiuimg/yls.jpg",
                "qmqiuimg/yzh.jpg","qmqiuimg/zl.jpg","qmqiuimg/zm.jpg","qmqiuimg/zs.jpg",
                "qmqiuimg/zstwcy.jpg","qmqiuimg/zzr.jpg"
                ).toMutableList()


       private fun getImgPath():String{

           var index = System.currentTimeMillis()%(imgArray.size)

           return imgArray[index.toInt()]

       }
        private fun getColor():Int{

           var index = Random().nextInt(19921205)

           return index

       }

    }

    enum class BaseUserErr(var code:Int,var message:String){

        USER_NOTFIND(201,"用户不存在"),
        USER_EXIST(202,"注册失败,用户名已存在,请重新注册"),
        USER_PASSWORDERR(203,"用户密码错误"),

    }

    @Autowired
    private lateinit var userService: BaseUserService
    @Autowired
    private lateinit var loginService: LoginLogService

    @GetMapping("/getAll")
    fun getAllUser(): BaseHttpResponse<List<BaseUser>> {

        var baseR= BaseHttpResponse<List<BaseUser>>()

        baseR.data=userService.getAll()

        return baseR
    }

    @PostMapping("/regist")
    fun insert(@RequestParam("name") name:String, @RequestParam("password") password:String): BaseHttpResponse<BaseUser> {

        var baseR=BaseHttpResponse<BaseUser>()

        when (userService.findByKey("name",name) != null) {

            true -> {

                baseR.code = BaseUserErr.USER_EXIST.code
                baseR.message = BaseUserErr.USER_EXIST.message
            }

            false -> {

                var keyPair = RSAUtils.generateRSAKeyPair()

                var savePassword = Base64Utils.encodeToString( RSAUtils.encryptData(password.toByteArray(),keyPair!!.public)!!)

                var user=BaseUser(null,name,savePassword, getImgPath(), getColor(),Base64Utils.encodeToString(keyPair!!.private.encoded),Base64Utils.encodeToString(keyPair!!.public.encoded))

                userService.insert(user)

                //将返回的private替换掉
                user.privateKey = publicPrivateKey

                baseR.data=user

            }
        }

        logger.info("path:/regist  name:$name success:${baseR.data != null}")

        return baseR
    }

    @PostMapping("/login")
    fun login(@RequestParam("name") name:String, @RequestParam("password") password:String,@RequestParam("auto") auto:Boolean,@RequestParam("loginWhere") loginWhere:String,@RequestParam("latitude") latitude:Double,@RequestParam("longitude") longitude:Double): BaseHttpResponse<BaseUser> {

        var baseR=BaseHttpResponse<BaseUser>()

        var userFind = userService.findByKey("name",name)

        when (userFind == null) {

            true -> {

                baseR.code = BaseUserErr.USER_NOTFIND.code
                baseR.message = BaseUserErr.USER_NOTFIND.message
            }

            false -> {

                    //如果是自动登录,就验证密文,手动登录就将密文解密为明文对比

                    var  savePassword = if (auto)userFind!!.password!! else String(RSAUtils.decryptData(Base64Utils.decodeFromString(userFind!!.password!!),RSAUtils.loadPrivateKey(userFind!!.privateKey!!))!!)

                    if (savePassword  == password){

                        var time = System.currentTimeMillis()
                        userFind.lastLoginTime = time


                        updataLoginTime(userFind,time,true)
                        insertLoginLog(userFind,time,loginWhere,latitude,longitude)

                        //将返回的private替换掉
                        userFind.privateKey = publicPrivateKey

                        baseR.data = userFind

                    }else{

                        baseR.code = BaseUserErr.USER_PASSWORDERR.code
                        baseR.message = BaseUserErr.USER_PASSWORDERR.message

                    }

            }
        }

        logger.info("path:/login  name:$name success:${baseR.data != null}")

        return baseR
    }

    @PostMapping("/loginout")
    fun loginOut(@RequestParam("name") name:String, @RequestParam("password") password:String,@RequestParam("auto") auto:Boolean,@RequestParam("loginWhere") loginWhere:String,@RequestParam("latitude") latitude:Double,@RequestParam("longitude") longitude:Double): BaseHttpResponse<Boolean> {

        var baseR=BaseHttpResponse<Boolean>()

        var userFind = userService.findByKey("name",name)

        when (userFind == null) {

            true -> {

                baseR.code = BaseUserErr.USER_NOTFIND.code
                baseR.message = BaseUserErr.USER_NOTFIND.message
            }

            false -> {

                    //如果是自动登录,就验证密文,手动登录就将密文解密为明文对比

                    var  savePassword = if (auto)userFind!!.password!! else String(RSAUtils.decryptData(Base64Utils.decodeFromString(userFind!!.password!!),RSAUtils.loadPrivateKey(userFind!!.privateKey!!))!!)

                    if (savePassword  == password){

                        var time = System.currentTimeMillis()

                        //更新登出时间
                        updataLoginTime(userFind,time,false)

                        baseR.data = true

                    }else{

                        baseR.code = BaseUserErr.USER_PASSWORDERR.code
                        baseR.message = BaseUserErr.USER_PASSWORDERR.message

                    }

            }
        }

        logger.info("path:/loginout  name:$name success:${baseR.data == true}")

        return baseR
    }


    @PostMapping("/delete")
    fun delete(@RequestParam("_id") _id:String): BaseHttpResponse<Boolean> {

        var baseR=BaseHttpResponse<Boolean>()

        when (userService.delete(_id).deletedCount) {
            0L -> {
                baseR.data=false
                baseR.message=BaseUserErr.USER_NOTFIND.message
            }

            else -> {
                baseR.data=true
            }
        }

        return baseR
    }


    @PostMapping("/updata")
    fun updata(@RequestParam("_id") _id:String,@RequestParam("name") name:String,@RequestParam("age") age:Int?=null,@RequestParam("male") male:Boolean?=null): BaseHttpResponse<Boolean> {

        var baseR=BaseHttpResponse<Boolean>()
        var hashMap = HashMap<String,Any>()

        hashMap["name"] = name

        if (age != null)
            hashMap["age"] = age
        if (male != null)
            hashMap["male"] = male


        when (userService.updata(_id,hashMap).modifiedCount) {
            0L -> {

                baseR.message = BaseUserErr.USER_NOTFIND.message
                baseR.data=false

            }

            else -> {
                baseR.data=true
            }
        }

        return baseR
    }


    @GetMapping("/findbyid")
    fun findById(@RequestParam("_id") _id:String): BaseHttpResponse<BaseUser> {

        var baseR=BaseHttpResponse<BaseUser>()
        var user = userService.findByKey("_id",_id)

        if (user == null){

            baseR.message = BaseUserErr.USER_NOTFIND.message
        }

        baseR.data = user

        return baseR
    }

    @GetMapping("/findByName")
    fun findByName(@RequestParam("name") name:String): BaseHttpResponse<Boolean> {

        var baseR=BaseHttpResponse<Boolean>()


        baseR.data = userService.findByKey("name",name) != null

        return baseR
    }

    @Async
    fun updataLoginTime(baseUser: BaseUser, time: Long,status:Boolean){

        //更新最近一次更新时间
        var updata =HashMap<String,Any>()


        updata[if (status)"lastLoginTime" else "lastLoginOutTime"] = time
        updata["status"] = status

        logger.info("path:/login(更新登录时间)  name:${baseUser.name} success:${userService.updata(baseUser._id!!,updata).modifiedCount > 0}")

    }

    @Async
    fun insertLoginLog(userFind: BaseUser, time: Long, loginWhere: String, latitude: Double, longitude: Double) {

        loginService.insert(LoginLog(null,userFind.name,loginWhere,latitude,longitude,time))
    }


}