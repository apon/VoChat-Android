package me.apon.vochat.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/17.
 */
class ChatSession(
    @ColumnInfo(name = "sessionId")
    var sessionId: String,
    var sessionPhoto: String,
    var sessionName: String,
    @Ignore
    var user: List<ChatUser>,
    @Embedded
    var newMessage: ChatMessage?,
    @ColumnInfo(name = "unreadCount")
    var unreadNum: Int,
    @Ignore
    private var id: String
) : IDialog<ChatMessage> {

    constructor() : this("", "", "", emptyList<ChatUser>(), null, 0, "")

    override fun getDialogPhoto(): String {
        return sessionPhoto
    }

    override fun getUnreadCount(): Int {
        return unreadNum
    }

    override fun setLastMessage(message: ChatMessage?) {
        newMessage = message
    }

    override fun getId(): String {
        return sessionId
    }

    override fun getUsers(): List<IUser> {
        return user
    }

    override fun getLastMessage(): ChatMessage? {
        return newMessage
    }

    override fun getDialogName(): String {
        return sessionName
    }
}