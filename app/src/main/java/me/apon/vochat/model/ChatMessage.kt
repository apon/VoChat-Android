package me.apon.vochat.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/17.
 */
class ChatMessage(
    @ColumnInfo(name = "id")
    var msgId: String?,
    @ColumnInfo(name = "text")
    var msgText: String,
    @ColumnInfo(name = "createdAt")
    var created: Date,
    @Embedded
    var chatUser: ChatUser?
) : IMessage {
    override fun getId(): String? {
        return msgId
    }

    override fun getCreatedAt(): Date {
        return created
    }

    override fun getUser(): IUser? {
        return chatUser
    }

    override fun getText(): String {
        return msgText
    }
}