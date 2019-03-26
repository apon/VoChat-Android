package me.apon.vochat.service.controller

import android.util.Log
import me.apon.vochat.app.VOChatApp
import me.apon.vochat.db.AppRoomDatabase
import me.apon.vochat.db.LocalMessage
import me.apon.vochat.model.*
import me.apon.vochat.service.callback.CallbackQueue
import me.apon.vochat.service.callback.MessageReceiver

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/22.
 */
object MsgController {
    /**
     * 收到服务端原始信息
     * 1. 解析消息的类型
     * 2. 根据不同的类型,转化成不同的消息
     * 3. 先保存在DB[insertOrreplace]中，session的更新，Unread的更新
     * 4上层通知
     * @param imMsgData
     */
    private val msgReceiver = object : MessageReceiver(CMD_REC_MSG, false) {
        val msgDao = AppRoomDatabase.getDatabase(VOChatApp.context).localMessageDao()

        override fun onReceive(msg: String) {
            val resp = Json.M.moshi.adapter<RecMessageResp>(RecMessageResp::class.java).fromJson(msg)

            when (resp?.peerType) {
                PEER_TYPE_C2C -> {
                    val message =
                        LocalMessage(resp.id, resp.fromId, resp.toId, resp.created, resp.peerType, resp.content)
                    msgDao.insert(message)
                }
                PEER_TYPE_GROUP -> {

                }
            }

        }

    }

    fun addToQueue(queue: CallbackQueue) {
        queue.addMessageReceiver(msgReceiver)
    }
}