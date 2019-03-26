package me.apon.vochat.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.apon.vochat.db.dao.LocalMessageDao
import me.apon.vochat.db.dao.LocalSessionDao
import me.apon.vochat.db.dao.LocalUserDao
import me.apon.vochat.model.User

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/13.
 */
@Database(entities = [LocalMessage::class, LocalSession::class, User::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            return instance ?: synchronized(this) {
                val room = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "vochat_database"
                ).allowMainThreadQueries()
//                    .addMigrations(Migration1T2)
                    .build()

                instance = room
                room
            }
        }

//        private val Migration1T2 = object : Migration(1,2){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE LocalSession ADD COLUMN ownerId TEXT")
//            }
//
//        }
    }


    abstract fun localMessageDao(): LocalMessageDao
    abstract fun localSessionDao(): LocalSessionDao
    abstract fun localUserDao(): LocalUserDao
}
