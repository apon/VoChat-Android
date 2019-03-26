package me.apon.vochat.db

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/23.
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}