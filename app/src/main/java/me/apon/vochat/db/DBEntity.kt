package me.apon.vochat.db

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/13.
 */


@Entity(tableName = "LocalMessage")
data class LocalMessage(
    @PrimaryKey var id: Long,
    val fromId: String,
    val toId: String,
    val created: Long,
    val peerType: Int,
    val content: String
)

@Entity(tableName = "LocalSession")
data class LocalSession(
    @PrimaryKey var id: String,
    val ownerId: String,
    val sessionName: String,
    val sessionPhoto: String,
    val unreadCount: Int,
    val lastMessage: String,
    val lastMessageCreated: Long
)

data class SessionUnReadCount(
    val unreadCount: Int
)
