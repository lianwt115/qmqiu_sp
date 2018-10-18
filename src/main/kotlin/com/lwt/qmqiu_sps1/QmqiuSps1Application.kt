package com.lwt.qmqiu_sps1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class QmqiuSps1Application

fun main(args: Array<String>) {
    runApplication<QmqiuSps1Application>(*args)
}
