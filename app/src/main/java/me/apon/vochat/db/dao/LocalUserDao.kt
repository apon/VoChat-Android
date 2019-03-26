package me.apon.vochat.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.apon.vochat.model.User

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/24.
 */
@Dao
interface LocalUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUsers(users: List<User>)

    @Query("select * from LocalUser")
    fun getUsers(): List<User>

    @Query("select * from LocalUser where id=:id")
    fun getUserById(id: String): User
}