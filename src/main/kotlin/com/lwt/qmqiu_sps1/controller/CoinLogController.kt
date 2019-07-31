package com.lwt.qmqiu_sps1.controller



import com.lwt.qmqiu_sps1.bean.*
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.CoinLogService
import com.lwt.qmqiu_sps1.service.GiftLogService
import com.lwt.qmqiu_sps1.service.ReportLogService
import com.lwt.qmqiu_sps1.utils.RSAUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/coin")
class CoinLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(CoinLogController::class.java)




    }

    enum class CoinLogErr(var code:Int,var message:String){

        USER_NOTFIND(201,"举报失败,用户不存在"),
        TYPE_ERR(202,"查询失败,类型错误"),
        DB_ERR(203,"查询失败,数据库错误"),
        ADMIN_ERR(204,"系统错误"),
        CHARGE_ERR(205,"非法充值码"),
        CHARGE_USED(206,"充值码已经使用过"),

    }

    @Autowired
    private lateinit var coinLogService: CoinLogService

    @Autowired
    private lateinit var userService: BaseUserService
    @Autowired
    private lateinit var giftLogService: GiftLogService

    @GetMapping("/coinrecord")
    fun coinRecord(@RequestParam("name") name:String, @RequestParam("type") type:Int,@RequestParam("all") all:Boolean): BaseHttpResponse<List<CoinLog>> {

        var baseR= BaseHttpResponse<List<CoinLog>>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null) {

            //0 充值(有记录未充值) 1消费 2兑换

            when (type) {

                0,1,2-> {

                    var list = coinLogService.getAll("name",name)

                    if (list.isNotEmpty()){

                      list = list.filter {

                          if (all){

                              it.cashType == type

                          }else{

                              it.cashType == type && it.used
                          }

                        }
                    }

                    baseR.data = list

                }

                else -> {
                    baseR.code = CoinLogErr.TYPE_ERR.code
                    baseR.message = CoinLogErr.TYPE_ERR.message
                }
            }

        }else{

            baseR.code = CoinLogErr.USER_NOTFIND.code
            baseR.message = CoinLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/creatchargenumber")
    fun creatChargeNumber(@RequestParam("name") name:String, @RequestParam("type") type:Int,@RequestParam("publickey") publickey:String,@RequestParam("count") count:Int): BaseHttpResponse<String> {

        var baseR= BaseHttpResponse<String>()

        if (name == BaseUserController.ADMIN_USER){

            //检测用户合法性
            var user = userService.findByKey("name",name)

            if (user != null) {

                //验证publickey
                if (publickey == user.publicKey!!){

                    //1为青木球
                    if (type == 1){

                        if (count==200 || count==500 || count==1000){

                            var coinLog = CoinLog()

                            coinLog.cash = count

                            coinLog.coinType = type

                            coinLog.name = name

                            coinLog.chargeNumber = Base64Utils.encodeToString( RSAUtils.encryptData(("${System.currentTimeMillis()}$count").toByteArray(), RSAUtils.publucKey)!!)

                            coinLogService.insert(coinLog)

                            baseR.data = coinLog.chargeNumber

                        }else{
                            logger.error("$name:充值金额异常")
                            baseR.code = CoinLogErr.ADMIN_ERR.code
                            baseR.message = CoinLogErr.ADMIN_ERR.message

                        }


                    }else{
                        logger.error("$name:充值类型错误")
                        baseR.code = CoinLogErr.ADMIN_ERR.code
                        baseR.message = CoinLogErr.ADMIN_ERR.message

                    }

                }else{
                    logger.error("$name:publickey错误")
                    baseR.code = CoinLogErr.ADMIN_ERR.code
                    baseR.message = CoinLogErr.ADMIN_ERR.message

                }


            }else{
                logger.error("$name:用户未创建")
                baseR.code = CoinLogErr.USER_NOTFIND.code
                baseR.message = CoinLogErr.USER_NOTFIND.message

            }


        }else{
            logger.error("充值用户非法:$name")
            baseR.code = CoinLogErr.ADMIN_ERR.code
            baseR.message = CoinLogErr.ADMIN_ERR.message

        }

        return baseR
    }

    /**
     * mongdb  事务后期优化
     */
    @PostMapping("/charge")
    fun charge(@RequestParam("name") name:String, @RequestParam("chargenum") chargenum:String): BaseHttpResponse<BaseUser> {

        var baseR= BaseHttpResponse<BaseUser>()

            //检测用户合法性
            var user = userService.findByKey("name",name)

            if (user != null) {

                var coinLog = coinLogService.findByKey("chargeNumber",chargenum)
                //验证publickey
                if (coinLog != null){

                    //充值码是否已经使用过

                    if (!coinLog.used) {

                        //进行充值操作
                        //更新用户信息
                        user.coin += coinLog.cash

                        var hashMap = HashMap<String,Any>()

                        hashMap["coin"] = user.coin

                        when (userService.updata(user._id!!,hashMap).modifiedCount) {

                            1L -> {

                                coinLog.used =true
                                coinLog.chargeUser = name
                                coinLog.chargeTime = System.currentTimeMillis()

                                var coinMap = HashMap<String,Any>()

                                coinMap["used"] = coinLog.used
                                coinMap["chargeUser"] = coinLog.chargeUser
                                coinMap["chargeTime"] = coinLog.chargeTime

                                when (coinLogService.updata(coinLog._id!!,coinMap).modifiedCount) {

                                    1L -> {

                                        baseR.data = user

                                    }

                                    else -> {

                                        baseR.code = CoinLogErr.DB_ERR.code
                                        baseR.message = CoinLogErr.DB_ERR.message

                                    }
                                }

                            }

                             else-> {

                                 baseR.code = CoinLogErr.DB_ERR.code
                                 baseR.message = CoinLogErr.DB_ERR.message

                            }
                        }

                    }else{

                        baseR.code = CoinLogErr.CHARGE_USED.code
                        baseR.message = CoinLogErr.CHARGE_USED.message

                    }

                }else{

                    baseR.code = CoinLogErr.CHARGE_ERR.code
                    baseR.message = CoinLogErr.CHARGE_ERR.message

                }


            }else{

                baseR.code = CoinLogErr.USER_NOTFIND.code
                baseR.message = CoinLogErr.USER_NOTFIND.message

            }

        return baseR
    }

    //礼物兑换成青木球   礼物价值以70%的价值兑换成青木球  青木球以90%价值转换为充值   充值部分不予提现,兑换部分可提现
    /**
     * mongdb  事务后期优化
     */
    @PostMapping("/exchange")
    fun exchange(@RequestParam("name") name:String, @RequestParam("giftIndex") giftIndex:String): BaseHttpResponse<BaseUser> {

        var baseR= BaseHttpResponse<BaseUser>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        if (user != null) {

            //是否在线
            if (user.status){

                var giftListEx = giftIndex.split("*")
                var giftListLocal = user.gift.split("*")

                if (giftListEx.size == 4){
                    //比较多少
                    if (giftListLocal[0].toInt()>=giftListEx[0].toInt()

                        && giftListLocal[1].toInt()>=giftListEx[1].toInt()
                        && giftListLocal[2].toInt()>=giftListEx[2].toInt()
                        && giftListLocal[3].toInt()>=giftListEx[3].toInt()


                    ){

                        //计算金额
                        var giftPrice = GiftLogController.priceList

                        var coinEx = (  giftListEx[0].toInt()*giftPrice[0] + giftListEx[1].toInt()*giftPrice[1] +

                        giftListEx[2].toInt()*giftPrice[2] + giftListEx[3].toInt()*giftPrice[3])*0.7


                        user.coin += coinEx.toInt()
                        user.coinExchange += coinEx.toInt()

                        user.gift = "${(giftListLocal[0].toInt()-giftListEx[0].toInt())}*${(giftListLocal[1].toInt()-giftListEx[1].toInt())}*${(giftListLocal[2].toInt()-giftListEx[2].toInt())}*${(giftListLocal[3].toInt()-giftListEx[3].toInt())}"


                        var hash = HashMap<String,Any>()

                        hash["coin"] = user.coin
                        hash["coinExchange"] = user.coinExchange
                        hash["gift"] = user.gift

                        userService.updata(user._id!!,hash)

                        //兑换记录
                        var coinLog = CoinLog()

                        coinLog.cash = coinEx.toInt()

                        coinLog.coinType = 1
                        //兑换
                        coinLog.cashType = 2

                        coinLog.name = name

                        coinLogService.insert(coinLog)

                        //礼物记录
                        //插入购买记录,并修改个人信息
                        var giftLog = GiftLog()

                        giftLog.type = 2

                        giftLog.cash = coinEx.toInt()

                        giftLog.giftCount = "${(giftListEx[0].toInt())}*${(giftListEx[1].toInt())}*${(giftListEx[2].toInt())}*${(giftListEx[3].toInt())}"

                        giftLog.name = name

                        giftLog.from = name

                        giftLog.to = "sys"

                        giftLogService.insert(giftLog)

                        baseR.data = user

                    }else{

                        //礼物个数校验
                        baseR.code = CoinLogErr.ADMIN_ERR.code
                        baseR.message = CoinLogErr.ADMIN_ERR.message

                    }

                }else{

                    //参数格式校验
                    baseR.code = CoinLogErr.ADMIN_ERR.code
                    baseR.message = CoinLogErr.ADMIN_ERR.message
                }


            }else{

                //校验是否登录
                baseR.code = CoinLogErr.ADMIN_ERR.code
                baseR.message = CoinLogErr.ADMIN_ERR.message
            }

        }else{

            baseR.code = CoinLogErr.USER_NOTFIND.code
            baseR.message = CoinLogErr.USER_NOTFIND.message

        }

        return baseR
    }




}