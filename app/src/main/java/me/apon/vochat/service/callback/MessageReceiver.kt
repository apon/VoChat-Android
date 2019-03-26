package me.apon.vochat.service.callback


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/10.
 */
abstract class MessageReceiver(val cmd:Long,val runOnMain:Boolean = true) {
     abstract fun  onReceive(msg: String)
}