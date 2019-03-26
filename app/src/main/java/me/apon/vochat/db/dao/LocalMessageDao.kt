package me.apon.vochat.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.apon.vochat.db.LocalMessage
import me.apon.vochat.model.ChatMessage

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/23.
 */
@Dao
interface LocalMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: LocalMessage)

    @Query("select id,fromId as userId,content as text,created as createdAt from LocalMessage where (fromId=:fromId and toId=:toId) or (fromId=:toId and toId=:fromId)")
    fun loadChatMessage(fromId: String, toId: String): List<ChatMessage>
}