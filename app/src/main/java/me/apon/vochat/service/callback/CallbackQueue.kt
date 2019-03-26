package me.apon.vochat.service.callback

import android.os.Handler
import java.util.concurrent.ConcurrentHashMap


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/9.
 */
class CallbackQueue {
    companion object {
        val INSTANCE: CallbackQueue by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CallbackQueue()
        }
    }

    @Volatile
    private var isRunning = false
    @Volatile
    private var isCheck = false

    //callback 队列
    private val callBackQueue = ConcurrentHashMap<Long, MessageListener>()
    private val receiverQueue = ConcurrentHashMap<Long, MutableList<MessageReceiver>>()
    private val timerHandler = Handler()

    fun onStart() {
        isRunning = true
        startCheck()
    }

    fun onDestory() {
        callBackQueue.clear()
        receiverQueue.clear()
        isRunning = false
    }

    private fun startCheck() {
        if (isRunning && isCheck) {
            isCheck = true
            timerHandler.postDelayed({
                checkTimeOut()
                isCheck = false
                startCheck()
            }, 5 * 1000)
        }
    }

    private fun checkTimeOut() {
        val currentTime = System.currentTimeMillis()

        for (entry in callBackQueue) {

            val msgListener = entry.value
            val seqNo = entry.key
            val timeRange = currentTime - msgListener.sendTime

            try {
                if (timeRange >= msgListener.timeOut) {
                    val listener = popCallBack(seqNo)
                    listener?.onTimeout()
                }
            } catch (e: Exception) {
            }

        }
    }

    fun pushCallBack(seqNo: Long, listener: MessageListener) {
        callBackQueue[seqNo] = listener
    }

    fun popCallBack(seqNo: Long): MessageListener? {
        synchronized(this@CallbackQueue) {
            return if (callBackQueue.containsKey(seqNo)) {
                callBackQueue.remove(seqNo)
            } else null
        }
    }

    fun addMessageReceiver(receiver: MessageReceiver) {
        val cmd = receiver.cmd
        var list: MutableList<MessageReceiver>? = receiverQueue[cmd]

        if (list.isNullOrEmpty())
            list = mutableListOf()

        list.add(receiver)
        receiverQueue[cmd] = list
    }

    fun getMessageReceiver(cmd: Long): MutableList<MessageReceiver>? {
        return receiverQueue[cmd]
    }

    fun deleteMessageReceiver(receiver: MessageReceiver) {
        val cmd = receiver.cmd
        var list: MutableList<MessageReceiver>? = receiverQueue[cmd]

        if (!list.isNullOrEmpty() && list.contains(receiver)) {
            list.remove(receiver)
        }
    }
}