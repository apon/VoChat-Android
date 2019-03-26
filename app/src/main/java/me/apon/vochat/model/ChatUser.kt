package me.apon.vochat.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.stfalcon.chatkit.commons.models.IUser

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/17.
 */
class ChatUser(
    @ColumnInfo(name = "userId")
    var userId: String,
    @Ignore
    private val name: String,
    @Ignore
    private val avatar: String,
    @Ignore
    private val online: Boolean
) : IUser {
    constructor() : this("", "", "", false)

    override fun getAvatar(): String {
        return avatar
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return userId
    }

    fun isOnline(): Boolean {
        return online
    }
}