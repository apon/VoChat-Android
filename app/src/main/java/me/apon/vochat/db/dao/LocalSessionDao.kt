package me.apon.vochat.db.dao

import androidx.room.*
import me.apon.vochat.db.LocalSession
import me.apon.vochat.db.SessionUnReadCount
import me.apon.vochat.model.ChatSession

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/24.
 */
@Dao
interface LocalSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateSession(session: LocalSession)

    @Query("update LocalSession set unreadCount=:unreadCount where id = :id and ownerId=:ownerId")
    fun updateUnReadCount(id: String, ownerId: String, unreadCount: Int)

    @Query("select unreadCount from LocalSession where id = :id and ownerId=:ownerId")
    fun getUnReadCount(id: String, ownerId: String): SessionUnReadCount?

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select id as sessionId ,id as userId,sessionName,sessionPhoto,unreadCount,lastMessage as text,lastMessageCreated as createdAt from LocalSession where ownerId=:ownerId")
    fun loadSession(ownerId: String): List<ChatSession>
}