package me.apon.vochat.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import me.apon.vochat.app.VOChatApp
import me.apon.vochat.db.AppRoomDatabase
import me.apon.vochat.db.LocalMessage
import me.apon.vochat.db.LocalSession
import me.apon.vochat.features.message.VoiceActivity
import me.apon.vochat.model.*
import me.apon.vochat.service.callback.MessageReceiver


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/8.
 */
class MainService : Service() {


    companion object {

        fun start(context: Context) {
            val intent = Intent(context, MainService::class.java)
            context.startService(intent)
        }
    }

    private val binder: CallServiceBinder = CallServiceBinder()

    inner class CallServiceBinder : Binder() {

        fun getService(): MainService {
            return this@MainService
        }
    }

    private val network = NetworkManager.instance
    fun getNetwork(): NetworkManager {
        return network
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.d("MainService", "-----onBind----")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
//        startForeground(1, Notification())
        network.start()
        network.addReceiver(msgReceiver)
        network.addReceiver(voiceReceiver)
        Log.d("MainService", "-----onCreate----")
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(netBroadcastReceiver, intentFilter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MainService", "-----onStartCommand----")
//        netManager.start()
        return START_STICKY

    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainService", "-----onDestroy----")
        network.release()
        unregisterReceiver(netBroadcastReceiver)
    }


    private val netBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            network.reset()
        }

    }

    ///////////////////处理收到的消息////////////////////////
    lateinit var messageCallback: () -> Unit

    private var messageTag: String? = null

    fun subscribeMsg(messageTag: String, callback: () -> Unit) {
        this.messageCallback = callback
        this.messageTag = messageTag
    }

    val msgDao = AppRoomDatabase.getDatabase(VOChatApp.context).localMessageDao()
    val sessionDao = AppRoomDatabase.getDatabase(VOChatApp.context).localSessionDao()
    var readCount = mutableMapOf<String, Int>()
    /**
     * 接收消息的入口
     */
    private val msgReceiver = object : MessageReceiver(CMD_REC_MSG, false) {

        override fun onReceive(msg: String) {
            val resp: RecMessageResp? = Json.M.moshi.adapter<RecMessageResp>(RecMessageResp::class.java).fromJson(msg)

            when (resp?.peerType) {
                PEER_TYPE_C2C -> {
                    val message = LocalMessage(
                        resp.id,
                        resp.fromId,
                        resp.toId,
                        resp.created,
                        resp.peerType,
                        resp.content
                    )
                    msgDao.insert(message)
                    if (sessionDao.getUnReadCount(resp.fromId, resp.toId)?.unreadCount ?: -1 == 0) {
                        readCount[resp.fromId] = 0
                    }
                    if (::messageCallback.isLateinit && messageTag == "${resp.toId}:${resp.fromId}") {
                        messageCallback()
                    } else {
                        var count: Int = readCount[resp.fromId] ?: 0
                        count++
                        NewMessageNotification.notify(
                            VOChatApp.context,
                            resp.fromId,
                            resp.fromName,
                            resp.content,
                            count
                        )
                        readCount[resp.fromId] = count

                    }
                    var count = readCount[resp.fromId] ?: 0

                    val session =
                        LocalSession(resp.fromId, resp.toId, resp.fromName, "", count, resp.content, resp.created)
                    sessionDao.updateSession(session)

                }
                PEER_TYPE_GROUP -> {

                }
            }

        }

    }

    private val voiceReceiver=object :MessageReceiver(CMD_REQ_VOICE,false){
        override fun onReceive(msg: String) {
            val resp = Json.M.moshi.adapter<ReqVoiceResp>(ReqVoiceResp::class.java).fromJson(msg)
            if (resp?.code == 200) {
                when(resp.reqType){
                    REQ_VOICE_TYPE_ACCEPT->{

                    }
                    REQ_VOICE_TYPE_CANCEL->{

                    }
                    REQ_VOICE_TYPE_ASK->{
                        VoiceActivity.startFromService(applicationContext,resp.fromId,resp.fromName)
                    }
                }
            }
        }
    }

}


