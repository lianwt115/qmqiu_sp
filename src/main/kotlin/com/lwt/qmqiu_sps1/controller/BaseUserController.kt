package com.lwt.qmqiu_sps1.controller


import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.RSATest
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.utils.RSAUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*
import java.security.KeyPairGenerator


@RestController
@RequestMapping("/user")
class BaseUserController {

    enum class BaseUserErr(var code:Int,var message:String){

        USER_NOTFIND(201,"用户不存在"),
        USER_EXIST(202,"注册失败,用户名已存在,请重新注册"),
        USER_PASSWORDERR(203,"用户密码错误"),

    }

    @Autowired
    private lateinit var userService: BaseUserService

    @GetMapping("/getAll")
    fun getAllUser(): BaseHttpResponse<List<BaseUser>> {

        var baseR= BaseHttpResponse<List<BaseUser>>()

        baseR.data=userService.getAll()

        return baseR
    }

    @PostMapping("/regist")
    fun insert(@RequestParam("name") name:String, @RequestParam("password") password:String): BaseHttpResponse<BaseUser> {


        var baseR=BaseHttpResponse<BaseUser>()

        when (userService.userExist(name) != null) {

            true -> {

                baseR.code = BaseUserErr.USER_EXIST.code
                baseR.message = BaseUserErr.USER_EXIST.message
            }

            false -> {

                var keyPair = RSAUtils.generateRSAKeyPair()

                var savePassword = Base64Utils.encodeToString( RSAUtils.encryptData(password.toByteArray(),keyPair!!.public)!!)

                var user=BaseUser(null,name,savePassword,Base64Utils.encodeToString(keyPair!!.private.encoded),Base64Utils.encodeToString(keyPair!!.public.encoded))

                userService.insert(user)

                baseR.data=user

            }
        }

        return baseR
    }

    @PostMapping("/login")
    fun login(@RequestParam("name") name:String, @RequestParam("password") password:String): BaseHttpResponse<BaseUser> {

        var baseR=BaseHttpResponse<BaseUser>()

        var userFind = userService.userExist(name)

        when (userFind == null) {

            true -> {

                baseR.code = BaseUserErr.USER_NOTFIND.code
                baseR.message = BaseUserErr.USER_NOTFIND.message
            }

            false -> {
                //存在就验证密码,将密文解密为明文对比

                var privateKey = RSAUtils.loadPrivateKey(userFind!!.privateKey!!)


                var  savePassword = String(RSAUtils.decryptData(Base64Utils.decodeFromString(userFind!!.password!!),privateKey)!!)

                if (savePassword  == password){
                    baseR.data = userFind
                }else{

                    baseR.code = BaseUserErr.USER_PASSWORDERR.code
                    baseR.message = BaseUserErr.USER_PASSWORDERR.message

                }

            }
        }

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
        var user = userService.findById(_id)

        if (user == null){

            baseR.message = BaseUserErr.USER_NOTFIND.message
        }

        baseR.data = user

        return baseR
    }


}