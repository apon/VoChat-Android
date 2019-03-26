package me.apon.vochat.service.callback

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/9.
 */
abstract class MessageListener {

    val timeOut: Long = 8 * 1000

    val sendTime: Long = System.currentTimeMillis()

    abstract fun onReceive(msg: String)

    abstract fun onTimeout()
}