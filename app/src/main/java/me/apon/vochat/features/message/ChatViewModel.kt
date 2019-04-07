package me.apon.vochat.features.message

import com.airbnb.mvrx.*
import com.orhanobut.hawk.Hawk
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.db.AppRoomDatabase
import me.apon.vochat.db.LocalMessage
import me.apon.vochat.db.LocalSession
import me.apon.vochat.model.*
import me.apon.vochat.service.callback.MessageListener

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/20.
 */

data class ChatRoomStat(
    val messages: Async<List<ChatMessage>> = Uninitialized
) : MvRxState

class ChatViewModel(
    initStat: ChatRoomStat,
    private val roomDatabase: AppRoomDatabase,
    private var baseActivity: BaseActivity?
) : BaseMvRxViewModel<ChatRoomStat>(initStat) {

    companion object : MvRxViewModelFactory<ChatViewModel, ChatRoomStat> {

        override fun create(viewModelContext: ViewModelContext, state: ChatRoomStat): ChatViewModel {
            val db = AppRoomDatabase.getDatabase(viewModelContext.activity.applicationContext)
            val activity = viewModelContext.activity as BaseActivity
            return ChatViewModel(state, db, activity)
        }
    }

    fun sendMsg(toId: String, toName: String, content: String) {

        val loginUser = Hawk.get<LoginUser>("loginUser")
        val create = System.currentTimeMillis()
        val msgReq = SendMessageReq(loginUser.id, loginUser.name, toId, toName, create, PEER_TYPE_C2C, content)
//        saveMessage(msgReq)
        baseActivity?.chatNetService?.sendRequest(msgReq, object : MessageListener() {
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<BaseResp>(BaseResp::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    saveMessage(msgReq, loginUser.id)
                    getMsg(msgReq.fromId, msgReq.toId)
                } else {
                    setState { copy(messages = Fail(Throwable(resp?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(messages = Fail(Throwable("发送出错！"))) }
            }
        })
    }

    private fun saveMessage(req: SendMessageReq, ownerId: String) {
        val msgDao = roomDatabase.localMessageDao()
        val msg = LocalMessage(req.id, req.fromId, req.toId, req.created, req.peerType, req.content)
        msgDao.insert(msg)
        val sessionDao = roomDatabase.localSessionDao()
        val session = LocalSession(req.toId, ownerId, req.toName, "", 0, req.content, req.created)
        sessionDao.updateSession(session)
    }


    fun getMsg(fromId: String, toId: String) {

        val msgDao = roomDatabase.localMessageDao()
        val msg = msgDao.loadChatMessage(fromId, toId)
        setState { copy(messages = Success(msg)) }

    }

    fun clearUnReadCount(sessionId: String) {
        val loginUser = Hawk.get<LoginUser>("loginUser")
        val sessionDao = roomDatabase.localSessionDao()
        sessionDao.updateUnReadCount(sessionId, loginUser.id, 0)
    }

    fun receiveOn(fromId: String, toId: String) {
        val messageTag = "$fromId:$toId"
        baseActivity?.mainService?.subscribeMsg(messageTag) {
            getMsg(fromId, toId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        baseActivity = null
    }
}