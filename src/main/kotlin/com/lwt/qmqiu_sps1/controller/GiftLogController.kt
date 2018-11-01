package com.lwt.qmqiu_sps1.controller


import com.lwt.qmqiu_sps1.bean.BaseHttpResponse
import com.lwt.qmqiu_sps1.bean.BaseUser
import com.lwt.qmqiu_sps1.bean.CoinLog
import com.lwt.qmqiu_sps1.bean.GiftLog
import com.lwt.qmqiu_sps1.service.BaseUserService
import com.lwt.qmqiu_sps1.service.CoinLogService
import com.lwt.qmqiu_sps1.service.GiftLogService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/gift")
class GiftLogController {

    companion object {

       private val logger = LoggerFactory.getLogger(GiftLogController::class.java)

       private val priceList = listOf<Int>(18,38,68,88)
    }

    enum class GiftLogErr(var code:Int,var message:String){

        GIFTLOG_NOTFIND(201,"礼物非法"),
        USER_NOTFIND(202,"用户不存在"),
        USER_EXIT(203,"用户状态异常"),
        USER_NOTENOUGH(204,"用户货币不足"),
        SYS_ERR(205,"系统错误"),
        GIFTLOG_COUNTERR(206,"礼物个数非法"),
        GIFTLOG_CASHERR(207,"总价异常"),
        GIFTLOG_PRICEERR(208,"礼物价格异常"),
        GIFTLOG_DBERR(209,"数据库错误"),
        GIFTLOG_NOTENOUGHERR(210,"用户礼物不足"),

    }

    @Autowired
    private lateinit var giftLogService: GiftLogService

    @Autowired
    private lateinit var userService: BaseUserService

    @Autowired
    private lateinit var coinLogService: CoinLogService

    @PostMapping("/giftbuy")
    fun giftBuy(@RequestParam("name") name:String, @RequestParam("cashCount") cashCount:Int,@RequestParam("giftCount") giftCount:String,@RequestParam("priceCount") priceCount:String): BaseHttpResponse<BaseUser> {

        var baseR= BaseHttpResponse<BaseUser>()

        //检测用户合法性
        var user = userService.findByKey("name",name)

        //用户存在
        if (user != null){

            //用户在线
            if (user.status!!){

                try {
                    //检测数量
                    var giftCountArr= giftCount.split("*")

                    //只有四种礼物
                    if (giftCountArr.size == 4 && giftCountArr[0].toInt()>=0 && giftCountArr[1].toInt()>=0 && giftCountArr[2].toInt()>=0 && giftCountArr[3].toInt()>=0){

                        //礼物价格
                        var priceCountArr= priceCount.split("*")

                        if (priceCountArr.size == 4 && priceCountArr[0].toInt()==priceList[0] && priceCountArr[1].toInt()==priceList[1] && priceCountArr[2].toInt()==priceList[2] && priceCountArr[3].toInt()==priceList[3]){

                            //检查总价
                            var useCash = giftCountArr[0].toInt()*priceCountArr[0].toInt()+
                                          giftCountArr[1].toInt()*priceCountArr[1].toInt()+
                                          giftCountArr[2].toInt()*priceCountArr[2].toInt()+
                                          giftCountArr[3].toInt()*priceCountArr[3].toInt()


                            if (useCash == cashCount){

                                //检查所有
                                if (user.coin > useCash){

                                    //插入购买记录,并修改个人信息
                                    var giftLog = GiftLog()

                                    giftLog.type = 0

                                    giftLog.cash = useCash

                                    giftLog.giftCount = giftCount

                                    giftLog.giftPrice = priceCount

                                    giftLog.name = name

                                    giftLog.from = "sys"

                                    giftLog.to = name

                                    giftLog.happenTime = System.currentTimeMillis()

                                    var coinLog = CoinLog()

                                    coinLog.coinType = 1

                                    coinLog.cashType = 1

                                    coinLog.cash = cashCount

                                    coinLog.name = name

                                    coinLog.toType = 2

                                    coinLog.happenTime = giftLog.happenTime

                                    //修改个人信息
                                    //修改货币
                                    user.coin = user.coin - useCash
                                    //修改礼物数量
                                    var giftBefore = user.gift.split("*")

                                    var one = giftBefore[0].toInt() + giftCountArr[0].toInt()
                                    var two = giftBefore[1].toInt() + giftCountArr[1].toInt()
                                    var three = giftBefore[2].toInt() + giftCountArr[2].toInt()
                                    var four = giftBefore[3].toInt() + giftCountArr[3].toInt()

                                    user.gift = one.toString().plus("*$two*$three*$four")

                                    var hash = HashMap<String,Any>()

                                    hash["coin"] = user.coin
                                    hash["gift"] = user.gift
                                    //TODO 执行事务
                                    when (userService.updata(user._id!!,hash).modifiedCount) {

                                        0L -> {
                                            baseR.code = GiftLogErr.GIFTLOG_DBERR.code
                                            baseR.message = GiftLogErr.GIFTLOG_DBERR.message
                                        }

                                        else -> {

                                            //插入购买记录
                                            giftLogService.insert(giftLog)
                                            coinLogService.insert(coinLog)
                                            baseR.data =  user

                                        }
                                    }



                                }else{

                                    baseR.code = GiftLogErr.USER_NOTENOUGH.code
                                    baseR.message = GiftLogErr.USER_NOTENOUGH.message

                                }


                            }else{

                                baseR.code = GiftLogErr.GIFTLOG_CASHERR.code
                                baseR.message = GiftLogErr.GIFTLOG_CASHERR.message

                            }


                        }else{

                            baseR.code = GiftLogErr.GIFTLOG_PRICEERR.code
                            baseR.message = GiftLogErr.GIFTLOG_PRICEERR.message

                        }


                    }else{

                        baseR.code = GiftLogErr.GIFTLOG_COUNTERR.code
                        baseR.message = GiftLogErr.GIFTLOG_COUNTERR.message

                    }


                }catch (e:Exception){

                    baseR.code = GiftLogErr.SYS_ERR.code
                    baseR.message = GiftLogErr.SYS_ERR.message

                    return baseR
                }


            }else{

                baseR.code = GiftLogErr.USER_EXIT.code
                baseR.message = GiftLogErr.USER_EXIT.message

            }

        }else{

            baseR.code = GiftLogErr.USER_NOTFIND.code
            baseR.message = GiftLogErr.USER_NOTFIND.message

        }

        return baseR
    }

    @PostMapping("/giftsend")
    fun giftSend(@RequestParam("name") name:String, @RequestParam("to") to:String,@RequestParam("giftIndex") giftIndex:Int,@RequestParam("giftCount") giftCount:Int): BaseHttpResponse<BaseUser> {

        var baseR= BaseHttpResponse<BaseUser>()

        //检测用户合法性
        var fromUser = userService.findByKey("name",name)
        var toUser = userService.findByKey("name",to)

        //用户存在
        if (fromUser != null && toUser!=null){

            //送的人要在线
            if (fromUser.status!! && name!=to ){

                //送的人是否有足够礼物
                try {

                    if (giftIndex in 0..3){

                        if (giftCount > 0){

                            if (fromUser.gift.split("*")[giftIndex].toInt() >= giftCount){
                                //插入赠送记录,并修改个人信息
                                var giftLog = GiftLog()

                                giftLog.type = 1

                                giftLog.cash = 0

                                var giftList = "0*0*0*0".split("*") as ArrayList<String>

                                giftList[giftIndex] = giftCount.toString()

                                giftLog.giftCount = giftList[0].plus("*${giftList[1]}*${giftList[2]}*${giftList[3]}")

                                giftLog.giftPrice = priceList[giftIndex].toString()

                                giftLog.name = name

                                giftLog.from = name

                                giftLog.to = to

                                giftLog.happenTime = System.currentTimeMillis()

                                //完成赠送手续
                                var fromCountNow = fromUser.gift.split("*")[giftIndex].toInt() - giftCount
                                var toCountNow =  toUser.gift.split("*")[giftIndex].toInt() + giftCount

                                var fromCountGift= fromUser.gift.split("*") as ArrayList<String>
                                var toCountGift= toUser.gift.split("*") as ArrayList<String>

                                //更换数字
                                fromCountGift[giftIndex] = fromCountNow.toString()
                                toCountGift[giftIndex] = toCountNow.toString()

                                //更新数据
                                var fromHash = HashMap<String,Any>()

                                fromHash["gift"] = fromCountGift[0].plus("*${fromCountGift[1]}*${fromCountGift[2]}*${fromCountGift[3]}")

                                var fromModifiedCount = userService.updata(fromUser._id!!,fromHash).modifiedCount
                                //TODO 执行事务
                                var toHash = HashMap<String,Any>()

                                toHash["gift"] = toCountGift[0].plus("*${toCountGift[1]}*${toCountGift[2]}*${toCountGift[3]}")

                                var toModifiedCount = userService.updata(toUser._id!!,toHash).modifiedCount

                                if (fromModifiedCount > 0 && toModifiedCount>0){

                                    giftLogService.insert(giftLog)

                                    fromUser.gift = fromHash["gift"] as String

                                    baseR.data = fromUser

                                }else{

                                    baseR.code = GiftLogErr.GIFTLOG_DBERR.code
                                    baseR.message = GiftLogErr.GIFTLOG_DBERR.message

                                }



                            }else{

                                baseR.code = GiftLogErr.GIFTLOG_NOTENOUGHERR.code
                                baseR.message = GiftLogErr.GIFTLOG_NOTENOUGHERR.message
                            }


                        }else{

                            baseR.code = GiftLogErr.GIFTLOG_COUNTERR.code
                            baseR.message = GiftLogErr.GIFTLOG_COUNTERR.message

                        }

                    }else{

                        baseR.code = GiftLogErr.GIFTLOG_NOTFIND.code
                        baseR.message = GiftLogErr.GIFTLOG_NOTFIND.message

                    }

                }catch (e:Exception){

                    baseR.code = GiftLogErr.SYS_ERR.code
                    baseR.message = GiftLogErr.SYS_ERR.message

                    return baseR

                }


            }else{

                baseR.code = GiftLogErr.USER_EXIT.code
                baseR.message = GiftLogErr.USER_EXIT.message

            }

        }else{

            baseR.code = GiftLogErr.USER_NOTFIND.code
            baseR.message = GiftLogErr.USER_NOTFIND.message

        }

        return baseR
    }

}